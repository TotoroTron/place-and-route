package placer;

import java.io.FileWriter;
import java.io.File;
import java.io.IOException;

import com.xilinx.rapidwright.design.Design;
import com.xilinx.rapidwright.device.Device;

public abstract class Prepacker {
    protected String prepackerName;
    FileWriter writer;

    protected Device device;
    protected Design design;
    protected String rootDir;

    protected final String[] FF5_BELS = new String[] { "A5FF", "B5FF", "C5FF", "D5FF" };
    protected final String[] FF_BELS = new String[] { "AFF", "BFF", "CFF", "DFF" };
    protected final String[] LUT5_BELS = new String[] { "A5LUT", "B5LUT", "C5LUT", "D5LUT" };
    protected final String[] LUT6_BELS = new String[] { "A6LUT", "B6LUT", "C6LUT", "D6LUT" };

    public Prepacker(String rootDir, Design design, Device device) {
        this.rootDir = rootDir;
        this.device = device;
        this.design = design;
    }

    public PrepackedDesign run() throws IOException {
        String printoutDir = rootDir + "/outputs/prepackers";
        File printoutFile = new File(printoutDir);
        if (!printoutFile.exists()) {
            printoutFile.mkdirs();
        }
        writer = new FileWriter(printoutDir + "/" + prepackerName + ".txt");
        writer.write(prepackerName + ".txt");
        PrepackedDesign prepackedDesign = prepackDesign();
        writer.close();
        return prepackedDesign;
    }

    protected abstract PrepackedDesign prepackDesign() throws IOException;

}
