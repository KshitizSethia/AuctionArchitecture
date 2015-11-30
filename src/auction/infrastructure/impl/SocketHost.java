package auction.infrastructure.impl;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

import auction.common.Bid;
import auction.common.Settings;

public class SocketHost implements Runnable {

  private final Player       player;
  private final int          port;
  private final String       playerName;
  private final ServerSocket serverSocket;

  SocketHost(Player player, int listenOnPort)
      throws UnknownHostException, IOException {
    this.player = player;
    port = listenOnPort;
    playerName = player.getName();
    serverSocket = new ServerSocket(port, 1000, InetAddress.getLocalHost());
    Log.info("Created server socket for " + playerName);
  }

  @Override
  public void run() {
    try {
      // todo keep time running between connection drops
      Socket socket = connectSocket();

      DataOutputStream out = new DataOutputStream(socket.getOutputStream());
      BufferedReader in =
          new BufferedReader(new InputStreamReader(socket.getInputStream(),
              StandardCharsets.UTF_8));

      while (player.isGameOn()) {
        if (player.canMove()) {

          sendCurrentStateToPlayer(out);

          // track time taken to reply
          long startTime = System.nanoTime();

          // in format: authenticationID itemIndex itemType amount
          // reading in 1 line at a time, so player can play multiple moves
          // which can be queued
          String incomingMessage = in.readLine();

          float timeDiff = getMillisElapsedFrom(startTime);

          Log.info("Message from " + playerName + ":\n" + incomingMessage);
          try {
            String[] parts = incomingMessage.split(" ");

            player.placeBid(new Bid(parts[0], Integer.parseInt(parts[1]),
                parts[2], Float.parseFloat(parts[3]), timeDiff));
            // bid successful
            out.writeUTF(Settings.BID_ACCEPTED);
            out.flush();
          } catch (IllegalStateException
              | IllegalArgumentException inputError) {
            // bid error, send message to player
            out.writeUTF(inputError.getMessage() + "\n");
            out.flush();
          }
        }
      }
      out.writeUTF(Settings.GAME_END);
      out.flush();

      serverSocket.close();

      Log.info("ServerSocket is outside it's while loop, game must have ended");
    } catch (IOException ioException) {
      Log.severe("Exception in SocketHost for " + player
          + ". SocketHost is now exiting.\n" + ioException);
    }
  }

  private float getMillisElapsedFrom(long startTime) {
    long endTime = System.nanoTime();
    float timeDiff = (float) ((endTime - startTime) * 1e6);
    Log.info(String.format("%s took %f time(millis)", playerName, timeDiff));
    return timeDiff;
  }

  private void sendCurrentStateToPlayer(DataOutputStream out)
      throws IOException {
    String outgoingMessage = "START\n" + player.getGameState() + "\nEND\n";
    Log.veryVerbose("Message to " + playerName + ":\n" + outgoingMessage);
    out.writeUTF(outgoingMessage);
    out.flush();
  }

  private Socket connectSocket() throws IOException, UnknownHostException {
    Log.info("Waiting to connect with " + playerName);
    Socket socket = serverSocket.accept();
    Log.info("Connected to: " + playerName);
    return socket;
  }
}