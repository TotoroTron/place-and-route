package placer;

import java.io.FileWriter;
import java.io.IOException;

import com.xilinx.rapidwright.design.Design;
import com.xilinx.rapidwright.device.Device;

public abstract class Placer {
    protected String placerName;
    FileWriter writer;

    protected final Device device;
    protected final Design design;

    protected String rootDir;
    protected String placedDcp;

    protected String[] FF5_BELS = new String[] { "A5FF", "B5FF", "C5FF", "D5FF" };
    protected String[] FF_BELS = new String[] { "AFF", "BFF", "CFF", "DFF" };
    protected String[] LUT5_BELS = new String[] { "A5LUT", "B5LUT", "C5LUT", "D5LUT" };
    protected String[] LUT6_BELS = new String[] { "A6LUT", "B6LUT", "C6LUT", "D6LUT" };

    public Placer(String rootDir, Design design, Device device) throws IOException {
        this.rootDir = rootDir;
        this.placedDcp = rootDir + "/outputs/checkpoints/placed.dcp";
        this.design = design;
        this.device = device;
    }

    public void run(PackedDesign packedDesign) throws IOException {
        writer = new FileWriter(rootDir + "/outputs/printout/" + placerName + ".txt");
        writer.write(placerName + ".txt");
        placeDesign(packedDesign);
        writer.close();
        design.writeCheckpoint(placedDcp);
    }

    public void run(PackedDesign packedDesign, PrepackedDesign prepackedDesign) throws IOException {
        writer = new FileWriter(rootDir + "/outputs/printout/" + placerName + ".txt");
        writer.write(placerName + ".txt");
        placeDesign(packedDesign);
        writer.close();
        design.writeCheckpoint(placedDcp);
    }

    protected abstract void placeDesign(PackedDesign packedDesign) throws IOException;

} // end class Placer
