package mystrategy;

import game.Game;
import game.Strategy;
import game.Utility;

import java.util.Random;
import java.util.Arrays;
import java.lang.Math;


/**
 * Reducing the Lemonade Stand Game to a multi-armed bandit problem
 * and solve it via Egreedy method.
 * @author Dimitri Diomaiuta
 */
public class Egreedy implements Strategy{

  int constantAction;
  Utility utility;
  int numActions;
  static final double EPSILON = 0.1;
  double[] rewards, myfrequencies;

  public Egreedy(Game g, long seed, String options) {
    this.numActions = g.getNumActions();
    Random r = new Random(seed);
    constantAction = r.nextInt(numActions);
    this.utility = g.getUtility();
    rewards = new double[numActions];
    myfrequencies = new double[numActions];
    Arrays.fill(rewards, 0);
    Arrays.fill(myfrequencies, 0);
  }

  private int argmax(double[] rewards) {
    int indexOfLargest = 0;
    for(int i = 1; i < rewards.length; i++) {
      if(rewards[i] > rewards[indexOfLargest]) {
        indexOfLargest = i;
      }
    }
    return indexOfLargest;
  }
  
  public int getAction() {
    if (Math.random() > this.EPSILON) {
      int indexOfLargest = argmax(rewards);
      myfrequencies[indexOfLargest] += 1;
      return indexOfLargest;
    }
    int rand = new Random().nextInt(numActions);
    myfrequencies[rand] += 1;
    return rand;
  }

  /* Given the actions of all the players it returns an array which
     size is the size of possible actions. At each index contains the
     possible revenue if that action number (which is the index) , had
     been taken. Intuitevely we can access our utility by
     possibleUtilities[actionTaken] */
  public int[] getPossibleUtilities(int[] actions){
    int[] possibleUtilities=new int[numActions];
    for(int i=0;i<possibleUtilities.length;i++){
      actions[0]=i;
      possibleUtilities[i]=utility.getUtility(actions)[0];
    }
    return possibleUtilities;
  }

  private void updateRewardVector(int currentReward, int action) {
    double n = myfrequencies[action];
    double prevReward = rewards[action];
    rewards[action] = ((n - 1) / n) * prevReward + (1 / n) * currentReward;
  }
  
  public void observeOutcome(int[] actions) {
    // Trying to get the utility I got out of the selected actions (actions[0])
    int currentAction = actions[0];
    int[] possibleUtilities = getPossibleUtilities(actions);
    updateRewardVector(possibleUtilities[currentAction], currentAction);
  }
  
}
