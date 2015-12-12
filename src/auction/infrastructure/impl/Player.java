package auction.infrastructure.impl;

import auction.common.Settings;
import auction.infrastructure.api.IAuctionPlayer;

public class Player implements IAuctionPlayer {

  private final AuctionModel model;

  private final String       authID;
  private final String       name;
  private final Object       lock;
  private float              cashLeft;
  private float              timeLeft;
  private volatile boolean   canMove;

  public Player(String name, String authID, AuctionModel model) {
    this.authID = authID;
    this.name = name;
    lock = new Object();
    cashLeft = Settings.INITIAL_CASH;
    timeLeft = Settings.INITIAL_TIME;
    canMove = false;
    this.model = model;
  }

  @Override
  public float getCashLeft() {
    synchronized (lock) {
      return cashLeft;
    }
  }

  @Override
  public void decrementCashLeft(float cashLeft) {
    synchronized (lock) {
      this.cashLeft -= cashLeft;

      if (cashLeft <= 0) {
        model.removePlayer(this);
      }
    }
  }

  @Override
  public float getTimeLeft() {
    synchronized (lock) {
      return timeLeft;
    }
  }

  @Override
  public void decrementTimeLeft(float timeTaken) {
    synchronized (lock) {
      this.timeLeft -= timeTaken;

      if (timeLeft < 0) {
        model.removePlayer(this);
      }
    }
  }

  @Override
  public String toString() {
    synchronized (lock) {
      return String.format(
          "(Name: %s, ID: %s, cashLeft: %f, time left (millis): %f)", name,
          authID, getCashLeft(), getTimeLeft());
    }
  }

  @Override
  public String getAuthId() {
    return authID;
  }

  @Override
  public String getName() {
    return name;
  }

  public boolean isGameOn() {
    synchronized (lock) {
      return model.isGameOn();
    }
  }

  @Override
  public String getGameState() {
    synchronized (lock) {
      return model.getStatus();
    }
  }

  @Override
  public void placeBid(Bid bid) {
    synchronized (lock) {
      model.placeBid(bid);
    }
  }

  @Override
  public void enableMove() {
    synchronized (lock) {
      canMove = true;
    }
  }

  @Override
  public boolean canMove() {
    synchronized (lock) {
      return canMove;
    }
  }

  @Override
  public void disableMove() {
    synchronized (lock) {
      canMove = false;
    }
  }
}
