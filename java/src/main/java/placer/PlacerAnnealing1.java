package placer;

import java.util.stream.Collectors;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import java.io.IOException;

import com.xilinx.rapidwright.design.Design;
import com.xilinx.rapidwright.design.SiteInst;
import com.xilinx.rapidwright.design.Net;

import com.xilinx.rapidwright.device.Device;
import com.xilinx.rapidwright.device.Tile;
import com.xilinx.rapidwright.device.Site;
import com.xilinx.rapidwright.device.SiteTypeEnum;

public class PlacerAnnealing1 extends Placer {
    public PlacerAnnealing1(String rootDir, Design design, Device device) throws IOException {
        super(rootDir, design, device);
    }

    public void placeDesign(PackedDesign packedDesign) throws IOException {
        List<List<SiteInst>> CARRYSiteInstChains = packedDesign.CARRYSiteInstChains;
        List<List<SiteInst>> DSPSiteInstCascades = packedDesign.DSPSiteInstCascades;
        List<SiteInst> CLBSiteInsts = packedDesign.CLBSiteInsts;
        List<SiteInst> RAMSiteInsts = packedDesign.RAMSiteInsts;
        double lowestCost = evaluateCost(); // initial cost
        int staleAttempts = 0;
        while (true) {
            double currCost = evaluateCost();

            if (staleAttempts > 100)
                break;
        }
    }
}
