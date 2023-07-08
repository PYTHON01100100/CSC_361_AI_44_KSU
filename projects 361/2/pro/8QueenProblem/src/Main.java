import java.util.*;

public class Main {
	public static void main(String[] args) {
		Scanner kb = new Scanner(System.in);
		System.out.print("Enter your n-Queen number:");
		int size = kb.nextInt(); 
		int[] initialState = InitialState.generateInitialState(size);
		System.out.println("Initial state:\n");
		InitialState.printBoard(initialState);
		int[] goalState = null;
		numbers BFS = new numbers();
		numbers DFS = new numbers();
		numbers Greedy = new numbers();
		numbers ASTAR = new numbers();
		int enumcounter = 0;
		for (Strategies strategy : Strategies.values()) {

			Queue<State> queue = new LinkedList<State>();
			Stack<State> stack = new Stack<State>();
			PriorityQueue<State> priorityQueue = new PriorityQueue<State>();
			Map<String, State> visited = new HashMap<String, State>();

			State initialStateObj = new State(initialState, 0, Children.heuristic(initialState));
			queue.add(initialStateObj);
			stack.push(initialStateObj);
			priorityQueue.add(initialStateObj);
			visited.put(Arrays.toString(initialState), initialStateObj);

			int steps = 0;
			int nodesGenerated = 1;
			int maxFringeSize = 1;

			while (true) {
				if (queue.isEmpty() && stack.isEmpty() && priorityQueue.isEmpty()) {
					break;
				}

				State currentStateObj = null;

				switch (strategy) {
				case BFS:
					currentStateObj = queue.remove();
					break;
				case DFS:
					currentStateObj = stack.pop();
					break;
				case GREEDY:
				case ASTAR:
					currentStateObj = priorityQueue.remove();
					break;
				}

				int[] currentState = currentStateObj.board;
				visited.put(Arrays.toString(currentState), currentStateObj);

				if (InitialState.isGoal(currentState)) {
					goalState = currentState;
					break;
				}

				List<State> children = Children.generateChildren(currentState, visited);

				switch (strategy) {
				case BFS:
					queue.addAll(children);
					break;
				case DFS:
					stack.addAll(children);
					break;
				case GREEDY:
					priorityQueue.addAll(children);
					break;
				case ASTAR:
					for (State child : children) {
						child.f = child.cost + Children.heuristic(child.board);
						priorityQueue.add(child);
					}
					break;
				}

				nodesGenerated += children.size();

				if (queue.size() > maxFringeSize) {
					maxFringeSize = queue.size();
				}

				if (stack.size() > maxFringeSize) {
					maxFringeSize = stack.size();
				}

				if (priorityQueue.size() > maxFringeSize) {
					maxFringeSize = priorityQueue.size();
				}

				steps++;
			}
			if (enumcounter == 0) {
				BFS.steps = steps;
				BFS.nodesGenerated = nodesGenerated;
				BFS.maxFringeSize = maxFringeSize;
			} else if (enumcounter == 1) {
				DFS.steps = steps;
				DFS.nodesGenerated = nodesGenerated;
				DFS.maxFringeSize = maxFringeSize;
			} else if (enumcounter == 2) {
				Greedy.steps = steps;
				Greedy.nodesGenerated = nodesGenerated;
				Greedy.maxFringeSize = maxFringeSize;
			} else if (enumcounter == 3) {
				ASTAR.steps = steps;
				ASTAR.nodesGenerated = nodesGenerated;
				ASTAR.maxFringeSize = maxFringeSize;
			}
			enumcounter++;
		}
		System.out.println("Enter the number of Strategy you want :");
		System.out.println("1-BFS");
		System.out.println("2-DFS");
		System.out.println("3-Greedy");
		System.out.println("4-A*");
		System.out.println("0-Exit ");
		int choose = kb.nextInt();
		while (choose != 0) {

			switch (choose) {
			case 1:
				System.out.print(Strategies.BFS.toString() + ":\n");
				InitialState.printBoard(goalState);
				System.out.println("\nSteps (solution cost): " + BFS.steps + "\nNodes generated (search cost): "
						+ BFS.nodesGenerated + "\nMax fringe size: " + BFS.maxFringeSize + "\n");
				break;

			case 2:
				System.out.print(Strategies.DFS.toString() + ":\n");
				InitialState.printBoard(goalState);
				System.out.println("\nSteps (solution cost): " + DFS.steps + "\nNodes generated (search cost): "
						+ DFS.nodesGenerated + "\nMax fringe size: " + DFS.maxFringeSize + "\n");
				break;

			case 3:
				System.out.print(Strategies.GREEDY.toString() + ":\n");
				InitialState.printBoard(goalState);
				System.out.println("\nSteps (solution cost): " + Greedy.steps + "\nNodes generated (search cost): "
						+ Greedy.nodesGenerated + "\nMax fringe size: " + Greedy.maxFringeSize + "\n");
				System.out.println("Heuristic values:");
				for (int i = 0; i < size; i++) {
					for (int j = 0; j < size; j++) {
						int[] state = Arrays.copyOf(goalState, size);
						state[i] = j;
						System.out.print(Children.heuristic(state) + "   ");
					}
					System.out.println();
				}
				System.out.println();

				break;

			case 4:
				System.out.print(Strategies.ASTAR.toString() + ":\n");
				InitialState.printBoard(goalState);
				System.out.println("\nSteps (solution cost): " + ASTAR.steps + "\nNodes generated (search cost): "
						+ ASTAR.nodesGenerated + "\nMax fringe size: " + ASTAR.maxFringeSize + "\n");
				System.out.println("Heuristic values:");
				for (int i = 0; i < size; i++) {
					for (int j = 0; j < size; j++) {
						int[] state = Arrays.copyOf(goalState, size);
						state[i] = j;
						System.out.print(Children.heuristic(state) + "   ");
					}
					System.out.println();
				}
				System.out.println();
				break;

			}
			System.out.println("Enter the number of Strategy you want :");
			System.out.println("1-BFS");
			System.out.println("2-DFS");
			System.out.println("3-Greedy");
			System.out.println("4-A*");
			System.out.println("0-Exit ");
			choose = kb.nextInt();
		}
	}

}
