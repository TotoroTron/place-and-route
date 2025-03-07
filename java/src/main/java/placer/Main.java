package placer;

import java.util.logging.Logger;
import java.util.logging.FileHandler;
import java.util.logging.SimpleFormatter;
import java.util.logging.Level;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Collection;

import com.xilinx.rapidwright.design.ModuleInst;
import com.xilinx.rapidwright.design.Module;
import com.xilinx.rapidwright.design.Design;
import com.xilinx.rapidwright.design.SiteInst;

import com.xilinx.rapidwright.device.Device;
import com.xilinx.rapidwright.device.ClockRegion;

public class Main {

    private static final Logger logger = Logger.getLogger(Main.class.getName());
    protected static final String rootDir = "/home/bcheng/workspace/dev/place-and-route";
    protected static final String synthesizedDcp = rootDir + "/outputs/checkpoints/synthesized.dcp";

    public static void main(String[] args) {
        try {
            FileHandler fileHandler = new FileHandler(rootDir + "/outputs/logger.log", true); // 'true' appends to file
            fileHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(fileHandler);
            logger.setLevel(Level.ALL); // Set logging level to record all messages
            logger.log(Level.INFO, "Begin Placer...");

            drawVivadoPlacement();

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
            // ClockRegion regionConstraint = device.getClockRegion("X1Y0");
            ClockRegion regionConstraint = null;
            PackerBasic1 B1Packer = new PackerBasic1(
                    rootDir, design, device, regionConstraint);
            B1Packer.printUniqueSites();
            B1Packer.printClockBuffers();
            PackedDesign packedDesign = B1Packer.run(prepackedDesign);

            // Stage 3) Placer:
            // takes the packedDesign and figures out an optimal mapping of SiteInsts onto
            // Sites via simulated annealing, analytical, electrostatic placement, etc.
            // works entirely on the SiteInst/Site/Tile level.

            PlacerAnnealRandom SARPlacer = new PlacerAnnealRandom(
                    rootDir, design, device, regionConstraint);
            SARPlacer.run(packedDesign, prepackedDesign);

            // PlacerAnnealMidpoint PAMPlacer = new PlacerAnnealMidpoint(
            // rootDir, design, device, regionConstraint);
            // PAMPlacer.run(packedDesign, prepackedDesign);

            // PlacerGreedyRandom GRPlacer = new PlacerGreedyRandom(rootDir, design, device,
            // regionConstraint);
            // GRPlacer.run(packedDesign, prepackedDesign);

            // PlacerGreedyMidpoint GMPlacer = new PlacerGreedyMidpoint(rootDir, design,
            // device, regionConstraint);
            // GMPlacer.run(packedDesign, prepackedDesign);

        } catch (IOException e) {
            logger.log(Level.SEVERE, "An IOException occurred while configuring the logger.", e);
        }
    }

    public static void drawVivadoPlacement() throws IOException {
        Design design = Design.readCheckpoint(rootDir + "/outputs/checkpoints/vivado_placed.dcp");
        Device device = Device.getDevice("xc7z020clg400-1");

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
