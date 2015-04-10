package org.gamesolver;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Deque;
import java.util.Stack;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;


public class ReversiPane extends JPanel implements MouseListener,KeyListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5401699366366851211L;

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}

	static JFrame frame;
	private static void createAndShowGUI() {
		JPanel pane = new ReversiPane();
		frame = new JFrame();
		frame.setTitle("Reversi");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.add(pane);
		frame.pack();
		frame.setVisible(true);
		pane.addMouseListener((MouseListener) pane);
		frame.addKeyListener((KeyListener) pane);
	}

	static final int DEPTH = 7;

	ReversiState game;
	//static final int width = 500;
	//static final int height = 500;
	int player;
	int computer;
	int minimax[];
	Stack<ReversiState> history;

	public ReversiPane() {
		game = new ReversiState();
		player = 1;
		computer = 1;
		minimax = game.minimax(getDepth(), player, -1);
		history = new Stack<>();
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(500, 500);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		this.setBackground(new Color(163, 183, 255));

		drawGrid(g);
		drawBoard(g);
		drawMoves(g);
		drawBest(g);
	}

	private void drawBest(Graphics g) {
		if (player == computer) {
			int width = this.getWidth() / game.cols;
			int height = this.getHeight() / game.rows;
			int row = minimax[1] / 8;
			int col = minimax[1] % 8;
			g.setColor(new Color(195, 255, 210));
			g.fillOval(col * width + 1, row * height + 1, width - 2, height - 2);
		}
	}

	private void drawMoves(Graphics g) {
		int width = this.getWidth() / game.cols;
		int height = this.getHeight() / game.rows;
		Deque<int[]> moveList = game.getMoves(player);
		for (int[] pos : moveList) {
			g.setColor(new Color(200,200,200));
			g.drawOval(pos[1] * width + 1, pos[0] * height + 1, width - 2, height - 2);
		}
	}

	private void drawBoard(Graphics g) {
		int width = this.getWidth() / game.cols;
		int height = this.getHeight() / game.rows;
		for (int i = 0; i < game.rows; i++)
			for (int j = 0; j < game.cols; j++) {
				if (game.board[i * game.cols + j] > 0) {
					g.setColor(new Color(0, 0, 0));
					g.fillOval(j * width + 1, i * height + 1, width - 2, height - 2);
				} else if (game.board[i * game.cols + j] < 0) {
					g.setColor(new Color(255, 255, 255));
					g.fillOval(j * width + 1, i * height + 1, width - 2, height - 2);
				}
			}
	}

	private void drawGrid(Graphics g) {
		g.setColor(new Color(0,0,0));
		for (int i = 1; i < game.rows; i++) { //draw rows
			int pos = this.getHeight() / game.rows * i;
			g.drawLine(0, pos, this.getWidth(), pos);
		}
		for (int j = 1; j < game.cols; j++) { //draw cols
			int pos = this.getWidth() / game.cols * j;
			g.drawLine(pos, 0, pos, this.getHeight());
		}
	}



	@Override
	public void mouseClicked(MouseEvent e) {
		int width = this.getWidth() / game.cols;
		int height = this.getHeight() / game.rows;
		int col = e.getX() / width;
		int row = e.getY() / height;

		System.out.println("Clicked " + row + ", " + col);
		history.push(game);
		game = new ReversiState(game);
		if (game.playMove(player, new int[]{row, col})) {
			Graphics g = this.getGraphics();
			drawBoard(g);
			
			player = -player;
			String turn = (player > 0 ? "Black" : "White");
			frame.setTitle(turn + "'s turn");
			
			if (game.getMoves(player).size() == 0) {
				player = -player;
			}
			
			if (player == computer)
				minimax = game.minimax(getDepth(), player, -1);
			else
				minimax[1] = -1;
		} else {
			history.pop();
		}


		this.repaint();
	}
	
	int getDepth() {
		return 8;
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		char keypress = e.getKeyChar();
		switch (keypress) {
		case 'r':
			game.rotateClockwise();
			break;
		case 'l':
			game.rotateCounterClockwise();
			break;
		case '1':
			computer = 1;
			break;
		case '2':
			computer = -1;
			break;
		case 'u':
			if (!history.isEmpty()) {
				game = history.pop();
				player = -player;
			}
			break;
		case 'n':
			player = 1;
			computer = 1;
			game = new ReversiState();
			history = new Stack<>();
			this.repaint();
			break;
		}
		if (player == computer) {
			minimax = game.minimax(getDepth(), player, -1);
		}
		this.repaint();
		System.out.println("Keypress: " + keypress);
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub

	}
	
	

}
