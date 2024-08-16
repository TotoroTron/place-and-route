package src;

import java.util.ArrayList;

import java.io.IOException;
import java.io.BufferedWriter;

import com.xilinx.rapidwright.design.Design;
import com.xilinx.rapidwright.design.Net;
import com.xilinx.rapidwright.device.Device;


public abstract class Placer {

    protected static Device device;
    protected static Design design;

    protected static final String rootDir = "/home/bcheng/workspace/dev/place-and-route/";
    protected static final String synthesizedDcp = rootDir.concat("/outputs/synthesized.dcp");
    protected static final String placedDcp = rootDir.concat("/outputs/placed.dcp");

    public void init(BufferedWriter writer) throws IOException {
        Design design = Design.readCheckpoint(synthesizedDcp);
        printDesignInfo(design);
        design = place(design); 
        design.writeCheckpoint(placedDcp);
    }

    public void printDesignInfo(Design design) {

        writer.write("Printing ModuleInst(s)...")
        for (ModuleInst mi : design.getModuleInsts()) {
            Module module = mi.getModule();
            String moduleName = module.getName();
            writer.write("Module name: ".concat(moduleName))
        }
    }

    private abstract Design place(Design design); 



}
