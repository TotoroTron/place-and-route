package src;

import com.xilinx.rapidwright.design.Design;
import com.xilinx.rapidwright.device.Device;

import com.xilinx.rapidwright.placer.blockplacer.BlockPlacer;

public class RapidWrightBlockPlacer extends Placer {

    public Design place(Design design) {
        BlockPlacer blockPlacer = new BlockPlacer();
        design = blockPlacer.placeDesign(design, true); // debug = true
        return design;
    }


}
