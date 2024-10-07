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
import com.xilinx.rapidwright.design.SiteInst;
import com.xilinx.rapidwright.design.Net;
import com.xilinx.rapidwright.design.Cell;
import com.xilinx.rapidwright.design.SitePinInst;

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
    // protected Design design;
    // protected EDIFNetlist netlist;

    protected String rootDir = "/home/bcheng/workspace/dev/place-and-route/";
    protected String synthesizedDcp = rootDir + "/outputs/synthesized.dcp";
    protected String placedDcp = rootDir + "/outputs/placed.dcp";

    public Placer() throws IOException {
        device = Device.getDevice("xc7z020clg400-1");
    }

    protected abstract Design place(Design design) throws IOException;

    public void run() throws IOException {

        Design design = Design.readCheckpoint(synthesizedDcp);
        design.flattenDesign();
        EDIFNetlist netlist = design.getNetlist();

        printOneTile();
        printAllDeviceTiles();
        printUniqueTiles();
        printAllDeviceSites();
        printUniqueSites();

        printEDIFHierCellInsts(netlist);
        printEDIFCellInstsTest(netlist);
        printEDIFCellInsts(netlist);
        printEDIFNets(netlist);
        printEDIFHierNets(netlist);
        printTopCell(netlist);

        printAllSiteInsts(design, "SiteInstsBeforePlace");
        printNets(design, "NetsBeforePlace");
        printCells(design, "CellsBeforePlace");
        // printVCCNet(design, "VCCNetBeforeRoute");

        design = place(design);

        printAllSiteInsts(design, "SiteInstsAfterPlace");
        printNets(design, "NetsAfterPlace");
        printCells(design, "CellsAfterPlace");
        printVCCNet(design, "VCCNetAfterRoute");

        design.writeCheckpoint(placedDcp);
    }

    public void printCells(Design design, String fileName) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(rootDir + "outputs/" + fileName + ".txt"));
        Collection<Cell> cells = design.getCells();

        System.out.println("\nNumber of cells: " + cells.size());

        for (Cell cell : cells) {
            String s1 = String.format(
                    "Cell: %-40s isPlaced = %-10s",
                    cell.getName(), cell.isPlaced());
            System.out.println(s1);
            writer.newLine();
            writer.write(s1);

            if (cell.getSite() != null) {
                String s2 = "\tSite: " + cell.getSite().getName();
                String s3 = "\tSiteInst: " + cell.getSiteInst().getName() + " \tPlaced = "
                        + cell.getSiteInst().isPlaced();
                String s4 = "\tSiteTypeEnum: " + cell.getSiteInst().getSiteTypeEnum();
                System.out.println(s2);
                System.out.println(s3);
                System.out.println(s4);
                writer.newLine();
                writer.write(s2);
                writer.newLine();
                writer.write(s3);
                writer.newLine();
                writer.write(s4);

            }
        }

        if (writer != null)
            writer.close();
    }

    public void printVCCNet(Design design, String fileName) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(rootDir + "outputs/" + fileName + ".txt"));

        Net net = design.getNet("GLOBAL_LOGIC1");
        List<SitePinInst> spis = net.getPins();
        if (spis.isEmpty())
            writer.write("\nNet has no pins!");
        else
            for (SitePinInst spi : spis)
                writer.write("\nSitePinInst: " + spi.getName());
        if (writer != null)
            writer.close();
    }

    public void printNets(Design design, String fileName) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(rootDir + "outputs/" + fileName + ".txt"));
        Collection<Net> nets = design.getNets();
        for (Net net : nets) {
            writer.write("\nNet: " + net.getName());
            List<SitePinInst> spis = net.getPins();
            if (spis.isEmpty())
                writer.write("\n\tSitePinInsts: None!");
            else
                for (SitePinInst spi : spis)
                    writer.write("\n\tSitePinInst: " + spi.getName() + " isRouted() = " + spi.isRouted());
            writer.newLine();
        }

        if (writer != null)
            writer.close();
    }

    public void printOneTile() throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(rootDir + "outputs/OneTile.txt"));
        Tile tile = device.getTile(155, 10); // ROW, COLUMN
        writer.write("\nTileType: " + tile.getTileTypeEnum());
        writer.write("\nTileTypeIndex: " + tile.getTileTypeIndex());
        writer.write("\nTileName: " + tile.getName());
        writer.write("\nRow: " + tile.getRow() + "\tCol: " + tile.getRow());
        writer.write("\nX: " + tile.getTileXCoordinate() + "\tY: " + tile.getTileYCoordinate());
        if (writer != null)
            writer.close();
    }

    public void printTileArray(BufferedWriter writer, Tile[] tiles, boolean showSites, boolean showBELs)
            throws IOException {
        if (tiles.length == 0)
            writer.write("\nEmpty Tile Array.");
        for (Tile tile : tiles) {
            String s1 = String.format(
                    "\nTileType: %-30s TileName: %-40s Row: %-5s Col: %-5s X: %-5s Y: %-5s",
                    tile.getTileTypeEnum(), tile.getName(), tile.getRow(), tile.getColumn(),
                    tile.getTileXCoordinate(), tile.getTileYCoordinate());
            writer.write(s1);
            Site[] sites = tile.getSites();
            if (showSites == true)
                printSiteArray(writer, sites, showBELs);
        }
    }

    public void printSiteArray(BufferedWriter writer, Site[] sites, boolean showBELs) throws IOException {
        if (sites.length == 0)
            writer.write("\n\tEmpty Site Array.");
        for (Site site : sites) {
            String s2 = String.format(
                    "\n\tSiteType: %-30s SiteName: %-40s ", site.getSiteTypeEnum(), site.getName());
            writer.write(s2);
            BEL[] bels = site.getBELs();
            if (showBELs == true)
                printBELArray(writer, bels);
        }
    }

    public void printBELArray(BufferedWriter writer, BEL[] bels) throws IOException {
        if (bels.length == 0)
            writer.write("\n\t\tEmpty BEL Array.");
        int word_count = 0;
        writer.write("\n\t\t");
        for (BEL bel : bels) {
            writer.write(bel.getName() + " ");
            word_count++;
            if (word_count == 8) {
                writer.write("\n\t\t");
                word_count = 0;
            }
        }
    }

    public void printAllSiteInsts(Design design, String fileName) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(rootDir + "outputs/" + fileName + ".txt"));
        Collection<SiteInst> sis = design.getSiteInsts();
        for (SiteInst si : sis) {
            writer.write("\nSiteInst: " + si.getSiteName());
            writer.write("\n\tisPlaced(): " + si.isPlaced());
        }
        if (writer != null)
            writer.close();
    }

    public void printAllDeviceTiles() throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(rootDir + "outputs/DeviceAllTiles.txt"));
        writer.write("\nPrinting all tiles in device: ");
        Tile[][] tiles = device.getTiles();
        for (Tile[] row : tiles) {
            printTileArray(writer, row, false, false);
        }
        if (writer != null)
            writer.close();
    }

    public void printAllDeviceSites() throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(rootDir + "outputs/DeviceAllSites.txt"));
        writer.write("Printing All Site(s) in device " + device.getName() + ": ");
        writer.write("Unique site types: " + device.getSiteTypeCount());
        Site[] sites = device.getAllSites();
        printSiteArray(writer, sites, false);
        if (writer != null)
            writer.close();
    }

    public void printUniqueTiles() throws IOException {
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
        writer.write("\nNumber of unique tile types: " + uniqueTiles.size());
        printTileArray(writer, uniqueTiles.toArray(new Tile[0]), true, true);
        if (writer != null)
            writer.close();
    }

    public void printUniqueSites() throws IOException {
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
        writer.write("\nNunmber of unique site types: " + uniqueSites.size());
        printSiteArray(writer, uniqueSites.toArray(new Site[0]), true);
        if (writer != null)
            writer.close();
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

    private void printTopCell(EDIFNetlist netlist) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(rootDir + "outputs/TopCell.txt"));
        EDIFCell topCell = netlist.getTopCell();
        EDIFCellInst topCellInst = netlist.getTopCellInst();
        EDIFHierCellInst topHierCellInst = netlist.getTopHierCellInst();
        writer.write("\ntopCellInst: " + topCellInst.getCellName() + " topHierCellInst: "
                + topHierCellInst.getFullHierarchicalInstName());
        writer.write("\nDepth: " + topHierCellInst.getDepth());
        writer.write("\nCell Type: " + topHierCellInst.getCellType().getName());
        writer.write("\nCell is primitive? " + topHierCellInst.getCellType().isPrimitive());

        // VHDL "components" and Verilog "modules" are also represented as cells.
        // The cells are either primitive or not primitive.
        // Primitive cells are LUTs, FDREs, etc.
        // Components instantiated inside a component is represented as a child cell of
        // the outer parent cell.

        Collection<EDIFPortInst> epis = topCellInst.getPortInsts();
        printEDIFPortInsts(writer, epis);

        List<EDIFHierPortInst> ehpis = topHierCellInst.getHierPortInsts();
        printEDIFHierPortInsts(writer, ehpis);

        if (writer != null)
            writer.close();
    }

    private void printEDIFPortInsts(BufferedWriter writer, Collection<EDIFPortInst> epis) throws IOException {
        if (epis.isEmpty())
            writer.write("\n\tEmpty EDIFPortInst Collection.");
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
                    "\nString Key: %-30s EDIFCellInst Value: %-30s EDIFName: %-20s EDIFView: %-20s",
                    key, eci.getCellName(), eci.getCellType().getEDIFView().getName(), eci.getCellType().getView());
            writer.write(s1);
            writer.write("\nEDIFPortInst(s) on this cell: ");
            Collection<EDIFPortInst> epis = eci.getPortInsts();
            printEDIFPortInsts(writer, epis);
            writer.newLine();
        }
        if (writer != null)
            writer.close();
    }

    public void printEDIFCellInstsTest(EDIFNetlist netlist) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(rootDir + "outputs/EDIFCellInstsTest.txt"));
        List<EDIFCellInst> ecis_list = netlist.getAllLeafCellInstances();
        writer.write("\nList size: " + ecis_list.size());
        for (EDIFCellInst eci : ecis_list) {
            writer.write("\nEDIFCellInst: " + eci.getCellName());
        }
        writer.newLine();
        HashMap<String, EDIFCellInst> ecis_map = netlist.generateCellInstMap();
        writer.write("\nMap size: " + ecis_list.size());
        for (Map.Entry<String, EDIFCellInst> entry : ecis_map.entrySet()) {
            EDIFCellInst eci = entry.getValue();
            writer.write("\nEDIFCellInst: " + eci.getCellName());
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
            writer.write("\nTop level EDIFPortInst(s) in this net: ");
            List<EDIFPortInst> topPorts = net.getAllTopLevelPortInsts();
            if (topPorts.isEmpty())
                writer.write("\n\tNONE!");
            else
                for (EDIFPortInst topPort : topPorts)
                    writer.write("\n\t" + topPort.toString());

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
        if (ehpis.isEmpty())
            writer.write("\n\tEmpty EDIFHierPortInst Collection.");
        for (EDIFHierPortInst ehpi : ehpis) {
            writer.write("\n\t" + ehpi.toString());
            // writer.write("\tHier Inst Name: " + ehpi.getHierarchicalInstName());
            // writer.write("\tFull Hier Inst Name: " + ehpi.getFullHierarchicalInstName());
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
        /*
         * EDIFHierNet key = entry.getKey(); // Net Name
         * EDIFHierNet val = entry.getValue(); // Net Parent
         * If Name = Parent, then it means the net source comes from a primitive cell
         * or an I/O pad
         * If Name != Parent, then the net source comes from non-primitive
         * hierarchica cell
         */
        for (EDIFHierNet ehn : ehns.values()) {
            writer.write("\n" + ehn.getHierarchicalNetName());
            writer.write("\nEDIFHierPortInst(s) on this net: ");
            Collection<EDIFHierPortInst> ehpis = ehn.getPortInsts();
            printEDIFHierPortInsts(writer, ehpis);
        }
        if (writer != null)
            writer.close();
    }

    public void printModuleImpls(Design design) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(rootDir + "outputs/ModuleImpls.txt"));
        writer.write("Printing ModuleImpls: ");
        Collection<ModuleImpls> modimpls = design.getModules();
        for (ModuleImpls modimpl : modimpls) {
            writer.write("\n" + modimpl.getName());
        }
        if (writer != null)
            writer.close();
    }

    public void printModuleInsts(Design design) throws IOException {
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
