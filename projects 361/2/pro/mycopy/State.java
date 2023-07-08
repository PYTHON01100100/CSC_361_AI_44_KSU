
public class State implements Comparable<State> {
	int[] board;
	int cost;
	int f;

	public State(int[] board, int cost, int h) {
		this.board = board;
		this.cost = cost;
		this.f = h;
	}

	// Compare two states based on their f value
	public int compareTo(State other) {
		return Integer.compare(this.f, other.f);
	}
}