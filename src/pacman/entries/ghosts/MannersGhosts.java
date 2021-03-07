package pacman.entries.ghosts;

import java.util.EnumMap;
import pacman.controllers.Controller;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;
import pacman.entries.ghosts.mannerspackage.States;
import pacman.entries.ghosts.mannerspackage.GhostStates.*;

/*
 * This is the class you need to modify for your entry. In particular, you need to
 * fill in the getActions() method. Any additional classes you write should either
 * be placed in this package or sub-packages (e.g., game.entries.ghosts.mypackage).
 */
public class MannersGhosts extends Controller<EnumMap<GHOST,MOVE>>
{
	public EnumMap<GHOST, MOVE> myMoves=new EnumMap<GHOST, MOVE>(GHOST.class);
	public static States<MannersGhosts> currentState = ScatterState.instanceOf();
	public Game game = null;
	public int internalGameTimer = 0;
	int prevLevel = 0;
	int prevLives = Integer.MAX_VALUE;
	static boolean haveWeChangedStates = false;
	public boolean shouldReverse = false;
	
	
	public EnumMap<GHOST, MOVE> getMove(Game incomingGame, long timeDue)
	{
		
		game = incomingGame;
		
		
		if((game.getCurrentLevel() > prevLevel) || (game.getPacmanNumberOfLivesRemaining() < prevLives)) { //Checks to see if we have increased levels or pacman lost a life
			internalGameTimer = 0;
			prevLives = game.getPacmanNumberOfLivesRemaining();
			prevLevel = game.getCurrentLevel();
		}
		
		if(currentState != FrightenedState.instanceOf()) { //increase the timer if we are not in the frightened state
			internalGameTimer++; //This value is incremented once every 40 milliseconds
		}
		
		haveWeChangedStates = false;
		
		currentState.update(this); //states need to make modifications to myMoves to return things
		if(shouldReverse) {
			for(GHOST g : GHOST.values()) {
				myMoves.put(g,  game.getGhostLastMoveMade(g).opposite());
			}
			shouldReverse = false;
		}
		
		return myMoves;
	}

	public void changeState(States<MannersGhosts> newState)
	{
		if(currentState == newState) {
			return;
		}
		currentState.exit(this);
		
		States<MannersGhosts> tmp = currentState;
		currentState = newState;
		
		currentState.enter(this, tmp);
		
		haveWeChangedStates = true;
	}
	
}
