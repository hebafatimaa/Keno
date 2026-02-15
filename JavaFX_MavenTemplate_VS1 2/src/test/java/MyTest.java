import static org.junit.jupiter.api.Assertions.*;

import Keno.model.GameSession;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;

class MyTest {
    // test on default states
    @Test
    void DefaultState () {
        GameSession s = new GameSession();
        assertEquals(0, s.getCurrentDrawing());
        assertFalse(s.isDrawingsLocked());
        assertFalse(s.isSpotsLocked());
        assertTrue(s.getPicks().isEmpty());
    }
    @Test
    void NewBetReset() {
        GameSession s = new GameSession();
        s.setSpotsChoice(4);
        s.lockSpots();
        s.addPick(10);
        s.updateTotals(3);
        s.addTotalMatches(java.util.Arrays.asList(1, 2, 3));
        double sessionBefore = s.getSessionTotal();
        s.newBet(); // start new bet
        assertTrue(s.getPicks().isEmpty()); // clear picks for drawing, spots, bet card
        assertTrue(s.getTotalMatches().isEmpty()); // clear matches
        assertEquals(0, s.getCurrentDrawing()); // drawing count back to 0
        assertEquals(0, s.getTotalWinnings()); // amount won reset to 0
        assertEquals(sessionBefore, s.getSessionTotal()); // keeps running total payout for session
    }
    // test on numbers from prize pool
    @Test
    void Draw20Numbers() {
        java.util.List<Integer> d = Keno.game.PrizePool.draw20();
        assertEquals(20, d.size()); // exactly 20 numbers
    }
    @Test
    void NumbersAreUnique() {
        java.util.List<Integer> d = Keno.game.PrizePool.draw20();
        assertEquals(new HashSet<>(d).size(), d.size()); // unique numbers
    }
    @Test
    void NumbersInRange() {
        java.util.List<Integer> d = Keno.game.PrizePool.draw20();
        assertTrue(d.stream().allMatch(n -> 1 <= n && n <= 80)); // numbers range from 1-80
    }
    // test on enabled/disabled features during game session
    @Test
    void LockDrawings() {
        GameSession s = new GameSession();
        s.setDrawingsChoice(3);
        s.lockDrawings();
        assertTrue(s.isDrawingsLocked()); // lock drawings
        assertEquals(3, s.getDrawingsChoice()); // save choices
    }
    @Test
    void LockSpots() {
        GameSession s = new GameSession();
        s.setSpotsChoice(4);
        s.lockSpots();
        assertTrue(s.isSpotsLocked()); // lock spots
        assertEquals(4, s.getSpotsChoice()); // save choices
    }

    // test on when there should be more drawings
    @Test
    void StartGame() {
        GameSession s = new GameSession();
        s.setDrawingsChoice(3);
        s.lockDrawings();
        s.setSpotsChoice(1);
        s.lockSpots();
        assertEquals(0, s.getCurrentDrawing()); //drawing number start at 0
        s.startIfFirst();
        assertEquals(1, s.getCurrentDrawing()); // first drawing
        s.startIfFirst(); // calling again should not affect game
        assertEquals(1, s.getCurrentDrawing()); // still the first drawing
    }
    @Test
    void CompleteDrawings() {
        GameSession s = new GameSession();
        s.setDrawingsChoice(1);
        s.lockDrawings();
        s.setSpotsChoice(1);
        s.lockSpots();
        s.startIfFirst(); // start first drawing
        assertEquals(1, s.getCurrentDrawing());
        assertFalse(s.hasMore()); // no more drawing
        s.next(); // should not run again
        assertEquals(1, s.getCurrentDrawing()); // still first drawing
    }
    @Test
    void DetermineNext() {
        GameSession s = new GameSession();
        s.setDrawingsChoice(2);
        s.lockDrawings();
        s.setSpotsChoice(1);
        s.lockSpots();
        s.startIfFirst(); // starts first drawing
        assertEquals(1, s.getCurrentDrawing());
        assertTrue(s.hasMore()); // another draw should exist
        s.next(); // starts second drawing
        assertEquals(2, s.getCurrentDrawing());
        assertFalse(s.hasMore()); // no more draws after the second one
    }
    @Test
    void NextBeforeStart() {
        GameSession s = new GameSession();
        s.setDrawingsChoice(2);
        s.setSpotsChoice(4);
        s.lockDrawings();
        s.lockSpots();
        s.next();
        assertEquals(0, s.getCurrentDrawing()); // next() before start has no effect
    }
    @Test
    void HasMoreAfterStart() {
        GameSession s = new GameSession();
        s.setDrawingsChoice(3);
        s.setSpotsChoice(1);
        s.lockDrawings();
        s.lockSpots();
        s.startIfFirst();
        assertEquals(1, s.getCurrentDrawing());
        assertTrue(s.hasMore()); // there should be more drawings left
    }

    // test on bet card picks
    @Test
    void NoDuplicates() {
        GameSession s = new GameSession();
        s.addPick(7);
        s.addPick(7);
        assertEquals(1, s.getPicks().size()); // ignore second value
        assertTrue(s.getPicks().contains(7));
    }
    @Test
    void AddRemovePicks() {
        GameSession s = new GameSession();
        s.addPick(7);
        s.addPick(15);
        assertEquals(2, s.getPicks().size());
        assertTrue(s.getPicks().contains(7));
        assertTrue(s.getPicks().contains(15));
        s.removePick(7);
        assertEquals(1, s.getPicks().size());
        assertFalse(s.getPicks().contains(7));
        assertTrue(s.getPicks().contains(15));
    }
    @Test
    void ClearPicks() {
        GameSession s = new GameSession();
        s.addPick(5);
        s.addPick(10);
        assertFalse(s.getPicks().isEmpty()); // not empty
        s.clearPicks(); // clear
        assertTrue(s.getPicks().isEmpty()); // should be empty
    }
    @Test
    void RemoveNonexistentPick() {
        GameSession s = new GameSession();
        s.addPick(10);
        int before = s.getPicks().size();
        s.removePick(11); // not present
        assertEquals(before, s.getPicks().size()); // no change
        assertTrue(s.getPicks().contains(10));
    }
    @Test
    void FillCard() {
        GameSession s = new GameSession();
        s.setDrawingsChoice(3);
        s.lockDrawings();
        s.setSpotsChoice(5);
        s.lockSpots();
        java.util.List<Integer> pool = new java.util.ArrayList<>();
        for (int i = 1; i <= 80; i++) pool.add(i);
        java.util.Collections.shuffle(pool);
        for (int i = 0; i < s.getSpotsChoice(); i++) {
            s.addPick(pool.get(i));
        }
        assertEquals(5, s.getPicks().size()); // exactly 5 numbers
        assertTrue(s.getPicks().stream().allMatch(n -> n >= 1 && n <= 80)); // within range 1-80
    }
    @Test
    void FillCardIfReady() {
        GameSession s = new GameSession();
        s.setDrawingsChoice(3);
        s.lockDrawings(); // only drawings locked (not spots)
        s.setSpotsChoice(4);
        java.util.List<Integer> before = new java.util.ArrayList<>(s.getPicks());
        if (!(s.isDrawingsLocked() && s.isSpotsLocked())) {
        } else {
            for (int i = 1; i <= s.getSpotsChoice(); i++) {
                s.addPick(i);
            }
        }
        assertEquals(before.size(), s.getPicks().size()); // button shouldn't do anything unless prior steps are completed
    }

    // test on Info Table
    @Test
    void TotalMatchesRemovesDuplicates() {
        GameSession s = new GameSession();
        s.addTotalMatches(java.util.Arrays.asList(1, 2, 3));
        s.addTotalMatches(java.util.Arrays.asList(3, 4, 5));
        java.util.Set<Integer> total = s.getTotalMatches();
        assertEquals(5, total.size()); // no duplicate values printed
        assertTrue(total.containsAll(java.util.Arrays.asList(1, 2, 3, 4, 5)));
    }
    @Test
    void PayoutInfo() {
        GameSession s = new GameSession();
        s.setSpotsChoice(4);
        s.lockSpots();
        s.updateTotals(3);  // $5
        assertEquals(5, s.getTotalWinnings()); // 3 matches in a 4-spot game should give $5
        s.updateTotals(4);  // #75
        assertEquals(80, s.getTotalWinnings()); // Total of 75 + 5 gives 80
    }
    @Test
    void NoPayoutAddsZero() {
        GameSession s = new GameSession();
        s.setSpotsChoice(4);
        s.lockSpots();
        s.updateTotals(1); // 4-spot game with only 1 hit gives $0
        assertEquals(0, s.getTotalWinnings());
    }
    @Test
    void TotalPayoutFromMultipleBets() {
        GameSession s = new GameSession();
        s.setSpotsChoice(4); //start new bet
        s.lockSpots();
        s.updateTotals(3); // $5
        s.newBet(); // add winnings to total payout
        assertEquals(5, s.getSessionTotal());

        s.setSpotsChoice(4); //start new bet
        s.lockSpots();
        s.updateTotals(4); // $75
        s.newBet(); // add winnings to total payout again
        assertEquals(80, s.getSessionTotal()); // 5 + 75
    }

    // test on payout results
    @Test
    void PayoutZeroBonus() {
        assertEquals(5, Keno.game.paytable.PaytableNorthCarolina.prizeFor(10, 0));
    }
    @Test
    void PayoutAdditionalKnownPayouts() {
        assertEquals(2, Keno.game.paytable.PaytableNorthCarolina.prizeFor(1, 1)); // 1-spot and hit 1 gives $2
        assertEquals(1, Keno.game.paytable.PaytableNorthCarolina.prizeFor(4, 2)); // 4-spot and hit 2 gives $1
        assertEquals(50, Keno.game.paytable.PaytableNorthCarolina.prizeFor(8, 6)); // 8-spot and hit 6 gives $50
        assertEquals(0, Keno.game.paytable.PaytableNorthCarolina.prizeFor(8, 3)); // 8-spot and hit 3 gives $0 (not a paying combo per your table)
    }
    @Test
    void PayoutUnknownCombosReturnZero() { //uncovered combinations should return 0
        assertEquals(0, Keno.game.paytable.PaytableNorthCarolina.prizeFor(2, 1));
        assertEquals(0, Keno.game.paytable.PaytableNorthCarolina.prizeFor(3, 3));
        assertEquals(0, Keno.game.paytable.PaytableNorthCarolina.prizeFor(8, 3));
        assertEquals(0, Keno.game.paytable.PaytableNorthCarolina.prizeFor(10, 1));
    }




}

