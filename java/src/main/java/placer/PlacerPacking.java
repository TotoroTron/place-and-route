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
            availablePlacements.remove(siteName);

            // BEL PACKING VERSION (WILL CAUSE ILLEGAL PLACEMENT)
            // if (availablePlacements.containsKey(siteName)) {
            // availablePlacements.get(siteName).removeAll(occupiedBELs);
            // if (availablePlacements.get(siteName).isEmpty()) {
            // availablePlacements.remove(siteName);
            // }
            // }
        }
    }

    public @Override void placeDesign() throws IOException {
        writer.write("\n\nPlacing Cells...");

        List<Cell> cells = spawnCells(); // returns placeable cells (no buffer or port cells)
        Map<String, List<String>> occupiedPlacements = new HashMap<>();

        List<Cell> CARRYCells = new ArrayList<>();
        List<Cell> FFCells = new ArrayList<>();
        List<Cell> LUTCells = new ArrayList<>();

        writer.write("\n\nPrinting Cell Types...");
        for (Cell cell : cells) {
            writer.write("\n\tCell: " + cell.getName() + " Type: " + cell.getType());
        }

        for (Cell cell : cells) {

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
                writer.write("\n\tPlacement success! Cell: " + cell.getName() + ", Site: " + selectedSiteName
                        + ", BEL: " + selectedBELName);
                addToMap(occupiedPlacements, selectedPlacement[0], selectedPlacement[1]);
            } else {
                writer.write("\n\tWARNING: Placement Failed!");
            }

        } // end for(Cell)

    } // end placDesign()

}
