package placer;

import java.util.logging.Logger;
import java.util.logging.FileHandler;
import java.util.logging.SimpleFormatter;
import java.util.logging.Level;

import java.io.IOException;

import com.xilinx.rapidwright.design.ModuleInst;
import com.xilinx.rapidwright.design.Module;
import com.xilinx.rapidwright.design.Design;
import com.xilinx.rapidwright.device.Device;

import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collection;
import com.xilinx.rapidwright.design.SiteInst;
import com.xilinx.rapidwright.device.Site;
import com.xilinx.rapidwright.device.BEL;
import com.xilinx.rapidwright.device.SiteTypeEnum;

public class Main {

    private static final Logger logger = Logger.getLogger(Main.class.getName());
    protected static final String rootDir = "/home/bcheng/workspace/dev/place-and-route";
    protected static final String synthesizedDcp = rootDir + "/outputs/synthesized.dcp";

    public static void main(String[] args) {
        try {
            // Logger to keep track of execution progress.
            FileHandler fileHandler = new FileHandler(rootDir + "/outputs/logger.log", true); // 'true' appends to file
            fileHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(fileHandler);
            logger.setLevel(Level.ALL); // Set logging level to record all messages
            logger.log(Level.INFO, "Begin Placer...");

            Design design = Design.readCheckpoint(synthesizedDcp);
            Device device = Device.getDevice("xc7z020clg400-1");
            testSiteInst();
            testModuleInst();

            // PackerBasic BPacker = new PackerBasic(rootDir, design, device);
            // PackedDesign packedDesign = BPacker.run();

            // PlacerSiteCentric SCPlacer = new PlacerSiteCentric(rootDir, design, device);
            // SCPlacer.printUniqueSites();
            // SCPlacer.run(packedDesign);

            // ViewVivadoCheckpoint ViewVivado = new ViewVivadoCheckpoint();
            // ViewVivado.run();

        } catch (IOException e) {
            logger.log(Level.SEVERE, "An IOException occurred while configuring the logger.", e);
        }
    }

    public static void testModuleInst() throws IOException {
        Design design = Design.readCheckpoint(rootDir + "/outputs/vivado_placed.dcp");
        Collection<ModuleInst> mis = design.getModuleInsts();
        if (mis.isEmpty()) {
            System.out.println("Collection<ModuleInst> empty!");
        } else {
            for (ModuleInst mi : mis) {
                System.out.println("\tSites in ModuleImpl: " + mi.getSiteInsts());
                for (SiteInst si : mi.getSiteInsts()) {
                    System.out.println("SiteInst: " + si.getName());
                }
            }
        }

        Collection<Module> modules = new ArrayList<>();
        Module module = new Module(design);
        modules.add(module);
        for (Module m : modules) {
            System.out.println("\tSites in Module: " + m.getName());
            for (SiteInst si : m.getSiteInsts()) {
                System.out.println("SiteInst: " + si.getName());
            }

        }

    }

    public static void testSiteInst() throws IOException {
        SiteInst si = new SiteInst("SiteInst si = new SiteInst(String name, SiteTypeEnum type)", SiteTypeEnum.SLICEL);
        System.out.println("\n" + si.getName());

        /*
         * BEL[] bels = si.getBELs();
         * performing this produces the exception below:
         *
         * Exception in thread "main" java.lang.NullPointerException: Cannot invoke
         * "com.xilinx.rapidwright.device.j.a()" because the return value of
         * "com.xilinx.rapidwright.design.SiteInst.a()" is null
         * at com.xilinx.rapidwright.design.SiteInst.getBELs(Unknown Source)
         */

        // BEL[] bels = si.getBELs();
        // if (bels == null)
        // System.out.println("\tBEL[] Null!");
        // for (BEL bel : Arrays.asList(bels)) {
        // System.out.println("\tBEL: " + bel.getName());
        // }

        // SiteInst si = new SiteInst("MySiteInst", design, SiteTypeEnum.SLICEL,
        // device.getSite("SLICE_X32Y149"));
    };
}
