/* Author: Ayrton San Joaquin
*  January 12 2020
*/

// problems: hamming, twin

import java.util.Arrays;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.In;

public class Board {
    private int[][] mainBoard;
    private final int n;
// use Arrays.deepEquals for array[] [] element comparison
    // create a board from an n-by-n array of tiles,
    // where tiles[row][col] = tile at (row, col)
    public Board(int[][] tiles) {
        n = tiles.length;
        mainBoard = new int[n][n];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j <n; j++) {
                mainBoard[i][j] = tiles[i][j];
            }
        }
    }
                                           
    // string representation of this board
    public String toString() {
        StringBuilder text = new StringBuilder();
        text.append(n + "\n");        
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                text.append(String.format("%2d ", mainBoard[i][j]));
            }
            text.append("\n");
        }
        return text.toString();
    }

    // board dimension n
    public int dimension() {
        return n;
    }

    // number of tiles out of place
    public int hamming() {
        int incorrect = 0;
        for (int i = 0; i < mainBoard.length; i++) {
            for (int j = 0; j < mainBoard.length; j++) {
            // if everything is in its place AND 0 is in the very last position
            if (!(i == n-1 && j == n-1) && mainBoard[i][j] != ((i * n) + (j+1))) incorrect++;
            if (((i == n-1 && j == n-1) &&(mainBoard[i][j]!= 0))) incorrect++;
            }
        }
        return incorrect;
    }

    // sum of Manhattan distances between tiles and goal
    public int manhattan() {
        int distance = 0;
        // i wanna go there and eat pizza
        for (int i = 0; i < mainBoard.length; i++) {
            for (int j = 0; j < mainBoard.length; j++) {
                if ( mainBoard[i][j] != 0 && mainBoard[i][j] != ((i * n) + (j+1))) {
                    int element = mainBoard[i][j];
                    // calculate target coords
                    int targetX = (element - 1) / n;
                    int targetY =  (element - 1) % n;

                    int dx = Math.abs(targetX - i);
                    int dy = Math.abs(targetY - j);
                    distance += dx +dy;
                }
            }
        }
        return distance;
    }


    // is this board the goal board?
    public boolean isGoal() {
        return manhattan() == 0;
    }

    // does this board equal y?
    public boolean equals(Object y) {
        // do some easy tests first
        if (y == null) return false;
        
        if (y == this) return true;

        if (y.getClass() != this.getClass()) return false;

        // convert object y into a type Board
        Board that = (Board) y;
        // check if dims are equal
        if (this.n != that.n) return false;

        // check if all elements within the array are equal
        return Arrays.deepEquals(this.mainBoard, that.mainBoard);
    }

    // all neighboring boards
    public Iterable<Board> neighbors() {
        Stack<Board> neighborList = new Stack<Board>();
        boolean found = false;

        for (int i = 0; i < mainBoard.length; i++) {
            if (found) break;

            for (int j = 0; j < mainBoard.length; j++) {
                if (found) break;

                if ( mainBoard[i][j] == 0) {
                    found = true;

                    if (i+1 < n)  {
                        Board neighbor = new Board(mainBoard);
                        neighbor.mainBoard[i][j] = neighbor.mainBoard[i+1][j];
                        neighbor.mainBoard[i+1][j] = 0;
                        neighborList.push(neighbor);
                    }

                    if (i-1 >= 0)  {
                        Board neighbor = new Board(mainBoard);
                        neighbor.mainBoard[i][j] = neighbor.mainBoard[i-1][j];
                        neighbor.mainBoard[i-1][j] = 0;
                        neighborList.push(neighbor);
                    }

                    if (j+1 < n)  {
                        Board neighbor = new Board(mainBoard);
                        neighbor.mainBoard[i][j] = neighbor.mainBoard[i][j+1];
                        neighbor.mainBoard[i][j+1] = 0;
                        neighborList.push(neighbor);
                    }

                    if (j-1 >= 0)  {
                        Board neighbor = new Board(mainBoard);
                        neighbor.mainBoard[i][j] = neighbor.mainBoard[i][j-1];
                        neighbor.mainBoard[i][j-1] = 0;
                        neighborList.push(neighbor);
                    }
                }
            }
        }
        return neighborList;
    }


    // a board that is obtained by exchanging any pair of tiles
    // to determine if original board is solvable
    // tbh, I could have just used a double for-loop...
    public Board twin() {
        Board twin = new Board(mainBoard);
        int firstItem;

        for (int i = 0; i < n; i++){
            for (int j = 0 ; j < n; j++){
                if (twin.mainBoard[i][j] != 0 && twin.mainBoard[i+1][j] != 0) {
                    firstItem = twin.mainBoard[i][j];
                    twin.mainBoard[i][j] = twin.mainBoard[i+1][j];
                    twin.mainBoard[i+1][j] = firstItem;
                }
                break;
            }
            break;
        }
        return twin;
    }

    // unit testing (not graded)
    public static void main(String[] args) {
        In in = new In(args[0]);
        int N = in.readInt();
        int[][] tiles = new int[N][N];
        for (int i = 0; i < N; i++)
            for (int j = 0; j < N; j++)
            tiles[i][j] = in.readInt();
        Board test = new Board(tiles);

        
        
        StdOut.println(test);
        
        StdOut.println(test.isGoal());
        StdOut.println(test.manhattan());

        StdOut.println(test.twin());

        StdOut.println("neighbors");
        for (Board board : test.neighbors()) {
            StdOut.println(board);
        }
    }

}