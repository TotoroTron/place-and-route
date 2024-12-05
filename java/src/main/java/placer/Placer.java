package placer;

import java.util.stream.Collectors;

import org.python.antlr.PythonParser.else_clause_return;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;

import com.xilinx.rapidwright.design.Design;
import com.xilinx.rapidwright.design.SiteInst;
import com.xilinx.rapidwright.design.Net;
import com.xilinx.rapidwright.design.Cell;
import com.xilinx.rapidwright.design.SitePinInst;

import com.xilinx.rapidwright.edif.EDIFNetlist;
import com.xilinx.rapidwright.edif.EDIFHierNet;
import com.xilinx.rapidwright.edif.EDIFNet;
import com.xilinx.rapidwright.edif.EDIFCellInst;
import com.xilinx.rapidwright.edif.EDIFHierCellInst;
import com.xilinx.rapidwright.edif.EDIFPortInst;
import com.xilinx.rapidwright.edif.EDIFHierPortInst;

import com.xilinx.rapidwright.device.Device;
import com.xilinx.rapidwright.device.Site;
import com.xilinx.rapidwright.device.SiteTypeEnum;
import com.xilinx.rapidwright.device.BEL;
import com.xilinx.rapidwright.device.BELPin;

public abstract class Placer {
    protected String placerName;
    FileWriter writer;

    protected Device device;
    protected Design design;
    // protected EDIFNetlist netlist;

    protected String rootDir = "/home/bcheng/workspace/dev/place-and-route/";
    protected String synthesizedDcp = rootDir + "/outputs/synthesized.dcp";
    protected String placedDcp = rootDir + "/outputs/placed.dcp";

    public Placer() throws IOException {
        design = Design.readCheckpoint(synthesizedDcp);
        device = Device.getDevice("xc7z020clg400-1");
    }

    public void run() throws IOException {
        // design.flattenDesign();
        printDesignNets(design);
        printEDIFHierCellInsts();
        placeDesign();
        manualIntraRouteSites();
        printDesignNets(design);
        writer.close();
        design.writeCheckpoint(placedDcp);
    }

    protected abstract SiteTypeEnum selectSiteType(
            Map<SiteTypeEnum, Set<String>> compatiblePlacements) throws IOException;

    protected abstract String[] selectSiteAndBEL(
            Map<String, List<String>> availablePlacements) throws IOException;

    protected abstract void removeOccupiedPlacements(
            Map<String, List<String>> occupiedPlacements,
            Map<String, List<String>> availablePlacements) throws IOException;

    public void placeDesign() throws IOException {
        writer.write("\nPlacing Cells...");

        List<Cell> cells = spawnCells(); // returns placeable cells (no buffer or port cells)
        Map<String, List<String>> occupiedPlacements = new HashMap<>();

        for (Cell cell : cells) {
            placeCell(cell, occupiedPlacements);
            writer.write("\n\tPlaced cell: " + cell.getName() + " at " + cell.getSiteInst().getName());
        } // end for(Cell)

        printOccupiedSites(occupiedPlacements);

    } // end placDesign()

    protected List<Cell> spawnCells() throws IOException {
        writer.write("\n\nSpawning cells from netlist...");
        EDIFNetlist netlist = design.getNetlist();

        List<Cell> cells = new ArrayList<>(); // spawn placeable cells from EDIFNetlist
        for (EDIFHierCellInst ehci : netlist.getAllLeafHierCellInstances()) {

            // SKIP UNPLACEABLE CELLS
            if (isBufferCell(design, ehci)) {
                continue; // buffer cells already placed by constraints
            }

            Cell cell = design.createCell(ehci.getFullHierarchicalInstName(), ehci.getInst());
            cell.setEDIFHierCellInst(ehci);
            Map<SiteTypeEnum, Set<String>> compatiblePlacements = cell.getCompatiblePlacements(device);
            if (compatiblePlacements.isEmpty()) {
                writer.write("\n\tWARNING: Cell: " + cell.getName() + " of type: " + cell.getType()
                        + " has no compatible placements! SKIPPING.");
                cells.remove(cell);
                continue;
            }
            removeBufferTypes(compatiblePlacements.keySet());

            // ABSTRACT
            SiteTypeEnum selectedSiteType = selectSiteType(compatiblePlacements);
            // END ABSTRACT

            if (selectedSiteType == null) {
                writer.write("\n\tWARNING: SiteTypeEnum: " + selectedSiteType +
                        " has no compatible sites!");
                cells.remove(cell);
                continue;
            }
            String s1 = String.format(
                    "\n\tSpawned cell: %-40s cellType: %-10s",
                    cell.getName(), cell.getType());
            writer.write(s1);
            cells.add(cell);
        }
        return cells;
    }

    protected void placeCell(
            Cell cell,
            Map<String, List<String>> occupiedPlacements) throws IOException {

        Map<SiteTypeEnum, Set<String>> compatiblePlacements = cell.getCompatiblePlacements(device);
        removeBufferTypes(compatiblePlacements.keySet());

        // ABSTRACT
        SiteTypeEnum selectedSiteType = selectSiteType(compatiblePlacements);

        List<String> siteNames = Arrays.stream(device.getAllCompatibleSites(selectedSiteType))
                .map(Site::getName) // return as string names only, not the site itself
                .collect(Collectors.toList()); // collect as list
        List<String> belNames = compatiblePlacements.get(selectedSiteType).stream()
                .filter(name -> !name.contains("5FF")) // placing regs on 5FF BELs will cause routing problems
                .collect(Collectors.toList()); // UG474, CH2 Storage Elements for more information
        Map<String, List<String>> availablePlacements = new HashMap<>();
        for (String siteName : siteNames) {
            availablePlacements.put(siteName, new ArrayList<>(belNames));
        }

        // ABSTRACT
        removeOccupiedPlacements(availablePlacements, occupiedPlacements);

        // ABSTRACT
        String[] selectedPlacement = selectSiteAndBEL(availablePlacements);

        String selectedSiteName = selectedPlacement[0];
        String selectedBELName = selectedPlacement[1];

        // System.out.println("Selected Site: " + selectedSiteName + ", Selected BEL: "
        // + selectedBELName);
        Site selectedSite = device.getSite(selectedSiteName);
        BEL selectedBEL = selectedSite.getBEL(selectedBELName);
        if (design.placeCell(cell, selectedSite, selectedBEL)) {
            addToMap(occupiedPlacements, selectedPlacement[0], selectedPlacement[1]);
        } else {
            writer.write("\n\tWARNING: Placement Failed! Cell: " + cell.getName() + ", Type: " + cell.getType());
        }

    } // end placeCell()

    public void printOccupiedSites(Map<String, List<String>> occupiedPlacements) throws IOException {
        writer.write("\n\nPrinting sites in occupiedPlacements... (" + occupiedPlacements.keySet().size() + ")");
        for (String siteName : occupiedPlacements.keySet()) {
            SiteInst si = design.getSiteInst(siteName);
            writer.write("\n\t" + siteName + ", " + si.getSiteTypeEnum());

            Map<String, Cell> belCellMap = si.getCellMap();
            for (Map.Entry<String, Cell> entry : belCellMap.entrySet()) {
                writer.write("\n\t\tBEL: " + entry.getKey() + ", Cell: " + entry.getValue());
            }

            Set<Net> nets = si.getConnectedNets();
            for (Net net : nets) {
                writer.write("\n\t\tNet: " + net.getName());
            }
        }
    }

    public void printEDIFHierCellInsts() throws IOException {
        List<EDIFHierCellInst> ehcis = design.getNetlist().getAllLeafHierCellInstances();
        writer.write("\n\nPrinting all EDIFHierCellInsts... (" + ehcis.size() + ")");
        for (EDIFHierCellInst ehci : ehcis) {
            writer.write("\n\t" + ehci.getFullHierarchicalInstName() + ", " + ehci.getCellName());
            List<EDIFHierPortInst> ehpis = ehci.getHierPortInsts();
            writer.write("\n\t\tEDIFHierPortInsts... (" + ehpis.size() + ")");
            for (EDIFHierPortInst ehpi : ehpis) {
                EDIFNet net = ehpi.getNet();
                EDIFHierNet hnet = ehpi.getInternalNet();
                if (hnet != null) {
                    writer.write("\n\t\t\tPort: " + ehpi.getFullHierarchicalInstName() + ", Net: "
                            + net.getName());
                } else {
                    writer.write("\n\t\t\tPort: " + ehpi.getFullHierarchicalInstName() + ", Net: "
                            + net.getName());
                }
            }
        }
        writer.write("\n\n");
    }

    public void printEDIFCellInstList(List<EDIFCellInst> ecis) throws IOException {
        if (ecis.size() > 0) {
            String cellType = ecis.get(0).getCellType().getName();
            writer.write("\n\tPrinting all EDIFCellInsts of type " + cellType + "... (" + ecis.size() + ")");
        }
        for (EDIFCellInst eci : ecis) {
            writer.write("\n\t\t" + eci.getCellType() + ": " + eci.getName());
            Collection<EDIFPortInst> epis = eci.getPortInsts();
            for (EDIFPortInst epi : epis) {
                writer.write("\n\t\t\t" + epi.getFullName());
            }
        }
    }

    protected boolean isBufferCell(Design design, EDIFHierCellInst ehci) {
        // Filter out IBUF/OBUF cells. They are already placed by constraints.
        Set<String> buffCells = new HashSet<>(Arrays.asList("IBUF", "OBUF"));
        if (buffCells.contains(ehci.getCellName()))
            return true;
        else
            return false;
    }

    protected void removeBufferTypes(Set<SiteTypeEnum> types) {
        List<SiteTypeEnum> buffSiteTypes = new ArrayList<>();
        Collections.addAll(buffSiteTypes,
                // FF Cells are reported to be "compatible" with these buffer sites
                SiteTypeEnum.ILOGICE2,
                SiteTypeEnum.ILOGICE3,
                SiteTypeEnum.OLOGICE2,
                SiteTypeEnum.OLOGICE3,
                SiteTypeEnum.IOB18,
                SiteTypeEnum.OPAD);
        types.removeAll(buffSiteTypes);
    }

    protected void addToMap(Map<String, List<String>> map, String key, String value) {
        map.computeIfAbsent(key, k -> new ArrayList<>()).add(value);
    }

    protected Design manualIntraRouteSites() throws IOException {
        writer.write("\n\nBeginning Intra-Routing...");

        for (SiteInst si : design.getSiteInsts()) {
            // route the site normally
            if (si == null) {
                writer.write("\n\tSiteInst: NULL!");
                continue;
            }
            si.routeSite();

            writer.write("\n\tROUTED siteName: " + si.getName());
            for (Cell cell : si.getCells()) {
                if (cell.getBEL() != null) {
                    String s1 = String.format(
                            "\n\t\tcellName: %-40s cellType: %-10s BELName: %-10s BELType: %-10s",
                            cell.getName(), cell.getType(), cell.getBELName(), cell.getBEL().getBELType());
                    writer.write(s1);
                } else {
                    writer.write("\n\t\tNull!");
                }
            }

            // does this site use a CARRY cell?
            // if so, we might need to route carry-in nets manually.
            Cell carryCell = si.getCells().stream()
                    .filter(cell -> cell.getBEL() != null)
                    .filter(cell -> cell.getBEL().isCarry())
                    .findFirst()
                    .orElse(null);
            if (carryCell != null) {
                writer.write("\n\t\tFound CARRY cell.");
                // if this CARRY4 is the first in a carry chain...
                Net cinNet = si.getNetFromSiteWire("CIN");
                if (cinNet.isGNDNet()) {
                    // manually remove CIN pin from the GND Net...
                    // otherwise, routing will complain that CIN is unreachable
                    cinNet.removePin(si.getSitePinInst("CIN"));
                    BELPin cinPin = si.getBELPin("CARRY4", "CIN");
                    si.unrouteIntraSiteNet(cinPin.getSourcePin(), cinPin);
                }
            }

            Cell ffCell = si.getCells().stream()
                    .filter(cell -> cell.getBEL() != null)
                    .filter(cell -> cell.getBEL().getBELType().contains("REG_INIT"))
                    .findFirst()
                    .orElse(null);
            if (ffCell != null) {
                writer.write("\n\t\tFound FF cell.");
                Net srNet = si.getNetFromSiteWire("SRUSEDMUX_OUT");
                if (!srNet.isGNDNet()) {
                    // srNet.addPin(si.getSitePinInst("SR"));
                    BELPin srPin = ffCell.getBEL().getPin("SR");
                    si.unrouteIntraSiteNet(srPin.getSourcePin(), srPin);
                    si.routeIntraSiteNet(srNet, si.getBELPin("SRUSEDMUX", "IN"), srPin);
                }
            }
        } // end for (SiteInst)
        return design;
    }

    public void placeDesignFromEDIFNetlist() throws IOException {
        writer.write("\n\nPlacing Cells...");

        Map<String, List<String>> occupiedPlacements = new HashMap<>();
        for (EDIFHierCellInst ehci : design.getNetlist().getAllLeafHierCellInstances()) {

            // SKIP UNPLACEABLE CELLS
            if (isBufferCell(design, ehci))
                continue; // buffer cells already placed by constraints
            Cell cell = design.createCell(ehci.getFullHierarchicalInstName(), ehci.getInst());
            Map<SiteTypeEnum, Set<String>> compatiblePlacements = cell.getCompatiblePlacements(device);
            if (compatiblePlacements.isEmpty()) {
                writer.write("\n\tWARNING: Cell: " + cell.getName() + " of type: " + cell.getType()
                        + " has no compatible placements!");
                continue;
            }
            removeBufferTypes(compatiblePlacements.keySet());

            // ABSTRACT
            SiteTypeEnum selectedSiteType = selectSiteType(compatiblePlacements);
            // END ABSTRACT

            if (selectedSiteType == null) {
                writer.write("\n\tWARNING: SiteTypeEnum: " + selectedSiteType +
                        " has no compatible sites!");
                continue;
            }

            // BEGIN PLACEMENT DECISION
            List<String> siteNames = Arrays.stream(device.getAllCompatibleSites(selectedSiteType))
                    .map(Site::getName) // return as string names only, not the site itself
                    .collect(Collectors.toList()); // collect as list
            List<String> belNames = compatiblePlacements.get(selectedSiteType).stream()
                    .filter(name -> !name.contains("5FF")) // placing regs on 5FF BELs will cause routing problems
                    .collect(Collectors.toList());
            // UG474, CH2 Storage Elements for more information
            Map<String, List<String>> availablePlacements = new HashMap<>();
            for (String siteName : siteNames) {
                availablePlacements.put(siteName, new ArrayList<>(belNames));
            }

            // ABSTRACT
            removeOccupiedPlacements(availablePlacements, occupiedPlacements);
            // END ABSTRACT

            if (availablePlacements.isEmpty()) {
                String s1 = String.format("\nWARNING: Cell: $-40s has no available placements!",
                        cell.getName());
                System.out.println(s1);
                writer.write(s1);
                continue;
            }

            // ABSTRACT
            String[] selectedPlacement = selectSiteAndBEL(availablePlacements);
            // END ABSTRACT

            String selectedSiteName = selectedPlacement[0];
            String selectedBELName = selectedPlacement[1];

            System.out.println("Selected Site + BEL: " + selectedSiteName + ", " + selectedBELName);
            Site selectedSite = device.getSite(selectedSiteName);
            BEL selectedBEL = selectedSite.getBEL(selectedBELName);
            if (design.placeCell(cell, selectedSite, selectedBEL)) {
                writer.write("\n\tPlacement success! Cell: " + cell.getName() + ", Site: " + selectedSiteName
                        + ", BEL: " + selectedBELName);
                addToMap(occupiedPlacements, selectedPlacement[0], selectedPlacement[1]);
            } else {
                writer.write("\n\tWARNING: Placement Failed!");
            }
        } // end for (EDIFHierCellInst)

    } // end placeDesignFromEDIFNetlist()

    protected void printMap(Map<String, List<String>> map) {
        for (Map.Entry<String, List<String>> entry : map.entrySet()) {
            String siteName = entry.getKey();
            List<String> occupiedBELs = entry.getValue();
            System.out.println("\tSite: " + siteName);
            for (String bel : occupiedBELs) {
                System.out.println("\t\tBEL: " + bel);
            }
        }
        System.out.println();
    } // end printMap()

    protected void printAllCompatiblePlacements(FileWriter writer, Cell cell)
            throws IOException {
        writer.write("\n\nPrinting Compatible placements: ");
        Map<SiteTypeEnum, Set<String>> compatibleBELs = cell.getCompatiblePlacements(device);

        for (Map.Entry<SiteTypeEnum, Set<String>> entry : compatibleBELs.entrySet()) {
            SiteTypeEnum siteType = entry.getKey();
            Set<String> belNames = entry.getValue();

            writer.write("\n\tSiteTypeEnum: " + siteType.name());
            Site[] sites = device.getAllSitesOfType(siteType);

            for (String bel : belNames)
                writer.write("\n\t\tBEL: " + bel);
            if (sites.length == 0) {
                writer.write("\n\t\tSites: None!");
                continue;
            }
            if (sites.length > 10) {
                writer.write("\n\t\t" + sites.length + " compatible sites.");
                continue;
            }
            for (Site site : sites)
                writer.write("\n\t\tSite: " + site.getName());
        }
        return;
    } // end printAllCompatiblePlacements()

    public void printCells(Collection<Cell> cells) throws IOException {
        for (Cell cell : cells) {
            writer.write("\n\tCell name: " + cell.getName());
            EDIFHierCellInst ehci = cell.getEDIFHierCellInst();
            if (ehci == null) {
                writer.write("\n\t\tEHCI Null!");
                continue;
            }
            writer.write("\n\t\tEDIFHierCellInst name: " + ehci.getCellName());
            writer.write("\n\t\tPrinting EDIFHierPortInsts...");

            Collection<EDIFHierPortInst> ehpis = ehci.getHierPortInsts();
            for (EDIFHierPortInst ehpi : ehpis) {
                writer.write("\n\t\t\tEDIFHierPortInst name: " +
                        ehpi.getFullHierarchicalInstName() + "/" + ehpi.getPortInst().getName());
            }
        }
    } // end printCells

    public void printCellNets(Cell cell) throws IOException {
        writer.write("\n\tcellName: " + cell.getName());
        EDIFCellInst eci = cell.getEDIFCellInst();
        writer.write("\n\tedifCellInst: " + eci.getName());

        writer.write("\n\tPrinting EDIFPortInsts on this cell...");
        Collection<EDIFPortInst> cellepis = eci.getPortInsts();
        for (EDIFPortInst cellepi : cellepis) {
            writer.write("\n\t\tEDIFPortInst: " + cellepi.getName());
            EDIFNet enet = cellepi.getNet();
            writer.write("\n\t\tEDIFNet: " + enet.getName());

            writer.write("\n\t\tPrinting EDIFPortInsts on this net...");
            Collection<EDIFPortInst> netepis = enet.getPortInsts();
            for (EDIFPortInst netepi : netepis) {
                EDIFCellInst neteci = netepi.getCellInst();
                writer.write("\n\t\t\tEDIFPortInst: " + netepi.getName() + " on EDIFCellInst: " + neteci.getName());
            }
        }
    }

    public void printDesignCells(Design design) throws IOException {
        writer.write("\n\nPrinting All Cells...");
        Collection<Cell> cells = design.getCells();
        for (Cell cell : cells) {
            String s1 = String.format(
                    "\n\tCell: %-40s isPlaced = %-10s",
                    cell.getName(), cell.isPlaced());
            writer.write(s1);
            if (cell.getSite() != null) {
                String s2 = "\n\t\tSite: " + cell.getSite().getName();
                String s3 = "\n\t\tSiteInst: " + cell.getSiteInst().getName() + " \tPlaced = "
                        + cell.getSiteInst().isPlaced();
                String s4 = "\n\t\tSiteTypeEnum: " + cell.getSiteInst().getSiteTypeEnum();
                writer.write(s2 + s3 + s4);
            }
        }
    } // end printDesignCells()

    public void printDesignNets(Design design) throws IOException {
        writer.write("\n\nPrinting All Nets...");
        Collection<Net> nets = design.getNets();
        for (Net net : nets) {
            writer.write("\n\tNet: " + net.getName());
            List<SitePinInst> spis = net.getPins();
            if (spis.isEmpty())
                writer.write("\n\t\tSitePinInsts: None!");
            else
                for (SitePinInst spi : spis)
                    writer.write("\n\t\tSitePinInst: " + spi.getName() + " isRouted() = " + spi.isRouted());
        }
    } // end printDesignNets()

    public void printBELArray(BufferedWriter writer, BEL[] bels) throws IOException {
        if (bels.length == 0)
            writer.write("\n\t\tEmpty BEL Array.");
        int word_count = 0;
        writer.write("\n\t\t");
        for (BEL bel : bels) {
            writer.write(bel.getName() + " ");
            word_count++;
            if (word_count == 8) {
                writer.write("\n\t\t");
                word_count = 0;
            }
        }
    }

    public void printSiteArray(BufferedWriter writer, Site[] sites, boolean showBELs) throws IOException {
        if (sites.length == 0)
            writer.write("\n\tEmpty Site Array.");
        for (Site site : sites) {
            String s2 = String.format(
                    "\n\tSiteType: %-30s SiteName: %-40s ", site.getSiteTypeEnum(), site.getName());
            writer.write(s2);
            BEL[] bels = site.getBELs();
            if (showBELs == true)
                printBELArray(writer, bels);
        }
    }

    public Set<SiteTypeEnum> printUniqueSites() throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(rootDir + "outputs/printout/DeviceUniqueSites.txt"));
        writer.write("\nPrinting unique sites in the device: ");
        writer.newLine();
        Site[] sites = device.getAllSites();
        Set<SiteTypeEnum> uniqueSiteTypes = new HashSet<>();
        List<Site> uniqueSites = new ArrayList<>();
        for (Site site : sites) {
            if (uniqueSiteTypes.add(site.getSiteTypeEnum())) {
                uniqueSites.add(site);
            }
        }
        writer.write("\nNunmber of unique site types: " + uniqueSites.size());
        printSiteArray(writer, uniqueSites.toArray(new Site[0]), true);
        if (writer != null)
            writer.close();

        return uniqueSiteTypes;
    }

    public Map<String, Integer> getCoordinateMinMaxOfType(SiteTypeEnum type) throws IOException {
        Map<String, Integer> map = new HashMap<>();
        Site[] sites = device.getAllSitesOfType(type);
        Integer x_min = sites[0].getInstanceX();
        Integer x_max = sites[0].getInstanceX();
        Integer y_min = sites[0].getInstanceY();
        Integer y_max = sites[0].getInstanceY();
        for (Site site : sites) {
            int x = site.getInstanceX();
            int y = site.getInstanceY();
            if (x < x_min)
                x_min = x;
            if (x > x_max)
                x_max = x;
            if (y < y_min)
                y_min = y;
            if (y > y_max)
                y_max = y;
        }
        map.put("X_MIN", x_min);
        map.put("X_MAX", x_max);
        map.put("Y_MIN", y_min);
        map.put("Y_MAX", y_max);
        return map;
    }

    public void printSitesOfType(SiteTypeEnum type) throws IOException {
        BufferedWriter writer = new BufferedWriter(
                new FileWriter(rootDir + "outputs/printout/AllSites_" + type + ".txt"));
        Site[] sites = device.getAllSitesOfType(type);

        writer.write("Found sites of type " + type + "(" + sites.length + ").");
        Integer x_min = sites[0].getInstanceX();
        Integer x_max = sites[0].getInstanceX();
        Integer y_min = sites[0].getInstanceY();
        Integer y_max = sites[0].getInstanceY();

        for (Site site : sites) {
            int x = site.getInstanceX();
            int y = site.getInstanceY();
            if (x < x_min)
                x_min = x;
            if (x > x_max)
                x_max = x;
            if (y < y_min)
                y_min = y;
            if (y > y_max)
                y_max = y;
        }
        writer.write("\nX_MIN=" + x_min + ", X_MAX=" + x_max + ", Y_MIN=" + y_min + ", Y_MAX=" + y_max);

        writer.write("\n\nPrinting Sites of Type " + type + "...");
        for (Site site : sites) {
            writer.write(
                    "\n\tSiteName: " + site.getName() + ", X=" + site.getInstanceX() + ", Y=" + site.getInstanceY());
        }

        if (writer != null)
            writer.close();
    }

} // end class
