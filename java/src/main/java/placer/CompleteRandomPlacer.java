package placer;

import java.util.Map;
// import java.util.Collection;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Set;

import java.io.IOException;

import com.xilinx.rapidwright.edif.EDIFNetlist;
import com.xilinx.rapidwright.edif.EDIFCell;
import com.xilinx.rapidwright.edif.EDIFCellInst;
import com.xilinx.rapidwright.edif.EDIFHierCellInst;
import com.xilinx.rapidwright.edif.EDIFPortInst;
import com.xilinx.rapidwright.edif.EDIFHierPortInst;
import com.xilinx.rapidwright.edif.EDIFNet;
import com.xilinx.rapidwright.edif.EDIFHierNet;

import com.xilinx.rapidwright.design.Design;
import com.xilinx.rapidwright.design.Cell;
import com.xilinx.rapidwright.design.Net;

import com.xilinx.rapidwright.device.SiteTypeEnum;
import com.xilinx.rapidwright.device.Site;
import com.xilinx.rapidwright.device.BEL;

public class CompleteRandomPlacer extends Placer {

    public CompleteRandomPlacer() throws IOException {
        super();
    }

    public Design place(Design design) {

        // Logical to Physical mapping

        EDIFNetlist netlist = design.getNetlist();

        List<Cell> cells = new ArrayList<>();
        List<Net> nets = new ArrayList<>();

        List<EDIFHierCellInst> cellInstList = netlist.getAllLeafHierCellInstances();
        for (EDIFHierCellInst ehci : cellInstList) {
            cells.add(design.createCell(ehci.getFullHierarchicalInstName(), ehci.getInst()));
        }

        Map<EDIFHierNet, EDIFHierNet> edifNetMap = netlist.getParentNetMap();
        for (Map.Entry<EDIFHierNet, EDIFHierNet> entry : edifNetMap.entrySet()) {
            EDIFHierNet key = entry.getKey(); // Net Name
            EDIFHierNet val = entry.getValue(); // Net Parent
            nets.add(design.createNet(key));
            // If Name = Parent, then it means the net source comes from a primitive cell or
            // an I/O pad
            // If Name != Parent, then the net source comes from non-primitive hierarchical
            // cell
        }

        // Find compatible sites and BELs for each cell.
        for (Cell cell : cells) {
            Map<SiteTypeEnum, Set<String>> siteBELMap = cell.getCompatiblePlacements(this.device);
            // need to print this map out
            Site site;
            BEL bel;
            if (design.placeCell(cell, site, bel) == false)
                // https://www.rapidwright.io/javadoc/com/xilinx/rapidwright/device/Site.html
                System.out.println(cell.getName() + "placement failed!");
        }

        // Net n = createNet(EDIFHierNet ehn);
        // Net n = createNet(String netName);
        // SitePinInst spi = net.connect(Cell c, String logicalPinName);
        // get the pins for each cell.

        return design;
    }

}

/*
 * // Create Cells from EDIFCellInsts
 * HashMap<String, EDIFCellInst> cellInstMap = netlist.generateCellInstMap();
 * for (Map.Entry<String, EDIFCellInst> entry : cellInstMap.entrySet()) {
 * String key = entry.getKey();
 * EDIFCellInst val = entry.getValue();
 * cells.add(design.createCell(key, val));
 * }
 *
 * // Cell c = createCell(String instName, EDIFCellInst instance);
 * // boolean b = design.placeCell(Cell c, Site site, BEL bel);
 * // returns true if placement successful or if already placed
 * 
 * HashMap<String, EDIFNet> edifNetMap =
 * netlist.generateEDIFNetMap(cellInstMap);
 * for (Map.Entry<String, EDIFNet> entry : edifNetMap.entrySet()) {
 * String key = entry.getKey();
 * EDIFNet val = entry.getValue();
 * nets.add(design.createNet(val)); // createNet only accepts EDIFHierNet...
 * }
 */
