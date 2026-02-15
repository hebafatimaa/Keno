package Keno.model;

import java.util.List;

public final class Result {
    public final List<Integer> matches;
    public final int hits;
    public final int prize;

    public Result(List<Integer> matches, int hits, int prize) {
        this.matches = matches;
        this.hits = hits;
        this.prize = prize;
    }
}