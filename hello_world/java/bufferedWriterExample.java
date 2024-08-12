import com.xilinx.rapidwright.device.*;

import java.io.IOException;
import java.io.BufferedWriter;
import java.io.FileWriter;

import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;


public class BufferedWriterExample {

    private static Device device;
    private static final Logger logger = Logger.getLogger(BufferedWriterExample.class.getName());

    public static void main(String[] args) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("bw_log.txt"))) {
            device = Device.getDevice("xc7z020clg400-1");
            printDeviceInfo(writer);
            anotherWritingFunction(writer);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "An IOException occurred.", e);
        }
    }

    public static void printDeviceInfo(BufferedWriter writer) throws IOException {
        Site[] sites = device.getAllSitesOfType(SiteTypeEnum.SLICEM);

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


