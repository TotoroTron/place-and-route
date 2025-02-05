package placer;

import java.util.stream.Collectors;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Random;

import java.io.IOException;

import com.xilinx.rapidwright.design.Design;
import com.xilinx.rapidwright.design.SiteInst;
import com.xilinx.rapidwright.design.SitePinInst;
import com.xilinx.rapidwright.design.Net;

import com.xilinx.rapidwright.device.Device;
import com.xilinx.rapidwright.device.Tile;
import com.xilinx.rapidwright.device.Site;
import com.xilinx.rapidwright.device.SiteTypeEnum;

public class PlacerGreedyRandom1 extends Placer {

    private Map<SiteTypeEnum, List<Site>> occupiedSites;
    private Map<SiteTypeEnum, List<Site>> availableSites;
    private Random rand;

    private List<Double> costHistory;

    public PlacerGreedyRandom1(String rootDir, Design design, Device device) throws IOException {
        super(rootDir, design, device);
        placerName = "PlacerGreedyRandom1";
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
            }
            staleMoves++;
            totalMoves++;
            if (staleMoves > 10 || totalMoves > 100)
                break;
        }
        writer.write("\n\n" + placerName + " cost history... ");
        for (int i = 0; i < costHistory.size(); i++) {
            writer.write("\n\tIter " + i + ": " + costHistory.get(i));
        }
        writer.write("\n\nTotal move iterations: " + totalMoves);
        writer.write("\n\nStale move iterations: " + staleMoves);
    }

    private List<Site> findSinkSites(SiteInst si) throws IOException {
        List<Site> sinkSites = si.getSitePinInsts().stream()
                .filter(spi -> spi.isOutPin())
                .map(spi -> spi.getNet())
                .map(net -> net.getSinkPins())
                .map(spis -> spis.stream()
                        .map(spi -> spi.getSite())
                        .collect(Collectors.toList()))
                .flatMap(List::stream) // List<List<Tile>> into List<Tile>
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
    }

    private void randomMove(PackedDesign packedDesign) throws IOException {
        SiteTypeEnum selectedSiteType = SiteTypeEnum.SLICEL;
        for (SiteInst si : packedDesign.CLBSiteInsts) {
            // System.out.println("Random Move SiteInst: " + si.getName());
            // writer.write("\nRandom Move SiteInst: " + si.getName());

            List<Site> sinkSites = findSinkSites(si);
            double oldCost = evaluateSite(sinkSites, si.getSite());
            // writer.write("\n\tCurrent Site: " + si.getSiteName() + ", Cost: " + oldCost);

            Site newSite = proposeCLBSite(selectedSiteType);
            double newCost = evaluateSite(sinkSites, newSite);
            // writer.write("\n\tCandidate Site: " + newSite.getName() + ", Cost: " +
            // newCost);

            if (newCost < oldCost) {
                unplaceSiteInst(si);
                placeSiteInst(si, newSite);
                // writer.write("\n\tCandidate site ACCEPTED!");
            } else {
                // writer.write("\n\tCandidate site REJECTED!");
            }
        }
    }

    private Site proposeCLBSite(SiteTypeEnum ste) {
        int randIndex = rand.nextInt(availableSites.get(ste).size());
        return availableSites.get(ste).get(randIndex);
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

    private Site selectCarryAnchorSite(int chainSize) {
        Random rand = new Random();
        SiteTypeEnum selectedSiteType = SiteTypeEnum.SLICEL;
        boolean validAnchor = false;
        Site selectedSite = null;
        int attempts = 0;
        while (true) {
            int randRange = availableSites.get(selectedSiteType).size();
            selectedSite = availableSites.get(selectedSiteType).get(rand.nextInt(randRange));
            int x = selectedSite.getInstanceX();
            int y = selectedSite.getInstanceY();
            for (int i = 0; i < chainSize; i++) {
                String name = "SLICE_X" + x + "Y" + (y + i);
                if (occupiedSites.get(selectedSiteType).contains(device.getSite(name))) {
                    validAnchor = false;
                    break;
                }
                validAnchor = true;
            }
            attempts++;
            if (attempts > 1000)
                throw new IllegalStateException("ERROR: Could not find CARRY4 chain anchor after 1000 attempts!");
            if (validAnchor)
                break;
        }
        return selectedSite;
    }

}
