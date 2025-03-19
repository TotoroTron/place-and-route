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

public class PlacerAnnealHybrid extends PlacerAnnealMidpoint {
    private String placerName = "PlacerAnnealHybrid";

    public PlacerAnnealHybrid(String rootDir, Design design, Device device, ClockRegion region) throws IOException {
        super(rootDir, design, device, region);
    }

    @Override
    public String getPlacerName() {
        return this.placerName;
    }

    @Override
    protected Site proposeSite(SiteInst si, List<Site> connections, boolean swapEnable) {
        // for random intial placements
        if (connections == null || connections.isEmpty()) {
            return proposeRandomSite(si, connections, swapEnable);
        }
        // for movement during general placement
        // hybrid: 50% chance midpoint, 50% chance random
        if (this.currentTemp < 100.0d) {
            return proposeMidpointSite(si, connections, swapEnable);
        } else {
            return proposeRandomSite(si, connections, swapEnable);
        }
    }

    @Override
    protected Site proposeAnchorSite(List<SiteInst> chain, List<Site> connections, boolean swapEnable) {
        // for random intial placements
        if (connections == null || connections.isEmpty()) {
            return proposeRandomAnchorSite(chain, connections, swapEnable);
        }
        // for movement during general placement
        // hybrid: 50% chance midpoint, 50% chance random
        if (this.currentTemp < 100.0d) {
            return proposeMidpointAnchorSite(chain, connections, swapEnable);
        } else {
            return proposeRandomAnchorSite(chain, connections, swapEnable);
        }

    }
}
