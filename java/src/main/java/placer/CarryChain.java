
package placer;

import java.util.List;
import com.xilinx.rapidwright.edif.EDIFHierCellInst;

public record CarryChain(
        List<CarryCellGroup> packs) {

}
