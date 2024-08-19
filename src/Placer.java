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

import com.xilinx.rapidwright.edif.EDIFNetlist;
import com.xilinx.rapidwright.edif.EDIFCellInst;

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
        //

        EDIFNetlist netlist = design.getNetlist();
        EDIFLibrary library = netlist.getHDIPrimitivesLibrary();
        
        List<EDIFCellInst> edifCellInsts = netlist.getAllLeafCellInstances();
        List<EDIFHierCellInst> edifHeirCellInsts = netlist.getAllLeafHeirCellInstances();

    }


    protected abstract Design place(Design design); 

}
