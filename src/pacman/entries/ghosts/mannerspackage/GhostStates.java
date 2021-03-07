package pacman.entries.ghosts.mannerspackage;

import java.awt.Color;
import java.util.Random;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.entries.ghosts.MannersGhosts;
import pacman.game.GameView;
import pacman.game.internal.Node;

public class GhostStates {
	static boolean debug = true;
	
	//Not sure if there is a way to implement holding the ghosts back, there is no indication as to where the ghosts are timed to come out
	public static class ChaseState extends States<MannersGhosts>{
		
		private final static ChaseState instance = new ChaseState();
		public ChaseState() {};
		public static ChaseState instanceOf() {
			return instance;
		}

		@Override
		public void update(MannersGhosts m) {
			float t = (m.internalGameTimer * 40) / 1000;
			for (GHOST g : GHOST.values()) {
				if(m.game.getGhostEdibleTime(g) > 0) {//Check to see if we should be scared
					m.changeState(FrightenedState.instanceOf());
					return;
				}
				
				//Handle changing states based on time and level
				if(m.game.getCurrentLevel() == 1) {
					if ((t < 7) || (t > 27 && t <= 34) || (t > 54 && t <= 59) || (t > 79 && t <= 84)){
						m.changeState(ScatterState.instanceOf());
						return;
					}
				} else if (m.game.getCurrentLevel() >= 2 && m.game.getCurrentLevel() < 5) {
					if (( t < 7) || (t > 27 && t <= 34) || (t > 54 && t <= 59) || (t > 1092 && (t*1000 <= 1092016.66667))) { //Conversion to milliseconds for the last one for 1/60 second precision
						m.changeState(ScatterState.instanceOf());
						return;
					}
				} else { //game is above level 5
					if((t < 5) || (t > 25 && t <= 30) || (t > 50 && t <= 55) || (t > 1092 && (t*1000 <= 1092016.66667))) { //Conversion to milliseconds for the last one for 1/60 second precision
						m.changeState(ScatterState.instanceOf());
						return;
					}
				}
				
				//If we made it here we are chasing pac-man
				Node[] nodes = m.game.getCurrentMaze().graph;
				switch(g) {
				
				//The chase behavior for pinky and inky is kinda strange, because walls don't register on nodes. 
				//So you have to loop through all the nodes to find the target instead of just skipping over the wall
				//I wouldve liked if the wall nodes were there, they just had a tag that made them unwalkable. 
				case PINKY:
					
					int idx = m.game.getPacmanCurrentNodeIndex();
					int x = m.game.getNodeXCood(idx);
					int y = m.game.getNodeYCood(idx);
					int target = idx;
					//if(debug) System.out.println("I think pacman is moving " + m.game.getPacmanLastMoveMade());
					
					switch(m.game.getPacmanLastMoveMade()) { //get pacmans facing
					
					case UP:
						
						boolean foundEasyNode = false;
						for(Node n : nodes) {
							for (int q = 1; q <= 4; q++) {
								for (int j = 1; j <= 4; j++) {
									if (n.x == x+q && n.y == y+j) {
										target = n.nodeIndex;
										foundEasyNode = true;
									}
								}
							}
						}
						
						
						break;
						
					case DOWN:
						int r = idx;
						for(int i = 0; i < 4; i++) {
							if(m.game.getNeighbour(r, MOVE.DOWN) == -1){
								target = r;
								break;
							} else {
								r = m.game.getNeighbour(r, MOVE.DOWN);
							}
							target = r;							
						}
						break;

					case LEFT:
						int r1 = idx;
						for(int i = 0; i < 4; i++) {
							if(m.game.getNeighbour(r1, MOVE.LEFT) == -1){
								target = r1;
								break;
							} else {
								r1 = m.game.getNeighbour(r1, MOVE.LEFT);
							}
							target = r1;
							
						}
						break;

					case RIGHT:
						int r11 = idx;
						for(int i = 0; i < 4; i++) {
							if(m.game.getNeighbour(r11, MOVE.RIGHT) == -1){
								target = r11;
								break;
							} else {
								r11 = m.game.getNeighbour(r11, MOVE.RIGHT);
							}
							target = r11;						
						}
						break;
						
					case NEUTRAL:
						break;
					}
					
					m.myMoves.put(g, m.game.getApproximateNextMoveTowardsTarget(m.game.getGhostCurrentNodeIndex(g), target, m.game.getGhostLastMoveMade(g), DM.PATH));
					if(debug) {GameView.addPoints(m.game,Color.PINK,target);}
					break;
					
					
				case BLINKY:
					if(debug) {GameView.addPoints(m.game,Color.RED,m.game.getPacmanCurrentNodeIndex());}
					m.myMoves.put(g, m.game.getApproximateNextMoveTowardsTarget(m.game.getGhostCurrentNodeIndex(g), m.game.getPacmanCurrentNodeIndex(), m.game.getGhostLastMoveMade(g), DM.PATH));
					break;
					
					
				case INKY:
					int idx1 = m.game.getPacmanCurrentNodeIndex();
					int x1 = m.game.getNodeXCood(idx1);
					int y1 = m.game.getNodeYCood(idx1);
					int target1 = idx1;
					//if(debug) System.out.println("I think pacman is moving " + m.game.getPacmanLastMoveMade());
					
					switch(m.game.getPacmanLastMoveMade()) { //get pacmans facing
					
					case UP:
						
						boolean foundEasyNode = false;
						for(Node n : nodes) {
							for (int q = 1; q <= 2; q++) {
								for (int j = 1; j <= 2; j++) {
									if (n.x == x1+q && n.y == y1+j) {
										target1 = n.nodeIndex;
										foundEasyNode = true;
									}
								}
							}
						}
						
						break;
						
					case DOWN:
						int r = idx1;
						for(int i = 0; i < 2; i++) {
							if(m.game.getNeighbour(r, MOVE.DOWN) == -1){
								target1 = r;
								break;
							} else {
								r = m.game.getNeighbour(r, MOVE.DOWN);
							}
							target1 = r;							
						}
						break;

					case LEFT:
						int r1 = idx1;
						for(int i = 0; i < 2; i++) {
							if(m.game.getNeighbour(r1, MOVE.LEFT) == -1){
								target1 = r1;
								break;
							} else {
								r1 = m.game.getNeighbour(r1, MOVE.LEFT);
							}
							target1 = r1;
							
						}
						break;

					case RIGHT:

						int r11 = idx1;
						for(int i = 0; i < 2; i++) {
							if(m.game.getNeighbour(r11, MOVE.RIGHT) == -1){
								target1 = r11;
								break;
							} else {
								r11 = m.game.getNeighbour(r11, MOVE.RIGHT);
							}
							target1 = r11;						
						}
						break;
						
					case NEUTRAL:
						break;
					}
					
					
					//find node at from target
					//target is 2 spaces ahead
					//have to find vector doubled etc etc. 
					int blinkyNode = m.game.getGhostCurrentNodeIndex(GHOST.BLINKY);
					int d = (int) Math.sqrt(Math.pow(m.game.getNodeXCood(target1) - m.game.getNodeXCood(blinkyNode), 2) + Math.pow(m.game.getNodeYCood(target1) - m.game.getNodeYCood(blinkyNode), 2));
					
					
					for(Node n : nodes) {
						for(int i = 1; i <= d; i++) {
							if(n.x+i == n.y && n.y+i == n.y) {
								target1 = n.nodeIndex;
							}
						}
					}
					
					m.myMoves.put(g, m.game.getApproximateNextMoveTowardsTarget(m.game.getGhostCurrentNodeIndex(g), target1, m.game.getGhostLastMoveMade(g), DM.PATH));
					if(debug) {GameView.addPoints(m.game,Color.CYAN,target1);}
					break;
					
					
					
					
				case SUE:
					if(m.game.getDistance(m.game.getGhostCurrentNodeIndex(g), m.game.getPacmanCurrentNodeIndex(), DM.PATH) >= 8) {
						m.myMoves.put(g, m.game.getApproximateNextMoveTowardsTarget(m.game.getGhostCurrentNodeIndex(g), m.game.getPacmanCurrentNodeIndex(), m.game.getGhostLastMoveMade(g), DM.PATH));
						if(debug) {GameView.addPoints(m.game,Color.ORANGE,m.game.getPacmanCurrentNodeIndex());}
					} else {
						m.myMoves.put(g,m.game.getApproximateNextMoveTowardsTarget(m.game.getGhostCurrentNodeIndex(g), 1191, m.game.getGhostLastMoveMade(g), DM.PATH));
						if(debug) {GameView.addPoints(m.game,Color.ORANGE,1191);}
					}
					break;
				}
			}
		}

		@Override
		public void exit(MannersGhosts m) {
			if(debug) if(debug) System.out.println("Exited Chase");
			m.shouldReverse = true;
		}

		@Override
		public void enter(MannersGhosts m, States<MannersGhosts> a) {
			if(debug) if(debug) System.out.println("Entered Chase");
		}
		
	}
	
	public static class ScatterState extends States<MannersGhosts>{ //Some issues with frightening

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
		public void update(MannersGhosts m) {
			float t = (m.internalGameTimer * 40) / 1000;
			for (GHOST g : GHOST.values()) {
				//This is not how I should be checking if im scared I don't think?
				if(m.game.getGhostEdibleTime(g) > 0) { //Check to see if we should be scared
					m.changeState(FrightenedState.instanceOf());
					return;
				}
				
				//Handle changing state based on time and level
				if(m.game.getCurrentLevel() == 1) {
					if ((t > 7 && t <= 27) || (t > 34 && t <= 54) || (t > 59 && t <= 79) || (t > 84)) {
						m.changeState(ChaseState.instanceOf());
						return;
					}
				} else if (m.game.getCurrentLevel() >= 2 && m.game.getCurrentLevel() < 5) {
					if ((t > 7 && t <= 27) || (t > 34 && t <= 54) || (t > 59 || t <= 1092) || (t*1000 > 1092016.66667)) {
						m.changeState(ChaseState.instanceOf());
						return;
					}
				} else { //game is above level 5
					if ((t > 5 && t <= 25) || (t > 30 && t <= 50) || (t > 55 && t <= 1092) || (t*1000 > 1092016.66667)) {
						m.changeState(ChaseState.instanceOf());
						return;
					}
				}
				
				//Ghosts obv cannot target outside of the maze, so choose approximate nearest node for corners
				//If the pathfinding algorithm finds we are on top, it returns a neutral, which is a good substritute since in every 
				//case the ghost only has one direction to go. 
				//If we get here we are scattering
				switch(g) {
				case PINKY: //Upper Left Corner
					if(debug) {GameView.addPoints(m.game,Color.PINK,0);}
					m.myMoves.put(g,m.game.getApproximateNextMoveTowardsTarget(m.game.getGhostCurrentNodeIndex(g), 0, m.game.getGhostLastMoveMade(g), DM.PATH));
					break;
					
				case BLINKY:
					if(debug) {GameView.addPoints(m.game,Color.RED,78);}
					m.myMoves.put(g,m.game.getApproximateNextMoveTowardsTarget(m.game.getGhostCurrentNodeIndex(g), 78, m.game.getGhostLastMoveMade(g), DM.PATH));
					break;
					
				case INKY:
					if(debug) {GameView.addPoints(m.game,Color.CYAN,1291);}
					m.myMoves.put(g,m.game.getApproximateNextMoveTowardsTarget(m.game.getGhostCurrentNodeIndex(g), 1291, m.game.getGhostLastMoveMade(g), DM.PATH));
					break;
					
				case SUE:
					if(debug) {GameView.addPoints(m.game,Color.ORANGE,1191);}
					m.myMoves.put(g,m.game.getApproximateNextMoveTowardsTarget(m.game.getGhostCurrentNodeIndex(g), 1191, m.game.getGhostLastMoveMade(g), DM.PATH));
					break;
				}
			}
		}

		@Override
		public void exit(MannersGhosts m) {
			if(debug) System.out.println("Exited Scatter");
			m.shouldReverse = true;
		}

		@Override
		public void enter(MannersGhosts m, States<MannersGhosts> a) {
			if(debug) System.out.println("Entered Scatter");
			
		}
		
	}
	
	public static class FrightenedState extends States<MannersGhosts>{ //Tentatively Done?  

		private final static FrightenedState instance = new FrightenedState();
		public FrightenedState() {};
		private States<MannersGhosts> prevState;
		int clock = 0;
		public static FrightenedState instanceOf() {
			return instance;
		}

		@Override
		public void update(MannersGhosts m) {
			for(GHOST g : GHOST.values()) {
				
				if(m.game.getGhostEdibleTime(g) <= 0) {//Check to see if we should be scared
					m.changeState(prevState);
					return;
				}
				
				int currIndex = m.game.getGhostCurrentNodeIndex(g);
				int[] junctions = m.game.getJunctionIndices();
				
				for (int i : junctions) { //Check to see if the ghost is at a junction
					if (currIndex == i) { //If so randomly return a possible move
						MOVE[] possibleMoves=m.game.getPossibleMoves(m.game.getGhostCurrentNodeIndex(g),m.game.getGhostLastMoveMade(g));
						m.myMoves.put(g,possibleMoves[(new Random()).nextInt(possibleMoves.length)]);
					}
						
				}
			
			//else we are not at a junction, continue in our direction
			m.myMoves.put(g, m.game.getGhostLastMoveMade(g));
			}
		}

		@Override
		public void exit(MannersGhosts m){
			m.internalGameTimer = clock;
			if(debug) System.out.println("Exited Frightened");
		}

		@Override
		public void enter(MannersGhosts m, States<MannersGhosts> a) {
			clock = m.internalGameTimer;
			prevState = a;
			if(debug) System.out.println("Entered Frightened");
		}
	}
	
}
