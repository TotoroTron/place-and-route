package placer;

import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Iterator;
import java.util.Random;

import java.io.FileWriter;
import java.io.IOException;

import com.xilinx.rapidwright.device.SiteTypeEnum;

public class PlacerRandom extends Placer {

    public PlacerRandom() throws IOException {
        super();
        placerName = "PlacerRandom";
        writer = new FileWriter(rootDir + "outputs/printout/" + placerName + ".txt");
    }

    protected SiteTypeEnum selectSiteType(Map<SiteTypeEnum, Set<String>> compatiblePlacements) throws IOException {
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
            // Only one BEL per site...
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

} // end class
