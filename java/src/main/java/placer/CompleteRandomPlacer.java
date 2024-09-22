package placer;

import java.util.Map;
// import java.util.Collection;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;

import java.io.IOException;

import com.xilinx.rapidwright.design.Design;
import com.xilinx.rapidwright.design.Cell;

import com.xilinx.rapidwright.edif.EDIFNetlist;
// import com.xilinx.rapidwright.edif.EDIFCell;
import com.xilinx.rapidwright.edif.EDIFCellInst;
// import com.xilinx.rapidwright.edif.EDIFHierCellInst;

// import com.xilinx.rapidwright.edif.EDIFPortInst;
// import com.xilinx.rapidwright.edif.EDIFHierPortInst;

import com.xilinx.rapidwright.design.Net;
// import com.xilinx.rapidwright.edif.EDIFHierNet;

public class CompleteRandomPlacer extends Placer {

    public CompleteRandomPlacer() throws IOException {
        super();
    }

    public Design place(Design design) {

        // Logical to Physical mapping

        EDIFNetlist netlist = design.getNetlist();

        List<Cell> cells = new ArrayList<>();
        List<Net> nets = new ArrayList<>();

        HashMap<String, EDIFCellInst> ecis = netlist.generateCellInstMap();
        for (Map.Entry<String, EDIFCellInst> entry : ecis.entrySet()) {
            String key = entry.getKey();
            EDIFCellInst val = entry.getValue();
            cells.add(design.createCell(key, val));
            // Cell c = createCell(String instName, EDIFCellInst instance);
            // boolean b = design.placeCell(Cell c, Site site, BEL bel);
            // returns true if placement successful or if already placed
        }

        // Net n = createNet(EDIFHierNet ehn);
        // Net n = createNet(String netName);
        // SitePinInst spi = net.connect(Cell c, String logicalPinName);
        // get the pins for each cell.

        return design;
    }

}
