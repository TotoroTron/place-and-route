// import com.xilinx.rapidwright.design.Design;
// import com.xilinx.rapidwright.device.Device;
// import com.xilinx.rapidwright.design.*;
import com.xilinx.rapidwright.device.*;

import java.util.List;

import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.Level;
import java.io.IOException;

public class LoggingExample {

    private Device device;

    // logger instance
    private static final Logger logger = Logger.getLogger(LoggingExample.class.getName());

    // logger setup method
    private static void setupLogger() {
        try {
            FileHandler fh = new FileHandler("program.log", false);
            fh.setFormatter(new SimpleFormatter());
            logger.addHandler(fh);
            logger.setLevel(Level.ALL); // or INFO, WARNING, SEVERE
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to set up logger.", e);
        }
    }

    public LoggingExample(String deviceName) {
        this.device = Device.getDevice(deviceName);
    }
    private void printDeviceInfo() {
        logger.log(Level.INFO, "Printing device info...");
        logger.log(Level.INFO, "Device Name: " + this.device.getName());

        Site[] sites = device.getAllSitesOfType(SiteTypeEnum.SLICEM);
        
        for (Site site : sites) {
            String s = site.getName();
            int x = site.getInstanceX();
            int y = site.getInstanceY(); 
            Tile t = site.getIntTile();
            logger.log(Level.INFO, "Site: " + s + "\tX=" + x + "Y=" + y + "\tTile: " + t);
        }
    } 
    
    public static void main(String[] args) {
        setupLogger();

        LoggingExample example = new LoggingExample("xc7z020clg400-1");
        example.printDeviceInfo();
    }
}
