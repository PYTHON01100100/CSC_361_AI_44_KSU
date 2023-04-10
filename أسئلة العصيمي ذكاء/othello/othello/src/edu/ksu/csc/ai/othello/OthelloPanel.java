package edu.ksu.csc.ai.othello;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import javax.swing.JPanel;

class OthelloPanel extends JPanel
implements MouseListener, MouseMotionListener {
	private static final long serialVersionUID = 1L;
	private GameState state;
	private Square highlight;
	private boolean mousePresent;
	private OthelloPlayer player1, player2;
	private static Font legendFont = new Font("Arial", Font.BOLD, 12);
	private static Color toolTipColor = new Color(255, 255, 255, 128);

	public OthelloPanel(GameState initialState, OthelloPlayer player1, OthelloPlayer player2) {
		state = initialState;
		highlight = new Square(0,0);
		mousePresent = false;
		this.player1 = player1;
		this.player2 = player2;
		addMouseListener(this);
		addMouseMotionListener(this);
		setPreferredSize(new Dimension(300, 300));
		setVisible(true);
	}

	public void updateState(GameState newState) {
		state = newState;
		repaint();
	}

	private boolean isHumansTurn() {
		return((state.getCurrentPlayer() == GameState.Player.PLAYER1 ? player1 : player2) instanceof HumanOthelloPlayer);
	}

	public void paint(Graphics graphics)
	{
		int height = getHeight();
		int width = getWidth();
		int square_width = (width - 7) / 8;
		int square_height = (height - 7) / 8;
		Image image = createImage(getWidth(), getHeight());
		Graphics g = image.getGraphics();
		int row, col;
		Color gray = new Color(128, 128, 128);
		Color trans_blue = new Color(200,200,200,128);

		((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		g.setColor(gray);
		g.fillRect(0, 0, width, height);

		/*
		 * Lets draw the grid:
		 */

		g.setColor(Color.BLUE);
		for(row=square_height+1; row<=height-square_height; row+=square_height+1)
			g.drawLine(0, row, width, row);
		for(col=square_width+1; col<=width-square_width; col+=square_width+1)
			g.drawLine(col, 0, col, height);

		if(state != null) {
			/*
			 * Now we draw the pieces:
			 */

			for(row=0; row<=7; row++) {
				for(col=0; col<=7; col++) {
					GameState.Player owner = state.getSquare(row, col);
					g.setColor((owner == GameState.Player.PLAYER1 ? Color.WHITE : Color.BLACK));
					if(owner != GameState.Player.EMPTY) {
						g.fillOval(col * square_width + col, row * square_height + row, square_width, square_height);
						g.setColor((owner == GameState.Player.PLAYER1 ? Color.BLUE : Color.GREEN));
						g.fillOval(col * square_width + col + square_width / 4, row * square_height + row + square_height / 4, square_width/2, square_height/2);
					}
				}
			}
		}

		if(isHumansTurn()) {   
			/*
			 * Now, highlight the valid moves:
			 */

			for(row=0; row<=7; row++) {
				for(col=0; col<=7; col++) {
					if(state.isLegalMove(new Square(row, col), state.getCurrentPlayer())) {
						g.setColor((state.getCurrentPlayer() == GameState.Player.PLAYER1 ? Color.BLUE : Color.GREEN));
						g.fillRect(col * square_width + col + 1, row * square_height + row + 1, square_width, square_height);
					}
				}
			}

			if(mousePresent) {
				g.setColor(trans_blue);
				g.fillRect(highlight.col * square_width + highlight.col, highlight.row * square_height + highlight.row, square_width + 1, square_height + 1);
			}
		}

		g.setFont(legendFont);

		for(row=0; row<=7; row++) {
			String legend = Integer.toString(row);
			int tWidth = g.getFontMetrics().stringWidth(legend) + 4;
			int tHeight = g.getFontMetrics().getHeight() + 4;
			int x = 1;
			int y = row * square_height + row + square_height/2 + 4;
			g.setColor(toolTipColor);
			g.fillRoundRect(x - tWidth / 2 + 4, y - tHeight + 4, tWidth, tHeight, 5, 5);
			g.setColor(Color.BLACK);
			g.drawString(legend, x, y);
		}
		for(col=0; col<=7; col++) {
			String legend = Square.colnames[col];
			int tWidth = g.getFontMetrics().stringWidth(legend) + 4;
			int tHeight = g.getFontMetrics().getHeight() + 4;
			int x = col*square_width + col + square_width/2;
			int y = 14;
			g.setColor(toolTipColor);
			g.fillRoundRect(x - tWidth / 2 + 4, y - tHeight + 4, tWidth, tHeight, 5, 5);
			g.setColor(Color.BLACK);
			g.drawString(legend, x, y);
		}

		graphics.drawImage(image, 0, 0, this);
	}

	Square last_coords;

	public void mouseMoved(MouseEvent e)
	{
		if(last_coords == null)
			last_coords = new Square(0, 0);

		highlight = mouseCoordsToRowCol(e);
		mousePresent = true;

		if(last_coords.row != highlight.row || last_coords.col != highlight.col)
			repaint();

		last_coords.row = highlight.row;
		last_coords.col = highlight.col;
	}

	public void mouseDragged(MouseEvent e) 
	{
	}

	private class MouseClickThread extends Thread
	{
		MouseEvent event;

		public MouseClickThread(MouseEvent e) 
		{
			super("MouseClickEventThread");
			event = e;
		}

		public void run()
		{
			if(isHumansTurn()) {
				HumanOthelloPlayer hop = (HumanOthelloPlayer)(state.getCurrentPlayer() == GameState.Player.PLAYER1 ? player1 : player2);
				hop.handleUIInput(mouseCoordsToRowCol(event));
			}
			mouseClickThread = null;
		}
	}

	MouseClickThread mouseClickThread;

	public void mouseClicked(MouseEvent e) 
	{
		if(isHumansTurn() && mouseClickThread == null) {
			mouseClickThread = new MouseClickThread(e);
			mouseClickThread.start();
		}
	}

	public void mouseEntered(MouseEvent e) 
	{
	}

	public void mouseExited(MouseEvent e) 
	{
		mousePresent = false;
	}

	public void mousePressed(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}

	private Square mouseCoordsToRowCol(MouseEvent e)
	{
		int x = e.getX();
		int y = e.getY();
		int height = getHeight();
		int width = getWidth();
		int square_width = (width - 7) / 8;
		int square_height = (height - 7) / 8;

		return new Square(y / (square_height + 1), x / (square_width + 1));
	}
}
