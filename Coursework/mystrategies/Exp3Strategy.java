package mystrategy;

import game.Game;
import game.Strategy;
import game.Utility;

import java.util.Random;
import java.util.Arrays;



/**
 * A strategy that chooses a random action on the first round, and then
 * plays that action thereafter.
 * @author maz
 *
 */
public class Exp3Strategy implements Strategy{
  /**
   * The action the player plays
   */
  static final double GAMMA = 0.2;
  int constantAction;
  Utility utility;
  int numActions;  // actions
  int rewards;
  double[] weights;
  
  
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
  public Exp3Strategy(Game g, long seed, String options) {
    this.numActions = g.getNumActions();
    Random r = new Random(seed);
    constantAction = r.nextInt(numActions);
    this.utility = g.getUtility();
    rewards = 0;
    weights = new double[numActions];
    Arrays.fill(weights, 1);
  }
  
  public int getAction() {
    for(int i = 0; i < weights.length; i++) {
      System.out.println(weights[i]);
    }
    System.out.println("My action is: " + constantAction);
    return constantAction;
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
  
  public void observeOutcome(int[] actions) {
    String output  = arrayToSimpleString(actions);
    // Printing my and other players actions
    System.out.println(output);
    // Trying to get the utility I got out of the selected actions (actions[0])
    int currentAction = actions[0];
    int[] possibleUtilities = getPossibleUtilities(actions);
    System.out.println("My utility is: " + possibleUtilities[currentAction]);
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
