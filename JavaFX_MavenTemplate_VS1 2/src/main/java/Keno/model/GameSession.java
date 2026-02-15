package Keno.model;

import Keno.game.paytable.PaytableNorthCarolina;

import java.util.*;

public class GameSession {
    private int drawingsChoice = 0;   // 1,2,3,4
    private int spotsChoice    = 0;   // 1,4,8,10
    private boolean drawingsLocked = false;
    private boolean spotsLocked    = false;

    private final LinkedHashSet<Integer> picks = new LinkedHashSet<>();

    private int currentDrawing = 0;

    private final LinkedHashSet<Integer> totalMatches = new LinkedHashSet<>();
    private int totalWinnings = 0;
    private int sessionTotal  = 0;

    public int getDrawingsChoice() {
        return drawingsChoice;
    }

    public void setDrawingsChoice(int v) {
        this.drawingsChoice = v;
    }

    public int getSpotsChoice() {
        return spotsChoice;
    }

    public void setSpotsChoice(int v) {
        this.spotsChoice = v;
    }

    public boolean isDrawingsLocked() {
        return drawingsLocked;
    }

    public void lockDrawings() {
        this.drawingsLocked = (drawingsChoice > 0);
    }

    public boolean isSpotsLocked() {
        return spotsLocked;
    }

    public void lockSpots() {
        this.spotsLocked = (spotsChoice > 0);
    }

    public Set<Integer> getPicks() {
        return Collections.unmodifiableSet(picks);
    }
    public void addPick(int n) {
        picks.add(n);
    }
    public void removePick(int n) {
        picks.remove(n);
    }
    public void clearPicks() {
        picks.clear();
    }

    public int getCurrentDrawing() {
        return currentDrawing;
    }

    public void startIfFirst() {
        if (currentDrawing == 0) currentDrawing = 1;
    }
    public void next() {
        if (currentDrawing > 0 && currentDrawing < drawingsChoice) currentDrawing++;
    }
    public boolean hasMore() {
        return currentDrawing > 0 && currentDrawing < drawingsChoice;
    }

    public Set<Integer> getTotalMatches() {
        return Collections.unmodifiableSet(totalMatches);
    }

    public void addTotalMatches(Collection<Integer> m) {
        totalMatches.addAll(m);
    }

    public int getTotalWinnings() {
        return totalWinnings;
    }

    public int getSessionTotal() {
        return sessionTotal;
    }

    public void updateTotals(int hits) {
        int prize = PaytableNorthCarolina.prizeFor(spotsChoice, hits);
        totalWinnings += prize;
        sessionTotal  += prize;
    }

    public void newBet() {
        drawingsChoice = 0;
        spotsChoice = 0;
        drawingsLocked = false;
        spotsLocked = false;
        resetCardState();
    }

    public void resetCardState() {
        picks.clear();
        currentDrawing = 0;
        totalMatches.clear();
        totalWinnings = 0;
    }
}