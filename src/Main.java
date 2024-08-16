package src;

import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;

import java.util.logging.Logger;
import java.util.logging.Level;

import java.util.List;
import java.util.Collection;

import com.xilinx.rapidwright.device.*;
import com.xilinx.rapidwright.design.*;

public class Main {

    private static final Logger logger = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("logger.txt"))) {
            RapidWrightBlockPlacer RWPlacer = new RapidWrightBlockPlacer();
            RWPlacer.init(writer);
            logger.log(Level.INFO, "Writing complete. Check 'logger.txt'");
        } catch (IOException e) {
            logger.log(Level.SEVERE, "An IOException occurred.", e);
        }
    }

}
