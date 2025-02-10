
package placer;

import java.awt.Graphics2D;
import java.awt.BasicStroke;
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

import com.xilinx.rapidwright.design.Design;
import com.xilinx.rapidwright.design.Net;
import com.xilinx.rapidwright.design.SiteInst;
import com.xilinx.rapidwright.design.SitePinInst;

import com.xilinx.rapidwright.edif.EDIFHierCellInst;
import com.xilinx.rapidwright.edif.EDIFHierPortInst;

import com.xilinx.rapidwright.device.Device;
import com.xilinx.rapidwright.device.Site;
import com.xilinx.rapidwright.device.SitePIPStatus;
import com.xilinx.rapidwright.device.SiteTypeEnum;
import com.xilinx.rapidwright.device.ClockRegion;

public class ImageMaker {

    Design design;
    Device device;

    private Set<SiteTypeEnum> uniqueSiteTypes;
    private Map<SiteTypeEnum, List<Site>> allSites;
    private Site[][] siteArray;
    private SiteInst[][] siteInstArray;

    private int x_low, y_low;
    private int width, height;
    private final int scale = 5; // hard-coded

    private BufferedImage image;

    private static final Map<SiteTypeEnum, Color> SITE_TYPE_COLORS = new HashMap<>();
    static {
        // Dull empty colors
        SITE_TYPE_COLORS.put(SiteTypeEnum.SLICEL, Color.decode("#3A4F5F")); // Dark Gray
        SITE_TYPE_COLORS.put(SiteTypeEnum.SLICEM, Color.decode("#3A4F5F")); // Dark Gray
        SITE_TYPE_COLORS.put(SiteTypeEnum.RAMB18E1, Color.decode("#775555")); // Dull Red
        SITE_TYPE_COLORS.put(SiteTypeEnum.FIFO18E1, Color.decode("#775555")); // Dull Red
        SITE_TYPE_COLORS.put(SiteTypeEnum.RAMB36E1, Color.decode("#775555")); // Dull Red
        SITE_TYPE_COLORS.put(SiteTypeEnum.DSP48E1, Color.decode("#446644")); // Dull Green
        SITE_TYPE_COLORS.put(SiteTypeEnum.BUFGCTRL, Color.decode("#888888")); // Medium Gray
    }

    private static final Map<SiteTypeEnum, Color> PLACEMENT_COLORS = new HashMap<>();
    static {
        // Bright active colors
        PLACEMENT_COLORS.put(SiteTypeEnum.SLICEL, Color.decode("#00FFFF")); // Cyan
        PLACEMENT_COLORS.put(SiteTypeEnum.SLICEM, Color.decode("#00FFFF")); // Cyan
        PLACEMENT_COLORS.put(SiteTypeEnum.RAMB18E1, Color.decode("#FF00FF")); // Magenta
        PLACEMENT_COLORS.put(SiteTypeEnum.FIFO18E1, Color.decode("#FF00FF")); // Magenta
        PLACEMENT_COLORS.put(SiteTypeEnum.RAMB36E1, Color.decode("#FF00FF")); // Magenta
        PLACEMENT_COLORS.put(SiteTypeEnum.DSP48E1, Color.decode("#FFFF00")); // Yellow
        PLACEMENT_COLORS.put(SiteTypeEnum.BUFGCTRL, Color.decode("#FFFFFF")); // White
    }

    public ImageMaker(Design design) throws IOException {
        this.design = design;
        this.device = design.getDevice();
        this.uniqueSiteTypes = new HashSet<>();
        this.allSites = new HashMap<>();
        initSites();
        this.image = new BufferedImage(scale * width, scale * height, BufferedImage.TYPE_INT_RGB);
    }

    private void initSites() {
        for (Site site : device.getAllSites()) {
            SiteTypeEnum siteType = site.getSiteTypeEnum();
            if (uniqueSiteTypes.add(siteType)) {
                allSites.put(siteType, new ArrayList<>());
            }
            allSites.get(siteType).add(site);
        }
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
        this.x_low = x_low;
        this.y_low = y_low;
        this.width = x_high - x_low + 1;
        this.height = y_high - y_low + 1;
        this.siteArray = new Site[width][height];
    }

    public void construct2DSiteArray() throws IOException {
        for (Map.Entry<SiteTypeEnum, List<Site>> entry : this.allSites.entrySet()) {
            for (Site site : entry.getValue()) {
                int x = site.getRpmX() - x_low;
                int y = site.getRpmY() - y_low;
                this.siteArray[x][y] = site;
            }
        }
    }

    public void construct2DPlacementArray() throws IOException {
        this.siteInstArray = new SiteInst[width][height];
        for (SiteInst si : this.design.getSiteInsts()) {
            int x = si.getSite().getRpmX() - x_low;
            int y = si.getSite().getRpmY() - y_low;
            this.siteInstArray[x][y] = si;
        }
    }

    public void construct2DSiteArrayImage() throws IOException {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Site site = siteArray[x][y];
                Color c;
                if (site == null) {
                    c = Color.BLACK;
                } else {
                    SiteTypeEnum type = site.getSiteTypeEnum();
                    c = SITE_TYPE_COLORS.getOrDefault(type, Color.DARK_GRAY);
                }

                // "Bottom-left" to "top-left" invert:
                // siteArray y=0 => bottom row, but Java image y=0 => top row.
                int destY = (height - 1 - y) * scale; // top-left system
                int destX = x * scale;

                // Fill a 1-pixel thick 5×5 border with the site color, black interior
                for (int dx = 0; dx < scale; dx++) {
                    for (int dy = 0; dy < scale; dy++) {
                        // Check if we're on the border (top/bottom/left/right)
                        boolean onBorder = (dx == 0 || dx == scale - 1 || dy == 0 || dy == scale - 1);
                        if (onBorder) {
                            image.setRGB(destX + dx, destY + dy, c.getRGB());
                        } else {
                            // Interior is black
                            image.setRGB(destX + dx, destY + dy, Color.BLACK.getRGB());
                        }
                    }
                }
            }
        }
    }

    public void overlayPlacementOnSiteArrayImage() throws IOException {
        int scale = 5;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                SiteInst si = siteInstArray[x][y];
                if (si == null)
                    continue;

                SiteTypeEnum type = si.getSiteTypeEnum();
                Color c = PLACEMENT_COLORS.getOrDefault(type, Color.DARK_GRAY);
                int destY = (height - 1 - y) * scale;
                int destX = x * scale;

                for (int dx = 0; dx < scale; dx++) {
                    for (int dy = 0; dy < scale; dy++) {
                        image.setRGB(destX + dx, destY + dy, c.getRGB());
                    }
                }
            }
        }
    }

    public void overlayNetsOnPlacementImage() {
        Graphics2D g2d = image.createGraphics();
        try {
            g2d.setStroke(new BasicStroke(1));
            List<Net> nets = new ArrayList<>(design.getNets());
            nets.sort((net1, net2) -> Integer.compare(net1.getSinkPins().size(), net2.getSinkPins().size()));
            for (Net net : nets) {
                // if (net.isClockNet()) {
                // System.out.println("Skipped Clock Net: " + net.getName());
                // continue;
                // }
                //
                if (net.isStaticNet()) {
                    System.out.println("Skipped Static Net: " + net.getName());
                    continue;
                }
                SitePinInst src = net.getSource();
                if (src == null) {
                    // SPI is null of the net is purely intra-site!
                    continue;
                }
                if (src.getSiteTypeEnum().equals(SiteTypeEnum.BUFGCTRL))
                    continue;

                List<SitePinInst> sinks = net.getSinkPins();
                int numSinks = sinks.size();
                int maxSinks = 20; // color of all nets with 10 sinks or more
                float ratio = (float) Math.min(numSinks, maxSinks) / (float) maxSinks;
                int redValue = (int) (0x80 + ratio * (0xFF - 0x80)); // range: 0x40..0xFF
                Color scaledRed = new Color(redValue, 0, 0);
                g2d.setColor(scaledRed);

                for (SitePinInst sink : sinks) {
                    int src_x = src.getSite().getRpmX() - x_low;
                    int src_y = src.getSite().getRpmY() - y_low;
                    int src_center_x = src_x * scale + scale / 2;
                    int src_center_y = (height - 1 - src_y) * scale + scale / 2;

                    int sink_x = sink.getSite().getRpmX() - x_low;
                    int sink_y = sink.getSite().getRpmY() - y_low;
                    int sink_center_x = sink_x * scale + scale / 2;
                    int sink_center_y = (height - 1 - sink_y) * scale + scale / 2;

                    g2d.drawLine(src_center_x, src_center_y, sink_center_x, sink_center_y);
                }
            }
        } finally {
            g2d.dispose();
        }
    }

    public void exportImage(String outputPath) throws IOException {
        // Write out to a file
        File outputFile = new File(outputPath);
        ImageIO.write(image, "png", outputFile);
    }

}
