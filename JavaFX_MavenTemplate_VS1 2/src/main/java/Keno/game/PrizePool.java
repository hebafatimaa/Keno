package Keno.game;

import java.util.*;

public final class PrizePool {
    private PrizePool() {}

    public static List<Integer> draw20() {
        List<Integer> pool = new ArrayList<>(80);
        for (int i = 1; i <= 80; i++) pool.add(i);
        Collections.shuffle(pool);
        List<Integer> out = new ArrayList<>(pool.subList(0, 20));
        Collections.sort(out);
        return out;
    }
}