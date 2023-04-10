package students.example;

import java.util.Date;

import edu.ksu.csc.ai.othello.GameState;
import edu.ksu.csc.ai.othello.OthelloPlayer;
import edu.ksu.csc.ai.othello.Square;

/**
 * An example of how students should implement their own agents.
 *
 * @author <a href="http://www.sultanik.com" target="_blank">Evan A. Sultanik</a>
 */
public class AOthelloPlayer extends OthelloPlayer {
	/**
	 * Creates a new <code>ExampleOthelloPlayer</code>.
	 */
	public AOthelloPlayer(String name) {
		super(name);
	}

	/**
	 * Returns the first move that the agent discovers is valid.
	 */
	public Square getMove(GameState currentState, Date deadline) {
		Square square = currentState.getValidMoves().toArray(new Square[0])[0];
		/* register this as our current best move; if there is a deadline and we don't reach it,
		 * then registering the move will make sure that that is the move we take.
		 * If we reach the deadline and we neither registered a move nor returned from this
		 * function, then a move will be chosen for us at random. */
		this.registerCurrentBestMove(square);
		
		if(deadline != null)
			log("I have " + this.getMillisUntilDeadline() + "ms remaining until the deadline.");
		
		/* registerCurrentBestMove(...) can be called multiple times to reset the current best
		 * move before returning from this function. */
		
		/* return the move that we have chosen */
		log("Example player is moving to " + square + "...");
		return square;
	}
}
