package auction.common;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Scanner;

import auction.infrastructure.impl.AuctionState;

public class ReadOnlyAuctionState {

  private final AuctionState auctionState;

  public ReadOnlyAuctionState(String auctionStateString)
      throws FileNotFoundException {
    // remove brackets used for pretty printing
    auctionStateString = auctionStateString.replace("(", "").replace(")", "");

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
    int minIndex = Integer.MAX_VALUE;
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
