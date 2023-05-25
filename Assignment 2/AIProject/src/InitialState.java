import java.util.*;
import java.util.Scanner;

public class InitialState {

	public static int[] generateInitialState(int n) {
		int[] state = new int[n];
		for (int i = 0; i < n; i++) {
			state[i] = i;
		}
		shuffleArray(state);
		return state;
	}

	public static void shuffleArray(int[] arr) {
		Random rnd = new Random();
		for (int i = arr.length - 1; i > 0; i--) {
			int index = rnd.nextInt(i + 1);
			int temp = arr[index];
			arr[index] = arr[i];
			arr[i] = temp;
		}
	}

	public static boolean isGoal(int[] state) {
		for (int i = 0; i < state.length; i++) {
			for (int j = i + 1; j < state.length; j++) {
				if (state[i] == state[j] || Math.abs(state[i] - state[j]) == j - i) {
					return false;
				}
			}
		}
		return true;
	}

	public static void printBoard(int[] state) {
		for (int i = 0; i < state.length; i++) {
			for (int j = 0; j < state.length; j++) {
				if (state[i] == j) {
					System.out.print("Q  ");
				} else {
					System.out.print("_  ");
				}
			}
			System.out.println("\n");
		}

	}

}
