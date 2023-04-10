package edu.ksu.csc.ai.othello;

/**
 * Thrown to indicate that a {@link Square move} is not valid for the
 * given {@link GameState}.
 *
 * @author <a href="http://www.sultanik.com" target="_blank">Evan A. Sultanik</a>
 */
public class InvalidMoveException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	Square move;
	GameState.Player player;

	/**
	 * Constructs an <code>InvalidMoveException</code> with the
	 * offending move, the player that attempted the invalid move, and
	 * a message explaining why the move was invalid.
	 */
	public InvalidMoveException(Square move, GameState.Player player, String message) {
		super(message);
		this.move = move;
		this.player = player;
	}

	/**
	 * Returns the offending move.
	 */
	public Square getMove() { return move; }

	/**
	 * Returns the offending player.
	 */
	public GameState.Player getPlayer() { return player; }

	/**
	 * Returns a string representation of this exception.
	 */
	public String toString() {
		return (move != null ? move.toString() + ": " : "") + super.toString();
	}
}
