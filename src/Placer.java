package src;

import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;

import com.xilinx.rapidwright.design.Design;
import com.xilinx.rapidwright.design.Net;
import com.xilinx.rapidwright.design.Module;
import com.xilinx.rapidwright.design.ModuleInst;
import com.xilinx.rapidwright.design.ModuleImpls;
import com.xilinx.rapidwright.design.Cell;

import com.xilinx.rapidwright.edif.EDIFTools;
import com.xilinx.rapidwright.edif.EDIFLibrary;
import com.xilinx.rapidwright.edif.EDIFNetlist;
import com.xilinx.rapidwright.edif.EDIFNet;
import com.xilinx.rapidwright.edif.EDIFHierNet;
import com.xilinx.rapidwright.edif.EDIFCell;
import com.xilinx.rapidwright.edif.EDIFCellInst;
import com.xilinx.rapidwright.edif.EDIFHierCellInst;
import com.xilinx.rapidwright.edif.EDIFPort;
import com.xilinx.rapidwright.edif.EDIFPortInst;


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

    private void printEDIFPortInsts(BufferedWriter writer, Collection<EDIFPortInst> epis) throws IOException {
        for (EDIFPortInst epi : epis) {
            String s = String.format(
                "\n\t\tNAME: %-20s FULL NAME: %-30s INDEX: %d", 
                epi.getName(), epi.getFullName(), epi.getIndex());
            writer.write(s);
        }
    }

    
    
    public void printDesignInfo(BufferedWriter writer, Design design) throws IOException {

        // Top level object for a logical EDIF netlist.
        EDIFNetlist netlist = design.getNetlist();

        // Graph G = (cells, nets)

        writer.write("Printing EDIFCell(s) in EDIFNetlist: ");
        HashMap<String, EDIFCellInst> ecis = netlist.generateCellInstMap();
        for (EDIFCellInst eci : ecis.values()) {
            writer.write("\n\t"+eci.getCellName());
            writer.write("\n\tEDIFPortInst(s): ");
            Collection<EDIFPortInst> epis = eci.getPortInsts();
            printEDIFPortInsts(writer, epis);
        }

        // Map.keySet() vs Map.values() vs Map.entrySet()

        writer.write("\n\nPrinting EDIFHierNet(s): ");
        Map<EDIFHierNet, EDIFHierNet> ehns = netlist.getParentNetMap();
        for (Map.Entry<EDIFHierNet, EDIFHierNet> entry : ehns.entrySet()) {
            EDIFHierNet key = entry.getKey();
            EDIFHierNet value = entry.getValue();
            String s = String.format(
                "\n\t %-50s  =>\t\t %-50s",
                key.getHierarchicalNetName(), value.getHierarchicalNetName()
            );
            writer.write(s);
        }


        writer.write("\n\nPrinting EDIFNet(s): ");
        HashMap<String, EDIFNet> nets = netlist.generateEDIFNetMap(ecis);
        writer.write("Net map contains "+nets.size()+" nets.");
        for (int i = 0; i < nets.size(); i++) {
            EDIFNet net = nets.get(i);
            writer.write("\n\tNet #"+i+": ");
            writer.write("\n\tEDIFPortInst(s): ");
            if (net == null) {
                writer.write("\n\t\tNet is null!");
                continue;
            }
            Collection<EDIFPortInst> epis = net.getPortInsts();
            printEDIFPortInsts(writer, epis);
        }


        // Keeps track of a set of EDIFCell objects that are part of a netlist.
        EDIFLibrary library = netlist.getHDIPrimitivesLibrary();
    }

    protected abstract Design place(Design design); 

}

    // https://www.rapidwright.io/javadoc/overview-tree.html
    // https://www.rapidwright.io/javadoc/com/xilinx/rapidwright/design/Design.html
    // https://www.rapidwright.io/javadoc/com/xilinx/rapidwright/device/Device.html
    // https://docs.amd.com/r/en-US/ug912-vivado-properties/CELL

    /*
     * 
     * List<EDIFHierCellInst> ehcis = netlist.getAllLeafHierCellInstances();
     * for (EDIFHierCellInst ehci : ehcis) {
     *     writer.write("\nCell Name: "+ehci.getCellName());
     *     writer.write("\n\tDepth: "+ehci.getDepth());
     *     writer.write("\n\tHierarchical Inst Name:"+ehci.getFullHierarchicalInstName());
     * }
     *
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
     * https://www.rapidwright.io/javadoc/com/xilinx/rapidwright/design/Unisim.html
     * https://www.rapidwright.io/javadoc/com/xilinx/rapidwright/edif/EDIFCell.html
     * https://www.rapidwright.io/javadoc/com/xilinx/rapidwright/edif/EDIFNetlist.html
     * https://www.rapidwright.io/javadoc/com/xilinx/rapidwright/edif/EDIFHierCellInst.html
     * https://www.rapidwright.io/javadoc/com/xilinx/rapidwright/edif/EDIFHierNet.html
     * https://www.rapidwright.io/javadoc/com/xilinx/rapidwright/edif/EDIFHierPortInst.html
     * 
    */
