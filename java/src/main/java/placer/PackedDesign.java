
package placer;

import java.util.List;

import com.xilinx.rapidwright.device.Site;
import com.xilinx.rapidwright.device.ClockRegion;
import com.xilinx.rapidwright.design.SiteInst;

public class PackedDesign {
    List<SiteInst> BUFGCTRLSiteInsts;
    List<List<SiteInst>> CARRYSiteInstChains;
    List<List<SiteInst>> DSPSiteInstCascades;
    List<SiteInst> CLBSiteInsts;
    List<SiteInst> RAMSiteInsts;

    List<Site> availableSites;
    List<Site> occupiedSites;

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
        this.RAMSiteInsts = RAMSiteInsts;
    };

    PackedDesign(PackedDesign packedDesign) {
        this.BUFGCTRLSiteInsts = packedDesign.BUFGCTRLSiteInsts;
        this.CARRYSiteInstChains = packedDesign.CARRYSiteInstChains;
        this.DSPSiteInstCascades = packedDesign.DSPSiteInstCascades;
        this.CLBSiteInsts = packedDesign.CLBSiteInsts;
        this.RAMSiteInsts = packedDesign.RAMSiteInsts;
    }

}
