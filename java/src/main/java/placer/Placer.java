package placer;

import java.io.FileWriter;
import java.io.IOException;

import java.util.stream.Collectors;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.Map;
import java.util.Random;

import com.xilinx.rapidwright.design.Design;
import com.xilinx.rapidwright.design.SiteInst;
import com.xilinx.rapidwright.design.Net;
import com.xilinx.rapidwright.design.SitePinInst;

import com.xilinx.rapidwright.device.Device;
import com.xilinx.rapidwright.device.Tile;
import com.xilinx.rapidwright.device.Site;
import com.xilinx.rapidwright.device.SiteTypeEnum;
import com.xilinx.rapidwright.device.ClockRegion;

public abstract class Placer {
    protected String placerName;
    FileWriter writer;
    protected String rootDir;
    protected String graphicsDir;
    protected String placedDcp;

    protected final Device device;
    protected final Design design;

    protected List<Double> costHistory;
    protected List<Long> moveTimes;
    protected List<Long> evalTimes;
    protected List<Long> renderTimes;
    protected List<Long> writeTimes;
    protected int movesLimit;
    protected List<Double> coolingSchedule;
    protected double currentTemp;

    protected ClockRegion regionConstraint;
    protected Set<SiteTypeEnum> uniqueSiteTypes;
    protected Map<SiteTypeEnum, List<Site>> allSites;
    protected Map<SiteTypeEnum, Map<Site, SiteInst>> occupiedSites;

    // store chain occupation info for fast site-to-chain access
    protected Map<SiteTypeEnum, Map<Site, List<SiteInst>>> occupiedSiteChains;
    protected Random rand;

    protected String[] FF5_BELS = new String[] { "A5FF", "B5FF", "C5FF", "D5FF" };
    protected String[] FF_BELS = new String[] { "AFF", "BFF", "CFF", "DFF" };
    protected String[] LUT5_BELS = new String[] { "A5LUT", "B5LUT", "C5LUT", "D5LUT" };
    protected String[] LUT6_BELS = new String[] { "A6LUT", "B6LUT", "C6LUT", "D6LUT" };

    public Placer(String rootDir, Design design, Device device) throws IOException {
        this.rootDir = rootDir;
        this.placedDcp = rootDir + "/outputs/checkpoints/placed.dcp";
        this.design = design;
        this.device = device;
    }

    public void run(PackedDesign packedDesign) throws IOException {
        writer = new FileWriter(rootDir + "/outputs/printout/" + placerName + ".txt");
        writer.write(placerName + ".txt");
        placeDesign(packedDesign);
        writer.close();
        design.writeCheckpoint(placedDcp);
    }

    public void run(PackedDesign packedDesign, PrepackedDesign prepackedDesign) throws IOException {
        writer = new FileWriter(rootDir + "/outputs/printout/" + placerName + ".txt");
        writer.write(placerName + ".txt");
        placeDesign(packedDesign);
        writer.close();
        design.writeCheckpoint(placedDcp);
    }

    protected abstract void placeDesign(PackedDesign packedDesign) throws IOException;

    // protected abstract Site proposeSite(SiteTypeEnum ste, boolean swapEnable);

    // protected abstract Site proposeAnchorSite(SiteTypeEnum ste, int chainSize,
    // boolean swapEnable);

    protected abstract void randomInitSingleSite(List<SiteInst> siteInsts) throws IOException;

    protected abstract void randomInitSiteChains(List<List<SiteInst>> chains) throws IOException;

    protected abstract void randomMoveSingleSite(List<SiteInst> sites) throws IOException;

    protected abstract void randomMoveSiteChains(List<List<SiteInst>> chains) throws IOException;

    protected void initSites() throws IOException {
        for (Site site : device.getAllSites()) {
            SiteTypeEnum siteType = site.getSiteTypeEnum();
            if (uniqueSiteTypes.add(siteType)) {
                List<Site> compatibleSites = new ArrayList<>(Arrays.asList(device.getAllSitesOfType(siteType)));
                if (regionConstraint != null) {
                    compatibleSites = compatibleSites.stream()
                            .filter(s -> s.getClockRegion() == null
                                    ? s.isGlobalClkBuffer()
                                    : s.getClockRegion().equals(regionConstraint))
                            .collect(Collectors.toList());
                }
                allSites.put(siteType, compatibleSites);
                occupiedSites.put(siteType, new HashMap<>());
            }
        }
        occupiedSiteChains.put(SiteTypeEnum.DSP48E1, new HashMap<>());
        occupiedSiteChains.put(SiteTypeEnum.SLICEL, new HashMap<>());
        occupiedSiteChains.put(SiteTypeEnum.SLICEM, new HashMap<>());
    }

    protected void placeSiteInst(SiteInst si, Site site) {
        occupiedSites.get(si.getSiteTypeEnum()).put(site, si);
        si.place(site);
    }

    protected void unplaceSiteInst(SiteInst si) {
        occupiedSites.get(si.getSiteTypeEnum()).remove(si.getSite());
        si.unPlace();
    }

    protected List<Site> findConnectedSites(SiteInst si, List<Site> selfConns) {
        Collection<SitePinInst> pins = si.getSitePinInsts();
        // Handle SPI output pins. Get all sinks.
        List<Site> outputSinks = pins.stream()
                .filter(spi -> spi.isOutPin())
                .map(spi -> spi.getNet())
                .map(net -> net.getSinkPins())
                .map(spis -> spis.stream()
                        .map(spi -> spi.getSite())
                        .collect(Collectors.toList()))
                .flatMap(List::stream) // List<List<Site>> into List<Site>
                .collect(Collectors.toList());
        // Handle SPI input pins. Only get the source.
        List<Site> inputSources = pins.stream()
                .filter(spi -> !spi.isOutPin())
                .map(spi -> spi.getNet())
                .filter(net -> net != null)
                .map(net -> net.getSource())
                .filter(spi -> spi != null)
                .map(spi -> spi.getSite())
                .collect(Collectors.toList());
        List<Site> allSites = new ArrayList<>();
        allSites.addAll(inputSources);
        allSites.addAll(outputSinks);
        return allSites;
    }

    protected boolean evaluateMoveAcceptance(double oldCost, double newCost) {
        // if the new cost is lower, accept it outright
        if (newCost < oldCost) {
            return true;
        }
        // otherwise, evaluate probability to accept higher cost
        double delta = newCost - oldCost;
        double acceptanceProbability = Math.exp(-delta / this.currentTemp);
        return Math.random() < acceptanceProbability;
    }

    protected double evaluateSite(List<Site> sinkSites, Site srcSite) throws IOException {
        double cost = 0;
        for (Site sinkSite : sinkSites) {
            // if (sinkSite.isGlobalClkBuffer()) {
            // // System.out.println("Skipped global clock buffer evaluation.");
            // continue;
            // }
            // if (sinkSite.isGlobalClkPad()) {
            // // System.out.println("Skipped global clock pad evaluation.");
            // continue;
            // }
            cost = cost + srcSite.getTile().getTileManhattanDistance(sinkSite.getTile());
        }
        return cost;
    }

    public double evaluateDesign() throws IOException {
        double cost = 0;
        Collection<Net> nets = design.getNets();
        for (Net net : nets) {
            // if (net.isClockNet() || net.isStaticNet()) {
            // // System.out.println("Skipped static net evaluation.");
            // continue;
            // }
            // if (net.isStaticNet()) {
            // // System.out.println("Skipped clock net evaluation.");
            // continue;
            // }
            Tile srcTile = net.getSourceTile();
            if (srcTile == null) // tile is null if its' purely intrasite!
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

    protected void unplaceAllSiteInsts(PackedDesign packedDesign) throws IOException {
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

    protected String getSiteTypePrefix(SiteTypeEnum siteType) {
        String siteTypePrefix = null;
        if (siteType == SiteTypeEnum.DSP48E1)
            siteTypePrefix = "DSP48_";
        else if (siteType == SiteTypeEnum.SLICEL || siteType == SiteTypeEnum.SLICEM)
            siteTypePrefix = "SLICE_";
        else if (siteType == SiteTypeEnum.RAMB18E1)
            siteTypePrefix = "RAMB18_";
        else
            throw new IllegalStateException("ERROR: Could not assign a String prefix to  SiteTypeEnum: " + siteType);
        return siteTypePrefix;
    }

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
            sites.add(device.getSite(siteTypePrefix + "X" + instX + "Y" + i));
        }
        if (finalTailInstY - finalAnchorInstY > 16) {
            System.out.println("buffer size: " + (finalTailInstY - finalAnchorInstY));
            System.out.println("\tinitAnchor: " + initAnchor.getInstanceY() + ", initTail: " + initTail.getInstanceY());
            System.out.println("\tfinalAnchor: " + finalAnchorInstY + ", finalTail: " + finalTailInstY);
            for (Site site : sites) {
                System.out.println("\t\tSiteInst: " + occupiedSites.get(site.getSiteTypeEnum()).get(site));
            }
        }
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

    public void printSiteInstPlacements(PackedDesign packedDesign) throws IOException {
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

} // end class Placer
