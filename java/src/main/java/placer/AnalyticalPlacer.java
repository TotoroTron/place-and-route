package placer;

import java.util.List;

import java.io.IOException;

import com.xilinx.rapidwright.design.Design;

import com.xilinx.rapidwright.edif.EDIFLibrary;
import com.xilinx.rapidwright.edif.EDIFNetlist;
import com.xilinx.rapidwright.edif.EDIFCellInst;
import com.xilinx.rapidwright.edif.EDIFHierCellInst;

public class AnalyticalPlacer extends Placer {

    public AnalyticalPlacer() throws IOException {
        super();
    }

    public Design place(Design design) {
        // Site[] compatibleSites = device.getAllCompatibleSites();

        // Top level object for a logical EDIF netlist.
        EDIFNetlist netlist = design.getNetlist();

        // Keeps track of a set of EDIFCell objects that are part of a netlist.
        EDIFLibrary library = netlist.getHDIPrimitivesLibrary();

        List<EDIFCellInst> edifCellInsts = netlist.getAllLeafCellInstances();
        for (EDIFCellInst eci : edifCellInsts) {
        }

        List<EDIFHierCellInst> edifHeirCellInsts = netlist.getAllLeafHierCellInstances();
        for (EDIFHierCellInst ehci : edifHeirCellInsts) {
        }

        return design;
    }

}