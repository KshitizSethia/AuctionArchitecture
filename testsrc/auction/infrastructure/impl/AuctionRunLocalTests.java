package auction.infrastructure.impl;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.util.UUID;

import org.junit.Test;

import auction.common.Settings;

public class AuctionRunLocalTests {

  @Test
  public void test_twoItems_addTwoPlayers_FirstWinsBothBidsByAmount()
      throws FileNotFoundException {
    AuctionModel model = new AuctionModel("testsrc/two_rounds.txt");

    String guid1 = UUID.randomUUID().toString();
    String guid2 = UUID.randomUUID().toString();

    Player p1 = new Player("p1", guid1, model);
    Player p2 = new Player("p2", guid2, model);
    model.addListener(p1);
    model.addListener(p2);

    // start auctioning
    assertFalse(p1.canMove());
    assertFalse(p2.canMove());
    assertFalse(model.isGameOn());
    model.startAuctioning();
    assertTrue(model.isGameOn());

    // ROUND 1
    // ----player 1s bid
    Bid p1_bid1 = new Bid(guid1, 0, "t5", 5, 10);
    assertTrue(p1.canMove());
    p1.placeBid(p1_bid1);
    assertTrue(
        p1.getTimeLeft() == Settings.INITIAL_TIME - p1_bid1.timeTakenToBid);
    assertFalse(p1.canMove());
    // ----player2s bid
    Bid p2_bid1 = new Bid(guid2, 0, "t5", 4, 10);
    assertTrue(p2.canMove());
    p2.placeBid(p2_bid1);
    assertTrue(
        p1.getTimeLeft() == Settings.INITIAL_TIME - p2_bid1.timeTakenToBid);
    // ----last player in a round will be enabled back instantly for next round
    // ----(except in last round)
    assertTrue(p2.canMove());
    // ----game status
    assertTrue(p1.getCashLeft() == Settings.INITIAL_CASH - p1_bid1.amount);
    assertTrue(p2.getCashLeft() == Settings.INITIAL_CASH);
    assertTrue(model.isGameOn());
    assertEquals(model.getStatus(), "(0,t5,p1,5.000000)\n(1,t4,-,0.000000)\n");

    // ROUND 2
    // ----player 1s bid
    Bid p1_bid2 = new Bid(guid1, 1, "t4", 10, 11);
    assertTrue(p1.canMove());
    p1.placeBid(p1_bid2);
    assertTrue(p1.getTimeLeft() == Settings.INITIAL_TIME
        - p1_bid1.timeTakenToBid - p1_bid2.timeTakenToBid);
    assertFalse(p1.canMove());
    // ----player2s bid
    Bid p2_bid2 = new Bid(guid2, 1, "t4", 9, 10);
    assertTrue(p2.canMove());
    p2.placeBid(p2_bid2);
    assertTrue(p2.getTimeLeft() == Settings.INITIAL_TIME
        - p2_bid1.timeTakenToBid - p2_bid2.timeTakenToBid);
    // ----end of game, players will not be enabled
    assertFalse(p2.canMove());
    assertFalse(p1.canMove());
    // ----game status
    assertTrue(p1.getCashLeft() == Settings.INITIAL_CASH - p1_bid1.amount
        - p1_bid2.amount);
    assertTrue(p2.getCashLeft() == Settings.INITIAL_CASH);
    assertFalse(model.isGameOn());
    assertEquals(model.getStatus(),
        "(0,t5,p1,5.000000)\n(1,t4,p1,10.000000)\n");
  }
}
