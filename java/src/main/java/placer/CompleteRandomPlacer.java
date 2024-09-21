package placer;

import java.util.Map;
import java.util.Collection;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;

import java.io.IOException;

import com.xilinx.rapidwright.design.Design;
import com.xilinx.rapidwright.design.Cell;

import com.xilinx.rapidwright.edif.EDIFNetlist;
import com.xilinx.rapidwright.edif.EDIFCell;
import com.xilinx.rapidwright.edif.EDIFCellInst;
import com.xilinx.rapidwright.edif.EDIFHierCellInst;

import com.xilinx.rapidwright.edif.EDIFPortInst;
import com.xilinx.rapidwright.edif.EDIFHierPortInst;

import com.xilinx.rapidwright.edif.EDIFHierNet;

public class CompleteRandomPlacer extends Placer {

    public CompleteRandomPlacer() throws IOException {
        super();
    }

    public Design place(Design design) {

        EDIFNetlist netlist = design.getNetlist();

        List<Cell> cells = new ArrayList<>();

        HashMap<String, EDIFCellInst> ecis = netlist.generateCellInstMap();
        for (Map.Entry<String, EDIFCellInst> entry : ecis.entrySet()) {
            String key = entry.getKey();
            EDIFCellInst val = entry.getValue();
            cells.add(design.createCell(key, val));
            // createCell(String instName, EDIFCellInst instance);
            // design.placeCell(Cell c, Site site, BEL bel);
            // figure out valid sites and bells for each cell
        }

        Map<EDIFHierNet, EDIFHierNet> ehns = netlist.getParentNetMap();
        for (EDIFHierNet ehn : ehns.values()) {
            Collection<EDIFHierPortInst> ehpis = ehn.getPortInsts();
        }
        return design;
    }

}
