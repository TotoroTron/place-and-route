import java.util.Map;
import java.util.Collection;
import java.util.List;

import java.io.IOException;

import com.xilinx.rapidwright.design.Design;
import com.xilinx.rapidwright.device.Device;

import com.xilinx.rapidwright.edif.EDIFNetlist;
import com.xilinx.rapidwright.edif.EDIFHierCellInst;
import com.xilinx.rapidwright.edif.EDIFHierPortInst;

import com.xilinx.rapidwright.edif.EDIFHierNet;

public class SimulatedAnnealingPlacer extends Placer {

    public SimulatedAnnealingPlacer() throws IOException {
        super();
    }

    public Design place(Design design) {

        EDIFNetlist netlist = design.getNetlist();

        List<EDIFHierCellInst> ehcis = netlist.getAllLeafHierCellInstances();
        for (EDIFHierCellInst ehci : ehcis) {
            List<EDIFHierPortInst> ehpis = ehci.getHierPortInsts();
        }

        Map<EDIFHierNet, EDIFHierNet> ehns = netlist.getParentNetMap();
        for (EDIFHierNet ehn : ehns.values()) {
            Collection<EDIFHierPortInst> ehpis = ehn.getPortInsts();
        }
        return design;
    }

}
