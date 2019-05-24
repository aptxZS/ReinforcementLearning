package mystrategy;

import game.Game;
import game.Strategy;
import game.Utility;

import java.util.Random;
import java.lang.Math;


/**
 * A strategy sticks with and utility threshold. If the last utility
 * is less than the threshold the next action will be sitting opposite
 * to a random opponent.
 * @author Dimitri Diomaiuta
 */
public class StickAndFollow implements Strategy{
  Utility utility;
  int numActions, myLastUtility, numberOfPlays, myAction;
  static final int UTILITY_THRESHOLD = 7;
  int[] opponentsLastAction;
  
  public StickAndFollow(Game g, long seed, String options) {
    this.numActions = g.getNumActions();
    Random r = new Random(seed);
    myAction = r.nextInt(numActions);
    this.utility = g.getUtility();
    myLastUtility = numberOfPlays = 0;
    opponentsLastAction = new int[2];
  }
  
  public int getAction() {
    if(numberOfPlays < 1) {
      numberOfPlays++;
    } else if(myLastUtility <= UTILITY_THRESHOLD) {
      // Take a random opponent and sit opposite to its previous spot.
      if(Math.random() < 0.5) {
        myAction = (opponentsLastAction[0] + (numActions / 2)) % numActions;
      } else {
        myAction = (opponentsLastAction[1] + (numActions / 2)) % numActions;
      }
    }
    return myAction;
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
    int currentAction = actions[0];
    opponentsLastAction[0] = actions[1];
    opponentsLastAction[1] = actions[2];
    int[] possibleUtilities = getPossibleUtilities(actions);
    myLastUtility = possibleUtilities[currentAction];
  }  
  
}
