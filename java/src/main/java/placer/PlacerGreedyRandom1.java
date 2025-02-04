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

public class PlacerGreedyRandom1 extends Placer {
    public PlacerGreedyRandom1(String rootDir, Design design, Device device) throws IOException {
        super(rootDir, design, device);
    }

    public void placeDesign(PackedDesign packedDesign) throws IOException {
        PackedDesign optimalDesign;
        double lowestCost = evaluateCost(); // initial cost
        int staleMoves = 0;
        while (true) {
            unplaceDesign(packedDesign);
            placeDesign(packedDesign);
            double currCost = evaluateCost();
            if (currCost < lowestCost) {
                staleMoves = 0;
                // how do i save an immutable "image" of the optimalDesign?
                optimalDesign = new PackedDesign(packedDesign);
            }
            staleMoves++;
            if (staleMoves > 100)
                break;
        }
    }

    private void unplaceDesign(PackedDesign packedDesign) {
        for (List<SiteInst> chain : packedDesign.CARRYSiteInstChains)
            for (SiteInst si : chain)
                si.unPlace();
        for (List<SiteInst> cascade : packedDesign.DSPSiteInstCascades)
            for (SiteInst si : cascade)
                si.unPlace();
        for (SiteInst si : packedDesign.CLBSiteInsts)
            si.unPlace();
        for (SiteInst si : packedDesign.RAMSiteInsts)
            si.unPlace();
    }

    private void randomMove(PackedDesign packedDesign) {

    }
}
