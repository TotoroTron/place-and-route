package placer;

import java.util.logging.Logger;
import java.util.logging.FileHandler;
import java.util.logging.SimpleFormatter;
import java.util.logging.Level;

import java.io.FileWriter;
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

// import org.ejml.data.DMatrixRMaj;
// import org.ejml.dense.row.CommonOps_DDRM;

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
            // List<PlacerAnnealRandom> SAPlacers = new ArrayList<PlacerAnnealRandom>();
            // SAPlacers.add(new PlacerAnnealRandom(rootDir, design, device, region));
            // SAPlacers.add(new PlacerAnnealMidpoint(rootDir, design, device, region));
            // SAPlacers.add(new PlacerAnnealHybrid(rootDir, design, device, region));
            // SAPlacers.add(new PlacerGreedyRandom(rootDir, design, device, region));
            // SAPlacers.add(new PlacerGreedyMidpoint(rootDir, design, device, region));

            // for (PlacerAnnealRandom placer : SAPlacers) {
            // System.out.println("\n\nStarting " + placer.getPlacerName() + "... \n\n");
            // placer.makeOutputDirs(placer.getPlacerName());
            // placer.initCoolingSchedule(10000.0d, 0.98d, 300);
            // placer.run(packedDesign);
            // }

            List<String> placerNames = new ArrayList<>();
            List<List<Double>> combinedCostHistory = new ArrayList<>();

            List<PlacerAnnealRandom> PARPlacers = new ArrayList<PlacerAnnealRandom>();
            for (int i = 0; i < 2; i++) {
                for (int j = 0; j < 5; j++) {
                    int initialTemp = 10000 + (i * 10000);
                    Double coolingRate = 0.84 + (j * 0.03);
                    PlacerAnnealRandom placer = new PlacerAnnealRandom(rootDir, design, device, region);
                    PARPlacers.add(placer);
                    String fullName = placer.getPlacerName() + "_" + initialTemp + "_"
                            + (int) Math.round(coolingRate * 100);
                    System.out.println("========================================");
                    System.out.println("STARTING: " + fullName + " ... ");
                    System.out.println("========================================");
                    placer.makeOutputDirs(fullName);
                    placer.initCoolingSchedule(initialTemp, coolingRate, 300);
                    placer.run(packedDesign);

                    placerNames.add(fullName);
                    combinedCostHistory.add(placer.getCostHistory());
                }
            }

            List<PlacerAnnealHybrid> PAHPlacers = new ArrayList<PlacerAnnealHybrid>();
            for (int i = 0; i < 2; i++) {
                for (int j = 0; j < 5; j++) {
                    int initialTemp = 10000 + (i * 10000);
                    Double coolingRate = 0.84 + (j * 0.03);
                    PlacerAnnealHybrid placer = new PlacerAnnealHybrid(rootDir, design, device, region);
                    PAHPlacers.add(placer);
                    String fullName = placer.getPlacerName() + "_" + initialTemp + "_"
                            + (int) Math.round(coolingRate * 100);
                    System.out.println("========================================");
                    System.out.println("STARTING: " + fullName + " ... ");
                    System.out.println("========================================");
                    placer.makeOutputDirs(fullName);
                    placer.initCoolingSchedule(initialTemp, coolingRate, 300);
                    placer.run(packedDesign);

                    placerNames.add(fullName);
                    combinedCostHistory.add(placer.getCostHistory());
                }
            }

            writeCostHistoryCSV(placerNames, combinedCostHistory, rootDir + "/outputs/combined_cost_history.csv");

        } catch (IOException e) {
            logger.log(Level.SEVERE, "An IOException occurred while configuring the logger.", e);
        }
    }

    public static void writeCostHistoryCSV(List<String> labels, List<List<Double>> columns, String filePath)
            throws IOException {

        int numRows = columns.stream().mapToInt(List::size).max().orElse(0);
        int numCols = columns.size();

        try (FileWriter writer = new FileWriter(filePath)) {

            for (int col = 0; col < numCols; col++) {
                writer.write(labels.get(col));
                if (col < numCols - 1) {
                    writer.write(",");
                }
            }
            writer.write("\n");

            for (int row = 0; row < numRows; row++) {
                StringBuilder line = new StringBuilder();
                for (int col = 0; col < numCols; col++) {
                    List<Double> column = columns.get(col);
                    if (row < column.size()) {
                        line.append(column.get(row));
                    }
                    if (col < numCols - 1) {
                        line.append(",");
                    }
                }
                writer.write(line.toString());
                writer.write("\n");
            }
        }
    }

}
