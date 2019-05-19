package mystrategy;

import game.Game;
import game.Strategy;
import game.Utility;

import java.util.Random;
import java.util.Arrays;
import java.lang.Math;


public class Egreedy implements Strategy{
  /**
   * The action the player plays
   */
  int constantAction;
  Utility utility;
  int numActions;
  static final double EPSILON = 0.1;
  double[] rewards, myfrequencies;
  
  
  /**
   * Construct a constant strategy. The game is
   * used to get the number of actions, and the
   * seed is used to seed a pseudorandom number 
   * generator, which then is queried for the action 
   * to take.
   * 
   * @param g
   * @param seed
   */
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
    System.out.println(rewards[0]);
    for(int i = 1; i < rewards.length; i++) {
      System.out.println(rewards[i]);
      if(rewards[i] > rewards[indexOfLargest]) {
        indexOfLargest = i;
      }
    }
    return indexOfLargest;
  }
  
  public int getAction() {
    if (Math.random() > this.EPSILON) {
      System.out.println("    REWARD VECTOR: ");
      int indexOfLargest = argmax(rewards);
      System.out.println("My action is: " + indexOfLargest);
      myfrequencies[indexOfLargest] += 1;
      return indexOfLargest;
    }
    int rand = new Random().nextInt(numActions);
    System.out.println("My action is random: " + rand);
    myfrequencies[rand] += 1;
    return rand;
  }

  public static String arrayToSimpleString(int[] actions){
    if (actions.length==0){
      return "";
    }
    String result = ""+actions[0];
    for(int i=1;i<actions.length;i++){
      result+=" "+actions[i];
    }
    return result;
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
    String output  = arrayToSimpleString(actions);
    // Printing my and other players actions
    System.out.println(output);
    // Trying to get the utility I got out of the selected actions (actions[0])
    int currentAction = actions[0];
    int[] possibleUtilities = getPossibleUtilities(actions);
    System.out.println("My utility is: " + possibleUtilities[currentAction]);
    updateRewardVector(possibleUtilities[currentAction], currentAction);
    // observeUtilities(possibleUtilities,currentAction);
  }

  public void observeUtilities(int[] possibleUtilities, int actionTaken) {
    System.out.println("Utilities length: " + possibleUtilities.length);
    for(int i=0;i<possibleUtilities.length;i++){
      //regrets[actionTaken][i] += possibleUtilities[i]-possibleUtilities[actionTaken];
      System.out.println(possibleUtilities[i]);
    }
  }
  
  
}
