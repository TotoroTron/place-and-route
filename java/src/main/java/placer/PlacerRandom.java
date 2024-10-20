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
    FileWriter writer;
    protected String placerName;

    public PlacerRandom() throws IOException {
        super();
        placerName = "PlacerRandom";
        writer = new FileWriter(rootDir + "outputs/printout/" + placerName + ".txt");
    }

    protected SiteTypeEnum selectSiteType(Map<SiteTypeEnum, Set<String>> compatiblePlacements) throws IOException {
        Iterator<SiteTypeEnum> iterator = compatiblePlacements.keySet().iterator();
        SiteTypeEnum selectedSiteType = iterator.next();
        if (device.getAllSitesOfType(selectedSiteType).length == 0) {
            System.out.println("NULL! " + selectedSiteType);
            return null;
        }
        return selectedSiteType;
    }

    protected String[] selectSiteAndBEL(
            Map<String, List<String>> availablePlacements,
            Map<String, List<String>> occupiedPlacements) throws IOException {

        Random random = new Random();
        List<String> availableSiteNames = new ArrayList<>(availablePlacements.keySet());
        String selectedSiteName = availableSiteNames.get(random.nextInt(availableSiteNames.size()));

        List<String> availableBELNames = availablePlacements.get(selectedSiteName);
        String selectedBELName = availableBELNames.get(random.nextInt(availableBELNames.size()));

        return new String[] { selectedSiteName, selectedBELName };
    }
} // end class
