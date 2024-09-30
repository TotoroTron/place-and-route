package placer;

import java.util.Collection;
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
import com.xilinx.rapidwright.design.ModuleInst;
import com.xilinx.rapidwright.design.ModuleImpls;
import com.xilinx.rapidwright.design.SiteInst;
import com.xilinx.rapidwright.design.SitePinInst;
import com.xilinx.rapidwright.design.Net;

import com.xilinx.rapidwright.edif.EDIFNetlist;
import com.xilinx.rapidwright.edif.EDIFLibrary;

import com.xilinx.rapidwright.edif.EDIFNet;
import com.xilinx.rapidwright.edif.EDIFHierNet;

import com.xilinx.rapidwright.edif.EDIFCell;
import com.xilinx.rapidwright.edif.EDIFCellInst;
import com.xilinx.rapidwright.edif.EDIFHierCellInst;

import com.xilinx.rapidwright.edif.EDIFPortInst;
import com.xilinx.rapidwright.edif.EDIFHierPortInst;

import com.xilinx.rapidwright.device.Device;
import com.xilinx.rapidwright.device.Site;
import com.xilinx.rapidwright.device.SiteTypeEnum;
import com.xilinx.rapidwright.device.BEL;
import com.xilinx.rapidwright.device.Tile;
import com.xilinx.rapidwright.device.TileTypeEnum;

public class ViewVivadoCheckpoint {

    protected Device device;
    protected Design design;

    private String rootDir = "/home/bcheng/workspace/dev/place-and-route/";
    private String synthDcp = rootDir + "/outputs/synthesized.dcp";
    private String placedDcp = rootDir + "/outputs/vivado_placed.dcp";
    private String routedDcp = rootDir + "/outputs/vivado_routed.dcp";

    public ViewVivadoCheckpoint() {

        this.device = Device.getDevice("xc7z020clg400-1");
    }

    public void run() throws IOException {
        this.design = Design.readCheckpoint(synthDcp);
        printout(design, "VivadoAfterSynthSiteInsts");
        this.design = Design.readCheckpoint(placedDcp);
        printout(design, "VivadoAfterPlaceSiteInsts");
        this.design = Design.readCheckpoint(routedDcp);
        printout(design, "VivadoAfterRouteSiteInsts");
    }

    public void printNets(Design design, BufferedWriter writer, String fileName) throws IOException {
        Collection<Net> nets = design.getNets();
        for (Net net : nets) {
            writer.write("\nNet: " + net.getName());
            List<SitePinInst> spis = net.getPins();
            for (SitePinInst spi : spis) {
                writer.write("\n\tSitePinInst: " + spi.getName());
            }

            Set<SiteInst> sis = net.getSiteInsts();
            for (SiteInst si : sis) {
                writer.write("\n\tSiteInst: " + si.getName());
            }
        }
    }

    public void printSiteInsts(Design design, BufferedWriter writer, String fileName) throws IOException {
        Collection<SiteInst> sis = design.getSiteInsts();
        for (SiteInst si : sis) {
            writer.write("\nSiteInst: " + si.getSiteName());
            writer.write("\n\tisPlaced(): " + si.isPlaced());
        }
    }

    public void printout(Design design, String fileName) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(rootDir + "outputs/" + fileName + ".txt"));
        printSiteInsts(this.design, writer, fileName);
        printNets(this.design, writer, fileName);
        if (writer != null)
            writer.close();
    }
}
