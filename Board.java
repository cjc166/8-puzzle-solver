import java.util.*;

/**
 * A class to represent details of a board of the 8 puzzle
 * Each state is represented by a unique orientation of the board
 *
 * @author calebcain
 */
public class Board {

    private Board parent = null;
    private String state = null;
    private String moveDirection;

    private char[] states = new char[9];
    private char[][] boardRep = new char[3][3];

    private String stateNoSpaces;

    private int blankTileRow;
    private int blankTileColumn;

    private int blankTileIndex;

    /**
     * Represents the state of the board
     * Contains representation of a physical state
     * Links to the "parent" board, the state before the current state of the Board
     *
     * Links to "children" boards, the possible boards it can get to with respective moves
     *
     * @param parent a link to the previous state of the board, Null if board is initial state
     * @param state the physical representation of the tiles in the board
     */
    public Board(Board parent, String state, String moveDirection) {
        this.parent = parent;
        this.state = state;
        this.moveDirection = moveDirection;

        boardToCharArray();
        findBlankTile();
        blankTileIndex();
    }

    /**
     * Converts the state of the board to an array of characters
     */
    private void boardToCharArray() {
        stateNoSpaces = state.replaceAll("\\s","");
        states = stateNoSpaces.toCharArray();
        boardRep[0][0] = states[0];
        boardRep[0][1] = states[1];
        boardRep[0][2] = states[2];
        boardRep[1][0] = states[3];
        boardRep[1][1] = states[4];
        boardRep[1][2] = states[5];
        boardRep[2][0] = states[6];
        boardRep[2][1] = states[7];
        boardRep[2][2] = states[8];
    }

    /**
     * Finds where the blank tile is located in the board
     */
    private void findBlankTile() {
        for (int i = 0; i < boardRep.length; i++) {
            for (int j = 0; j < boardRep.length; j++) {
                if (boardRep[i][j] == 'b') {
                    blankTileRow = i;
                    blankTileColumn = j;
                }
            }
        }
    }

    /**
     * Finds where the blank tile is in the string
     */
    public void blankTileIndex() {
        for (int i = 0; i < states.length; i++) {
            if (states[i] == 'b')
                blankTileIndex = i;
        }
    }

    /**
     * Checks if the current state of the board is the goal
     * @return true if the board is the goal, false if it is not
     */
    public boolean isGoal() {
        if (state.equals("b12 345 678"))
            return true;
        else
            return false;
    }

    /**
     * gets the direction a tile is moving in
     * @return
     */
    public String getMoveDirection() {
        return moveDirection;
    }

    /**
     * returns the state of the 8 puzzle in string representation
     * @return the string representing the puzzle
     */
    public String getState() {
        return state;
    }

    /**
     * sets the state of the puzzle as a string
     * @param setState the state of the puzzle to be set
     */
    public void setState(String setState) {
        state = setState;
    }

    /**
     * returns the row of the blank tile in the current board
     * @return the int value of the row
     */
    public int getBlankTileRow() {
        return blankTileRow;
    }

    /**
     * returns the column of the blank tile in the current board
     * @return the int value of the column
     */
    public int getBlankTileColumn() {
        return blankTileColumn;
    }

    /**
     * a heuristic for the board
     * this heuristic calculates the number of misplaced tiles in the board compared to the goal state
     *
     * @return the number of misplaced tiles compared to the goal state
     */
    public int heuristicOne() {
        int misplacedTiles = 0;
        char[] goalState = "b12345678".toCharArray();

        for (int i = 0; i < goalState.length; i++) {
            if (states[i] != (goalState[i]))
                misplacedTiles += 1;
        }

        return misplacedTiles;
    }

    /**
     * a heuristic for the board
     * this heuristic calculates the sum of the distances of the tiles from their goal positions
     *
     * @return the sum of the distances of the tiles from their goal positions
     */
    public int heuristicTwo() {
        int manhattanDistance = 0;

        manhattanDistance = manhattanDistance();

        return manhattanDistance;
    }

    /**
     * Evaluation function to use for beam search
     * Function used is equivalent to manhattan distance
     *
     * @return the manhattan distance
     */
    public int evaluationFunction() {
        int evaluation = 0;
        evaluation = manhattanDistance();
        return evaluation;
    }

    /**
     * Computer the manhattan distance of an 8 puzzle board
     *
     * @return the manhattan distance of the board
     */
    public int manhattanDistance() {
        int manhattanDistance = 0;

        char[][] goalState = new char[3][3];
        goalState[0][0] = 'b';
        goalState[0][1] = '1';
        goalState[0][2] = '2';
        goalState[1][0] = '3';
        goalState[1][1] = '4';
        goalState[1][2] = '5';
        goalState[2][0] = '6';
        goalState[2][1] = '7';
        goalState[2][2] = '8';

        for (int m = 0; m < boardRep.length; m++) {
            for (int n = 0; n < boardRep.length; n++) {

                char currentChar = boardRep[m][n];

                int currentRow = m;
                int currentColumn = n;

                for (int i = 0; i < goalState.length; i++) {
                    for (int j = 0; j < goalState.length; j++) {
                        if (goalState[i][j] == boardRep[m][n]) {
                            int goalRow = i;
                            int goalColumn = j;
                            int tileDistance = Math.abs(goalRow - currentRow) + Math.abs(goalColumn - currentColumn);
                            manhattanDistance += tileDistance;
                        }
                    }
                }
            }
        }

        return manhattanDistance;
    }

    /**
     * Moves the blank space in the board to the right
     */
    public Iterable<Board> neighbors() {
        Stack<Board> boardStack = new Stack<Board>();

        //move right
        if (blankTileColumn < 2) {
            Board boardRight = new Board(this, moveBlank(1), "Right");
            boardStack.push(boardRight);
        }

        //move left
        if (blankTileColumn > 0) {
            Board boardLeft = new Board(this, moveBlank(-1), "Left");
            boardStack.push(boardLeft);
        }

        //move up
        if (blankTileRow > 0) {
            Board boardUp = new Board(this, moveBlank(-3), "Up");
            boardStack.push(boardUp);
        }

        //move down
        if (blankTileRow < 2) {
            Board boardDown = new Board(this, moveBlank(3), "Down");
            boardStack.push(boardDown);
        }

        return boardStack;

    }

    /**
     * move the blank tile a certain direction
     * @param offset the integer representation of how the tile is being moved
     * @return returns a string representing the state of the new board
     */
    public String moveBlank(int offset) {
        states = stateNoSpaces.toCharArray();
        char blank = states[blankTileIndex];
        char temp = states[blankTileIndex + offset];
        char[] newStates = states;
        newStates[blankTileIndex] = temp;
        newStates[blankTileIndex + offset] = blank;

        String newState = convertToStateString(newStates);

        return newState;
    }

    /**
     * Convert to the string representation we want of the puzzle states
     * @param newStates a char array representing the state
     * @return the string format of the states
     */
    public String convertToStateString(char[] newStates) {
        StringBuilder builder = new StringBuilder();

        builder.append(newStates[0]);
        builder.append(newStates[1]);
        builder.append(newStates[2]);
        builder.append(' ');
        builder.append(newStates[3]);
        builder.append(newStates[4]);
        builder.append(newStates[5]);
        builder.append(' ');
        builder.append(newStates[6]);
        builder.append(newStates[7]);
        builder.append(newStates[8]);

        return builder.toString();
    }

    /**
     * Determine if two boards are equal
     * @param y the board to check if is equal
     * @return a boolean true if the two are equal, false if they are not
     */
    public boolean equals(Object y){
        Board x = (Board) y;
        if (y == null)
            return false;
        if (this == y)
            return true;
        if (this.getClass() != y.getClass())
            return false;

        return (this.getState().equals(x.getState()));
    }
}
