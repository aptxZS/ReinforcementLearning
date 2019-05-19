package mystrategy;

import game.Game;
import game.Strategy;
import game.Utility;

import java.util.Random;
import java.util.ArrayList;
import java.lang.Math;


/**
 * A strategy that chooses a random action on the first round, and then
 * plays that action thereafter.
 * @author maz
 *
 */
public class EA2 implements Strategy{
  /**
   * The action the player plays
   */
  int constantAction;
  Utility utility;
  int numActions;
  EA2Player playerI, playerJ;
  static final double smallGamma = 0.75;  // The response rate
  static final double smallP = 0.5;  // The scal parameter
  static final double tol = 0.1;  // The tollerance for the conditions to hold
  int stickCounter;
  int numberOfPlays;  // The t parameter
  ArrayList<Integer> myHistory;
  double bigGamma;
  
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
  public EA2(Game g, long seed, String options) {
    this.numActions = g.getNumActions();
    Random r = new Random(seed);
    constantAction = r.nextInt(numActions);
    this.utility = g.getUtility();
    playerI = new EA2Player();
    playerJ = new EA2Player();
    stickCounter = 5;  // stick for the first 5 moves
    numberOfPlays = 0;
    myHistory = new ArrayList<Integer>();
    bigGamma = 0;
  }
  
  public int getAction() {
    if(stickCounter > 0) {  // 1st condition
      System.out.println("My action is: " + constantAction);
      return constantAction;
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

  // Updates the follow and stick indices of the other two players
  public void updateIndeces() {
    // double bigGamma = 0;
    // for(int k = 1; k < numberOfPlays; k++) {
    //   bigGamma += Math.pow(smallGamma, numberOfPlays - k);
    // }
    // System.out.println("Big gamma: " + bigGamma);
    this.bigGamma += Math.pow(smallGamma, numberOfPlays - 1);

    System.out.println("This big gamma: " + this.bigGamma);
    System.out.println("number of plays: " + numberOfPlays);
    playerI.updateStickIndex(smallGamma, bigGamma, numberOfPlays, smallP);
    playerJ.updateStickIndex(smallGamma, bigGamma, numberOfPlays, smallP);
    playerI.updateFollowIndices(smallGamma, bigGamma, numberOfPlays, smallP, playerJ.history, myHistory);
    playerI.updateFollowIndices(smallGamma, bigGamma, numberOfPlays, smallP, playerI.history, myHistory);
  }
  
  public void observeOutcome(int[] actions) {
    myHistory.add(actions[0]);
    playerI.history.add(actions[1]);
    playerJ.history.add(actions[2]);
    numberOfPlays++;
    if(numberOfPlays > 1) {
      updateIndeces();
    }
    System.out.println(playerI);
    System.out.println(playerJ);
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
