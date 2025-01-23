package placer;

import java.util.List;
import java.util.Map;

import com.xilinx.rapidwright.edif.EDIFHierCellInst;

public class PackedDesign {
    List<Pair<EDIFHierCellInst, EDIFHierCellInst>> DSPPairs;
    List<List<CarryCellGroup>> CARRYChains;
    Map<Pair<String, String>, LUTFFGroup> LUTFFGroups;
    List<List<EDIFHierCellInst>> LUTGroups;

    PackedDesign(
            List<Pair<EDIFHierCellInst, EDIFHierCellInst>> DSPPairs,
            List<List<CarryCellGroup>> CARRYChains,
            Map<Pair<String, String>, LUTFFGroup> LUTFFGroups,
            List<List<EDIFHierCellInst>> LUTGroups) {
        this.DSPPairs = DSPPairs;
        this.CARRYChains = CARRYChains;
        this.LUTFFGroups = LUTFFGroups;
        this.LUTGroups = LUTGroups;
    };

}
