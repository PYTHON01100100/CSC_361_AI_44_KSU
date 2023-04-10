package edu.ksu.csc.ai.othello;

import java.util.Date;

/**
 * An othello-playing agent that plays at random.
 *
 * @author <a href="http://www.sultanik.com" target="_blank">Evan A. Sultanik</a>
 */
public class RandomOthelloPlayer extends OthelloPlayer {
	/**
	 * Creates a new othello-playing agent that plays at random.
	 */
	public RandomOthelloPlayer(String name) {
		super(name);
	}

	/**
	 * Returns a random, valid move from <code>currentState</code>.
	 */
	public Square getMove(GameState currentState, Date deadline) {
		Square moves[] = currentState.getValidMoves().toArray(new Square[0]);
		int next = currentState.getRandom().nextInt(moves.length);
		log("Randomly moving to " + moves[next].toString() + "...");
		return moves[next];
	}
}
