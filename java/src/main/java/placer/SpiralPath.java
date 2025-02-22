package placer;

import java.util.ArrayList;
import java.util.List;

public class SpiralPath {

    public static List<Pair<Integer, Integer>> generateDiamondSpiral(int radius) {
        List<Pair<Integer, Integer>> spiral = new ArrayList<>();

        // Always include the center first
        spiral.add(new Pair<>(0, 0));

        // For each "ring" from 1 up to 'radius'
        for (int r = 1; r <= radius; r++) {
            // Segment 1: move from (r, 0) up to (0, r)
            for (int i = 0; i <= r; i++) {
                spiral.add(new Pair<>(r - i, i));
            }

            // Segment 2: move from (0, r) to (-r, 0)
            // Start i=1 to avoid duplicating (0, r)
            for (int i = 1; i <= r; i++) {
                spiral.add(new Pair<>(-i, r - i));
            }

            // Segment 3: move from (-r, 0) down to (0, -r)
            // Start i=1 to avoid duplicating (-r, 0)
            for (int i = 1; i <= r; i++) {
                spiral.add(new Pair<>(-r + i, -i));
            }

            // Segment 4: move from (0, -r) back to (r, 0)
            // Start i=1 to avoid duplicating (0, -r)
            // End i < r so we don't duplicate (r, 0) which starts the next ring
            for (int i = 1; i < r; i++) {
                spiral.add(new Pair<>(i, -(r - i)));
            }
        }

        return spiral;
    }

}
