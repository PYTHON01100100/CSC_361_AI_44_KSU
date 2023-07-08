
public class State implements Comparable<State> {
	int[] board;
	int depth;
	int f;

	public State(int[] board, int depth, int h) {
		this.board = board;
		this.depth = depth;
		this.f = h;
	}

	// Compare two states based on their f value
	public int compareTo(State other) {
		return Integer.compare(this.f, other.f);
	}
}