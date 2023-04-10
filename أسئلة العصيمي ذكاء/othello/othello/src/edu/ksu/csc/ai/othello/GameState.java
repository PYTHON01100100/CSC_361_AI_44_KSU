package edu.ksu.csc.ai.othello;

import java.math.BigInteger;
import java.util.AbstractSet;
import java.util.HashSet;
import java.util.Random;


/**
 * A class for storing all aspects of the game state of Othello,
 * including the board state and current player.  This class also
 * includes utilities such as a successor function.
 *
 * @author <a href="http://www.sultanik.com" target="_blank">Evan A. Sultanik</a>
 */
public class GameState implements Cloneable {
	private Player player;
	private GameState previous;
	private Square move;
	private Random random;
	private HashSet<Square> validMoves1;
	private HashSet<Square> validMoves2;
	private int p1score; /* cache the scores after they're calculated for the first time */
	private int p2score;
	private BigInteger hash;

	/**
	 * An enumeration of the possible owners of a square in the game board.
	 *
	 * @author <a href="http://www.sultanik.com" target="_blank">Evan A. Sultanik</a>
	 */
	public enum Player {
		/**
		 * Player 1.
		 */
		PLAYER1,
		/**
		 * Player 2.
		 */
		PLAYER2, 
		/**
		 * Indicates that a square in the board is empty.
		 */
		EMPTY
	}

	enum Direction {UP, DOWN, LEFT, RIGHT, UPLEFT, UPRIGHT, DOWNLEFT, DOWNRIGHT}

	/**
	 * An enumeration of the possible states of the game.
	 *
	 * @author <a href="http://www.sultanik.com" target="_blank">Evan A. Sultanik</a>
	 */
	public enum GameStatus {
		/**
		 * The game is over and player 1 is the winner.
		 */
		PLAYER1WON,
		/**
		 * The game is over and player 2 is the winner.
		 */
		PLAYER2WON,
		/**
		 * The game is over and it is a tie.
		 */
		TIE,
		/**
		 * The game is not yet over.
		 */
		PLAYING}

	private Player board[][];

	/**
	 * Constructs a new <code>GameState</code> with the initial board
	 * configuration, a random initial player, and the random number
	 * generator seeded to a random value.
	 */
	public GameState() {
		random = new Random();
		init();
	}


	/**
	 * Creates a new GameState with the initial board configuration, a
	 * random initial player, and the random number generator seeded
	 * to the given value.
	 */
	public GameState(long randomNumberGeneratorSeed) {
		random = new Random(randomNumberGeneratorSeed);
		init();
	}

	private void init() {
		board = new Player[8][8];
		for(int i=0; i<8; i++)
			for(int j=0; j<8; j++)
				board[i][j] = Player.EMPTY;
		player = (random.nextInt(2) == 0 ? Player.PLAYER1 : Player.PLAYER2);
		if(player == Player.PLAYER2) {
			board[3][3] = Player.PLAYER1;
			board[3][4] = Player.PLAYER2;
			board[4][3] = Player.PLAYER2;
			board[4][4] = Player.PLAYER1;
		} else {
			board[3][3] = Player.PLAYER2;
			board[3][4] = Player.PLAYER1;
			board[4][3] = Player.PLAYER1;
			board[4][4] = Player.PLAYER2;
		}
		previous = null;
		move = null;
		validMoves1 = null;
		validMoves2 = null;
		p1score = -1;
		p2score = -1;
		hash = null;
	}

	/**
	 * Returns a copy of this GameState.  Note that this is a shallow
	 * copy; preceding states are not cloned.
	 */
	public Object clone() {
		GameState gs = new GameState();
		gs.board = new Player[8][8];
		for(int i=0; i<8; i++)
			for(int j=0; j<8; j++)
				gs.board[i][j] = board[i][j];
		gs.player = player;
		gs.previous = previous;
		gs.move = move;
		gs.random = random;
		gs.validMoves1 = null;
		gs.validMoves2 = null;
		gs.p1score = -1; /* force a recount of the scores */
		gs.p2score = -1;
		gs.hash = hash;
		return gs;
	}

	/**
	 * Returns the player whose turn it is to make a move.
	 */
	public Player getCurrentPlayer() {
		return player;
	}

	/**
	 * Returns the random number generator for this game.
	 */
	public Random getRandom() {
		return random;
	}

	/**
	 * Returns the opponent of a player.
	 */
	public Player getOpponent(Player player) {
		if(player == Player.PLAYER1)
			return Player.PLAYER2;
		else if(player == Player.PLAYER2)
			return Player.PLAYER1;
		else
			return player;
	}

	/**
	 * Returns the player that currently owns the given square.
	 */
	public Player getSquare(int row, int col) {
		if(row < 0 || row >= 8 || col < 0 || col >= 8)
			return null;
		else
			return board[row][col];
	}

	/**
	 * Returns the player that currently owns the given square.
	 */
	public Player getSquare(Square square) {
		return getSquare(square.row, square.col);
	}

	Square wouldFlip(Square move, Player player, Direction direction)
	{
		int row = move.row;
		int col = move.col;
		switch(direction) {
		case UP:
			for(row=move.row-1; row>=0; row--) {
				if(board[row][move.col] == getOpponent(player))
					continue;
				else if(board[row][move.col] == Player.EMPTY)
					return null;
				else if(board[row][move.col] == player) {
					if(row == move.row-1)
						return null;
					return new Square(row, move.col);
				}
			}
			return null;
		case DOWN:
			for(row=move.row+1; row<=7; row++) {
				if(board[row][move.col] == getOpponent(player))
					continue;
				else if(board[row][move.col] == Player.EMPTY)
					return null;
				else if(board[row][move.col] == player) {
					if(row == move.row+1)
						return null;
					return new Square(row, move.col);
				}
			}
			return null;
		case LEFT:
			for(col=move.col-1; col>=0; col--) {
				if(board[move.row][col] == getOpponent(player))
					continue;
				else if(board[move.row][col] == Player.EMPTY)
					return null;
				else if(board[move.row][col] == player) {
					if(col == move.col-1)
						return null;
					return new Square(move.row, col);
				}
			}
			return null;
		case RIGHT:
			for(col=move.col+1; col<=7; col++) {
				if(board[move.row][col] == getOpponent(player))
					continue;
				else if(board[move.row][col] == Player.EMPTY)
					return null;
				else if(board[move.row][col] == player) {
					if(col == move.col+1)
						return null;
					return new Square(move.row, col);
				}
			}
			return null;
		case UPLEFT:
			while(--row >= 0 && --col >= 0) {
				if(board[row][col] == getOpponent(player))
					continue;
				else if(board[row][col] == Player.EMPTY)
					return null;
				else if(board[row][col] == player) {
					if(row == move.row-1 && col == move.col-1)
						return null;
					return new Square(row, col);
				}
			}
			return null;
		case UPRIGHT:
			while(--row >= 0 && ++col <= 7) {
				if(board[row][col] == getOpponent(player))
					continue;
				else if(board[row][col] == Player.EMPTY)
					return null;
				else if(board[row][col] == player) {
					if(row == move.row-1 && col == move.col+1)
						return null;
					return new Square(row, col);
				}
			}
			return null;
		case DOWNLEFT:
			while(++row <= 7 && --col >= 0) {
				if(board[row][col] == getOpponent(player))
					continue;
				else if(board[row][col] == Player.EMPTY)
					return null;
				else if(board[row][col] == player) {
					if(row == move.row+1 && col == move.col-1)
						return null;
					return new Square(row, col);
				}
			}
			return null;
		case DOWNRIGHT:
			while(++row <= 7 && ++col <= 7) {
				if(board[row][col] == getOpponent(player))
					continue;
				else if(board[row][col] == Player.EMPTY)
					return null;
				else if(board[row][col] == player) {
					if(row == move.row+1 && col == move.col+1)
						return null;
					return new Square(row, col);
				}
			}
			return null;
		default:
			return null;
		}
	}

	/**
	 * Returns <code>true</code> if and only if <code>move</code> is
	 * legal for <code>player</code>.
	 */
	public boolean isLegalMove(Square move, Player player) {
		int row = move.row;
		int col = move.col;
		if(board[row][col] != Player.EMPTY)
			return false;
		for(Direction d : Direction.values()) {
			if(wouldFlip(move, player, d) != null)
				return true;
		}
		return false;
	}

	/**
	 * Returns all valid Moves that may be taken from this state.
	 */
	public AbstractSet<Square> getValidMoves() {
		return getValidMoves(getCurrentPlayer());
	}

	/**
	 * Returns all valid Moves that may be taken by
	 * <code>player</code> from this state.
	 */
	public AbstractSet<Square> getValidMoves(Player player) {
		HashSet<Square> moves = (player == Player.PLAYER1 ? validMoves1 : validMoves2);
		if(moves != null)
			return moves;
		moves = new HashSet<Square>();
		for(int i=0; i<8; i++) {
			for(int j=0; j<8; j++) {
				Square m = new Square(i,j);
				if(isLegalMove(m, player))
					moves.add(m);
			}
		}
		if(player == Player.PLAYER1)
			validMoves1 = moves;
		else
			validMoves2 = moves;
		return moves;
	}

	/**
	 * Returns the number of spaces currently owned by the given
	 * player.
	 */
	public int getScore(Player player) {
		if(player == Player.PLAYER1 && p1score >= 0)
			return p1score;
		else if(player == Player.PLAYER2 && p2score >= 0)
			return p2score;
		int count = 0;
		for(int i=0; i<8; i++) {
			for(int j=0; j<8; j++) {
				if(board[i][j] == player)
					count++;
			}
		}
		if(player == Player.PLAYER1)
			p1score = count;
		else if(player == Player.PLAYER2)
			p2score = count;
		return count;
	}

	/**
	 * Returns the winner of the game or <code>null</code> if the game
	 * was either a tie or the game has not yet finished.
	 */
	public Player getWinner() {
		switch(getStatus()) {
		case PLAYER1WON:
			return Player.PLAYER1;
		case PLAYER2WON:
			return Player.PLAYER2;
		default:
			return null;
		}
	}

	/**
	 * Returns the current status of the game.
	 */
	public GameStatus getStatus() {
		if(getValidMoves(Player.PLAYER1).size() <= 0 &&
				getValidMoves(Player.PLAYER2).size() <= 0) {
			int p1score = getScore(Player.PLAYER1);
			int p2score = getScore(Player.PLAYER2);
			if(p1score > p2score)
				return GameStatus.PLAYER1WON;
			else if(p1score < p2score)
				return GameStatus.PLAYER2WON;
			else
				return GameStatus.TIE;
		} else {
			return GameStatus.PLAYING;
		}
	}

	/**
	 * Equivalent to {@link #getSuccessors(boolean) getSuccessors(true)}.
	 *
	 * @see #getSuccessors(boolean)
	 */
	public AbstractSet<GameState> getSuccessors() {
		return getSuccessors(true);
	}

	/**
	 * Returns all valid GameStates that may succeed this state.
	 *
	 * <p>Note that if <code>includePreviousStateReference</code> is
	 * <code>false</code>, the returned states will return
	 * <code>null</code> when {@link #getPreviousState()} is called.
	 * This is useful to reduce memory if the back-references are not
	 * required.</p>
	 *
	 * @param includePreviousStateReference whether or not the returned states should have back-references to <code>this</code>.
	 * @see #applyMove(Square, boolean)
	 */
	public AbstractSet<GameState> getSuccessors(boolean includePreviousStateReference) {
		Square moves[] = getValidMoves().toArray(new Square[0]);
		HashSet<GameState> successors = new HashSet<GameState>(moves.length);
		for(int i=0; i<moves.length; i++) {
			try {
				successors.add(applyMove(moves[i], includePreviousStateReference));
			} catch(InvalidMoveException ime) {
				/* This should not happen! */
				System.err.println(ime.toString());
			}
		}
		return successors;
	}

	/**
	 * Equivalent to {@link #applyMove(Square,boolean) applyMove(move, true)}.
	 *
	 * @see #applyMove(Square,boolean)
	 */
	public GameState applyMove(Square move) throws InvalidMoveException {
		return applyMove(move, true);
	}

	/**
	 * Returns the GameState resulting from applying the given move to
	 * this state.
	 *
	 * <p><code>applyMove</code> <b>does not</b> apply the given move
	 * to the current state; it does not in any way alter
	 * <code>this</code>.  Instead, <code>applyMove</code>
	 * <em>returns</em> the state that <em>results</em> from making
	 * the given move in the current state.</p>
	 *
	 * <p>Note that if <code>includePreviousStateReference</code> is
	 * <code>false</code>, the returned state will return
	 * <code>null</code> when {@link #getPreviousState()} is called.
	 * This is useful to save memory if the back-references to
	 * previous states are not required (<i>i.e.</i> the previous
	 * states may be garbage collected).</p>
	 *
	 * @param includePreviousStateReference whether or not the returned state should have a back-reference to <code>this</code>.
	 * @throws InvalidMoveException if <code>move</code> is not a valid move from this state.
	 */
	public GameState applyMove(Square move, boolean includePreviousStateReference) throws InvalidMoveException {
		Square bracket;
		boolean found_good_direction = false;
		int row;
		int col;
		GameState newState = (GameState)clone();
		Player player = getCurrentPlayer();
		newState.previous = (includePreviousStateReference ? this : null);
		newState.move = move;

		if(move == null) {
			throw new InvalidMoveException(move, getCurrentPlayer(), "The move sent to GameState.applyMove() was null!");
		}

		if(board[move.row][move.col] != Player.EMPTY)
			throw new InvalidMoveException(move, getCurrentPlayer(), "The space is not empty!");

		bracket = wouldFlip(move, player, Direction.UP);
		if(bracket != null) {
			found_good_direction = true;
			row = move.row;
			col = move.col;
			while(--row != bracket.row)
				newState.board[row][col] = player;
		}

		bracket = wouldFlip(move, player, Direction.DOWN);
		if(bracket != null) {
			found_good_direction = true;
			row = move.row;
			col = move.col;
			while(++row != bracket.row)
				newState.board[row][col] = player;
		}

		bracket = wouldFlip(move, player, Direction.LEFT);
		if(bracket != null) {
			found_good_direction = true;
			row = move.row;
			col = move.col;
			while(--col != bracket.col)
				newState.board[row][col] = player;
		}

		bracket = wouldFlip(move, player, Direction.RIGHT);
		if(bracket != null) {
			found_good_direction = true;
			row = move.row;
			col = move.col;
			while(++col != bracket.col)
				newState.board[row][col] = player;
		}

		bracket = wouldFlip(move, player, Direction.UPLEFT);
		if(bracket != null) {
			found_good_direction = true;
			row = move.row;
			col = move.col;
			while(--row != bracket.row && --col != bracket.col)
				newState.board[row][col] = player;
		}

		bracket = wouldFlip(move, player, Direction.UPRIGHT);
		if(bracket != null) {
			found_good_direction = true;
			row = move.row;
			col = move.col;
			while(--row != bracket.row && ++col != bracket.col)
				newState.board[row][col] = player;
		}

		bracket = wouldFlip(move, player, Direction.DOWNLEFT);
		if(bracket != null) {
			found_good_direction = true;
			row = move.row;
			col = move.col;
			while(++row != bracket.row && --col != bracket.col)
				newState.board[row][col] = player;
		}

		bracket = wouldFlip(move, player, Direction.DOWNRIGHT);
		if(bracket != null) {
			found_good_direction = true;
			row = move.row;
			col = move.col;
			while(++row != bracket.row && ++col != bracket.col)
				newState.board[row][col] = player;
		}

		if(found_good_direction)
			newState.board[move.row][move.col] = player;
		else
			throw new InvalidMoveException(move, player, "This move does not flip any of the opponents' pieces!");

		newState.player = getOpponent(player);

		if(newState.getValidMoves().size() <= 0)
			/* the other player has no valid moves, so their turn is skipped */
			newState.player = player;

		return newState;
	}

	/**
	 * Returns the previous state (or <code>null</code> if this is the
	 * initial state).  This function may also return
	 * <code>null</code> if <code>this</code> was created without a
	 * back-reference to the previous state (to save memory).
	 *
	 * @see #applyMove(Square, boolean)
	 */
	public GameState getPreviousState() {
		return previous;
	}

	/**
	 * Returns the previous move that was used to get to this state
	 * (or <code>null</code> if this is the initial state).
	 */
	public Square getPreviousMove() {
		return move;
	}

	/**
	 * Returns a string representation of the game board.
	 * <p>Example:
<pre>
    a b c d e f g h   [@=2 O=5]
  0 . . . . . . . .
  1 . . . . . . . .
  2 . . . O . . . .
  3 . . . O O . . .
  4 . . @ O @ . . .
  5 . . O . . . . .
  6 . . . . . . . .
  7 . . . . . . . .
</pre></p>
	 */
	public String toString() {
		String s = "    a b c d e f g h   [@=" + getScore(Player.PLAYER1) + " O=" + getScore(Player.PLAYER2) + "]\n";
		for(int i=0; i<8; i++) {
			s += "  " + i;
			for(int j=0; j<8; j++) {
				Player p = getSquare(i, j);
				s += " " + (p == Player.PLAYER1 ? "@" : (p == Player.PLAYER2 ? "O" : "."));
			}
			if(i < 7)
				s += "\n";
		}
		return s;
	}

	/**
	 * Returns whether or not <code>o</code> is equivalent to this
	 * GameState.
	 *
	 * @see #uniqueHashCode()
	 * @see #hashCode()
	 */
	public boolean equals(Object o) {
		if(!(o instanceof GameState))
			return false;
		GameState gs = (GameState)o;
		if(gs.player != player)
			return false;
		for(int i=0; i<8; i++) {
			for(int j=0; j<8; j++) {
				if(board[i][j] != gs.board[i][j])
					return false;
			}
		}
		return true;
	}

	private static BigInteger multiplier[] = null;
	private static BigInteger two = new BigInteger("2");

	/**
	 * Returns a unique number identifying this GameState.
	 *
	 * <p>
	 * <table>
	 * <tr><td colspan="4" align="left">The following are always true:</td></tr>
	 * <tr><td><ul type="disc"><li></li></ul></td><td align="right"><code>x.uniqueHashCode() == y.uniqueHashCode()</code></td><td>&rArr;</td><td><code>x.equals(y)</code></td></tr>
	 * <tr><td><ul type="disc"><li></li></ul></td><td align="right"><code>x.equals(y)</code></td><td>&rArr;</td><td><code>x.uniqueHashCode() == y.uniqueHashCode()</code></td></tr>
	 * <tr><td><ul type="disc"><li></li></ul></td><td align="right"><code>x.equals(y)</code></td><td>&rArr;</td><td><code>x.hashCode() == y.hashCode()</code></td></tr>
	 * <tr><td colspan="4" align="left"><em>However</em>, the following are <em>not necessarily</em> true:</td></tr>
	 * <tr><td><ul type="disc"><li></li></ul></td><td align="right"><code>x.hashCode() == y.hashCode()</code></td><td>&rArr;</td><td><code>x.equals(y)</code></td></tr>
	 * </table></p>
	 *
	 * @see #equals(Object)
	 * @see #hashCode()
	 */
	public BigInteger uniqueHashCode() {
		if(hash == null) {
			hash = (player == Player.PLAYER1 ? BigInteger.ZERO : BigInteger.ONE);
			int i, j, idx = 0;
			if(multiplier == null) {
				BigInteger three = new BigInteger("3");
				multiplier = new BigInteger[64];
				multiplier[0] = three;
				for(i=1; i<64; i++)
					multiplier[i] = multiplier[i-1].multiply(three);
			}
			for(i=0; i<8; i++) {
				for(j=0; j<8; j++) {
					Player p = getSquare(i, j);
					if(p == Player.EMPTY)
						idx++;
					else
						hash.add(multiplier[idx++].multiply(p == Player.PLAYER1 ? BigInteger.ONE : two));
				}
			}
		}
		return hash;
	}

	/**
	 * Equivalent to calling {@link java.lang.Object#hashCode() hashCode()} on the result of {@link #uniqueHashCode() uniqueHashCode()}.
	 *
	 * @see #equals(Object)
	 * @see #hashCode()
	 */
	public int hashCode() {
		return uniqueHashCode().hashCode();
	}

	public static void main(String[] args) {
		GameState gs = new GameState();
		System.out.println(gs);
		System.out.println("Successors:");
		GameState succ[] = gs.getSuccessors().toArray(new GameState[0]);
		for(int i=0; i<succ.length; i++)
			System.out.println(succ[i]);
	}
}
