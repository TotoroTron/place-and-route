
package placer;

import java.io.FileWriter;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

import com.xilinx.rapidwright.design.Design;
import com.xilinx.rapidwright.design.SiteInst;
import com.xilinx.rapidwright.design.SitePinInst;

import com.xilinx.rapidwright.device.Device;
import com.xilinx.rapidwright.device.Site;
import com.xilinx.rapidwright.device.SiteTypeEnum;
import com.xilinx.rapidwright.device.ClockRegion;

public class PlacerAnalytical extends Placer {

    private String placerName = "PlacerAnalytical";

    public PlacerAnalytical(String rootDir, Design design, Device device, ClockRegion region) throws IOException {
        super(rootDir, design, device);
        this.regionConstraint = region;
    }

    public String getPlacerName() {
        return this.placerName;
    }

    public void placeDesign(PackedDesign packedDesign) throws IOException {
        initSites();
        initRpmGrid();
        unplaceAllSiteInsts(packedDesign);
        randomInitialPlacement(packedDesign);
        ImageMaker imInitRandom = new ImageMaker(design);
        imInitRandom.renderAll();
        imInitRandom.exportImage(graphicsDir + "/random_placement.png");
        design.writeCheckpoint(rootDir + "/outputs/checkpoints/init_placed.dcp");

        int move = 0;
        while (move < 100) {
            move++;
            //
            //
            //
            //
        }

        ImageMaker imPlaced = new ImageMaker(design);
        imPlaced.renderAll();
        imPlaced.exportImage(graphicsDir + "/final_placement.png");
        exportCostHistory(printoutDir + "/cost_history.csv");
        printTimingBenchmarks();
        writer.write("\n\nTotal move iterations: " + move);
    }
}
