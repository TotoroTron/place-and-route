package placer;

import java.util.logging.Logger;
import java.util.logging.FileHandler;
import java.util.logging.SimpleFormatter;
import java.util.logging.Level;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Collection;

import java.util.stream.Collectors;
import java.util.List;

import com.xilinx.rapidwright.design.Net;
import com.xilinx.rapidwright.design.ModuleInst;
import com.xilinx.rapidwright.design.Module;
import com.xilinx.rapidwright.design.ConstraintGroup;
import com.xilinx.rapidwright.design.Design;
import com.xilinx.rapidwright.design.SiteInst;

import com.xilinx.rapidwright.device.Device;
import com.xilinx.rapidwright.device.ClockRegion;
import com.xilinx.rapidwright.device.Tile;
import com.xilinx.rapidwright.device.TileTypeEnum;

import com.xilinx.rapidwright.design.DesignTools;

import org.ejml.data.DMatrixRMaj;
import org.ejml.dense.row.CommonOps_DDRM;

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

            // Design vivado_design = Design.readCheckpoint(rootDir +
            // "/outputs/checkpoints/vivado_routed.dcp");
            // drawPlacement(vivado_design, rootDir + "/outputs/placers/vivado_routed.png");

            // Design rapidwright_design = Design
            // .readCheckpoint(rootDir + "/outputs/checkpoints/routed.dcp");
            // drawPlacement(rapidwright_design, rootDir +
            // "/outputs/placers/rapidwright_routed.png");

            Design design = Design.readCheckpoint(synthesizedDcp);
            Device device = Device.getDevice("xc7z020clg400-1");
            ImageMaker imdev = new ImageMaker(device);
            imdev.exportImage(rootDir + "/outputs/device.png");

            // Stage 1) Prepacker:
            // works entirely on the EDIFHierCellInst level.
            // identifies cell patterns like CARRY chains, DSP cascades, LUTFF pairs, etc.
            PrepackerBasic BPrepacker = new PrepackerBasic(rootDir, design, device);
            PrepackedDesign prepackedDesign = BPrepacker.run();

            // Stage 2) Packer:
            // takes the prepackedDesign and packs the EDIFHierCellInst into SiteInsts
            // also provides an initial random placement of SiteInsts onto actual Sites.
            // ClockRegion region = device.getClockRegion("X1Y0");
            ClockRegion region = null;
            PackerBasic1 B1Packer = new PackerBasic1(
                    rootDir, design, device, region);
            PackedDesign packedDesign = B1Packer.run(prepackedDesign);
            B1Packer.printUniqueSites();
            B1Packer.printClockBuffers();

            // Stage 3) Placer:
            // takes the packedDesign and figures out an optimal mapping of SiteInsts onto
            // Sites via simulated annealing, analytical, electrostatic placement, etc.
            // works entirely on the SiteInst/Site/Tile level.
            List<PlacerAnnealRandom> SAPlacers = new ArrayList<PlacerAnnealRandom>();
            SAPlacers.add(new PlacerAnnealRandom(rootDir, design, device, region));
            SAPlacers.add(new PlacerAnnealHybrid(rootDir, design, device, region));
            // SAPlacers.add(new PlacerAnnealMidpoint(rootDir, design, device, region));
            // SAPlacers.add(new PlacerGreedyRandom(rootDir, design, device, region));
            // SAPlacers.add(new PlacerGreedyMidpoint(rootDir, design, device, region));

            for (PlacerAnnealRandom placer : SAPlacers) {
                System.out.println("\n\nStarting " + placer.getPlacerName() + "... \n\n");
                placer.makeOutputDirs(placer.getPlacerName());
                placer.initCoolingSchedule(10000.0d, 0.98d, 50);
                placer.run(packedDesign);
            }

        } catch (IOException e) {
            logger.log(Level.SEVERE, "An IOException occurred while configuring the logger.", e);
        }
    }

    public static void drawPlacement(Design design, String filePath) throws IOException {
        ImageMaker im = new ImageMaker(design);
        im.renderAll();
        im.exportImage(filePath);
        double cost = 0;
        Collection<Net> nets = design.getNets();
        System.out.println("Number of Nets: " + nets.size());
        for (Net net : nets) {
            Tile srcTile = net.getSourceTile();
            if (srcTile == null) // tile is null if its' purely intrasite!
                continue;
            if (srcTile.getTileTypeEnum() == TileTypeEnum.RIOB33)
                continue;
            List<Tile> sinkTiles = net.getSinkPins().stream()
                    .map(spi -> spi.getTile())
                    .collect(Collectors.toList());
            for (Tile sinkTile : sinkTiles) {
                cost = cost + srcTile.getTileManhattanDistance(sinkTile);
            }
        }
        System.out.println("Design Cost: " + cost);
    }

    public static void helloEJML() {
        // 1. Create two 2x2 matrices A and B
        DMatrixRMaj A = new DMatrixRMaj(new double[][] {
                { 1, 2 },
                { 3, 4 }
        });
        DMatrixRMaj B = new DMatrixRMaj(new double[][] {
                { 5, 6 },
                { 7, 8 }
        });

        // 2. Matrix addition C = A + B
        DMatrixRMaj C = new DMatrixRMaj(A.numRows, A.numCols);
        CommonOps_DDRM.add(A, B, C);

        // 3. Matrix multiplication D = A * B
        DMatrixRMaj D = new DMatrixRMaj(A.numRows, B.numCols);
        CommonOps_DDRM.mult(A, B, D);

        // 4. Matrix inversion invA = A^-1
        DMatrixRMaj invA = new DMatrixRMaj(A.numRows, A.numCols);
        CommonOps_DDRM.invert(A, invA);

        // 5. Solve Ax = b (for x)
        // Here, b is a 2x1 column vector
        DMatrixRMaj b = new DMatrixRMaj(new double[][] { { 1 }, { 2 } });
        DMatrixRMaj x = new DMatrixRMaj(A.numRows, b.numCols);
        CommonOps_DDRM.solve(A, b, x);

        // Print results
        System.out.println("Matrix A:");
        A.print();
        System.out.println("Matrix B:");
        B.print();
        System.out.println("C = A + B:");
        C.print();
        System.out.println("D = A * B:");
        D.print();
        System.out.println("invA = A^-1:");
        invA.print();
        System.out.println("Solution x for Ax = b:");
        x.print();
    }

}
