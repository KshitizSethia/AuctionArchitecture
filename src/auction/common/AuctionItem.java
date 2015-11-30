package auction.common;

public final class AuctionItem {

  private static final String NO_WINNER = "-";
  private final String        type;
  private final int           index;
  private String              winnerName;
  private float               winningAmount;
  private boolean             winnerSet;

  public AuctionItem(String type, int index) {
    this.type = type;
    this.index = index;
    winnerSet = false;
  }

  public AuctionItem(int index, String type, String winnerName,
      float winningAmount) {
    this.index = index;
    this.type = type;
    if (!winnerName.equals(NO_WINNER)) {
      this.winnerSet = true;
      this.winnerName = winnerName;
      this.winningAmount = winningAmount;
    } else {
      winnerSet = false;
    }
  }

  public void setWinner(String winnerName, float amount) {
    if (winnerSet) {
      throw new IllegalStateException("This item is already sold!");
    }
    winnerSet = true;
    this.winnerName = winnerName;
    winningAmount = amount;
  }

  @Override
  public String toString() {
    String result = String.format("(%d,%s,%s,%f)", index, type,
        winnerSet ? winnerName : NO_WINNER, winnerSet ? winningAmount : 0);
    return result;
  }

  public boolean isWon() {
    return winnerSet;
  }

  public int getIndex() {
    return index;
  }

  public String getType() {
    return type;
  }

}
