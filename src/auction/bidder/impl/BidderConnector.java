package auction.bidder.impl;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import auction.common.BiddersBid;
import auction.common.Error;
import auction.common.ReadOnlyAuctionState;
import auction.common.Settings;

public class BidderConnector {

  public static void main(String[] args)
      throws UnknownHostException, IOException {
    if (args.length < 2) {
      System.out.println("Usage: <player_guid> <port>");
      System.exit(-1);
    }

    String playerGuid = args[0];
    int port = Integer.parseInt(args[1]);

    Bidder bidder = new Bidder(playerGuid);

    boolean gameOn = true;

    while (gameOn) {
      Socket connector = tryConnectingToServer(port);

      DataOutputStream out = new DataOutputStream(connector.getOutputStream());
      DataInputStream in = new DataInputStream(connector.getInputStream());

      String incomingMessage = in.readUTF();
      Log.info("Recieved from server\n" + incomingMessage);

      if (incomingMessage.contains(Settings.GAME_END)) {
        Log.info("Recieved game end");
        gameOn = false;
        break;
      }

      ReadOnlyAuctionState auctionState =
          new ReadOnlyAuctionState(incomingMessage);
      BiddersBid bid = bidder.getBid(auctionState);

      Log.info("sending bid: " + bid.toString());
      out.writeUTF(bid.toString() + "\n");
      out.flush();

      String response = in.readUTF();
      if (response.contains(Settings.BID_ACCEPTED)) {
        Log.info("Bid has been accepted");
        bidder.decrementCash(bid.amount);
      } else {
        Log.severe("bid not accepted, error message: " + response);

        /**
         * You can use the error code here to debug what you are doing wrong
         */
        Error error = Error.valueOf(response);
        if (error == Error.PLAYER_NOT_ENOUGH_CASH
            || error == Error.PLAYER_TIME_OUT) {
          gameOn = false;
        }
      }

      out.close();
      in.close();
      connector.close();
    }

    Log.info("Bidder is exiting!");
  }

  private static Socket tryConnectingToServer(int port) {
    Socket connector = null;
    do {
      try {
        connector = new Socket(InetAddress.getLocalHost(), port);
      } catch (Exception ignored) {
      }
    } while (connector == null);
    return connector;
  }

}
