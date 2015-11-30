package auction.bidder.impl;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import auction.common.Bid;
import auction.common.ReadOnlyAuctionState;
import auction.common.Settings;

public class BidderConnector {

  public static void main(String[] args)
      throws UnknownHostException, IOException {
    if (args.length < 3) {
      System.out.println("Usage: <player_name> <player_guid> <port>");
      System.exit(-1);
    }

    String playerName = args[0];
    String playerGuid = args[1];
    int port = Integer.parseInt(args[2]);

    Bidder bidder = new Bidder(playerGuid);

    Socket connector = new Socket(InetAddress.getLocalHost(), port);

    DataOutputStream out = new DataOutputStream(connector.getOutputStream());
    DataInputStream in = new DataInputStream(connector.getInputStream());

    boolean gameOn = true;

    while (gameOn) {
      String incomingMessage = in.readUTF();
      Log.info("Recieved from server\n" + incomingMessage);

      if (incomingMessage.contains(Settings.GAME_END)) {
        Log.info("Recieved game end");
        gameOn = false;
        break;
      }

      ReadOnlyAuctionState auctionState =
          new ReadOnlyAuctionState(incomingMessage);
      Bid bid = bidder.getBid(auctionState);
      Log.info("sending bid: " + bid.toString());
      out.writeUTF(bid.toString());
      out.flush();

      String response = in.readUTF();
      while (!response.equals(Settings.BID_ACCEPTED)) {
        Log.severe("bid not accepted, error message: " + response);
        Log.info("trying to bid again");
        bid = bidder.getBid(auctionState);
        Log.info("sending bid: " + bid.toString());
        out.writeUTF(bid.toString());
        out.flush();
      }
      Log.info("Bid has been accepted");

    }

    Log.info("Bidder is exiting!");
    connector.close();
  }

}
