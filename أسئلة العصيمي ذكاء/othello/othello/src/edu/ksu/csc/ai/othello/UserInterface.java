package edu.ksu.csc.ai.othello;

/**
 * @author <a href="http://www.sultanik.com" target="_blank">Evan A. Sultanik</a>
 */
public interface UserInterface {
	/**
	 * Callback function for receiving updates to the state of the game.
	 */
	public void handleStateUpdate(GameState newState);

	/**
	 * Causes the user interface to prompt the user for the agents and
	 * names for the players and constructs/returns new instances of
	 * the prescribed players.
	 */
	public OthelloPlayer[] getPlayers();

	/**
	 * Assigns the agents that will be playing the current game.
	 */
	public void setPlayers(OthelloPlayer player1, OthelloPlayer player2);

	/**
	 * Callback function for updating the amount of time that remains
	 * before a player's deadline.
	 * @param secondsRemaining The amount of time in seconds before
	 * the end of <code>player</code>'s deadline.  A negative value
	 * indicates that the player has an infinite deadline.
	 */
	public void updateTimeRemaining(OthelloPlayer player, int secondsRemaining);

	/**
	 * Callback function for updating the total amount of time (in
	 * milliseconds) a player has used thus far in the game.
	 */
	public void updateTimeUsed(OthelloPlayer player, long millisUsed);
}
