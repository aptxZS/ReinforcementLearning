package mystrategy;

import game.Game;
import game.Strategy;
import game.Utility;

import java.util.Random;
import java.util.Arrays;
import java.lang.Math;
import java.util.stream.*;


/**
 * Reducing the Lemonade Stand Game to a multi-armed bandit problem
 * and solve it via Exp3 method.
 * @author Dimitri Diomaiuta
 */
public class Exp3Strategy implements Strategy{
  
  static final double GAMMA = 0.2;
  int constantAction;
  Utility utility;
  int numActions;  // actions
  double[] weights;
  double selectedProb;

  public Exp3Strategy(Game g, long seed, String options) {
    this.numActions = g.getNumActions();
    Random r = new Random(seed);
    constantAction = r.nextInt(numActions);
    this.utility = g.getUtility();
    weights = new double[numActions];
    Arrays.fill(weights, 1);
    selectedProb = 0;
  }

  // Uses categorical distribution to get the action index
  public int getActionIndexFromProbabilities(double[] probabilities) {
    double cumulative = 0;
    double rand = Math.random();
    for(int i = 0; i < probabilities.length; i++) {
      cumulative += probabilities[i];
      if(cumulative > rand) {
        selectedProb = probabilities[i];
        return i;
      }
    }
    selectedProb = probabilities[probabilities.length - 1];
    return probabilities.length - 1;
  }
  
  public int getAction() {
    double weightsSum = DoubleStream.of(weights).sum();
    double[] probabilities = new double[numActions];
    for(int i = 0; i < weights.length; i++) {
      probabilities[i] = (1 - this.GAMMA) * (weights[i] / weightsSum) + (this.GAMMA * 1.0 / numActions);
    }
    int action = getActionIndexFromProbabilities(probabilities);
    return action;
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

  public void updateWeights(int currentAction, int currentReward) {
    double estimatedReward = (currentReward / numActions) / selectedProb;
    weights[currentAction] *= Math.exp(estimatedReward * this.GAMMA / numActions);
  }
  
  public void observeOutcome(int[] actions) {
    // Trying to get the utility I got out of the selected actions (actions[0])
    int currentAction = actions[0];
    int[] possibleUtilities = getPossibleUtilities(actions);
    int currentReward = possibleUtilities[currentAction];
    updateWeights(currentAction, currentReward);
  }
  
}
