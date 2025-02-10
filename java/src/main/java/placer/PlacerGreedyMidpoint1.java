
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;

import com.xilinx.rapidwright.design.ModuleInst;
import com.xilinx.rapidwright.design.Design;
import com.xilinx.rapidwright.design.SiteInst;
import com.xilinx.rapidwright.design.SitePinInst;
import com.xilinx.rapidwright.design.Net;

import com.xilinx.rapidwright.device.Device;
import com.xilinx.rapidwright.device.Tile;

import com.xilinx.rapidwright.device.Site;
import com.xilinx.rapidwright.device.SiteTypeEnum;

public class PlacerGreedyMidpoint1 extends Placer {

    private Set<SiteTypeEnum> uniqueSiteTypes;
    private Map<SiteTypeEnum, List<Site>> allSites;
    private Map<SiteTypeEnum, Set<Site>> occupiedSites;
    private Random rand;

    private List<Double> costHistory;
    private List<Long> moveTimes;
    private List<Long> evalTimes;
    private List<Long> writeTimes;

    public PlacerGreedyMidpoint1(String rootDir, Design design, Device device) throws IOException {
        super(rootDir, design, device);
        this.placerName = "PlacerGreedyMidpoint1";
        this.uniqueSiteTypes = new HashSet<>();
        this.occupiedSites = new HashMap<>();
        this.allSites = new HashMap<>();
        this.costHistory = new ArrayList<>();
        this.moveTimes = new ArrayList<>();
        this.evalTimes = new ArrayList<>();
        this.writeTimes = new ArrayList<>();
        this.rand = new Random();
    }

    public void placeDesign(PackedDesign packedDesign) throws IOException {
        initSites();
        Double lowestCost = evaluateDesign(); // initial cost
        int staleMoves = 0;
        int totalMoves = 0;
        unplaceAllSiteInsts(packedDesign);
        randomInitialPlacement(packedDesign);
        design.writeCheckpoint(rootDir + "/outputs/graphics/" + placerName + "_placed_inital.dcp");

        ImageMaker imInitial = new ImageMaker(design);
        imInitial.construct2DSiteArray();
        imInitial.construct2DSiteArrayImage();
        imInitial.exportImage(rootDir + "/outputs/graphics/" + placerName + "_blank_site_array.png");
        imInitial.construct2DPlacementArray();
        imInitial.overlayPlacementOnSiteArrayImage();
        imInitial.exportImage(rootDir + "/outputs/graphics/" + placerName + "_initial_placement_array.png");

        while (true) {
            long t0 = System.currentTimeMillis();
            randomMove(packedDesign);
            long t1 = System.currentTimeMillis();
            moveTimes.add(t1 - t0);

            t0 = System.currentTimeMillis();
            double currCost = evaluateDesign();
            t1 = System.currentTimeMillis();
            evalTimes.add(t1 - t0);

            this.costHistory.add(currCost);
            if (currCost < lowestCost) {
                t0 = System.currentTimeMillis();
                design.writeCheckpoint(placedDcp);
                t1 = System.currentTimeMillis();
                writeTimes.add(t1 - t0);
                staleMoves = 0;
                lowestCost = currCost;
            }
            staleMoves++;
            totalMoves++;
            if (staleMoves > 25 || totalMoves > 250)
                break;
        }

        ImageMaker imPlaced = new ImageMaker(design);
        imPlaced.construct2DSiteArray();
        imPlaced.construct2DSiteArrayImage();
        imPlaced.construct2DPlacementArray();
        imPlaced.overlayPlacementOnSiteArrayImage();
        imPlaced.exportImage(rootDir + "/outputs/graphics/" + placerName + "_placement_array.png");

        imPlaced.overlayNetsOnPlacementImage();
        imPlaced.exportImage(rootDir + "/outputs/graphics/" + placerName + "_netlist_array.png");

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
                occupiedSites.put(siteType, new HashSet<>());
            }
            allSites.get(siteType).add(site);
        }
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

    private double evaluateSite(List<Site> sinkSites, Site srcSite) throws IOException {
        double cost = 0;
        for (Site sinkSite : sinkSites) {
            cost = cost + srcSite.getTile().getTileManhattanDistance(sinkSite.getTile());
        }
        return cost;
    }

    public double evaluateDesign() throws IOException {
        double cost = 0;
        Collection<Net> nets = design.getNets();
        for (Net net : nets) {
            Tile srcTile = net.getSourceTile();
            if (srcTile == null) // net is null if its' purely intrasite!
                continue;
            List<Tile> sinkTiles = net.getSinkPins().stream()
                    .map(spi -> spi.getTile())
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
        randomInitCLBSites(packedDesign);
    }

    private void randomMove(PackedDesign packedDesign) throws IOException {
        randomMoveDSPSiteCascades(packedDesign);
        randomMoveCARRYSiteChains(packedDesign);
        randomMoveRAMSites(packedDesign);
        randomMoveCLBSites(packedDesign);
    }

    private void placeSiteInst(SiteInst si, Site site) {
        occupiedSites.get(si.getSiteTypeEnum()).add(site);
        si.place(site);
    }

    private void unplaceSiteInst(SiteInst si) {
        occupiedSites.get(si.getSiteTypeEnum()).remove(si.getSite());
        si.unPlace();
    }

    private void randomInitCLBSites(PackedDesign packedDesign) throws IOException {
        for (SiteInst si : packedDesign.CLBSiteInsts) {
            Site selectedSite = proposeSite(si.getSiteTypeEnum(), null);
            placeSiteInst(si, selectedSite);
        }
    }

    private void randomInitRAMSites(PackedDesign packedDesign) throws IOException {
        for (SiteInst si : packedDesign.RAMSiteInsts) {
            Site selectedSite = proposeSite(si.getSiteTypeEnum(), null);
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
                placeSiteInst(chain.get(i), newSite);
            }
        }
    }

    private void randomMoveCLBSites(PackedDesign packedDesign) throws IOException {
        // SiteTypeEnum ste = SiteTypeEnum.SLICEL;
        for (SiteInst si : packedDesign.CLBSiteInsts) {
            SiteTypeEnum ste = si.getSiteTypeEnum();
            List<Site> sinkSites = findSinkSites(si);
            double oldCost = evaluateSite(sinkSites, si.getSite());
            Site newSite = proposeSite(ste, sinkSites);
            double newCost = evaluateSite(sinkSites, newSite);
            if (newCost < oldCost) {
                unplaceSiteInst(si);
                placeSiteInst(si, newSite);
            }
        }
    }

    private void randomMoveRAMSites(PackedDesign packedDesign) throws IOException {
        for (SiteInst si : packedDesign.RAMSiteInsts) {
            List<Site> sinkSites = findSinkSites(si);
            double oldCost = evaluateSite(sinkSites, si.getSite());
            // Site newSite = proposeRAMSite();
            SiteTypeEnum ste = si.getSiteTypeEnum();
            Site newSite = proposeSite(ste, sinkSites);
            double newCost = evaluateSite(sinkSites, newSite);
            if (newCost < oldCost) {
                unplaceSiteInst(si);
                placeSiteInst(si, newSite);
            }
        }
    }

    private void randomMoveDSPSiteCascades(PackedDesign packedDesign) throws IOException {
        SiteTypeEnum ste = SiteTypeEnum.DSP48E1;
        for (List<SiteInst> cascade : packedDesign.DSPSiteInstCascades) {
            Site selectedAnchor = proposeDSPAnchorSite(ste, cascade.size());
            double oldCost = 0;
            double newCost = 0;
            List<Site> newSiteCascade = new ArrayList<>();
            for (int i = 0; i < cascade.size(); i++) {
                List<Site> sinkSites = findSinkSites(cascade.get(i));
                oldCost = oldCost + evaluateSite(sinkSites, cascade.get(i).getSite());
                Site newSite = device
                        .getSite("DSP48_X" + selectedAnchor.getInstanceX() + "Y" + (selectedAnchor.getInstanceY() + i));
                newSiteCascade.add(newSite);
                newCost = newCost + evaluateSite(sinkSites, newSite);
            }
            if (newCost < oldCost) {
                for (int i = 0; i < cascade.size(); i++) {
                    unplaceSiteInst(cascade.get(i));
                    placeSiteInst(cascade.get(i), newSiteCascade.get(i));
                }
            }
        }
    }

    private void randomMoveCARRYSiteChains(PackedDesign packedDesign) throws IOException {
        SiteTypeEnum selectedSiteType = SiteTypeEnum.SLICEL;
        for (List<SiteInst> chain : packedDesign.CARRYSiteInstChains) {
            Site selectedAnchor = proposeCARRYAnchorSite(selectedSiteType, chain.size());
            double oldCost = 0;
            double newCost = 0;
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

    private Site proposeSite(SiteTypeEnum ste, List<Site> sinkSites) {
        boolean validSite = false;
        Site selectedSite = null;
        int attempts = 0;
        while (true) {
            int randIndex = rand.nextInt(allSites.get(ste).size());
            selectedSite = allSites.get(ste).get(randIndex);
            if (occupiedSites.get(ste).contains(selectedSite)) {

                //
                // TODO: evaluate potential swap
                //
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
                if (occupiedSites.get(ste).contains(site)) {
                    //
                    // TODO: evaluate potential swap
                    //
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
                if (occupiedSites.get(ste).contains(site)) {
                    //
                    // TODO: evaluate potential swap
                    //
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
