package auction.infrastructure.impl;

import auction.infrastructure.api.IAuctionListener;

public interface IAuctionPlayer extends IAuctionListener {

  public void decrementCashLeft(float amount);

  public float getCashLeft();

  public float getTimeLeft();

  public void decrementTimeLeft(float timeTakenToBid);

  public String getName();

  public void enableMove();

  String getGameState();

  void placeBid(Bid bid);

  boolean canMove();

  public void disableMove();
}
