package placer;

import java.util.Random;
import java.util.EnumSet;
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

        // CREATE NETS
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

        // CREATE CELLS
        List<EDIFHierCellInst> cellInstList = netlist.getAllLeafHierCellInstances();

        for (EDIFHierCellInst ehci : cellInstList) {
            // boolean isTopLevel ehci.isTopLevelInst();
            //
            // can use this for arbitrating SLICE vs I/OLOGICE ?
            //
            cells.add(design.createCell(ehci.getFullHierarchicalInstName(), ehci.getInst()));
        }

        // PRINT COMPATIBLE PLACEMENTS FOR EACH CELL
        for (Cell cell : cells) {
            writer.write("\nCell: " + cell.getName());
            Map<SiteTypeEnum, Set<String>> compatibleBELs = cell.getCompatiblePlacements(this.device);
            for (Map.Entry<SiteTypeEnum, Set<String>> entry : compatibleBELs.entrySet()) {
                SiteTypeEnum siteType = entry.getKey();
                Set<String> belNames = entry.getValue();
                writer.write("\n\tSiteTypeEnum: " + siteType.name());
                for (String bel : belNames) {
                    writer.write("\n\t\tBEL: " + bel);
                }
            }
        }

        // ASSIGN RANDOM SITE AND BEL TO EACH CELL
        writer.newLine();
        writer.newLine();
        writer.newLine();

        Random rand = new Random();
        Set<String> assignedBELs = new HashSet<>();

        for (Cell cell : cells) {
            writer.write("\nCell : " + cell.getName());

            Map<SiteTypeEnum, Set<String>> compatibleBELs = cell.getCompatiblePlacements(this.device);
            List<Map.Entry<SiteTypeEnum, Set<String>>> entryList = new ArrayList<>(compatibleBELs.entrySet());

            boolean uniqueBELFound = false;

            while (!uniqueBELFound && !entryList.isEmpty()) {

                if (entryList.isEmpty()) {
                    writer.write("\n\tcompatibleBELs list is empty!");
                    continue;
                }

                Map.Entry<SiteTypeEnum, Set<String>> randomEntry = entryList.get(rand.nextInt(entryList.size()));
                SiteTypeEnum randomSiteType = randomEntry.getKey();
                Set<String> belNames = randomEntry.getValue();

                // If the cell is not a top level cell, do not assign it to a buffer site
                if (!cell.getEDIFHierCellInst().isTopLevelInst()) {
                    //
                    // this does not cover the situation where buffer sites are not in entryList
                    //
                    entryList.remove(SiteTypeEnum.ILOGICE2);
                    entryList.remove(SiteTypeEnum.ILOGICE3);
                    entryList.remove(SiteTypeEnum.OLOGICE2);
                    entryList.remove(SiteTypeEnum.OLOGICE3);
                }

                if (belNames.isEmpty()) {
                    writer.write("\n\tbelNames list is empty!");
                    continue;
                }

                List<String> availableBELs = new ArrayList<>();
                for (String bel : belNames) {
                    if (!assignedBELs.contains(bel)) {
                        availableBELs.add(bel);
                    }
                }

                if (availableBELs.isEmpty()) {
                    entryList.remove(randomEntry);
                    continue;
                }

                String randomBEL = availableBELs.get(rand.nextInt(availableBELs.size()));
                assignedBELs.add(randomBEL);

                writer.write("\n\tAssigned SiteType: " + randomSiteType);
                writer.write("\n\tAssigned BEL: " + randomBEL);

                uniqueBELFound = true;

                design.placeCell(cell, randomSiteType, device.getBEL(randomSiteType, randomBEL));

            } // end while (!uniqueBELFound && !entryList.isEmpty())

        } // end for (Cell cell : cells)

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
