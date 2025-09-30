// Designed to appear as maze constructed of (X's) with the options of movement listed below
package maze;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * MazeGame represents a playable, text-based maze.
 */
public class MazeGame
{
    // === Constants ===
    public static final int HEIGHT = 19;
    public static final int WIDTH = 39;
    public static final int ROW = 0;
    public static final int COL = 1;

    // === Fields ===
    private Scanner playerInput;
    private boolean[][] blocked;
    private boolean[][] visited;
    private int[] player; // {row, col}
    private int[] start;  // {row, col}
    private int[] goal;   // {row, col}

    // === Constructors ===

    public MazeGame(String mazeFile, Scanner playerInput) throws FileNotFoundException
    {
        this.playerInput = playerInput;
        loadMaze(mazeFile);
    }

    public MazeGame(String mazeFile) throws FileNotFoundException
    {
        this(mazeFile, new Scanner(System.in));
    }

    // === Getters and Setters ===

    public Scanner getPlayerInput() { return playerInput; }
    public void setPlayerInput(Scanner input) { playerInput = input; }

    // Player getters/setters
    public int getPlayerRow() { return player[ROW]; }
    public void setPlayerRow(int r) { if (r >= 0 && r < HEIGHT) player[ROW] = r; }
    public int getPlayerCol() { return player[COL]; }
    public void setPlayerCol(int c) { if (c >= 0 && c < WIDTH) player[COL] = c; }

    // Start getters/setters
    public int getStartRow() { return start[ROW]; }
    public void setStartRow(int r) { if (r >= 0 && r < HEIGHT) start[ROW] = r; }
    public int getStartCol() { return start[COL]; }
    public void setStartCol(int c) { if (c >= 0 && c < WIDTH) start[COL] = c; }

    // Goal getters/setters
    public int getGoalRow() { return goal[ROW]; }
    public void setGoalRow(int r) { if (r >= 0 && r < HEIGHT) goal[ROW] = r; }
    public int getGoalCol() { return goal[COL]; }
    public void setGoalCol(int c) { if (c >= 0 && c < WIDTH) goal[COL] = c; }

    // Blocked/Visited getters/setters (deep copy)
    public boolean[][] getBlocked() { return copyTwoDimBoolArray(blocked); }
    public void setBlocked(boolean[][] b) { blocked = copyTwoDimBoolArray(b); }
    public boolean[][] getVisited() { return copyTwoDimBoolArray(visited); }
    public void setVisited(boolean[][] v) { visited = copyTwoDimBoolArray(v); }

    // === Helper Methods ===

    private boolean[][] copyTwoDimBoolArray(boolean[][] arr)
    {
        boolean[][] copy = new boolean[arr.length][arr[0].length];
        for (int r = 0; r < arr.length; r++)
            for (int c = 0; c < arr[r].length; c++)
                copy[r][c] = arr[r][c];
        return copy;
    }

    private void loadMaze(String mazeFile) throws FileNotFoundException
    {
        blocked = new boolean[HEIGHT][WIDTH];
        visited = new boolean[HEIGHT][WIDTH];
        player = new int[2];
        start = new int[2];
        goal = new int[2];

        Scanner fileScanner = new Scanner(new File(mazeFile));

        for (int r = 0; r < HEIGHT; r++)
            for (int c = 0; c < WIDTH; c++)
            {
                String token = fileScanner.next();
                char ch = token.charAt(0);

                switch (ch)
                {
                    case '1': blocked[r][c] = true; break;
                    case '0': blocked[r][c] = false; break;
                    case 'S':
                        blocked[r][c] = false;
                        start[ROW] = r; start[COL] = c;
                        player[ROW] = r; player[COL] = c;
                        break;
                    case 'G':
                        blocked[r][c] = false;
                        goal[ROW] = r; goal[COL] = c;
                        break;
                    default:
                        throw new IllegalArgumentException(
                            "Unexpected char '" + ch + "' at row " + r + ", col " + c);
                }
            }

        fileScanner.close();
    }

    public void printMaze()
    {
        System.out.print("*"); for (int c = 0; c < WIDTH; c++) System.out.print("-"); System.out.println("*");

        for (int r = 0; r < HEIGHT; r++)
        {
            System.out.print("|");
            for (int c = 0; c < WIDTH; c++)
            {
                if (player[ROW] == r && player[COL] == c) System.out.print("@");
                else if (start[ROW] == r && start[COL] == c) System.out.print("S");
                else if (goal[ROW] == r && goal[COL] == c) System.out.print("G");
                else if (visited[r][c]) System.out.print(".");
                else if (blocked[r][c]) System.out.print("X");
                else System.out.print(" ");
            }
            System.out.println("|");
        }

        System.out.print("*"); for (int c = 0; c < WIDTH; c++) System.out.print("-"); System.out.println("*");
    }

    private void prompt()
    {
        printMaze();
        System.out.print("Enter your move (up, down, left, right, or q to quit): ");
    }

    private boolean playerAtGoal() { return player[ROW] == goal[ROW] && player[COL] == goal[COL]; }

    private boolean valid(int r, int c) { return r >= 0 && r < HEIGHT && c >= 0 && c < WIDTH && !blocked[r][c]; }

    private void visit(int r, int c) { visited[r][c] = true; }

    public boolean makeMove(String move)
    {
        if (move == null || move.isEmpty()) return false;

        char choice = Character.toLowerCase(move.charAt(0));
        int newRow = player[ROW], newCol = player[COL];

        switch (choice)
        {
            case 'q': return true;
            case 'u': newRow--; break;
            case 'd': newRow++; break;
            case 'l': newCol--; break;
            case 'r': newCol++; break;
            default: return false;
        }

        if (valid(newRow, newCol))
        {
            player[ROW] = newRow;
            player[COL] = newCol;
            visit(newRow, newCol);
        }

        return playerAtGoal();
    }

    public void playGame()
    {
        boolean gameOver = false;
        do {
            prompt();
            String move = playerInput.nextLine();
            gameOver = makeMove(move);
        } while (!gameOver);

        System.out.println(playerAtGoal() ? "You Won!" : "Goodbye!");
    }
}
