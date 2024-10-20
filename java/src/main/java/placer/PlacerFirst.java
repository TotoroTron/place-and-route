package placer;

import java.util.Set;
import java.util.List;
import java.util.Map;
import java.util.Iterator;

import java.io.FileWriter;
import java.io.IOException;

import com.xilinx.rapidwright.device.SiteTypeEnum;

public class PlacerFirst extends Placer {
    FileWriter writer;

    public PlacerFirst() throws IOException {
        super();
        placerName = "PlacerFirst";
        writer = new FileWriter(rootDir + "outputs/printout/" + placerName + ".txt");
    }

    protected SiteTypeEnum selectSiteType(Map<SiteTypeEnum, Set<String>> compatiblePlacements) throws IOException {
        Iterator<SiteTypeEnum> iterator = compatiblePlacements.keySet().iterator();
        SiteTypeEnum selectedSiteType = iterator.next();
        if (device.getAllSitesOfType(selectedSiteType).length == 0) {
            return null;
        }
        return selectedSiteType;
    }

    protected String[] selectSiteAndBEL(
            Map<String, List<String>> availablePlacements,
            Map<String, List<String>> occupiedPlacements) throws IOException {

        // Select first site and in first site, first BEL
        String selectedSiteName = availablePlacements.keySet().iterator().next();
        String selectedBELName = availablePlacements.get(selectedSiteName).get(0);

        return new String[] { selectedSiteName, selectedBELName };
    }

} // end class
