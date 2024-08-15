import java.io.BufferedWriter;

public class RapidWrightBlockPlacer extends Placer {

    public Design place(Design design, BufferedWriter writer){
        design = BlockPlacer.placeDesign(design, true); // debug = true
        return design;
    }


}
