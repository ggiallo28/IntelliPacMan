package DecisionTree;

import java.util.ArrayList;
import java.util.EnumMap;
import pacman.EvaluationHeuristic;
import pacman.controllers.Controller;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

/*
 * This is the class you need to modify for your entry. In particular, you need to
 * fill in the getAction() method. Any additional classes you write should either
 * be placed in this package or sub-packages (e.g., game.entries.pacman.mypackage).
 */
public class Greedy extends Controller<MOVE>
{
	static private int DEPTH = 7;
	EnumMap<GHOST, MOVE> ghostMoves = new EnumMap<GHOST, MOVE>(GHOST.class);

	public long dfsSearch(Node node, int life){
		Game gameState = node.getPredecessor().getGameState().copy();
		if (!isValidMove(gameState.getPossibleMoves(gameState.getPacmanCurrentNodeIndex()), node.getMove()))
			return Integer.MIN_VALUE;
		
		gameState.advanceGame(node.getMove(), ghostMoves);
		node.setGameState(gameState);
			
		ArrayList<Node> neighbors = node.getNeighbors();
		if (neighbors == null || node.getGameState().gameOver())// || node.getGameState().getPacmanNumberOfLivesRemaining() < life) 
			return EvaluationHeuristic.evaluateGameState(node.getGameState()); // end of branch return heuristic

		long bestValue = Integer.MIN_VALUE;
		for (Node neighbor : neighbors) {		
			long value = dfsSearch(neighbor,life);
			if (value > bestValue)
				bestValue = value;
		}
		return bestValue;	
	}
	
	boolean isValidMove(MOVE[] validMoves, MOVE move) {
		for (MOVE validMove : validMoves) {
			if (move == validMove) return true;
		}
		return false;
	}


	public MOVE getMove(Game game, long timeDue) 
	{
		this.ghostMoves.put(GHOST.BLINKY, game.getGhostLastMoveMade(GHOST.BLINKY));
		this.ghostMoves.put(GHOST.INKY, game.getGhostLastMoveMade(GHOST.INKY));
		this.ghostMoves.put(GHOST.PINKY, game.getGhostLastMoveMade(GHOST.PINKY));
		this.ghostMoves.put(GHOST.SUE, game.getGhostLastMoveMade(GHOST.SUE));

		Tree tree = new Tree(DEPTH);
		tree.getHeadNode().setGameState(game);
		ArrayList<Node> neighbors = tree.getHeadNode().getNeighbors();
		
		long leftValue = dfsSearch(neighbors.get(0),game.getPacmanNumberOfLivesRemaining());
		long rightValue = dfsSearch(neighbors.get(1),game.getPacmanNumberOfLivesRemaining());
		long upValue = dfsSearch(neighbors.get(2),game.getPacmanNumberOfLivesRemaining());
		long downValue = dfsSearch(neighbors.get(3),game.getPacmanNumberOfLivesRemaining());
		
		return EvaluationHeuristic.getBestMove(leftValue, rightValue, upValue, downValue);
	}
}