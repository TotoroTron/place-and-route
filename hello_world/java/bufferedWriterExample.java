import com.xilinx.rapidwright.device.*;
import com.xilinx.rapidwright.design.*;

import java.io.IOException;
import java.io.BufferedWriter;
import java.io.FileWriter;

import java.util.List;
import java.util.Collection;
import java.util.logging.Logger;
import java.util.logging.Level;


public class BufferedWriterExample {

    private static final Logger logger = Logger.getLogger(BufferedWriterExample.class.getName());
    private static Device device;

    private static final String projectDir = "/home/bcheng/workspace/dev/place-and-route/";  
    private static final String synthesizedDcp = projectDir.concat("/tcl/synthesized.dcp");
    private static final String placedDcp = projectDir.concat("tcl/placed.dcp");
    private static Design design;

    public static void main(String[] args) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("bw_log.txt"))) {
            device = Device.getDevice("xc7z020clg400-1");
            printDesignModules(writer);
            printDeviceInfo(writer);
            anotherWritingFunction(writer);
            logger.log(Level.INFO, "Writing complete. Check 'bw_log.txt'");
        } catch (IOException e) {
            logger.log(Level.SEVERE, "An IOException occurred.", e);
        }
    }


    public static void printDesignModules(BufferedWriter writer) throws IOException {
        design = Design.readCheckpoint(synthesizedDcp);
        Collection<ModuleImpls> modules = design.getModules();

        writer.write("Printing design ModuleImpl(s)... ");
        for (ModuleImpls module : modules) {
            String name = module.getName();
            writer.write("\t".concat(name));
        }
    }

    public static void printDeviceInfo(BufferedWriter writer) throws IOException {
        Site[] sites = device.getAllSites();
        // Site[] sites = device.getAllSitesOfType(SiteTypeEnum.SLICEM);

        for (Site site : sites) {
            String name = site.getName();
            int siteX = site.getInstanceX();
            int siteY = site.getInstanceY();
            Tile tile = site.getIntTile();

            String line = String.format(
                "Site: %-20s\tX = %-5d\tY = %-5d\tTile: %-20s%n",
                name, siteX, siteY, tile
            );

            writer.write(line);
        }
    }

    public static void anotherWritingFunction(BufferedWriter writer) throws IOException {
        writer.write("Hello 2!");
        writer.newLine();
    }
}


