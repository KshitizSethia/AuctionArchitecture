package auction.common;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Scanner;

import auction.infrastructure.impl.AuctionState;

public class ReadOnlyAuctionState {

  private final AuctionState auctionState;

  public ReadOnlyAuctionState(String auctionStateString)
      throws FileNotFoundException {
    auctionState = new AuctionState(new Scanner(auctionStateString));
  }

  /**
   * 
   * @return unmodifiable list of items
   */
  public List<AuctionItem> getItems() {
    return auctionState.getItems();
  }

  public AuctionItem getItemToBeBid() {
    int minIndex = -1;
    AuctionItem chosenItem = null;
    for (AuctionItem item : auctionState.getItems()) {
      if (!item.isWon() && item.getIndex() < minIndex) {
        minIndex = item.getIndex();
        chosenItem = item;
      }
    }
    return chosenItem;
  }

}
