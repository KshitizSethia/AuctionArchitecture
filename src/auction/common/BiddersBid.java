package auction.common;

import auction.infrastructure.impl.Bid;

public class BiddersBid extends Bid {

  public BiddersBid(String authID, int itemIndex, String itemType,
      float amount) {
    super(authID, itemIndex, itemType, amount, 0);
  }

  @Override
  public String toString() {
    return String.format("%s,%d,%s,%f", authID, itemIndex, itemType, amount);
  }

}
