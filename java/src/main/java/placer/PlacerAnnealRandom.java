
package placer;

import java.io.IOException;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

import com.xilinx.rapidwright.design.Design;
import com.xilinx.rapidwright.design.SiteInst;
import com.xilinx.rapidwright.design.SitePinInst;

import com.xilinx.rapidwright.device.Device;
import com.xilinx.rapidwright.device.Site;
import com.xilinx.rapidwright.device.SiteTypeEnum;
import com.xilinx.rapidwright.device.ClockRegion;

public class PlacerAnnealRandom extends Placer {

    public PlacerAnnealRandom(String rootDir, Design design, Device device, ClockRegion region) throws IOException {
        super(rootDir, design, device);
        this.placerName = "PlacerAnnealRandom";
        this.graphicsDir = rootDir + "/outputs/graphics";
        this.regionConstraint = region;
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
        unplaceAllSiteInsts(packedDesign);
        randomInitialPlacement(packedDesign);
        design.writeCheckpoint(rootDir + "/outputs/checkpoints/init_placed.dcp");

        this.movesLimit = 500;
        initCoolingSchedule(20000.0f, 0.98f);
        int move = 0;
        while (true) {
            if (move >= movesLimit)
                break;
            System.out.println("move: " + move);

            this.currentTemp = this.coolingSchedule.get(move);

            long t0 = System.currentTimeMillis();
            randomMove(packedDesign);
            long t1 = System.currentTimeMillis();
            moveTimes.add(t1 - t0);

            t0 = System.currentTimeMillis();
            double currCost = evaluateDesign();
            t1 = System.currentTimeMillis();
            evalTimes.add(t1 - t0);

            this.costHistory.add(currCost);

            t0 = System.currentTimeMillis();
            ImageMaker gifFrame = new ImageMaker(design);
            gifFrame.renderAll();
            gifFrame.exportImage(graphicsDir + "/images/" + String.format("%08d", move) + ".png", "png");
            t1 = System.currentTimeMillis();
            renderTimes.add(t1 - t0);

            move++;
        }
        ImageMaker imPlaced = new ImageMaker(design);
        imPlaced.renderAll();
        imPlaced.exportImage(graphicsDir + "/final_placement.png", "png");
        exportCostHistory(rootDir + "/outputs/printout/convergence.csv");
        printTimingBenchmarks();
        writer.write("\n\nTotal move iterations: " + move);
    }

    @Override
    protected List<Site> findConnectedSites(SiteInst si, List<Site> selfConns) {
        List<Site> connectedSites = si.getSitePinInsts().stream()
                .map(spi -> spi.getNet())
                .filter(net -> net != null)
                .filter(net -> !net.isClockNet() && !net.isStaticNet())
                .map(net -> net.getPins())
                .map(spis -> spis.stream()
                        .map(spi -> spi.getSite())
                        // for chain swaps, ignore connections within the buffers.
                        // DSP cascades can have very many self connections.
                        // Allows DSP cascades to move more freely
                        .filter(site -> !((selfConns != null) && selfConns.contains(site)))
                        .collect(Collectors.toList()))
                .flatMap(List::stream) // List<List<Site>> into List<Site>
                .collect(Collectors.toList());
        return connectedSites;
    }

    protected void initCoolingSchedule(double initialTemp, double alpha) throws IOException {
        this.writer.write("\nPrinting Cooling Schedule...");
        this.coolingSchedule = new ArrayList<>();
        // geometric cooling
        double currentTemp = initialTemp;
        for (int i = 0; i < movesLimit; i++) {
            this.coolingSchedule.add(currentTemp);
            this.writer.write("\n\t" + currentTemp);
            currentTemp *= alpha;
        }
        // greedy
        // for (int i = 0; i < movesLimit; i++) {
        // this.coolingSchedule.add(0.0d);
        // }
        // logarithmic cooling
        // for (int i = 0; i < movesLimit; i++) {
        // // avoid log(0) by using (i + 1)
        // double currentTemp = initialTemp / (1 + alpha * Math.log(1 + i));
        // this.coolingSchedule.add(currentTemp);
        // this.writer.write("\n\t" + currentTemp);
        // }
    }

    protected void randomInitialPlacement(PackedDesign packedDesign) throws IOException {
        randomInitSiteChains(packedDesign.DSPSiteInstCascades);
        randomInitSiteChains(packedDesign.CARRYSiteInstChains);
        randomInitSingleSite(packedDesign.RAMSiteInsts);
        randomInitSingleSite(packedDesign.CLBSiteInsts);
    }

    protected void randomMove(PackedDesign packedDesign) throws IOException {
        randomMoveSiteChains(packedDesign.DSPSiteInstCascades);
        randomMoveSiteChains(packedDesign.CARRYSiteInstChains);
        randomMoveSingleSite(packedDesign.RAMSiteInsts);
        randomMoveSingleSite(packedDesign.CLBSiteInsts);
    }

    protected Site proposeSite(SiteTypeEnum ste, boolean swapEnable) {
        Site selectedSite = null;
        int attempts = 0;
        while (true) {
            if (attempts > 1000)
                throw new IllegalStateException("ERROR: Could not propose " + ste + " site after 1000 attempts!");
            int randIndex = rand.nextInt(allSites.get(ste).size());
            selectedSite = allSites.get(ste).get(randIndex);
            if (occupiedSiteChains.containsKey(ste)) { // never propose site swap with a chain
                if (occupiedSiteChains.get(ste).containsKey(selectedSite)) {
                    attempts++;
                    continue;
                }
            } else if (!swapEnable) { // swapping with other single sites only
                if (occupiedSites.get(ste).containsKey(selectedSite)) {
                    attempts++;
                    continue;
                }
            }
            break;
        }
        return selectedSite;
    } // end proposeSite()

    protected Site proposeAnchorSite(SiteTypeEnum ste, int chainSize, boolean swapEnable) {
        boolean validAnchor = false;
        Site selectedSite = null;
        int attempts = 0;
        while (true) {
            int randIndex = rand.nextInt(allSites.get(ste).size());
            selectedSite = allSites.get(ste).get(randIndex);
            int x = selectedSite.getInstanceX();
            int y = selectedSite.getInstanceY();
            for (int i = 0; i < chainSize; i++) {
                Site site = device.getSite(getSiteTypePrefix(ste) + "X" + x + "Y" + (y + i));
                if (site == null) {
                    validAnchor = false;
                    break;
                }
                if (!swapEnable) {
                    if (occupiedSites.get(ste).containsKey(site)) {
                        validAnchor = false;
                        break;
                    }
                }
                validAnchor = true;
            }
            attempts++;
            if (attempts > 1000)
                throw new IllegalStateException(
                        "ERROR: Could not propose " + ste + " chain anchor after 1000 attempts!");
            if (validAnchor)
                break;
        }
        return selectedSite;
    }

    protected void randomInitSingleSite(List<SiteInst> siteInsts) throws IOException {
        for (SiteInst si : siteInsts) {
            Site selectedSite = proposeSite(si.getSiteTypeEnum(), false);
            placeSiteInst(si, selectedSite);
        }
    }

    protected void randomInitSiteChains(List<List<SiteInst>> chains) throws IOException {
        for (List<SiteInst> chain : chains) {
            SiteTypeEnum siteType = chain.get(0).getSiteTypeEnum();
            Site selectedAnchor = proposeAnchorSite(siteType, chain.size(), false);
            for (int i = 0; i < chain.size(); i++) {
                Site newSite = device.getSite(getSiteTypePrefix(siteType) + "X" + selectedAnchor.getInstanceX() +
                        "Y" + (selectedAnchor.getInstanceY() + i));
                occupiedSiteChains.get(siteType).put(newSite, chain);
                placeSiteInst(chain.get(i), newSite);
            }
        }
    } // end randomInitSiteChains()

    protected void randomMoveSingleSite(List<SiteInst> sites) throws IOException {
        for (SiteInst si : sites) {
            SiteTypeEnum ste = si.getSiteTypeEnum();
            List<Site> homeConns = findConnectedSites(si, null);
            Site homeSite = si.getSite();
            Site awaySite = proposeSite(ste, true);
            double oldCost = 0;
            double newCost = 0;
            SiteInst awaySi = occupiedSites.get(ste).get(awaySite);
            if (awaySi != null) {
                List<Site> awayConns = findConnectedSites(awaySi, null);
                oldCost += evaluateSite(homeConns, homeSite);
                oldCost += evaluateSite(awayConns, awaySite);
                newCost += evaluateSite(homeConns, awaySite);
                newCost += evaluateSite(awayConns, homeSite);
            } else {
                oldCost += evaluateSite(homeConns, homeSite);
                newCost += evaluateSite(homeConns, awaySite);
            }
            if (evaluateMoveAcceptance(oldCost, newCost)) {
                if (awaySi != null) {
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
    } // end randomMoveSingleSite()

    protected void randomMoveSiteChains(List<List<SiteInst>> chains) throws IOException {
        loopThruChains: for (List<SiteInst> homeChain : chains) {
            //
            SiteTypeEnum siteType = homeChain.get(0).getSiteTypeEnum();
            Site homeChainAnchor = homeChain.get(0).getSite();
            int homeInstX = homeChainAnchor.getInstanceX();
            Site homeChainTail = homeChain.get(homeChain.size() - 1).getSite();
            //
            Site awayInitAnchor = proposeAnchorSite(siteType, homeChain.size(), true);
            int awayInstX = awayInitAnchor.getInstanceX();
            Site awayInitTail = device.getSite(getSiteTypePrefix(siteType) +
                    "X" + awayInstX +
                    "Y" + (awayInitAnchor.getInstanceY() + homeChain.size() - 1));
            //
            List<Site> awayBuffer = findBufferZone(siteType, awayInitAnchor, awayInitTail);
            List<SiteInst> siteInstsInAwayBuffer = collectSiteInstsInBuffer(siteType, awayBuffer);
            List<Site> homeBuffer = null;
            List<SiteInst> siteInstsInHomeBuffer = null;
            //
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
            //
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
            double oldCost = 0;
            double newCost = 0;
            for (int i = 0; i < homeBuffer.size(); i++) {
                if (siteInstsInHomeBuffer.get(i) != null) {
                    List<Site> homeConns = findConnectedSites(siteInstsInHomeBuffer.get(i), homeBuffer);
                    for (Site conn : homeConns) {
                        if (awayBuffer.contains(conn)) {
                            continue loopThruChains; // skip this chain swap proposal
                            // CONTINUES THE OUTER-MOST LOOP!
                        }
                    }
                    oldCost += evaluateSite(homeConns, homeBuffer.get(i));
                    newCost += evaluateSite(homeConns, awayBuffer.get(i));
                }
                if (siteInstsInAwayBuffer.get(i) != null) {
                    List<Site> awayConns = findConnectedSites(siteInstsInAwayBuffer.get(i), awayBuffer);
                    for (Site conn : awayConns) {
                        if (homeBuffer.contains(conn)) {
                            continue loopThruChains; // skip this chain swap proposal
                            // CONTINUES THE OUTER-MOST LOOP!
                        }
                    }
                    oldCost += evaluateSite(awayConns, awayBuffer.get(i));
                    newCost += evaluateSite(awayConns, homeBuffer.get(i));
                }
            }

            if (evaluateMoveAcceptance(oldCost, newCost)) {
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

        } // end for loopThruChains
    } // end randomMoveSiteChains()

} // end class
