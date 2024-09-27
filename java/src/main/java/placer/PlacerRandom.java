package placer;

import java.util.Random;
import java.util.EnumSet;
import java.util.Collections;
import java.util.Collection;
import java.util.Map;
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

public class PlacerRandom extends Placer {
    private final String rootDir = "/home/bcheng/workspace/dev/place-and-route/";

    public PlacerRandom() throws IOException {
        super();
    }

    public Design place(Design design) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(rootDir + "outputs/PlacerRandom.txt"));
        EDIFNetlist netlist = design.getNetlist();
        List<Cell> cells = new ArrayList<>();
        List<Net> nets = new ArrayList<>();

        Map<EDIFHierNet, EDIFHierNet> edifNetMap = netlist.getParentNetMap();
        List<EDIFHierCellInst> cellInstList = netlist.getAllLeafHierCellInstances();

        // CREATE AND PLACE CELLS
        for (EDIFHierCellInst ehci : cellInstList) {

            Cell cell = design.createCell(ehci.getFullHierarchicalInstName(), ehci.getInst());
            Map<SiteTypeEnum, Set<String>> compatibleBELs = cell.getCompatiblePlacements(device);
            writer.write("\nPlacing Cell: " + cell.getName());

            if (!ehci.isTopLevelInst()) {
                // If the EDIFHierCellInst is a Flip Flop type, getCompatiblePlacements will say
                // that I/OLOGIC is compatible with it.
                List<SiteTypeEnum> bufferTypes = new ArrayList<>();
                Collections.addAll(bufferTypes,
                        SiteTypeEnum.ILOGICE2,
                        SiteTypeEnum.ILOGICE3,
                        SiteTypeEnum.OLOGICE2,
                        SiteTypeEnum.OLOGICE3);
                // for (SiteTypeEnum ste : compatibleBELs.keySet()) {}
                compatibleBELs.keySet().removeAll(bufferTypes);
            }

            Set<String> occupiedSiteBELs = new HashSet<>();
            int iterCount = 0;
            while (true) {
                Random rand = new Random();
                List<SiteTypeEnum> keys = new ArrayList<>(compatibleBELs.keySet());
                SiteTypeEnum selectedSiteType = keys.get(rand.nextInt(keys.size())); // Randomly selected SiteTypeEnum

                Set<String> randBELSet = compatibleBELs.get(selectedSiteType); // Randomly selected Set<String>
                List<String> randBELList = new ArrayList<>(randBELSet);
                String selectedBELName = randBELList.get(rand.nextInt(randBELList.size()));

                Site[] sites = device.getAllSitesOfType(selectedSiteType);
                System.out.println("Cell: " + cell.getName());
                System.out.println("Sites length: " + sites.length);
                Site selectedSite = sites[rand.nextInt(sites.length)];
                BEL selectedBEL = selectedSite.getBEL(selectedBELName);

                if (occupiedSiteBELs.add(selectedSite.getName() + "_" + selectedBELName)) {
                    // Unique siteBEL pair successfully added to Set
                    design.placeCell(cell, selectedSite, selectedBEL);
                    writer.write("Placed cell: " + cell.getName() + " at site: "
                            + selectedSite.getName() + " at BEL: " + selectedBELName);
                    break;
                }
                iterCount++;
                if (iterCount == 100) {
                    writer.write("\nCould not place cell: " + cell.getName() + " after 100 random selections!");
                    break;
                }
            }

        }

        return design;
    }
}
