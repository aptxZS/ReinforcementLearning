package mystrategy;

import java.util.ArrayList;
import java.lang.Math;

public class EA2Player {

  double stickIndex, followIndex, followOther, follow0;
  ArrayList<Integer> history;

  public EA2Player() {
    stickIndex = followIndex = followOther = follow0 = 0;
    history = new ArrayList<Integer>();
  }

  public void updateStickIndex(double smallGamma, double bigGamma, int numberOfPlays, double smallP) {
    double result = 0;
    for(int k = 1; k < numberOfPlays; k++) {
      double upperLimit = 11;
      double x = Math.pow(smallGamma, -k) / bigGamma;
      double diff = (upperLimit + history.get(k) + history.get(k-1)) % upperLimit;
      double distance = diff < upperLimit - diff ? diff : upperLimit - diff;
      result += x / Math.pow(distance, smallP);
    }
    stickIndex = -result;
  }

  public void updateFollowIndeces(double smallGamma, double bigGamma, int numberOfPlays, double smallP, ArrayList<Integer> historyOther, ArrayList<Integer> history0) {
    double followOtherResult = 0;
    double follow0Result = 0;
    double followIndexResult = 0;
    for(int k = 1; k < numberOfPlays; k++) {
      double upperLimit = 11;
      double x = Math.pow(smallGamma, -k) / bigGamma;
      // Updating follow player J index
      double diffWithOther = (upperLimit + history.get(k) + historyOther.get(k-1)) % upperLimit;
      double distanceWithOther = diffWithOther < upperLimit - diff ? diff : upperLimit - diff;
      followOtherResult += x / Math.pow(distance, smallP);
      // Updating follow player 0 index
      double diffWith0 = (upperLimit + history.get(k) + history0.get(k-1)) % upperLimit;
      double distanceWith0 = diffWith0 < upperLimit - diff ? diff : upperLimit - diff;
      follow0Result += x / Math.pow(distance, smallP);
      // Updating general follow index
      followIndexResult += x / Math.pow(Math.min(diffWithOther, diffWith0) , smallP);
    }
    followOther = -followOtherResult;
    follow0 = -follow0Result;
    followIndex = -followIndexResult;
  }

}
