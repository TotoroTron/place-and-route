
package placer;

import java.util.stream.Collectors;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Random;

import java.io.FileWriter;
import java.io.IOException;

import com.xilinx.rapidwright.design.Design;
import com.xilinx.rapidwright.design.SiteInst;
import com.xilinx.rapidwright.design.SitePinInst;
import com.xilinx.rapidwright.design.Net;

import com.xilinx.rapidwright.device.Device;
import com.xilinx.rapidwright.device.Tile;

import com.xilinx.rapidwright.device.Site;
import com.xilinx.rapidwright.device.SiteTypeEnum;

public class PlacerGreedyRandom2 extends Placer {

    private Map<SiteTypeEnum, List<Site>> occupiedSites;
    private Map<SiteTypeEnum, List<Site>> availableSites;
    private Map<SiteTypeEnum, Map<Site, Boolean>> utilization; // ?
    private Random rand;

    private List<Double> costHistory;

    public PlacerGreedyRandom2(String rootDir, Design design, Device device) throws IOException {
        super(rootDir, design, device);
        placerName = "PlacerGreedyRandom2";
        costHistory = new ArrayList<>();
        rand = new Random();
    }

    public void placeDesign(PackedDesign packedDesign) throws IOException {
        initAvailableSites(packedDesign);
        Double lowestCost = evaluateDesign(); // initial cost
        int staleMoves = 0;
        int totalMoves = 0;
        while (true) {
            randomMove(packedDesign);
            Double currCost = evaluateDesign();
            costHistory.add(currCost);
            if (currCost < lowestCost) {
                design.writeCheckpoint(placedDcp);
                staleMoves = 0;
                lowestCost = currCost;
            }
            staleMoves++;
            totalMoves++;
            if (staleMoves > 25 || totalMoves > 250)
                break;
        }
        exportCostHistory();
        writer.write("\n\nTotal move iterations: " + totalMoves);
        writer.write("\n\nStale move iterations: " + staleMoves);
    }

    public void exportCostHistory() throws IOException {
        FileWriter csv = new FileWriter(rootDir + "/outputs/printout/" + placerName + "_Convergence.csv");
        csv.write("Iter, Cost");
        for (int i = 0; i < costHistory.size(); i++) {
            csv.write("\n" + i + ", " + costHistory.get(i));
        }
        if (csv != null)
            csv.close();
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

    private void initAvailableSites(PackedDesign packedDesign) throws IOException {
        this.occupiedSites = packedDesign.occupiedSites;
        this.availableSites = packedDesign.availableSites;
        this.utilization = new HashMap<>();
    }

    private void randomMove(PackedDesign packedDesign) throws IOException {
        // Chunkiest movements first
        randomMoveDSPSiteCascades(packedDesign);
        randomMoveCARRYSiteChains(packedDesign);
        randomMoveRAMSites(packedDesign);
        randomMoveCLBSites(packedDesign);
    }

    private Site proposeSite(SiteTypeEnum ste) {
        int randIndex = rand.nextInt(availableSites.get(ste).size());
        return availableSites.get(ste).get(randIndex);
    }

    private Site proposeRAMSite() {
        // For some reason, a RAM SiteInst placed on a RAMB18E1 Site
        // cannot be moved to a FIFO18E1 Site or vice versa.
        // The placement will simply be ignored.
        // RAMs are locked by their SiteTypeEnum assigned during the packing
        // stage's initial random placement.
        List<Site> compatibleSites = new ArrayList<>();
        compatibleSites.addAll(availableSites.get(SiteTypeEnum.RAMB18E1));
        compatibleSites.addAll(availableSites.get(SiteTypeEnum.FIFO18E1));
        int randIndex = rand.nextInt(compatibleSites.size());
        Site selectedSite = compatibleSites.get(randIndex);
        return selectedSite;
    }

    private void placeSiteInst(SiteInst si, Site site) {
        availableSites.get(si.getSiteTypeEnum()).remove(site);
        occupiedSites.get(si.getSiteTypeEnum()).add(site);
        si.place(site);

    }

    private void unplaceSiteInst(SiteInst si) {
        occupiedSites.get(si.getSiteTypeEnum()).remove(si.getSite());
        availableSites.get(si.getSiteTypeEnum()).add(si.getSite());
        si.unPlace();
    }

    private void randomMoveCLBSites(PackedDesign packedDesign) throws IOException {
        SiteTypeEnum selectedSiteType = SiteTypeEnum.SLICEL;
        for (SiteInst si : packedDesign.CLBSiteInsts) {
            List<Site> sinkSites = findSinkSites(si);
            double oldCost = evaluateSite(sinkSites, si.getSite());
            Site newSite = proposeSite(selectedSiteType);
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
            Site newSite = proposeSite(ste);
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

    private Site proposeCARRYAnchorSite(SiteTypeEnum ste, int chainSize) {
        boolean validAnchor = false;
        Site selectedSite = null;
        int attempts = 0;
        while (true) {
            int randIndex = rand.nextInt(availableSites.get(ste).size());
            selectedSite = availableSites.get(ste).get(randIndex);
            int x = selectedSite.getInstanceX();
            int y = selectedSite.getInstanceY();
            for (int i = 0; i < chainSize; i++) {
                String name = "SLICE_X" + x + "Y" + (y + i);
                if (occupiedSites.get(ste).contains(device.getSite(name))) {
                    validAnchor = false;
                    break;
                }
                if (!availableSites.get(ste).contains(device.getSite(name))) {
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
            int randIndex = rand.nextInt(availableSites.get(ste).size());
            selectedSite = availableSites.get(ste).get(randIndex);
            int x = selectedSite.getInstanceX();
            int y = selectedSite.getInstanceY();
            for (int i = 0; i < cascadeSize; i++) {
                String name = "DSP48_X" + x + "Y" + (y + i);
                if (occupiedSites.get(ste).contains(device.getSite(name))) {
                    validAnchor = false;
                    break;
                }
                if (!availableSites.get(ste).contains(device.getSite(name))) {
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

}
