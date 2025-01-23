
package placer;

import com.xilinx.rapidwright.edif.EDIFHierCellInst;

public record DSPPair(
        EDIFHierCellInst anchor, EDIFHierCellInst downstream) {

}
