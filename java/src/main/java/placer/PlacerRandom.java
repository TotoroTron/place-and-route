package placer;

import java.util.Random;
import java.util.EnumSet;
import java.util.Collections;
import java.util.Collection;
import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.Arrays;
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
import com.xilinx.rapidwright.design.SitePinInst;
import com.xilinx.rapidwright.design.SiteInst;

import com.xilinx.rapidwright.device.SiteTypeEnum;
import com.xilinx.rapidwright.device.Site;
import com.xilinx.rapidwright.device.BEL;

public class PlacerRandom extends Placer {
    private final String rootDir = "/home/bcheng/workspace/dev/place-and-route/";

    public PlacerRandom() throws IOException {
        super();
    }

    private void printAllCompatiblePlacements(BufferedWriter writer, Cell cell)
            throws IOException {
        writer.write("\n\tCompatible placements: ");
        Map<SiteTypeEnum, Set<String>> compatibleBELs = cell.getCompatiblePlacements(this.device);

        for (Map.Entry<SiteTypeEnum, Set<String>> entry : compatibleBELs.entrySet()) {
            SiteTypeEnum siteType = entry.getKey();
            Set<String> belNames = entry.getValue();

            writer.write("\n\t\tSiteTypeEnum: " + siteType.name());
            Site[] sites = device.getAllSitesOfType(siteType);

            for (String bel : belNames)
                writer.write("\n\t\t\tBEL: " + bel);
            if (sites.length == 0) {
                writer.write("\n\t\t\tSites: None!");
                continue;
            }
            if (sites.length > 10) {
                writer.write("\n\t\t\t" + sites.length + " compatible sites.");
                continue;
            }
            for (Site site : sites)
                writer.write("\n\t\t\tSite: " + site.getName());
        }
        return;
    }

    public Design place(Design design) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(rootDir + "outputs/PlacerRandom.txt"));
        EDIFNetlist netlist = design.getNetlist();

        // CREATE AND PLACE CELLS
        List<EDIFHierCellInst> cellInstList = netlist.getAllLeafHierCellInstances();
        for (EDIFHierCellInst ehci : cellInstList) {

            Cell cell = design.createCell(ehci.getFullHierarchicalInstName(), ehci.getInst());
            Map<SiteTypeEnum, Set<String>> compatibleBELs = cell.getCompatiblePlacements(device);

            writer.write("\nPlacing Cell: " + cell.getName());
            printAllCompatiblePlacements(writer, cell);

            if (ehci.isTopLevelInst()) {
            }

            Set<String> skipCells = new HashSet<>(Arrays.asList("IBUF", "OBUF"));
            if (skipCells.contains(ehci.getCellName())) {
                // IBUFs and OBUFs are already placed by the constraints xdc file.
                continue;
            }

            List<SiteTypeEnum> sitelessTypes = new ArrayList<>();
            Collections.addAll(sitelessTypes,
                    SiteTypeEnum.ILOGICE2,
                    SiteTypeEnum.ILOGICE3,
                    SiteTypeEnum.OLOGICE2,
                    SiteTypeEnum.OLOGICE3,
                    SiteTypeEnum.IOB18,
                    SiteTypeEnum.OPAD
            // SiteTypeEnum.IPAD has sites
            );
            compatibleBELs.keySet().removeAll(sitelessTypes);

            Set<String> occupiedSiteBELs = new HashSet<>();
            int iterCount = 0;
            while (true) {
                Random rand = new Random();
                List<SiteTypeEnum> keys = new ArrayList<>(compatibleBELs.keySet());

                if (keys.isEmpty()) {
                    writer.write("\n\tCell: " + cell.getName() + " has no compatible BELs!");
                    break;
                }

                SiteTypeEnum selectedSiteType = keys.get(rand.nextInt(keys.size())); // Randomly selected SiteTypeEnum
                Site[] sites = device.getAllSitesOfType(selectedSiteType);

                if (sites.length == 0) {
                    writer.write(
                            "\n\tSiteTypeEnum: " + selectedSiteType + " has no compatible sites on the device!");
                    break;
                }

                Set<String> randBELSet = compatibleBELs.get(selectedSiteType); // Randomly selected Set<String>
                List<String> randBELList = new ArrayList<>(randBELSet);
                String selectedBELName = randBELList.get(rand.nextInt(randBELList.size()));

                Site selectedSite = sites[rand.nextInt(sites.length)];
                BEL selectedBEL = selectedSite.getBEL(selectedBELName);

                if (occupiedSiteBELs.add(selectedSite.getName() + "_" + selectedBELName)) {
                    design.placeCell(cell, selectedSite, selectedBEL);
                    writer.write("\n\tPLACED CELL: ");
                    writer.write("\n\t\tBEL: " + selectedBELName);
                    writer.write("\n\t\tSite: " + selectedSite.getName());

                    System.out.println("\tPLACED CELL: ");
                    System.out.println("\t\tBEL: " + selectedBELName);
                    System.out.println("\t\tSite: " + selectedSite.getName());
                    cell.getSiteInst().routeSite();
                    break;
                }
                if (iterCount == 100) {
                    writer.write("\n\tCould not place cell: " + cell.getName() + " after 100 random selections!");
                    break;
                }
                iterCount++;
            }
            writer.newLine();

        } // end for (EDIFHierCellInst ehci : cellInstList)

        writer.newLine();
        writer.newLine();
        writer.newLine();

        // writer.write("\nRouting Intra-Site Connections...");
        // // ROUTE INTRA-SITE CONNECTIONS
        // for (Cell cell : design.getCells()) {
        // Set<String> skipCells = new HashSet<>(Arrays.asList("IBUF", "OBUF"));
        // if (skipCells.contains(cell.getEDIFCellInst().getCellName())) {
        // // IBUFs and OBUFs are already placed by the constraints xdc file.
        // continue;
        // }
        // System.out.println("\tCell: " + cell.getName());
        // SiteInst si = cell.getSiteInst();
        // writer.write("\n\tSiteInst: " + si.getName());
        // System.out.println(" SiteInst: " + si.getName());
        // cell.getSiteInst().routeSite();
        // }

        writer.newLine();
        writer.newLine();
        writer.newLine();

        // CREATE NETS
        Map<EDIFHierNet, EDIFHierNet> edifNetMap = netlist.getParentNetMap();
        for (EDIFHierNet ehn : edifNetMap.keySet()) {
            Net net = design.createNet(ehn);
            // writer.write("\nNet: " + net.getName());
            // System.out.println("Net: " + net.getName());
            // List<SitePinInst> spis = net.getPins();
            // for (SitePinInst spi : spis) {
            // writer.write("\n\tSitePinInst: " + spi.getName());
            // System.out.println("\tSitePinInst: " + spi.getName());

            // }
        }

        if (writer != null)
            writer.close();
        return design;

    } // end place()
} // end class

// String ehciName = ehci.getCellName();
// if (ehciName == "OBUF" || ehciName == "IBUF") {
// writer.write("\n\tOBUF/IBUF cell has no corresponding SiteTypeEnum!");
// writer.write("\n\tis Top level? " + ehci.isTopLevelInst());
// break;
// }

// if (!ehci.isTopLevelInst()) {
// // If the EDIFHierCellInst is a Flip Flop type, getCompatiblePlacements will
// say
// // that I/OLOGIC is compatible with it.
// List<SiteTypeEnum> bufferTypes = new ArrayList<>();
// Collections.addAll(bufferTypes,
// SiteTypeEnum.ILOGICE2,
// SiteTypeEnum.ILOGICE3,
// SiteTypeEnum.OLOGICE2,
// SiteTypeEnum.OLOGICE3);
// // for (SiteTypeEnum ste : compatibleBELs.keySet()) {}
// compatibleBELs.keySet().removeAll(bufferTypes);
// }
