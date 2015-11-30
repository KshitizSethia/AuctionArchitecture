package auction.bidder.impl;

import java.util.logging.Level;

abstract class Log {

  private static java.util.logging.Logger logger =
      java.util.logging.Logger.getLogger(Log.class.getName());

  public static void log(Level level, String msg) {
    logger.log(level, msg);
  }

  public static void info(String msg) {
    logger.log(Level.INFO, msg);
  }

  public static void severe(String msg) {
    logger.log(Level.SEVERE, msg);
  }

  public static void veryVerbose(String msg) {
    logger.log(Level.FINEST, msg);
  }
}
