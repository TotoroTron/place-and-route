package placer;

import java.io.FileWriter;
import java.io.IOException;

import com.xilinx.rapidwright.design.Design;
import com.xilinx.rapidwright.device.Device;

public abstract class Packer {
    protected String packerName;
    FileWriter writer;

    protected Device device;
    protected Design design;
    protected String rootDir;

    protected String[] FF5_BELS = new String[] { "A5FF", "B5FF", "C5FF", "D5FF" };
    protected String[] FF_BELS = new String[] { "AFF", "BFF", "CFF", "DFF" };
    protected String[] LUT5_BELS = new String[] { "A5LUT", "B5LUT", "C5LUT", "D5LUT" };
    protected String[] LUT6_BELS = new String[] { "A6LUT", "B6LUT", "C6LUT", "D6LUT" };

    public Packer(String rootDir, Design design, Device device) {
        this.rootDir = rootDir;
        this.device = device;
        this.design = design;
    }

    public PackedDesign run() throws IOException {
        PackedDesign packedDesign = packDesign();
        writer.close();
        return packedDesign;
    }

    protected abstract PackedDesign packDesign() throws IOException;

}
