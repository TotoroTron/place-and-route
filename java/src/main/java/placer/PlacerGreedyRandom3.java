
package placer;

import java.awt.Color;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.FileWriter;
import java.io.File;
import java.io.IOException;
import java.nio.Buffer;
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

    // allows fast access to EDIF Cell Pack from device Site
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
        // randomInitialPlacement(packedDesign);

        int frameCounter = 1;
        while (true) {
            if (totalMoves > 600)
                break;
            System.out.println("totalMoves = " + totalMoves);
            long t0 = System.currentTimeMillis();
            if (totalMoves == 0) {
                randomInitDSPSiteCascades(packedDesign);
                randomInitRAMSites(packedDesign);
            } else if (totalMoves > 0 && totalMoves < 200) {
                randomMoveDSPSiteCascades(packedDesign);
                randomMoveRAMSites(packedDesign);
            } else if (totalMoves == 200) {
                randomInitCARRYSiteChains(packedDesign);
            } else if (totalMoves > 200 && totalMoves < 400) {
                randomMoveCARRYSiteChains(packedDesign);
            } else if (totalMoves == 400) {
                randomInitCLBSites(packedDesign);
            } else if (totalMoves > 400) {
                randomMove(packedDesign);
            }
            long t1 = System.currentTimeMillis();
            moveTimes.add(t1 - t0);

            t0 = System.currentTimeMillis();
            float currCost = evaluateDesign();
            t1 = System.currentTimeMillis();
            evalTimes.add(t1 - t0);

            this.costHistory.add(currCost);

            // if (currCost < lowestCost) {
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
        randomInitRAMSites(packedDesign);
        for (int i = 0; i < 200; i++) {
            randomMoveDSPSiteCascades(packedDesign);
            randomMoveRAMSites(packedDesign);
        }
        randomInitCARRYSiteChains(packedDesign);
        for (int i = 0; i < 200; i++) {
            randomMoveCARRYSiteChains(packedDesign);
        }
        randomInitCLBSites(packedDesign);

        // randomInitDSPSiteCascades(packedDesign);
        // randomInitCARRYSiteChains(packedDesign);
        // randomInitRAMSites(packedDesign);
        // randomInitCLBSites(packedDesign);
    }

    private void randomMove(PackedDesign packedDesign) throws IOException {
        // Chunkiest movements first
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

    private void randomInitDSPSiteCascades(PackedDesign packedDesign) throws IOException {
        SiteTypeEnum ste = SiteTypeEnum.DSP48E1;
        for (List<SiteInst> cascade : packedDesign.DSPSiteInstCascades) {
            Site selectedAnchor = proposeDSPAnchorSite(ste, cascade.size());
            for (int i = 0; i < cascade.size(); i++) {
                Site newSite = device
                        .getSite("DSP48_X" + selectedAnchor.getInstanceX() + "Y" + (selectedAnchor.getInstanceY() + i));
                occupiedSiteChains.get(ste).put(newSite, cascade);
                placeSiteInst(cascade.get(i), newSite);
            }
        }
    }

    private void randomInitCARRYSiteChains(PackedDesign packedDesign) throws IOException {
        for (List<SiteInst> chain : packedDesign.CARRYSiteInstChains) {
            SiteTypeEnum ste = chain.get(0).getSiteTypeEnum();
            Site selectedAnchor = proposeCARRYAnchorSite(ste, chain.size());
            for (int i = 0; i < chain.size(); i++) {
                Site newSite = device
                        .getSite("SLICE_X" + selectedAnchor.getInstanceX() + "Y" + (selectedAnchor.getInstanceY() + i));
                occupiedSiteChains.get(ste).put(newSite, chain);
                placeSiteInst(chain.get(i), newSite);
            }
        }
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

    private void randomMoveDSPSiteCascades(PackedDesign packedDesign) throws IOException {
        SiteTypeEnum siteType = SiteTypeEnum.DSP48E1;
        for (List<SiteInst> homeCascade : packedDesign.DSPSiteInstCascades) {

            /*
             * WORK IN PROGRESS
             * this control flow is ass!!
             *
             */

            Site candidateAnchor = proposeDSPAnchorSite(siteType, homeCascade.size());

            List<SiteInst> homeBuffer = new ArrayList<>();
            List<SiteInst> awayBuffer = new ArrayList<>();
            int awayBufferLow = candidateAnchor.getRpmY();
            int awayBufferHigh = awayBufferLow;
            int awayBufferSize = awayBufferHigh - awayBufferLow;

            int k = 0;
            // Find the size of the away buffer zone.
            while (true) {
                if (awayBufferHigh >= candidateAnchor.getRpmY() + homeCascade.size())
                    break;

                Site awaySite = device.getSite(
                        "DSP48_X" + candidateAnchor.getInstanceX() + "Y" + (candidateAnchor.getInstanceY() + k));

                // If this Site belongs to a Chain...
                List<SiteInst> existingChain = occupiedSiteChains.get(siteType).get(awaySite);
                if (existingChain != null) {
                    int low = existingChain.get(0).getSite().getRpmY();
                    int high = low + existingChain.size();
                    if (low < awayBufferLow) {
                        awayBufferLow = low;
                    }
                    if (high > awayBufferHigh) {
                        awayBufferHigh = high;
                    }
                } else {
                    awayBufferHigh++;
                }
            }

            // Collect all SiteInsts in the away buffer zone.
            for (int i = 0; i < awayBufferSize; i++) {
                Site awaySite = device.getSite(
                        "DSP48_X" + candidateAnchor.getInstanceX() + "Y" + (awayBufferLow + i));
                // If this Site belongs to either a Chain or loose CLB...
                SiteInst awaySiteInst = occupiedSites.get(siteType).get(awaySite);
                if (awaySiteInst != null)
                    awayBuffer.add(awaySiteInst);
                else
                    awayBuffer.add(null);
            }

            // Find the home buffer zone.
            Site homeAnchor = homeCascade.get(0).getSite();
            Site homeTail = homeCascade.get(homeCascade.size() - 1).getSite();
            int sweepSize = awayBufferSize - homeCascade.size() + 1;

            int homeBufferLow = homeAnchor.getRpmY() - sweepSize;
            int homeBufferHigh = homeTail.getRpmY() + sweepSize;

            // Sweep the home buffer to find a legal chain swap.
            for (int i = 0; i < sweepSize; i++) {
                int collisions = 0;
                for (int j = 0; j < awayBufferSize; j++) {
                    Site homeSite = device.getSite(
                            "DSP_48X" + homeBufferLow + "Y" + j);
                    if (j >= homeAnchor.getRpmY() && j <= homeTail.getRpmY()) {
                        // these sites are gettings swapped anyway
                        continue;
                    } else if (occupiedSites.get(siteType).containsKey(homeSite) && awayBuffer.get(j) != null) {
                        collisions++;
                    }
                }
                if (collisions == 0)
                    break; // swap is legal
            }

            /*
             * WORK IN PROGRESS
             * this control flow is ass!!
             *
             */

            // float oldCost = 0;
            // float newCost = 0;
            // List<Site> newSiteCascade = new ArrayList<>();
            // for (int i = 0; i < cascade.size(); i++) {
            // List<Site> sinkSites = findSinkSites(cascade.get(i));
            // oldCost = oldCost + evaluateSite(sinkSites, cascade.get(i).getSite());
            // Site newSite = device
            // .getSite("DSP48_X" + candidateAnchor.getInstanceX() + "Y"
            // + (candidateAnchor.getInstanceY() + i));
            // newSiteCascade.add(newSite);
            // newCost = newCost + evaluateSite(sinkSites, newSite);
            // }
            // if (newCost < oldCost) {
            // for (int i = 0; i < cascade.size(); i++) {
            // unplaceSiteInst(cascade.get(i));
            // placeSiteInst(cascade.get(i), newSiteCascade.get(i));
            // }
            // }
        }
    }

    private void randomMoveCARRYSiteChains(PackedDesign packedDesign) throws IOException {
        SiteTypeEnum selectedSiteType = SiteTypeEnum.SLICEL;
        for (List<SiteInst> chain : packedDesign.CARRYSiteInstChains) {
            Site selectedAnchor = proposeCARRYAnchorSite(selectedSiteType, chain.size());
            float oldCost = 0;
            float newCost = 0;
            List<Site> newSiteChain = new ArrayList<>();
            for (int i = 0; i < chain.size(); i++) {
                List<Site> sinkSites = findSinkSites(chain.get(i));
                oldCost = oldCost + evaluateSite(sinkSites, chain.get(i).getSite());
                Site newSite = device
                        .getSite("SLICE_X" + selectedAnchor.getInstanceX() + "Y" + (selectedAnchor.getInstanceY() + i));
                newSiteChain.add(newSite);
                newCost = newCost + evaluateSite(sinkSites, newSite);
            }
            if (newCost < oldCost) {
                for (int i = 0; i < chain.size(); i++) {
                    unplaceSiteInst(chain.get(i));
                    placeSiteInst(chain.get(i), newSiteChain.get(i));
                }
            }
        }
    }

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
