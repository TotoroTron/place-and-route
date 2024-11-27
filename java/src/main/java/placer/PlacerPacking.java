package placer;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
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

import com.xilinx.rapidwright.edif.EDIFCellInst;
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

        Random random = new Random();
        List<String> availableSiteNames = new ArrayList<>(availablePlacements.keySet());
        String selectedSiteName = availableSiteNames.get(random.nextInt(availableSiteNames.size()));

        List<String> availableBELNames = availablePlacements.get(selectedSiteName);
        String selectedBELName = availableBELNames.get(random.nextInt(availableBELNames.size()));

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
            if (availablePlacements.containsKey(siteName)) {
                availablePlacements.get(siteName).removeAll(occupiedBELs);
                if (availablePlacements.get(siteName).isEmpty())
                    availablePlacements.remove(siteName);
            }
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

        printSitesOfType(SiteTypeEnum.SLICEL);
        printSitesOfType(SiteTypeEnum.SLICEM);

        writer.write("\n\nPlacing carry chains... (" + EDIFCarryChains.size() + ")");

        // PLACE CARRY CHAINS
        Random rand = new Random();
        for (List<EDIFCellInst> chain : EDIFCarryChains) {
            writer.write("\n\tchain size: " + chain.size());
            int x_anchor = 0;
            int y_anchor = 0;

            for (int i = 0; i < chain.size(); i++) {
                Cell cell = design.createCell(chain.get(i).getName(), chain.get(i));
                Cell anchor = null;

                Map<SiteTypeEnum, Set<String>> compatiblePlacements = cell.getCompatiblePlacements(device);
                List<SiteTypeEnum> compatibleSiteTypes = new ArrayList<>(compatiblePlacements.keySet());
                int randIndex = rand.nextInt(compatibleSiteTypes.size());
                SiteTypeEnum selectedSiteType = compatibleSiteTypes.get(randIndex);

                if (i == 0) {
                    Map<String, Integer> minmax = getCoordinateMinMaxOfType(selectedSiteType);
                    int x_max = minmax.get("X_MAX");
                    int x_min = minmax.get("X_MIN");
                    int y_max = minmax.get("Y_MAX");
                    int y_min = minmax.get("Y_MIN");
                    writer.write("\n\tselectedSiteType: " + selectedSiteType);
                    writer.write("\n\tX_MAX: " + x_max + ", X_MIN: " + x_min + ", Y_MAX: " + y_max + ", Y_MIN" + y_min);
                    String anchorSiteName = null;
                    String anchorBELName = null;
                    int x = 0;
                    int y = 0;

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
                            }
                        }
                        attempts++;
                    } // end while

                    if (validAnchor) {
                        x_anchor = x;
                        y_anchor = y;
                        anchorSiteName = "SLICE_X" + x_anchor + "Y" + y_anchor;
                        anchorBELName = "CARRY4";
                        Site anchorSite = device.getSite(anchorSiteName);
                        BEL anchorBEL = anchorSite.getBEL(anchorBELName);
                        if (design.placeCell(cell, anchorSite, anchorBEL)) {
                            writer.write("\n\tPlaced Cell: " + cell.getName() + ", Type: "
                                    + cell.getType() + ", Site: " + anchorSiteName + ", BEL: "
                                    + anchorBELName);

                            addToMap(occupiedPlacements, anchorSiteName, anchorBELName);
                        } else {
                            writer.write("\n\tWARNING: Placement Failed! Cell: " + cell.getName() + ", Type: "
                                    + cell.getType() + ", Attempted Site: " + anchorSiteName + ", Attempted BEL: "
                                    + anchorBELName);
                        }
                    } else {
                        writer.write("\nCOULD NOT PLACE CARRY CHAIN ANCHOR!");
                    }

                } else {
                    String siteName = "SLICE_X" + x_anchor + "Y" + (y_anchor + i);
                    String belName = "CARRY4";
                    Site site = device.getSite(siteName);
                    BEL bel = site.getBEL(belName);

                    if (design.placeCell(cell, site, bel)) {
                        writer.write("\n\tPlaced Cell: " + cell.getName() + ", Type: "
                                + cell.getType() + ", Site: " + siteName + ", BEL: "
                                + belName);
                        addToMap(occupiedPlacements, siteName, belName);
                    } else {
                        writer.write("\n\tWARNING: Placement Failed! Cell: " + cell.getName() + ", Type: "
                                + cell.getType() + ", Attempted Site: " + siteName + ", Attempted BEL: "
                                + belName);
                    }
                } // end if (i==0)

            } // end for (int i = 0; i < chain.size(); i++)

        } // end for (List<EDIFCellInst> chain : EDIFCarryChains)

        writer.write("\n\nSpawning remaining cells...");
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

        writer.write("\n\nPlacing cells...");
        // PLACE REMAINING CELLS
        for (Map.Entry<String, List<Cell>> entry : cellGroups.entrySet()) {
            String cellType = entry.getKey();
            if (cellType == "IBUF" || cellType == "OBUF" || cellType == "GND"
                    || cellType == "VCC" || cellType == "CARRY4") {
                continue;
            }
            List<Cell> cells = entry.getValue();
            writer.write("\n\tPlacing " + cellType + " cells...");
            for (Cell cell : cells) {
                writer.write("\n\t\t" + cell.getType() + " : " + cell.getName());
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

}
// end class
