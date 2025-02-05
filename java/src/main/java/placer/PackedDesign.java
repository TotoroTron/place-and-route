
package placer;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;

import com.xilinx.rapidwright.device.Site;
import com.xilinx.rapidwright.device.SiteTypeEnum;
import com.xilinx.rapidwright.design.SiteInst;

public class PackedDesign {
    List<SiteInst> BUFGCTRLSiteInsts;
    List<List<SiteInst>> CARRYSiteInstChains;
    List<List<SiteInst>> DSPSiteInstCascades;
    List<SiteInst> CLBSiteInsts;
    List<SiteInst> RAMSiteInsts;

    Map<SiteTypeEnum, List<Site>> availableSites;
    Map<SiteTypeEnum, List<Site>> occupiedSites;

    PackedDesign(
            List<SiteInst> BUFGCTRLSiteInsts,
            List<List<SiteInst>> CARRYSiteInstChains,
            List<List<SiteInst>> DSPSiteInstCascades,
            List<SiteInst> CLBSiteInsts,
            List<SiteInst> RAMSiteInsts,
            Map<SiteTypeEnum, List<Site>> availableSites,
            Map<SiteTypeEnum, List<Site>> occupiedSites) {
        this.BUFGCTRLSiteInsts = BUFGCTRLSiteInsts;
        this.CARRYSiteInstChains = CARRYSiteInstChains;
        this.DSPSiteInstCascades = DSPSiteInstCascades;
        this.CLBSiteInsts = CLBSiteInsts;
        this.RAMSiteInsts = RAMSiteInsts;
        this.availableSites = availableSites;
        this.occupiedSites = occupiedSites;
    };

    PackedDesign(PackedDesign packedDesign) {
        this.BUFGCTRLSiteInsts = packedDesign.BUFGCTRLSiteInsts;
        this.CARRYSiteInstChains = packedDesign.CARRYSiteInstChains;
        this.DSPSiteInstCascades = packedDesign.DSPSiteInstCascades;
        this.CLBSiteInsts = packedDesign.CLBSiteInsts;
        this.RAMSiteInsts = packedDesign.RAMSiteInsts;
        this.availableSites = packedDesign.availableSites;
        this.occupiedSites = packedDesign.occupiedSites;
    }

}
