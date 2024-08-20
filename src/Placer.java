package src;

import java.util.List;
import java.util.ArrayList;
import java.util.Collection;

import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;

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

    protected Device device;
    protected Design design;

    private final String rootDir = "/home/bcheng/workspace/dev/place-and-route/";
    private final String synthesizedDcp = rootDir+"/outputs/synthesized.dcp";
    private final String placedDcp = rootDir+"/outputs/placed.dcp";

    public Placer() {
        this.design = Design.readCheckpoint(synthesizedDcp);
        this.device = Device.getDevice("xc7z020clg400-1");
    }

    public void run(BufferedWriter writer) throws IOException {
        printDesignInfo(writer, design);
        design = place(design); 
        design.writeCheckpoint(placedDcp);
    }


    public void printDesignInfo(BufferedWriter writer, Design design) throws IOException {
        // https://www.rapidwright.io/javadoc/overview-tree.html
        // https://docs.amd.com/r/en-US/ug912-vivado-properties/CELL

        // Top level object for a logical EDIF netlist.
        EDIFNetlist netlist = design.getNetlist();

        // Keeps track of a set of EDIFCell objects that are part of a netlist.
        EDIFLibrary library = netlist.getHDIPrimitivesLibrary();

        /*
         *
         * These ideas are equivalent:
         *      edif.EDIFCell   => design.Cell 
         *      Logical Cell    => Physical Cell 
         *      Post-Synth Cell => Post-Place Cell
         * 
         * EDIFCell: Represents a logical cell in an EDIF netlist.
         * Cell: Corresponds to the leaf cell within the logical netlist EDIFCellInst and
         *      provides a mapping to a physical location BEL on the device.
         *      It could also be called a BELInst.
         * 
         * Synthesis provides a raw EDIF netlist with EDIFCellInst(s).
         * Placement maps these logical EDIFCellInst(s) onto physical BELs / Tiles / Sites.
         *      Cells are created during placement via design.createAndPlaceCell(...).
         *      Example : Cell or2 = design.createAndPlaceCell("or2", Unisim.OR2, "SLICE_X112Y140/C6LUT");
         *      Example : Cell led = design.createAndPlaceIOB("led", PinType.OUT, "R14", "LVCMOS33")
         * 
         * https://www.rapidwright.io/javadoc/com/xilinx/rapidwright/design/Cell.html
         * https://www.rapidwright.io/javadoc/com/xilinx/rapidwright/edif/EDIFCell.html
         *
        */

        EDIFCell topCell = netlist.getTopCell();
        writer.write("\nTop Cell: "+topCell.getView());
         
        List<EDIFCellInst> edifCellInsts = netlist.getAllLeafCellInstances();
        writer.write("\n\nCell Instances: ");
        for (EDIFCellInst eci : edifCellInsts) {
            writer.write("\n"+eci.getCellName());
        }

        List<EDIFHierCellInst> edifHeirCellInsts = netlist.getAllLeafHierCellInstances();
        writer.write("\n\nCell Instances with Hierarchy: ");
        for (EDIFHierCellInst ehci : edifHeirCellInsts) {
            writer.write("\n"+ehci.getFullHierarchicalInstName());
        }

    }


    protected abstract Design place(Design design); 

}
