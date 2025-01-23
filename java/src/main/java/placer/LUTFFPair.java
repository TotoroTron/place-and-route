
package placer;

import com.xilinx.rapidwright.edif.EDIFHierCellInst;

public record LUTFFPair(
        EDIFHierCellInst lut, EDIFHierCellInst ff) {

}
