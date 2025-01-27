
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
import com.xilinx.rapidwright.device.TileTypeEnum;
import com.xilinx.rapidwright.device.SLR;
import com.xilinx.rapidwright.device.ClockRegion;

public class PlacerSiteCentric extends Placer {

    ClockRegion regionConstraint;
    Set<SiteTypeEnum> deviceSiteTypes;
    Map<SiteTypeEnum, List<Site>> occupiedSites;
    Map<SiteTypeEnum, List<Site>> availableSites;

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
                    System.out.println("Constraint: " + regionConstraint.getName());
                    compatibleSites.stream()
                            .filter(s -> s.getClockRegion() == regionConstraint)
                            .collect(Collectors.toList());
                }
                availableSites.put(siteType, compatibleSites);
                occupiedSites.put(siteType, new ArrayList<>());
            }
        }

        Set<ClockRegion> clockregions = new HashSet<>();
        for (Site site : availableSites.get(SiteTypeEnum.SLICEL)) {
            if (clockregions.add(site.getClockRegion()))
                System.out.println(site.getClockRegion().getName());
        }
    }

    public void placeDesign(PackedDesign packedDesign) throws IOException {
        List<Pair<EDIFHierCellInst, EDIFHierCellInst>> DSPPairs = packedDesign.DSPPairs;
        List<EDIFHierCellInst> RAMCells = packedDesign.RAMCells;
        List<List<CarryCellGroup>> CARRYChains = packedDesign.CARRYChains;
        Map<Pair<String, String>, LUTFFGroup> LUTFFGroups = packedDesign.LUTFFGroups;
        List<List<EDIFHierCellInst>> LUTGroups = packedDesign.LUTGroups;

        List<Site> occupiedDSPSites = new ArrayList<>();
        List<Site> occupiedRAMSites = new ArrayList<>();
        List<Site> occupiedCLBSites = new ArrayList<>();

        placeCarryChainSites(CARRYChains);
        placeDSPPairSites(DSPPairs, occupiedDSPSites);
        placeRAMSites(RAMCells, occupiedRAMSites);
        placeLUTFFPairGroups(LUTFFGroups);
        placeLUTGroups(LUTGroups);

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
        for (Site site : occupiedRAMSites) {
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
        if (compatibleSiteTypes.isEmpty())
            System.out.println("ERROR: device contains no SLICEL or SLICEM!");
        SiteTypeEnum selectedSiteType = compatibleSiteTypes.get(rand.nextInt(compatibleSiteTypes.size()));
        return selectedSiteType;
    }

    protected Site selectCLBSite() {
        Random rand = new Random();
        SiteTypeEnum selectedSiteType = selectCLBSiteType();
        Site selectedSite = availableSites.get(selectedSiteType).remove(rand.nextInt(availableSites.size()));
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
            int numSites = availableSites.get(selectedSiteType).size();
            selectedSite = availableSites.get(selectedSiteType).get(rand.nextInt(numSites));
            int x = selectedSite.getInstanceX();
            int y = selectedSite.getInstanceY();
            for (int i = 0; i < chainSize; i++) {
                String name = "SLICE_X" + x + "Y" + (y + i);
                if (design.getSiteInstFromSiteName(name) != null
                        || device.getSite(name) == null
                        || selectedSite.getClockRegion() != regionConstraint) {
                    validAnchor = false;
                    break;
                }
                validAnchor = true;
            }
            attempts++;
            if (attempts > 1000) {
                System.out.println("ERROR: Could not find carry chain anchor after 1000 attempts!");
                break;
            }
            if (validAnchor)
                break;
        }
        return selectedSite;
    }

    protected Site selectBRAMSite(List<Site> occupiedBRAMSites) {
        Random rand = new Random();
        List<Site> compatibleSites = new ArrayList<Site>(
                Arrays.asList(device.getAllCompatibleSites(SiteTypeEnum.RAMB18E1)));
        compatibleSites.removeAll(occupiedBRAMSites);
        Site selectedSite = compatibleSites.get(rand.nextInt(compatibleSites.size()));
        occupiedBRAMSites.add(selectedSite);
        return selectedSite;
    }

    protected Site selectDSPSite(List<Site> occupiedDSPSites, List<Tile> occupiedDSPTiles) {
        Random rand = new Random();
        List<Site> compatibleSites = new ArrayList<Site>(
                Arrays.asList(device.getAllCompatibleSites(SiteTypeEnum.DSP48E1)));
        Site selectedSite = compatibleSites.get(rand.nextInt(compatibleSites.size()));
        occupiedDSPSites.add(selectedSite);
        occupiedDSPTiles.add(selectedSite.getTile());
        return selectedSite;
    }

    protected Tile selectDSPTile(List<Site> occupiedDSPSites, List<Tile> occupiedDSPTiles) {
        Random rand = new Random();
        List<TileTypeEnum> compatibleTileTypes = new ArrayList<TileTypeEnum>();
        compatibleTileTypes.add(TileTypeEnum.DSP_L);
        compatibleTileTypes.add(TileTypeEnum.DSP_R);
        int randIndex = rand.nextInt(compatibleTileTypes.size());
        TileTypeEnum selectedTileType = compatibleTileTypes.get(randIndex);
        List<Tile> compatibleTiles = device.getAllTiles().stream()
                .filter(t -> t.getTileTypeEnum().equals(selectedTileType))
                .collect(Collectors.toList());
        compatibleTiles.removeAll(occupiedDSPTiles);
        Tile selectedTile = compatibleTiles.get(rand.nextInt(compatibleTiles.size()));
        occupiedDSPTiles.add(selectedTile);
        occupiedDSPSites.addAll(Arrays.asList(selectedTile.getSites()));
        return selectedTile;
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

    private void placeCarryChainSites(List<List<CarryCellGroup>> EDIFCarryChains)
            throws IOException {
        writer.write("\n\nPlacing carry chains... (" + EDIFCarryChains.size() + ")");
        for (List<CarryCellGroup> chain : EDIFCarryChains) {
            writer.write("\n\t\tchain size: " + chain.size());
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

    private void placeRAMSites(List<EDIFHierCellInst> RAMCells, List<Site> occupiedRAMSites)
            throws IOException {
        Random rand = new Random();
        List<Site> compatibleSites = new ArrayList<Site>(
                Arrays.asList(device.getAllCompatibleSites(SiteTypeEnum.RAMB18E1)));
        compatibleSites.removeAll(occupiedRAMSites);

        if (RAMCells.isEmpty()) {
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

            if (RAMCells.isEmpty())
                break;

            EDIFHierCellInst cell0 = RAMCells.get(0);
            SiteInst si0 = new SiteInst(cell0.getFullHierarchicalInstName(), design, SiteTypeEnum.RAMB18E1,
                    ramSites.get(0));
            si0.createCell(cell0, si0.getBEL("RAMB18E1"));
            si0.routeSite();
            compatibleSites.remove(ramSites.get(0));
            occupiedRAMSites.add(ramSites.get(0));
            RAMCells.remove(cell0);

            if (RAMCells.isEmpty())
                break;

            EDIFHierCellInst cell1 = RAMCells.get(0);
            SiteInst si1 = new SiteInst(cell1.getFullHierarchicalInstName(), design, SiteTypeEnum.RAMB18E1,
                    ramSites.get(1));
            si1.createCell(cell1, si1.getBEL("RAMB18E1"));
            si1.routeSite();
            compatibleSites.remove(ramSites.get(1));
            occupiedRAMSites.add(ramSites.get(1));
            RAMCells.remove(cell1);
        }
    } // end placeRAMSites()

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
