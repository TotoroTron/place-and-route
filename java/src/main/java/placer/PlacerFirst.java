package placer;

import java.util.Random;
import java.util.EnumSet;
import java.util.Collections;
import java.util.Collection;
import java.util.Set;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.stream.Collectors;

import java.io.FileWriter;
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

public class PlacerFirst extends Placer {

    public PlacerFirst() throws IOException {
        super();
    }

    private void printAllCompatiblePlacements(FileWriter writer, Cell cell)
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

    private boolean isBufferCell(Design design, EDIFHierCellInst ehci) {
        // Filter out IBUF/OBUF cells. They are already placed by constraints.
        Set<String> buffCells = new HashSet<>(Arrays.asList("IBUF", "OBUF"));
        if (buffCells.contains(ehci.getCellName()))
            return true;
        else
            return false;
    }

    private void removeBufferTypes(Set<SiteTypeEnum> types) {
        List<SiteTypeEnum> buffSiteTypes = new ArrayList<>();
        Collections.addAll(buffSiteTypes,
                // FF Cells are reported to be "compatible" with these buffer sites
                SiteTypeEnum.ILOGICE2,
                SiteTypeEnum.ILOGICE3,
                SiteTypeEnum.OLOGICE2,
                SiteTypeEnum.OLOGICE3,
                SiteTypeEnum.IOB18,
                SiteTypeEnum.OPAD);
        types.removeAll(buffSiteTypes);
    }

    public Design place(Design design) throws IOException {
        FileWriter writer = new FileWriter(rootDir + "outputs/printout/PlacerRandom.txt");
        // design.flattenDesign();

        // CREATE AND PLACE CELLS
        writer.write("\nPlacing Cells...");

        // Set<String> occupiedBELs = new HashSet<>(); // <"SITE_X66Y77 AFF">
        Set<String[]> occupiedBELs = new HashSet<>();

        for (EDIFHierCellInst ehci : design.getNetlist().getAllLeafHierCellInstances()) {

            // Filter out IBUF/OBUF cells. They are already placed by constraints.
            if (isBufferCell(design, ehci))
                continue;

            // Create the Cell out of EDIFHierCellInst
            Cell cell = design.createCell(ehci.getFullHierarchicalInstName(), ehci.getInst());
            Map<SiteTypeEnum, Set<String>> compatiblePlacements = cell.getCompatiblePlacements(device);
            writer.write("\nPlacing Cell: " + cell.getName());

            // Select a SiteTypeEnum
            Iterator<SiteTypeEnum> iterator = compatiblePlacements.keySet().iterator();
            SiteTypeEnum selectedSiteType = iterator.next();
            // hacky way to get first elem of a set. not reliable.

            // Get all device site names with this SiteTypeEnum
            List<String> siteNames = Arrays.stream(device.getAllCompatibleSites(selectedSiteType))
                    .map(Site::getName) // Extract site names
                    .collect(Collectors.toList()); // Collect to Set

            //
            // TODO =======================================
            //
            removeBufferTypes(compatiblePlacements.keySet());
            String selectedBEL = device.getSite(siteNames.remove(0)).getBELs());
            if (design.placeCell(cell, selectedSite, selectedBEL)) {
                occupiedBELs.add(selectedSite.getName() + " " + selectedBEL.getName());
            } else {
                writer.write("\n\tPLACEMENT FAILED!");
                continue; // break for-loop
            }
            // 
            // TODO =======================================
            //

        } // end for (ehci)

        writer.write("Beginning Intra-Routing...");

        for (SiteInst si : design.getSiteInsts()) {
            // route the site normally
            si.routeSite();

            // does this site use a CARRY cell?
            // if so, we might need to route carry-in nets manually.
            Cell carryCell = si.getCells().stream()
                    .filter(cell -> cell.getBEL() != null)
                    .filter(cell -> cell.getBEL().isCarry())
                    .findFirst()
                    .orElse(null);
            if (carryCell != null) {
                writer.write("\nFound CARRY cell.");
                BELPin[] belpins = carryCell.getBEL().getPins();
                for (BELPin bp : belpins) {
                    writer.write("\nBELPin: " + bp.getName());
                    writer.write("\n\tSource BELPin: " + bp.getSourcePin());
                    for (BELPin siteConn : bp.getSiteConns()) {
                        writer.write("\n\tsiteConn: " + siteConn.getName());
                    }
                }

                // if this CARRY4 is the first in a carry chain...
                Net cinNet = si.getNetFromSiteWire("CIN");
                if (cinNet.isGNDNet()) {
                    // manually remove CIN pin from the GND Net...
                    // otherwise, routing will complain that CIN is unreachable
                    cinNet.removePin(si.getSitePinInst("CIN"));
                    BELPin cinPin = si.getBELPin("CARRY4", "CIN");
                    si.unrouteIntraSiteNet(cinPin.getSourcePin(), cinPin);
                }
            }

            Cell ffCell = si.getCells().stream()
                    .filter(cell -> cell.getBEL() != null)
                    .filter(cell -> cell.getBEL().isFF())
                    .findFirst()
                    .orElse(null);
            if (ffCell != null) {
                Net srNet = si.getNetFromSiteWire("SRUSEDMUX_OUT");
                if (!srNet.isGNDNet()) {
                    // srNet.addPin(si.getSitePinInst("SR"));
                    BELPin srPin = ffCell.getBEL().getPin("SR");
                    si.unrouteIntraSiteNet(srPin.getSourcePin(), srPin);
                    si.routeIntraSiteNet(srNet, si.getBELPin("SRUSEDMUX", "IN"), srPin);
                }
            }
        }

        if (writer != null)
            writer.close();
        return design;

    } // end place()

} // end class
