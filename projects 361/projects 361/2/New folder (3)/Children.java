import java.util.*;

public class Children {
	public static int heuristic(int[] state) {
		int h = 0;
		for (int i = 0; i < state.length; i++) {
			for (int j = i + 1; j < state.length; j++) {
				if (state[i] == state[j] || Math.abs(state[i] - state[j]) == j - i) {
					h++;
				}
			}
		}
		return h;
	}

	public static List<State> generateChildren(int[] state, Map<String, State> visited) {
		List<State> children = new ArrayList<State>();

		for (int i = 0; i < state.length; i++) {
			for (int j = 0; j < state.length; j++) {
				if (i != j) {
					int[] child = Arrays.copyOf(state, state.length);
					int temp = child[i];
					child[i] = child[j];
					child[j] = temp;

					if (!visited.containsKey(Arrays.toString(child))) {
						State childState = new State(child, visited.get(Arrays.toString(state)).depth + 1,
								heuristic(child));
						children.add(childState);
					}
				}
			}
		}

		return children;
	}
}
