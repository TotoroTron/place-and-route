package placer;

import java.util.List;
import com.xilinx.rapidwright.edif.EDIFHierCellInst;

public record CarryCellGroup(
        EDIFHierCellInst carry,
        List<EDIFHierCellInst> luts,
        List<EDIFHierCellInst> ffs) {
}
