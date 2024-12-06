package placer;

import java.util.stream.Collectors;

import org.python.antlr.PythonParser.else_clause_return;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.LinkedHashMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Random;

import java.io.FileWriter;
import java.io.IOException;

import com.xilinx.rapidwright.design.Cell;
import com.xilinx.rapidwright.design.SiteInst;
import com.xilinx.rapidwright.design.Net;

import com.xilinx.rapidwright.edif.EDIFNetlist;
import com.xilinx.rapidwright.edif.EDIFHierNet;
import com.xilinx.rapidwright.edif.EDIFNet;
import com.xilinx.rapidwright.edif.EDIFHierCellInst;
import com.xilinx.rapidwright.edif.EDIFHierPortInst;
import com.xilinx.rapidwright.edif.EDIFCellInst;
import com.xilinx.rapidwright.edif.EDIFPortInst;

import com.xilinx.rapidwright.device.BEL;
import com.xilinx.rapidwright.device.BELPin;
import com.xilinx.rapidwright.device.Site;
import com.xilinx.rapidwright.device.SiteTypeEnum;

public class PlacerPackingHier extends Placer {

    public PlacerPackingHier() throws IOException {
        super();
        placerName = "PlacerPackingHier";
        writer = new FileWriter(rootDir + "outputs/printout/" + placerName + ".txt");
        writer.write(placerName + ".txt");
    }

    protected SiteTypeEnum selectSiteType(
            Map<SiteTypeEnum, Set<String>> compatiblePlacements) throws IOException {

        Iterator<SiteTypeEnum> iterator = compatiblePlacements.keySet().iterator();
        SiteTypeEnum selectedSiteType = iterator.next();
        if (device.getAllSitesOfType(selectedSiteType).length == 0)
            return null;
        return selectedSiteType;
    }

    protected String[] selectSiteAndBEL(
            Map<String, List<String>> availablePlacements) throws IOException {

        // Random random = new Random();
        // List<String> availableSiteNames = new
        // ArrayList<>(availablePlacements.keySet());
        // String selectedSiteName =
        // availableSiteNames.get(random.nextInt(availableSiteNames.size()));

        // List<String> availableBELNames = availablePlacements.get(selectedSiteName);
        // String selectedBELName =
        // availableBELNames.get(random.nextInt(availableBELNames.size()));

        // Select first site and in first site, first BEL
        String selectedSiteName = availablePlacements.keySet().iterator().next();
        String selectedBELName = availablePlacements.get(selectedSiteName).get(0);

        return new String[] { selectedSiteName, selectedBELName };
    }

    protected void removeOccupiedPlacements(
            Map<String, List<String>> availablePlacements,
            Map<String, List<String>> occupiedPlacements) throws IOException {
        for (Map.Entry<String, List<String>> entry : occupiedPlacements.entrySet()) {
            String siteName = entry.getKey();
            List<String> occupiedBELs = entry.getValue();

            // SINGLE BEL PER SITE
            availablePlacements.remove(siteName);

            // // BEL PACKING (CAUSES ILLEGAL PLACEMENT FOR FIRST/RANDOM PLACER)
            // if (availablePlacements.containsKey(siteName)) {
            // availablePlacements.get(siteName).removeAll(occupiedBELs);
            // if (availablePlacements.get(siteName).isEmpty())
            // availablePlacements.remove(siteName);
            // }
        }
    }

    public @Override void placeDesign() throws IOException {
        EDIFNetlist netlist = design.getNetlist();
        List<EDIFHierCellInst> ehcis = netlist.getAllLeafHierCellInstances();

        // Create a map to group cells by type
        Map<String, List<EDIFHierCellInst>> EDIFCellGroups = new LinkedHashMap<>();
        EDIFCellGroups.put("IBUF", new ArrayList<>());
        EDIFCellGroups.put("OBUF", new ArrayList<>());
        EDIFCellGroups.put("VCC", new ArrayList<>());
        EDIFCellGroups.put("GND", new ArrayList<>());
        EDIFCellGroups.put("CARRY4", new ArrayList<>());
        EDIFCellGroups.put("FDRE", new ArrayList<>());
        EDIFCellGroups.put("LUT", new ArrayList<>());
        EDIFCellGroups.put("DSP48E1", new ArrayList<>());
        EDIFCellGroups.put("RAMB18E1", new ArrayList<>());

        Set<String> uniqueEdifCellTypes = new HashSet<>();

        for (EDIFHierCellInst ehci : ehcis) {
            EDIFCellInst eci = ehci.getInst();
            // populate unique cell tyeps
            uniqueEdifCellTypes.add(eci.getCellType().getName());

            // add this cell to the corresponding group based on type
            for (String cellType : EDIFCellGroups.keySet()) {
                if (eci.getCellType().getName().contains(cellType)) {
                    EDIFCellGroups.get(cellType).add(ehci);
                    writer.write("\n\tFound " + cellType + " cell: " + eci.getName());
                    break; // once matched, no need to check other types
                }
            }
        }

        writer.write("\n\nSet of all Unique EDIF Cell Types... (" + uniqueEdifCellTypes.size() + ")");
        for (String edifCellType : uniqueEdifCellTypes) {
            writer.write("\n\t" + edifCellType);
        }
        writer.write("\nPrinting EDIFCells By Type...");
        for (Map.Entry<String, List<EDIFHierCellInst>> entry : EDIFCellGroups.entrySet()) {
            writer.write("\n\n" + entry.getKey() + " Cells (" + entry.getValue().size() + "):");
            List<EDIFCellInst> cells = entry.getValue().stream()
                    .map(e -> e.getInst()) // Assuming getInst() is a method in EDIFCellInst
                    .collect(Collectors.toList());
            printEDIFCellInstList(cells);
        }

        // BUILD CARRY CHAINS
        List<List<EDIFHierCellInst>> EDIFCarryChains = new ArrayList<>();
        List<EDIFHierCellInst> EDIFCarryCells = EDIFCellGroups.get("CARRY4");
        while (!EDIFCarryCells.isEmpty()) {
            List<EDIFHierCellInst> chain = new ArrayList<>();
            EDIFHierCellInst ehci = EDIFCarryCells.get(0);
            // every iteration, EDIFCarryCells gets updated so .get(0) is different.
            buildCarryChain(ehci, chain);
            EDIFCarryChains.add(chain);
            EDIFCarryCells.removeAll(chain);
            writer.write("\n\nPrinting cells in this carry chain...");
            for (EDIFHierCellInst cell : chain) {
                writer.write("\n\t" + cell.getInst().getName());
            }
        }

        List<List<EDIFHierCellInst>> EDIFDSPPairs = new ArrayList<>();
        List<EDIFHierCellInst> EDIFDSPCells = EDIFCellGroups.get("DSP48E1");
        // while (!EDIFDSPCells.isEmpty()) {

        // }

        List<List<EDIFHierCellInst>> EDIFLUTFFPairs = new ArrayList<>();
        List<EDIFHierCellInst> EDIFFDRECells = EDIFCellGroups.get("FDRE");
        // while (!EDIFFDRECells.isEmpty()) {

        // }

        Map<String, List<Cell>> cellGroups = new LinkedHashMap<>();
        cellGroups.put("IBUF", new ArrayList<>());
        cellGroups.put("OBUF", new ArrayList<>());
        cellGroups.put("VCC", new ArrayList<>());
        cellGroups.put("GND", new ArrayList<>());
        cellGroups.put("CARRY4", new ArrayList<>());
        cellGroups.put("LUT", new ArrayList<>());
        cellGroups.put("FDRE", new ArrayList<>());
        cellGroups.put("DSP48E1", new ArrayList<>());
        cellGroups.put("RAMB18E1", new ArrayList<>());

        // List of occupied BELs
        Map<String, List<String>> occupiedPlacements = new HashMap<>();

        placeCarryChains(EDIFCarryChains, occupiedPlacements);

        List<String> skipCells = Arrays.asList("IBUF", "OBUF", "GND", "VCC", "CARRY4", "DSP48E1");

        // SPAWN CELLS IN REMAINING GROUPS
        writer.write("\n\nSpawning remaining cells...");
        for (Map.Entry<String, List<EDIFHierCellInst>> entry : EDIFCellGroups.entrySet()) {
            if (skipCells.contains(entry.getKey()))
                continue;
            String edifCellType = entry.getKey();
            List<EDIFHierCellInst> edifCells = entry.getValue();
            for (EDIFHierCellInst edifCell : edifCells) {
                Cell cell = design.createCell(edifCell.getFullHierarchicalInstName(), edifCell.getInst());
                // cell.setEDIFHierCellInst(edifCell);
                cellGroups.get(edifCellType).add(cell);
            }
        }

        // PLACE REMAINING CELLS
        writer.write("\n\nPlacing remaining cells...");
        for (Map.Entry<String, List<Cell>> entry : cellGroups.entrySet()) {
            String cellType = entry.getKey();
            if (skipCells.contains(cellType))
                continue;
            List<Cell> cells = entry.getValue();
            writer.write("\n\tPlacing " + cellType + " cells...");
            for (Cell cell : cells) {
                writer.write("\n\t\t" + cell.getType() + " : " + cell.getName());

                placeCell(cell, occupiedPlacements);
                writer.write("\n\t\t\tPlaced cell: " + cell.getName() + " at " + cell.getSiteInst().getName() + " on "
                        + cell.getBELName());
            }
        }

        // writer.write("\n\nIntra-Routing SiteInsts... ");
        // for (String siteName : occupiedPlacements.keySet()) {
        // design.getSiteInst(siteName).routeSite();
        // }
        printOccupiedSites(occupiedPlacements);
    }

    private void placeDSPPairs(List<List<EDIFHierCellInst>> EDIFDSPPairs) {

    }

    private void placeLUTFFPairs(List<List<EDIFHierCellInst>> EDIFLUTFFPairs) {

    }

    private void placeCarryChains(List<List<EDIFHierCellInst>> EDIFCarryChains,
            Map<String, List<String>> occupiedPlacements) throws IOException {
        writer.write("\n\nPlacing carry chains... (" + EDIFCarryChains.size() + ")");

        // PLACE CARRY CHAINS
        for (List<EDIFHierCellInst> chain : EDIFCarryChains) {
            writer.write("\n\tchain size: " + chain.size());
            Random rand = new Random();

            EDIFHierCellInst anchorCell = chain.get(0);
            // EDIFHierCellInst anchorHierCell =
            // netlist.getHierCellInstFromName(anchorCell.getName());

            Cell cell = design.createCell(anchorCell.getFullHierarchicalInstName(), anchorCell.getInst());
            // cell.setEDIFHierCellInst(anchorCell);

            Map<SiteTypeEnum, Set<String>> compatiblePlacements = cell.getCompatiblePlacements(device);
            List<SiteTypeEnum> compatibleSiteTypes = new ArrayList<>(compatiblePlacements.keySet());
            int randIndex = rand.nextInt(compatibleSiteTypes.size());
            SiteTypeEnum selectedSiteType = compatibleSiteTypes.get(randIndex);

            String anchorSiteName = findCarryChainAnchorSite(selectedSiteType, chain);
            String anchorBELName = "CARRY4";
            Site anchorSite = device.getSite(anchorSiteName);
            BEL anchorBEL = anchorSite.getBEL(anchorBELName);

            // find and place the anchor cell
            if (anchorSiteName == null) {
                writer.write("\nWARNING: COULD NOT PLACE CARRY CHAIN ANCHOR!");
                break;
            } else {
                placeCarryCell(cell, anchorSite, anchorBEL, occupiedPlacements);
            }

            // place the rest of the chain
            for (int i = 1; i < chain.size(); i++) {
                Cell c = design.createCell(chain.get(i).getFullHierarchicalInstName(), chain.get(i).getInst());
                // cell.setEDIFHierCellInst(chain.get(i));
                String siteName = "SLICE_X" + anchorSite.getInstanceX() + "Y" + (anchorSite.getInstanceY() + i);
                String belName = "CARRY4";
                Site site = device.getSite(siteName);
                BEL bel = site.getBEL(belName);
                placeCarryCell(c, site, bel, occupiedPlacements);
            }

        } // end for (List<EDIFCellInst> chain : EDIFCarryChains)

    }

    private void placeCarryCell(Cell cell, Site site, BEL bel, Map<String, List<String>> occupiedPlacements)
            throws IOException {
        if (design.placeCell(cell, site, bel)) {
            writer.write("\n\tPlaced Cell: " + cell.getName() + ", Type: "
                    + cell.getType() + ", Site: " + site.getName() + ", BEL: "
                    + bel.getName());
            addToMap(occupiedPlacements, site.getName(), bel.getName());
        } else {
            writer.write("\n\tWARNING: Placement Failed! Cell: " + cell.getName() + ", Type: "
                    + cell.getType() + ", Attempted Site: " + site.getName() + ", Attempted BEL: "
                    + bel.getName());
        }
    }

    protected List<SiteInst> buildCarrySiteInsts(List<EDIFHierCellInst> chain) throws IOException {
        List<SiteInst> carrySiteInsts = new ArrayList<>();
        for (EDIFHierCellInst ehci : chain) {
            SiteInst si = new SiteInst();

        }
        return carrySiteInsts;
    }

    private String findCarryChainAnchorSite(SiteTypeEnum selectedSiteType, List<EDIFHierCellInst> chain)
            throws IOException {
        Map<String, Integer> minmax = getCoordinateMinMaxOfType(selectedSiteType);
        int x_max = minmax.get("X_MAX");
        int x_min = minmax.get("X_MIN");
        int y_max = minmax.get("Y_MAX");
        int y_min = minmax.get("Y_MIN");
        writer.write("\n\tselectedSiteType: " + selectedSiteType);
        writer.write(
                "\n\tX_MAX: " + x_max + ", X_MIN: " + x_min + ", Y_MAX: " + y_max + ", Y_MIN: " + y_min);
        String anchorSiteName = null;
        int x = 0;
        int y = 0;
        Random rand = new Random();
        boolean validAnchor = false;
        int attempts = 0;
        while (!validAnchor && attempts < 1000) {
            x = rand.nextInt((x_max - x_min) + 1) + x_min;
            y = rand.nextInt((y_max - y_min) + 1) + y_min;
            for (int j = 0; j < chain.size(); j++) {
                String name = "SLICE_X" + x + "Y" + (y + j);
                if (design.getSiteInstFromSiteName(name) != null || device.getSite(name) == null) {
                    validAnchor = false;
                    break;
                } else {
                    validAnchor = true;
                    anchorSiteName = "SLICE_X" + x + "Y" + y;
                }
            }
            attempts++;
        }
        return anchorSiteName;
    }

    protected void buildCarryChain(EDIFHierCellInst ehci, List<EDIFHierCellInst> chain) throws IOException {
        // traverse the carry chain in the cin direction to find starting cell of chain
        // the start of chain occurs when CIN connects to GND
        EDIFHierCellInst currCell = ehci;
        while (true) {
            EDIFHierPortInst currCellPort = currCell.getPortInst("CI");
            EDIFHierNet hnet = currCellPort.getHierarchicalNet();
            if (hnet.getNet().isGND())
                break;
            Collection<EDIFHierPortInst> netPorts = hnet.getPortInsts();
            Map<String, EDIFHierPortInst> netPortsMap = netPorts.stream()
                    .collect(Collectors.toMap(
                            p -> p.getPortInst().getName(),
                            p -> p));
            EDIFHierPortInst sourceCellPort = netPortsMap.get("CO[3]");
            EDIFHierCellInst sourceCell = sourceCellPort.getHierarchicalInst()
                    .getChild(sourceCellPort.getPortInst().getCellInst().getName());
            currCell = sourceCell;
        }

        // we now have the starting carry cell as currCell
        // now traverse in the cout direction
        // the end of the chain occurs when portinst CO[3] is null

        while (true) {
            chain.add(currCell);
            EDIFHierPortInst currCellPort = currCell.getPortInst("CO[3]");
            if (currCellPort == null)
                break;
            EDIFHierNet hnet = currCellPort.getHierarchicalNet();
            Collection<EDIFHierPortInst> netPorts = hnet.getPortInsts();
            Map<String, EDIFHierPortInst> netPortsMap = netPorts.stream()
                    .collect(Collectors.toMap(
                            p -> p.getPortInst().getName(),
                            p -> p));
            EDIFHierPortInst sinkCellPort = netPortsMap.get("CI");
            EDIFHierCellInst sinkCell = sinkCellPort.getHierarchicalInst()
                    .getChild(sinkCellPort.getPortInst().getCellInst().getName());
            currCell = sinkCell;
        }
    }

    protected void routeRAMSite(SiteInst si) throws IOException {
        writer.write("\n\nPrinting RAM Site Nets.../" + si.getName());
        Map<Net, List<String>> map = si.getNetToSiteWiresMap();
        for (Map.Entry<Net, List<String>> entry : map.entrySet()) {
            Net net = entry.getKey();
            List<String> wires = entry.getValue();
            writer.write("\n\tNet: " + net.getName());
            for (String wire : wires) {
                writer.write("\n\t\tWire: " + wire);
            }
        }
    }

} // end class
