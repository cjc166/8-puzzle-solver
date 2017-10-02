/**
 * The Main class to run the 8 puzzle solving simulation
 *
 * @author calebcain
 */

import java.util.*;
import java.io.*;

public class Main {

    public Main(String[] args) {
        maxNodes(100000);

        if (args.length == 1 && args[0].endsWith(".txt")) {

            String filename = args[0];
            String line = null;

            try {
                FileReader fileReader = new FileReader(filename);

                BufferedReader bufferedReader = new BufferedReader(fileReader);

                while ((line = bufferedReader.readLine()) != null) {
                    line.replaceAll("\n", "");

                    // return pressed
                    if (line.length() == 0)
                        continue;

                    // split line into arguments
                    args = line.split(" ");

                    processArguments(args);
                }

                bufferedReader.close();

            } catch (FileNotFoundException ex) {
                System.out.println("Unable to open file '" + filename + "'");
            } catch (IOException ex) {
                System.out.println("Error reading file '" + filename + "'");
            }
        }

        getUserInput(args);
    }

    /**
     * The goal state of the 8 puzzle board
     */
    private final Board goalState = new Board(null, "b12 345 678", null);

    /**
     * The current state of the 8 puzzle board
     */
    private Board currentState;

    /**
     *  the max number of nodes to search while trying to find  a solution before printing an error message
     */
    private int maxNodes = 0;

    /**
     * priority queue for search
     */
    private PriorityQueue<Node> priorityQueue;

    private Node currentNode;

    private Node goalNode = new Node(goalState, null, 0);

    /**
     * gets the max amount of nodes a search algorithm is to search before stopping
     * @return the max number of nodes
     */
    public int getMaxNodes() {
        return maxNodes;
    }

    /**
     * sets the max number of nodes a search algorithm is to search before stopping
     * @param newMaxNodes the new number of max nodes to search
     */
    public void setMaxNodes(int newMaxNodes) {
        maxNodes = newMaxNodes;
    }

    /**
     * Represents a node which represents a game board
     */
    public class Node implements Comparable<Node> {
        public Board board;
        public Node previous;
        public int moves;

        public Node(Board gameboard, Node parent, int moveCount) {
            board = gameboard;
            previous = parent;
            moves = moveCount;
        }

        public int compareTo(Node otherNode) {
            if (heuristicOne() > otherNode.heuristicOne())
                return 1;
            else if (heuristicOne() < otherNode.heuristicOne())
                return -1;
            else
                return 0;
        }

        public int heuristicOne() {
            return board.heuristicOne();
        }

        public int heuristicTwo() {
            return board.heuristicTwo();
        }
    }

    /**
     * prompt format for commands to be entered
     */
    public static void prompt() {

        System.out.print(">> ");
    }

    /**
     *  prints a message when the command entered to the program is invalid
     */
    public static void invalidCommand() {
        System.out.println("INVALID COMMAND: Please enter a valid command and parameter if one is necessary");
    }

    /**
     *  prints a message when the input to a command must be an integer but was not
     */
    public static void wrongInputType() {
        System.out.println("INVALID ARGUMENT: Argument to this command must be an Integer");
    }

    /**
     * Checks if the state of the puzzle is the goal state
     */
    public void checkGoal() {
        if (goalState.getState().equals(currentState.getState()))
            System.out.println("CONGRATS, you have reached the goal and solved the puzzle!");
    }

    /**
     * sets the state of the puzzle
     * @param topRow the top row of the puzzle
     * @param bottomRow the middle row of the puzzle
     * @param middleRow the bottom row of the puzzle
     */
    public  void setState(String topRow, String middleRow, String bottomRow) {
        String state = topRow + " " + middleRow + " " + bottomRow;
        currentState = new Board(null, state, null);

        System.out.println("\nSetting state of puzzle to be " + state);
        if (currentState.getState().equals(goalState.getState()))
            System.out.println("This is the goal state\n");
    }

    /**
     * randomizes the state of the puzzle from the goal state
     * @param moves the number of random moves to make
     */
    public void randomizeState(Integer moves) {
        currentState = goalState;
        System.out.println("\nRandomizing state of puzzle");
        randomize(moves);
    }

    /**
     * Picks random directions to move the blank tile and randomizes the puzzle
     * Starts with the goal state to assure a solution is possible
     * @param n the amount of random moves to make
     */
    public void randomize(int n) {
        Random randomDirection = new Random();
        randomDirection.setSeed(373456123);

        int movesPerformed = 0;
        while (movesPerformed < n) {
            int direction = 0;

            /* choose a random move direction */
            int random = Math.abs(randomDirection.nextInt() % 100);

            if (random < 25)
                direction = 1;
            if (random >= 25 && random < 50)
                direction = -1;
            if (random >= 50 && random < 75)
                direction = 3;
            if (random >= 75 && random < 100)
                direction = -3;

            if (direction == 1) {
                if (currentState.getBlankTileColumn() < 2) {
                    Board nextState = new Board(currentState, currentState.moveBlank(direction), "Right");
                    movesPerformed += 1;
                    currentState = nextState;
                }
            }

            if (direction == -1) {
                if (currentState.getBlankTileColumn() > 0) {
                    Board nextState = new Board(currentState, currentState.moveBlank(direction), "Left");
                    movesPerformed += 1;
                    currentState = nextState;
                }
            }

            if (direction == 3) {
                if (currentState.getBlankTileRow() < 2) {
                    Board nextState = new Board(currentState, currentState.moveBlank(direction), "Down");
                    movesPerformed += 1;
                    currentState = nextState;
                }
            }

            if (direction == -3) {
                if (currentState.getBlankTileRow() > 0) {
                    Board nextState = new Board(currentState, currentState.moveBlank(direction), "Up");
                    movesPerformed += 1;
                    currentState = nextState;
                }
            }
        }
        System.out.println("Puzzle state randomized, the current state is: " + currentState.getState());
    }

    /**
     * prints the current state of the puzzle
     */
    public void printState() {
        // print the current state of the puzzle
        try {
            System.out.println("The current state of the puzzle is " + currentState.getState());
        } catch (NullPointerException n) {
            System.out.println("The state of the 8-puzzle must be set first");
        }
    }

    /**
     * moves the blank tile to the right in the current state of the puzzle if possible
     */
    public void moveRight() {
        if (currentState.getBlankTileColumn() < 2) {
            Board nextState = new Board(currentState, currentState.moveBlank(1), "Right");
            currentState = nextState;
            System.out.println("Blank tile move to the right, the new state of the puzzle is: " + currentState.getState());
            checkGoal();
        }
        else
            System.out.println("Cannot move tile to the right");
    }

    /**
     * moves the blank tile to the left in the current state of the puzzle if possible
     */
    public void moveLeft() {
        if (currentState.getBlankTileColumn() > 0) {
            Board nextState = new Board(currentState, currentState.moveBlank(-1), "Left");
            currentState = nextState;
            System.out.println("Blank tile move to the left, the new state of the puzzle is: " + currentState.getState());
            checkGoal();
        }
        else
            System.out.println("Cannot move tile to the left");
    }

    /**
     * moves the blank tile up in the current state of the puzzle if possible
     */
    public void moveUp() {
        if (currentState.getBlankTileRow() > 0) {
            Board nextState = new Board(currentState, currentState.moveBlank(-3), "Up");
            currentState = nextState;
            System.out.println("Blank tile moved up, the new state of the puzzle is: " + currentState.getState());
            checkGoal();
        }
        else
            System.out.println("Cannot move tile up");
    }

    /**
     * moves the blank tile down in the current state of the puzzle if possible
     */
    public void moveDown() {
        if (currentState.getBlankTileRow() < 2) {
            Board nextState = new Board(currentState, currentState.moveBlank(3), "Down");
            currentState = nextState;
            System.out.println("Blank tile moved down, the new state of the puzzle is: " + currentState.getState());
            checkGoal();
        }
        else
            System.out.println("Cannot move tile down");
    }

    /**
     *  moves the blank tile in a specific direction
     *  @param direction the direction to move the blank tile
     */
    public void move(String direction) {

        if (direction.equalsIgnoreCase("right"))
            moveRight();
        else if (direction.equalsIgnoreCase("left"))
            moveLeft();
        else if (direction.equalsIgnoreCase("up"))
            moveUp();
        else if (direction.equalsIgnoreCase("down"))
            moveDown();
        else
            System.out.println("INVALID DIRECTION: Please enter a valid move direction");

    }

    /**
     *  specifies the max number of nodes to be searched using any given search algorithm
     *  @param n the max number of nodes to search
     */
    public void maxNodes(Integer n) {
        setMaxNodes(n);
        System.out.println("Max number of nodes to search set to " + getMaxNodes());
    }

    /**
     * solves the puzzle using a-star search with a given heuristic
     * @param heuristic the heuristic to use to solve the puzzle
     */
    public void solveAStar(String heuristic) {

        if (heuristic.equalsIgnoreCase("h1")) {
            System.out.println("\nAttempting to solve using A* search with heuristic " + heuristic + "\n");
            aStar(1);
        }
        else if (heuristic.equalsIgnoreCase("h2")) {
            System.out.println("\nAttempting to solve using A* search with heuristic " + heuristic + "\n");
            aStar(2);
        }
        else
            System.out.println("Plese enter a valid heuristic to use\nValid heuristics:\nh1\nh2");
    }

    /**
     * method to solve the puzzle using a given heuristic for a-star search
     * prints the solution to the puzzle if there is one
     *
     * @param heuristic the heuristic to be used
     */
    public void aStar(final int heuristic) {

        //creates a priority queue that orders by heuristic one, misplaced tiles
        priorityQueue = new PriorityQueue<Node>(20, new Comparator<Node>() {
            @Override
            public int compare(Node node, Node t1) {
                if (heuristic == 1) {
                    if ((node.moves + node.board.heuristicOne()) < (t1.board.heuristicOne() + t1.moves))
                        return -1;
                    if ((node.moves + node.board.heuristicOne()) > (t1.board.heuristicOne() + t1.moves))
                        return 1;
                    return 0;
                }
                if (heuristic == 2) {
                    if ((node.moves + node.board.heuristicTwo()) < (t1.board.heuristicTwo() + t1.moves))
                        return -1;
                    if ((node.moves + node.board.heuristicTwo()) > (t1.board.heuristicTwo() + t1.moves))
                        return 1;
                    return 0;
                }
                return 0;
            }
        });

        int nodesVisited = 0;
        currentNode = new Node(currentState, null, 0);
        nodesVisited++;
        priorityQueue.add(currentNode);

        while (currentNode.board.isGoal() == false) {

            for (Board b : currentNode.board.neighbors()) {

                if (!b.equals(currentNode.board)) {
                    priorityQueue.add(new Node(b, currentNode, currentNode.moves + 1));
                    nodesVisited++;
                }

                if (nodesVisited > getMaxNodes()) {
                    System.out.println("Solution not found.\n" +
                            "Max number of nodes to search exceeded.\n" +
                            "May need to change or set number of max nodes to search if have not done so.\n");
                    return;
                }
            }

            currentNode = priorityQueue.remove();
            currentState = currentNode.board;
        }

        printSolution();
        checkGoal();
    }

    /**
     *  solves the puzzle using beam search with a given number of states
     *  @param k the max number of states to use in our search
     */
    public void solveBeam(Integer k) {

        System.out.println("\nAttempting to solve using beam search with " + k + " states\n");

        //creates a priority queue that orders by heuristic one, misplaced tiles
        priorityQueue = new PriorityQueue<Node>(20, new Comparator<Node>() {
            @Override
            public int compare(Node node, Node t1) {

                if (node.board.evaluationFunction() < t1.board.evaluationFunction())
                    return -1;
                if (node.board.evaluationFunction() > t1.board.evaluationFunction())
                    return 1;

                return 0;
            }
        });

        currentNode = new Node(currentState, null, 0);
        priorityQueue.add(currentNode);

        while (currentNode.board.isGoal() == false) {

            for (Board b : currentNode.board.neighbors()) {

                if (!b.equals(currentNode.board)) {
                    priorityQueue.add(new Node(b, currentNode, currentNode.moves + 1));
                }

                if (priorityQueue.size() > k) {
                    System.out.println("Solution not found.\n" +
                            "Beam search state space exceeded.\n" +
                            "May need to change state space to search in order to find a solution\n");
                    return;
                }
            }

            currentNode = priorityQueue.remove();
            currentState = currentNode.board;
        }

        printSolution();
        checkGoal();

    }

    public void printSolution() {
        ArrayList<Node> solution = new ArrayList<Node>();
        while(currentNode.previous != null) {
            solution.add(currentNode);
            currentNode = currentNode.previous;
        }

        Collections.reverse(solution);

        System.out.println("Start state is: " + solution.get(0).previous.board.getState() + "\n");

        for (int i = 0; i < solution.size(); i++) {
            Node printNode = solution.get(i);
            System.out.println("Move Number: " + printNode.moves +
                    "\nCurrent State: " + printNode.board.getState() +
                    "\nMove Direction: " + printNode.board.getMoveDirection());

            if (printNode.previous != null)
                System.out.println("Previous State: " + printNode.previous.board.getState() + "\n");
            else
                System.out.println("");
        }
    }

    /**
     * processes the arguments fed to the program by the user or from a file
     * @param args
     */
    public void processArguments(String[] args) {

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("exit"))
                System.exit(0);
            else if (args[0].equalsIgnoreCase("printState"))
                printState();
            else
                invalidCommand();
        }

        else if (args.length == 2) {

            if (args[0].equalsIgnoreCase("randomizeState")) {
                try {
                    randomizeState(Integer.parseInt(args[1]));
                } catch (NumberFormatException n) {
                    wrongInputType();
                }
            }
            else if (args[0].equalsIgnoreCase("move"))
                move(args[1]);
            else if (args[0].equalsIgnoreCase("maxNodes")) {
                try {
                    maxNodes(Integer.parseInt(args[1]));
                } catch (NumberFormatException n) {
                    wrongInputType();
                }
            }
            else
                invalidCommand();
        }

        else if (args.length == 3) {

            if (args[0].equalsIgnoreCase("solve"))
            {
                if (args[1].equalsIgnoreCase("A-star")) {
                    solveAStar(args[2]);
                }
                else if (args[1].equalsIgnoreCase("beam")) {
                    try {
                        solveBeam(Integer.parseInt(args[2]));
                    } catch (NumberFormatException n) {
                        wrongInputType();
                    }
                }
                else
                    invalidCommand();
            }
            else
                invalidCommand();
        }

        else if (args.length == 4) {
            if (args[0].equalsIgnoreCase("setState"))
                setState(args[1], args[2], args[3]);
            else
                invalidCommand();
        }

        else
            invalidCommand();
    }

    /**
     * continuously retrieves the commands the user feeds to the program
     * @param args
     */
    public void getUserInput(String[] args) {
        Scanner scanner = new Scanner(System.in);

        for (prompt(); scanner.hasNextLine(); prompt()) {

            String line = scanner.nextLine().replaceAll("\n", "");

            // return pressed
            if (line.length() == 0)
                continue;

            // split line into arguments
            args = line.split(" ");

            processArguments(args);
        }
    }


    /**
     * main method to execute the program with continuous input from the user or a .txt file
     * @param args
     */
    public static void main(String[] args) {

        Main main = new Main(args);

    }
}
