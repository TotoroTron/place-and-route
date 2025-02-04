package placer;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;

import com.xilinx.rapidwright.design.Design;
import com.xilinx.rapidwright.design.SiteInst;
import com.xilinx.rapidwright.design.Net;
import com.xilinx.rapidwright.design.Cell;
import com.xilinx.rapidwright.design.SitePinInst;

import com.xilinx.rapidwright.edif.EDIFNetlist;
import com.xilinx.rapidwright.edif.EDIFHierNet;
import com.xilinx.rapidwright.edif.EDIFNet;
import com.xilinx.rapidwright.edif.EDIFCellInst;
import com.xilinx.rapidwright.edif.EDIFHierCellInst;
import com.xilinx.rapidwright.edif.EDIFPortInst;
import com.xilinx.rapidwright.edif.EDIFHierPortInst;

import com.xilinx.rapidwright.device.Device;
import com.xilinx.rapidwright.device.Site;
import com.xilinx.rapidwright.device.SiteTypeEnum;
import com.xilinx.rapidwright.device.BEL;
import com.xilinx.rapidwright.device.BELPin;
import com.xilinx.rapidwright.device.ClockRegion;

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
        this.placedDcp = rootDir + "/outputs/placed.dcp";
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

    protected abstract void placeDesign(PackedDesign packedDesign) throws IOException;

} // end class Placer
