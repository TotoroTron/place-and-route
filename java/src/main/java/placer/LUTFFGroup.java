package placer;

import java.util.List;
import com.xilinx.rapidwright.edif.EDIFHierCellInst;

public record LUTFFGroup(
        List<Pair<EDIFHierCellInst, EDIFHierCellInst>> group) {

}
