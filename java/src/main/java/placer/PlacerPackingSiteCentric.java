
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

import com.xilinx.rapidwright.edif.EDIFNetlist;
import com.xilinx.rapidwright.edif.EDIFHierNet;
import com.xilinx.rapidwright.edif.EDIFHierCellInst;
import com.xilinx.rapidwright.edif.EDIFHierPortInst;
import com.xilinx.rapidwright.edif.EDIFCellInst;

import com.xilinx.rapidwright.device.BEL;
import com.xilinx.rapidwright.device.Site;
import com.xilinx.rapidwright.device.SiteTypeEnum;

public class PlacerPackingSiteCentric extends Placer {

    public PlacerPackingSiteCentric() throws IOException {
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
        Map<String, List<EDIFHierCellInst>> EDIFCellGroups = new HashMap<>();
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
        // while (!EDIFCarryCells.isEmpty()) {
        // }

        // BUILD DSP PAIRS
        List<List<EDIFHierCellInst>> EDIFDSPPairs = new ArrayList<>();
        List<EDIFHierCellInst> EDIFDSPCells = EDIFCellGroups.get("DSP48E1");
        // while (!EDIFDSPCells.isEmpty()) {
        // }

        // BUILD LUT-FF PAIRS
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

        // REPLACE WITH SITE CENTRIC
        // placeCarryChains(EDIFCarryChains, occupiedPlacements);

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

        List<EDIFHierCellInst[]> DSPPairs = findDSPPairs(EDIFCellGroups);
        Map<EDIFHierCellInst, List<EDIFHierCellInst>> LUTFFTrees = findLUTFFTrees(EDIFCellGroups);
        List<EDIFHierCellInst[]> LUTFFPairs = findLUTFFPairs(EDIFCellGroups);
        List<List<EDIFHierCellInst>> CARRYChains = findCarryChains(EDIFCellGroups);

        // writer.write("\n\nIntra-Routing SiteInsts... ");
        // for (String siteName : occupiedPlacements.keySet()) {
        // design.getSiteInst(siteName).routeSite();
        // }
        printOccupiedSites(occupiedPlacements);
    }

    private Map<EDIFHierCellInst, List<EDIFHierCellInst>> findLUTFFTrees(
            Map<String, List<EDIFHierCellInst>> EDIFCellGroups) throws IOException {
        Map<EDIFHierCellInst, List<EDIFHierCellInst>> trees = new HashMap<>();
        for (EDIFHierCellInst LUT_EHCI : EDIFCellGroups.get("LUT")) {
            EDIFHierPortInst LUT_EHPI = LUT_EHCI.getPortInst("O");
            EDIFHierNet HNET = LUT_EHPI.getHierarchicalNet();
            // exclude sources, include sinks
            List<EDIFHierPortInst> sinkPorts = HNET.getLeafHierPortInsts(false, true);
            if (sinkPorts.size() < 2) // less than 2 sinks?
                continue;
            List<EDIFHierCellInst> FF_EHCIS = sinkPorts.stream()
                    .map(ehpi -> ehpi.getHierarchicalInst().getChild(ehpi.getPortInst().getCellInst().getName()))
                    .filter(ehci -> ehci.getCellType().getName().equals("FDRE"))
                    .collect(Collectors.toList());
            if (FF_EHCIS.size() < 2) // less than 2 ff sinks?
                continue;
            trees.put(LUT_EHCI, FF_EHCIS);
            EDIFCellGroups.get("LUT").remove(LUT_EHCI);
            EDIFCellGroups.get("FDRE").removeAll(FF_EHCIS);
        }
        return trees;
    }

    private List<EDIFHierCellInst[]> findLUTFFPairs(Map<String, List<EDIFHierCellInst>> EDIFCellGroups)
            throws IOException {
        List<EDIFHierCellInst[]> pairs = new ArrayList<>();
        for (EDIFHierCellInst LUT_EHCI : EDIFCellGroups.get("LUT")) {
            EDIFHierPortInst LUT_EHPI = LUT_EHCI.getPortInst("O");
            EDIFHierNet HNET = LUT_EHPI.getHierarchicalNet();
            // exclude sources, include sinks
            List<EDIFHierPortInst> sinkPorts = HNET.getLeafHierPortInsts(false, true);
            if (sinkPorts.size() != 1)
                continue;
            EDIFHierPortInst FF_EHPI = sinkPorts.get(0);
            EDIFHierCellInst FF_EHCI = FF_EHPI.getHierarchicalInst()
                    .getChild(FF_EHPI.getPortInst().getCellInst().getName());
            if (!FF_EHCI.getCellType().getName().contains("FDRE"))
                continue;
            EDIFHierCellInst[] pair = new EDIFHierCellInst[2];
            pair[0] = LUT_EHCI;
            pair[1] = FF_EHCI;
            EDIFCellGroups.get("LUT").remove(LUT_EHCI);
            EDIFCellGroups.get("FDRE").remove(FF_EHCI);
            pairs.add(pair);
        }
        return pairs;
    }

    private List<EDIFHierCellInst[]> findDSPPairs(Map<String, List<EDIFHierCellInst>> EDIFCellGroups) {
        List<EDIFHierCellInst[]> pairs = new ArrayList<>();
        for (EDIFHierCellInst DSP_EHCI : EDIFCellGroups.get("DSP48E1")) {
            List<EDIFHierPortInst> pcins = DSP_EHCI.getHierPortInsts().stream()
                    .filter(ehpi -> ehpi.getPortInst().getName().contains("PCIN"))
                    .collect(Collectors.toList());
            if (pcins.isEmpty())
                continue;
            List<EDIFHierPortInst> pcouts = pcins.stream()
                    .map(cin -> cin.getHierarchicalNet().getLeafHierPortInsts(true, false).get(0))
                    .collect(Collectors.toList());

            Set<EDIFHierCellInst> pcoutCells = pcouts.stream()
                    .map(cout -> cout.getHierarchicalInst().getChild(cout.getPortInst().getCellInst().getName()))
                    .collect(Collectors.toSet());

            //
            // ************WIP************
            // FIND ANY/ALL DSP CELLS CONNECTED TO THIS CELL'S CIN PORTS
            // IF MORE THAN ONE CONNECTED DSP, AN ERROR HAS OCCURRED
            //
            //
        }

        return pairs;
    }

    private List<List<EDIFHierCellInst>> findCarryChains(Map<String, List<EDIFHierCellInst>> EDIFCellGroups) {
        List<List<EDIFHierCellInst>> chains = new ArrayList<>();
        while (!EDIFCellGroups.get("CARRY4").isEmpty()) {
            List<EDIFHierCellInst> chain = new ArrayList<>();
            EDIFHierCellInst currCell = EDIFCellGroups.get("CARRY4").get(0);
            // every iteration, EDIFCarryCells gets updated so .get(0) is different.
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
            chains.add(chain);
        }
        return chains;
    }

    private List<SiteInst> buildCarrySiteInsts(List<EDIFHierCellInst> chain,
            Map<String, List<EDIFHierCellInst>> EDIFCellGroups) throws IOException {

        List<SiteInst> carrySiteInsts = new ArrayList<>();
        for (EDIFHierCellInst ehci : chain) {
            SiteInst si = new SiteInst();
            si.createCell(ehci, si.getBEL("CARRY4"));
            EDIFCellGroups.get("CARRY4").remove(ehci);

            Map<String, String[]> O_CARRY_FF_MAP = new HashMap<>();
            O_CARRY_FF_MAP.put("O0", new String[] { "AFF", "A5FF" });
            O_CARRY_FF_MAP.put("O1", new String[] { "BFF", "B5FF" });
            O_CARRY_FF_MAP.put("O2", new String[] { "CFF", "C5FF" });
            O_CARRY_FF_MAP.put("O3", new String[] { "DFF", "D5FF" });

            for (Map.Entry<String, String[]> entry : O_CARRY_FF_MAP.entrySet()) {
                String PORT_NAME = entry.getKey();
                EDIFHierPortInst ehpi = ehci.getPortInst(PORT_NAME);
                EDIFHierNet hnet = ehpi.getHierarchicalNet();
                // bool include sources, bool include sinks
                EDIFHierPortInst sinkPort = hnet.getLeafHierPortInsts(false, true).get(0);
                EDIFHierCellInst sinkCell = sinkPort.getHierarchicalInst()
                        .getChild(sinkPort.getPortInst().getCellInst().getName());
                // for now, just always use FF not 5FF
                if (sinkCell.getCellType().getName() == "FDRE") {
                    si.createCell(sinkCell, si.getBEL(entry.getValue()[0])); // XFF
                    EDIFCellGroups.get("FDRE").remove(sinkCell);
                }
            }

            Map<String, String[]> S_CARRY_LUT_MAP = new HashMap<>();
            S_CARRY_LUT_MAP.put("S0", new String[] { "A5LUT", "A6LUT" });
            S_CARRY_LUT_MAP.put("S1", new String[] { "B5LUT", "B6LUT" });
            S_CARRY_LUT_MAP.put("S2", new String[] { "C5LUT", "C6LUT" });
            S_CARRY_LUT_MAP.put("S3", new String[] { "D5LUT", "D6LUT" });

            for (Map.Entry<String, String[]> entry : S_CARRY_LUT_MAP.entrySet()) {
                String PORT_NAME = entry.getKey();
                EDIFHierPortInst ehpi = ehci.getPortInst(PORT_NAME);
                EDIFHierNet hnet = ehpi.getHierarchicalNet();
                // bool include sources, bool include sinks
                EDIFHierPortInst sourcePort = hnet.getLeafHierPortInsts(true, false).get(0);
                EDIFHierCellInst sourceCell = sourcePort.getHierarchicalInst()
                        .getChild(sourcePort.getPortInst().getCellInst().getName());
                String sourceCellType = sourceCell.getCellType().getName();
                if (sourceCellType == "LUT6") {
                    si.createCell(sourceCell, si.getBEL(entry.getValue()[1])); // X6LUT
                    EDIFCellGroups.get("LUT").remove(sourceCell);
                } else if (sourceCellType.contains("LUT")) {
                    si.createCell(sourceCell, si.getBEL(entry.getValue()[0])); // X5LUT
                    EDIFCellGroups.get("LUT").remove(sourceCell);
                }
            }

            carrySiteInsts.add(si);

        } // end for (EDIFHierCellInst ehci : chain)

        return carrySiteInsts;
    } // end buildCarrySiteInsts()

} // end class
