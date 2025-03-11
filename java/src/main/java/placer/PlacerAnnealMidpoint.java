
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

public class PlacerAnnealMidpoint extends PlacerAnnealRandom {

    private String placerName = "PlacerAnnealMidpoint";

    private int spiralPathMaxRadius = 1000;
    private List<Pair<Integer, Integer>> spiralPath = SpiralPath.generateDiamondSpiral(spiralPathMaxRadius);

    public PlacerAnnealMidpoint(String rootDir, Design design, Device device, ClockRegion region)
            throws IOException {
        super(rootDir, design, device, region);
    }

    @Override
    public String getPlacerName() {
        return this.placerName;
    }

    private Pair<Integer, Integer> findMidpoint(List<Site> connections) {
        int sum_x = 0;
        int sum_y = 0;
        int size = connections.size();
        for (Site conn : connections) {
            sum_x += conn.getRpmX();
            sum_y += conn.getRpmY();
        }
        Pair<Integer, Integer> midpoint = new Pair<Integer, Integer>(sum_x / size, sum_y / size);
        return midpoint;
    } // end findMidpoint()

    @Override
    protected Site proposeSite(SiteInst si, List<Site> connections, boolean swapEnable) {
        if (connections == null || connections.isEmpty()) {
            return proposeRandomSite(si, connections, swapEnable);
        } else {
            return proposeMidpointSite(si, connections, swapEnable);
        }
    } // end proposeSite()

    @Override
    protected Site proposeAnchorSite(List<SiteInst> chain, List<Site> connections, boolean swapEnable) {
        if (connections == null || connections.isEmpty()) {
            return proposeRandomAnchorSite(chain, connections, swapEnable);
        } else {
            return proposeMidpointAnchorSite(chain, connections, swapEnable);
        }

    }

    protected Site proposeMidpointSite(SiteInst si, List<Site> connections, boolean swapEnable) {
        // https://adaptivesupport.amd.com/s/question/0D52E00006hpT8KSAU/vivado-coordinate-systems?language=en_US
        SiteTypeEnum ste = si.getSiteTypeEnum();
        Pair<Integer, Integer> connsMidpt = findMidpoint(connections);
        int mid_x = connsMidpt.key();
        int mid_y = connsMidpt.value();

        // The rpmGrid is non-convex, so we need to radially search around
        // the proposed midpoint to find a legal site.
        int spiralPathSize = 10000;

        // spiral path search for legal site
        Site selectedSite = null;
        int attempts = 0;
        while (true) {
            if (attempts > spiralPathSize)
                throw new IllegalStateException(
                        "ERROR: Could not propose " + ste + " site after  " + spiralPathSize + " attempts!");
            int dx = spiralPath.get(attempts).key();
            int dy = spiralPath.get(attempts).value();
            int curr_x = mid_x + dx;
            int curr_y = mid_y + dy;
            if (curr_x < 0 || curr_y < 0) {
                System.out.println("(x, y): (" + curr_x + ", " + curr_y + ")");
            }

            selectedSite = rpmGrid[curr_x][curr_y];
            // System.out.println("currSite: " + selectedSite);

            // selectedSite = device.getSite(getSiteTypePrefix(ste) + "X" + curr_x + "Y" +
            // curr_y);

            if (selectedSite == null) {
                attempts++;
                continue;
            }
            if (selectedSite.getSiteTypeEnum() != ste) {
                attempts++;
                continue;
            }
            if (occupiedSiteChains.containsKey(ste)) {
                // never propose single site swap with a chain
                if (occupiedSiteChains.get(ste).containsKey(selectedSite)) {
                    attempts++;
                    continue;
                }
            } else if (!swapEnable) { // if swaps disabled
                if (occupiedSites.get(ste).containsKey(selectedSite)) {
                    attempts++;
                    continue;
                }
            }
            // System.out.println("Attempts: " + attempts + " accepted: " + selectedSite);
            break;
        }
        return selectedSite;

    }

    protected Site proposeMidpointAnchorSite(List<SiteInst> chain, List<Site> connections, boolean swapEnable) {
        if (connections == null || connections.isEmpty()) {
            return proposeRandomAnchorSite(chain, connections, swapEnable);
        }

        boolean validAnchor = false;
        Site selectedAnchor = null;
        SiteTypeEnum ste = chain.get(0).getSiteTypeEnum();
        Pair<Integer, Integer> connsMidpt = findMidpoint(connections);
        int mid_x = connsMidpt.key();
        int mid_y = connsMidpt.value();
        int spiralPathSize = 10000;
        int attempts = 0;
        while (true) {
            if (attempts > spiralPathSize)
                throw new IllegalStateException(
                        "ERROR: Could not propose " + ste + " anchor site after  " + spiralPathSize + " attempts!");
            int dx = spiralPath.get(attempts).key();
            int dy = spiralPath.get(attempts).value();
            int curr_x = mid_x + dx;
            if (curr_x < 0)
                curr_x = 0;
            int curr_y = mid_y + dy;
            if (curr_y < 0)
                curr_y = 0;
            selectedAnchor = rpmGrid[curr_x][curr_y];
            if (selectedAnchor == null) {
                attempts++;
                continue;
            }
            if (selectedAnchor.getSiteTypeEnum() != ste) {
                attempts++;
                continue;
            }
            if (!swapEnable) {
                if (occupiedSites.get(ste).containsKey(selectedAnchor)) {
                    attempts++;
                    continue;
                }
            }
            int anchorInstX = selectedAnchor.getInstanceX();
            int anchorInstY = selectedAnchor.getInstanceY();
            for (int i = 0; i < chain.size(); i++) {
                Site site = device.getSite(getSiteTypePrefix(ste) + "X" + anchorInstX + "Y" + (anchorInstY + i));
                if (site == null) {
                    validAnchor = false;
                    break;
                }
                if (!allSites.get(ste).contains(site)) {
                    validAnchor = false;
                    break;
                }
                if (!swapEnable) {
                    if (occupiedSites.get(ste).containsKey(site)) {
                        validAnchor = false;
                        break;
                    }
                }
                if (site.getSiteTypeEnum() != ste) {
                    validAnchor = false;
                    break;
                }
                validAnchor = true;
            }
            attempts++;
            if (validAnchor)
                break;
        }
        return selectedAnchor;
    } // end proposeMidpointAnchorSite()

} // end class
