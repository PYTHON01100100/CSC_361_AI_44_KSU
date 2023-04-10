package edu.ksu.csc.ai.othello;

import java.util.Date;

/**
 * This class provides the API for othello-playing agents.  Here is an
 * example of a simple random othello-playing agent (see {@link
 * RandomOthelloPlayer}):
 * <p><pre>
public class RandomOthelloPlayer extends OthelloPlayer {
    public RandomOthelloPlayer(String name) {
	super(name);
    }

    public Square getMove(GameState currentState, Date deadline) {
	Square moves[] = currentState.getValidMoves().toArray(new Square[0]);
	int next = currentState.getRandom().nextInt(moves.length);
	log("Randomly moving to " + moves[next].toString() + "...");
	return moves[next];
    }
}
</pre></p>
 * @author <a href="http://www.sultanik.com" target="_blank">Evan A. Sultanik</a>
 */
public abstract class OthelloPlayer {
	private String name;
	private Logger logger;
	private Date currentDeadline;
	private Square tempMove;
	private Thread currentThread;

	/**
	 * Creates a new Othello Player
	 */
	public OthelloPlayer(String name) {
		this.name = name;
		logger = null;
		currentDeadline = null;
		currentThread = null;
	}

	/**
	 * Returns the move chosen by this player given the current game
	 * state.  <code>deadline</code> is the time by which this
	 * function must return.  If <code>deadline</code> is
	 * <code>null</code>, there is no deadline.
	 */
	public abstract Square getMove(GameState currentState, Date deadline);

	/**
	 * Returns the name of this player.
	 */
	public String getName() {
		return name;
	}

	Square getMoveInternal(GameState currentState, Date deadline) {
		if(currentThread != null)
			throw new IllegalStateException("getMoveInternal(...) is already being called by another thread (" + currentThread + ")");
		currentDeadline = deadline;
		tempMove = null;
		currentThread = Thread.currentThread();
		Square move;
		try {
			move = getMove(currentState, deadline);
		} finally {
			currentThread = null;
			currentDeadline = null;
		}
		return move;
	}
	
	/**
	 * Register's the best move the agent has found so far in its search.  If the agent runs out of time, this is the move that will be used for the agent.  If no move is registered and the agent misses its deadline, then a move will be chosen at random.
	 * 
	 * @param bestMove The best move that the agent has found so far.
	 * @throws IllegalStateException if {@link #getMove(GameState, Date)} is not currently being run, or if it is being run from a different thread.
	 */
	protected final void registerCurrentBestMove(Square bestMove) throws IllegalStateException {
		if(currentThread == null)
			throw new IllegalStateException("This OthelloPlayer is not currently running getMove(...)!");
		else if(currentThread != Thread.currentThread())
			throw new IllegalStateException("registerCurrentBestMove(...) can only be called from the thread that is currently running getMove(...): " + currentThread);
		else if(this.getMillisUntilDeadline() >= 0)
			tempMove = bestMove; /* only set the move if the deadline hasn't yet expired */
	}
	
	/**
	 * Returns The best move that the agent has found so far, as registered using {@link #registerCurrentBestMove(Square)}.  If no move has been registered, or if {@link #getMove(GameState, Date)} is not currently running, then <code>null</code> is returned.
	 */
	protected final Square getCurrentBestMove() {
		return tempMove;
	}

	void setLogger(Logger logger) {
		this.logger = logger;
	}

	/**
	 * Utility function for returning the number of milliseconds remaining until the deadline.
	 */
	protected final long getMillisUntilDeadline() {
		if(currentDeadline == null)
			return 0;
		else
			return currentDeadline.getTime() - (new Date()).getTime();
	}

	/**
	 * Sends a log message to the user interface.
	 */
	protected void log(String message) {
		if(logger == null)
			System.out.println(name + ": " + message);
		else
			logger.log(message, this);
	}

	/**
	 * Returns a string representation of this player.
	 */
	public String toString() {
		return name;
	}
}
