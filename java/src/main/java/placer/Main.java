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
            FileHandler fileHandler = new FileHandler(rootDir + "/outputs/logger.log", true); // 'true' appends to file
            fileHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(fileHandler);
            logger.setLevel(Level.ALL); // Set logging level to record all messages
            logger.log(Level.INFO, "Begin Placer...");

            Design design = Design.readCheckpoint(synthesizedDcp);
            Device device = Device.getDevice("xc7z020clg400-1");

            // Stage 1) Prepacker:
            // works entirely on the EDIFHierCellInst level.
            // identifies cell patterns like CARRY chains, DSP cascades, LUTFF pairs, etc.
            PrepackerBasic BPrepacker = new PrepackerBasic(rootDir, design, device);
            PrepackedDesign prepackedDesign = BPrepacker.run();

            // Stage 2) Packer:
            // takes the prepackedDesign and packs the EDIFHierCellInst into SiteInsts
            // also provides an initial random placement of SiteInsts onto actual Sites.
            // PackerBasic BPacker = new PackerBasic(rootDir, design, device,
            // device.getClockRegion("X1Y0"));
            PackerBasic BPacker = new PackerBasic(rootDir, design, device, null);
            BPacker.printUniqueSites();
            BPacker.printClockBuffers();
            PackedDesign packedDesign = BPacker.run(prepackedDesign);

            // Stage 3) Placer:
            // takes the packedDesign and figures out an optimal mapping of SiteInsts onto
            // Sites via simulated annealing, analytical, electrostatic placement, etc.
            // works entirely on the SiteInst/Site/Tile level.
            PlacerGreedyRandom1 GR1Placer = new PlacerGreedyRandom1(rootDir, design, device);
            GR1Placer.run(packedDesign);

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
}
