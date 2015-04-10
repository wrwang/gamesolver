package org.gamesolver;
import java.util.Deque;
import java.util.List;
import java.util.Scanner;
import java.util.Set;


public class PlayGame {

	static int DEPTH = 7;

	public static void main(String[] args) {
		//int playFirst = Integer.parseInt(args[0]);
		int playFirst = 1;

		if (playFirst != 1) playFirst = -1;

		ReversiState game = new ReversiState();
		ReversiState gamePrev = new ReversiState(game);
		Scanner scan = new Scanner(System.in);

		System.out.println("Computer player: ");
		String humanString = scan.nextLine();
		int human = 0;
		try {
			human = Integer.parseInt(humanString);
		} catch (Exception e) {
			human = 0;
		}
		if (human != 1) human = -1;

		long timeTaken[] = new long[64];

		while (game.getVictory() != 1000) {
			System.out.println(game.toString(playFirst));
			System.out.println("Game score: " + game.getScore());

			if (game.getMoves(playFirst).size() == 0) {
				System.out.println("Player " + (playFirst == 1 ? 1 : 2) + " has no moves");
				playFirst = -playFirst;
			}
			int[] minimax;
			long now = System.currentTimeMillis();
			minimax = game.minimax(DEPTH, playFirst, playFirst);

			timeTaken[game.moves] = (System.currentTimeMillis() - now);
			System.out.println("Best move: " + minimax[1] + " resulting in score: " + minimax[0]);
			Deque<int[]> moves = game.getMoves(playFirst);
			boolean played = false;
			while (!played) {
				System.out.print("Player " + (playFirst == 1 ? 1 : 2) + "'s move: ");
				String posString = "";// = scan.nextLine();
				if (posString.equals("u")) {
					game = gamePrev;
					break;
				} else if (posString.equals("r")) {
					game.rotateClockwise();
					playFirst = -playFirst;
					break;
				} else if (posString.equals("l")) {
					game.rotateCounterClockwise();
					playFirst = -playFirst;
					break;
				}
				int pos = -1;
				try {
					pos = Integer.parseInt(posString);
				} catch (Exception e) {
					pos = -1;
				}
				pos = minimax[1];
				System.out.println(pos);
				if (pos == -1) {
					for (long l : timeTaken) {
						System.out.print(l +" ");
					}
					System.exit(0);
				}
				gamePrev = new ReversiState(game);
				int position[] = {pos / 8, pos % 8};
				played = game.playMove(playFirst, position);
			}

			playFirst = -playFirst;
		}
	}


	/*public static void main(String[] args) {
		TicTacToeState game = new TicTacToeState();
		game.playMove(-1, 0);
		game.playMove(-1, 1);
		game.playMove(-1, 6);
		game.playMove(1, 2);
		game.playMove(1, 4);
		game.playMove(1, 8);
		System.out.println(game);
		int[] minimax = game.minimax(10, 1, 0, 0);
		System.out.println("Best move: " + minimax[1] + " resulting in score: " + minimax[0]);
		//System.out.println(game.getScore(1) - game.getScore(-1));
	}*/

}
