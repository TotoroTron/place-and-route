
package placer;

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
import java.util.Random;
import java.io.IOException;

import com.xilinx.rapidwright.design.Design;
import com.xilinx.rapidwright.design.Net;
import com.xilinx.rapidwright.design.SiteInst;
import com.xilinx.rapidwright.design.SitePinInst;

import com.xilinx.rapidwright.edif.EDIFHierCellInst;
import com.xilinx.rapidwright.edif.EDIFHierPortInst;

import com.xilinx.rapidwright.device.Device;
import com.xilinx.rapidwright.device.Site;
import com.xilinx.rapidwright.device.SitePIPStatus;
import com.xilinx.rapidwright.device.SiteTypeEnum;
import com.xilinx.rapidwright.device.ClockRegion;

public class PlacerSiteCentric extends Placer {

    protected ClockRegion regionConstraint;
    protected Set<SiteTypeEnum> deviceSiteTypes;
    protected Map<SiteTypeEnum, List<Site>> occupiedSites;
    protected Map<SiteTypeEnum, List<Site>> availableSites;

    public PlacerSiteCentric(String rootDir, Design design, Device device, ClockRegion region) throws IOException {
        super(rootDir, design, device);
        placerName = "PlacerSiteCentric";
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
                            .filter(s -> s.getClockRegion() != null)
                            .filter(s -> s.getClockRegion().equals(regionConstraint))
                            .collect(Collectors.toList());
                }
                availableSites.put(siteType, compatibleSites);
                occupiedSites.put(siteType, new ArrayList<>());
            }
        }
    }

    public void placeDesign(PackedDesign packedDesign) throws IOException {
        List<List<EDIFHierCellInst>> DSPCascades = packedDesign.DSPCascades;
        List<EDIFHierCellInst> RAMCells = packedDesign.RAMCells;
        List<List<CarryCellGroup>> CARRYChains = packedDesign.CARRYChains;
        Map<Pair<String, String>, LUTFFGroup> LUTFFGroups = packedDesign.LUTFFGroups;
        List<List<EDIFHierCellInst>> LUTGroups = packedDesign.LUTGroups;

        placeCarryChainSites(CARRYChains);
        placeDSPCascades(DSPCascades);
        placeRAMSites(RAMCells);
        placeLUTFFPairGroups(LUTFFGroups);
        placeLUTGroups(LUTGroups);

        writer.write("\n\nALL CELL PATTERNS HAVE BEEN PLACED...");
        writer.write("\n\nPrinting occupied SLICEL sites... (" + occupiedSites.get(SiteTypeEnum.SLICEL).size() + ")");
        for (Site site : occupiedSites.get(SiteTypeEnum.SLICEL)) {
            writer.write("\n\tSite: " + site.getName());
        }
        writer.write("\n\nPrinting occupied SLICEM sites... (" + occupiedSites.get(SiteTypeEnum.SLICEM).size() + ")");
        for (Site site : occupiedSites.get(SiteTypeEnum.SLICEM)) {
            writer.write("\n\tSite: " + site.getName());
        }
        writer.write("\n\nPrinting occupied DSP48E1 Sites... (" + occupiedSites.get(SiteTypeEnum.DSP48E1).size() + ")");
        for (Site site : occupiedSites.get(SiteTypeEnum.DSP48E1)) {
            writer.write("\n\tSite: " + site.getName());
        }
        writer.write(
                "\n\nPrinting occupied RAMB18E1 Sites... (" + occupiedSites.get(SiteTypeEnum.RAMB18E1).size() + ")");
        for (Site site : occupiedSites.get(SiteTypeEnum.RAMB18E1)) {
            writer.write("\n\tSite: " + site.getName());
        }
        writer.write(
                "\n\nPrinting occupied FIFO18E1 Sites... (" + occupiedSites.get(SiteTypeEnum.FIFO18E1).size() + ")");
        for (Site site : occupiedSites.get(SiteTypeEnum.FIFO18E1)) {
            writer.write("\n\tSite: " + site.getName());
        }
    } // end placeDesign()

    protected SiteTypeEnum selectCLBSiteType() {
        Random rand = new Random();
        List<SiteTypeEnum> compatibleSiteTypes = new ArrayList<SiteTypeEnum>();
        if (deviceSiteTypes.contains(SiteTypeEnum.SLICEL))
            compatibleSiteTypes.add(SiteTypeEnum.SLICEL);
        if (deviceSiteTypes.contains(SiteTypeEnum.SLICEM))
            compatibleSiteTypes.add(SiteTypeEnum.SLICEM);
        if (compatibleSiteTypes.isEmpty()) {
            throw new IllegalStateException("ERROR: device contains no Sites of type SLICEL or SLICEM !");
        }
        SiteTypeEnum selectedSiteType = compatibleSiteTypes.get(rand.nextInt(compatibleSiteTypes.size()));
        return selectedSiteType;
    }

    protected Site selectCLBSite() {
        Random rand = new Random();
        SiteTypeEnum selectedSiteType = selectCLBSiteType();
        int randRange = availableSites.get(selectedSiteType).size();
        Site selectedSite = availableSites.get(selectedSiteType).remove(rand.nextInt(randRange));
        // Site selectedSite = availableSites.get(selectedSiteType).remove(0);
        occupiedSites.get(selectedSiteType).add(selectedSite);
        return selectedSite;
    }

    protected Site selectCarryAnchorSite(int chainSize) {
        Random rand = new Random();
        SiteTypeEnum selectedSiteType = selectCLBSiteType();
        boolean validAnchor = false;
        Site selectedSite = null;
        int attempts = 0;
        while (true) {
            int randRange = availableSites.get(selectedSiteType).size();
            selectedSite = availableSites.get(selectedSiteType).get(rand.nextInt(randRange));
            // selectedSite = availableSites.get(selectedSiteType).get(attempts);
            int x = selectedSite.getInstanceX();
            int y = selectedSite.getInstanceY();
            for (int i = 0; i < chainSize; i++) {
                String name = "SLICE_X" + x + "Y" + (y + i);
                if (design.getSiteInstFromSiteName(name) != null
                        || device.getSite(name) == null
                        || device.getSite(name).getClockRegion() != regionConstraint) {
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

    protected Site selectDSPAnchorSite(int cascadeSize) {
        Random rand = new Random();
        SiteTypeEnum siteType = SiteTypeEnum.DSP48E1;
        boolean validAnchor = false;
        Site selectedSite = null;
        int attempts = 0;
        while (true) {
            int randRange = availableSites.get(siteType).size();
            selectedSite = availableSites.get(siteType).get(rand.nextInt(randRange));
            // selectedSite = availableSites.get(siteType).get(attempts);
            int x = selectedSite.getInstanceX();
            int y = selectedSite.getInstanceY();
            for (int i = 0; i < cascadeSize; i++) {
                String name = "DSP48_X" + x + "Y" + (y + i);
                if (design.getSiteInstFromSiteName(name) != null
                        || device.getSite(name) == null
                        || device.getSite(name).getClockRegion() != regionConstraint) {
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

    protected SiteTypeEnum selectRAMSiteType() {
        Random rand = new Random();

        List<SiteTypeEnum> compatibleSiteTypes = new ArrayList<SiteTypeEnum>();
        // if (deviceSiteTypes.contains(SiteTypeEnum.FIFO18E1))
        // compatibleSiteTypes.add(SiteTypeEnum.FIFO18E1);
        if (deviceSiteTypes.contains(SiteTypeEnum.RAMB18E1))
            compatibleSiteTypes.add(SiteTypeEnum.RAMB18E1);
        if (compatibleSiteTypes.isEmpty()) {
            throw new IllegalStateException(
                    "ERROR: device or clock region contains no Sites of type FIFO18E1 or RAMB18E1 !");
        }
        SiteTypeEnum selectedSiteType = compatibleSiteTypes.get(rand.nextInt(compatibleSiteTypes.size()));
        return selectedSiteType;
    }

    private void placeRAMSites(List<EDIFHierCellInst> RAMCells)
            throws IOException {
        writer.write("\n\nPlacing RAMBCells... (" + RAMCells.size() + ")");
        for (EDIFHierCellInst ehci : RAMCells) {
            Site selectedSite = selectRAMSite();
            SiteInst si = new SiteInst(ehci.getFullHierarchicalInstName(), design, SiteTypeEnum.RAMB18E1,
                    selectedSite);
            si.createCell(ehci, si.getBEL("RAMB18E1"));
            si.routeSite();
        }
    } // end placeRAMSites()

    protected Site selectRAMSite() {
        Random rand = new Random();
        // SiteTypeEnum selectedSiteType = selectRAMSiteType();
        List<Site> compatibleSites = new ArrayList<>();
        compatibleSites.addAll(availableSites.get(SiteTypeEnum.RAMB18E1));
        compatibleSites.addAll(availableSites.get(SiteTypeEnum.FIFO18E1));
        compatibleSites.sort(
                Comparator.comparingInt(Site::getInstanceY)
                        .thenComparingInt(Site::getInstanceX)
                        .reversed());
        int randRange = compatibleSites.size();
        Site selectedSite = compatibleSites.get(rand.nextInt(randRange));
        // Site selectedSite = compatibleSites.get(0);
        SiteTypeEnum selectedSiteType = selectedSite.getSiteTypeEnum();
        availableSites.get(selectedSiteType).remove(selectedSite);
        occupiedSites.get(selectedSiteType).add(selectedSite);
        return selectedSite;
    }

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
        design.removeNet(si.getNetFromSiteWire("CARRY4_CO2"));
        design.removeNet(si.getNetFromSiteWire("CARRY4_CO1"));
        design.removeNet(si.getNetFromSiteWire("CARRY4_CO0"));
        // add default XOR PIPs for unused FFs
        for (String FF : FF_BELS)
            if (si.getCell(FF) == null)
                si.addSitePIP(si.getSitePIP(FF.charAt(0) + "OUTMUX", "XOR"));
    } // end rerouteCarryNets()

    private void rerouteFFSrCeNets(SiteInst si) {
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

    private void placeCarrySite(CarryCellGroup carryCellGroup, SiteInst si) {
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
        rerouteFFSrCeNets(si);
    } // end placeCarrySite()

    private void placeCarryChainSites(List<List<CarryCellGroup>> EDIFCarryChains)
            throws IOException {
        writer.write("\n\nPlacing carry chains... (" + EDIFCarryChains.size() + ")");
        for (List<CarryCellGroup> chain : EDIFCarryChains) {
            writer.write("\n\t\tChain Size: (" + chain.size() + "), Chain Anchor: "
                    + chain.get(0).carry().getFullHierarchicalInstName());
            Site anchorSite = selectCarryAnchorSite(chain.size());
            SiteTypeEnum selectedSiteType = anchorSite.getSiteTypeEnum();
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
                occupiedSites.get(selectedSiteType).add(site);
                availableSites.get(selectedSiteType).remove(site);
            }
        } // end for (List<EDIFCellInst> chain : EDIFCarryChains)
    } // end placeCarryChainSites()

    private void placeDSPCascades(List<List<EDIFHierCellInst>> EDIFDSPCascades) throws IOException {
        writer.write("\n\nPlacing DSP Cascades... (" + EDIFDSPCascades.size() + ")");
        for (List<EDIFHierCellInst> cascade : EDIFDSPCascades) {
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
            }
        }
    } // end placeDSPCascades()

    private void placeLUTFFPairGroups(
            Map<Pair<String, String>, LUTFFGroup> LUTFFEnableResetGroups) throws IOException {
        writer.write("\n\nPlacing LUT-FF Pair Groups... (" + LUTFFEnableResetGroups.size() + ")");

        for (Map.Entry<Pair<String, String>, LUTFFGroup> entry : LUTFFEnableResetGroups.entrySet()) {
            Pair<String, String> netPair = entry.getKey();

            String s1 = String.format("\n\tCENet: %-50s RNet: %-50s", netPair.key(), netPair.value());
            writer.write(s1);
            LUTFFGroup lutffgroup = entry.getValue();

            for (List<Pair<EDIFHierCellInst, EDIFHierCellInst>> LUTFFPairs : splitIntoGroups(lutffgroup.group(), 4)) {
                Site selectedSite = selectCLBSite();
                SiteTypeEnum selectedSiteType = selectedSite.getSiteTypeEnum();
                SiteInst si = design.createSiteInst(selectedSite);
                for (int i = 0; i < LUTFFPairs.size(); i++) {
                    EDIFHierCellInst ff = LUTFFPairs.get(i).value();
                    EDIFHierCellInst lut = LUTFFPairs.get(i).key();
                    si.createCell(ff, si.getBEL(FF_BELS[i]));
                    if (lut != null)
                        si.createCell(lut, si.getBEL(LUT6_BELS[i]));
                }
                si.routeSite();

                // accomodate LUT outputs that have sinks outside the lutff pair
                // si.addSitePIP(si.getSitePIP("DUSED", "0"));
                // si.addSitePIP(si.getSitePIP("CUSED", "0"));
                // si.addSitePIP(si.getSitePIP("BUSED", "0"));
                // si.addSitePIP(si.getSitePIP("AUSED", "0"));
                // si.addSitePIP(si.getSitePIP("DOUTMUX", "O6"));
                //
                List<String> lutOPIP = new ArrayList<>(List.of("AUSED", "BUSED", "CUSED", "DUSED"));
                //
                // DOES NOT SUPPORT STACKED LUTS OR LUT5s!
                //
                for (int i = 0; i < LUTFFPairs.size(); i++) {
                    EDIFHierCellInst lut = LUTFFPairs.get(i).key();
                    if (lut != null) {
                        EDIFHierPortInst lutOPort = lut.getPortInst("O");
                        if (lutOPort.getHierarchicalNet().getLeafHierPortInsts(false, true).size() > 1) {
                            si.addSitePIP(si.getSitePIP(lutOPIP.get(i), "0"));
                        }
                    }
                }
                rerouteFFSrCeNets(si);
            }
        }
    } // end placeLUTFFPairGroups()

    private void placeStackedLUTGroups(
            Pair<List<List<EDIFHierCellInst>>, List<List<EDIFHierCellInst>>> stackedLUTGroups) {
        for (List<EDIFHierCellInst> group : stackedLUTGroups.key()) {
            Site selectedSite = selectCLBSite();
            SiteInst si = design.createSiteInst(selectedSite);
            for (int i = 0; i < group.size(); i++) {
                si.createCell(group.get(i), si.getBEL(LUT5_BELS[i]));
            }
            si.routeSite();
        }
        for (List<EDIFHierCellInst> group : stackedLUTGroups.value()) {
            Site selectedSite = selectCLBSite();
            SiteInst si = design.createSiteInst(selectedSite);
            for (int i = 0; i < group.size(); i++) {
                si.createCell(group.get(i), si.getBEL(LUT6_BELS[i]));
            }
            si.routeSite();
        }
    }

    private void placeLUTGroups(List<List<EDIFHierCellInst>> LUTGroups) {
        for (List<EDIFHierCellInst> group : LUTGroups) {
            Site selectedSite = selectCLBSite();
            SiteInst si = design.createSiteInst(selectedSite);
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
        }
    }

} // end class
