
package placer;

import java.io.FileWriter;
import java.io.File;
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

    private String placerName = "PlacerAnnealRandom";

    public PlacerAnnealRandom(String rootDir, Design design, Device device, ClockRegion region) throws IOException {
        super(rootDir, design, device);
        this.regionConstraint = region;
    }

    public String getPlacerName() {
        return this.placerName;
    }

    public void initCoolingSchedule(double initialTemp, double alpha, int movesLimit) throws IOException {
        this.movesLimit = movesLimit;
        this.writer.write("\nPrinting Cooling Schedule...");
        // geometric cooling
        double currentTemp = initialTemp;
        for (int i = 0; i < movesLimit; i++) {
            this.coolingSchedule.add(currentTemp);
            this.writer.write("\n\t" + currentTemp);
            currentTemp *= alpha;
        }
    }

    public void placeDesign(PackedDesign packedDesign) throws IOException {
        initSites();
        initRpmGrid();
        addIOBuffersToNetlist();
        printNetlist();
        unplaceAllSiteInsts(packedDesign);
        randomInitialPlacement(packedDesign);
        ImageMaker imInitRandom = new ImageMaker(design);
        imInitRandom.renderAll();
        imInitRandom.exportImage(graphicsDir + "/random_placement.png");
        design.writeCheckpoint(rootDir + "/outputs/checkpoints/init_placed.dcp");

        int move = 0;
        while (true) {
            if (move >= movesLimit)
                break;

            this.currentTemp = this.coolingSchedule.get(move);

            long t0 = System.currentTimeMillis();
            double currCost = evaluateDesign();
            if (move % 20 == 0)
                System.out
                        .println("Move: " + move + ", Design Cost: " + currCost + ", Temperature: " + this.currentTemp);
            long t1 = System.currentTimeMillis();
            evalTimes.add(t1 - t0);
            this.costHistory.add(currCost);

            t0 = System.currentTimeMillis();
            move(packedDesign);
            t1 = System.currentTimeMillis();
            moveTimes.add(t1 - t0);

            t0 = System.currentTimeMillis();
            ImageMaker gifFrame = new ImageMaker(design);
            gifFrame.renderAll();
            gifFrame.exportImage(graphicsDir + "/images/" + String.format("%08d", move) + ".png");
            t1 = System.currentTimeMillis();
            renderTimes.add(t1 - t0);

            move++;
        }
        ImageMaker imPlaced = new ImageMaker(design);
        imPlaced.renderAll();
        imPlaced.exportImage(graphicsDir + "/final_placement.png");
        exportCostHistory(printoutDir + "/cost_history.csv");
        printTimingBenchmarks();
        writer.write("\n\nTotal move iterations: " + move);
    }

    protected void move(PackedDesign packedDesign) throws IOException {
        moveSiteChains(packedDesign.DSPSiteInstCascades);
        moveSiteChains(packedDesign.CARRYSiteInstChains);
        moveSingleSite(packedDesign.RAMSiteInsts);
        moveSingleSite(packedDesign.CLBSiteInsts);
    }

    protected void moveSingleSite(List<SiteInst> sites) throws IOException {
        for (SiteInst si : sites) {
            SiteTypeEnum ste = si.getSiteTypeEnum();
            List<Site> homeConns = findConnectedSites(si, null);
            Site homeSite = si.getSite();
            Site awaySite = proposeSite(si, homeConns, true);
            SiteInst awaySi = occupiedSites.get(ste).get(awaySite);
            double oldCost = 0;
            double newCost = 0;
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

    protected void moveSiteChains(List<List<SiteInst>> chains) throws IOException {
        loopThruChains: for (List<SiteInst> homeChain : chains) {
            //
            SiteTypeEnum siteType = homeChain.get(0).getSiteTypeEnum();
            Site homeChainAnchor = homeChain.get(0).getSite();
            int homeInstX = homeChainAnchor.getInstanceX();
            //
            //
            List<Site> homeChainConns = new ArrayList<>();
            for (SiteInst si : homeChain) {
                homeChainConns.addAll(findConnectedSites(si, homeChainConns));
            }
            Site awayInitAnchor = proposeAnchorSite(homeChain, homeChainConns, true);
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
    } // end moveSiteChains()

    protected List<Site> findBufferZone(SiteTypeEnum siteType, Site initAnchor, Site initTail)
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
        for (int i = finalAnchorInstY; i <= finalTailInstY; i++) {
            Site site = device.getSite(siteTypePrefix + "X" + instX + "Y" + i);
            sites.add(site);
        }
        // if (finalTailInstY - finalAnchorInstY > 16) {
        // System.out.println("buffer size: " + (finalTailInstY - finalAnchorInstY));
        // System.out.println("\tinitAnchor: " + initAnchor.getInstanceY() + ",
        // initTail: " + initTail.getInstanceY());
        // System.out.println("\tfinalAnchor: " + finalAnchorInstY + ", finalTail: " +
        // finalTailInstY);
        // for (Site site : sites) {
        // System.out.println("\t\tSiteInst: " +
        // occupiedSites.get(site.getSiteTypeEnum()).get(site));
        // }
        // }
        return sites;
    } // end findBufferZone()

    protected boolean bufferContainsOverlaps(SiteTypeEnum siteType, Site initAnchor, Site initTail)
            throws IOException {
        boolean containsOverlap = false;
        List<SiteInst> residentChainAtAnchor = occupiedSiteChains.get(siteType).get(initAnchor);
        if (residentChainAtAnchor != null) {
            Site residentAnchor = residentChainAtAnchor.get(0).getSite();
            if (residentAnchor.getInstanceY() < initAnchor.getInstanceY())
                containsOverlap = true;
        }
        List<SiteInst> residentChainAtTail = occupiedSiteChains.get(siteType).get(initTail);
        if (residentChainAtTail != null) {
            Site residentTail = residentChainAtTail.get(residentChainAtTail.size() - 1).getSite();
            if (residentTail.getInstanceY() > initTail.getInstanceY())
                containsOverlap = true;
        }
        return containsOverlap;
    } // end bufferContainsOverlaps()

    protected List<SiteInst> collectSiteInstsInBuffer(SiteTypeEnum siteType, List<Site> bufferZone)
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
    } // end collectSiteInstsInBuffer()

} // end class
