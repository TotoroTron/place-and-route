import java.util.logging.Logger;
import java.util.logging.FileHandler;
import java.util.logging.SimpleFormatter;
import java.util.logging.Level;

import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;


public class Main {

    private static final Logger logger = Logger.getLogger(Main.class.getName());
    protected static final String rootDir = "/home/bcheng/workspace/dev/place-and-route/";

    public static void main(String[] args) {
        try {
            // Logger to keep track of execution progress.
            FileHandler fileHandler = new FileHandler(rootDir+"outputs/logger.txt", true); // 'true' appends to file
            fileHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(fileHandler);
            logger.setLevel(Level.ALL); // Set logging level to record all messages

            logger.log(Level.INFO, "Begin SimulatedAnnealingPlacer...");
            SimulatedAnnealingPlacer SAPlacer = new SimulatedAnnealingPlacer();
            SAPlacer.run();
            logger.log(Level.INFO, "Data writing complete. Check 'output.txt'");
            
        } catch (IOException e) {
            logger.log(Level.SEVERE, "An IOException occurred while configuring the logger.", e);
        }
    }
}
