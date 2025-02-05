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
import com.xilinx.rapidwright.design.Net;

import com.xilinx.rapidwright.device.Device;
import com.xilinx.rapidwright.device.Tile;
import com.xilinx.rapidwright.device.Site;
import com.xilinx.rapidwright.device.SiteTypeEnum;

public class PlacerGreedyRandom1 extends Placer {

    protected Map<SiteTypeEnum, List<Site>> occupiedSites;
    protected Map<SiteTypeEnum, List<Site>> availableSites;

    public PlacerGreedyRandom1(String rootDir, Design design, Device device) throws IOException {
        super(rootDir, design, device);
    }

    public void placeDesign(PackedDesign packedDesign) throws IOException {
        initAvailableSites(packedDesign);
        double lowestCost = evaluateCost(); // initial cost
        int staleMoves = 0;
        while (true) {
            unplaceDesign(packedDesign);
            randomMove(packedDesign);
            double currCost = evaluateCost();
            if (currCost < lowestCost) {
                design.writeCheckpoint(placedDcp);
                staleMoves = 0;
            }
            staleMoves++;
            if (staleMoves > 100)
                break;
        }
    }

    private void initAvailableSites(PackedDesign packedDesign) {
        this.occupiedSites = packedDesign.occupiedSites;
        this.availableSites = packedDesign.availableSites;
    }

    private void unplaceDesign(PackedDesign packedDesign) {
        // unplace and replace one-by-one instead of batch unplace
        for (List<SiteInst> chain : packedDesign.CARRYSiteInstChains) {
            for (SiteInst si : chain) {
                si.unPlace();
                SiteTypeEnum type = si.getSiteTypeEnum();
                Site site = si.getSite();
                occupiedSites.get(type).remove(site);
                availableSites.get(type).add(site);
            }
        }
        for (SiteInst si : packedDesign.CLBSiteInsts) {
            si.unPlace();
            SiteTypeEnum type = si.getSiteTypeEnum();
            Site site = si.getSite();
            occupiedSites.get(type).remove(site);
            availableSites.get(type).add(site);
        }
        // for (List<SiteInst> cascade : packedDesign.DSPSiteInstCascades)
        // for (SiteInst si : cascade)
        // si.unPlace();
        // for (SiteInst si : packedDesign.RAMSiteInsts)
        // si.unPlace();
    }

    private void randomMove(PackedDesign packedDesign) {
        Random rand = new Random();
        List<List<SiteInst>> CARRYSiteInstChains = packedDesign.CARRYSiteInstChains;
        List<List<SiteInst>> DSPSiteInstCascades = packedDesign.DSPSiteInstCascades;

    }

    private Site selectCLBSite() {
        Random rand = new Random();
        List<Site> compatibleSites = new ArrayList<>();
        compatibleSites.addAll(availableSites.get(SiteTypeEnum.SLICEL));
        compatibleSites.addAll(availableSites.get(SiteTypeEnum.SLICEM));
        Site selectedSite = compatibleSites.get(rand.nextInt(compatibleSites.size()));
        SiteTypeEnum selectedSiteType = selectedSite.getSiteTypeEnum();
        availableSites.get(selectedSiteType).remove(selectedSite);
        occupiedSites.get(selectedSiteType).add(selectedSite);
        return selectedSite;
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
