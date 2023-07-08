import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import java.awt.Font;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.Font;
import java.awt.Font;
import java.util.*;

public class t1 extends JFrame {
	 public enum Strategy {
	        BFS,
	        DFS,
	        GREEDY,
	        ASTAR
	    }
    private static JTextArea textArea;

    public t1() {
        textArea = new JTextArea();
        textArea.setFont(new Font("Arial", Font.PLAIN, 16)); // set font size to 16
        JScrollPane scrollPane = new JScrollPane(textArea); // wrap the JTextArea in a JScrollPane
        add(scrollPane);
        setSize(400, 400);
        setVisible(true);
    }

    public static void print(String message) {
        textArea.append(message);
    }

    public static void main(String[] args) {
        t1 window = new t1();

        int n = 8; // board size
        int[] initialState = generateInitialState(n);
        print("Initial state:\n");
        print(printBoard(initialState)); // print initial state
        int[] goalState = null;

        // run each search strategy
        for (Strategy strategy : Strategy.values()) {

            Queue<State> queue = new LinkedList<State>();
            Stack<State> stack = new Stack<State>();
            PriorityQueue<State> priorityQueue = new PriorityQueue<State>();
            Map<String, State> visited = new HashMap<String, State>();

            State initialStateObj = new State(initialState, 0, heuristic(initialState));
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

                if (isGoal(currentState)) {
                    goalState = currentState;
                    break;
                }

                List<State> children = generateChildren(currentState, visited);

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
                            child.f = child.depth + heuristic(child.board);
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
  print(strategy.toString() + ":\n" + printBoard(goalState) + "\nSteps (solution cost): " + steps + "\nNodes generated (search cost): " + nodesGenerated + "\nMax fringe size: " + maxFringeSize + "\n");

          
           if (strategy == Strategy.GREEDY || strategy == Strategy.ASTAR) {
               print("Heuristic values:\n");
               for (int i = 0; i < n; i++) {
                   for (int j = 0; j < n; j++) {
                       int[] state = Arrays.copyOf(goalState, n);
                       state[i] = j;
                      print(heuristic(state) + "\t");
                   }
                   print("\n");
               }
               print("\n");
           }

           print("\n");
        }
        
        
        

    }
    
    // State of the board
    public static class State implements Comparable<State> {
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

    // Check if the given state is a goal state
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

    // Generate all possible children for the given state
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
                        State childState = new State(child, visited.get(Arrays.toString(state)).depth + 1, heuristic(child));
                        children.add(childState);
                    }
                }
            }
        }

        return children;
    }

    // Generate initial state randomly
    public static int[] generateInitialState(int n) {
        int[] state = new int[n];
        for (int i = 0; i < n; i++) {
            state[i] = i;
        }
        shuffleArray(state);
        return state;
    }

    // Shuffle an array using Fisher�Yates shuffle algorithm
    public static void shuffleArray(int[] arr) {
        Random rnd = new Random();
        for (int i = arr.length - 1; i > 0; i--) {
            int index = rnd.nextInt(i + 1);
            int temp = arr[index];
            arr[index] = arr[i];
            arr[i] = temp;
        }
    }

    // Calculate the heuristic value for the given state
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

    // Print the board
    public static String printBoard(int[] state) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < state.length; i++) {
            for (int j = 0; j < state.length; j++) {
                if (state[i] == j) {
                    sb.append("Q  ");
                } else {
                    sb.append("_  ");
                }
            }
            sb.append("\n");
        }
        return sb.toString();
    }
    
    
}