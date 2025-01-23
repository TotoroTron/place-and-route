
package placer;

import java.util.stream.Collectors;

import java.util.Collection;
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

import com.xilinx.rapidwright.design.Design;
import com.xilinx.rapidwright.design.Net;
import com.xilinx.rapidwright.design.Cell;
import com.xilinx.rapidwright.design.SiteInst;
import com.xilinx.rapidwright.design.SitePinInst;
import com.xilinx.rapidwright.design.PinType;

import com.xilinx.rapidwright.edif.EDIFHierNet;
import com.xilinx.rapidwright.edif.EDIFHierCellInst;
import com.xilinx.rapidwright.edif.EDIFHierPortInst;
import com.xilinx.rapidwright.edif.EDIFCellInst;

import com.xilinx.rapidwright.device.Device;
import com.xilinx.rapidwright.device.BEL;
import com.xilinx.rapidwright.device.BELPin;
import com.xilinx.rapidwright.device.Site;
import com.xilinx.rapidwright.device.SitePIP;
import com.xilinx.rapidwright.device.SitePIPStatus;
import com.xilinx.rapidwright.device.SiteTypeEnum;
import com.xilinx.rapidwright.device.Tile;

public class PlacerSiteCentric extends Placer {

    public PlacerSiteCentric(String rootDir, Design design, Device device) throws IOException {
        super(rootDir, design, device);
        placerName = "PlacerSiteCentric";
        writer = new FileWriter(rootDir + "outputs/printout/" + placerName + ".txt");
        writer.write(placerName + ".txt");
    }

    public void placeDesign(PackedDesign packedDesign) throws IOException {
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

        List<Pair<EDIFHierCellInst, EDIFHierCellInst>> DSPPairs = packedDesign.DSPPairs;
        List<List<CarryCellGroup>> CARRYChains = packedDesign.CARRYChains;
        Map<Pair<String, String>, LUTFFGroup> LUTFFGroups = packedDesign.LUTFFGroups;
        List<List<EDIFHierCellInst>> LUTGroups = packedDesign.LUTGroups;

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

    protected Site selectCLBSite(List<Site> occupiedCLBSites) {
        Random rand = new Random();
        List<SiteTypeEnum> compatibleSiteTypes = new ArrayList<SiteTypeEnum>();
        compatibleSiteTypes.add(SiteTypeEnum.SLICEL);
        compatibleSiteTypes.add(SiteTypeEnum.SLICEM);
        int randIndex = rand.nextInt(compatibleSiteTypes.size());
        SiteTypeEnum selectedSiteType = compatibleSiteTypes.get(randIndex);

        List<Site> availableSites = new ArrayList<Site>(
                Arrays.asList(device.getAllCompatibleSites(selectedSiteType)));
        availableSites.removeAll(occupiedCLBSites);

        // Site selectedSite = availableSites.get(rand.nextInt(availableSites.size()));
        Site selectedSite = availableSites.get(0);

        occupiedCLBSites.add(selectedSite);

        return selectedSite;
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
            compatibleSiteTypes.add(SiteTypeEnum.SLICEM);

            int randIndex = rand.nextInt(compatibleSiteTypes.size());
            SiteTypeEnum selectedSiteType = compatibleSiteTypes.get(randIndex);
            Site anchorSite = findCarryChainAnchorSite(selectedSiteType, chain);
            if (anchorSite == null) {
                System.out.println("\nWARNING: COULD NOT PLACE CARRY CHAIN ANCHOR!\n");
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

        if (EDIFCellGroups.get("RAMB18E1") == null) {
            writer.write("\nWARNING: This design has zero RAMB18E1 cells!\n");
            System.out.println("WARNING: This design has zero RAMB18E1 cells!");
            return;
        }

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

    private void placeLUTFFPairGroups(
            Map<Pair<String, String>, LUTFFGroup> LUTFFEnableResetGroups,
            List<Site> occupiedCLBSites) throws IOException {
        writer.write("\n\nPlacing LUT-FF Pair Groups... (" + LUTFFEnableResetGroups.size() + ")");

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
            Pair<List<List<EDIFHierCellInst>>, List<List<EDIFHierCellInst>>> stackedLUTGroups,
            List<Site> occupiedCLBSites) {
        for (List<EDIFHierCellInst> group : stackedLUTGroups.key()) {
            Site selectedSite = selectCLBSite(occupiedCLBSites);
            SiteInst si = design.createSiteInst(selectedSite);
            for (int i = 0; i < group.size(); i++) {
                si.createCell(group.get(i), si.getBEL(LUT5_BELS[i]));
            }
            si.routeSite();
        }
        for (List<EDIFHierCellInst> group : stackedLUTGroups.value()) {
            Site selectedSite = selectCLBSite(occupiedCLBSites);
            SiteInst si = design.createSiteInst(selectedSite);
            for (int i = 0; i < group.size(); i++) {
                si.createCell(group.get(i), si.getBEL(LUT6_BELS[i]));
            }
            si.routeSite();
        }
    }

    private void placeLUTGroups(List<List<EDIFHierCellInst>> LUTGroups,
            List<Site> occupiedCLBSites) {
        for (List<EDIFHierCellInst> group : LUTGroups) {
            Site selectedSite = selectCLBSite(occupiedCLBSites);
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
