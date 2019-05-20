package mystrategy;

import java.util.ArrayList;
import java.lang.Math;

public class EA2Player {

  double stickIndex, followIndex, followOther, follow0;
  ArrayList<Integer> history;
  static double upperLimit;

  public EA2Player() {
    stickIndex = followIndex = followOther = follow0 = 0;
    history = new ArrayList<Integer>();
    upperLimit = 12;
  }

  public static int oppositeLocation(int location) {
    int half = (int) upperLimit / 2;
    return location >=  half ? location - half : location + half;
  }

  public void updateStickIndex(double smallGamma, double bigGamma, int numberOfPlays, double smallP) {
    double result = 0;
    for(int k = 1; k < numberOfPlays; k++) {
      double x = Math.pow(smallGamma, numberOfPlays - k) / bigGamma;
      // System.out.println("k: " + history.get(k) + " k-1: " + history.get(k-1));
      double diff = (upperLimit + history.get(k) - history.get(k-1)) % upperLimit;
      double distance = diff < upperLimit - diff ? diff : upperLimit - diff;
      // System.out.println(distance);
      result += x * Math.pow(distance, smallP);
    }
    stickIndex = -result;
  }

  public void updateFollowIndices(double smallGamma, double bigGamma, int numberOfPlays, double smallP, ArrayList<Integer> historyOther, ArrayList<Integer> history0) {
    double followOtherResult = 0;
    double follow0Result = 0;
    double followIndexResult = 0;
    for(int k = 1; k < numberOfPlays; k++) {
      double x = Math.pow(smallGamma, numberOfPlays - k) / bigGamma;
      // Updating follow player J index
      double diffWithOther = (upperLimit + history.get(k) - oppositeLocation(historyOther.get(k-1))) % upperLimit;
      double distanceWithOther = diffWithOther < upperLimit - diffWithOther ? diffWithOther : upperLimit - diffWithOther;
      followOtherResult += x * Math.pow(distanceWithOther, smallP);
      // Updating follow player 0 index
      double diffWith0 = (upperLimit + history.get(k) - oppositeLocation(history0.get(k-1))) % upperLimit;
      double distanceWith0 = diffWith0 < upperLimit - diffWith0 ? diffWith0 : upperLimit - diffWith0;
      follow0Result += x * Math.pow(distanceWith0, smallP);
      // Updating general follow index
      followIndexResult += x * Math.pow(Math.min(distanceWithOther, distanceWith0) , smallP);
    }
    followOther = -followOtherResult;
    follow0 = -follow0Result;
    followIndex = -followIndexResult;
  }

  public String toString() {
    return "Player {\n    stickIndex: " + stickIndex + "\n    followIndex: " + followIndex + "\n    followOther: " + followOther + "\n    follow0: " + follow0 + "\n    history: " + history + "\n}";
  }

}
