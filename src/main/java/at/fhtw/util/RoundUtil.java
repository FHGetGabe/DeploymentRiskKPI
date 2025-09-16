package at.fhtw.util;

public class RoundUtil {
  public static double roundToSixDecimals (double value) {
    return Math.round(value * 1_000_000.0) / 1_000_000.0;
  }
}
