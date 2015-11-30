package auction.bidder.impl;

import java.util.Random;

import auction.common.AuctionItem;
import auction.common.BiddersBid;
import auction.common.ReadOnlyAuctionState;
import auction.common.Settings;

public class Bidder {

  private final String playerGuid;
  private float        cashLeft;

  public Bidder(String playerGuid) {
    this.playerGuid = playerGuid;
    this.cashLeft = Settings.INITIAL_CASH;
  }

  public BiddersBid getBid(ReadOnlyAuctionState auctionState) {
    AuctionItem itemToBeBid = auctionState.getItemToBeBid();

    /**
     * Your code goes here!
     * 
     * 
     */

    float bidAmount = new Random().nextInt((int) cashLeft);
    BiddersBid result = new BiddersBid(playerGuid, itemToBeBid.getIndex(),
        itemToBeBid.getType(), bidAmount);
    return result;
  }

  public void decrementCash(float amount) {
    cashLeft -= amount;
  }

}
