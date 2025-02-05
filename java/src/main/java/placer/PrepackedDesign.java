package placer;

import java.util.List;
import java.util.Map;

import com.xilinx.rapidwright.edif.EDIFHierCellInst;

// This is essentially the EDIF representation of Relatively Placed Macros (RPMs)
// RPMs: a list of basic logical elements (BELs) grouped into sets (RPMs)
// https://docs.amd.com/r/en-US/ug903-vivado-using-constraints/Defining-Relatively-Placed-Macros 
public class PrepackedDesign {
    List<EDIFHierCellInst> BUFGCTRLCells;
    List<List<EDIFHierCellInst>> DSPCascades;
    List<List<CarryCellGroup>> CARRYChains;
    Map<Pair<String, String>, LUTFFGroup> LUTFFGroups;
    List<List<EDIFHierCellInst>> LUTGroups;
    List<EDIFHierCellInst> RAMCells;

    PrepackedDesign(
            List<EDIFHierCellInst> BUFGCTRLCells,
            List<List<EDIFHierCellInst>> DSPCascades,
            List<EDIFHierCellInst> RAMCells,
            List<List<CarryCellGroup>> CARRYChains,
            Map<Pair<String, String>, LUTFFGroup> LUTFFGroups,
            List<List<EDIFHierCellInst>> LUTGroups) {
        this.BUFGCTRLCells = BUFGCTRLCells;
        this.DSPCascades = DSPCascades;
        this.RAMCells = RAMCells;
        this.CARRYChains = CARRYChains;
        this.LUTFFGroups = LUTFFGroups;
        this.LUTGroups = LUTGroups;
    };

}
