
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

// This placer first places cell types in stages:
// 1) Random init and move DSP and RAM siteinsts.
// 2) Random init CARRY chains and move DSP, RAM, and CARRY chain.
// 3) Random init loose CLB siteinsts and move all.
public class PlacerGreedyRandom3 extends Placer {

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

    public PlacerGreedyRandom3(String rootDir, Design design, Device device) throws IOException {
        super(rootDir, design, device);
        this.placerName = "PlacerGreedyRandom3";
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

        int frameCounter = 1;
        while (true) {
            if (totalMoves > 10)
                break;
            System.out.println("totalMoves = " + totalMoves);
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
            gifFrame.exportImage(graphicsDir + "/gif/" + String.format("%08d", frameCounter) + ".png", "png");
            t1 = System.currentTimeMillis();
            renderTimes.add(t1 - t0);

            frameCounter++;

            // t0 = System.currentTimeMillis();
            // design.writeCheckpoint(placedDcp);
            // t1 = System.currentTimeMillis();
            // writeTimes.add(t1 - t0);

            // staleMoves = 0;
            totalMoves++;
        }

        design.writeCheckpoint(placedDcp);

        ImageMaker imPlaced = new ImageMaker(design);
        imPlaced.renderAll();
        imPlaced.exportImage(graphicsDir + "/final_placement.png", "png");

        exportCostHistory(rootDir + "/outputs/printout/" + placerName + ".csv");
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

    private List<Site> findSinkSites(SiteInst si) throws IOException {
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
        if (srcSite == null)
            return 0; // sink is null if net is purely intrasite!
        float cost = 0;
        for (Site sinkSite : sinkSites) {
            if (sinkSite == null)
                continue; // sink has not been placed yet
            cost = cost + srcSite.getTile().getTileManhattanDistance(sinkSite.getTile());
        }
        return cost;
    }

    public float evaluateDesign() throws IOException {
        float cost = 0;
        Collection<Net> nets = design.getNets();
        for (Net net : nets) {
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
                si.unPlace();
            }
        }
        for (List<SiteInst> chain : packedDesign.CARRYSiteInstChains) {
            for (SiteInst si : chain) {
                si.unPlace();
            }
        }
        for (SiteInst si : packedDesign.CLBSiteInsts) {
            si.unPlace();
        }
        for (SiteInst si : packedDesign.RAMSiteInsts) {
            si.unPlace();
        }

    }

    private void randomInitialPlacement(PackedDesign packedDesign) throws IOException {
        randomInitDSPSiteCascades(packedDesign);
        randomInitCARRYSiteChains(packedDesign);
        randomInitRAMSites(packedDesign);
        // randomInitCLBSites(packedDesign);
    }

    private void randomMove(PackedDesign packedDesign) throws IOException {
        // Chunkiest movements first
        // randomMoveDSPSiteCascades(packedDesign);
        randomMoveCARRYSiteChains(packedDesign);
        // randomMoveRAMSites(packedDesign);
        // randomMoveCLBSites(packedDesign);
    }

    private void placeSiteInst(SiteInst si, Site site) {
        occupiedSites.get(si.getSiteTypeEnum()).put(site, si);
        si.place(site);
    }

    private void unplaceSiteInst(SiteInst si) {
        occupiedSites.get(si.getSiteTypeEnum()).remove(si.getSite());
        si.unPlace();
    }

    private void randomInitCLBSites(PackedDesign packedDesign) throws IOException {
        for (SiteInst si : packedDesign.CLBSiteInsts) {
            Site selectedSite = proposeSite(si.getSiteTypeEnum());
            placeSiteInst(si, selectedSite);
        }
    }

    private void randomInitRAMSites(PackedDesign packedDesign) throws IOException {
        for (SiteInst si : packedDesign.RAMSiteInsts) {
            Site selectedSite = proposeSite(si.getSiteTypeEnum());
            placeSiteInst(si, selectedSite);
        }
    }

    private void randomInitSiteChains(List<List<SiteInst>> chains) throws IOException {
        for (List<SiteInst> chain : chains) {
            SiteTypeEnum siteType = chain.get(0).getSiteTypeEnum();
            Site selectedAnchor = proposeChainAnchorSite(siteType, chain.size());
            for (int i = 0; i < chain.size(); i++) {
                Site newSite = device.getSite(getSiteTypePrefix(siteType) + "X" + selectedAnchor.getInstanceX() +
                        "Y" + (selectedAnchor.getInstanceY() + i));
                occupiedSiteChains.get(siteType).put(newSite, chain);
                placeSiteInst(chain.get(i), newSite);
            }
        }
    }

    private void randomInitDSPSiteCascades(PackedDesign packedDesign) throws IOException {
        randomInitSiteChains(packedDesign.DSPSiteInstCascades);
    }

    private void randomInitCARRYSiteChains(PackedDesign packedDesign) throws IOException {
        randomInitSiteChains(packedDesign.CARRYSiteInstChains);
    }

    private void randomMoveCLBSites(PackedDesign packedDesign) throws IOException {
        for (SiteInst si : packedDesign.CLBSiteInsts) {
            SiteTypeEnum ste = si.getSiteTypeEnum();
            List<Site> sinkSites = findSinkSites(si);
            if (si.getSite() == null) {
                System.out.println("SiteInst null: " + si.getName());
            }
            float oldCost = evaluateSite(sinkSites, si.getSite());
            Site newSite = proposeSite(ste);
            float newCost = evaluateSite(sinkSites, newSite);
            if (newCost < oldCost) {
                unplaceSiteInst(si);
                placeSiteInst(si, newSite);
            }
        }
    }

    private void randomMoveRAMSites(PackedDesign packedDesign) throws IOException {
        for (SiteInst si : packedDesign.RAMSiteInsts) {
            List<Site> sinkSites = findSinkSites(si);
            float oldCost = evaluateSite(sinkSites, si.getSite());
            SiteTypeEnum ste = si.getSiteTypeEnum();
            Site newSite = proposeSite(ste);
            float newCost = evaluateSite(sinkSites, newSite);
            if (newCost < oldCost) {
                unplaceSiteInst(si);
                placeSiteInst(si, newSite);
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
        int finalBufferSize = finalTailInstY - finalAnchorInstY + 1;
        for (int i = 0; i < finalBufferSize; i++) {
            sites.add(device.getSite(siteTypePrefix + "X" + instX + "Y" + (finalAnchorInstY + i)));
        }

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
            Site residentTail = residentChainAtTail.get(0).getSite();
            if (residentTail.getInstanceY() > initTail.getInstanceY())
                containsOverlap = true;
        }
        return containsOverlap;
    }

    private List<SiteInst> collectSiteInstsInBuffer(SiteTypeEnum siteType, List<Site> bufferZone)
            throws IOException {
        List<SiteInst> sis = new ArrayList<>();
        for (Site site : bufferZone) {
            sis.add(occupiedSites.get(siteType).get(site));
            // adds null if key doesnt exist in map
        }
        return sis;
    }

    private void randomMoveDSPSiteCascades(PackedDesign packedDesign) throws IOException {
        randomMoveSiteChains(packedDesign.DSPSiteInstCascades);
    }

    private void randomMoveCARRYSiteChains(PackedDesign packedDesign) throws IOException {
        randomMoveSiteChains(packedDesign.CARRYSiteInstChains);
    }

    private void randomMoveSiteChains(List<List<SiteInst>> chains) throws IOException {
        loopThruChains: for (List<SiteInst> homeChain : chains) {

            SiteTypeEnum siteType = homeChain.get(0).getSiteTypeEnum();

            Site homeChainAnchor = homeChain.get(0).getSite();

            if (homeChainAnchor == null) {
                System.out.println("homeChainAnchor null!");
                continue;
            }
            int homeInstX = homeChainAnchor.getInstanceX();
            Site homeChainTail = homeChain.get(homeChain.size() - 1).getSite();
            if (homeChainTail == null) {
                System.out.println("homeChainTail null!");
                continue;
            }
            System.out.println("homeChainAnchor: " + homeChainAnchor.getName());
            System.out.println("homeChainTail: " + homeChainTail.getName());

            Site awayInitAnchor = proposeChainAnchorSite(siteType, homeChain.size());
            int awayInstX = awayInitAnchor.getInstanceX();
            Site awayInitTail = device.getSite(getSiteTypePrefix(siteType) +
                    "X" + awayInstX +
                    "Y" + (awayInitAnchor.getInstanceY() + homeChain.size() - 1));
            System.out.println("awayInitAnchor: " + awayInitAnchor.getName());
            System.out.println("awayInitTail: " + awayInitTail.getName());

            List<Site> awayBuffer = findBufferZone(siteType, awayInitAnchor, awayInitTail);
            List<SiteInst> siteInstsInAwayBuffer = collectSiteInstsInBuffer(siteType, awayBuffer);
            List<Site> homeBuffer = null;
            List<SiteInst> siteInstsInHomeBuffer = null;

            int sweepSize = awayBuffer.size() - homeChain.size() + 1;
            System.out.println("\tawayBuffer.size(): " + awayBuffer.size());
            System.out.println("\thomeChain.size(): " + homeChain.size());
            System.out.println("\tsweepSize: " + sweepSize);
            boolean legalSwap = false;

            // sweep possible home buffers to find a legal chain swap
            findLegalHomeBuffer: for (int i = 0; i < sweepSize; i++) {
                // System.out.println("Sweep: " + i);
                int homeBufferAnchorInstY = homeChainAnchor.getInstanceY() - sweepSize + i;
                Site homeBufferAnchor = device.getSite(getSiteTypePrefix(siteType) +
                        "X" + homeInstX + "Y" + homeBufferAnchorInstY);
                if (homeBufferAnchor == null) // fell off the device!
                    continue findLegalHomeBuffer;

                int homeBufferTailInstY = homeBufferAnchorInstY + awayBuffer.size();
                Site homeBufferTail = device.getSite(getSiteTypePrefix(siteType) +
                        "X" + homeInstX + "Y" + homeBufferTailInstY);
                if (homeBufferTail == null) // fell off the device!
                    continue findLegalHomeBuffer;

                // System.out.println("homeBufferAnchorInstY: " + homeBufferAnchorInstY);
                // System.out.println("homeBufferTailInstY: " + homeBufferTailInstY);

                if (!bufferContainsOverlaps(siteType, homeBufferAnchor, homeBufferTail)) {
                    legalSwap = true;
                    homeBuffer = new ArrayList<>();
                    for (int j = homeBufferAnchorInstY; j < homeBufferTailInstY; j++) {
                        // System.out.println("j: " + j);
                        homeBuffer.add(device.getSite(getSiteTypePrefix(siteType) +
                                "X" + homeInstX + "Y" + (homeBufferAnchorInstY + j)));
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
                    oldCost = oldCost + evaluateSite(homeSinks, homeBuffer.get(i));
                    newCost = newCost + evaluateSite(homeSinks, awayBuffer.get(i));
                }
                if (siteInstsInAwayBuffer.get(i) != null) {
                    List<Site> awaySinks = findSinkSites(siteInstsInAwayBuffer.get(i));
                    for (Site sink : awaySinks) {
                        if (homeBuffer.contains(sink)) {
                            continue loopThruChains; // skip this chain swap proposal
                            // CONTINUES THE OUTER-MOST LOOP!
                        }
                    }
                    oldCost = oldCost + evaluateSite(awaySinks, awayBuffer.get(i));
                    newCost = newCost + evaluateSite(awaySinks, homeBuffer.get(i));
                }
            }

            if (newCost < oldCost) {
                for (int i = 0; i < homeBuffer.size(); i++) {
                    SiteInst homeSi = siteInstsInHomeBuffer.get(i);
                    SiteInst awaySi = siteInstsInAwayBuffer.get(i);

                    List<SiteInst> homeSiChain = null;
                    List<SiteInst> awaySiChain = null;

                    if (homeSi != null) {
                        System.out.println("Unplaced homeSi " + homeSi.getSiteName());
                        SiteTypeEnum ste = homeSi.getSiteTypeEnum();
                        homeSiChain = occupiedSiteChains.get(ste).remove(homeSi.getSite());
                        unplaceSiteInst(homeSi);
                    }
                    if (awaySi != null) {
                        System.out.println("Unplaced awaySi " + awaySi.getSiteName());
                        SiteTypeEnum ste = awaySi.getSiteTypeEnum();
                        awaySiChain = occupiedSiteChains.get(ste).remove(awaySi.getSite());
                        unplaceSiteInst(awaySi);
                    }

                    if (homeSi != null) {
                        SiteTypeEnum ste = homeSi.getSiteTypeEnum();
                        if (homeSiChain != null)
                            occupiedSiteChains.get(ste).put(awayBuffer.get(i), homeSiChain);
                        placeSiteInst(homeSi, awayBuffer.get(i));
                        System.out.println("Placed homeSi " + homeSi.getSiteName());
                    }
                    if (awaySi != null) {
                        SiteTypeEnum ste = awaySi.getSiteTypeEnum();
                        if (awaySiChain != null) {
                            occupiedSiteChains.get(ste).put(homeBuffer.get(i), awaySiChain);
                        }
                        placeSiteInst(awaySi, homeBuffer.get(i));
                        System.out.println("Placed awaySi " + awaySi.getSiteName());
                    }
                }
            }

        }
    }

    // private void randomMoveCARRYSiteChains(PackedDesign packedDesign) throws
    // IOException {
    // SiteTypeEnum selectedSiteType = SiteTypeEnum.SLICEL;
    // for (List<SiteInst> chain : packedDesign.CARRYSiteInstChains) {
    // Site selectedAnchor = proposeCARRYAnchorSite(selectedSiteType, chain.size());
    // float oldCost = 0;
    // float newCost = 0;
    // List<Site> newSiteChain = new ArrayList<>();
    // for (int i = 0; i < chain.size(); i++) {
    // List<Site> sinkSites = findSinkSites(chain.get(i));
    // oldCost = oldCost + evaluateSite(sinkSites, chain.get(i).getSite());
    // Site newSite = device
    // .getSite("SLICE_X" + selectedAnchor.getInstanceX() + "Y" +
    // (selectedAnchor.getInstanceY() + i));
    // newSiteChain.add(newSite);
    // newCost = newCost + evaluateSite(sinkSites, newSite);
    // }
    // if (newCost < oldCost) {
    // for (int i = 0; i < chain.size(); i++) {
    // unplaceSiteInst(chain.get(i));
    // placeSiteInst(chain.get(i), newSiteChain.get(i));
    // }
    // }
    // }
    // }

    private Site proposeSite(SiteTypeEnum ste) {
        boolean validSite = false;
        Site selectedSite = null;
        int attempts = 0;
        while (true) {
            int randIndex = rand.nextInt(allSites.get(ste).size());
            selectedSite = allSites.get(ste).get(randIndex);
            if (occupiedSites.get(ste).containsKey(selectedSite)) {
                validSite = false;
            } else {
                validSite = true;
            }
            attempts++;
            if (attempts > 1000)
                throw new IllegalStateException("ERROR: Could not propose " + ste + " site after 1000 attempts!");
            if (validSite)
                break;
        }
        return selectedSite;
    }

    private Site proposeCARRYAnchorSite(SiteTypeEnum ste, int chainSize) {
        boolean validAnchor = false;
        Site selectedSite = null;
        int attempts = 0;
        while (true) {
            int randIndex = rand.nextInt(allSites.get(ste).size());
            selectedSite = allSites.get(ste).get(randIndex);
            int x = selectedSite.getInstanceX();
            int y = selectedSite.getInstanceY();
            for (int i = 0; i < chainSize; i++) {
                Site site = device.getSite("SLICE_X" + x + "Y" + (y + i));
                if (site == null) {
                    validAnchor = false;
                    break;
                }
                if (!allSites.get(ste).contains(site)) {
                    validAnchor = false;
                    break;
                }
                if (occupiedSites.get(ste).containsKey(site)) {
                    validAnchor = false;
                    break;
                }
                validAnchor = true;
            }
            attempts++;
            if (attempts > 1000)
                throw new IllegalStateException("ERROR: Could not propose CARRY4 chain anchor after 1000 attempts!");
            if (validAnchor)
                break;
        }
        return selectedSite;
    }

    private Site proposeChainAnchorSite(SiteTypeEnum siteType, int chainSize) {
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

    private Site proposeDSPAnchorSite(SiteTypeEnum ste, int cascadeSize) {
        boolean validAnchor = false;
        Site selectedSite = null;
        int attempts = 0;
        while (true) {
            int randIndex = rand.nextInt(allSites.get(ste).size());
            selectedSite = allSites.get(ste).get(randIndex);
            int x = selectedSite.getInstanceX();
            int y = selectedSite.getInstanceY();
            for (int i = 0; i < cascadeSize; i++) {
                Site site = device.getSite("DSP48_X" + x + "Y" + (y + i));
                if (site == null) {
                    validAnchor = false;
                    break;
                }
                if (!allSites.get(ste).contains(site)) {
                    validAnchor = false;
                    break;
                }
                validAnchor = true;
            }
            attempts++;
            if (attempts > 1000)
                throw new IllegalStateException("ERROR: Could not propose DSP48E1 cascade anchor after 1000 attempts!");
            if (validAnchor)
                break;
        }
        return selectedSite;
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
