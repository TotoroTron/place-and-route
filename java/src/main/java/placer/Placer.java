package placer;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;

import com.xilinx.rapidwright.design.Design;
import com.xilinx.rapidwright.design.ModuleInst;
import com.xilinx.rapidwright.design.ModuleImpls;

import com.xilinx.rapidwright.edif.EDIFNetlist;
import com.xilinx.rapidwright.edif.EDIFHierNet;
import com.xilinx.rapidwright.edif.EDIFCellInst;
import com.xilinx.rapidwright.edif.EDIFHierCellInst;
import com.xilinx.rapidwright.edif.EDIFHierPortInst;

import com.xilinx.rapidwright.device.Device;

public abstract class Placer {

    protected Device device;
    protected Design design;

    private final String rootDir = "/home/bcheng/workspace/dev/place-and-route/";
    private final String synthesizedDcp = rootDir + "/outputs/synthesized.dcp";
    private final String placedDcp = rootDir + "/outputs/placed.dcp";

    public Placer() throws IOException {
        this.design = Design.readCheckpoint(synthesizedDcp);
        this.device = Device.getDevice("xc7z020clg400-1");
    }

    public void run() throws IOException {
        EDIFNetlist netlist = design.getNetlist();
        printEDIFCellInsts(netlist);
        printEDIFHierCellInsts(netlist);
        printEDIFHierNets(netlist);
        design = place(design);
        design.writeCheckpoint(placedDcp);
    }

    // Graph G = (ports, nets)
    // nets = Collection<Collection<EDIFHierPortInst>>

    public void printEDIFCellInsts(EDIFNetlist netlist) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(rootDir + "outputs/EDIFCellInsts.txt"));
        writer.write("Printing EDIFCellInst(s): ");
        writer.newLine();
        HashMap<String, EDIFCellInst> ecis = netlist.generateCellInstMap();
        for (Map.Entry<String, EDIFCellInst> entry : ecis.entrySet()) {
            String key = entry.getKey();
            EDIFCellInst eci = entry.getValue();
            String s1 = String.format(
                    "\nString Key: %-40s  =>\tEDIFCellInst Value: %-40s", key, eci.getCellName());
            writer.write(s1);
        }
        if (writer != null)
            writer.close();
    }

    private void printEDIFHierPortInsts(BufferedWriter writer, Collection<EDIFHierPortInst> ehpis) throws IOException {
        for (EDIFHierPortInst ehpi : ehpis) {
            writer.write("\n\t" + ehpi.toString());
            // writer.write("\n\t"+ehpi.getFullHierarchicalInstName());
            // writer.write("\n\t"+ehpi.getHierarchicalInstName());
        }
    }

    public void printEDIFHierCellInsts(EDIFNetlist netlist) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(rootDir + "outputs/EDIFHierCellInsts.txt"));
        writer.write("Printing EDIFHierCellInsts(s) in EDIFNetlist: ");
        writer.newLine();
        List<EDIFHierCellInst> ehcis = netlist.getAllLeafHierCellInstances();
        for (EDIFHierCellInst ehci : ehcis) {
            writer.write("\n" + ehci.getCellName() + " : " + ehci.getFullHierarchicalInstName());
            writer.write("\nEDIFHierPortInst(s) on this cell: ");
            List<EDIFHierPortInst> ehpis = ehci.getHierPortInsts();
            printEDIFHierPortInsts(writer, ehpis);
        }
        if (writer != null)
            writer.close();
    }

    public void printEDIFHierNets(EDIFNetlist netlist) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(rootDir + "outputs/EDIFHierNets.txt"));
        writer.write("Printing EDIFHierNet(s): ");
        Map<EDIFHierNet, EDIFHierNet> ehns = netlist.getParentNetMap();
        for (EDIFHierNet ehn : ehns.values()) {
            writer.write("\n" + ehn.getHierarchicalNetName());
            writer.write("\nEDIFHierPortInst(s) on this net: ");
            Collection<EDIFHierPortInst> ehpis = ehn.getPortInsts();
            printEDIFHierPortInsts(writer, ehpis);
        }
        if (writer != null)
            writer.close();
    }

    public void printModuleImpls(EDIFNetlist netlist) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(rootDir + "outputs/ModuleImpls.txt"));
        writer.write("Printing ModuleImpls: ");
        Collection<ModuleImpls> modimpls = design.getModules();
        for (ModuleImpls modimpl : modimpls) {
            writer.write("\n" + modimpl.getName());
        }
        if (writer != null)
            writer.close();
    }

    public void printModuleInsts(EDIFNetlist netlist) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(rootDir + "outputs/ModuleInsts.txt"));
        writer.write("Printing ModuleInsts: ");
        Collection<ModuleInst> modinsts = design.getModuleInsts();
        for (ModuleInst modinst : modinsts) {
            writer.write("\n" + String.valueOf(modinst.isPlaced()));
        }
    }

    protected abstract Design place(Design design);

}

/*
 * Map<EDIFHierNet, EDIFHierNet> ehns = netlist.getParentNetMap();
 * for (Map.Entry<EDIFHierNet, EDIFHierNet> entry : ehns.entrySet()) {
 * EDIFHierNet alias = entry.getKey();
 * EDIFHierNet net = entry.getValue();
 * String s1 = String.format(
 * "\n\tAlias: %-40s  =>\tCanonical: %-40s",
 * alias.getHierarchicalNetName(), net.getHierarchicalNetName()
 * );
 * writerNets.write(s1);
 */

/*
 * writerNets.write("Printing EDIFHierNet(s): ");
 * List<EDIFHierNet> ehns1 = netlist.getNetAliases();
 * for (EDIFHierNet ehn : ehns1) {
 * ehn = netlist.getParentNet(ehn);
 * writerNets.write("\n\tInst: "+ehn.getHierarchicalInstName()+"\t\tNet: "+ehn.
 * getHierarchicalNetName());
 * }
 * 
 */

/*
 * private void printEDIFPortInsts(BufferedWriter writer,
 * Collection<EDIFPortInst> epis) throws IOException {
 * for (EDIFPortInst epi : epis) {
 * String s = String.format(
 * "\n\t\tNAME: %-20s FULL NAME: %-30s INDEX: %d",
 * epi.getName(), epi.getFullName(), epi.getIndex()
 * );
 * writer.write(s);
 * }
 * }
 */

/*
 * writer.write("Printing EDIFCell(s) in EDIFNetlist: ");
 * HashMap<String, EDIFCellInst> ecis = netlist.generateCellInstMap();
 * for (EDIFCellInst eci : ecis.values()) {
 * writer.write("\n\t"+eci.getCellName());
 * writer.write("\n\tEDIFPortInst(s): ");
 * Collection<EDIFPortInst> epis = eci.getPortInsts();
 * printEDIFPortInsts(writer, epis);
 * }
 */

/*
 * writer.write("\n\nPrinting EDIFNet(s): ");
 * HashMap<String, EDIFNet> nets = netlist.generateEDIFNetMap(ecis);
 * writer.write("Net map contains "+nets.size()+" nets.");
 * for (int i = 0; i < nets.size(); i++) {
 * EDIFNet net = nets.get(i);
 * writer.write("\n\tNet #"+i+": ");
 * writer.write("\n\tEDIFPortInst(s): ");
 * if (net == null) {
 * writer.write("\n\t\tNet is null!");
 * continue;
 * }
 * Collection<EDIFPortInst> epis = net.getPortInsts();
 * printEDIFPortInsts(writer, epis);
 * }
 */

// https://www.rapidwright.io/javadoc/overview-tree.html
// https://www.rapidwright.io/javadoc/com/xilinx/rapidwright/design/Design.html
// https://www.rapidwright.io/javadoc/com/xilinx/rapidwright/device/Device.html
// https://docs.amd.com/r/en-US/ug912-vivado-properties/CELL

/*
 * 
 * List<EDIFHierCellInst> ehcis = netlist.getAllLeafHierCellInstances();
 * for (EDIFHierCellInst ehci : ehcis) {
 * writer.write("\nCell Name: "+ehci.getCellName());
 * writer.write("\n\tDepth: "+ehci.getDepth());
 * writer.write("\n\tHierarchical Inst Name:"+ehci.getFullHierarchicalInstName()
 * );
 * }
 *
 *
 * These ideas are equivalent:
 * edif.EDIFCell => design.Cell
 * Logical Cell => Physical Cell
 * Post-Synth Cell => Post-Place Cell
 * 
 * EDIFCell: Represents a logical cell in an EDIF netlist.
 * Cell: Corresponds to the leaf cell within the logical netlist EDIFCellInst
 * and
 * provides a mapping to a physical location BEL on the device.
 * It could also be called a BELInst.
 * 
 * Synthesis provides a raw EDIF netlist with EDIFCellInst(s).
 * Placement maps these logical EDIFCellInst(s) onto physical BELs / Tiles /
 * Sites.
 * Cells are created during placement via design.createAndPlaceCell(...).
 * Example : Cell or2 = design.createAndPlaceCell("or2", Unisim.OR2,
 * "SLICE_X112Y140/C6LUT");
 * Example : Cell led = design.createAndPlaceIOB("led", PinType.OUT, "R14",
 * "LVCMOS33")
 * 
 * https://www.rapidwright.io/javadoc/com/xilinx/rapidwright/design/Cell.html
 * https://www.rapidwright.io/javadoc/com/xilinx/rapidwright/design/Unisim.html
 * https://www.rapidwright.io/javadoc/com/xilinx/rapidwright/edif/EDIFCell.html
 * https://www.rapidwright.io/javadoc/com/xilinx/rapidwright/edif/EDIFNetlist.
 * html
 * https://www.rapidwright.io/javadoc/com/xilinx/rapidwright/edif/
 * EDIFHierCellInst.html
 * https://www.rapidwright.io/javadoc/com/xilinx/rapidwright/edif/EDIFHierNet.
 * html
 * https://www.rapidwright.io/javadoc/com/xilinx/rapidwright/edif/
 * EDIFHierPortInst.html
 * 
 */
