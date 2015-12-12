package auction.infrastructure.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import auction.common.Error;
import auction.infrastructure.api.IAuctionListener;
import auction.infrastructure.api.IAuctionModel;
import auction.infrastructure.api.IAuctionPlayer;

public class AuctionModel implements IAuctionModel {

  private final Object                 playerLock;
  private final Object                 gameLock;
  private final List<IAuctionListener> listeners;
  private final AuctionState           state;

  AuctionModel(String fileWithAuctionItems) throws FileNotFoundException {

    playerLock = new Object();
    gameLock = new Object();

    Scanner fileScanner = new Scanner(new File(fileWithAuctionItems));
    state = new AuctionState(fileScanner);
    listeners = new ArrayList<IAuctionListener>();
  }

  void addListener(IAuctionListener listener) {
    synchronized (playerLock) {
      synchronized (gameLock) {
        if (isGameOn()) {
          throw new IllegalStateException(
              Error.GAME_STARTED_CANNOT_ADD_LISTENER.toString());
        }
        if (!listeners.contains(listener)) {
          Log.info("adding listener: " + listener);
          listeners.add(listener);
        }
      }
    }
  }

  void startAuctioning() {
    synchronized (gameLock) {
      Log.info("starting auction");
      List<IAuctionPlayer> players =
          new ArrayList<IAuctionPlayer>(listeners.size());
      for (IAuctionListener listener : listeners) {
        if (listener instanceof IAuctionPlayer) {
          Log.info("player added to AuctionState: " + listener);
          players.add((IAuctionPlayer) listener);
        }
      }
      state.startAuctioning(players);
      Log.info("Auction starting, current state:\n" + state.toString());
    }
  }

  void placeBid(Bid bid) {
    if (bid == null) {
      throw new IllegalArgumentException(Error.BID_NULL.toString());
    }
    synchronized (gameLock) {
      if (!state.isGameOn()) {
        throw new IllegalStateException(
            Error.GAME_NOT_STARTED_CANT_PLACE_BID.toString());
      }
      Log.info("placed bid: " + bid.toString());
      // accept bid only from bidders who are in listeners
      boolean authenticatedBidder = false;
      for (IAuctionListener listener : listeners) {
        if (bid.authID.equals(listener.getAuthId())) {
          authenticatedBidder = true;
          break;
        }
      }
      if (!authenticatedBidder) {
        throw new IllegalArgumentException(
            Error.PLAYER_NOT_REGISTERED.toString());
      }

      // forward bid to state
      state.placeBid(bid);
    }
  }

  String getStatus() {
    synchronized (gameLock) {
      return state.toString();
    }
  }

  public void removePlayer(Player player) {
    synchronized (playerLock) {
      synchronized (gameLock) {
        listeners.remove(player);
        state.removePlayer(player);
      }
    }
  }

  public boolean isGameOn() {
    synchronized (gameLock) {
      return state.isGameOn();
    }
  }
}
