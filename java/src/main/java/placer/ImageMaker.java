
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

    int x_low, x_high;
    int y_low, y_high;
    int width, height;

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
        initSites();
        construct2DSiteArray();
    }

    private void initSites() {
        for (Site site : device.getAllSites()) {
            SiteTypeEnum siteType = site.getSiteTypeEnum();
            if (uniqueSiteTypes.add(siteType)) {
                allSites.put(siteType, new ArrayList<>());
            }
            allSites.get(siteType).add(site);
        }
    }

    public void construct2DSiteArray() throws IOException {
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
        this.x_high = x_high;
        this.x_low = x_low;
        this.y_high = y_high;
        this.y_low = y_low;
        this.width = x_high - x_low + 1;
        this.height = y_high - y_low + 1;
        this.siteArray = new Site[width][height];
        for (Map.Entry<SiteTypeEnum, List<Site>> entry : this.allSites.entrySet()) {
            for (Site site : entry.getValue()) {
                int x = site.getRpmX() - x_low;
                int y = site.getRpmY() - y_low;
                this.siteArray[x][y] = site;
            }
        }
    }

    public void construct2DPlacementArray() throws IOException {
        int width = siteArray.length;
        int height = siteArray[0].length;
        int x_low = siteArray[0][0].getRpmX();
        int y_low = siteArray[0][0].getRpmY();
        this.siteInstArray = new SiteInst[width][height];
        for (SiteInst si : this.design.getSiteInsts()) {
            int x = si.getSite().getRpmX() - x_low;
            int y = si.getSite().getRpmY() - y_low;
            this.siteInstArray[x][y] = si;
        }
    }

    public void construct2DSiteArrayImage(Site[][] siteArray) throws IOException {
        int width = siteArray.length;
        int height = siteArray[0].length;
        int scale = 5; // each site will be 5×5 pixels
        int upscaledWidth = width * scale;
        int upscaledHeight = height * scale;
        BufferedImage image = new BufferedImage(upscaledWidth, upscaledHeight, BufferedImage.TYPE_INT_RGB);
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

    public BufferedImage overlayPlacementOnSiteArrayImage(BufferedImage image, Site[][] siteArray,
            SiteInst[][] siteInstArray) throws IOException {
        int width = siteArray.length;
        int height = siteArray[0].length;
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
        return image;
    }

    public BufferedImage overlayNetsOnPlacementImage(BufferedImage image, Site[][] siteArray) {
        int x_low = siteArray[0][0].getRpmX();
        int y_low = siteArray[0][0].getRpmY();
        for (Net net : design.getNets()) {
            SitePinInst src = net.getSource();
            List<SitePinInst> sinks = net.getSinkPins();

        }

        return image;
    }

    public void exportImage(BufferedImage image, String outputPath) throws IOException {
        // Write out to a file
        File outputFile = new File(outputPath);
        ImageIO.write(image, "png", outputFile);
    }

}
