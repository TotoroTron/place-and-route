package src;

import java.util.List;
import java.util.ArrayList;
import java.util.Collection;

import com.xilinx.rapidwright.design.Design;
import com.xilinx.rapidwright.device.Device;

import com.xilinx.rapidwright.device.Site;
import com.xilinx.rapidwright.device.SiteTypeEnum;

import com.xilinx.rapidwright.design.Design;
import com.xilinx.rapidwright.design.Net;
import com.xilinx.rapidwright.design.Module;
import com.xilinx.rapidwright.design.ModuleInst;
import com.xilinx.rapidwright.design.ModuleImpls;
import com.xilinx.rapidwright.design.Cell;


import com.xilinx.rapidwright.edif.EDIFLibrary;
import com.xilinx.rapidwright.edif.EDIFNetlist;
import com.xilinx.rapidwright.edif.EDIFCell;
import com.xilinx.rapidwright.edif.EDIFCellInst;
import com.xilinx.rapidwright.edif.EDIFHierCellInst;

public class AnalyticalPlacer extends Placer {

    public AnalyticalPlacer() {
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
