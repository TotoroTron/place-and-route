
package placer;

import java.util.stream.Collectors;

import org.python.antlr.PythonParser.else_clause_return;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.LinkedHashMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Random;

import java.io.FileWriter;
import java.io.IOException;

import com.xilinx.rapidwright.design.Cell;
import com.xilinx.rapidwright.design.SiteInst;

import com.xilinx.rapidwright.edif.EDIFNetlist;
import com.xilinx.rapidwright.edif.EDIFHierNet;
import com.xilinx.rapidwright.edif.EDIFHierCellInst;
import com.xilinx.rapidwright.edif.EDIFHierPortInst;
import com.xilinx.rapidwright.edif.EDIFCellInst;

import com.xilinx.rapidwright.device.BEL;
import com.xilinx.rapidwright.device.Site;
import com.xilinx.rapidwright.device.SiteTypeEnum;
import com.xilinx.rapidwright.device.Tile;

public class PlacerPackingSiteCentric extends Placer {

    public PlacerPackingSiteCentric() throws IOException {
        super();
        placerName = "PlacerPackingHier";
        writer = new FileWriter(rootDir + "outputs/printout/" + placerName + ".txt");
        writer.write(placerName + ".txt");
    }

    protected SiteTypeEnum selectSiteType(
            Map<SiteTypeEnum, Set<String>> compatiblePlacements) throws IOException {

        Iterator<SiteTypeEnum> iterator = compatiblePlacements.keySet().iterator();
        SiteTypeEnum selectedSiteType = iterator.next();
        if (device.getAllSitesOfType(selectedSiteType).length == 0)
            return null;
        return selectedSiteType;
    }

    protected String[] selectSiteAndBEL(
            Map<String, List<String>> availablePlacements) throws IOException {

        // Random random = new Random();
        // List<String> availableSiteNames = new
        // ArrayList<>(availablePlacements.keySet());
        // String selectedSiteName =
        // availableSiteNames.get(random.nextInt(availableSiteNames.size()));

        // List<String> availableBELNames = availablePlacements.get(selectedSiteName);
        // String selectedBELName =
        // availableBELNames.get(random.nextInt(availableBELNames.size()));

        // Select first site and in first site, first BEL
        String selectedSiteName = availablePlacements.keySet().iterator().next();
        String selectedBELName = availablePlacements.get(selectedSiteName).get(0);

        return new String[] { selectedSiteName, selectedBELName };
    }

    protected void removeOccupiedPlacements(
            Map<String, List<String>> availablePlacements,
            Map<String, List<String>> occupiedPlacements) throws IOException {
        for (Map.Entry<String, List<String>> entry : occupiedPlacements.entrySet()) {
            String siteName = entry.getKey();
            List<String> occupiedBELs = entry.getValue();

            // SINGLE BEL PER SITE
            availablePlacements.remove(siteName);

            // // BEL PACKING (CAUSES ILLEGAL PLACEMENT FOR FIRST/RANDOM PLACER)
            // if (availablePlacements.containsKey(siteName)) {
            // availablePlacements.get(siteName).removeAll(occupiedBELs);
            // if (availablePlacements.get(siteName).isEmpty())
            // availablePlacements.remove(siteName);
            // }
        }
    }

    private List<List<CarryCellGroup>> findCarryChains(
            Map<String, List<EDIFHierCellInst>> EDIFCellGroups) {
        List<String> OPorts = new ArrayList<>(List.of("O[0]", "O[1]", "O[2]", "O[3]"));
        List<String> SPorts = new ArrayList<>(List.of("S[0]", "S[1]", "S[2]", "S[3]"));
        List<List<CarryCellGroup>> carryCellGroupChains = new ArrayList<>();
        // find the carry chain anchor
        while (!EDIFCellGroups.get("CARRY4").isEmpty()) { // iterating over chains
            List<EDIFHierCellInst> chain = new ArrayList<>();
            EDIFHierCellInst currCell = EDIFCellGroups.get("CARRY4").get(0);

            // every iteration, EDIFCarryCells gets updated so .get(0) is different.
            while (true) {
                EDIFHierPortInst currCellPort = currCell.getPortInst("CI");
                EDIFHierNet hnet = currCellPort.getHierarchicalNet();
                if (hnet.getNet().isGND())
                    break;
                Collection<EDIFHierPortInst> netPorts = hnet.getPortInsts();
                Map<String, EDIFHierPortInst> netPortsMap = netPorts.stream()
                        .collect(Collectors.toMap(
                                p -> p.getPortInst().getName(),
                                p -> p));
                EDIFHierPortInst sourceCellPort = netPortsMap.get("CO[3]");
                EDIFHierCellInst sourceCell = sourceCellPort.getHierarchicalInst()
                        .getChild(sourceCellPort.getPortInst().getCellInst().getName());
                currCell = sourceCell;
            }

            // we now have the starting carry cell as currCell
            // now traverse in the cout direction
            // the end of the chain occurs when portinst CO[3] is null
            List<CarryCellGroup> carryCellGroups = new ArrayList<>();

            while (true) { // iterating through the chain itself
                chain.add(currCell); // currCell = carry cell
                CarryCellGroup carryCellGroup = new CarryCellGroup(currCell, new ArrayList<>(), new ArrayList<>());

                // assemble the carry site cells
                for (int i = 0; i < 4; i++) {
                    EDIFHierNet ONet = currCell.getPortInst(OPorts.get(i)).getHierarchicalNet();
                    EDIFHierPortInst ONetSink = ONet.getLeafHierPortInsts(false, true).get(0);
                    EDIFHierCellInst ONetSinkCell = ONetSink.getHierarchicalInst()
                            .getChild(ONetSink.getPortInst().getCellInst().getName());
                    if (ONetSinkCell.getCellType().getName().contains("FDRE"))
                        carryCellGroup.ffs().add(ONetSinkCell);
                    else
                        carryCellGroup.ffs().add(null);

                    EDIFHierNet SNet = currCell.getPortInst(SPorts.get(i)).getHierarchicalNet();
                    EDIFHierPortInst SNetSource = SNet.getLeafHierPortInsts(true, false).get(0);
                    EDIFHierCellInst SNetSourceCell = SNetSource.getHierarchicalInst()
                            .getChild(SNetSource.getPortInst().getCellInst().getName());
                    if (SNetSourceCell.getCellType().getName().contains("LUT"))
                        carryCellGroup.luts().add(SNetSourceCell);
                    else
                        carryCellGroup.luts().add(null);
                }
                carryCellGroups.add(carryCellGroup);
                EDIFCellGroups.get("CARRY4").remove(carryCellGroup.carry());
                EDIFCellGroups.get("FDRE").removeAll(carryCellGroup.ffs());
                EDIFCellGroups.get("LUT").removeAll(carryCellGroup.luts());

                // find the next carry cell
                EDIFHierPortInst currCellPort = currCell.getPortInst("CO[3]");
                if (currCellPort == null)
                    break;
                EDIFHierNet hnet = currCellPort.getHierarchicalNet();
                Collection<EDIFHierPortInst> netPorts = hnet.getPortInsts();
                Map<String, EDIFHierPortInst> netPortsMap = netPorts.stream()
                        .collect(Collectors.toMap(
                                p -> p.getPortInst().getName(),
                                p -> p));
                EDIFHierPortInst sinkCellPort = netPortsMap.get("CI");
                EDIFHierCellInst sinkCell = sinkCellPort.getHierarchicalInst()
                        .getChild(sinkCellPort.getPortInst().getCellInst().getName());
                currCell = sinkCell;
            }

            carryCellGroupChains.add(carryCellGroups);
        }
        return carryCellGroupChains;
    }

    private Site findLUTFFSite(SiteTypeEnum selectedSiteType, List<Site> occupiedCLBSites) throws IOException {
        Map<String, Integer> minmax = getCoordinateMinMaxOfType(selectedSiteType);
        int x_max = minmax.get("X_MAX");
        int x_min = minmax.get("X_MIN");
        int y_max = minmax.get("Y_MAX");
        int y_min = minmax.get("Y_MIN");
        writer.write("\n\tselectedSiteType: " + selectedSiteType);
        writer.write(
                "\n\tX_MAX: " + x_max + ", X_MIN: " + x_min + ", Y_MAX: " + y_max + ", Y_MIN: " + y_min);
        Random rand = new Random();
        List<Site> availableSites = new ArrayList<Site>(Arrays.asList(device.getAllCompatibleSites(selectedSiteType)));
        availableSites.removeAll(occupiedCLBSites);
        Site selectedSite = availableSites.get(rand.nextInt(availableSites.size()));

        return selectedSite;
    };

    private Site findCarryChainAnchorSite(SiteTypeEnum selectedSiteType, List<CarryCellGroup> chain)
            throws IOException {
        Map<String, Integer> minmax = getCoordinateMinMaxOfType(selectedSiteType);
        int x_max = minmax.get("X_MAX");
        int x_min = minmax.get("X_MIN");
        int y_max = minmax.get("Y_MAX");
        int y_min = minmax.get("Y_MIN");
        writer.write("\n\tselectedSiteType: " + selectedSiteType);
        writer.write(
                "\n\tX_MAX: " + x_max + ", X_MIN: " + x_min + ", Y_MAX: " + y_max + ", Y_MIN: " + y_min);
        Site anchorSite = null;
        int x = 0;
        int y = 0;
        Random rand = new Random();
        boolean validAnchor = false;
        int attempts = 0;
        Site[] compatibleSites = device.getAllCompatibleSites(selectedSiteType);
        while (!validAnchor && attempts < 1000) {
            // x = rand.nextInt((x_max - x_min) + 1) + x_min;
            // y = rand.nextInt((y_max - y_min) + 1) + y_min;
            Site selectedSite = compatibleSites[rand.nextInt(compatibleSites.length)];
            x = selectedSite.getInstanceX();
            y = selectedSite.getInstanceY();
            for (int j = 0; j < chain.size(); j++) {
                String name = "SLICE_X" + x + "Y" + (y + j);
                if (design.getSiteInstFromSiteName(name) != null || device.getSite(name) == null) {
                    // if site is already occupied OR if site doesnt exist
                    validAnchor = false;
                    break;
                } else {
                    validAnchor = true;
                    anchorSite = selectedSite;
                    // anchorSiteName = "SLICE_X" + x + "Y" + y;
                }
            }
            attempts++;
        }
        return anchorSite;
    }

    private void placeCarrySite(CarryCellGroup carryCellGroup, SiteInst si,
            Map<String, List<String>> occupiedBELs) {
        //
        // ****** POTENTIAL FUTURE BUG IN HIDING ********
        // what guarantees that all of the FFs connected to the CARRY4 all
        // share the same CE and Reset?
        //
        List<Pair<String, String>> FF_BELS = new ArrayList<>();
        FF_BELS.add(new Pair<String, String>("A5FF", "AFF"));
        FF_BELS.add(new Pair<String, String>("B5FF", "BFF"));
        FF_BELS.add(new Pair<String, String>("C5FF", "CFF"));
        FF_BELS.add(new Pair<String, String>("D5FF", "DFF"));

        List<Pair<String, String>> LUT_BELS = new ArrayList<>();
        LUT_BELS.add(new Pair<String, String>("A5LUT", "A6LUT"));
        LUT_BELS.add(new Pair<String, String>("B5LUT", "B6LUT"));
        LUT_BELS.add(new Pair<String, String>("C5LUT", "C6LUT"));
        LUT_BELS.add(new Pair<String, String>("D5LUT", "D6LUT"));

        for (int i = 0; i < 4; i++) {
            EDIFHierCellInst ff = carryCellGroup.ffs().get(i);
            if (ff != null)
                si.createCell(ff, si.getBEL(FF_BELS.get(i).value()));
            EDIFHierCellInst lut = carryCellGroup.luts().get(i);
            if (lut != null)
                si.createCell(lut, si.getBEL(LUT_BELS.get(i).value()));
        }

        si.createCell(carryCellGroup.carry(), si.getBEL("CARRY4"));

        // if a carry is used, just treat all the site's BELs as used
        occupiedBELs.put(si.getName(), new ArrayList<>(
                List.of("AFF", "A5FF", "BFF", "B5FF", "CFF", "C5FF", "DFF", "D5FF",
                        "A5LUT", "A6LUT", "B5LUT", "B6LUT", "C5LUT", "C6LUT", "D5LUT", "D6LUT")));

    } // end placeCarrySite()

    private void placeCarryChainSites(List<List<CarryCellGroup>> EDIFCarryChains,
            Map<String, List<String>> occupiedBELs, Map<String, List<EDIFHierCellInst>> EDIFCellGroups)
            throws IOException {
        writer.write("\n\nPlacing carry chains... (" + EDIFCarryChains.size() + ")");
        // PLACE CARRY CHAINS
        for (List<CarryCellGroup> chain : EDIFCarryChains) {
            writer.write("\n\tchain size: " + chain.size());
            Random rand = new Random();
            CarryCellGroup anchorGroup = chain.get(0);
            List<SiteTypeEnum> compatibleSiteTypes = new ArrayList<SiteTypeEnum>();
            compatibleSiteTypes.add(SiteTypeEnum.SLICEL);
            compatibleSiteTypes.add(SiteTypeEnum.SLICEM);

            int randIndex = rand.nextInt(compatibleSiteTypes.size());
            SiteTypeEnum selectedSiteType = compatibleSiteTypes.get(randIndex);
            Site anchorSite = findCarryChainAnchorSite(selectedSiteType, chain);

            // find and place the anchor cell
            if (anchorSite == null) {
                writer.write("\nWARNING: COULD NOT PLACE CARRY CHAIN ANCHOR!");
                break;
            } else {
                SiteInst si = new SiteInst(anchorGroup.carry().getFullHierarchicalInstName(), design, selectedSiteType,
                        anchorSite);
                placeCarrySite(chain.get(0), si, occupiedBELs);
            }

            // place the rest of the chain vertically
            for (int i = 1; i < chain.size(); i++) {
                String siteName = "SLICE_X" + anchorSite.getInstanceX() + "Y" + (anchorSite.getInstanceY() + i);
                Site site = device.getSite(siteName);
                SiteInst si = new SiteInst(chain.get(i).carry().getFullHierarchicalInstName(), design, selectedSiteType,
                        site);
                placeCarrySite(chain.get(i), si, occupiedBELs);
            }

        } // end for (List<EDIFCellInst> chain : EDIFCarryChains)
    } // end placeCarryChainSites()

    private List<Pair<EDIFHierCellInst, EDIFHierCellInst>> findDSPPairs(
            Map<String, List<EDIFHierCellInst>> EDIFCellGroups) throws IOException {
        List<EDIFHierCellInst> visitedDSPs = new ArrayList<>();
        List<Pair<EDIFHierCellInst, EDIFHierCellInst>> pairs = new ArrayList<>();
        for (EDIFHierCellInst DSP_CIN : EDIFCellGroups.get("DSP48E1")) {
            List<EDIFHierPortInst> pcins = DSP_CIN.getHierPortInsts().stream()
                    .filter(ehpi -> ehpi.getPortInst().getName().contains("PCIN"))
                    .collect(Collectors.toList());
            if (pcins.isEmpty())
                continue;
            // there should only be one source
            List<EDIFHierPortInst> pcouts = pcins.stream()
                    .map(cin -> cin.getHierarchicalNet().getLeafHierPortInsts(true, false).get(0))
                    .collect(Collectors.toList());
            Set<EDIFHierCellInst> DSP_COUT_SET = pcouts.stream()
                    .map(cout -> cout.getHierarchicalInst().getChild(cout.getPortInst().getCellInst().getName()))
                    .filter(cell -> cell.getInst().getCellName().equals("DSP48E1"))
                    .collect(Collectors.toSet());
            if (DSP_COUT_SET.size() < 1)
                continue;
            if (DSP_COUT_SET.size() > 1) {
                writer.write("WARNING: DSP Cell " + DSP_CIN.getFullHierarchicalInstName()
                        + " has multiple DSP cells on PCIN bus!");
            }
            EDIFHierCellInst DSP_COUT = DSP_COUT_SET.stream().collect(Collectors.toList()).get(0);
            visitedDSPs.add(DSP_CIN);
            visitedDSPs.add(DSP_COUT);
            var pair = new Pair<EDIFHierCellInst, EDIFHierCellInst>(DSP_CIN, DSP_COUT);
            pairs.add(pair);
        }
        EDIFCellGroups.get("DSP48E1").removeAll(visitedDSPs);
        return pairs;
    }

    private void placeDSPPairSites(List<Pair<EDIFHierCellInst, EDIFHierCellInst>> EDIFDSPPairs,
            List<Site> occupiedDSPSites,
            Map<String, List<EDIFHierCellInst>> EDIFCellGroups) throws IOException {
        Random rand = new Random();
        List<Site> compatibleSites = new ArrayList<Site>(
                Arrays.asList(device.getAllCompatibleSites(SiteTypeEnum.DSP48E1)));
        for (Pair<EDIFHierCellInst, EDIFHierCellInst> pair : EDIFDSPPairs) {
            Tile selectedTile = compatibleSites.get(rand.nextInt(compatibleSites.size())).getTile();
            Site[] dspSitePair = selectedTile.getSites();

            SiteInst si0 = new SiteInst(
                    pair.key().getFullHierarchicalInstName(), design, SiteTypeEnum.DSP48E1, dspSitePair[0]);
            si0.createCell(pair.key(), si0.getBEL("DSP48E1"));
            // si0.routeSite();

            SiteInst si1 = new SiteInst(
                    pair.value().getFullHierarchicalInstName(), design, SiteTypeEnum.DSP48E1, dspSitePair[1]);
            si1.createCell(pair.value(), si1.getBEL("DSP48E1"));
            // si1.routeSite();

            compatibleSites.remove(dspSitePair[0]);
            compatibleSites.remove(dspSitePair[1]);
            occupiedDSPSites.add(dspSitePair[0]);
            occupiedDSPSites.add(dspSitePair[1]);
        }
    } // end placeDSPPairSites()

    private Map<Pair<String, String>, List<Pair<EDIFHierCellInst, EDIFHierCellInst>>> findLUTFFEnableResetGroups(
            Map<String, List<EDIFHierCellInst>> EDIFCellGroups) throws IOException {
        List<EDIFHierCellInst> visitedFFs = new ArrayList<>();
        Map<Pair<String, String>, List<Pair<EDIFHierCellInst, EDIFHierCellInst>>> groups = new HashMap<>();

        for (EDIFHierCellInst ffCell : EDIFCellGroups.get("FDRE")) {
            // examine the CE net to determine which group
            EDIFHierPortInst CEPort = ffCell.getPortInst("CE");
            String CENet = CEPort.getHierarchicalNet().getHierarchicalNetName();
            EDIFHierPortInst RPort = ffCell.getPortInst("R");
            String RNet = RPort.getHierarchicalNet().getHierarchicalNetName();
            var enableResetPair = new Pair<String, String>(CENet, RNet);

            // examine the D net to find the LUT pair
            EDIFHierPortInst DPort = ffCell.getPortInst("D");
            EDIFHierPortInst sourcePort = DPort.getHierarchicalNet().getLeafHierPortInsts(true, false).get(0);

            EDIFHierCellInst sourceCell = sourcePort.getHierarchicalInst()
                    .getChild(sourcePort.getPortInst().getCellInst().getName());

            var LUTFFPair = sourceCell.getCellType().getName().contains("LUT")
                    ? new Pair<EDIFHierCellInst, EDIFHierCellInst>(sourceCell, ffCell)
                    : new Pair<EDIFHierCellInst, EDIFHierCellInst>(null, ffCell);

            groups.computeIfAbsent(enableResetPair, k -> new ArrayList<>()).add(LUTFFPair);
            visitedFFs.add(ffCell);
        }
        EDIFCellGroups.get("FDRE").removeAll(visitedFFs);
        return groups;
    }

    private void placeLUTFFPairGroups(
            Map<Pair<String, String>, List<Pair<EDIFHierCellInst, EDIFHierCellInst>>> LUTFFEnableResetGroups,
            List<Site> occupiedCLBSites,
            Map<String, List<EDIFHierCellInst>> EDIFCellGroups) throws IOException {
        writer.write("\n\nPlacing LUT-FF Pair Groups... (" + LUTFFEnableResetGroups.size() + ")");
        for (Map.Entry<Pair<String, String>, List<Pair<EDIFHierCellInst, EDIFHierCellInst>>> group : LUTFFEnableResetGroups
                .entrySet()) {
            Pair<String, String> netPair = group.getKey();

            String s1 = String.format("\n\tCENet: %-50s RNet: %-50s", netPair.key(), netPair.value());
            writer.write(s1);
            List<Pair<EDIFHierCellInst, EDIFHierCellInst>> LUTFFList = group.getValue();

            List<Pair<String, String>> FF_BELS = new ArrayList<>();
            FF_BELS.add(new Pair<String, String>("A5FF", "AFF"));
            FF_BELS.add(new Pair<String, String>("B5FF", "BFF"));
            FF_BELS.add(new Pair<String, String>("C5FF", "CFF"));
            FF_BELS.add(new Pair<String, String>("D5FF", "DFF"));

            List<Pair<String, String>> LUT_BELS = new ArrayList<>();
            LUT_BELS.add(new Pair<String, String>("A5LUT", "A6LUT"));
            LUT_BELS.add(new Pair<String, String>("B5LUT", "B6LUT"));
            LUT_BELS.add(new Pair<String, String>("C5LUT", "C6LUT"));
            LUT_BELS.add(new Pair<String, String>("D5LUT", "D6LUT"));

            for (List<Pair<EDIFHierCellInst, EDIFHierCellInst>> LUTFFPairs : splitIntoGroups(LUTFFList, 4)) {
                // FIND COMPATIBLE SITE
                Random rand = new Random();
                List<SiteTypeEnum> compatibleSiteTypes = new ArrayList<SiteTypeEnum>();
                compatibleSiteTypes.add(SiteTypeEnum.SLICEL);
                compatibleSiteTypes.add(SiteTypeEnum.SLICEM);
                int randIndex = rand.nextInt(compatibleSiteTypes.size());
                SiteTypeEnum selectedSiteType = compatibleSiteTypes.get(randIndex);

                List<Site> availableSites = new ArrayList<Site>(
                        Arrays.asList(device.getAllCompatibleSites(selectedSiteType)));
                availableSites.removeAll(occupiedCLBSites);
                Site selectedSite = availableSites.get(rand.nextInt(availableSites.size()));

                SiteInst si = design.createSiteInst(selectedSite);
                for (int i = 0; i < LUTFFPairs.size(); i++) {
                    EDIFHierCellInst LUTCell = LUTFFPairs.get(i).key();
                    if (LUTCell != null)
                        si.createCell(LUTCell, si.getBEL(LUT_BELS.get(i).value()));
                    si.createCell(LUTFFPairs.get(i).value(), si.getBEL(FF_BELS.get(i).value()));
                }
            }
        }
    } // end placeLUTFFPairGroups()

    public @Override void placeDesign() throws IOException {
        EDIFNetlist netlist = design.getNetlist();
        List<EDIFHierCellInst> ehcis = netlist.getAllLeafHierCellInstances();

        // Create a map to group cells by type
        Map<String, List<EDIFHierCellInst>> EDIFCellGroups = new HashMap<>();
        EDIFCellGroups.put("IBUF", new ArrayList<>());
        EDIFCellGroups.put("OBUF", new ArrayList<>());
        EDIFCellGroups.put("VCC", new ArrayList<>());
        EDIFCellGroups.put("GND", new ArrayList<>());
        EDIFCellGroups.put("CARRY4", new ArrayList<>());
        EDIFCellGroups.put("FDRE", new ArrayList<>());
        EDIFCellGroups.put("LUT", new ArrayList<>());
        EDIFCellGroups.put("DSP48E1", new ArrayList<>());
        EDIFCellGroups.put("RAMB18E1", new ArrayList<>());

        Set<String> uniqueEdifCellTypes = new HashSet<>();

        for (EDIFHierCellInst ehci : ehcis) {
            EDIFCellInst eci = ehci.getInst();
            // populate unique cell tyeps
            uniqueEdifCellTypes.add(eci.getCellType().getName());

            // add this cell to the corresponding group based on type
            for (String cellType : EDIFCellGroups.keySet()) {
                if (eci.getCellType().getName().contains(cellType)) {
                    EDIFCellGroups.get(cellType).add(ehci);
                    writer.write("\n\tFound " + cellType + " cell: " + eci.getName());
                    break; // once matched, no need to check other types
                }
            }
        }

        writer.write("\n\nSet of all Unique EDIF Cell Types... (" + uniqueEdifCellTypes.size() + ")");
        for (String edifCellType : uniqueEdifCellTypes) {
            writer.write("\n\t" + edifCellType);
        }
        writer.write("\nPrinting EDIFCells By Type...");
        for (Map.Entry<String, List<EDIFHierCellInst>> entry : EDIFCellGroups.entrySet()) {
            writer.write("\n\n" + entry.getKey() + " Cells (" + entry.getValue().size() + "):");
            List<EDIFCellInst> cells = entry.getValue().stream()
                    .map(e -> e.getInst())
                    .collect(Collectors.toList());
            printEDIFCellInstList(cells);
        }

        List<Pair<EDIFHierCellInst, EDIFHierCellInst>> DSPPairs = findDSPPairs(EDIFCellGroups);
        // List<List<EDIFHierCellInst>> CARRYChains = findCarryChains(EDIFCellGroups);
        List<List<CarryCellGroup>> CARRYChains = findCarryChains(
                EDIFCellGroups);
        Map<Pair<String, String>, List<Pair<EDIFHierCellInst, EDIFHierCellInst>>> LUTFFEnableResetGroups = findLUTFFEnableResetGroups(
                EDIFCellGroups);

        writer.write("\n\nPrinting DSPPairs... (" + DSPPairs.size() + ")");
        for (Pair<EDIFHierCellInst, EDIFHierCellInst> pair : DSPPairs) {
            writer.write("\n\t(" + pair.key().getCellType().getName() + ": " + pair.key().getFullHierarchicalInstName()
                    + ", " + pair.value().getCellType().getName() + ": " + pair.value().getFullHierarchicalInstName()
                    + ")");
        }

        writeCARRYChains(CARRYChains);

        List<Site> occupiedDSPSites = new ArrayList<>();
        List<Site> occupiedRAMSites = new ArrayList<>();
        List<Site> occupiedCLBSites = new ArrayList<>();
        Map<String, List<String>> occupiedBELs = new HashMap<>();

        placeCarryChainSites(CARRYChains, occupiedBELs, EDIFCellGroups);
        placeDSPPairSites(DSPPairs, occupiedDSPSites, EDIFCellGroups);
        // placeRAMSites();

        writer.write("\n\nNumber of stray FF cells ... (" + EDIFCellGroups.get("FDRE").size() + ")");
        writer.write("\n\nNumber of stray LUT cells ... (" + EDIFCellGroups.get("LUT").size() + ")");
        writer.write("\n\nPrinting Unique CE-R pairs... (" + LUTFFEnableResetGroups.size() + ")");
        for (Pair<String, String> pair : LUTFFEnableResetGroups.keySet()) {
            String s1 = String.format("\n\tCE: %-50s R: %-50s", pair.key(), pair.value());
            writer.write(s1);
        }
        writer.write(
                "\n\nPrinting Unique CE-R pairs with associated FF Cells... (" + LUTFFEnableResetGroups.size() + ")");
        for (Map.Entry<Pair<String, String>, List<Pair<EDIFHierCellInst, EDIFHierCellInst>>> entry : LUTFFEnableResetGroups
                .entrySet()) {
            Pair<String, String> netPair = entry.getKey();
            String CENet = netPair.key();
            String RNet = netPair.value();
            List<Pair<EDIFHierCellInst, EDIFHierCellInst>> cellList = entry.getValue();
            writer.write("\n\tCENet: " + CENet + ", RNet: " + RNet + " with cells... (" + cellList.size() + ")");
            for (Pair<EDIFHierCellInst, EDIFHierCellInst> pair : cellList) {
                if (pair.key() == null) {
                    writer.write("\n\t\tLUT: NULL!" + " => FF: "
                            + pair.value().getFullHierarchicalInstName());
                } else {
                    writer.write("\n\t\tLUT: " + pair.key().getFullHierarchicalInstName() + " => FF: "
                            + pair.value().getFullHierarchicalInstName());
                }
            }
        }

        placeLUTFFPairGroups(LUTFFEnableResetGroups, occupiedCLBSites, EDIFCellGroups);

    } // end placeDesign()
}
// end class
