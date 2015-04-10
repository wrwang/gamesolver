package org.gamesolver;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.Dictionary;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;


public class ReversiState {
	static final int rows = 8, cols = 8;
	//static final long LIMIT = 100000000;
	int board[] = new int[rows*cols];

	static final int boardValues[][] = 
		{
		{1000, -300, 100, 80, 80, 100, -300, 1000},
		{-300, -500, -45, -50, -50, -45, -500, -300},
		{100, -45, 3, 1, 1, 3, -45, 100},
		{80, -50, 1, 5, 5, 1, -50, 80},
		{80, -50, 1, 5, 5, 1, -50, 80},
		{100, -45, 3, 1, 1, 3, -45, 100},
		{-300, -500, -45, -50, -50, -45, -500, -300},
		{1000, -300, 100, 80, 80, 100, -300, 1000}
		};

	public static int directions[][] =
		{
		{-1,-1},{-1,1},{1,-1},{1,1},
		{0,-1},{0,1},{-1,0},{1,0}
		};

	int moves = 0;

	public ReversiState() {
		board[3*cols+3] = -1;
		board[4*cols+4] = -1;

		board[3*cols+4] = 1;
		board[4*cols+3] = 1;
	}

	public ReversiState(ReversiState rs) {
		System.arraycopy(rs.board, 0, board, 0, rs.board.length);
		moves = rs.moves;
	}

	public int[] minimax(int depth, int player, int eval) {
		return minimax(this, depth, player, Integer.MIN_VALUE, Integer.MAX_VALUE, eval);
	}

	public static int[] minimax(ReversiState rs, int depth, int player, 
			int alpha, int beta, int eval) {
		Deque<int[]> nextMoves = rs.getMoves(player);
		Integer score;
		int[] bestMove = {-1, -1};
		
		//if (!nextMoves.isEmpty()) timeLimit /= nextMoves.size();  
		//System.out.println(timeLimit + ", " + depth);
		
		
		if (nextMoves.isEmpty() || depth < 1 || rs.gameOver()) {
			if (eval > 0) {
				score = rs.getScore1();
			} else {
				score = rs.getScore2();
			}
			return new int[] {score, -1};
		} else {
			for (int[] move : nextMoves) {
				ReversiState nextState = new ReversiState(rs);
				nextState.playMove(player, move);
				int minimax[] = minimax(nextState, depth - 1, -player, alpha, beta, eval);
				score = minimax[0];
				if (player > 0) {
					if (score > alpha) {
						alpha = score;
						bestMove = move;
					}
				} else {
					if (score < beta) {
						beta = score;
						bestMove = move;
					}
				}
				if (alpha >= beta) break;
			}
		}
		
		if (bestMove[0] + bestMove[1] < 0) {
			bestMove = nextMoves.peek();
		}

		return new int[] {(player > 0 ? alpha : beta), bestMove[0] * cols + bestMove[1]};
	}

	public Deque<int[]> getMoves(int player) {
		Deque<int[]> moves = new ArrayDeque<>();
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				int[] pos = {i, j};
				if (getPos(pos) == 0 && adjacentOccupied(pos, player) && isValidMove(pos, player)) {
					moves.add(pos);
				}
			}
		}
		return moves;
	}

	public boolean playMove(int player, int[] pos) {
		boolean valid = false;
		if (inBounds(pos) && getPos(pos) == 0) {
			putPos(pos, player);
			for (int[] d : directions) {
				if (isValid(pos, d, player)) {
					search(pos, d, player);
					valid = true;
				}
			}
			if (!valid) putPos(pos, 0);
			else moves++;
		}
		return valid;
	}

	int getPos(int[] p) {
		return board[p[0]*cols+p[1]];
	}

	void putPos(int[] p, int i) {
		board[p[0]*cols+p[1]] = i;
	}

	boolean adjacentOccupied(int[] start, int i) {
		//boolean valid = false;
		int p[] = new int[2];
		for (int[] d : directions) {
			p[0] = start[0] + d[0];
			p[1] = start[1] + d[1];
			if (inBounds(p) && getPos(p) == -i) return true;
		}
		return false;
	}

	boolean isValidMove(int[] start, int i) {
		for (int[] d : directions) {
			int[] p = {start[0] + d[0], start[1] + d[1]};
			if (inBounds(p) && getPos(p) == -i && isValid(start, d, i)) 
				return true;
		}
		return false;
	}

	boolean isValid(int[] start, int[] d, int i) {
		boolean seenOther = false;
		
		int[] p = {start[0] + d[0], start[1] + d[1]};
		while (inBounds(p) && getPos(p) == -i) {
			if (!seenOther && getPos(p) == -i) seenOther = true;
			p[0] += d[0];
			p[1] += d[1];
		}
		if (!inBounds(p)) return false;
		return getPos(p) == i && seenOther;
	}

	void search(int[] start, int[] d, int i) {
		int[] p = {start[0] + d[0], start[1] + d[1]};
		while (inBounds(p) && getPos(p) != 0 && getPos(p) != i) {
			putPos(p, i);
			p[0] += d[0];
			p[1] += d[1];
		}
	}

	boolean inBounds(int[] p) {
		return (p[0] >= 0 && p[0] < rows && p[1] >= 0 && p[1] < cols);
	}

	int getVictory() {
		if (gameOver()) return (getScore() > 0 ? 1 : -1);
		return 0;
	}

	int getScore() {
		int sum = 0;
		for (int i = 0; i < rows; i++)
			for (int j = 0; j < cols; j++)
				sum += board[i*cols+j];  
		return sum;
	}
	
	int getScore1() {
		int sum = 0;
		for (int i = 0; i < rows; i++)
			for (int j = 0; j < cols; j++)
				sum += board[i*cols+j] * boardValues[i][j]; 
		sum += (getMoves(1).size() - getMoves(-1).size()) * 10; 
		return sum;
	}
	
	int getScore2() {
		int sum = 0;
		for (int i = 0; i < rows; i++)
			for (int j = 0; j < cols; j++)
				sum += board[i*cols+j] * boardValues[i][j]; 
		sum += (getMoves(1).size() - getMoves(-1).size()) * 100; 
		return sum;
	}

	boolean gameOver() {
		for (int i = 0; i < rows; i++)
			for (int j = 0; j < cols; j++)
				if (board[i*cols+j] == 0) return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++)
				if (board[i*cols+j] == 0) 
					sb.append(String.format("%2d ", i * cols + j));
				else
					sb.append(board[i*cols+j] > 0 ? " x " : " o ");
			sb.append("\n");
		}
		return sb.toString();
	}

	public String toString(int player) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++)
				if (board[i*cols+j] == 0)
					sb.append(String.format("   "));
				else
					sb.append(board[i*cols+j] > 0 ? " x " : " o ");
			sb.append("\n");
		}
		return sb.toString();
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(board);
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof ReversiState) {
			ReversiState rs = (ReversiState) o;
			return Arrays.equals(board, rs.board);
		}
		return false;
	}

	public void transpose() {
		for (int i = 0; i < rows; i++)
			for (int j = i + 1; j < cols; j++) {
				int x = board[i * cols + j];
				board[i * cols + j] = board[j * cols + i];
				board[j * cols + i] = x;
			}
	}

	public void swapRows() {
		for (int i = 0, k = rows - 1; i < k; i++, k--) {
			int[] x = new int[cols];
			System.arraycopy(board, i * cols, x, 0, cols);
			System.arraycopy(board, k * cols, board, i * cols, cols);
			System.arraycopy(x, 0, board, k * cols, cols);
		}
	}

	public void rotateCounterClockwise() {
		transpose();
		swapRows();
	}

	public void rotateClockwise() {
		swapRows();
		transpose();
	}

}
