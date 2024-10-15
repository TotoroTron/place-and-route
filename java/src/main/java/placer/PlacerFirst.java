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

    private void addToMap(Map<String, List<String>> map, String key, String value) {
        map.computeIfAbsent(key, k -> new ArrayList<>()).add(value);
    }

    private void printMap(Map<String, List<String>> map) {
        for (Map.Entry<String, List<String>> entry : map.entrySet()) {
            String siteName = entry.getKey();
            List<String> occupiedBELs = entry.getValue();
            System.out.println("\tSite: " + siteName);
            for (String bel : occupiedBELs) {
                System.out.println("\t\tBEL: " + bel);
            }
        }
        System.out.println();
    }

    public Design place(Design design) throws IOException {
        FileWriter writer = new FileWriter(rootDir + "outputs/printout/PlacerRandom.txt");
        // design.flattenDesign();

        // CREATE AND PLACE CELLS
        writer.write("\nPlacing Cells...");

        Map<String, List<String>> occupiedPlacements = new HashMap<>();
        // a "placement" consists of a site-BEL pair

        for (EDIFHierCellInst ehci : design.getNetlist().getAllLeafHierCellInstances()) {

            // Filter out IBUF/OBUF cells. They are already placed by constraints.
            if (isBufferCell(design, ehci))
                continue; // continue for-loop

            // Create the Cell out of EDIFHierCellInst
            Cell cell = design.createCell(ehci.getFullHierarchicalInstName(), ehci.getInst());
            Map<SiteTypeEnum, Set<String>> compatiblePlacements = cell.getCompatiblePlacements(device);

            if (compatiblePlacements.isEmpty()) {
                writer.write("\n\tWARNING: Cell: " + cell.getName() + " of type: " + cell.getType()
                        + " has no compatible placements!");
                continue;
            }

            // printAllCompatiblePlacements(writer, cell);

            // Remove Buffer SiteType
            removeBufferTypes(compatiblePlacements.keySet());

            // Select a SiteType
            // hacky way to get "first" elem of a set. not reliable.
            Iterator<SiteTypeEnum> iterator = compatiblePlacements.keySet().iterator();
            SiteTypeEnum selectedSiteType = iterator.next();

            if (device.getAllSitesOfType(selectedSiteType).length == 0) {
                writer.write("\n\tWARNING: SiteTypeEnum: " + selectedSiteType +
                        " has no compatible sites!");
                continue;
            }

            // Get all device site names of selected SiteType
            List<String> siteNames = Arrays.stream(device.getAllCompatibleSites(selectedSiteType))
                    .map(Site::getName) // return as string names only, not the site itself
                    .collect(Collectors.toList()); // collect as list

            // Get all bel names in the selected site
            List<String> belNames = compatiblePlacements.get(selectedSiteType).stream()
                    .filter(name -> !name.contains("5FF"))
                    .collect(Collectors.toList());

            Map<String, List<String>> availablePlacements = new HashMap<>();
            for (String siteName : siteNames) {
                availablePlacements.put(siteName, new ArrayList<>(belNames));
            }

            System.out.println("Printing occupiedPlacements...");
            printMap(occupiedPlacements);
            // System.out.println("Printing availablePlacements...");

            // printMap(availablePlacements);

            // Remove occupiedPlacements from availablePlacements
            for (Map.Entry<String, List<String>> entry : occupiedPlacements.entrySet()) {
                String siteName = entry.getKey();
                List<String> occupiedBELs = entry.getValue();

                // Only one BEL per site...
                availablePlacements.remove(siteName);

                // BEL PACKING VERSION (WILL CAUSE ILLEGAL PLACEMENT)
                // if (availablePlacements.containsKey(siteName)) {
                // availablePlacements.get(siteName).removeAll(occupiedBELs);
                // if (availablePlacements.get(siteName).isEmpty()) {
                // availablePlacements.remove(siteName);
                // }
                // }
            }

            if (availablePlacements.isEmpty()) {
                String s1 = String.format("\nWARNING: Cell: $-40s has no available placements!",
                        cell.getName());
                System.out.println(s1);
                writer.write(s1);
                continue;
            }

            // Select first site and in first site, first BEL
            String selectedSiteName = availablePlacements.keySet().iterator().next();
            String selectedBELName = availablePlacements.get(selectedSiteName).get(0);

            Site selectedSite = device.getSite(selectedSiteName);
            BEL selectedBEL = selectedSite.getBEL(selectedBELName);

            String s1 = String.format(
                    "\nCell: %-40s, EDIFCellType: %-10s, cellType: %-10s, SiteType: %-10s, Site: %-10s, BEL: %-10s",
                    cell.getName(), ehci.getCellType(), cell.getType(), selectedSiteType, selectedSiteName,
                    selectedBELName);
            writer.write(s1);
            if (design.placeCell(cell, selectedSite, selectedBEL)) {
                writer.write("\n\tPlacement success!");
                addToMap(occupiedPlacements, selectedSiteName, selectedBELName);
            } else {
                writer.write("\n\tWARNING: Placement Failed!");
            }

            //
        } // end for (ehci)

        writer.write("\n\nBeginning Intra-Routing...");

        for (SiteInst si : design.getSiteInsts()) {
            // route the site normally
            si.routeSite();

            writer.write("\nCells in site: " + si.getName());
            for (Cell cell : si.getCells()) {
                if (cell.getBEL() != null) {
                    writer.write("\n\tCellName : " + cell.getName() + ", CellType: " + cell.getType() +
                            ", BELName: " + cell.getBELName() + ", BELType: " + cell.getBEL().getBELType());
                } else {
                    writer.write("\n\tNull!");
                }
            }

            // does this site use a CARRY cell?
            // if so, we might need to route carry-in nets manually.
            Cell carryCell = si.getCells().stream()
                    .filter(cell -> cell.getBEL() != null)
                    .filter(cell -> cell.getBEL().isCarry())
                    .findFirst()
                    .orElse(null);
            if (carryCell != null) {
                writer.write("\nFound CARRY cell.");
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
                    .filter(cell -> cell.getBEL().getBELType().contains("REG_INIT"))
                    .findFirst()
                    .orElse(null);
            if (ffCell != null) {
                writer.write("\nFound FF cell.");
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
