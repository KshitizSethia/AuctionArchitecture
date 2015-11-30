package auction.bidder.impl;

import java.util.Random;

import auction.common.AuctionItem;
import auction.common.Bid;
import auction.common.ReadOnlyAuctionState;
import auction.common.Settings;

public class Bidder {

  private final String playerGuid;
  private float        cashLeft;

  public Bidder(String playerGuid) {
    this.playerGuid = playerGuid;
    this.cashLeft = Settings.INITIAL_CASH;
  }

  public Bid getBid(ReadOnlyAuctionState auctionState) {
    AuctionItem itemToBeBid = auctionState.getItemToBeBid();

    /**
     * Your code goes here!
     * 
     * 
     */

    float bidAmount = new Random().nextInt((int) cashLeft);
    // todo make new Bid constructor
    Bid result = new Bid(playerGuid, itemToBeBid.getIndex(),
        itemToBeBid.getType(), bidAmount, 0);
    return result;
  }

}
