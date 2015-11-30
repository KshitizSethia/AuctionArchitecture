package auction.infrastructure.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

import auction.common.AuctionItem;
import auction.common.Bid;

public class AuctionState {

  private List<AuctionItem>    items;
  // index starts from 0
  private int                  currentItemIndex;
  private List<IAuctionPlayer> players;

  private List<Bid>            bidsForCurrentRound;

  private final Object         lock;

  public AuctionState(Scanner scannerToAuctionState)
      throws FileNotFoundException {
    lock = new Object();

    items = new ArrayList<AuctionItem>();

    int index = 0;
    while (scannerToAuctionState.hasNext()) {
      String line = scannerToAuctionState.nextLine();
      if (!line.contains(",")) {
        items.add(new AuctionItem(line, index));
      } else {
        String[] parts = line.split(",");
        items.add(new AuctionItem(Integer.parseInt(parts[0]), parts[1],
            parts[2], Float.parseFloat(parts[3])));
      }
      index++;
    }

    scannerToAuctionState.close();

    currentItemIndex = Integer.MAX_VALUE;
    bidsForCurrentRound = new ArrayList<Bid>();
  }

  void startAuctioning(List<IAuctionPlayer> players) {
    synchronized (lock) {
      this.players = players;
      currentItemIndex = 0;
      notifyAllPlayers(true);
    }
  }

  void placeBid(Bid bid) {
    synchronized (lock) {

      Log.info("validating bid" + bid);
      IAuctionPlayer playerPlacingBid = validateBid(bid);

      Log.info("accepted bid: " + bid);
      playerPlacingBid.decrementTimeLeft(bid.timeTakenToBid);
      playerPlacingBid.disableMove();

      bidsForCurrentRound.add(bid);
      Log.info("current bids: " + bidsForCurrentRound.toString());

      if (allBidsPlaced()) {
        executeCurrentRound();
      }
    }
  }

  private void notifyAllPlayers(boolean enableThem) {
    synchronized (lock) {
      for (IAuctionPlayer player : players) {
        if (enableThem) {
          player.enableMove();
        } else {
          player.disableMove();
        }
      }
    }
  }

  private void executeCurrentRound() {
    Log.info("all bids are in, executing round");
    Log.info("current bids: " + bidsForCurrentRound);

    Bid winningBid = getWinningBid();
    Log.info("winning bid is: " + winningBid);
    if (winningBid != null) {
      // update item
      AuctionItem itemWon = items.get(currentItemIndex);

      // update winning player
      for (IAuctionPlayer player : players) {
        if (winningBid.authID.equals(player.getAuthId())) {
          itemWon.setWinner(player.getName(), winningBid.amount);
          player.decrementCashLeft(winningBid.amount);
          break;
        }
      }

      // update auction state
      currentItemIndex++;
      bidsForCurrentRound.clear();

      Log.info("Round " + (currentItemIndex - 1)
          + " complete, current auction state: \n" + this.toString());

      // enable or disable players based on whether game is over
      if (isGameOn()) {
        notifyAllPlayers(true);
      } else {
        notifyAllPlayers(false);
      }

    } else {
      // todo verify this never happens
      throw new IllegalStateException("Got a null winning bid, server error!");
    }
  }

  boolean isGameOn() {
    return biddersStillPresent() && itemsLeftToAuction();
  }

  private boolean itemsLeftToAuction() {
    return currentItemIndex < items.size();
  }

  private boolean biddersStillPresent() {
    if (players == null) {
      return false;
    }

    for (IAuctionPlayer player : players) {
      if (player.getCashLeft() > 0 && player.getTimeLeft() > 0) {
        return true;
      }
    }
    return false;
  }

  /**
   * 
   * @return
   */
  private Bid getWinningBid() {
    // todo handle case where all players are finished with cash and can't place
    // bid - This case should never occur

    // get best bid at index 0
    Collections.sort(bidsForCurrentRound, new Comparator<Bid>() {

      @Override
      public int compare(Bid bid1, Bid bid2) {
        if (bid1.amount != bid2.amount) {
          return bid1.amount > bid2.amount ? -1 : 1;
        }
        return bid1.timeTakenToBid < bid2.timeTakenToBid ? -1 : 1;
      }

    });

    return bidsForCurrentRound.get(0);
  }

  private boolean allBidsPlaced() {
    for (IAuctionPlayer player : players) {

      if (player.getTimeLeft() > 0) {

        boolean alivePlayerHasBid = false;

        for (Bid recordedBid : bidsForCurrentRound) {
          if (recordedBid.authID.equals(player.getAuthId())) {
            alivePlayerHasBid = true;
            break;
          }
        }

        if (!alivePlayerHasBid) {
          return false;
        }

      }
    }
    return true;
  }

  /**
   * ALSO decreases time left for player ALSO removes players who have timed out
   * 
   * @param bid
   */
  private IAuctionPlayer validateBid(Bid bid) {
    // verify if correct item being bid on
    if (bid.itemIndex != currentItemIndex) {
      // todo let bidder place another bid if wrong bid placed
      throw new IllegalArgumentException(Error.BID_FOR_WRONG_ITEM.toString());
    }
    // verify only one bid placed per bidder per round
    if (bidsForCurrentRound.contains(bid)) {
      throw new IllegalArgumentException(Error.BID_DUPLICATE.toString());
    }

    for (IAuctionPlayer player : players) {
      if (bid.authID == player.getAuthId()) {

        // verify if player has cash to pay if bid goes through
        // todo let player place another bid
        if (bid.amount > player.getCashLeft()) {
          throw new IllegalArgumentException(
              Error.PLAYER_NOT_ENOUGH_CASH.toString());
        }

        // verify if player has time left to bid
        if (player.getTimeLeft() < bid.timeTakenToBid) {
          // todo don't let player place bid after it has timed out
          throw new IllegalArgumentException(Error.PLAYER_TIME_OUT.toString());
        }

        // verification complete, return player for updates to player
        return player;
      }
    }

    throw new IllegalArgumentException("validateBid has no player to return");
  }

  @Override
  public String toString() {
    synchronized (lock) {
      StringBuilder sbr = new StringBuilder();
      for (AuctionItem item : items) {
        sbr.append(item.toString() + "\n");
      }
      return sbr.toString();
    }
  }

  void removePlayer(Player player) {
    synchronized (lock) {
      players.remove(player);
      Log.info("Removed player from AuctionState, current players " + players);
    }
  }

  public List<AuctionItem> getItems() {
    synchronized (lock) {
      return Collections.unmodifiableList(items);
    }
  }

}
