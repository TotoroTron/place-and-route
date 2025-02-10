
package placer;

import java.awt.Color;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.FileWriter;
import java.io.File;
import java.io.IOException;

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

public class PlacerGreedyRandom2 extends Placer {

    private Set<SiteTypeEnum> uniqueSiteTypes;
    private Map<SiteTypeEnum, List<Site>> allSites;
    private Map<SiteTypeEnum, Set<Site>> occupiedSites;
    private Random rand;

    private List<Double> costHistory;
    private List<Long> moveTimes;
    private List<Long> evalTimes;
    private List<Long> writeTimes;

    private static final Map<SiteTypeEnum, Color> SITE_TYPE_COLORS = new HashMap<>();
    static {
        SITE_TYPE_COLORS.put(SiteTypeEnum.SLICEL, Color.CYAN);
        SITE_TYPE_COLORS.put(SiteTypeEnum.SLICEM, Color.CYAN);
        SITE_TYPE_COLORS.put(SiteTypeEnum.RAMB18E1, Color.GREEN);
        SITE_TYPE_COLORS.put(SiteTypeEnum.FIFO18E1, Color.GREEN);
        SITE_TYPE_COLORS.put(SiteTypeEnum.RAMB36E1, Color.GREEN);
        SITE_TYPE_COLORS.put(SiteTypeEnum.DSP48E1, Color.YELLOW);
        SITE_TYPE_COLORS.put(SiteTypeEnum.BUFGCTRL, Color.MAGENTA);
    }

    public PlacerGreedyRandom2(String rootDir, Design design, Device device) throws IOException {
        super(rootDir, design, device);
        this.placerName = "PlacerGreedyRandom2";
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
        export2DSiteArrayImageUpscaled(construct2DSiteArray(), rootDir + "/outputs/site_array.png");
        Double lowestCost = evaluateDesign(); // initial cost
        int staleMoves = 0;
        int totalMoves = 0;
        unplaceAllSiteInsts(packedDesign);
        randomInitialPlacement(packedDesign);
        design.writeCheckpoint(rootDir + "/outputs/placed_inital.dcp");
        // printSiteInstPlacements(packedDesign);

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
            if (staleMoves > 20 || totalMoves > 200)
                break;
        }

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
        // Chunkiest movements first
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
        // SiteTypeEnum[] compatibleTypes = new SiteTypeEnum[4];
        // compatibleTypes[0] = SiteTypeEnum.SLICEL;
        // compatibleTypes[1] = SiteTypeEnum.SLICEL;
        // SiteTypeEnum ste = (rand.nextDouble() < 0.75)
        // ? SiteTypeEnum.SLICEL
        // : SiteTypeEnum.SLICEM;
        for (SiteInst si : packedDesign.CLBSiteInsts) {
            Site selectedSite = proposeSite(si.getSiteTypeEnum());
            placeSiteInst(si, selectedSite);
        }
    }

    private void randomInitRAMSites(PackedDesign packedDesign) throws IOException {
        // SiteTypeEnum[] compatibleTypes = new SiteTypeEnum[2];
        // compatibleTypes[0] = SiteTypeEnum.RAMB18E1;
        // compatibleTypes[1] = SiteTypeEnum.FIFO18E1;
        // SiteTypeEnum ste = compatibleTypes[rand.nextInt(2)];
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
                placeSiteInst(cascade.get(i), newSite);
            }
        }
    }

    private void randomInitCARRYSiteChains(PackedDesign packedDesign) throws IOException {
        // SiteTypeEnum[] compatibleTypes = new SiteTypeEnum[4];
        // compatibleTypes[0] = SiteTypeEnum.SLICEL;
        // compatibleTypes[1] = SiteTypeEnum.SLICEL;
        // SiteTypeEnum ste = (rand.nextDouble() < 0.75)
        // ? SiteTypeEnum.SLICEL
        // : SiteTypeEnum.SLICEM;
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
            Site newSite = proposeSite(ste);
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

    private Site proposeSite(SiteTypeEnum ste) {
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

    public Site[][] construct2DSiteArray() throws IOException {
        int x_high = 0, y_high = 0;
        int x_low = 99999999, y_low = 9999999;
        for (Map.Entry<SiteTypeEnum, List<Site>> entry : this.allSites.entrySet()) {
            for (Site site : entry.getValue()) {
                int site_x = site.getRpmX();
                if (site_x > x_high)
                    x_high = site_x;
                if (site_x < x_low)
                    x_low = site_x;
                int site_y = site.getRpmY();
                if (site_y > y_high)
                    y_high = site_y;
                if (site_y < y_low)
                    y_low = site_y;
            }
        }
        int width = x_high - x_low + 1;
        int height = y_high - y_low + 1;
        Site[][] siteArray = new Site[width][height];
        for (Map.Entry<SiteTypeEnum, List<Site>> entry : this.allSites.entrySet()) {
            for (Site site : entry.getValue()) {
                int x = site.getRpmX() - x_low;
                int y = site.getRpmY() - y_low;
                siteArray[x][y] = site;
            }
        }
        return siteArray;
    }

    public void export2DSiteArrayImage(Site[][] siteArray, String outputPath) throws IOException {
        int width = siteArray.length;
        int height = siteArray[0].length;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Site site = siteArray[x][y];
                Color color = null;
                if (site == null) {
                    color = Color.BLACK;
                } else {
                    SiteTypeEnum type = site.getSiteTypeEnum();
                    color = SITE_TYPE_COLORS.getOrDefault(type, Color.GRAY);
                }
                image.setRGB(x, (height - 1 - y), color.getRGB());
            }
        }
        File outputFile = new File(outputPath);
        ImageIO.write(image, "png", outputFile);
    }

    public void export2DSiteArrayImageUpscaled(Site[][] siteArray, String outputPath) throws IOException {
        int width = siteArray.length;
        int height = siteArray[0].length;
        int scale = 5; // each site will be 5×5 pixels

        int upscaledWidth = width * scale;
        int upscaledHeight = height * scale;

        // Create a new (upscaled) BufferedImage
        BufferedImage image = new BufferedImage(upscaledWidth, upscaledHeight, BufferedImage.TYPE_INT_RGB);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                // Determine the site color
                Site site = siteArray[x][y];
                Color c;
                if (site == null) {
                    c = Color.BLACK;
                } else {
                    SiteTypeEnum type = site.getSiteTypeEnum();
                    c = SITE_TYPE_COLORS.getOrDefault(type, Color.GRAY);
                }

                // "Bottom-left" to "top-left" invert:
                // The 'y' in siteArray=0 means the bottom row, but Java image=0 is the top row.
                // So we flip (height - 1 - y). For each site, fill 5x5 block.
                int destY = (height - 1 - y) * scale; // top-left system
                int destX = x * scale;

                // Fill 5x5 block in the upscaled image
                for (int dx = 0; dx < scale; dx++) {
                    for (int dy = 0; dy < scale; dy++) {
                        image.setRGB(destX + dx, destY + dy, c.getRGB());
                    }
                }
            }
        }

        // Write out to a file
        File outputFile = new File(outputPath);
        ImageIO.write(image, "png", outputFile);
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
