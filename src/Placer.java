import com.xilinx.rapidwright.design.Design;
import com.xilinx.rapidwright.device.Device;

import java.io.BufferedWriter;

public abstract class Placer {

    private static Device device;
    private static Design design;

    private static final String rootDir = "/home/bcheng/workspace/dev/place-and-route/";
    private static final String synthesizedDcp = projectDir.concat("tcl/synthesized.dcp");
    private static final String placedDcp = projectDir.concat("tcl/placed.dcp");

    public static void setup(BufferedWriter writer) throws IOException {
        Design design = Design.readCheckpoint(synthesizedDcp);
        design = place(design); 
        design.writeCheckpoint(placedDcp);
    }

    public abstract void place(Design design); 


}
