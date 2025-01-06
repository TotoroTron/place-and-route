
package placer;

import java.util.stream.Collectors;

import org.python.antlr.PythonParser.else_clause_return;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Random;

import java.io.FileWriter;
import java.io.IOException;

import com.xilinx.rapidwright.design.Net;
import com.xilinx.rapidwright.design.Cell;
import com.xilinx.rapidwright.design.SiteInst;
import com.xilinx.rapidwright.design.SitePinInst;
import com.xilinx.rapidwright.design.PinType;

import com.xilinx.rapidwright.edif.EDIFHierNet;
import com.xilinx.rapidwright.edif.EDIFHierCellInst;
import com.xilinx.rapidwright.edif.EDIFHierPortInst;
import com.xilinx.rapidwright.edif.EDIFCellInst;

import com.xilinx.rapidwright.device.BEL;
import com.xilinx.rapidwright.device.BELPin;
import com.xilinx.rapidwright.device.Site;
import com.xilinx.rapidwright.device.SitePIP;
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

    protected Site selectCLBSite(List<Site> occupiedCLBSites) {
        Random rand = new Random();
        List<SiteTypeEnum> compatibleSiteTypes = new ArrayList<SiteTypeEnum>();
        compatibleSiteTypes.add(SiteTypeEnum.SLICEL);
        // compatibleSiteTypes.add(SiteTypeEnum.SLICEM);
        int randIndex = rand.nextInt(compatibleSiteTypes.size());
        SiteTypeEnum selectedSiteType = compatibleSiteTypes.get(randIndex);

        List<Site> availableSites = new ArrayList<Site>(
                Arrays.asList(device.getAllCompatibleSites(selectedSiteType)));
        availableSites.removeAll(occupiedCLBSites);
        Site selectedSite = availableSites.get(rand.nextInt(availableSites.size()));
        occupiedCLBSites.add(selectedSite);

        return selectedSite;
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
            } // end while(true) iterating through the chain itself
            carryCellGroupChains.add(carryCellGroups);
        } // end while() iterating over chains
        return carryCellGroupChains;
    }

    private Site findCarryChainAnchorSite(SiteTypeEnum selectedSiteType, List<CarryCellGroup> chain)
            throws IOException {
        Site anchorSite = null;
        int x = 0;
        int y = 0;
        Random rand = new Random();
        boolean validAnchor = false;
        int attempts = 0;
        Site[] compatibleSites = device.getAllCompatibleSites(selectedSiteType);
        while (!validAnchor && attempts < 1000) {
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
                }
            }
            attempts++;
        }
        return anchorSite;
    }

    private void placeCarrySite(CarryCellGroup carryCellGroup, SiteInst si) {
        // ****** POTENTIAL FUTURE BUG IN HIDING ********
        // what guarantees that all of the FFs connected to the CARRY4 all
        // share the same CE and Reset?
        String[] FF5_BELS = new String[] { "A5FF", "B5FF", "C5FF", "D5FF" };
        String[] FF_BELS = new String[] { "AFF", "BFF", "CFF", "DFF" };
        String[] LUT5_BELS = new String[] { "A5LUT", "B5LUT", "C5LUT", "D5LUT" };
        String[] LUT6_BELS = new String[] { "A6LUT", "B6LUT", "C6LUT", "D6LUT" };
        for (int i = 0; i < 4; i++) {
            EDIFHierCellInst ff = carryCellGroup.ffs().get(i);
            if (ff != null)
                si.createCell(ff, si.getBEL(FF_BELS[i]));
            EDIFHierCellInst lut = carryCellGroup.luts().get(i);
            if (lut != null)
                si.createCell(lut, si.getBEL(LUT6_BELS[i]));
            // carry site LUTs MUST be placed on LUT6 BELs.
            // only LUT6/O6 can connect to CARRY4/S0
        }
        si.createCell(carryCellGroup.carry(), si.getBEL("CARRY4"));

        si.routeSite(); // default routing

        // undo default CARRY4/DI nets
        SitePinInst AX = si.getSitePinInst("AX");
        if (AX != null)
            si.unrouteIntraSiteNet(AX.getBELPin(), si.getBELPin("ACY0", "AX"));
        SitePinInst DX = si.getSitePinInst("DX");
        if (DX != null)
            si.unrouteIntraSiteNet(DX.getBELPin(), si.getBELPin("DCY0", "DX"));
        // activate PIPs for CARRY4/COUT
        si.addSitePIP(si.getSitePIP("COUTUSED", "0"));
        // activate PIPs for CARRY4/DI pins
        si.addSitePIP(si.getSitePIP("DCY0", "DX"));
        si.addSitePIP(si.getSitePIP("CCY0", "CX"));
        si.addSitePIP(si.getSitePIP("BCY0", "BX"));
        si.addSitePIP(si.getSitePIP("ACY0", "AX"));
        // remove stray CARRY4/CO nets
        design.removeNet(si.getNetFromSiteWire("CARRY4_CO2"));
        design.removeNet(si.getNetFromSiteWire("CARRY4_CO1"));
        design.removeNet(si.getNetFromSiteWire("CARRY4_CO0"));
        // add default XOR PIPs for unused FFs
        for (String FF : new String[] { "DFF", "CFF", "BFF", "AFF" }) {
            if (si.getCell(FF) == null) {
                si.addSitePIP(si.getSitePIP(FF.charAt(0) + "OUTMUX", "XOR"));
            }
        }
        // activate PIPs for SR and CE pins
        Net SRNet = si.getNetFromSiteWire("SRUSEDMUX_OUT");
        Net CENet = si.getNetFromSiteWire("CEUSEDMUX_OUT");
        // deactivate the default PIP from routeSite()
        for (String FF : new String[] { "DFF", "CFF", "BFF", "AFF" }) {
            si.unrouteIntraSiteNet(si.getBELPin("SRUSEDGND", "0"), si.getBELPin(FF, "SR"));
            si.unrouteIntraSiteNet(si.getBELPin("CEUSEDVCC", "1"), si.getBELPin(FF, "CE"));
        }
        // activate the correct SR CE PIP
        if (SRNet != null)
            si.addSitePIP(si.getSitePIP("SRUSEDMUX", SRNet.isGNDNet() ? "0" : "IN"));
        if (CENet != null)
            si.addSitePIP(si.getSitePIP("CEUSEDMUX", CENet.isVCCNet() ? "1" : "IN"));
    } // end placeCarrySite()

    private void placeCarryChainSites(List<List<CarryCellGroup>> EDIFCarryChains,
            List<Site> occupiedCLBSites)
            throws IOException {
        writer.write("\n\nPlacing carry chains... (" + EDIFCarryChains.size() + ")");
        for (List<CarryCellGroup> chain : EDIFCarryChains) {
            writer.write("\n\t\tchain size: " + chain.size());
            Random rand = new Random();
            CarryCellGroup anchorGroup = chain.get(0);
            List<SiteTypeEnum> compatibleSiteTypes = new ArrayList<SiteTypeEnum>();
            compatibleSiteTypes.add(SiteTypeEnum.SLICEL);
            // compatibleSiteTypes.add(SiteTypeEnum.SLICEM);

            int randIndex = rand.nextInt(compatibleSiteTypes.size());
            SiteTypeEnum selectedSiteType = compatibleSiteTypes.get(randIndex);
            Site anchorSite = findCarryChainAnchorSite(selectedSiteType, chain);
            if (anchorSite == null) {
                writer.write("\nWARNING: COULD NOT PLACE CARRY CHAIN ANCHOR!");
                break;
            }
            for (int i = 0; i < chain.size(); i++) {
                Site site = (i == 0) ? anchorSite
                        : device.getSite("SLICE_X" + anchorSite.getInstanceX() + "Y" + (anchorSite.getInstanceY() + i));
                SiteInst si = new SiteInst(chain.get(i).carry().getFullHierarchicalInstName(), design, selectedSiteType,
                        site);
                placeCarrySite(chain.get(i), si);
                if (i == 0) { // additional routing logic for anchor site
                    Net CINNet = si.getNetFromSiteWire("CIN");
                    CINNet.removePin(si.getSitePinInst("CIN"));
                    si.addSitePIP(si.getSitePIP("PRECYINIT", "0"));
                }
                occupiedCLBSites.add(site);
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
            var pair = new Pair<EDIFHierCellInst, EDIFHierCellInst>(DSP_COUT, DSP_CIN);
            pairs.add(pair);
        }
        EDIFCellGroups.get("DSP48E1").removeAll(visitedDSPs);
        return pairs;
    }

    private void placeDSPPairSites(List<Pair<EDIFHierCellInst, EDIFHierCellInst>> EDIFDSPPairs,
            List<Site> occupiedDSPSites) throws IOException {
        Random rand = new Random();
        List<Site> compatibleSites = new ArrayList<Site>(
                Arrays.asList(device.getAllCompatibleSites(SiteTypeEnum.DSP48E1)));
        compatibleSites.removeAll(occupiedDSPSites);
        for (Pair<EDIFHierCellInst, EDIFHierCellInst> pair : EDIFDSPPairs) {
            Tile selectedTile = compatibleSites.get(rand.nextInt(compatibleSites.size())).getTile();
            List<Site> dspSitePair = Arrays.asList(selectedTile.getSites()).stream()
                    .filter(s -> s.getSiteTypeEnum().equals(SiteTypeEnum.DSP48E1))
                    .collect(Collectors.toList());

            // DSP supplying COUT MUST be placed first!
            SiteInst si0 = new SiteInst(
                    pair.key().getFullHierarchicalInstName(), design, SiteTypeEnum.DSP48E1, dspSitePair.get(0));
            si0.createCell(pair.key(), si0.getBEL("DSP48E1"));
            si0.routeSite();

            SiteInst si1 = new SiteInst(
                    pair.value().getFullHierarchicalInstName(), design, SiteTypeEnum.DSP48E1, dspSitePair.get(1));
            si1.createCell(pair.value(), si1.getBEL("DSP48E1"));
            si1.routeSite();

            compatibleSites.remove(dspSitePair.get(0));
            compatibleSites.remove(dspSitePair.get(1));
            occupiedDSPSites.add(dspSitePair.get(0));
            occupiedDSPSites.add(dspSitePair.get(1));
        }
    } // end placeDSPPairSites()

    private void placeRAMSites(List<Site> occupiedRAMSites, Map<String, List<EDIFHierCellInst>> EDIFCellGroups)
            throws IOException {
        Random rand = new Random();
        List<Site> compatibleSites = new ArrayList<Site>(
                Arrays.asList(device.getAllCompatibleSites(SiteTypeEnum.RAMB18E1)));
        compatibleSites.removeAll(occupiedRAMSites);
        // System.out.println(EDIFCellGroups.get("RAMB18E1").size());

        while (true) {
            Tile selectedTile = compatibleSites.get(rand.nextInt(compatibleSites.size())).getTile();
            List<Site> ramSites = Arrays.asList(selectedTile.getSites()).stream()
                    .filter(s -> s.getSiteTypeEnum().equals(SiteTypeEnum.RAMB18E1)
                            || Arrays.asList(s.getAlternateSiteTypeEnums()).contains(SiteTypeEnum.RAMB18E1))
                    .collect(Collectors.toList());

            if (EDIFCellGroups.get("RAMB18E1").isEmpty())
                break;

            EDIFHierCellInst cell0 = EDIFCellGroups.get("RAMB18E1").get(0);
            SiteInst si0 = new SiteInst(cell0.getFullHierarchicalInstName(), design, SiteTypeEnum.RAMB18E1,
                    ramSites.get(0));
            si0.createCell(cell0, si0.getBEL("RAMB18E1"));
            si0.routeSite();
            compatibleSites.remove(ramSites.get(0));
            occupiedRAMSites.add(ramSites.get(0));
            EDIFCellGroups.get("RAMB18E1").remove(cell0);

            if (EDIFCellGroups.get("RAMB18E1").isEmpty())
                break;

            EDIFHierCellInst cell1 = EDIFCellGroups.get("RAMB18E1").get(0);
            SiteInst si1 = new SiteInst(cell1.getFullHierarchicalInstName(), design, SiteTypeEnum.RAMB18E1,
                    ramSites.get(1));
            si1.createCell(cell1, si1.getBEL("RAMB18E1"));
            si1.routeSite();
            compatibleSites.remove(ramSites.get(1));
            occupiedRAMSites.add(ramSites.get(1));
            EDIFCellGroups.get("RAMB18E1").remove(cell1);
        }
    } // end placeRAMSites()

    private Map<Pair<String, String>, LUTFFGroup> findLUTFFGroups(
            Map<String, List<EDIFHierCellInst>> EDIFCellGroups) throws IOException {
        List<EDIFHierCellInst> visitedFFs = new ArrayList<>();
        List<EDIFHierCellInst> visitedLUTs = new ArrayList<>();
        Map<Pair<String, String>, LUTFFGroup> groups = new HashMap<>();

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

            groups.computeIfAbsent(enableResetPair, k -> new LUTFFGroup(new ArrayList<>())).group().add(LUTFFPair);
            visitedFFs.add(ffCell);
            visitedLUTs.add(sourceCell);
        }
        EDIFCellGroups.get("FDRE").removeAll(visitedFFs);
        EDIFCellGroups.get("LUT").removeAll(visitedLUTs);
        return groups;
    }

    private void placeLUTFFPairGroups(
            Map<Pair<String, String>, LUTFFGroup> LUTFFEnableResetGroups,
            List<Site> occupiedCLBSites) throws IOException {
        writer.write("\n\nPlacing LUT-FF Pair Groups... (" + LUTFFEnableResetGroups.size() + ")");

        String[] FF5_BELS = new String[] { "A5FF", "B5FF", "C5FF", "D5FF" };
        String[] FF_BELS = new String[] { "AFF", "BFF", "CFF", "DFF" };
        String[] LUT5_BELS = new String[] { "A5LUT", "B5LUT", "C5LUT", "D5LUT" };
        String[] LUT6_BELS = new String[] { "A6LUT", "B6LUT", "C6LUT", "D6LUT" };

        for (Map.Entry<Pair<String, String>, LUTFFGroup> entry : LUTFFEnableResetGroups.entrySet()) {
            Pair<String, String> netPair = entry.getKey();

            String s1 = String.format("\n\tCENet: %-50s RNet: %-50s", netPair.key(), netPair.value());
            writer.write(s1);
            LUTFFGroup lutffgroup = entry.getValue();

            for (List<Pair<EDIFHierCellInst, EDIFHierCellInst>> LUTFFPairs : splitIntoGroups(lutffgroup.group(), 4)) {
                Site selectedSite = selectCLBSite(occupiedCLBSites);
                occupiedCLBSites.add(selectedSite);
                SiteInst si = design.createSiteInst(selectedSite);
                for (int i = 0; i < LUTFFPairs.size(); i++) {
                    EDIFHierCellInst lut = LUTFFPairs.get(i).key();
                    EDIFHierCellInst ff = LUTFFPairs.get(i).value();
                    if (lut != null) {
                        si.createCell(lut, si.getBEL(LUT6_BELS[i]));
                    }
                    si.createCell(ff, si.getBEL(FF_BELS[i]));
                }
                si.routeSite();
                // activate PIPs for SR and CE pins
                Net SRNet = si.getNetFromSiteWire("SRUSEDMUX_OUT");
                Net CENet = si.getNetFromSiteWire("CEUSEDMUX_OUT");

                for (String FF_BEL : FF_BELS) {
                    si.unrouteIntraSiteNet(si.getBELPin("SRUSEDGND", "0"), si.getBELPin(FF_BEL, "SR"));
                    si.unrouteIntraSiteNet(si.getBELPin("CEUSEDVCC", "1"), si.getBELPin(FF_BEL, "CE"));
                }

                if (SRNet != null)
                    si.addSitePIP(si.getSitePIP("SRUSEDMUX", SRNet.isGNDNet() ? "0" : "IN"));
                if (CENet != null)
                    si.addSitePIP(si.getSitePIP("CEUSEDMUX", CENet.isVCCNet() ? "1" : "IN"));

                // accomodate LUT outputs that have sinks outside the lutff pair
                si.addSitePIP(si.getSitePIP("DOUTMUX", "O6"));
                si.addSitePIP(si.getSitePIP("COUTMUX", "O6"));
                si.addSitePIP(si.getSitePIP("BOUTMUX", "O6"));
                si.addSitePIP(si.getSitePIP("AOUTMUX", "O6"));
            }
        }
    } // end placeLUTFFPairGroups()

    private Pair<List<List<EDIFHierCellInst>>, List<List<EDIFHierCellInst>>> buildStackedLUTGroups(
            Map<String, List<EDIFHierCellInst>> EDIFCellGroups) throws IOException {
        List<EDIFHierCellInst> LUTCells = new ArrayList<>(EDIFCellGroups.get("LUT"));
        List<EDIFHierCellInst> LUT6Cells = new ArrayList<>();
        EDIFCellGroups.get("LUT").removeIf(c -> {
            if (c.getCellType().getName().contains("LUT6")) {
                LUT6Cells.add(c);
                return true;
            }
            return false;
        });
        EDIFCellGroups.get("LUT").removeAll(LUTCells);
        EDIFCellGroups.get("LUT").removeAll(LUT6Cells);
        List<List<EDIFHierCellInst>> LUT6Groups = splitIntoGroups(LUT6Cells, 4);
        List<List<EDIFHierCellInst>> LUTGroups = splitIntoGroups(LUTCells, 8);
        var pairLists = new Pair<List<List<EDIFHierCellInst>>, List<List<EDIFHierCellInst>>>(LUT6Groups, LUTGroups);
        return pairLists;
    }

    private List<List<EDIFHierCellInst>> buildLUTGroups(Map<String, List<EDIFHierCellInst>> EDIFCellGroups)
            throws IOException {
        List<List<EDIFHierCellInst>> LUTGroups = splitIntoGroups(EDIFCellGroups.get("LUT"), 4);
        for (List<EDIFHierCellInst> group : LUTGroups)
            EDIFCellGroups.get("LUT").removeAll(group);
        return LUTGroups;
    }

    private void placeStackedLUTGroups(
            Pair<List<List<EDIFHierCellInst>>, List<List<EDIFHierCellInst>>> stackedLUTGroups,
            List<Site> occupiedCLBSites) {

        List<String> LUT_BELS = new ArrayList<>();
        LUT_BELS.add("A6LUT");
        LUT_BELS.add("B6LUT");
        LUT_BELS.add("C6LUT");
        LUT_BELS.add("D6LUT");
        LUT_BELS.add("A5LUT");
        LUT_BELS.add("B5LUT");
        LUT_BELS.add("C5LUT");
        LUT_BELS.add("D5LUT");

        for (List<EDIFHierCellInst> group : stackedLUTGroups.key()) {
            Site selectedSite = selectCLBSite(occupiedCLBSites);
            SiteInst si = design.createSiteInst(selectedSite);
            for (int i = 0; i < group.size(); i++) {
                si.createCell(group.get(i), si.getBEL(LUT_BELS.get(i)));
            }
            si.routeSite();
        }
        for (List<EDIFHierCellInst> group : stackedLUTGroups.value()) {
            Site selectedSite = selectCLBSite(occupiedCLBSites);
            SiteInst si = design.createSiteInst(selectedSite);
            for (int i = 0; i < group.size(); i++) {
                si.createCell(group.get(i), si.getBEL(LUT_BELS.get(i)));
            }
            si.routeSite();
        }
    }

    private void placeLUTGroups(List<List<EDIFHierCellInst>> LUTGroups,
            List<Site> occupiedCLBSites) {
        List<String> LUT_BELS = new ArrayList<>();
        LUT_BELS.add("A6LUT");
        LUT_BELS.add("B6LUT");
        LUT_BELS.add("C6LUT");
        LUT_BELS.add("D6LUT");
        LUT_BELS.add("A5LUT");
        LUT_BELS.add("B5LUT");
        LUT_BELS.add("C5LUT");
        LUT_BELS.add("D5LUT");
        for (List<EDIFHierCellInst> group : LUTGroups) {
            Site selectedSite = selectCLBSite(occupiedCLBSites);
            SiteInst si = design.createSiteInst(selectedSite);
            for (int i = 0; i < group.size(); i++) {
                si.createCell(group.get(i), si.getBEL(LUT_BELS.get(i)));
            }
            si.routeSite();
        }
    }

    private void placeVCCGND(Map<String, List<EDIFHierCellInst>> EDIFCellGroups) throws IOException {
        List<EDIFHierCellInst> VCCCells = EDIFCellGroups.get("VCC");
        writer.write("\n\nPrinting VCC Cells...");
        for (EDIFHierCellInst ehci : VCCCells) {
            Cell cell = design.createCell(ehci.getFullHierarchicalInstName(), ehci.getInst());
            Map<SiteTypeEnum, Set<String>> compatiblePlacements = cell.getCompatiblePlacements(device);
            if (compatiblePlacements.isEmpty()) {
                writer.write("\n\tVCC Cell: " + cell.getName() + " has no compatible placements!");
            } else {
                writer.write("\n\tVCC Cell: " + cell.getName());
                for (Map.Entry<SiteTypeEnum, Set<String>> entry : compatiblePlacements.entrySet()) {
                    writer.write("\n\t\tSiteType: " + entry.getKey());
                }
            }
        }
    }

    public @Override void placeDesign() throws IOException {

        // Create a map to group cells by type
        Map<String, List<EDIFHierCellInst>> EDIFCellGroups = new HashMap<>();
        Set<String> uniqueEdifCellTypes = new HashSet<>();

        for (EDIFHierCellInst ehci : design.getNetlist().getAllLeafHierCellInstances()) {
            String cellType = ehci.getInst().getCellType().getName();
            // group all luts together
            if (cellType.contains("LUT"))
                cellType = "LUT";
            // populate unique cell types
            if (uniqueEdifCellTypes.add(cellType)) // set returns bool
                EDIFCellGroups.put(cellType, new ArrayList<>()); // spawn unique group
            // add cell to corresponding group
            EDIFCellGroups.get(cellType).add(ehci); // add cell to corresponding group
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
        List<List<CarryCellGroup>> CARRYChains = findCarryChains(EDIFCellGroups);
        Map<Pair<String, String>, LUTFFGroup> LUTFFGroups = findLUTFFGroups(EDIFCellGroups);
        List<List<EDIFHierCellInst>> LUTGroups = buildLUTGroups(EDIFCellGroups);

        printCARRYChains(CARRYChains);
        printDSPPairs(DSPPairs);
        printLUTFFGroups(LUTFFGroups);
        printLUTGroups(LUTGroups);

        List<Site> occupiedDSPSites = new ArrayList<>();
        List<Site> occupiedRAMSites = new ArrayList<>();
        List<Site> occupiedCLBSites = new ArrayList<>();

        placeCarryChainSites(CARRYChains, occupiedCLBSites);
        placeDSPPairSites(DSPPairs, occupiedDSPSites);
        placeRAMSites(occupiedRAMSites, EDIFCellGroups);
        placeLUTFFPairGroups(LUTFFGroups, occupiedCLBSites);
        placeLUTGroups(LUTGroups, occupiedCLBSites);

        writer.write("\n\nALL CELL PATTERNS HAVE BEEN PLACED...");
        writer.write("\n\nPrinting occupiedCLBSites... (" + occupiedCLBSites.size() + ")");
        for (Site site : occupiedCLBSites) {
            writer.write("\n\tSite: " + site.getName());
        }
        writer.write("\nPrinting occupiedDSPSites... (" + occupiedDSPSites.size() + ")");
        for (Site site : occupiedDSPSites) {
            writer.write("\n\tSite: " + site.getName());
        }
        writer.write("\nPrinting occupiedRAMSites... (" + occupiedRAMSites.size() + ")");
        for (Site site : occupiedDSPSites) {
            writer.write("\n\tSite: " + site.getName());
        }
        writer.write("\n\nPrinting remaining cells in EDIFCellGroups...");
        for (Map.Entry<String, List<EDIFHierCellInst>> entry : EDIFCellGroups.entrySet()) {
            writer.write("\n\tGroup: " + entry.getKey() + "... (" + entry.getValue().size() + ")");
            if (entry.getValue().isEmpty())
                writer.write("\n\t\tEmpty!");
            for (EDIFHierCellInst ehci : entry.getValue()) {
                writer.write("\n\t\tUnplaced Cell: " + ehci.getCellType() + ": " + ehci.getFullHierarchicalInstName());
            }
        }

    } // end placeDesign()
} // end class

// Pair<List<List<EDIFHierCellInst>>, List<List<EDIFHierCellInst>>>
// stackedLUTGroups = buildStackedLUTGroups(
// EDIFCellGroups);
// placeStackedLUTGroups(stackedLUTGroups, occupiedCLBSites);

// List<SiteTypeEnum> unroutableSites = new ArrayList<>();
// unroutableSites.add(SiteTypeEnum.ILOGICE2);
// unroutableSites.add(SiteTypeEnum.ILOGICE3);
// unroutableSites.add(SiteTypeEnum.OLOGICE2);
// unroutableSites.add(SiteTypeEnum.OLOGICE3);
// unroutableSites.add(SiteTypeEnum.IOB33);
// unroutableSites.add(SiteTypeEnum.IOB18);
// unroutableSites.add(SiteTypeEnum.IPAD);
// unroutableSites.add(SiteTypeEnum.OPAD);
