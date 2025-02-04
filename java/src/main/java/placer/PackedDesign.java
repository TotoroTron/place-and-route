
package placer;

import java.util.List;
import java.util.Map;

import com.xilinx.rapidwright.design.SiteInst;

public class PackedDesign {
    List<List<SiteInst>> DSPCascades;
    List<List<SiteInst>> CARRYChains;
    List<SiteInst> LUTFFSites;
    List<SiteInst> RAMSites;
    List<SiteInst> BUFGCTRLSites;

    PackedDesign(
            List<List<SiteInst>> DSPCascades,
            List<List<SiteInst>> CARRYChains,
            List<SiteInst> LUTFFSites,
            List<SiteInst> RAMSites,
            List<SiteInst> BUFGCTRLSites) {
        this.DSPCascades = DSPCascades;
        this.CARRYChains = CARRYChains;
        this.LUTFFSites = LUTFFSites;
        this.RAMSites = RAMSites;
        this.BUFGCTRLSites = BUFGCTRLSites;
    };

}
