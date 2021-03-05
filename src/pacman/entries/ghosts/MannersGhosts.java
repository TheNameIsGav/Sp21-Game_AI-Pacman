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
	private static EnumMap<GHOST, MOVE> myMoves=new EnumMap<GHOST, MOVE>(GHOST.class);
	public static State currentState = ScatterState.instanceOf();
	public static Game game = null;
	static int internalGameTimer = 0;
	int prevLevel = 0;
	int prevLives = Integer.MAX_VALUE;
	static boolean haveWeChangedStates = false;
	
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
		
		currentState.update(); //states need to make modifications to myMoves to return things
		
		return myMoves;
	}
	
	public static abstract class State {
		public abstract void update();
		public abstract void exit();
		public abstract void enter();
	}
	
	private static class ChaseState extends State{
		
		private final static ChaseState instance = new ChaseState();
		public ChaseState() {};
		public static ChaseState instanceOf() {
			return instance;
		}

		@Override
		public void update() {
			float t = (internalGameTimer * 40) / 1000;
			for (GHOST g : GHOST.values()) {
				if(game.getGhostEdibleTime(g) > 0) {//Check to see if we should be scared
					changeState(FrightenedState.instanceOf());
					break;
				}
				
				//Handle changing states based on time and level
				if(game.getCurrentLevel() == 1) {
					if ((t < 7) || (t > 27 && t <= 34) || (t > 54 && t <= 59) || (t > 79 && t <= 84)){
						changeState(ScatterState.instanceOf());
						break;
					}
				} else if (game.getCurrentLevel() >= 2 && game.getCurrentLevel() < 5) {
					if (( t < 7) || (t > 27 && t <= 34) || (t > 54 && t <= 59) || (t > 1092 && (t*1000 <= 1092016.66667))) { //Conversion to milliseconds for the last one for 1/60 second precision
						changeState(ScatterState.instanceOf());
						break;
					}
				} else { //game is above level 5
					if((t < 5) || (t > 25 && t <= 30) || (t > 50 && t <= 55) || (t > 1092 && (t*1000 <= 1092016.66667))) { //Conversion to milliseconds for the last one for 1/60 second precision
						changeState(ScatterState.instanceOf());
						break;
					}
				}
				
				//If we made it here we are chasing pac-man
				switch(g) {
				case PINKY:
					break;
				case BLINKY:
					break;
				case INKY:
					break;
				case SUE:
					break;
				}
			}
		}

		@Override
		public void exit() {
			for (GHOST g : GHOST.values()) {
				myMoves.put(g, game.getGhostLastMoveMade(g).opposite());
			}
		}

		@Override
		public void enter() {
			// TODO Auto-generated method stub
		}
		
	}
	
	private static class ScatterState extends State{

		private final static ScatterState instance = new ScatterState();
		public ScatterState() {};
		public static ScatterState instanceOf() {
			return instance;
		}
		//Lower Right Corner: 1291
		//Lower Left Corner: 1191
		//Upper left Corner: 1
		//Upper Right Corner: 78
		@Override
		public void update() {
			float t = (internalGameTimer * 40) / 1000;
			for (GHOST g : GHOST.values()) {
				if(game.getGhostEdibleTime(g) > 0) { //Check to see if we should be scared
					changeState(FrightenedState.instanceOf());
					break;
				}
				
				//Handle changing state based on time and level
				if(game.getCurrentLevel() == 1) {
					if ((t > 7 && t <= 27) || (t > 34 && t <= 54) || (t > 59 && t <= 79) || (t > 84)) {
						changeState(ChaseState.instanceOf());
						break;
					}
				} else if (game.getCurrentLevel() >= 2 && game.getCurrentLevel() < 5) {
					if ((t > 7 && t <= 27) || (t > 34 && t <= 54) || (t > 59 || t <= 1092) || (t*1000 > 1092016.66667)) {
						changeState(ChaseState.instanceOf());
						break;
					}
				} else { //game is above level 5
					if ((t > 5 && t <= 25) || (t > 30 && t <= 50) || (t > 55 && t <= 1092) || (t*1000 > 1092016.66667)) {
						changeState(ChaseState.instanceOf());
						break;
					}
				}
				
				//If we get here we are scattering
				switch(g) {
				case PINKY:
					break;
				case BLINKY:
					break;
				case INKY:
					break;
				case SUE:
					break;
				}
			}
		}

		@Override
		public void exit() {
			for(GHOST g : GHOST.values()) {
				myMoves.put(g,game.getGhostLastMoveMade(g).opposite());
			}
		}

		@Override
		public void enter() {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	private static class FrightenedState extends State{

		private final static FrightenedState instance = new FrightenedState();
		public FrightenedState() {};
		int clock = 0;
		public static FrightenedState instanceOf() {
			return instance;
		}

		@Override
		public void update() {
			for(GHOST g : GHOST.values()) {
				int currIndex = game.getGhostCurrentNodeIndex(g);
				int[] junctions = game.getJunctionIndices();
				
				for (int i : junctions) { //Check to see if the ghost is at a junction
					if (currIndex == i) { //If so randomly return a possible move
						MOVE[] possibleMoves=game.getPossibleMoves(game.getGhostCurrentNodeIndex(g),game.getGhostLastMoveMade(g));
						myMoves.put(g,possibleMoves[(new Random()).nextInt(possibleMoves.length)]);
					}
						
				}
			
			//else we are not at a junction, continue in our direction
			myMoves.put(g, game.getGhostLastMoveMade(g));
			}
		}

		@Override
		public void exit(){
			internalGameTimer = clock;
		}

		@Override
		public void enter() {
			clock = internalGameTimer;
		}
	}
	
	public static void changeState(State newState)
	{
		if(currentState == newState) {
			return;
		}
		currentState.exit();
		
		currentState = newState;
		
		currentState.enter();
		
		haveWeChangedStates = true;
	}
	
}
