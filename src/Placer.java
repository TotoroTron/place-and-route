package src;

import java.util.List;
import java.util.ArrayList;
import java.util.Collection;

import java.io.IOException;
import java.io.BufferedWriter;

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

import com.xilinx.rapidwright.device.Device;


public abstract class Placer {

    protected static Device device;
    protected static Design design;

    protected static final String rootDir = "/home/bcheng/workspace/dev/place-and-route/";
    protected static final String synthesizedDcp = rootDir.concat("/outputs/synthesized.dcp");
    protected static final String placedDcp = rootDir.concat("/outputs/placed.dcp");


    public void init(BufferedWriter writer) throws IOException {
        Design design = Design.readCheckpoint(synthesizedDcp);
        printDesignInfo(writer, design);
        design = place(design); 
        design.writeCheckpoint(placedDcp);
    }


    public void printDesignInfo(BufferedWriter writer, Design design) throws IOException {
        // https://www.rapidwright.io/javadoc/overview-tree.html

        // Top level object for a logical EDIF netlist.
        EDIFNetlist netlist = design.getNetlist();

        // Keeps track of a set of EDIFCell objects that are part of a netlist.
        EDIFLibrary library = netlist.getHDIPrimitivesLibrary();



        // Logical cells in an EDIF netlist.
        // Can be both a leaf cell or a hierarchical cell.
        Collection<EDIFCell> cells = library.getCells();
        writer.write("\n\nEDIF Cells: ");
        for (EDIFCell cell : cells) {
            writer.write("\n".concat(cell.getView()));
        }



        EDIFCell topCell = netlist.getTopCell();
        writer.write("\nTop Cell: ".concat(topCell.getView()));
        
        List<EDIFCellInst> edifCellInsts = netlist.getAllLeafCellInstances();
        writer.write("\n\nCell Instances: ");
        for (EDIFCellInst eci : edifCellInsts) {
            writer.write("\n".concat(eci.getCellName()));
        }

        List<EDIFHierCellInst> edifHeirCellInsts = netlist.getAllLeafHierCellInstances();
        writer.write("\n\nCell Instances with Hierarchy: ");
        for (EDIFHierCellInst ehci : edifHeirCellInsts) {
            writer.write("\n".concat(ehci.getFullHierarchicalInstName()));
        }

    }


    protected abstract Design place(Design design); 

}
