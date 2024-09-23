package placer;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;

import com.xilinx.rapidwright.design.Design;
import com.xilinx.rapidwright.design.ModuleInst;
import com.xilinx.rapidwright.design.ModuleImpls;

import com.xilinx.rapidwright.edif.EDIFNetlist;
import com.xilinx.rapidwright.edif.EDIFLibrary;

import com.xilinx.rapidwright.edif.EDIFNet;
import com.xilinx.rapidwright.edif.EDIFHierNet;

import com.xilinx.rapidwright.edif.EDIFCell;
import com.xilinx.rapidwright.edif.EDIFCellInst;
import com.xilinx.rapidwright.edif.EDIFHierCellInst;

import com.xilinx.rapidwright.edif.EDIFPortInst;
import com.xilinx.rapidwright.edif.EDIFHierPortInst;

import com.xilinx.rapidwright.device.Device;
import com.xilinx.rapidwright.device.Site;
import com.xilinx.rapidwright.device.SiteTypeEnum;
import com.xilinx.rapidwright.device.BEL;
import com.xilinx.rapidwright.device.Tile;
import com.xilinx.rapidwright.device.TileTypeEnum;

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

    protected abstract Design place(Design design);

    public void run() throws IOException {
        EDIFNetlist netlist = design.getNetlist();

        printAllDeviceTiles(device);
        printUniqueTiles(device);

        printAllDeviceSites(device);
        printUniqueSites(device);

        design = place(design);
        design.writeCheckpoint(placedDcp);
    }

    public void printTileArray(BufferedWriter writer, Tile[] tiles) throws IOException {
        for (Tile tile : tiles) {
            String s1 = String.format(
                    "\nTileName: %-30s TileType: %-20s, Row: %-5s Col: %-5s, X: %-5s Y: %-5s",
                    tile.getName(), tile.getTileTypeEnum(), tile.getRow(), tile.getColumn(),
                    tile.getTileXCoordinate(), tile.getTileYCoordinate());
            writer.write(s1);
            Site[] sites = tile.getSites();
            printSiteArray(writer, sites);
        }
    }

    public void printSiteArray(BufferedWriter writer, Site[] sites) throws IOException {
        for (Site site : sites) {
            String s2 = String.format(
                    "\n\tSiteName: %-30s SiteType: %-20s", site.getName(), site.getSiteTypeEnum());
            writer.write(s2);
            // printBELArray(writer, site);
        }
        writer.newLine();
    }

    public void printBELArray(BufferedWriter writer, Site site) throws IOException {
        writer.write("\n\t\tBELs in this site: \n\t\t");
        BEL[] bels = site.getBELs();
        int word_count = 0;
        for (BEL bel : bels) {
            writer.write(bel.getName() + " ");
            word_count++;
            if (word_count == 8) {
                writer.write("\n\t\t");
                word_count = 0;
            }
        }
    }

    public void printAllDeviceTiles(Device device) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(rootDir + "outputs/DeviceAllTiles.txt"));
        writer.write("\nPrinting all tiles in device: ");
        Tile[][] tiles = device.getTiles();
        for (Tile[] row : tiles) {
            printTileArray(writer, row);
        }
        if (writer != null)
            writer.close();
    }

    public void printUniqueTiles(Device device) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(rootDir + "outputs/DeviceUniqueTiles.txt"));
        // writer.write("Number of unique tile types: " + device.getTileTypeCount());
        // why is this inconsistent with unique TypeEnums?
        writer.write("\nPrinting unique tiles in device: ");
        writer.newLine();

        Tile[][] tiles = device.getTiles();
        Set<TileTypeEnum> uniqueTileTypes = new HashSet<>();
        List<Tile> uniqueTiles = new ArrayList<>();

        for (Tile[] row : tiles) {
            for (Tile tile : row) {
                if (uniqueTileTypes.add(tile.getTileTypeEnum())) {
                    uniqueTiles.add(tile);
                }
            }
        }

        writer.write("\nUnique tile types: ");
        for (Tile uniqueTile : uniqueTiles) {
            writer.write("\n\t" + uniqueTile.getTileTypeEnum());
        }

        writer.newLine();
        writer.write("\nNumber of unique tile types: " + uniqueTiles.size());
        writer.write("\nUnique tile types with detail: ");
        writer.newLine();
        for (Tile uniqueTile : uniqueTiles) {
            writer.write("\nTileType: " + uniqueTile.getTileTypeEnum());
            Site[] sites = uniqueTile.getSites();
            printSiteArray(writer, sites);
        }

        if (writer != null)
            writer.close();

    }

    public void printUniqueSites(Device device) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(rootDir + "outputs/DeviceUniqueSites.txt"));
        // writer.write("Number of unique sites in the device: " +
        // device.getSiteTypeCount());
        // why is this inconsistent with unique TypeEnums?
        writer.write("\nPrinting unique sites in the device: ");
        writer.newLine();

        Site[] sites = device.getAllSites();
        Set<SiteTypeEnum> uniqueSiteTypes = new HashSet<>();
        List<Site> uniqueSites = new ArrayList<>();

        for (Site site : sites) {
            if (uniqueSiteTypes.add(site.getSiteTypeEnum())) {
                uniqueSites.add(site);
            }
        }

        writer.write("\nUnique site types: ");
        for (Site uniqueSite : uniqueSites) {
            writer.write("\n\t" + uniqueSite.getSiteTypeEnum());
        }

        writer.newLine();
        writer.write("\nNumber of unique site types: " + uniqueSites.size());
        writer.write("\nUnique sites types with detail: ");
        writer.newLine();

        printSiteArray(writer, uniqueSites.toArray(new Site[0]));

        if (writer != null)
            writer.close();
    }

    public void printAllDeviceSites(Device device) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(rootDir + "outputs/DeviceAllSites.txt"));
        writer.write("Printing All Site(s) in device " + device.getName() + ": ");
        writer.write("Unique site types: " + device.getSiteTypeCount());
        Site[] sites = device.getAllSites();
        printSiteArray(writer, sites);
        if (writer != null)
            writer.close();
    }

    public void printDeviceSlices(Device device) throws IOException {
        BufferedWriter writerL = new BufferedWriter(new FileWriter(rootDir + "outputs/DeviceSLICELs.txt"));
        BufferedWriter writerM = new BufferedWriter(new FileWriter(rootDir + "outputs/DeviceSLICEMs.txt"));
        writerL.write("Printing SLICEL Sites in device " + device.getName() + ": ");
        writerM.write("Printing SLICEM Sites in device " + device.getName() + ": ");
        Site[] sliceLs = device.getAllSitesOfType(SiteTypeEnum.SLICEL);
        Site[] sliceMs = device.getAllSitesOfType(SiteTypeEnum.SLICEM);
        for (Site sliceL : sliceLs) {
            writerL.write("\n" + sliceL.getName());
            printBELArray(writerL, sliceL);
        }
        for (Site sliceM : sliceMs) {
            writerM.write("\n" + sliceM.getName());
            printBELArray(writerM, sliceM);
        }
        if (writerL != null)
            writerL.close();
        if (writerM != null)
            writerM.close();
    }

    // ============= LIBRARY PRINTOUT ================

    public void printEDIFLibrary(EDIFNetlist netlist) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(rootDir + "outputs/EDIFLibrary.txt"));
        writer.write("Printing EDIFCells in EDIFLibrary: ");
        writer.newLine();
        EDIFLibrary library = netlist.getHDIPrimitivesLibrary();
        Map<String, EDIFCell> ecs = library.getCellMap();
        for (String cell : ecs.keySet())
            writer.write("\nEDIFCell: " + cell);
        if (writer != null)
            writer.close();
    }

    // ============== NON-HIER PRINTOUT ==================

    private void printEDIFPortInsts(BufferedWriter writer, Collection<EDIFPortInst> epis) throws IOException {
        for (EDIFPortInst epi : epis) {
            writer.write("\n\t" + epi.toString());
            // writer.write("\tName: " + epi.getName());
            // writer.write("\tFull Name: " + epi.getFullName());
        }
    }

    public void printEDIFCellInsts(EDIFNetlist netlist) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(rootDir + "outputs/EDIFCellInsts.txt"));
        writer.write("Printing EDIFCellInst(s) HashMap: ");
        writer.newLine();
        HashMap<String, EDIFCellInst> ecis = netlist.generateCellInstMap();
        for (Map.Entry<String, EDIFCellInst> entry : ecis.entrySet()) {
            String key = entry.getKey();
            EDIFCellInst eci = entry.getValue();
            String s1 = String.format(
                    "\nString Key: %-30s  =>\tEDIFCellInst Value: %-30s", key, eci.getCellName());
            writer.write(s1);
            writer.write("\nEDIFPortInst(s) on this cell: ");
            Collection<EDIFPortInst> epis = eci.getPortInsts();
            printEDIFPortInsts(writer, epis);
            writer.newLine();
        }
        if (writer != null)
            writer.close();
    }

    public void printEDIFNets(EDIFNetlist netlist) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(rootDir + "outputs/EDIFNets.txt"));
        writer.write("Printing EDIFNets: ");
        writer.newLine();
        HashMap<String, EDIFCellInst> ecis = netlist.generateCellInstMap();
        HashMap<String, EDIFNet> ens = netlist.generateEDIFNetMap(ecis);
        for (EDIFNet net : ens.values()) {

            // Top level ports are exactly what they sound like:
            // Ports declared in the top_level.vhd module.
            // NONE means this net does not have ports in the top_level module.
            writer.write("\nTop level EDIFPortInst(s) in this net: ");
            List<EDIFPortInst> topPorts = net.getAllTopLevelPortInsts();
            if (topPorts.isEmpty())
                writer.write("\n\tNONE!");
            else
                for (EDIFPortInst topPort : topPorts)
                    writer.write("\n\t" + topPort.toString());

            // Source ports are exactly what they sound like:
            // Output ports of any module.
            writer.write("\nSource EDIFPortInst(s) in this net: ");
            List<EDIFPortInst> sourcePorts = net.getSourcePortInsts(true); // bool includeTopLevelPorts
            printEDIFPortInsts(writer, sourcePorts);

            writer.write("\nEDIFPortInst(s) in this net: ");
            Collection<EDIFPortInst> epis = net.getPortInsts();
            printEDIFPortInsts(writer, epis);
            writer.newLine();
        }
        if (writer != null)
            writer.close();
    }

    // ============ HIERARCHICAL PRINTOUTS ===============

    private void printEDIFHierPortInsts(BufferedWriter writer, Collection<EDIFHierPortInst> ehpis) throws IOException {
        for (EDIFHierPortInst ehpi : ehpis) {
            writer.write("\n\t" + ehpi.toString());
            writer.write("\tHier Inst Name: " + ehpi.getHierarchicalInstName());
            writer.write("\tFull Hier Inst Name: " + ehpi.getFullHierarchicalInstName());
        }
    }

    public void printEDIFHierCellInsts(EDIFNetlist netlist) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(rootDir + "outputs/EDIFHierCellInsts.txt"));
        writer.write("Printing EDIFHierCellInsts(s) with their EDIFHierPortInst(s): ");
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
        writer.write("Printing EDIFHierNet(s) with their EDIFHierPortInst(s): ");
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
        if (writer != null)
            writer.close();
    }
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
