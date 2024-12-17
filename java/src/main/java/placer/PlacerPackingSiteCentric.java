
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

    private Map<EDIFHierCellInst, List<EDIFHierCellInst>> findLUTFFTrees(
            Map<String, List<EDIFHierCellInst>> EDIFCellGroups) throws IOException {
        List<EDIFHierCellInst> visitedLUTs = new ArrayList<>();
        List<EDIFHierCellInst> visitedFFs = new ArrayList<>();
        Map<EDIFHierCellInst, List<EDIFHierCellInst>> trees = new HashMap<>();
        for (EDIFHierCellInst LUT_EHCI : EDIFCellGroups.get("LUT")) {
            EDIFHierPortInst LUT_EHPI = LUT_EHCI.getPortInst("O");
            EDIFHierNet HNET = LUT_EHPI.getHierarchicalNet();
            // exclude sources, include sinks
            List<EDIFHierPortInst> sinkPorts = HNET.getLeafHierPortInsts(false, true);
            if (sinkPorts.size() < 2) // less than 2 sinks?
                continue;
            List<EDIFHierCellInst> FF_EHCIS = sinkPorts.stream()
                    .map(ehpi -> ehpi.getHierarchicalInst().getChild(ehpi.getPortInst().getCellInst().getName()))
                    .filter(ehci -> ehci.getCellType().getName().equals("FDRE"))
                    .collect(Collectors.toList());
            if (FF_EHCIS.size() < 2) // less than 2 ff sinks?
                continue;
            for (EDIFHierCellInst ff : FF_EHCIS)
                visitedLUTs.add(LUT_EHCI);
            visitedFFs.addAll(FF_EHCIS);
            trees.put(LUT_EHCI, FF_EHCIS);
        }
        EDIFCellGroups.get("LUT").removeAll(visitedLUTs);
        EDIFCellGroups.get("FDRE").removeAll(visitedFFs);
        return trees;
    }

    private List<EDIFHierCellInst[]> findLUTFFPairs(Map<String, List<EDIFHierCellInst>> EDIFCellGroups)
            throws IOException {
        List<EDIFHierCellInst> visitedLUTs = new ArrayList<>();
        List<EDIFHierCellInst> visitedFFs = new ArrayList<>();
        List<EDIFHierCellInst[]> pairs = new ArrayList<>();
        for (EDIFHierCellInst LUT_EHCI : EDIFCellGroups.get("LUT")) {
            EDIFHierPortInst LUT_EHPI = LUT_EHCI.getPortInst("O");
            EDIFHierNet HNET = LUT_EHPI.getHierarchicalNet();
            // exclude sources, include sinks
            List<EDIFHierPortInst> sinkPorts = HNET.getLeafHierPortInsts(false, true);
            if (sinkPorts.size() != 1)
                continue;
            EDIFHierPortInst FF_EHPI = sinkPorts.get(0);
            EDIFHierCellInst FF_EHCI = FF_EHPI.getHierarchicalInst()
                    .getChild(FF_EHPI.getPortInst().getCellInst().getName());
            if (!FF_EHCI.getCellType().getName().contains("FDRE"))
                continue;
            visitedLUTs.add(LUT_EHCI);
            visitedFFs.add(FF_EHCI);
            EDIFHierCellInst[] pair = new EDIFHierCellInst[2];
            pair[0] = LUT_EHCI;
            pair[1] = FF_EHCI;
            pairs.add(pair);
        }
        EDIFCellGroups.get("LUT").removeAll(visitedLUTs);
        EDIFCellGroups.get("FDRE").removeAll(visitedFFs);
        return pairs;
    }

    private List<EDIFHierCellInst[]> findDSPPairs(Map<String, List<EDIFHierCellInst>> EDIFCellGroups)
            throws IOException {
        List<EDIFHierCellInst> visitedDSPs = new ArrayList<>();
        List<EDIFHierCellInst[]> pairs = new ArrayList<>();
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
            EDIFHierCellInst[] pair = new EDIFHierCellInst[2];
            pair[0] = DSP_CIN;
            pair[1] = DSP_COUT;
            pairs.add(pair);
        }
        EDIFCellGroups.get("DSP48E1").removeAll(visitedDSPs);
        return pairs;
    }

    private void placeDSPPairSites(List<EDIFHierCellInst[]> EDIFDSPPairs, List<String> occupiedDSPSites,
            Map<String, List<EDIFHierCellInst>> EDIFCellGroups) throws IOException {
        Random rand = new Random();
        List<Site> compatibleSites = new ArrayList<Site>(
                Arrays.asList(device.getAllCompatibleSites(SiteTypeEnum.DSP48E1)));
        for (EDIFHierCellInst[] pair : EDIFDSPPairs) {
            Tile selectedTile = compatibleSites.get(rand.nextInt(compatibleSites.size())).getTile();
            Site[] dspSitePair = selectedTile.getSites();

            SiteInst si0 = new SiteInst(pair[0].getFullHierarchicalInstName(), design, SiteTypeEnum.DSP48E1,
                    dspSitePair[0]);
            si0.createCell(pair[0], si0.getBEL("DSP48E1"));
            // si0.routeSite();

            SiteInst si1 = new SiteInst(pair[1].getFullHierarchicalInstName(), design, SiteTypeEnum.DSP48E1,
                    dspSitePair[1]);
            si1.createCell(pair[1], si1.getBEL("DSP48E1"));
            // si1.routeSite();

            compatibleSites.remove(dspSitePair[0]);
            compatibleSites.remove(dspSitePair[1]);
            occupiedDSPSites.add(dspSitePair[0].getName());
            occupiedDSPSites.add(dspSitePair[1].getName());
        }
    } // end placeDSPPairSites()

    private List<List<EDIFHierCellInst>> findCarryChains(Map<String, List<EDIFHierCellInst>> EDIFCellGroups) {
        List<List<EDIFHierCellInst>> chains = new ArrayList<>();
        while (!EDIFCellGroups.get("CARRY4").isEmpty()) {
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

            while (true) {
                chain.add(currCell);
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
            EDIFCellGroups.get("CARRY4").removeAll(chain);
            chains.add(chain);
        }
        return chains;
    }

    private String findCarryChainAnchorSite(SiteTypeEnum selectedSiteType, List<EDIFHierCellInst> chain)
            throws IOException {
        Map<String, Integer> minmax = getCoordinateMinMaxOfType(selectedSiteType);
        int x_max = minmax.get("X_MAX");
        int x_min = minmax.get("X_MIN");
        int y_max = minmax.get("Y_MAX");
        int y_min = minmax.get("Y_MIN");
        writer.write("\n\tselectedSiteType: " + selectedSiteType);
        writer.write(
                "\n\tX_MAX: " + x_max + ", X_MIN: " + x_min + ", Y_MAX: " + y_max + ", Y_MIN: " + y_min);
        String anchorSiteName = null;
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
                    anchorSiteName = "SLICE_X" + x + "Y" + y;
                }
            }
            attempts++;
        }
        return anchorSiteName;
    }

    private void placeCarrySite(EDIFHierCellInst carryCell, SiteInst si,
            Map<String, List<String>> occupiedBELs,
            Map<String, List<EDIFHierCellInst>> EDIFCellGroups) {

        System.out.println("SiteTypeEnum: " + si.getSiteTypeEnum());
        si.createCell(carryCell, si.getBEL("CARRY4"));
        System.out.println("Created CARRY4");

        // if a carry is used, just treat all the site's lanes as used
        occupiedBELs.put(si.getName(), new ArrayList<>(
                List.of("AFF", "A5FF", "BFF", "B5FF", "CFF", "C5FF", "DFF", "D5FF",
                        "A5LUT", "A6LUT", "B5LUT", "B6LUT", "C5LUT", "C6LUT", "D5LUT", "D6LUT")));

        Map<String, String[]> O_CARRY_FF_MAP = new HashMap<>();
        O_CARRY_FF_MAP.put("O[0]", new String[] { "AFF", "A5FF" });
        O_CARRY_FF_MAP.put("O[1]", new String[] { "BFF", "B5FF" });
        O_CARRY_FF_MAP.put("O[2]", new String[] { "CFF", "C5FF" });
        O_CARRY_FF_MAP.put("O[3]", new String[] { "DFF", "D5FF" });

        for (Map.Entry<String, String[]> entry : O_CARRY_FF_MAP.entrySet()) {
            String PORT_NAME = entry.getKey();
            System.out.println("PORT_NAME: " + PORT_NAME);
            EDIFHierPortInst ehpi = carryCell.getPortInst(PORT_NAME);
            EDIFHierNet hnet = ehpi.getHierarchicalNet();
            // bool include sources, bool include sinks
            EDIFHierPortInst sinkPort = hnet.getLeafHierPortInsts(false, true).get(0);
            EDIFHierCellInst sinkCell = sinkPort.getHierarchicalInst()
                    .getChild(sinkPort.getPortInst().getCellInst().getName());
            // for now, just always use FF not 5FF
            // System.out.println("sinkCell: " + sinkCell.getCellType().getName());
            if (sinkCell.getCellType().getName().contains("FDRE")) {
                si.createCell(sinkCell, si.getBEL(entry.getValue()[0])); // XFF
                System.out.println("Created FDRE");
            }
        }

        Map<String, String[]> S_CARRY_LUT_MAP = new HashMap<>();
        S_CARRY_LUT_MAP.put("S[0]", new String[] { "A5LUT", "A6LUT" });
        S_CARRY_LUT_MAP.put("S[1]", new String[] { "B5LUT", "B6LUT" });
        S_CARRY_LUT_MAP.put("S[2]", new String[] { "C5LUT", "C6LUT" });
        S_CARRY_LUT_MAP.put("S[3]", new String[] { "D5LUT", "D6LUT" });

        for (Map.Entry<String, String[]> entry : S_CARRY_LUT_MAP.entrySet()) {
            String PORT_NAME = entry.getKey();
            EDIFHierPortInst ehpi = carryCell.getPortInst(PORT_NAME);
            EDIFHierNet hnet = ehpi.getHierarchicalNet();
            // bool include sources, bool include sinks
            EDIFHierPortInst sourcePort = hnet.getLeafHierPortInsts(true, false).get(0);
            EDIFHierCellInst sourceCell = sourcePort.getHierarchicalInst()
                    .getChild(sourcePort.getPortInst().getCellInst().getName());
            String sourceCellType = sourceCell.getCellType().getName();
            // if (sourceCellType == "LUT6") {
            // si.createCell(sourceCell, si.getBEL(entry.getValue()[1])); // X6LUT
            // System.out.println("Created LUT6");
            // } else if (sourceCellType.contains("LUT")) {
            // si.createCell(sourceCell, si.getBEL(entry.getValue()[0])); // X5LUT
            // System.out.println("Created LUT5");
            // }
            if (sourceCellType.contains("LUT")) {
                si.createCell(sourceCell, si.getBEL(entry.getValue()[1])); // X6LUT
            }
        }
    }

    private void placeCarryChainSites(List<List<EDIFHierCellInst>> EDIFCarryChains,
            Map<String, List<String>> occupiedBELs, Map<String, List<EDIFHierCellInst>> EDIFCellGroups)
            throws IOException {
        writer.write("\n\nPlacing carry chains... (" + EDIFCarryChains.size() + ")");
        // PLACE CARRY CHAINS
        for (List<EDIFHierCellInst> chain : EDIFCarryChains) {
            writer.write("\n\tchain size: " + chain.size());
            Random rand = new Random();
            EDIFHierCellInst anchorCell = chain.get(0);
            List<SiteTypeEnum> compatibleSiteTypes = new ArrayList<SiteTypeEnum>();
            compatibleSiteTypes.add(SiteTypeEnum.SLICEL);
            compatibleSiteTypes.add(SiteTypeEnum.SLICEM);

            int randIndex = rand.nextInt(compatibleSiteTypes.size());
            SiteTypeEnum selectedSiteType = compatibleSiteTypes.get(randIndex);
            String anchorSiteName = findCarryChainAnchorSite(selectedSiteType, chain);
            Site anchorSite = device.getSite(anchorSiteName);

            // find and place the anchor cell
            if (anchorSiteName == null) {
                writer.write("\nWARNING: COULD NOT PLACE CARRY CHAIN ANCHOR!");
                break;
            } else {
                SiteInst si = new SiteInst(anchorCell.getFullHierarchicalInstName(), design, selectedSiteType,
                        anchorSite);
                placeCarrySite(chain.get(0), si, occupiedBELs, EDIFCellGroups);
            }

            // place the rest of the chain vertically
            for (int i = 1; i < chain.size(); i++) {
                String siteName = "SLICE_X" + anchorSite.getInstanceX() + "Y" + (anchorSite.getInstanceY() + i);
                Site site = device.getSite(siteName);
                SiteInst si = new SiteInst(chain.get(i).getFullHierarchicalInstName(), design, selectedSiteType, site);
                placeCarrySite(chain.get(i), si, occupiedBELs, EDIFCellGroups);
            }

        } // end for (List<EDIFCellInst> chain : EDIFCarryChains)
    } // end placeCarryChainSites()

    private void placeLUTFFTrees(Map<EDIFHierCellInst, List<EDIFHierCellInst>> LUTFFTrees,
            Map<String, List<String>> occupiedBELs, Map<String, List<EDIFHierCellInst>> EDIFCellGroups) {

    }

    private void placeLUTFFPairs(List<EDIFHierCellInst[]> LUTFFPairs, Map<String, List<String>> occupiedBELs,
            Map<String, List<EDIFHierCellInst>> EDIFCellGroups) {

    }

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

        List<EDIFHierCellInst[]> DSPPairs = findDSPPairs(EDIFCellGroups);
        List<List<EDIFHierCellInst>> CARRYChains = findCarryChains(EDIFCellGroups);
        Map<EDIFHierCellInst, List<EDIFHierCellInst>> LUTFFTrees = findLUTFFTrees(EDIFCellGroups);
        List<EDIFHierCellInst[]> LUTFFPairs = findLUTFFPairs(EDIFCellGroups);

        writer.write("\n\nPrinting LUTFFPairs... (" + LUTFFPairs.size() + ")");
        for (EDIFHierCellInst[] pair : LUTFFPairs) {
            writer.write("\n\t(" + pair[0].getCellType().getName() + ": " + pair[0].getFullHierarchicalInstName()
                    + ", " + pair[1].getCellType().getName() + ": " + pair[1].getFullHierarchicalInstName() + ")");
        }

        writer.write("\n\nPrinting LUTFFTrees... (" + LUTFFTrees.size() + ")");
        for (Map.Entry<EDIFHierCellInst, List<EDIFHierCellInst>> entry : LUTFFTrees.entrySet()) {
            EDIFHierCellInst LUT = entry.getKey();
            List<EDIFHierCellInst> FFS = entry.getValue();
            writer.write("\n\tRoot LUT: " + LUT.getCellType().getName() + ": " + LUT.getFullHierarchicalInstName());
            for (EDIFHierCellInst FF : FFS)
                writer.write("\n\t\tLeaf FF: " + FF.getCellType().getName() + ": " + FF.getFullHierarchicalInstName());
        }

        writer.write("\n\nPrinting DSPPairs... (" + DSPPairs.size() + ")");
        for (EDIFHierCellInst[] pair : DSPPairs) {
            writer.write("\n\t(" + pair[0].getCellType().getName() + ": " + pair[0].getFullHierarchicalInstName()
                    + ", " + pair[1].getCellType().getName() + ": " + pair[1].getFullHierarchicalInstName() + ")");
        }

        writer.write("\n\nPrinting CARRYChains... (" + CARRYChains.size() + ")");
        for (List<EDIFHierCellInst> chain : CARRYChains) {
            writer.write("\n\tAnchor: " + chain.get(0).getFullHierarchicalInstName());
            for (int i = 1; i < chain.size(); i++) {
                writer.write("\n\t\t" + chain.get(i).getFullHierarchicalInstName());
            }
        }

        List<String> occupiedDSPSites = new ArrayList<>();
        List<String> occupiedRAMSites = new ArrayList<>();
        Map<String, List<String>> occupiedBELs = new HashMap<>();

        placeCarryChainSites(CARRYChains, occupiedBELs, EDIFCellGroups);
        placeDSPPairSites(DSPPairs, occupiedDSPSites, EDIFCellGroups);

        Map<String, List<Cell>> cellGroups = new HashMap<>();
        cellGroups.put("IBUF", new ArrayList<>());
        cellGroups.put("OBUF", new ArrayList<>());
        cellGroups.put("VCC", new ArrayList<>());
        cellGroups.put("GND", new ArrayList<>());
        cellGroups.put("CARRY4", new ArrayList<>());
        cellGroups.put("LUT", new ArrayList<>());
        cellGroups.put("FDRE", new ArrayList<>());
        cellGroups.put("DSP48E1", new ArrayList<>());
        cellGroups.put("RAMB18E1", new ArrayList<>());

    } // end placeDesign()

} // end class

// List<List<SiteInst>> CARRYSiteInsts = CARRYChains.stream()
// .map(chain -> buildCarryChainSiteInsts(chain, EDIFCellGroups))
// .collect(Collectors.toList());
// List<List<SiteInst>> CARRYSiteInsts = new ArrayList<>();
// for (List<EDIFHierCellInst> edifChain : CARRYChains) {
// List<SiteInst> siteChain = buildCarryChainSiteInsts(edifChain,
// EDIFCellGroups);
// CARRYSiteInsts.add(siteChain);
// }
// writer.write("\n\nPrinting Carry SiteInsts... (" + CARRYSiteInsts.size() +
// ")");
// for (List<SiteInst> chain : CARRYSiteInsts) {
// writer.write("\n\tCarry SiteInst Cells...");
// for (SiteInst si : chain) {
// Map<String, Cell> BEL_CELL_MAP = si.getCellMap();
// for (Map.Entry<String, Cell> entry : BEL_CELL_MAP.entrySet()) {
// writer.write("\n\t\tCell: " + entry.getValue().getName() + ", BEL: " +
// entry.getKey());
// }
// }
// }
