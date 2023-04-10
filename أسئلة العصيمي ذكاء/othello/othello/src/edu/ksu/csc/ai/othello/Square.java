package edu.ksu.csc.ai.othello;

/**
 * A class for representing an individual square in the othello board.
 * <p>For a description of the string representation of Othello board
 * squares, {@link #Square(String) see here}.</p>
 * @author <a href="http://www.sultanik.com" target="_blank">Evan A. Sultanik</a>
 */
public class Square {
	int row, col;
	static final String colnames[] = {"a","b","c","d","e","f","g","h"};

	/**
	 * Constructs a new <code>Square</code> from a string
	 * representation of the row and column.  Rows are numbered
	 * <code>0&ndash;7</code> from top to bottom and columns are
	 * labeled <code>a&ndash;h</code> from left to right.
<p><pre>
    a    b    c    d    e    f    g    h
  -----------------------------------------
0 | a0 | b0 | c0 | d0 | e0 | f0 | g0 | h0 |
  -----------------------------------------
1 | a1 | b1 | c1 | d1 | e1 | f1 | g1 | h1 |
  -----------------------------------------
2 | a2 | b2 | c2 | d2 | e2 | f2 | g2 | h2 |
  -----------------------------------------
3 | a3 | b3 | c3 | d3 | e3 | f3 | g3 | h3 |
  -----------------------------------------
4 | a4 | b4 | c4 | d4 | e4 | f4 | g4 | h4 |
  -----------------------------------------
5 | a5 | b5 | c5 | d5 | e5 | f5 | g5 | h5 |
  -----------------------------------------
6 | a6 | b6 | c6 | d6 | e6 | f6 | g6 | h6 |
  -----------------------------------------
7 | a7 | b7 | c7 | d7 | e7 | f7 | g7 | h7 |
  -----------------------------------------
</pre></p>
	 * <p>The row and column may occur in any order; "<code>c6</code>" is equivalent to "<code>6c</code>".</p>
	 * @throws IllegalArgumentException if a row and column could not be parsed from the string.
	 */
	public Square(String square) throws IllegalArgumentException {
		boolean error = false;
		if(square.length() != 2)
			error = true;
		else {
			try {
				square = square.toLowerCase();
				char c1 = square.charAt(0);
				char c2 = square.charAt(1);
				row = intValue(c1);
				if(row >= 0) {
					col = colIdxFromName(c2);
				} else {
					row = intValue(c2);
					col = colIdxFromName(c1);
				}
			} catch(Exception e) {
				error = true;
			}
			if(row < 0 || col < 0 || row >= 8 || col >= 8)
				error = true;
		}

		if(error)
			throw new IllegalArgumentException("Square definition \"" + square + "\" is not properly formatted.");
	}

	/**
	 * Constructs a new <code>Square</code> from a given row and
	 * column in the board.  Note that <em>no</em> checking is done to
	 * ensure that the given arguments are within the bounds of the
	 * standard 8<code>x</code>8 othello board.  Also note that the
	 * rows and columns are indexed from zero.
	 * @see #Square(String)
	 */
	public Square(int row, int col) {
		this.row = row;
		this.col = col;
	}

	int colIdxFromName(char c) {
		for(int i=0; i<colnames.length; i++) {
			if(colnames[i].equals(new String(new char[]{c})))
				return i;
		}
		return -1;
	}

	int intValue(char c) {
		int v = -1;
		try {
			v = Integer.parseInt(new String(new char[]{c}));
		} catch(NumberFormatException nfe) {
			v = -1;
		}
		return v;
	}

	/**
	 * Returns the row of this square.
	 */
	public int getRow() {
		return row;
	}


	/**
	 * Returns the column of this square.
	 */
	public int getCol() {
		return col;
	}

	/**
	 * Returns a string representation of this square.
	 * @see #Square(String)
	 */
	public String toString() {
		return colnames[col] + Integer.toString(row);
	}

	/**
	 * Returns <code>true</code> if and only if the given object is
	 * <code>instanceof</code> <code>Square</code> and if
	 * <code>o</code> points to the same row and column as
	 * <code>this</code>.
	 */
	public boolean equals(Object o) {
		if(!(o instanceof Square))
			return false;
		Square s = (Square)o;
		return (s.row == row && s.col == col);
	}

	/**
	 * Returns a unique idendentifier for this square.  It is ensured
	 * that <code>x.hashCode()==y.hashCode()</code> implies
	 * <code>x.equals(y)</code>.
	 *
	 * @see #equals(Object)
	 */
	public int hashCode() {
		return 9*row + col;
	}
}
