package placer;

import java.util.List;
import java.util.Map;

import com.xilinx.rapidwright.edif.EDIFHierCellInst;

public class PackedDesign {
    // List<Pair<EDIFHierCellInst, EDIFHierCellInst>> DSPPairs;
    List<List<EDIFHierCellInst>> DSPCascades;
    List<List<CarryCellGroup>> CARRYChains;
    Map<Pair<String, String>, LUTFFGroup> LUTFFGroups;
    List<List<EDIFHierCellInst>> LUTGroups;
    List<EDIFHierCellInst> RAMCells;
    List<EDIFHierCellInst> BUFGCTRLCells;

    PackedDesign(
            // List<Pair<EDIFHierCellInst, EDIFHierCellInst>> DSPPairs,
            List<List<EDIFHierCellInst>> DSPCascades,
            List<EDIFHierCellInst> RAMCells,
            List<List<CarryCellGroup>> CARRYChains,
            Map<Pair<String, String>, LUTFFGroup> LUTFFGroups,
            List<List<EDIFHierCellInst>> LUTGroups,
            List<EDIFHierCellInst> BUFGCTRLCells) {
        // this.DSPPairs = DSPPairs;
        this.DSPCascades = DSPCascades;
        this.RAMCells = RAMCells;
        this.CARRYChains = CARRYChains;
        this.LUTFFGroups = LUTFFGroups;
        this.LUTGroups = LUTGroups;
        this.BUFGCTRLCells = BUFGCTRLCells;
    };

}
