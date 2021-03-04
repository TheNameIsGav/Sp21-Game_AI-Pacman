package pacman.entries.ghosts;

import java.util.EnumMap;
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
	public State<GHOST> currentState = ScatterState.instanceOf();
	public Game currGame = null;
	
	public EnumMap<GHOST, MOVE> getMove(Game game, long timeDue)
	{
		if(currGame == null) {
			currGame = game;
		}
		
		for (GHOST ghost : GHOST.values()) {
			
			myMoves.put(ghost, currentState.update(ghost)); //performs the update behavior of the ghost?
			//should check to see if we need to update the position in the state?
		}
		
		return myMoves;
	}
	
	private static class ChaseState extends State<GHOST>{
		
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
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public MOVE enter(GHOST g) {
			// TODO Auto-generated method stub
			return null;
		}
		
		
	}
	
	private static class ScatterState extends State<GHOST>{

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
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public MOVE enter(GHOST g) {
			// TODO Auto-generated method stub
			return null;
		}
		
	}
	
	private static class FrightenedState extends State<GHOST>{

		private final static FrightenedState instance = new FrightenedState();
		public FrightenedState() {};
		public static FrightenedState instanceOf() {
			return instance;
		}
		
		@Override
		public MOVE update(GHOST g) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public MOVE exit(GHOST g) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public MOVE enter(GHOST g) {
			// TODO Auto-generated method stub
			return null;
		}

		
	}
	
	public void changeState(State<GHOST> newState, GHOST g)
	{

		currentState.exit(g);
		
		currentState = newState;
		
		currentState.enter(g);
	}
	
}
