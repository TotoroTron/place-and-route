package placer;

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

public class PlacerGreedyRandom extends PlacerAnnealRandom {

    private String placerName = "PlacerGreedyRandom";

    public PlacerGreedyRandom(String rootDir, Design design, Device device, ClockRegion region) throws IOException {
        super(rootDir, design, device, region);
    }

    @Override
    public String getPlacerName() {
        return this.placerName;
    }

    @Override
    public void initCoolingSchedule(double initialTemp, double alpha, int movesLimit) throws IOException {
        this.movesLimit = movesLimit;
        this.coolingSchedule = new ArrayList<>();
        for (int i = 0; i < movesLimit; i++) {
            this.coolingSchedule.add(0.0d);
        }
    }
}
