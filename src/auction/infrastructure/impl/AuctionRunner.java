package auction.infrastructure.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class AuctionRunner {

  // private static final int BIDDERS_PER_TEAM = 2;

  private static final HashMap<String, String[]> teamDetails =
      new HashMap<String, String[]>();

  public static void main(String[] args)
      throws NumberFormatException, UnknownHostException, IOException {
    if (args.length < 2) {
      System.out.println("Usage: <input_file> <team_details>");
      System.exit(-1);
    }
    AuctionModel model = new AuctionModel(args[0]);

    // add all team names and their guids
    readTeamsFromFile(args[1]);

    // make server socket for each team
    List<Thread> serverSockets = new ArrayList<Thread>(teamDetails.size());

    for (String teamName : teamDetails.keySet()) {
      Log.info("trying to setup team: " + teamName);

      String teamGuid = teamDetails.get(teamName)[0];
      String portNum = teamDetails.get(teamName)[1];

      Player player = new Player(teamName, teamGuid, model);
      model.addListener(player);

      SocketHost host = new SocketHost(player, Integer.parseInt(portNum));
      serverSockets.add(new Thread(host));
    }

    //startGame
    model.startAuctioning();
    for (Thread thread : serverSockets) {
      thread.start();
    }
  }

  static void readTeamsFromFile(String filePath) throws FileNotFoundException {
    // read <team_name> <team_guid> pairs from file
    Scanner scanner = new Scanner(new File(filePath));
    while (scanner.hasNext()) {
      String teamName = scanner.next();
      String teamGuid = scanner.next();
      String teamPort = scanner.next();
      teamDetails.put(teamName, new String[] { teamGuid, teamPort });
    }
    scanner.close();
  }
}
