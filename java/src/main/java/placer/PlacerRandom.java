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

import com.xilinx.rapidwright.device.Device;
import com.xilinx.rapidwright.device.SiteTypeEnum;
import com.xilinx.rapidwright.device.Site;
import com.xilinx.rapidwright.device.BEL;
import com.xilinx.rapidwright.device.BELPin;

public class PlacerRandom extends Placer {

    public PlacerRandom() throws IOException {
        super();
    }

    private void printAllCompatiblePlacements(BufferedWriter writer, Cell cell)
            throws IOException {
        writer.write("\n\tCompatible placements: ");
        Map<SiteTypeEnum, Set<String>> compatibleBELs = cell.getCompatiblePlacements(device);

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
        // design.flattenDesign();
        EDIFNetlist netlist = design.getNetlist();

        // CREATE AND PLACE CELLS
        List<EDIFHierCellInst> cellInstList = netlist.getAllLeafHierCellInstances();
        for (EDIFHierCellInst ehci : cellInstList) {

            Set<String> buffCells = new HashSet<>(Arrays.asList("IBUF", "OBUF"));
            if (buffCells.contains(ehci.getCellName())) {
                writer.write("\nIBUF/OBUF type already placed by constraints.");
                Cell buffCell = design.getCell(ehci.getFullHierarchicalInstName());
                writer.write("\n\tCell: " + buffCell.getName() + "\tplaced at Site: " + buffCell.getSite().getName());
                continue; // continue for-loop
            }

            Cell cell = design.createCell(ehci.getFullHierarchicalInstName(), ehci.getInst());
            Map<SiteTypeEnum, Set<String>> compatibleBELs = cell.getCompatiblePlacements(device);
            writer.write("\nPlacing Cell: " + cell.getName());
            printAllCompatiblePlacements(writer, cell);

            List<SiteTypeEnum> buffSiteTypes = new ArrayList<>();
            Collections.addAll(buffSiteTypes,
                    SiteTypeEnum.ILOGICE2,
                    SiteTypeEnum.ILOGICE3,
                    SiteTypeEnum.OLOGICE2,
                    SiteTypeEnum.OLOGICE3,
                    SiteTypeEnum.IOB18,
                    SiteTypeEnum.OPAD);
            compatibleBELs.keySet().removeAll(buffSiteTypes);

            Set<String> occupiedSiteBELs = new HashSet<>();
            int iterCount = 0;
            while (true) {
                Random rand = new Random();
                List<SiteTypeEnum> keys = new ArrayList<>(compatibleBELs.keySet());
                if (keys.isEmpty()) {
                    writer.write("\n\tWARNING: Cell: " + cell.getName()
                            + " has no compatible BELs on the device!");
                    break; // break while-loop
                }
                SiteTypeEnum selectedSiteType = keys.get(rand.nextInt(keys.size())); // Randomly selected SiteTypeEnum
                Site[] sites = device.getAllSitesOfType(selectedSiteType);
                if (sites.length == 0) {
                    writer.write(
                            "\n\tWARNING: SiteTypeEnum: " + selectedSiteType
                                    + " has no compatible sites on the device!");
                    break; // break while-loop
                }

                Set<String> randBELSet = compatibleBELs.get(selectedSiteType); // Randomly selected Set<String>
                List<String> randBELList = new ArrayList<>(randBELSet);
                String selectedBELName = randBELList.get(rand.nextInt(randBELList.size()));
                Site selectedSite = sites[rand.nextInt(sites.length)];
                BEL selectedBEL = selectedSite.getBEL(selectedBELName);
                if (occupiedSiteBELs.add(selectedSite.getName() + "_" + selectedBELName)) {
                    if (!design.placeCell(cell, selectedSite, selectedBEL)) {
                        writer.write("\n\tPLACEMENT FAILED!");
                        break; // break while-loop
                    }
                    List<EDIFHierPortInst> ehpis = ehci.getHierPortInsts();
                    for (EDIFHierPortInst ehpi : ehpis) {
                        Net newNet = design.createNet(ehpi.getHierarchicalNet());
                        writer.write("\n\tCreated Net: " + newNet.getName());
                    }
                    writer.write("\n\tPLACED CELL: ");
                    writer.write("\n\t\tBEL: " + cell.getBEL().getName());
                    writer.write("\n\t\tSite: " + cell.getSite().getName());
                    writer.write("\n\t\tSite Inst: " + cell.getSiteInst().getName());
                    writer.write("\n\t\tSiteTypeEnum: " + cell.getSiteInst().getSiteTypeEnum());
                    break; // break while-loop
                }

                if (iterCount == 100) {
                    writer.write(
                            "\n\tWARNING: Could not place cell: " + cell.getName() + " after 100 random selections!");
                    break; // break while-loop
                }
                iterCount++;

            } // end while-loop

            writer.newLine();

        } // end for (EDIFHierCellInst ehci : cellInstList)

        writer.newLine();
        writer.newLine();
        writer.newLine();

        if (writer != null)
            writer.close();

        return design;

    } // end place()
} // end class
