
package placer;

import java.io.FileWriter;
import java.io.File;
import java.io.IOException;
import java.util.stream.Collectors;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;

import com.xilinx.rapidwright.edif.EDIFHierCellInst;

import com.xilinx.rapidwright.design.ModuleInst;
import com.xilinx.rapidwright.design.Design;
import com.xilinx.rapidwright.design.SiteInst;
import com.xilinx.rapidwright.design.SitePinInst;
import com.xilinx.rapidwright.design.Net;

import com.xilinx.rapidwright.device.Device;
import com.xilinx.rapidwright.device.Tile;

import com.xilinx.rapidwright.device.Site;
import com.xilinx.rapidwright.device.SiteTypeEnum;

public class PlacerAnnealMidpoint extends Placer {

    private String graphicsDir;
    private Set<SiteTypeEnum> uniqueSiteTypes;
    private Map<SiteTypeEnum, List<Site>> allSites;
    private Map<SiteTypeEnum, Map<Site, SiteInst>> occupiedSites;

    // store chain occupation info for fast access
    private Map<SiteTypeEnum, Map<Site, List<SiteInst>>> occupiedSiteChains;
    private Random rand;

    private List<Float> costHistory;
    private List<Long> moveTimes;
    private List<Long> evalTimes;
    private List<Long> renderTimes;
    private List<Long> writeTimes;
    private List<Pair<Integer, Integer>> spiralPath = SpiralPath.generateDiamondSpiral(1000);

    public PlacerAnnealMidpoint(String rootDir, Design design, Device device) throws IOException {
        super(rootDir, design, device);
        this.placerName = "PlacerAnnealMidpoint";
        this.graphicsDir = rootDir + "/outputs/graphics/" + placerName;
        this.uniqueSiteTypes = new HashSet<>();
        this.occupiedSites = new HashMap<>();
        this.occupiedSiteChains = new HashMap<>();
        this.allSites = new HashMap<>();
        this.costHistory = new ArrayList<>();
        this.moveTimes = new ArrayList<>();
        this.evalTimes = new ArrayList<>();
        this.renderTimes = new ArrayList<>();
        this.writeTimes = new ArrayList<>();
        this.rand = new Random();
    }

    public void placeDesign(PackedDesign packedDesign) throws IOException {
        initSites();
        Float lowestCost = evaluateDesign(); // initial cost
        int staleMoves = 0;
        int totalMoves = 0;

        unplaceAllSiteInsts(packedDesign);
        randomInitialPlacement(packedDesign);

        // design.writeCheckpoint(rootDir + "/outputs/checkpoints/init_placed.dcp");

        while (true) {
            if (totalMoves > 200)
                break;
            System.out.println("totalMoves: " + totalMoves);
            long t0 = System.currentTimeMillis();
            randomMove(packedDesign);
            long t1 = System.currentTimeMillis();
            moveTimes.add(t1 - t0);

            t0 = System.currentTimeMillis();
            float currCost = evaluateDesign();
            t1 = System.currentTimeMillis();
            evalTimes.add(t1 - t0);

            this.costHistory.add(currCost);

            t0 = System.currentTimeMillis();
            ImageMaker gifFrame = new ImageMaker(design);
            gifFrame.renderAll();
            gifFrame.exportImage(graphicsDir + "/gif/" + String.format("%08d", totalMoves) + ".png", "png");
            t1 = System.currentTimeMillis();
            renderTimes.add(t1 - t0);

            // t0 = System.currentTimeMillis();
            // design.writeCheckpoint(placedDcp);
            // t1 = System.currentTimeMillis();
            // writeTimes.add(t1 - t0);

            // staleMoves = 0;
            totalMoves++;
        }

        ImageMaker imPlaced = new ImageMaker(design);
        imPlaced.renderAll();
        imPlaced.exportImage(graphicsDir + "/final_placement.png", "png");

        exportCostHistory(rootDir + "/outputs/printout/convergence.csv");
        printTimingBenchmarks();
        writer.write("\n\nTotal move iterations: " + totalMoves);
        writer.write("\n\nStale move iterations: " + staleMoves);
    }

    private void initSites() throws IOException {
        for (Site site : device.getAllSites()) {
            SiteTypeEnum siteType = site.getSiteTypeEnum();
            if (uniqueSiteTypes.add(siteType)) {
                allSites.put(siteType, new ArrayList<>());
                occupiedSites.put(siteType, new HashMap<>());
            }
            allSites.get(siteType).add(site);
        }

        occupiedSiteChains.put(SiteTypeEnum.DSP48E1, new HashMap<>());
        occupiedSiteChains.put(SiteTypeEnum.SLICEL, new HashMap<>());
        occupiedSiteChains.put(SiteTypeEnum.SLICEM, new HashMap<>());
    }

    private List<Site> findSinkSites(SiteInst si) {
        List<Site> sinkSites = si.getSitePinInsts().stream()
                .filter(spi -> spi.isOutPin())
                .map(spi -> spi.getNet())
                .map(net -> net.getSinkPins())
                .map(spis -> spis.stream()
                        .map(spi -> spi.getSite())
                        .collect(Collectors.toList()))
                .flatMap(List::stream) // List<List<Site>> into List<Site>
                // .filter(site -> site != null)
                .collect(Collectors.toList());
        return sinkSites;
    }

    private float evaluateSite(List<Site> sinkSites, Site srcSite) throws IOException {
        float cost = 0;
        for (Site sinkSite : sinkSites) {
            if (sinkSite == null)
                continue; // sink has not been placed yet
            if (sinkSite.isGlobalClkBuffer() || sinkSite.isGlobalClkPad())
                continue;
            cost = cost + srcSite.getTile().getTileManhattanDistance(sinkSite.getTile());
        }
        return cost;
    }

    public float evaluateDesign() throws IOException {
        float cost = 0;
        Collection<Net> nets = design.getNets();
        for (Net net : nets) {
            if (net.isClockNet() || net.isStaticNet())
                continue;
            Tile srcTile = net.getSourceTile();
            if (srcTile == null) // net is null if its' purely intrasite!
                continue;
            List<Tile> sinkTiles = net.getSinkPins().stream()
                    .map(spi -> spi.getTile())
                    .filter(tile -> tile != null)
                    .collect(Collectors.toList());
            for (Tile sinkTile : sinkTiles) {
                cost = cost + srcTile.getTileManhattanDistance(sinkTile);
            }
        }
        return cost;
    }

    private void unplaceAllSiteInsts(PackedDesign packedDesign) throws IOException {
        for (List<SiteInst> cascade : packedDesign.DSPSiteInstCascades) {
            for (SiteInst si : cascade) {
                unplaceSiteInst(si);
            }
        }
        for (List<SiteInst> chain : packedDesign.CARRYSiteInstChains) {
            for (SiteInst si : chain) {
                unplaceSiteInst(si);
            }
        }
        for (SiteInst si : packedDesign.CLBSiteInsts) {
            unplaceSiteInst(si);
        }
        for (SiteInst si : packedDesign.RAMSiteInsts) {
            unplaceSiteInst(si);
        }

    }

    private void randomInitialPlacement(PackedDesign packedDesign) throws IOException {
        randomInitDSPSiteCascades(packedDesign);
        randomInitCARRYSiteChains(packedDesign);
        randomInitRAMSites(packedDesign);
        randomInitCLBSites(packedDesign);
    }

    private void randomMove(PackedDesign packedDesign) throws IOException {
        randomMoveDSPSiteCascades(packedDesign);
        randomMoveCARRYSiteChains(packedDesign);
        randomMoveRAMSites(packedDesign);
        randomMoveCLBSites(packedDesign);
    }

    private void placeSiteInst(SiteInst si, Site site) {
        occupiedSites.get(si.getSiteTypeEnum()).put(site, si);
        si.place(site);
    }

    private void unplaceSiteInst(SiteInst si) {
        occupiedSites.get(si.getSiteTypeEnum()).remove(si.getSite());
        si.unPlace();
    }

    private Site proposeSite(SiteTypeEnum ste, boolean swapEnable) {
        boolean validSite = false;
        Site selectedSite = null;
        int attempts = 0;
        while (true) {
            if (attempts > 1000)
                throw new IllegalStateException("ERROR: Could not propose " + ste + " site after 1000 attempts!");
            int randIndex = rand.nextInt(allSites.get(ste).size());
            selectedSite = allSites.get(ste).get(randIndex);
            if (occupiedSiteChains.containsKey(ste)) { // never propose single site swap with a chain
                if (!occupiedSiteChains.get(ste).containsKey(selectedSite)) {
                    validSite = true;
                }
            } else if (!swapEnable) { // swapping with other single sites only
                if (!occupiedSites.get(ste).containsKey(selectedSite)) {
                    validSite = true;
                }
            } else {
                validSite = true;
            }
            if (validSite)
                break;
            attempts++;
        }
        return selectedSite;
    }

    private Pair<Integer, Integer> findMidpoint(List<Site> sinks) {
        int sum_x = 0;
        int sum_y = 0;
        for (Site sink : sinks) {
            sum_x += sink.getRpmX();
            sum_y += sink.getRpmY();
        }
        Pair<Integer, Integer> midpoint = new Pair<Integer, Integer>(sum_x / 2, sum_y / 2);
        return midpoint;
    }

    private Site proposeMidpointAnchorSite(List<SiteInst> chain, boolean swapEnable) {

        boolean validAnchor = false;
        Site selectedSite = null;
        SiteTypeEnum ste = chain.get(0).getSiteTypeEnum();

        List<Site> sinks = new ArrayList<>();
        for (SiteInst si : chain) {
            sinks.addAll(findSinkSites(si));
        }
        Pair<Integer, Integer> midpoint = findMidpoint(sinks);
        int attempts = 0;

        while (true) {

            int x = midpoint.key() + spiralPath.get(attempts).key();
            int y = midpoint.value() - chain.size() / 2 + spiralPath.get(attempts).value();
            Site site = device.getSite(getSiteTypePrefix(ste) + "X" + x + "Y" + y);
            if (site == null) {
                validAnchor = false;
                continue;
            }
            if (!allSites.get(ste).contains(site)) {
                validAnchor = false;
                continue;
            }
            if (!swapEnable) {
                if (occupiedSites.get(ste).containsKey(site)) {
                    validAnchor = false;
                    continue;
                }
            }
            if (site.getSiteTypeEnum() == ste) {
                validAnchor = true;
                selectedSite = site;
                break;
            }
        }
        return selectedSite;
    }

    private Site proposeRandomAnchorSite(SiteTypeEnum siteType, int chainSize, boolean swapEnable) {
        boolean validAnchor = false;
        Site selectedSite = null;
        int attempts = 0;
        while (true) {
            int randIndex = rand.nextInt(allSites.get(siteType).size());
            selectedSite = allSites.get(siteType).get(randIndex);
            int x = selectedSite.getInstanceX();
            int y = selectedSite.getInstanceY();
            for (int i = 0; i < chainSize; i++) {
                Site site = device.getSite(getSiteTypePrefix(siteType) + "X" + x + "Y" + (y + i));
                if (site == null) {
                    validAnchor = false;
                    break;
                }
                if (!allSites.get(siteType).contains(site)) {
                    validAnchor = false;
                    break;
                }
                if (!swapEnable) {
                    if (occupiedSites.get(siteType).containsKey(site)) {
                        validAnchor = false;
                        break;
                    }
                }
                validAnchor = true;
            }
            attempts++;
            if (attempts > 1000)
                throw new IllegalStateException(
                        "ERROR: Could not propose " + siteType + " chain anchor after 1000 attempts!");
            if (validAnchor)
                break;
        }
        return selectedSite;

    }

    private void randomInitCLBSites(PackedDesign packedDesign) throws IOException {
        for (SiteInst si : packedDesign.CLBSiteInsts) {
            Site selectedSite = proposeSite(si.getSiteTypeEnum(), false);
            placeSiteInst(si, selectedSite);
        }
    }

    private void randomInitRAMSites(PackedDesign packedDesign) throws IOException {
        for (SiteInst si : packedDesign.RAMSiteInsts) {
            Site selectedSite = proposeSite(si.getSiteTypeEnum(), false);
            placeSiteInst(si, selectedSite);
        }
    }

    private void randomInitDSPSiteCascades(PackedDesign packedDesign) throws IOException {
        randomInitSiteChains(packedDesign.DSPSiteInstCascades);
    }

    private void randomInitCARRYSiteChains(PackedDesign packedDesign) throws IOException {
        randomInitSiteChains(packedDesign.CARRYSiteInstChains);
    }

    private void randomInitSiteChains(List<List<SiteInst>> chains) throws IOException {
        for (List<SiteInst> chain : chains) {
            SiteTypeEnum siteType = chain.get(0).getSiteTypeEnum();
            Site selectedAnchor = proposeRandomAnchorSite(siteType, chain.size(), false);
            for (int i = 0; i < chain.size(); i++) {
                Site newSite = device.getSite(getSiteTypePrefix(siteType) + "X" + selectedAnchor.getInstanceX() +
                        "Y" + (selectedAnchor.getInstanceY() + i));
                occupiedSiteChains.get(siteType).put(newSite, chain);
                placeSiteInst(chain.get(i), newSite);
            }
        }
    }

    private void randomMoveRAMSites(PackedDesign packedDesign) throws IOException {
        randomMoveSingleSite(packedDesign.RAMSiteInsts);
    }

    private void randomMoveCLBSites(PackedDesign packedDesign) throws IOException {
        randomMoveSingleSite(packedDesign.CLBSiteInsts);
    }

    private void randomMoveDSPSiteCascades(PackedDesign packedDesign) throws IOException {
        randomMoveSiteChains(packedDesign.DSPSiteInstCascades);
    }

    private void randomMoveCARRYSiteChains(PackedDesign packedDesign) throws IOException {
        randomMoveSiteChains(packedDesign.CARRYSiteInstChains);
    }

    private void randomMoveSingleSite(List<SiteInst> sites) throws IOException {
        for (SiteInst si : sites) {
            SiteTypeEnum ste = si.getSiteTypeEnum();
            List<Site> homeSinks = findSinkSites(si);
            Site homeSite = si.getSite();
            Site awaySite = proposeSite(ste, true);
            float oldCost = 0;
            float newCost = 0;
            SiteInst awaySi = occupiedSites.get(ste).get(awaySite);
            if (awaySi != null) {
                List<Site> awaySinks = findSinkSites(awaySi);
                oldCost += evaluateSite(homeSinks, homeSite);
                oldCost += evaluateSite(awaySinks, awaySite);
                newCost += evaluateSite(homeSinks, awaySite);
                newCost += evaluateSite(awaySinks, homeSite);
            } else {
                oldCost += evaluateSite(homeSinks, homeSite);
                newCost += evaluateSite(homeSinks, awaySite);
            }
            if (newCost < oldCost) {
                // System.out.println("newCost: " + newCost);
                // System.out.println("oldCost: " + oldCost);
                if (awaySi != null) {
                    if (homeSite.getSiteTypeEnum() == SiteTypeEnum.RAMB18E1)
                        System.out.println("RAM Swap Accepted!");
                    unplaceSiteInst(si);
                    unplaceSiteInst(awaySi);
                    placeSiteInst(si, awaySite);
                    placeSiteInst(awaySi, homeSite);
                } else {
                    unplaceSiteInst(si);
                    placeSiteInst(si, awaySite);
                }
            }
        }
    }

    private void randomMoveSiteChains(List<List<SiteInst>> chains) throws IOException {
        loopThruChains: for (List<SiteInst> homeChain : chains) {

            SiteTypeEnum siteType = homeChain.get(0).getSiteTypeEnum();
            Site homeChainAnchor = homeChain.get(0).getSite();
            int homeInstX = homeChainAnchor.getInstanceX();
            Site homeChainTail = homeChain.get(homeChain.size() - 1).getSite();

            Site awayInitAnchor = proposeRandomAnchorSite(siteType, homeChain.size(), true);
            int awayInstX = awayInitAnchor.getInstanceX();
            Site awayInitTail = device.getSite(getSiteTypePrefix(siteType) +
                    "X" + awayInstX +
                    "Y" + (awayInitAnchor.getInstanceY() + homeChain.size() - 1));

            List<Site> awayBuffer = findBufferZone(siteType, awayInitAnchor, awayInitTail);
            List<SiteInst> siteInstsInAwayBuffer = collectSiteInstsInBuffer(siteType, awayBuffer);
            List<Site> homeBuffer = null;
            List<SiteInst> siteInstsInHomeBuffer = null;

            int sweepSize = awayBuffer.size() - homeChain.size();
            boolean legalSwap = false;

            // sweep possible home buffers to find a legal chain swap
            findLegalHomeBuffer: for (int i = 0; i <= sweepSize; i++) {
                int homeBufferAnchorInstY = homeChainAnchor.getInstanceY() - sweepSize + i;
                Site homeBufferAnchor = device.getSite(getSiteTypePrefix(siteType) +
                        "X" + homeInstX + "Y" + homeBufferAnchorInstY);
                if (homeBufferAnchor == null) // fell off the device!
                    continue findLegalHomeBuffer;

                int homeBufferTailInstY = homeBufferAnchorInstY + awayBuffer.size() - 1;
                Site homeBufferTail = device.getSite(getSiteTypePrefix(siteType) +
                        "X" + homeInstX + "Y" + homeBufferTailInstY);
                if (homeBufferTail == null) // fell off the device!
                    continue findLegalHomeBuffer;

                if (!bufferContainsOverlaps(siteType, homeBufferAnchor, homeBufferTail)) {
                    legalSwap = true;
                    homeBuffer = new ArrayList<>();
                    for (int y = homeBufferAnchorInstY; y <= homeBufferTailInstY; y++) {
                        homeBuffer.add(device.getSite(getSiteTypePrefix(siteType) +
                                "X" + homeInstX + "Y" + y));
                    }
                    siteInstsInHomeBuffer = collectSiteInstsInBuffer(siteType, homeBuffer);
                    break findLegalHomeBuffer; // found a legal chain swap
                } else {
                    continue findLegalHomeBuffer; // try again
                }
            }

            if (!legalSwap) // all possible home buffers in sweep window failed
                continue loopThruChains; // skip this chain swap proposal

            // for now, ensure no overlap between away buffer and home buffer
            if (!Collections.disjoint(awayBuffer, homeBuffer)) {
                continue loopThruChains; // skip this chain swap proposal
            }

            if (homeBuffer.size() != awayBuffer.size())
                throw new IllegalStateException("ERROR: homeBuffer.size(): " + homeBuffer.size()
                        + " != awayBuffer.size(): " + awayBuffer.size());
            if (siteInstsInHomeBuffer.size() != siteInstsInAwayBuffer.size())
                throw new IllegalStateException("ERROR: siteInstsInHomeBuffer.size(): " + siteInstsInHomeBuffer.size()
                        + " != siteInstInAwayBuffer.size(): " + siteInstsInAwayBuffer.size());
            if (homeBuffer.size() != siteInstsInHomeBuffer.size())
                throw new IllegalStateException("ERROR: homeBuffer.size(): " + homeBuffer.size()
                        + " != siteInstInHomeBuffer.size(): " + siteInstsInHomeBuffer.size());

            if (homeBuffer.size() > 16) {
                System.out.println("homeBuffer.size(): " + homeBuffer.size());
            }

            // evaluate the cost of the swap
            float oldCost = 0;
            float newCost = 0;
            for (int i = 0; i < homeBuffer.size(); i++) {
                if (siteInstsInHomeBuffer.get(i) != null) {
                    List<Site> homeSinks = findSinkSites(siteInstsInHomeBuffer.get(i));
                    for (Site sink : homeSinks) {
                        if (awayBuffer.contains(sink)) {
                            continue loopThruChains; // skip this chain swap proposal
                            // CONTINUES THE OUTER-MOST LOOP!
                        }
                    }
                    oldCost += evaluateSite(homeSinks, homeBuffer.get(i));
                    newCost += evaluateSite(homeSinks, awayBuffer.get(i));
                }
                if (siteInstsInAwayBuffer.get(i) != null) {
                    List<Site> awaySinks = findSinkSites(siteInstsInAwayBuffer.get(i));
                    for (Site sink : awaySinks) {
                        if (homeBuffer.contains(sink)) {
                            continue loopThruChains; // skip this chain swap proposal
                            // CONTINUES THE OUTER-MOST LOOP!
                        }
                    }
                    oldCost += evaluateSite(awaySinks, awayBuffer.get(i));
                    newCost += evaluateSite(awaySinks, homeBuffer.get(i));
                }
            }

            // System.out.println("newCost: " + newCost + ", oldCost: " + oldCost);
            if (newCost < oldCost) {
                for (int i = 0; i < homeBuffer.size(); i++) {
                    SiteTypeEnum ste = homeBuffer.get(i).getSiteTypeEnum();
                    SiteInst homeSi = siteInstsInHomeBuffer.get(i);
                    SiteInst awaySi = siteInstsInAwayBuffer.get(i);
                    List<SiteInst> homeSiChain = null;
                    List<SiteInst> awaySiChain = null;

                    if (homeSi != null) {
                        homeSiChain = occupiedSiteChains.get(ste).remove(homeBuffer.get(i));
                        unplaceSiteInst(homeSi);
                    }
                    if (awaySi != null) {
                        awaySiChain = occupiedSiteChains.get(ste).remove(awayBuffer.get(i));
                        unplaceSiteInst(awaySi);
                    }

                    if (homeSi != null) {
                        placeSiteInst(homeSi, awayBuffer.get(i));
                        if (homeSiChain != null) {
                            occupiedSiteChains.get(ste).put(awayBuffer.get(i), homeSiChain);
                        }
                    }
                    if (awaySi != null) {
                        placeSiteInst(awaySi, homeBuffer.get(i));
                        if (awaySiChain != null) {
                            occupiedSiteChains.get(ste).put(homeBuffer.get(i), awaySiChain);
                        }
                    }
                }
            }

        }
    }

    private String getSiteTypePrefix(SiteTypeEnum siteType) {
        String siteTypePrefix = null;
        if (siteType == SiteTypeEnum.DSP48E1)
            siteTypePrefix = "DSP48_";
        else if (siteType == SiteTypeEnum.SLICEL || siteType == SiteTypeEnum.SLICEM)
            siteTypePrefix = "SLICE_";
        else
            throw new IllegalStateException("ERROR: Could not assign a String prefix to  SiteTypeEnum: " + siteType);
        return siteTypePrefix;
    }

    private List<Site> findBufferZone(SiteTypeEnum siteType, Site initAnchor, Site initTail)
            throws IOException {
        List<Site> sites = new ArrayList<>();

        // System.out.println("initAnchor: " + initAnchor.getInstanceY());
        // System.out.println("initTail: " + initTail.getInstanceY());

        int instX = initAnchor.getInstanceX();
        int finalAnchorInstY = initAnchor.getInstanceY();
        int finalTailInstY = initTail.getInstanceY();

        // is there a chain overlap at the anchor?
        List<SiteInst> residentChainAtAnchor = occupiedSiteChains.get(siteType).get(initAnchor);
        if (residentChainAtAnchor != null) {
            Site finalAnchor = residentChainAtAnchor.get(0).getSite();
            finalAnchorInstY = finalAnchor.getInstanceY();
        }

        // is there a chain overlap at the tail?
        List<SiteInst> residentChainAtTail = occupiedSiteChains.get(siteType).get(initTail);
        if (residentChainAtTail != null) {
            Site finalTail = residentChainAtTail.get(residentChainAtTail.size() - 1).getSite();
            finalTailInstY = finalTail.getInstanceY();
        }

        String siteTypePrefix = getSiteTypePrefix(siteType);
        for (int i = finalAnchorInstY; i <= finalTailInstY; i++) {
            sites.add(device.getSite(siteTypePrefix + "X" + instX + "Y" + i));
        }

        // System.out.println("finalAnchor: " + finalAnchorInstY);
        // System.out.println("finalTail: " + finalTailInstY);
        // System.out.println();

        return sites;
    }

    private boolean bufferContainsOverlaps(SiteTypeEnum siteType, Site initAnchor, Site initTail)
            throws IOException {
        boolean containsOverlap = false;
        List<SiteInst> residentChainAtAnchor = occupiedSiteChains.get(siteType).get(initAnchor);
        if (residentChainAtAnchor != null) {
            Site residentAnchor = residentChainAtAnchor.get(0).getSite();
            if (residentAnchor.getInstanceY() < initAnchor.getInstanceY())
                containsOverlap = true;
        }
        List<SiteInst> residentChainAtTail = occupiedSiteChains.get(siteType).get(initAnchor);
        if (residentChainAtTail != null) {
            Site residentTail = residentChainAtTail.get(residentChainAtTail.size() - 1).getSite();
            if (residentTail.getInstanceY() > initTail.getInstanceY())
                containsOverlap = true;
        }
        return containsOverlap;
    }

    private List<SiteInst> collectSiteInstsInBuffer(SiteTypeEnum siteType, List<Site> bufferZone)
            throws IOException {
        List<SiteInst> sis = new ArrayList<>();
        for (Site site : bufferZone) {
            SiteInst si = occupiedSites.get(siteType).get(site);
            if (si == null) {
                sis.add(null);
            } else {
                sis.add(si);
            }
            // adds null if key doesnt exist in map
        }
        return sis;
    }

    public void printTimingBenchmarks() throws IOException {
        writer.write("\n\nPrinting Move Times... ");
        for (int i = 0; i < moveTimes.size(); i++) {
            writer.write("\n\tIter: " + i + ", Time (ms): " + moveTimes.get(i));
        }
        writer.write("\n\nPrinting Eval Times... ");
        for (int i = 0; i < evalTimes.size(); i++) {
            writer.write("\n\tIter: " + i + ", Time (ms): " + evalTimes.get(i));
        }
        writer.write("\n\nPrinting Render Times...");
        for (int i = 0; i < renderTimes.size(); i++) {
            writer.write("\n\tIter: " + i + ", Time (ms): " + renderTimes.get(i));
        }
        writer.write("\n\nPrinting DCP Write Times... ");
        for (int i = 0; i < writeTimes.size(); i++) {
            writer.write("\n\tIter: " + i + ", Time (ms): " + writeTimes.get(i));
        }
    }

    public void exportCostHistory(String fileName) throws IOException {
        FileWriter csv = new FileWriter(fileName);
        csv.write("Iter, Cost");
        for (int i = 0; i < costHistory.size(); i++) {
            csv.write("\n" + i + ", " + costHistory.get(i));
        }
        if (csv != null)
            csv.close();
    }

    private void printSiteInstPlacements(PackedDesign packedDesign) throws IOException {
        for (List<SiteInst> cascade : packedDesign.DSPSiteInstCascades) {
            for (SiteInst si : cascade) {
                String site = si == null ? "Null!" : si.getName();
                writer.write("\nSiteInst: " + si.getName() + ", Site: " + site);
            }
        }
        for (List<SiteInst> chain : packedDesign.CARRYSiteInstChains) {
            for (SiteInst si : chain) {
                String site = si == null ? "Null!" : si.getName();
                writer.write("\nSiteInst: " + si.getName() + ", Site: " + site);
            }
        }
        for (SiteInst si : packedDesign.CLBSiteInsts) {
            String site = si == null ? "Null!" : si.getName();
            writer.write("\nSiteInst: " + si.getName() + ", Site: " + site);
        }
        for (SiteInst si : packedDesign.RAMSiteInsts) {
            String site = si == null ? "Null!" : si.getName();
            writer.write("\nSiteInst: " + si.getName() + ", Site: " + site);
        }
    }
}
