package mystrategy;

import game.Game;
import game.Strategy;
import game.Utility;

import java.util.Random;
import java.util.ArrayList;
import java.lang.Math;


/**
 * Reimplementation of the EA2 strategy.
 * @author Dimitri Diomaiuta
 */
public class EA2 implements Strategy{
  int constantAction;
  Utility utility;
  int numActions;
  EA2Player playerI, playerJ;
  static final double smallGamma = 0.75;  // The response rate
  static final double smallP = 0.5;  // The scal parameter
  static final double tol = 0.1;  // The tollerance for the conditions to hold
  static final int initialStick = 6;
  static final int T = 2;
  int stickCounter;
  int numberOfPlays;  // The t parameter
  ArrayList<Integer> myHistory;
  double bigGamma;
  int currentUtility;
  int lastAction;

  public EA2(Game g, long seed, String options) {
    this.numActions = g.getNumActions();
    Random r = new Random(seed);
    lastAction = constantAction = r.nextInt(numActions);
    this.utility = g.getUtility();
    playerI = new EA2Player();
    playerJ = new EA2Player();
    stickCounter = initialStick;  // stick for the first 5 moves
    numberOfPlays = 0;
    myHistory = new ArrayList<Integer>();
    bigGamma = 0;
    currentUtility = 0;
  }
  
  public int getAction() {
    // Resetting initial stickCounter
    if(stickCounter > 0) {  // C0: stick
      stickCounter--;
      return lastAction;
    }
    // C1: Player i has higher stick index that its follow index and player j stick or follow index --> sit opposite player i
    if(playerI.stickIndex > playerI.followIndex - tol && (playerI.stickIndex > playerJ.stickIndex - tol || playerI.stickIndex > playerJ.followIndex - tol)) {
      return EA2Player.oppositeLocation(playerI.history.get(playerI.history.size()-1));
    }
    // C2: Both player i and j have high stick indices
    if(playerI.stickIndex > playerI.followIndex - tol && playerJ.stickIndex > playerJ.followIndex - tol) {
      // C2.1: utility > 8 --> Stick and set stickCount to T
      if(currentUtility > 8) {
        // We assume initialStick is T
        stickCounter = T;
        return lastAction;
      }
      // C2.2: utility <= 8 --> sit opposite the stickiest and set stickCount to T
      stickCounter = T;
      return playerI.stickIndex > playerJ.stickIndex ? EA2Player.oppositeLocation(playerI.history.get(playerI.history.size()-1)) :
          EA2Player.oppositeLocation(playerJ.history.get(playerJ.history.size()-1));
    }
    // C3: Player i has higher follow index than stick index and player j stick or follow index.
    if (playerI.followIndex > playerI.stickIndex - tol && (playerI.stickIndex > playerJ.stickIndex - tol || playerI.stickIndex > playerJ.followIndex - tol)) {
      // C3.1: player i is following me --> stick
      if(playerI.follow0 > playerI.followOther) {
        return lastAction;
      }
      // C3.2: player i is following j --> sit on j
      return playerJ.history.get(playerJ.history.size()-1);
    }
    // C4: Both player i and j have high follow indices and are following each other --> sit on the opponent with the highest follow index
    if(playerI.followIndex > playerI.stickIndex - tol && playerJ.followIndex > playerJ.stickIndex - tol
       && playerI.followOther > playerI.follow0 && playerJ.followOther > playerJ.follow0) {
      return playerI.followIndex > playerJ.followIndex ? playerI.history.get(playerI.history.size()-1) :
          playerJ.history.get(playerJ.history.size()-1);
    }
    // C5: Players i and j are sitting opposite to each other --> play carrot and stick --> sit on opponent with lower stick index
    if(EA2Player.oppositeLocation(playerI.history.get(playerI.history.size() - 1)) == EA2Player.oppositeLocation(playerJ.history.get(playerJ.history.size() - 1))) {
      return playerI.stickIndex < playerJ.stickIndex ? playerI.history.get(playerI.history.size()-1) :
          playerJ.history.get(playerJ.history.size()-1);
    }
    // C6: No condition satisfied
    return lastAction;
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
    this.bigGamma += Math.pow(smallGamma, numberOfPlays - 1);
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
    lastAction = actions[0];
    int[] possibleUtilities = getPossibleUtilities(actions);
    currentUtility = possibleUtilities[lastAction];
  }
    
}
