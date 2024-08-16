package src;

import java.io.IOException;
import java.io.BufferedWriter;

import com.xilinx.rapidwright.design.Design;
import com.xilinx.rapidwright.device.Device;


public abstract class Placer {

    protected static Device device;
    protected static Design design;

    protected static final String rootDir = "/home/bcheng/workspace/dev/place-and-route/";
    protected static final String synthesizedDcp = rootDir.concat("/outputs/synthesized.dcp");
    protected static final String placedDcp = rootDir.concat("/outputs/placed.dcp");

    public void init(BufferedWriter writer) throws IOException {
        Design design = Design.readCheckpoint(synthesizedDcp);
        design = place(design); 
        design.writeCheckpoint(placedDcp);
    }

    public abstract Design place(Design design); 


}
