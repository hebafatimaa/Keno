package Keno.game.paytable;

import java.util.Map;

public class PaytableNorthCarolina {
    private static final Map<Integer, Map<Integer, Integer>> TABLE = Map.of(
            1,  Map.of(1, 2),
            4,  Map.of(2, 1, 3, 5, 4, 75),
            8,  Map.of(4, 2, 5, 12, 6, 50, 7, 750, 8, 10_000),
            10, Map.of(0, 5, 5, 2, 6, 15, 7, 40, 8, 450, 9, 4_250, 10, 100_000)
    );

    private PaytableNorthCarolina() {}

    public static int prizeFor(int spots, int hits) {
        var byHits = TABLE.get(spots);
        return (byHits == null) ? 0 : byHits.getOrDefault(hits, 0);
    }
}
