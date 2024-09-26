package placer;

import java.util.Random;
import java.util.Map;
// import java.util.Collection;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;

import com.xilinx.rapidwright.edif.EDIFNetlist;
import com.xilinx.rapidwright.edif.EDIFCell;
import com.xilinx.rapidwright.edif.EDIFCellInst;
import com.xilinx.rapidwright.edif.EDIFHierCellInst;
import com.xilinx.rapidwright.edif.EDIFPortInst;
import com.xilinx.rapidwright.edif.EDIFHierPortInst;
import com.xilinx.rapidwright.edif.EDIFNet;
import com.xilinx.rapidwright.edif.EDIFHierNet;

import com.xilinx.rapidwright.design.Design;
import com.xilinx.rapidwright.design.Cell;
import com.xilinx.rapidwright.design.Net;

import com.xilinx.rapidwright.device.SiteTypeEnum;
import com.xilinx.rapidwright.device.Site;
import com.xilinx.rapidwright.device.BEL;

public class CompleteRandomPlacer extends Placer {

    private final String rootDir = "/home/bcheng/workspace/dev/place-and-route/";

    public CompleteRandomPlacer() throws IOException {
        super();
    }

    public Design place(Design design) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(rootDir + "outputs/CompleteRandomPlacer.txt"));

        EDIFNetlist netlist = design.getNetlist();

        List<Cell> cells = new ArrayList<>();
        List<Net> nets = new ArrayList<>();

        List<EDIFHierCellInst> cellInstList = netlist.getAllLeafHierCellInstances();
        for (EDIFHierCellInst ehci : cellInstList) {
            cells.add(design.createCell(ehci.getFullHierarchicalInstName(), ehci.getInst()));
        }

        Map<EDIFHierNet, EDIFHierNet> edifNetMap = netlist.getParentNetMap();
        for (Map.Entry<EDIFHierNet, EDIFHierNet> entry : edifNetMap.entrySet()) {
            EDIFHierNet key = entry.getKey(); // Net Name
            EDIFHierNet val = entry.getValue(); // Net Parent
            nets.add(design.createNet(key));
            // If Name = Parent, then it means the net source comes from a primitive cell or
            // an I/O pad
            // If Name != Parent, then the net source comes from non-primitive hierarchical
            // cell
        }

        // HashSet<BEL> occupiedBELs = new HashSet<>();
        HashSet<Site> activeSites = new HashSet<>();

        HashMap<Site, HashSet<BEL>> map = new HashMap<>();
        Random rand = new Random();

        // Find compatible sites and BELs for each cell.
        for (Cell cell : cells) {
            writer.write("\nCell: " + cell.getName());
            Map<SiteTypeEnum, Set<String>> compatibleBELs = cell.getCompatiblePlacements(this.device);
            for (Map.Entry<SiteTypeEnum, Set<String>> entry : compatibleBELs.entrySet()) {
                SiteTypeEnum siteType = entry.getKey();
                Set<String> bels = entry.getValue();
                writer.write("\n\tSiteTypeEnum: " + siteType.name());
                for (String bel : bels) {
                    writer.write("\n\t\tBEL: " + bel);
                }
            }

            List<Map.Entry<SiteTypeEnum, Set<String>>> entryList = new ArrayList<>(compatibleBELs.entrySet());

            // Select a random bel.
            Site site;
            BEL bel;

            if (design.placeCell(cell, site, bel) == false)
                System.out.println(cell.getName() + "placement failed!");

        }

        // Net n = createNet(EDIFHierNet ehn);
        // Net n = createNet(String netName);
        // SitePinInst spi = net.connect(Cell c, String logicalPinName);
        // get the pins for each cell.

        if (writer != null)
            writer.close();
        return design;
    }

}

/*
 * // Create Cells from EDIFCellInsts
 * HashMap<String, EDIFCellInst> cellInstMap = netlist.generateCellInstMap();
 * for (Map.Entry<String, EDIFCellInst> entry : cellInstMap.entrySet()) {
 * String key = entry.getKey();
 * EDIFCellInst val = entry.getValue();
 * cells.add(design.createCell(key, val));
 * }
 *
 * // Cell c = createCell(String instName, EDIFCellInst instance);
 * // boolean b = design.placeCell(Cell c, Site site, BEL bel);
 * // returns true if placement successful or if already placed
 * 
 * HashMap<String, EDIFNet> edifNetMap =
 * netlist.generateEDIFNetMap(cellInstMap);
 * for (Map.Entry<String, EDIFNet> entry : edifNetMap.entrySet()) {
 * String key = entry.getKey();
 * EDIFNet val = entry.getValue();
 * nets.add(design.createNet(val)); // createNet only accepts EDIFHierNet...
 * }
 */
