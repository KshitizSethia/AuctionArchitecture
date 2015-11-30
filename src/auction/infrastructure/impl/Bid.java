package auction.infrastructure.impl;

public class Bid {

  public final String authID;
  public final float  amount;
  public final int    itemIndex;
  public final float  timeTakenToBid;
  public final String itemType;

  public Bid(String authID, int itemIndex, String itemType, float amount,
      float timeTakenToBid) {
    this.authID = authID;
    this.amount = amount;
    this.itemIndex = itemIndex;
    this.itemType = itemType;
    this.timeTakenToBid = timeTakenToBid;
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof Bid)) {
      return false;
    }

    Bid otherBid = (Bid) obj;
    return this.authID.equals(otherBid.authID)
        && this.itemIndex == otherBid.itemIndex;
  }

  @Override
  public String toString() {
    return String.format(
        "(Player auth: %s, amount: %f, itemIndex: %d, itemType: %s, timeTakenToBid: %f)",
        authID, amount, itemIndex, itemType, timeTakenToBid);
  }
}
