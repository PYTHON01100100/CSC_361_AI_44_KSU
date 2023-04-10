package edu.ksu.csc.ai.othello;

/**
 * An interface for logging events.
 *
 * @author <a href="http://www.sultanik.com" target="_blank">Evan A. Sultanik</a>
 */
public interface Logger {
	/**
	 * Logs a message from a given source.
	 */
	public void log(String message, Object source);
}
