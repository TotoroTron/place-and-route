package src;

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
            FileHandler fileHandler = new FileHandler(rootDir.concat("outputs/logger.txt"), true); // 'true' appends to file
            fileHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(fileHandler);
            logger.setLevel(Level.ALL); // Set logging level to record all messages

            // BufferedWriter to print data.
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(rootDir.concat("outputs/output.txt")))) {
                RapidWrightBlockPlacer RWPlacer = new RapidWrightBlockPlacer();
                logger.log(Level.INFO, "Begin placement...\n");
                RWPlacer.init(writer);
                logger.log(Level.INFO, "Data writing complete. Check 'output.txt'");
            } catch (IOException e) {
                logger.log(Level.SEVERE, "An IOException occurred while writing data.", e);
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "An IOException occurred while configuring the logger.", e);
        }
    }
}
