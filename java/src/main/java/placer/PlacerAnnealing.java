package placer;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;

import com.xilinx.rapidwright.design.Design;
import com.xilinx.rapidwright.design.SiteInst;
import com.xilinx.rapidwright.design.Net;
import com.xilinx.rapidwright.design.Cell;
import com.xilinx.rapidwright.design.SitePinInst;

import com.xilinx.rapidwright.device.Device;
import com.xilinx.rapidwright.device.Site;
import com.xilinx.rapidwright.device.SiteTypeEnum;
import com.xilinx.rapidwright.device.BEL;
import com.xilinx.rapidwright.device.BELPin;
import com.xilinx.rapidwright.device.ClockRegion;

public class PlacerAnnealing extends Placer {
    public PlacerAnnealing(String rootDir, Design design, Device device) throws IOException {
        super(rootDir, design, device);
    }

    public void placeDesign(PackedDesign packedDesign) throws IOException {

        List<List<SiteInst>> CARRYSiteInstChains = packedDesign.CARRYSiteInstChains;
        List<List<SiteInst>> DSPSiteInstCascades = packedDesign.DSPSiteInstCascades;
        List<SiteInst> CLBSiteInsts = packedDesign.CLBSiteInsts;
        List<SiteInst> RAMSiteInsts = packedDesign.RAMSiteSiteInsts;

    }
}
