package auction.infrastructure.impl;

public enum Error {
  BID_DUPLICATE("Bid alredy placed for this round"),
  BID_FOR_WRONG_ITEM("Bid placed for wrong item"), BID_NULL("Bid is null"),
  GAME_NOT_STARTED_CANT_PLACE_BID(
      "Game not started, cannot place bid right now"),
  GAME_STARTED_CANNOT_ADD_LISTENER("Game in progress, cannot add listener"),
  PLAYER_NOT_ENOUGH_CASH("Player doesn't have enough cash"),
  PLAYER_NOT_REGISTERED("Player not registered"),
  PLAYER_TIME_OUT("Player has run out of time");

  private String message;

  private Error(String message) {
    this.message = message;
  }

  @Override
  public String toString() {
    return message;
  }
}
