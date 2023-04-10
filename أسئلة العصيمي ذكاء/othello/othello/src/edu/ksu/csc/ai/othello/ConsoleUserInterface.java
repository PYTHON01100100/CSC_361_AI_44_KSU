package edu.ksu.csc.ai.othello;

import java.util.*;
import java.io.*;

/**
 * A text-based user interface that allows for execution in a headless environment.
 *
 * @author <a href="http://www.sultanik.com" target="_blank">Evan A. Sultanik</a>
 */
public class ConsoleUserInterface implements UserInterface {
	private OthelloPlayer player1, player2;

	public void handleStateUpdate(GameState newState) {
		GameState previous = newState.getPreviousState();
		String player1name = player1.getName() + " (@)";
		String player2name = player2.getName() + " (O)";
		String currentPlayerName = (newState.getCurrentPlayer() == GameState.Player.PLAYER1 ? player1name : player2name);
		if(previous != null) {
			String previousPlayerName = (previous.getCurrentPlayer() == GameState.Player.PLAYER1 ? player1name : player2name);
			System.out.println(previousPlayerName + " moved to " + newState.getPreviousMove());
			System.out.println("");
			if(previous.getCurrentPlayer() == newState.getCurrentPlayer())
				System.out.println(previousPlayerName + " gets to move again!");
			else
				System.out.println("It is now " + currentPlayerName + "'s turn:");
		} else {
			System.out.println("\nIt is now " + currentPlayerName + "'s turn:");
		}
		System.out.println(newState.toString());
		OthelloPlayer currentPlayer = (newState.getCurrentPlayer() == GameState.Player.PLAYER1 ? player1 : player2);
		if(currentPlayer instanceof HumanOthelloPlayer && newState.getStatus() == GameState.GameStatus.PLAYING) {
			/* it's a human's turn! */
			Square move = null;
			while(move == null) {
				System.out.print("What is your move (example: \"a3\")? ");
				BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
				try {
					String moveString = in.readLine();
					move = new Square(moveString);
				} catch(IOException ioe) {
					ioe.printStackTrace();
					move = null;
				} catch(IllegalArgumentException iae) {
					System.err.println(iae.toString());
					move = null;
				}
			}
			((HumanOthelloPlayer)currentPlayer).handleUIInput(move);
		}
	}
	
	protected OthelloPlayer getPlayer1() {
		return player1;
	}
	
	protected OthelloPlayer getPlayer2() {
		return player2;
	}

	/**
	 * Prints all classes that are <code>instanceof</code>
	 * <code>OthelloPlayer</code> that have been found in the
	 * classpath and then calls <code>System.exit(1)</code>.
	 */
	public OthelloPlayer[] getPlayers() {
		Othello.printUsage();
		System.err.println("");
		System.err.println("Searching the classpath for possible agent classes...");
		Vector<String> possiblePlayers = new Vector<String>();
		for(Iterator<Class<? extends OthelloPlayer>> i = ClassCreator.getClassesOfType(OthelloPlayer.class).iterator(); i.hasNext();) {
			Class<? extends OthelloPlayer> c = i.next();
			if(!c.getName().equals("edu.ksu.csc.ai.othello.OthelloPlayer")) {
				possiblePlayers.add(c.getName());
			}
		}
		System.err.println("The following are the possible agent classes I found:");
		for(int i=0; i<possiblePlayers.size(); i++)
			System.err.println("    " + possiblePlayers.elementAt(i));
		System.err.println("\nPlease use the command line interface to select the agents.");
		System.exit(1);
		return new OthelloPlayer[0];
	}

	public void setPlayers(OthelloPlayer player1, OthelloPlayer player2) {
		this.player1 = player1;
		this.player2 = player2;
	}

	public void updateTimeRemaining(OthelloPlayer player, int secondsRemaining) {
	}

	public void updateTimeUsed(OthelloPlayer player, long millisUsed) {
		if(!(player instanceof HumanOthelloPlayer))
			System.out.println("Player " + player.getName() + " has used a total of " + millisUsed + "ms thinking thus far.");
	}
}
