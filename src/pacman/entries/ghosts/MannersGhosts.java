package pacman.entries.ghosts;

import java.util.EnumMap;
import java.util.Random;

import pacman.controllers.Controller;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

/*
 * This is the class you need to modify for your entry. In particular, you need to
 * fill in the getActions() method. Any additional classes you write should either
 * be placed in this package or sub-packages (e.g., game.entries.ghosts.mypackage).
 */
public class MannersGhosts extends Controller<EnumMap<GHOST,MOVE>>
{
	private EnumMap<GHOST, MOVE> myMoves=new EnumMap<GHOST, MOVE>(GHOST.class);
	public State currentState = ScatterState.instanceOf();
	public static Game game = null;
	static int internalGameTimer = 0;
	int prevLevel = 0;
	int prevLives = Integer.MAX_VALUE;
	boolean haveWeChangedStates = false;
	
	public EnumMap<GHOST, MOVE> getMove(Game incomingGame, long timeDue)
	{
		if(game == null) {
			game = incomingGame;
		}
		
		if((game.getCurrentLevel() > prevLevel) || (game.getPacmanNumberOfLivesRemaining() < prevLives)) { //Checks to see if we have increased levels or pacman lost a life
			internalGameTimer = 0;
			prevLives = game.getPacmanNumberOfLivesRemaining();
			prevLevel = game.getCurrentLevel();
		}
		
		if(currentState != FrightenedState.instanceOf()) { //increase the timer if we are not in the frightened state
			internalGameTimer++; //This value is incremented once every 40 milliseconds
		}
		
		haveWeChangedStates = false;
		
		for (GHOST ghost : GHOST.values()) {
			
			int t = (internalGameTimer * 40) / 1000; //converts internalGameTimer to milliseconds then to seconds
			
			//Controls the timing of levels
			if (game.getCurrentLevel() == 1) {
				/*
				Scatter for 7 seconds (0-7) , then Chase for 20 seconds (7 - 27).
				Scatter for 7 seconds (27-34), then Chase for 20 seconds(34-54).
				Scatter for 5 seconds (54-59), then Chase for 20 seconds (59-79).
				Scatter for 5 seconds (79-84), then switch to Chase mode permanently (>84).
				*/
				
				if ((t < 7) || (t > 27 && t <= 34) || (t > 54 && t <= 59) || (t > 79 && t <= 84)){
					changeState(ScatterState.instanceOf(), ghost);
				} else if ((t > 7 && t <= 27) || (t > 34 && t <= 54) || (t > 59 && t <= 79) || (t > 84)) {
					changeState(ChaseState.instanceOf(), ghost);
				}
				
			} else if (game.getCurrentLevel() >= 2 && game.getCurrentLevel() < 5) {
				/*
				Scatter for 7 seconds (0-7) , then Chase for 20 seconds (7 - 27).
				Scatter for 7 seconds (27-34), then Chase for 20 seconds(34-54).
				Scatter for 5 seconds (54-59), then Chase for 1033 seconds (59-1092).
				Scatter for 1/60 seconds (79-(1092 + 1/60)), then switch to Chase mode permanently (>(1092 + 1/6)).
				*/
			} else if (game.getCurrentLevel() >= 5) {
				/*
				Scatter for 5 seconds, then Chase for 20 seconds.
				Scatter for 5 seconds, then Chase for 20 seconds.
				Scatter for 5 seconds, then Chase for 1037 seconds .
				Scatter for 1/60 seconds, then switch to Chase mode permanently.
				*/
			}
			
			//Check to see if we need to enter frightened mode
			if(game.getGhostEdibleTime(ghost) > 0 && currentState != FrightenedState.instanceOf()) {
				changeState(FrightenedState.instanceOf(), ghost);
			} 
			
			//Check to see that if we have never changed states, then update
			if (!haveWeChangedStates){
				myMoves.put(ghost, currentState.update(ghost)); //else we are in the right state and should update
			}
			
		}
		
		return myMoves;
	}
	
	public static abstract class State {
		public abstract MOVE update(GHOST g);
		public abstract MOVE exit(GHOST g);
		public abstract MOVE enter(GHOST g);
	}
	
	private static class ChaseState extends State{
		
		private final static ChaseState instance = new ChaseState();
		public ChaseState() {};
		public static ChaseState instanceOf() {
			return instance;
		}

		@Override
		public MOVE update(GHOST g) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public MOVE exit(GHOST g) {
			return game.getGhostLastMoveMade(g).opposite();
		}

		@Override
		public MOVE enter(GHOST g) {
			// TODO Auto-generated method stub
			return null;
		}
		
	}
	
	private static class ScatterState extends State{

		private final static ScatterState instance = new ScatterState();
		public ScatterState() {};
		public static ScatterState instanceOf() {
			return instance;
		}
		
		
		@Override
		public MOVE update(GHOST g) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public MOVE exit(GHOST g) {
			return game.getGhostLastMoveMade(g).opposite();
		}

		@Override
		public MOVE enter(GHOST g) {
			// TODO Auto-generated method stub
			return null;
		}
		
	}
	
	private static class FrightenedState extends State{

		private final static FrightenedState instance = new FrightenedState();
		public FrightenedState() {};
		public static FrightenedState instanceOf() {
			return instance;
		}

		@Override
		public MOVE update(GHOST g) {
			int currIndex = game.getGhostCurrentNodeIndex(g);
			int[] junctions = game.getJunctionIndices();
			
			for (int i : junctions) { //Check to see if the ghost is at a junction
				if (currIndex == i) { //If so randomly return a possible move
					MOVE[] possibleMoves=game.getPossibleMoves(game.getGhostCurrentNodeIndex(g),game.getGhostLastMoveMade(g));
					return possibleMoves[(new Random()).nextInt(possibleMoves.length)];
				}
					
			}
			
			//else we are not at a junction, continue in our direction
			return game.getGhostLastMoveMade(g);
		}

		@Override
		public MOVE exit(GHOST g) {
			return null;
		}

		@Override
		public MOVE enter(GHOST g) {
			return null;
		}

		
	}
	
	public void changeState(State newState, GHOST g)
	{
		if(currentState == newState) {
			return;
		}
		currentState.exit(g);
		
		currentState = newState;
		
		currentState.enter(g);
		
		haveWeChangedStates = true;
	}
	
}
