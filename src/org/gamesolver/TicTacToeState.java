package org.gamesolver;
import java.util.ArrayList;
import java.util.List;


public class TicTacToeState {
	int board[];
	// 0 1 2
	// 3 4 5
	// 6 7 8

	public TicTacToeState() {
		board = new int[9];
	}

	public int[] minimax(int depth, int player, int alpha, int beta) {
		List<Integer> nextMoves = getMoves();
		int maxScore = (player == 1 ? Integer.MIN_VALUE : Integer.MAX_VALUE);
		int bestMove = -1;

		if (getVictory(player))
			maxScore = player*1000;
		else if (nextMoves.isEmpty() || depth == 0)
			maxScore = getScore(1) - getScore(-1);
		else
			for (int move : nextMoves) {
				board[move] = player;
				int score = minimax(depth - 1, -player, alpha, beta)[0];
				if (player == 1) {
					if (score > maxScore) {
						maxScore = score;
						bestMove = move;
					}
				} else {
					if (score < maxScore) {
						maxScore = score;
						bestMove = move;
					}
				}
				board[move] = 0;
			}

		return new int[] {maxScore, bestMove};
	}

	public List<Integer> getMoves() {
		List<Integer> moves = new ArrayList<>();
		for (int i = 0; i < 9; i++)
			if (board[i] == 0) moves.add(i);
		return moves;
	}

	public boolean playMove(int player, int pos) {
		if (board[pos] == 0) { 
			board[pos] = player;
			return true;
		}
		return false;
	}

	public int getScore(int player) {
		int score = 0;

		// check rows
		for (int i = 0; i < 3; i++) {
			int rowScore = 0;
			for (int j = 0; j < 3; j++) {
				if (board[i * 3 + j] == player) rowScore++;
			}
			score += Math.pow(5, rowScore);
		}

		// check cols
		for (int i = 0; i < 3; i++) {
			int rowScore = 0;
			for (int j = 0; j < 3; j++) {
				if (board[j * 3 + i] == player) rowScore++;
			}
			score += Math.pow(5, rowScore);
		}

		// check \
		int rowScore = 0;
		for (int j = 0; j < 3; j++) {
			if (board[j * 4] == player) rowScore++;
		}
		score += Math.pow(5, rowScore);

		// check /
		rowScore = 0;
		for (int j = 0; j < 3; j++) {
			if (board[j * 2 + 2] == player) rowScore++;
		}
		score += Math.pow(5, rowScore);

		return score;
	}

	int[] victoryBoards = {0b111000000, 0b000111000, 0b000000111,
			0b100100100, 0b010010010, 0b001001001,
			0b100010001, 0b001010100};
	public int getVictory() {
		if (getVictory(1)) return 1;
		if (getVictory(-1)) return -1;
		return 0;
	}

	public boolean getVictory(int player) {
		int boardInt = 0;
		for (int i = 0; i < 9; i++) {
			if (board[i] == player) {
				boardInt |= 1 << i;
			}
		}

		for (int board : victoryBoards) {
			if ((boardInt & board) == board) return true;
		}
		return false;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 9; i++) {
			if (i % 3 == 0) sb.append("\n");
			if (board[i] == 0) {
				sb.append(i);
			} else {
				if (board[i] == 1) {
					sb.append('x');
				} else {
					sb.append('o');
				}
			}
		}
		return sb.toString();
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof TicTacToeState) {
			TicTacToeState game = (TicTacToeState) o;
			for (int i = 0; i < 9; i++) {
				if (this.board[i] != game.board[i]) return false; 
			}
			return true;
		}
		return false;
	}

	@Override
	public int hashCode() {
		int hash = 1;
		for (int i = 0; i < 9; i++) {
			hash = hash * 13 + board[i]; 
		}
		return hash;
	}
}
