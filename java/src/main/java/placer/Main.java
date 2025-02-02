package placer;

import java.util.logging.Logger;
import java.util.logging.FileHandler;
import java.util.logging.SimpleFormatter;
import java.util.logging.Level;

import java.io.IOException;

import java.util.List;
import java.util.Set;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.HashMap;

import com.xilinx.rapidwright.design.ModuleInst;
import com.xilinx.rapidwright.design.Module;
import com.xilinx.rapidwright.design.Design;
import com.xilinx.rapidwright.design.SiteInst;
import com.xilinx.rapidwright.design.Cell;
import com.xilinx.rapidwright.design.Unisim;

import com.xilinx.rapidwright.edif.EDIFHierCellInst;

import com.xilinx.rapidwright.device.Device;
import com.xilinx.rapidwright.device.Site;
import com.xilinx.rapidwright.device.BEL;
import com.xilinx.rapidwright.device.SiteTypeEnum;
import com.xilinx.rapidwright.device.ClockRegion;

public class Main {

    private static final Logger logger = Logger.getLogger(Main.class.getName());
    protected static final String rootDir = "/home/bcheng/workspace/dev/place-and-route";
    protected static final String synthesizedDcp = rootDir + "/outputs/synthesized.dcp";

    public static void main(String[] args) {
        try {
            // Logger to keep track of execution progress.
            FileHandler fileHandler = new FileHandler(rootDir + "/outputs/logger.log", true); // 'true' appends to file
            fileHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(fileHandler);
            logger.setLevel(Level.ALL); // Set logging level to record all messages
            logger.log(Level.INFO, "Begin Placer...");

            Design design = Design.readCheckpoint(synthesizedDcp);
            Device device = Device.getDevice("xc7z020clg400-1");
            System.out.println("FSR ROWS: " + device.getNumOfClockRegionRows() + ", FSR COLS: "
                    + device.getNumOfClockRegionsColumns());
            // testSiteInst1();
            // testModuleInst();

            PackerBasic BPacker = new PackerBasic(rootDir, design, device);
            PackedDesign packedDesign = BPacker.run();

            PlacerSiteCentric SCPlacer = new PlacerSiteCentric(rootDir, design, device,
                    device.getClockRegion("X1Y1"));
            SCPlacer.printUniqueSites();
            SCPlacer.printClockBuffers();
            SCPlacer.run(packedDesign);

            // ViewVivadoCheckpoint ViewVivado = new ViewVivadoCheckpoint();
            // ViewVivado.run();

        } catch (IOException e) {
            logger.log(Level.SEVERE, "An IOException occurred while configuring the logger.", e);
        }
    }

    public static void testModuleInst() throws IOException {
        Design design = Design.readCheckpoint(rootDir + "/outputs/vivado_placed.dcp");
        Collection<ModuleInst> mis = design.getModuleInsts();
        if (mis.isEmpty()) {
            System.out.println("Collection<ModuleInst> empty!");
        } else {
            for (ModuleInst mi : mis) {
                System.out.println("\tSites in ModuleImpl: " + mi.getSiteInsts());
                for (SiteInst si : mi.getSiteInsts()) {
                    System.out.println("SiteInst: " + si.getName());
                }
            }
        }

        Collection<Module> modules = new ArrayList<>();
        Module module = new Module(design);
        modules.add(module);
        for (Module m : modules) {
            System.out.println("\tSites in Module: " + m.getName());
            for (SiteInst si : m.getSiteInsts()) {
                System.out.println("SiteInst: " + si.getName());
            }

        }

    }

    public static void printBELCellMap(SiteInst si) throws IOException {
        System.out.println("BEL-Cell Map: ");
        Map<String, Cell> belCellMap = si.getCellMap();
        for (Map.Entry<String, Cell> entry : belCellMap.entrySet()) {
            String bel = entry.getKey();
            Cell cell = entry.getValue();
            System.out.println(
                    "\tBEL: " + bel + ", Cell:" + cell.getEDIFHierCellInst().getFullHierarchicalInstName());
        }
    }

    public static void printBELs(SiteInst si) throws IOException {
        System.out.println("BELs in this SiteInst: ");
        BEL[] bels = si.getBELs();
        for (BEL bel : bels) {
            System.out.println("\tBEL: " + bel.getName());
        }

    }

    public static void testSiteInst1() throws IOException {
        Design design = Design.readCheckpoint(synthesizedDcp);
        Device device = Device.getDevice("xc7z020clg400-1");

        // look at the design EDIF netlist and find just one FDRE ehci
        EDIFHierCellInst testCell = null;
        for (EDIFHierCellInst ehci : design.getNetlist().getAllLeafHierCellInstances()) {
            if (ehci.getCellName().equals("FDRE")) {
                testCell = ehci;
                break;
            }
        }
        if (testCell == null) {
            System.out.println("Could not find a FDRE EDIFHierCellInst from netlist!");
            return;
        }
        System.out.println("Found EDIFHierCellInst: " + testCell.getFullHierarchicalInstName());

        // SiteInst si = new SiteInst("MySiteInst", design, SiteTypeEnum.SLICEL,
        // device.getSite("SLICE_X93Y51"));
        SiteInst si = new SiteInst("MySiteInst", SiteTypeEnum.SLICEL);
        // printBELs(si);

        si.createCell(testCell, si.getBEL("AFF"));
        printBELCellMap(si);
        // printBELs(si);

        si.unPlace();
        System.out.println("\n UNPLACED SITEINST\n");
        printBELCellMap(si);
        // printBELs(si);

        si.place(device.getSite("SLICE_X93Y50"));
        System.out.println("\n MOVED + REPLACED SITEINST\n");
        printBELCellMap(si);
        // printBELs(si);

    };
}
