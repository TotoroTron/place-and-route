
package placer;

import java.util.List;
import java.util.Map;

import com.xilinx.rapidwright.design.SiteInst;

public class PackedDesign {
    List<SiteInst> BUFGCTRLSiteInsts;
    List<List<SiteInst>> DSPSiteInstCascades;
    List<List<SiteInst>> CARRYSiteInstChains;
    List<SiteInst> CLBSiteInsts;
    List<SiteInst> RAMSiteSiteInsts;

    PackedDesign(
            List<SiteInst> BUFGCTRLSiteInsts,
            List<List<SiteInst>> CARRYSiteInstChains,
            List<List<SiteInst>> DSPSiteInstCascades,
            List<SiteInst> CLBSiteInsts,
            List<SiteInst> RAMSiteInsts) {
        this.BUFGCTRLSiteInsts = BUFGCTRLSiteInsts;
        this.CARRYSiteInstChains = CARRYSiteInstChains;
        this.DSPSiteInstCascades = DSPSiteInstCascades;
        this.CLBSiteInsts = CLBSiteInsts;
        this.RAMSiteSiteInsts = RAMSiteInsts;
    };

}
