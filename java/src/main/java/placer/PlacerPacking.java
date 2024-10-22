package placer;

import java.util.stream.Collectors;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;

import java.io.FileWriter;
import java.io.IOException;

import com.xilinx.rapidwright.design.Cell;

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
            // availablePlacements.remove(siteName);

            // BEL PACKING (CAUSES ILLEGAL PLACEMENT FOR FIRST/RANDOM PLACER)
            if (availablePlacements.containsKey(siteName)) {
                availablePlacements.get(siteName).removeAll(occupiedBELs);
                if (availablePlacements.get(siteName).isEmpty()) {
                    availablePlacements.remove(siteName);
                }
            }
        }
    }

    private void placeCell(
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

        System.out.println("Selected Site + BEL: " + selectedSiteName + ", " + selectedBELName);
        Site selectedSite = device.getSite(selectedSiteName);
        BEL selectedBEL = selectedSite.getBEL(selectedBELName);
        if (design.placeCell(cell, selectedSite, selectedBEL)) {
            String s1 = String.format(
                    "\n\tcellName: %-40s Site: %-10s BEL: %-10s",
                    cell.getName(), selectedSiteName, selectedBELName);
            addToMap(occupiedPlacements, selectedPlacement[0], selectedPlacement[1]);
        } else {
            writer.write("\n\tWARNING: Placement Failed!");
        }

    } // end placeCell()

    public @Override void placeDesign() throws IOException {
        writer.write("\n\nPlacing Design...");

        List<Cell> cells = spawnCells(); // returns placeable cells (no buffer or port cells)
        Map<String, List<String>> occupiedPlacements = new HashMap<>();

        List<Cell> CARRYCells = new ArrayList<>();
        List<Cell> FFCells = new ArrayList<>();
        List<Cell> LUTCells = new ArrayList<>();

        writer.write("\n\nPrinting Cell Types...");
        for (Cell cell : cells) {
            String s1 = String.format(
                    "\n\tcellName: %-40s cellType = %-10s",
                    cell.getName(), cell.getType());
            writer.write(s1);
        }

        for (Cell cell : cells) {
            if (cell.getType() == "CARRY4") {
                CARRYCells.add(cell);
                cells.remove(cell);
            }
            if (cell.getType() == "FDRE") {
                FFCells.add(cell);
                cells.remove(cell);
            }
            if (cell.getType().contains("LUT")) {
                LUTCells.add(cell);
                cells.remove(cell);
            }
        }

        for (Cell CARRYCell : CARRYCells) {
            /*
             * Find the input and output pins of this CARRY cell.
             *
             * First, check if this carry-in is sourced by another carry or if carry-out
             * is the source of another carry.
             * Place those CARRY cells first in some recursive fashion.
             * while(true) with break conditions?.
             *
             * Do the output pins connect to FF cells?
             * If so, find that FF cell and place it in the same site.
             *
             * Do the input pins connect to LUT cells?
             * If so, find that LUT cell and place it in the same site.
             */
            placeCell(CARRYCell, occupiedPlacements);
        }

        for (Cell FFCell : FFCells) {
            /*
             * Find the input pins of the FF cell.
             * Do the input pins connect to LUTs?
             * If so, find that LUT and place it in the same site.
             */
            placeCell(FFCell, occupiedPlacements);
        }

        for (Cell LUTCell : LUTCells) {
            /*
             * By now, most LUTs should already be placed.
             * If this LUT connects to other LUTs,
             * try to place them in an *adjacent* site
             */
            placeCell(LUTCell, occupiedPlacements);
        }

    } // end placeDesign()

} // end class
