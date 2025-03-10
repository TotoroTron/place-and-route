
package placer;

import java.util.Random;
import java.util.stream.Collectors;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.io.IOException;

import com.xilinx.rapidwright.design.Design;
import com.xilinx.rapidwright.design.Net;
import com.xilinx.rapidwright.design.SiteInst;
import com.xilinx.rapidwright.design.SitePinInst;
import com.xilinx.rapidwright.design.Cell;

import com.xilinx.rapidwright.edif.EDIFHierCellInst;
import com.xilinx.rapidwright.edif.EDIFHierPortInst;

import com.xilinx.rapidwright.device.Device;
import com.xilinx.rapidwright.device.BEL;
import com.xilinx.rapidwright.device.Site;
import com.xilinx.rapidwright.device.Tile;
import com.xilinx.rapidwright.device.SitePIPStatus;
import com.xilinx.rapidwright.device.SiteTypeEnum;
import com.xilinx.rapidwright.device.ClockRegion;

public class PackerBasic1 extends Packer {

    private Random rand;
    protected ClockRegion regionConstraint;
    protected Set<SiteTypeEnum> deviceSiteTypes;
    protected Map<SiteTypeEnum, List<Site>> occupiedSites;
    protected Map<SiteTypeEnum, List<Site>> availableSites;

    public PackerBasic1(String rootDir, Design design, Device device, ClockRegion region) throws IOException {
        super(rootDir, design, device);
        rand = new Random();
        packerName = "PackerBasic1";
        deviceSiteTypes = new HashSet<>();
        occupiedSites = new HashMap<>();
        availableSites = new HashMap<>();
        regionConstraint = region;
        initAvailableSites();
    }

    private void initAvailableSites() {
        Site[] deviceSites = device.getAllSites();
        for (Site site : deviceSites) {
            SiteTypeEnum siteType = site.getSiteTypeEnum();
            if (deviceSiteTypes.add(siteType)) { // if new unique type is found
                List<Site> compatibleSites = new ArrayList<>(Arrays.asList(device.getAllSitesOfType(siteType)));
                if (regionConstraint != null) {
                    compatibleSites = compatibleSites.stream()
                            .filter(s -> s.getClockRegion() == null
                                    ? s.isGlobalClkBuffer()
                                    : s.getClockRegion().equals(regionConstraint))
                            .collect(Collectors.toList());
                }
                availableSites.put(siteType, compatibleSites);
                occupiedSites.put(siteType, new ArrayList<>());
            }
        }
    }

    public double evaluateCost() throws IOException {
        double cost = 0;
        Collection<Net> nets = design.getNets();
        for (Net net : nets) {
            // System.out.println("Net: " + net.getName());
            if (net.isClockNet() || net.isStaticNet())
                continue;
            Tile srcTile = net.getSourceTile();
            // this returns null if the net is purely intrasite!
            if (srcTile == null)
                continue;
            // System.out.println("\tsrcTile: " + srcTile.getName());
            List<Tile> sinkTiles = net.getSinkPins().stream()
                    .map(spi -> spi.getTile())
                    .collect(Collectors.toList());
            for (Tile sinkTile : sinkTiles) {
                cost = cost + srcTile.getManhattanDistance(sinkTile);
            }
        }
        return cost;
    }

    public PackedDesign packDesign(PrepackedDesign prepackedDesign) throws IOException {
        printDeviceSiteTypes();

        List<EDIFHierCellInst> BUFGCTRLCells = prepackedDesign.BUFGCTRLCells;
        List<List<CarryCellGroup>> CARRYChains = prepackedDesign.CARRYChains;
        List<List<EDIFHierCellInst>> DSPCascades = prepackedDesign.DSPCascades;
        List<EDIFHierCellInst> RAMCells = prepackedDesign.RAMCells;
        Map<Pair<String, String>, LUTFFGroup> LUTFFGroups = prepackedDesign.LUTFFGroups;
        List<List<EDIFHierCellInst>> LUTGroups = prepackedDesign.LUTGroups;

        List<SiteInst> BUFGCTRLSiteInsts = packBUFGCTRLSiteInsts(BUFGCTRLCells);
        List<List<SiteInst>> CARRYSiteInstChains = packCarryChains(CARRYChains);
        List<List<SiteInst>> DSPSiteInstCascades = packDSPCascades(DSPCascades);
        List<SiteInst> RAMSiteInsts = packRAMSiteInsts(RAMCells);
        List<SiteInst> LUTFFSiteInsts = packLUTFFPairGroups(LUTFFGroups);
        List<SiteInst> LUTSiteInsts = packLUTGroups(LUTGroups);
        List<SiteInst> CLBSiteInsts = new ArrayList<>();
        CLBSiteInsts.addAll(LUTFFSiteInsts);
        CLBSiteInsts.addAll(LUTSiteInsts);

        PackedDesign packedDesign = new PackedDesign(
                BUFGCTRLSiteInsts, CARRYSiteInstChains, DSPSiteInstCascades, RAMSiteInsts, CLBSiteInsts,
                deviceSiteTypes);

        writer.write("\n\nALL CELL PATTERNS HAVE BEEN ARBITRARILY PLACED...");
        printOccupiedSites();
        double cost = evaluateCost();
        writer.write("\n\nInitial HPWL cost: " + cost);

        ImageMaker im = new ImageMaker(design);
        im.renderAll();
        im.exportImage(rootDir + "/outputs/packers/init_packing.png");
        return packedDesign;

    } // end placeDesign()

    private void packCarrySite(CarryCellGroup carryCellGroup, SiteInst si) {
        // ****** POTENTIAL FUTURE BUG IN HIDING ********
        // what guarantees that all of the FFs connected to the CARRY4 all
        // share the same CE and Reset?
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
        // default intrasite routing
        si.routeSite();
        // sometimes the default routeSite() is insufficient, so some manual
        // intervention is required
        rerouteCarryNets(si);
        rerouteFFClkSrCeNets(si);
    } // end placeCarrySite()

    private List<List<SiteInst>> packCarryChains(List<List<CarryCellGroup>> EDIFCarryChains)
            throws IOException {
        List<List<SiteInst>> siteInstChains = new ArrayList<>();
        writer.write("\n\nPacking carry chains... (" + EDIFCarryChains.size() + ")");
        for (List<CarryCellGroup> edifChain : EDIFCarryChains) {
            List<SiteInst> siteInstChain = new ArrayList<>();
            writer.write("\n\t\tChain Size: (" + edifChain.size() + "), Chain Anchor: "
                    + edifChain.get(0).carry().getFullHierarchicalInstName());
            Site anchorSite = selectCarryAnchorSite(edifChain.size());
            SiteTypeEnum selectedSiteType = anchorSite.getSiteTypeEnum();
            for (int i = 0; i < edifChain.size(); i++) {
                Site site = (i == 0) ? anchorSite
                        : device.getSite("SLICE_X" + anchorSite.getInstanceX() + "Y" + (anchorSite.getInstanceY() + i));
                SiteInst si = new SiteInst(edifChain.get(i).carry().getFullHierarchicalInstName(), design,
                        selectedSiteType,
                        site);
                packCarrySite(edifChain.get(i), si);
                if (i == 0) { // additional routing logic for anchor site
                    Net CINNet = si.getNetFromSiteWire("CIN");
                    CINNet.removePin(si.getSitePinInst("CIN"));
                    si.addSitePIP(si.getSitePIP("PRECYINIT", "0"));
                }
                occupiedSites.get(selectedSiteType).add(site);
                availableSites.get(selectedSiteType).remove(site);
                siteInstChain.add(si);
            }
            siteInstChains.add(siteInstChain);
        } // end for (List<EDIFCellInst> chain : EDIFCarryChains)
        return siteInstChains;
    } // end packCarryChains()

    private List<List<SiteInst>> packDSPCascades(List<List<EDIFHierCellInst>> EDIFDSPCascades) throws IOException {
        List<List<SiteInst>> siteInstCascades = new ArrayList<>();
        writer.write("\n\nPacking DSP Cascades... (" + EDIFDSPCascades.size() + ")");
        for (List<EDIFHierCellInst> cascade : EDIFDSPCascades) {
            List<SiteInst> siteInstCascade = new ArrayList<>();
            // each DSP tile has 2 DSP sites on the Zynq 7000
            writer.write("\n\tCascade Size: (" + cascade.size() + "), Cascade Anchor: "
                    + cascade.get(0).getFullHierarchicalInstName());
            Site anchorSite = selectDSPAnchorSite(cascade.size());
            SiteTypeEnum siteType = SiteTypeEnum.DSP48E1;
            for (int i = 0; i < cascade.size(); i++) {
                writer.write("\n\t\tPlacing DSP: " + cascade.get(i).getFullHierarchicalInstName());
                Site site = (i == 0) ? anchorSite
                        : device.getSite("DSP48_X" + anchorSite.getInstanceX() + "Y" + (anchorSite.getInstanceY() + i));
                SiteInst si = new SiteInst(cascade.get(i).getFullHierarchicalInstName(), design, siteType, site);
                si.createCell(cascade.get(i), si.getBEL("DSP48E1"));
                occupiedSites.get(siteType).add(site);
                availableSites.get(siteType).remove(site);
                si.routeSite();
                siteInstCascade.add(si);
            }
            siteInstCascades.add(siteInstCascade);
        }
        return siteInstCascades;
    } // end packDSPCascades()

    private List<SiteInst> packRAMSiteInsts(List<EDIFHierCellInst> RAMCells)
            throws IOException {
        List<SiteInst> RAMSiteInsts = new ArrayList<>();
        writer.write("\n\nPacking RAMBCells... (" + RAMCells.size() + ")");
        for (EDIFHierCellInst ehci : RAMCells) {
            Site selectedSite = selectRAMSite();
            SiteTypeEnum ste = selectedSite.getSiteTypeEnum();
            SiteInst si = null;
            if (ste == SiteTypeEnum.RAMB18E1) {
                si = new SiteInst(
                        ehci.getFullHierarchicalInstName(), design, SiteTypeEnum.RAMB18E1, selectedSite);
                si.createCell(ehci, si.getBEL("RAMB18E1"));
            }
            if (ste == SiteTypeEnum.FIFO18E1) {
                si = new SiteInst(
                        ehci.getFullHierarchicalInstName(), design, SiteTypeEnum.FIFO18E1, selectedSite);
                si.createCell(ehci, si.getBEL("RAMB18E1"));
                System.out.println("SiteInst:" + si);
                System.out.println("\tCell-BEL Map:");
                for (Map.Entry<String, Cell> entry : si.getCellMap().entrySet()) {
                    System.out.println("\t\t<" + entry.getKey() + ", " + entry.getValue() + ">");
                }
            }
            si.routeSite();
            RAMSiteInsts.add(si);
        }
        return RAMSiteInsts;
    } // end packRAMSiteInsts()

    private List<SiteInst> packLUTFFPairGroups(
            Map<Pair<String, String>, LUTFFGroup> LUTFFEnableResetGroups) throws IOException {
        List<SiteInst> LUTFFSiteInsts = new ArrayList<>();
        writer.write("\n\nPacking LUT-FF Pair Groups... (" + LUTFFEnableResetGroups.size() + ")");
        for (Map.Entry<Pair<String, String>, LUTFFGroup> entry : LUTFFEnableResetGroups.entrySet()) {
            Pair<String, String> netPair = entry.getKey();
            String s1 = String.format("\n\tCENet: %-50s RNet: %-50s", netPair.key(), netPair.value());
            writer.write(s1);
            LUTFFGroup lutffgroup = entry.getValue();
            for (List<Pair<EDIFHierCellInst, EDIFHierCellInst>> LUTFFPairs : splitIntoGroups(lutffgroup.group(), 4)) {
                Site selectedSite = selectCLBSite();
                SiteInst si = new SiteInst(LUTFFPairs.get(0).value().getFullHierarchicalInstName(), design,
                        selectedSite.getSiteTypeEnum(), selectedSite);
                for (int i = 0; i < LUTFFPairs.size(); i++) {
                    EDIFHierCellInst ff = LUTFFPairs.get(i).value();
                    EDIFHierCellInst lut = LUTFFPairs.get(i).key();
                    si.createCell(ff, si.getBEL(FF_BELS[i]));
                    if (lut != null)
                        si.createCell(lut, si.getBEL(LUT6_BELS[i]));
                }
                si.routeSite();
                List<String> lutOPIP = new ArrayList<>(List.of("AUSED", "BUSED", "CUSED", "DUSED"));
                // DOES NOT SUPPORT STACKED LUTS OR LUT5s!
                for (int i = 0; i < LUTFFPairs.size(); i++) {
                    EDIFHierCellInst lut = LUTFFPairs.get(i).key();
                    if (lut != null) {
                        EDIFHierPortInst lutOPort = lut.getPortInst("O");
                        if (lutOPort.getHierarchicalNet().getLeafHierPortInsts(false, true).size() > 1) {
                            si.addSitePIP(si.getSitePIP(lutOPIP.get(i), "0"));
                        }
                    }
                }
                rerouteFFClkSrCeNets(si);
                LUTFFSiteInsts.add(si);
            }
        }
        return LUTFFSiteInsts;
    } // end packLUTFFPairGroups()

    private List<SiteInst> packLUTGroups(List<List<EDIFHierCellInst>> LUTGroups) throws IOException {
        List<SiteInst> LUTSiteInsts = new ArrayList<>();
        writer.write("\n\nPacking LUT Groups...");
        for (List<EDIFHierCellInst> group : LUTGroups) {
            Site selectedSite = selectCLBSite();
            SiteInst si = new SiteInst(group.get(0).getFullHierarchicalInstName(), design,
                    selectedSite.getSiteTypeEnum(), selectedSite);
            for (int i = 0; i < group.size(); i++) {
                si.createCell(group.get(i), si.getBEL(LUT6_BELS[i]));
            }
            si.routeSite();
            // for whatever reason, this does not activate DUSED PIP for
            // serializer_inst/state[1]_i_3_n_0
            si.addSitePIP(si.getSitePIP("DUSED", "0"));
            si.addSitePIP(si.getSitePIP("CUSED", "0"));
            si.addSitePIP(si.getSitePIP("BUSED", "0"));
            si.addSitePIP(si.getSitePIP("AUSED", "0"));
            LUTSiteInsts.add(si);
        }
        return LUTSiteInsts;
    } // end packLUTGroups()

    private List<SiteInst> packBUFGCTRLSiteInsts(List<EDIFHierCellInst> BUFGCTRLCells) throws IOException {
        List<SiteInst> BUFGCTRLSiteInsts = new ArrayList<>();
        writer.write("\n\nPacking BUFGCTRL Cells... (" + BUFGCTRLCells.size());
        for (EDIFHierCellInst ehci : BUFGCTRLCells) {
            Site selectedSite = selectBUFGCTRLSite();
            SiteInst si = new SiteInst(ehci.getFullHierarchicalInstName(), design, SiteTypeEnum.BUFGCTRL, selectedSite);
            si.createCell(ehci, si.getBEL("BUFGCTRL"));
            si.routeSite();
            BUFGCTRLSiteInsts.add(si);
        }
        return BUFGCTRLSiteInsts;
    }

    private Site selectCLBSite() {
        // List<Site> compatibleSites = new ArrayList<>();
        // compatibleSites.addAll(availableSites.get(SiteTypeEnum.SLICEL));
        // compatibleSites.addAll(availableSites.get(SiteTypeEnum.SLICEM));
        // if (compatibleSites.isEmpty()) {
        // throw new IllegalStateException(
        // "ERROR: device or clock region contains no Sites of type SLICEL or SLICEM!");
        // }
        // compatibleSites.sort(
        // Comparator.comparingInt(Site::getInstanceY)
        // .thenComparingInt(Site::getInstanceX)
        // .reversed());
        // Site selectedSite = compatibleSites.get(0);
        // SiteTypeEnum selectedSiteType = selectedSite.getSiteTypeEnum();
        SiteTypeEnum selectedSiteType = SiteTypeEnum.SLICEL;
        // int randIndex = rand.nextInt(availableSites.get(selectedSiteType).size());
        Site selectedSite = availableSites.get(selectedSiteType).remove(0);
        occupiedSites.get(selectedSiteType).add(selectedSite);
        return selectedSite;
    }

    private Site selectCarryAnchorSite(int chainSize) {
        SiteTypeEnum selectedSiteType = SiteTypeEnum.SLICEL;
        boolean validAnchor = false;
        Site selectedSite = null;
        int attempts = 0;
        while (true) {
            // int randIndex = rand.nextInt(availableSites.get(selectedSiteType).size());
            selectedSite = availableSites.get(selectedSiteType).get(attempts);
            int x = selectedSite.getInstanceX();
            int y = selectedSite.getInstanceY();
            for (int i = 0; i < chainSize; i++) {
                String name = "SLICE_X" + x + "Y" + (y + i);
                if (occupiedSites.get(selectedSiteType).contains(device.getSite(name))) {
                    validAnchor = false;
                    break;
                }
                if (!availableSites.get(selectedSiteType).contains(device.getSite(name))) {
                    validAnchor = false;
                    break;
                }
                validAnchor = true;
            }
            attempts++;
            if (attempts > 1000)
                throw new IllegalStateException("ERROR: Could not find CARRY4 chain anchor after 1000 attempts!");
            if (validAnchor)
                break;
        }
        return selectedSite;
    }

    private Site selectDSPAnchorSite(int cascadeSize) {
        SiteTypeEnum ste = SiteTypeEnum.DSP48E1;
        boolean validAnchor = false;
        Site selectedSite = null;
        int attempts = 0;
        while (true) {
            // int randIndex = rand.nextInt(availableSites.get(ste).size());
            selectedSite = availableSites.get(ste).get(attempts);
            int x = selectedSite.getInstanceX();
            int y = selectedSite.getInstanceY();
            for (int i = 0; i < cascadeSize; i++) {
                String name = "DSP48_X" + x + "Y" + (y + i);
                if (occupiedSites.get(ste).contains(device.getSite(name))) {
                    validAnchor = false;
                    break;
                }
                if (!availableSites.get(ste).contains(device.getSite(name))) {
                    validAnchor = false;
                    break;
                }
                validAnchor = true;
            }
            attempts++;
            if (attempts > 1000)
                throw new IllegalStateException("ERROR: Could not find DSP48E1 cascade anchor after 1000 attempts!");
            if (validAnchor)
                break;
        }
        return selectedSite;
    }

    private Site selectRAMSite() {
        List<Site> compatibleSites = new ArrayList<>();
        compatibleSites.addAll(availableSites.get(SiteTypeEnum.RAMB18E1));
        compatibleSites.addAll(availableSites.get(SiteTypeEnum.FIFO18E1));
        if (compatibleSites.isEmpty()) {
            throw new IllegalStateException(
                    "ERROR: device or clock region contains no Sites of type FIFO18E1 or RAMB18E1!");
        }
        compatibleSites.sort(
                Comparator.comparingInt(Site::getInstanceX)
                        .thenComparingInt(Site::getInstanceY)
                        .reversed());
        Site selectedSite = compatibleSites.get(0);
        SiteTypeEnum ste = selectedSite.getSiteTypeEnum();

        availableSites.get(ste).remove(selectedSite);
        occupiedSites.get(ste).add(selectedSite);
        return selectedSite;
    }

    private Site selectBUFGCTRLSite() {
        SiteTypeEnum siteType = SiteTypeEnum.BUFGCTRL;
        Site selectedSite = availableSites.get(siteType).stream()
                .filter(s -> s.getTile().getName().contains("CLK_BUFG_TOP"))
                .collect(Collectors.toList()).remove(0);
        // the crystal clock comes in on pin H16 on the xc7z020 which is located on top
        // half of device, so can only use BUFGs on the top half of the device, which
        // are (BUFG sites in Tile "CLK_BUFG_TOP..." as opposed to "CLK_BUFG_BOT...").
        // Otherwise, routing will complain about poor CCIO - BUFG placement.
        occupiedSites.get(siteType).add(selectedSite);
        return selectedSite;
    } // end selectBUFGCTRLSite()

    private void rerouteCarryNets(SiteInst si) {
        // activate PIPs for CARRY4/COUT
        si.addSitePIP(si.getSitePIP("COUTUSED", "0"));
        // undo default CARRY4/DI nets
        SitePinInst AX = si.getSitePinInst("AX");
        if (AX != null)
            si.unrouteIntraSiteNet(AX.getBELPin(), si.getBELPin("ACY0", "AX"));
        SitePinInst DX = si.getSitePinInst("DX");
        if (DX != null)
            si.unrouteIntraSiteNet(DX.getBELPin(), si.getBELPin("DCY0", "DX"));
        // activate PIPs for CARRY4/DI pins
        si.addSitePIP(si.getSitePIP("DCY0", "DX"));
        si.addSitePIP(si.getSitePIP("CCY0", "CX"));
        si.addSitePIP(si.getSitePIP("BCY0", "BX"));
        si.addSitePIP(si.getSitePIP("ACY0", "AX"));
        // remove stray CARRY4/CO nets
        if (si.getNetFromSiteWire("CARRY4_CO2") != null)
            design.removeNet(si.getNetFromSiteWire("CARRY4_CO2"));
        if (si.getNetFromSiteWire("CARRY4_CO1") != null)
            design.removeNet(si.getNetFromSiteWire("CARRY4_CO1"));
        if (si.getNetFromSiteWire("CARRY4_CO0") != null)
            design.removeNet(si.getNetFromSiteWire("CARRY4_CO0"));
        // add default XOR PIPs for unused FFs
        for (String FF : FF_BELS)
            if (si.getCell(FF) == null)
                si.addSitePIP(si.getSitePIP(FF.charAt(0) + "OUTMUX", "XOR"));
    } // end rerouteCarryNets()

    private void rerouteFFClkSrCeNets(SiteInst si) {
        si.addSitePIP("CLKINV", "CLK");
        // activate PIPs for SR and CE pins
        Net SRNet = si.getNetFromSiteWire("SRUSEDMUX_OUT");
        Net CENet = si.getNetFromSiteWire("CEUSEDMUX_OUT");
        // if routeSite() default PIPs are incorrect, deactivate them then activate
        // correct PIP
        if (SRNet != null) {
            if (SRNet.isGNDNet()) {
                if (si.getSitePIPStatus(si.getSitePIP("SRUSEDMUX", "IN")) == SitePIPStatus.ON)
                    for (String FF : FF_BELS)
                        si.unrouteIntraSiteNet(si.getSitePinInst("SR").getBELPin(), si.getBELPin(FF, "SR"));
                si.addSitePIP(si.getSitePIP("SRUSEDMUX", "0"));
            } else {
                if (si.getSitePIPStatus(si.getSitePIP("SRUSEDMUX", "0")) == SitePIPStatus.ON)
                    for (String FF : FF_BELS)
                        si.unrouteIntraSiteNet(si.getBELPin("SRUSEDGND", "0"), si.getBELPin(FF, "SR"));
                si.addSitePIP(si.getSitePIP("SRUSEDMUX", "IN"));
            }
        }
        if (CENet != null) {
            if (CENet.isVCCNet()) {
                if (si.getSitePIPStatus(si.getSitePIP("CEUSEDMUX", "IN")) == SitePIPStatus.ON)
                    for (String FF : FF_BELS)
                        si.unrouteIntraSiteNet(si.getSitePinInst("CE").getBELPin(), si.getBELPin(FF, "CE"));
                si.addSitePIP(si.getSitePIP("CEUSEDMUX", "1"));
            } else {
                if (si.getSitePIPStatus(si.getSitePIP("CEUSEDMUX", "1")) == SitePIPStatus.ON)
                    for (String FF : FF_BELS)
                        si.unrouteIntraSiteNet(si.getBELPin("CEUSEDGND", "1"), si.getBELPin(FF, "CE"));
                si.addSitePIP(si.getSitePIP("CEUSEDMUX", "IN"));
            }
        }
    } // end rerouteFFSrCeNets()

    private void printDeviceSiteTypes() throws IOException {
        writer.write("\n\nDevice Site Types: ");
        for (SiteTypeEnum ste : deviceSiteTypes) {
            writer.write("\n\tSiteTypeEnum: " + ste);
        }
    }

    private void printOccupiedSites() throws IOException {
        for (Map.Entry<SiteTypeEnum, List<Site>> sites : occupiedSites.entrySet()) {
            writer.write("\n\nPrinting occupied " + sites.getKey() + " sites... ("
                    + sites.getValue().size() + ")");
            for (Site site : sites.getValue()) {
                writer.write("\n\tSite: " + site.getName() + ", Tile: " + site.getTile().getName());
            }
        }
    }

} // end class PackerBasic
