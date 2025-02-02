package placer;

import java.util.stream.Collectors;

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
import com.xilinx.rapidwright.device.ClockRegion;

public abstract class Placer {
    protected String placerName;
    FileWriter writer;

    protected final Device device;
    protected final Design design;
    // protected EDIFNetlist netlist;

    protected String rootDir;
    protected String placedDcp;

    protected String[] FF5_BELS = new String[] { "A5FF", "B5FF", "C5FF", "D5FF" };
    protected String[] FF_BELS = new String[] { "AFF", "BFF", "CFF", "DFF" };
    protected String[] LUT5_BELS = new String[] { "A5LUT", "B5LUT", "C5LUT", "D5LUT" };
    protected String[] LUT6_BELS = new String[] { "A6LUT", "B6LUT", "C6LUT", "D6LUT" };

    public Placer(String rootDir, Design design, Device device) throws IOException {
        this.rootDir = rootDir;
        this.placedDcp = rootDir + "/outputs/placed.dcp";
        this.design = design;
        this.device = device;
    }

    public void run(PackedDesign packedDesign) throws IOException {
        writer = new FileWriter(rootDir + "/outputs/printout/" + placerName + ".txt");
        writer.write(placerName + ".txt");
        placeDesign(packedDesign);
        writer.close();
        design.writeCheckpoint(placedDcp);
    }

    protected abstract void placeDesign(PackedDesign packedDesign) throws IOException;

    public static int ceilDiv(int x, int y) {
        return -Math.floorDiv(-x, y);
    }

    public static <T> List<List<T>> splitIntoGroups(List<T> list, int groupSize) {
        List<List<T>> result = new ArrayList<>();
        for (int i = 0; i < list.size(); i += groupSize) {
            // Create subgroups of size groupSize
            List<T> group = list.subList(i, Math.min(i + groupSize, list.size()));
            result.add(new ArrayList<>(group));
        }
        return result;
    }

    public static <T> List<List<T>> splitIntoPairs(List<T> list) {
        List<List<T>> pairs = new ArrayList<>();
        for (int i = 0; i < list.size(); i += 2) {
            T first = list.get(i);
            T second = (i + 1 < list.size()) ? list.get(i + 1) : null;
            pairs.add(List.of(first, second));
        }
        return pairs;
    }

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

    private void writeChainDetails(List<CarryCellGroup> chain) throws IOException {
        writer.write("\n\tAnchor Carry: " + safeGetName(chain.get(0).carry()));
        writeLutsAndFFs(chain.get(0));
        for (int i = 1; i < chain.size(); i++) {
            writer.write("\n\t\tCarry: " + safeGetName(chain.get(i).carry()));
            writeLutsAndFFs(chain.get(i));
        }
    }

    private void writeLutsAndFFs(CarryCellGroup group) throws IOException {
        for (int j = 0; j < 4; j++) {
            writer.write("\n\t\tLUT: " + safeGetName(group.luts().get(j)));
            writer.write("\n\t\t FF: " + safeGetName(group.ffs().get(j)));
        }
    }

    private String safeGetName(EDIFHierCellInst instance) {
        return instance != null ? instance.getFullHierarchicalInstName() : "Null!";
    }

    public void printCARRYChains(List<List<CarryCellGroup>> CARRYChains) throws IOException {
        writer.write("\n\nPrinting CARRYChains... (" + CARRYChains.size() + ")");
        for (List<CarryCellGroup> chain : CARRYChains) {
            writeChainDetails(chain);
        }
    }

    public void printDSPPairs(List<Pair<EDIFHierCellInst, EDIFHierCellInst>> DSPPairs) throws IOException {
        writer.write("\n\nPrinting DSPPairs... (" + DSPPairs.size() + ")");
        for (Pair<EDIFHierCellInst, EDIFHierCellInst> pair : DSPPairs) {
            writer.write("\n\t(" + pair.key().getCellType().getName() + ": " + pair.key().getFullHierarchicalInstName()
                    + ", " + pair.value().getCellType().getName() + ": " + pair.value().getFullHierarchicalInstName()
                    + ")");
        }
    }

    public void printLUTFFGroups(
            Map<Pair<String, String>, LUTFFGroup> LUTFFGroups)
            throws IOException {
        for (Pair<String, String> pair : LUTFFGroups.keySet()) {
            String s1 = String.format("\n\tCE: %-50s R: %-50s", pair.key(), pair.value());
            writer.write(s1);
        }
        writer.write("\n\nPrinting Unique CE-R pairs... (" + LUTFFGroups.size() + ")");
        writer.write(
                "\n\nPrinting Unique CE-R pairs with associated FF Cells... (" + LUTFFGroups.size() + ")");
        for (Map.Entry<Pair<String, String>, LUTFFGroup> entry : LUTFFGroups.entrySet()) {
            Pair<String, String> netPair = entry.getKey();
            String CENet = netPair.key();
            String RNet = netPair.value();
            LUTFFGroup lutffgroup = entry.getValue();
            writer.write(
                    "\n\tCENet: " + CENet + ", RNet: " + RNet + " with cells... (" + lutffgroup.group().size() + ")");
            for (Pair<EDIFHierCellInst, EDIFHierCellInst> pair : lutffgroup.group()) {
                if (pair.key() == null) {
                    writer.write("\n\t\tLUT: NULL!" + " => FF: "
                            + pair.value().getFullHierarchicalInstName());
                } else {
                    writer.write("\n\t\tLUT: " + pair.key().getFullHierarchicalInstName() + " => FF: "
                            + pair.value().getFullHierarchicalInstName());
                }
            }
        }
    }

    public void printLUTGroups(List<List<EDIFHierCellInst>> LUTGroups) throws IOException {
        writer.write("\n\nPrinting LUT Groups... (" + LUTGroups.size() + ")");
        for (List<EDIFHierCellInst> list : LUTGroups) {
            writer.write("\n\tGroup:");
            for (EDIFHierCellInst ehci : list) {
                writer.write("\n\t\t" + ehci.getCellType() + ": " + ehci.getFullHierarchicalInstName());
            }
        }
    }

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

    private void printClockBufferSite(BufferedWriter writer, Site site) throws IOException {
        writer.write("\nSite: " + site.getName()
                + ", Type: " + site.getSiteTypeEnum()
                + ", Clock Region: " + site.getClockRegion());
    }

    public void printClockBuffers() throws IOException {
        BufferedWriter writer = new BufferedWriter(
                new FileWriter(rootDir + "/outputs/printout/DeviceGlobalClkBuffers.txt"));

        Site[] sites = device.getAllSites();

        writer.write("\n\nPrinting Global Clock Buffer Sites in the device... ");
        for (Site site : sites)
            if (site.isGlobalClkBuffer())
                printClockBufferSite(writer, site);

        writer.write("\n\nPrinting Regional Clock Buffer Sites in the device... ");
        for (Site site : sites)
            if (site.isRegionalClkBuffer())
                printClockBufferSite(writer, site);

        writer.write("\n\nPrinting Global Clock Pads in the device... ");
        for (Site site : sites)
            if (site.isGlobalClkPad())
                printClockBufferSite(writer, site);

        writer.write("\n\nPrinting Regional Clock Pads Sites in the device... ");
        for (Site site : sites)
            if (site.isRegionalClkPad())
                printClockBufferSite(writer, site);

        if (writer != null)
            writer.close();
        return;

    }

    public Set<SiteTypeEnum> printUniqueSites() throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(rootDir + "/outputs/printout/DeviceUniqueSites.txt"));
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
                new FileWriter(rootDir + "/outputs/printout/AllSites_" + type + ".txt"));
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
