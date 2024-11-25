package placer;

import java.util.stream.Collectors;
import java.util.Iterator;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import java.io.FileWriter;
import java.io.IOException;

import com.xilinx.rapidwright.design.Cell;
import com.xilinx.rapidwright.edif.EDIFCellInst;
import com.xilinx.rapidwright.edif.EDIFHierCellInst;
import com.xilinx.rapidwright.edif.EDIFPortInst;
import com.xilinx.rapidwright.edif.EDIFPropertyValue;
import com.xilinx.rapidwright.edif.EDIFHierPortInst;
import com.xilinx.rapidwright.edif.EDIFDirection;
import com.xilinx.rapidwright.edif.EDIFNet;
import com.xilinx.rapidwright.edif.EDIFNetlist;

import com.xilinx.rapidwright.device.BEL;
import com.xilinx.rapidwright.device.Site;
import com.xilinx.rapidwright.device.SiteTypeEnum;

public class PlacerPacking extends Placer {

    public PlacerPacking() throws IOException {
        super();
        placerName = "PlacerPacking";
        writer = new FileWriter(rootDir + "outputs/printout/" + placerName + ".txt");
        writer.write(placerName + ".txt");
    }

    protected SiteTypeEnum selectSiteType(Map<SiteTypeEnum, Set<String>> compatiblePlacements) throws IOException {
        Iterator<SiteTypeEnum> iterator = compatiblePlacements.keySet().iterator();
        SiteTypeEnum selectedSiteType = iterator.next();
        if (device.getAllSitesOfType(selectedSiteType).length == 0)
            return null;
        return selectedSiteType;
    }

    protected String[] selectSiteAndBEL(Map<String, List<String>> availablePlacements) throws IOException {
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

            // BEL PACKING (CAUSES ILLEGAL PLACEMENT FOR FIRST/RANDOM PLACER)
            // if (availablePlacements.containsKey(siteName)) {
            // availablePlacements.get(siteName).removeAll(occupiedBELs);
            // if (availablePlacements.get(siteName).isEmpty()) {
            // availablePlacements.remove(siteName);
            // }
            // }
        }
    }

    public @Override void placeDesign() throws IOException {
        EDIFNetlist netlist = design.getNetlist();
        List<EDIFCellInst> ecis = netlist.getAllLeafCellInstances();

        // Create a map to group cells by type
        Map<String, List<EDIFCellInst>> EDIFCellGroups = new LinkedHashMap<>();
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

        for (EDIFCellInst eci : ecis) {
            // populate unique cell tyeps
            uniqueEdifCellTypes.add(eci.getCellType().getName());

            // add this cell to the corresponding group based on type
            for (String cellType : EDIFCellGroups.keySet()) {
                if (eci.getCellType().getName().contains(cellType)) {
                    EDIFCellGroups.get(cellType).add(eci);
                    writer.write("\n\tFound " + cellType + " cell: " + eci.getCellName());
                    break; // once matched, no need to check other types
                }
            }
        }

        writer.write("\n\nSet of all Unique EDIF Cell Types... (" + uniqueEdifCellTypes.size() + ")");
        for (String edifCellType : uniqueEdifCellTypes) {
            writer.write("\n\t" + edifCellType);
        }
        writer.write("\nPrinting Cells By Type...");
        for (Map.Entry<String, List<EDIFCellInst>> entry : EDIFCellGroups.entrySet()) {
            writer.write("\n\n" + entry.getKey() + " Cells (" + entry.getValue().size() + "):");
            printEDIFCellInstList(entry.getValue());
        }

        // BUILD CARRY CHAINS
        List<List<EDIFCellInst>> EDIFCarryChains = new LinkedList<>();
        List<EDIFCellInst> EDIFCarryCells = EDIFCellGroups.get("CARRY4");
        while (!EDIFCarryCells.isEmpty()) {
            List<EDIFCellInst> chain = new ArrayList<>();
            EDIFCellInst ehci = EDIFCarryCells.get(0);
            buildCarryChain(ehci, chain);
            EDIFCarryChains.add(chain);
            EDIFCarryCells.removeAll(chain);
            writer.write("\n\nPrinting cells in this carry chain...");
            for (EDIFCellInst cell : chain) {
                writer.write("\n\t" + cell.getName());
            }
        }

        // PLACE ALL CARRY CELLS USING CARRY CHAIN STRUCTURE
        // PLACE EACH CONSECUTIVE CELL IN CHAIN ONE TILE ABOVE
        // FOR EACH CARRY CELL, PLACE CONNECTED LUTS AND FFS IN THE SAME SITE

        Map<String, List<Cell>> cellGroups = new LinkedHashMap<>();
        cellGroups.put("IBUF", new ArrayList<>());
        cellGroups.put("OBUF", new ArrayList<>());
        cellGroups.put("VCC", new ArrayList<>());
        cellGroups.put("GND", new ArrayList<>());
        cellGroups.put("CARRY4", new ArrayList<>());
        cellGroups.put("FDRE", new ArrayList<>());
        cellGroups.put("LUT", new ArrayList<>());
        cellGroups.put("DSP48E1", new ArrayList<>());
        cellGroups.put("RAMB18E1", new ArrayList<>());

        // List of occupied BELs
        Map<String, List<String>> occupiedPlacements = new HashMap<>();

        // PLACE CARRY CHAINS
        for (List<EDIFCellInst> chain : EDIFCarryChains) {
            for (int i = 0; i < chain.size(); i++) {
                Cell cell = design.createCell(chain.get(i).getName(), chain.get(i));
                if (i == 0) {
                    // place the cell normally, randomly
                    placeCell(cell, occupiedPlacements);
                } else {

                    // place cell in the site above previous cell
                }
            }
        }

        // SPAWN CELLS IN REMAINING GROUPS
        for (Map.Entry<String, List<EDIFCellInst>> entry : EDIFCellGroups.entrySet()) {
            String edifCellType = entry.getKey();
            if (edifCellType == "CARRY4")
                continue;
            List<EDIFCellInst> edifCells = entry.getValue();
            for (EDIFCellInst edifCell : edifCells) {
                cellGroups.get(edifCellType).add(design.createCell(edifCell.getName(), edifCell));
            }
        }

        // PLACE REMAINING CELLS
        for (Map.Entry<String, List<Cell>> entry : cellGroups.entrySet()) {
            String cellType = entry.getKey();
            if (cellType == "CARRY4")
                continue;
            List<Cell> cells = entry.getValue();
            for (Cell cell : cells) {
                placeCell(cell, occupiedPlacements);
            }
        }

        // IDENTIFY LUT FF PAIRS
        // PLACE THOSE LUTS AND FFS IN SAME SITE, SAME ROW
        //
        // PLACE REMAINING LUTS
        // PLACE REMAINING FFS
        //
        // PLACE ALL RAMS
        // PLACE ALL DSPS

    }

} // end class
