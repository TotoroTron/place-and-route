
package placer;

import java.util.Random;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
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

public class PackerBasic2 extends Packer {

    private Random rand;
    protected Set<SiteTypeEnum> uniqueSiteTypes;
    protected Map<SiteTypeEnum, Site> dummySites;

    public PackerBasic2(String rootDir, Design design, Device device, ClockRegion region) throws IOException {
        super(rootDir, design, device);
        rand = new Random();
        packerName = "PackerBasic2";
        uniqueSiteTypes = new HashSet<>();
        dummySites = new HashMap<>();
        initAvailableSites();
    }

    private void initAvailableSites() {
        Site[] deviceSites = device.getAllSites();
        for (Site site : deviceSites) {
            SiteTypeEnum siteType = site.getSiteTypeEnum();
            if (uniqueSiteTypes.add(siteType)) { // if new unique type is found
                dummySites.put(siteType, device.getAllSitesOfType(siteType)[0]);
            }
        }
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
        List<SiteInst> LUTFFSiteInsts = packLUTFFPairGroups(LUTFFGroups);
        List<SiteInst> LUTSiteInsts = packLUTGroups(LUTGroups);
        List<List<SiteInst>> CARRYSiteInstChains = packCarryChains(CARRYChains);
        List<List<SiteInst>> DSPSiteInstCascades = packDSPCascades(DSPCascades);
        List<SiteInst> RAMSiteInsts = packRAMSiteInsts(RAMCells);
        List<SiteInst> CLBSiteInsts = new ArrayList<>();
        CLBSiteInsts.addAll(LUTFFSiteInsts);
        CLBSiteInsts.addAll(LUTSiteInsts);

        PackedDesign packedDesign = new PackedDesign(
                BUFGCTRLSiteInsts, CARRYSiteInstChains, DSPSiteInstCascades, RAMSiteInsts, CLBSiteInsts,
                uniqueSiteTypes);
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
            writer.write("\n\t\tChain Size: (" + edifChain.size() + "), Chain Anchor Cell: "
                    + edifChain.get(0).carry().getFullHierarchicalInstName());
            SiteTypeEnum siteType = SiteTypeEnum.SLICEL;
            Site dummySite = dummySites.get(siteType); // select arbitrary site
            System.out.println("Dummy Site: " + dummySite.getName());
            for (int i = 0; i < edifChain.size(); i++) {
                SiteInst si = new SiteInst(edifChain.get(i).carry().getFullHierarchicalInstName(), design,
                        siteType, dummySite);
                packCarrySite(edifChain.get(i), si);
                if (i == 0) { // additional routing logic for anchor site
                    Net CINNet = si.getNetFromSiteWire("CIN");
                    CINNet.removePin(si.getSitePinInst("CIN"));
                    si.addSitePIP(si.getSitePIP("PRECYINIT", "0"));
                }
                si.unPlace(); // unplace from dummy site
                siteInstChain.add(si);
            }
            siteInstChains.add(siteInstChain);
        } // end for (List<EDIFCellInst> chain : EDIFCarryChains)
        return siteInstChains;
    } // end packCarryChains()

    private List<List<SiteInst>> packDSPCascades(List<List<EDIFHierCellInst>> EDIFDSPCascades) throws IOException {
        List<List<SiteInst>> siteInstCascades = new ArrayList<>();
        writer.write("\n\nPacking DSP Cascades... (" + EDIFDSPCascades.size() + ")");
        for (List<EDIFHierCellInst> edifCascade : EDIFDSPCascades) {
            List<SiteInst> siteInstCascade = new ArrayList<>();
            writer.write("\n\tCascade Size: (" + edifCascade.size() + "), Cascade Anchor: "
                    + edifCascade.get(0).getFullHierarchicalInstName());
            SiteTypeEnum siteType = SiteTypeEnum.DSP48E1;
            Site dummySite = dummySites.get(siteType); // select arbitrary site
            for (int i = 0; i < edifCascade.size(); i++) {
                SiteInst si = new SiteInst(edifCascade.get(i).getFullHierarchicalInstName(), design,
                        siteType, dummySite);
                si.createCell(edifCascade.get(i), si.getBEL("DSP48E1"));
                si.routeSite();
                si.unPlace(); // unplace from dummy site
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
            List<SiteTypeEnum> siteTypes = new ArrayList<>();
            siteTypes.add(SiteTypeEnum.RAMB18E1);
            siteTypes.add(SiteTypeEnum.FIFO18E1);
            Site dummySite = dummySites.get(siteTypes.get(rand.nextInt(siteTypes.size())));
            SiteInst si = new SiteInst(ehci.getFullHierarchicalInstName(), design,
                    SiteTypeEnum.RAMB18E1, dummySite);
            si.createCell(ehci, si.getBEL("RAMB18E1"));
            si.routeSite();
            si.unPlace();
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
                SiteTypeEnum siteType = SiteTypeEnum.SLICEL;
                Site selectedSite = dummySites.get(siteType);
                System.out.println("Dummy site: " + selectedSite.getName());
                SiteInst si = design.createSiteInst(selectedSite);
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
                si.unPlace();
                LUTFFSiteInsts.add(si);
            }
        }
        return LUTFFSiteInsts;
    } // end packLUTFFPairGroups()

    private List<SiteInst> packLUTGroups(List<List<EDIFHierCellInst>> LUTGroups) throws IOException {
        List<SiteInst> LUTSiteInsts = new ArrayList<>();
        writer.write("\n\nPacking LUT Groups...");
        for (List<EDIFHierCellInst> group : LUTGroups) {
            SiteTypeEnum siteType = SiteTypeEnum.SLICEL;
            Site selectedSite = dummySites.get(siteType);
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
            si.unPlace();
            LUTSiteInsts.add(si);
        }
        return LUTSiteInsts;
    } // end packLUTGroups()

    private List<SiteInst> packBUFGCTRLSiteInsts(List<EDIFHierCellInst> BUFGCTRLCells) throws IOException {
        List<SiteInst> BUFGCTRLSiteInsts = new ArrayList<>();
        writer.write("\n\nPacking BUFGCTRL Cells... (" + BUFGCTRLCells.size());
        for (EDIFHierCellInst ehci : BUFGCTRLCells) {
            SiteTypeEnum siteType = SiteTypeEnum.BUFGCTRL;
            Site dummySite = dummySites.get(siteType);
            SiteInst si = new SiteInst(ehci.getFullHierarchicalInstName(), design,
                    SiteTypeEnum.BUFGCTRL, dummySite);
            si.createCell(ehci, si.getBEL("BUFGCTRL"));
            si.routeSite();
            si.unPlace();
            BUFGCTRLSiteInsts.add(si);
        }
        return BUFGCTRLSiteInsts;
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
        for (int i = 0; i < 3; i++) {
            if (si.getNetFromSiteWire("CARRY4_CO" + i) != null)
                if (si.getNetFromSiteWire("CARRY4_CO" + i).getLogicalHierNet()
                        .getLeafHierPortInsts(false, true).isEmpty())
                    design.removeNet(si.getNetFromSiteWire("CARRY4_CO" + i));
        }
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
        writer.write("\n\nUnique Device Site Types: ");
        for (SiteTypeEnum ste : uniqueSiteTypes) {
            writer.write("\n\tSiteTypeEnum: " + ste);
        }
    }

} // end class PackerBasic
