import com.xilinx.rapidwright.device.*;
import com.xilinx.rapidwright.design.*;
import com.xilinx.rapidwright.placer.blockplacer.BlockPlacer;

import java.io.IOException;
import java.io.BufferedWriter;
import java.io.FileWriter;

import java.util.List;
import java.util.Collection;
import java.util.logging.Logger;
import java.util.logging.Level;


public class Main {

    private static final Logger logger = Logger.getLogger(BufferedWriterExample.class.getName());

    public static void main(String[] args) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("logger.txt"))) {
            device = Device.getDevice("xc7z020clg400-1");
            RapidWriteBlockPlacer.place(writer);
            logger.log(Level.INFO, "Writing complete. Check 'logger.txt'");
        } catch (IOException e) {
            logger.log(Level.SEVERE, "An IOException occurred.", e);
        }
    }

}
