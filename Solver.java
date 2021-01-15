/* Author: Ayrton San Joaquin
*  January 14 2020
*/
import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.Stack;

public class Solver {

    // Search node class
    private class Triplet implements Comparable<Triplet> {
        private int moves, priority = 0;
        private Board tiles;
        private Triplet prev;


        private Triplet (int moves, Triplet prev, Board tiles){
            this.moves = moves;
            this.tiles = tiles;
            this.prev = prev; 
            // apparently hamming distance is costly (research)
            this.priority = tiles.manhattan() + moves;
        }

        public int getMoves() {return moves;}
        public Board getTiles() {return tiles;}
        public int getPriority() {return priority;}
        public Triplet getPrev() {return prev;}

        public int compareTo(Triplet that) {
            return this.getPriority() - that.getPriority();
        } 
    }

    private int moveNumber;
    private Stack<Board> solList = new Stack<Board>();
    private boolean solvable = false;

    // find a solution to the initial board (using the A* algorithm)
    public Solver(Board initial) {
        if (initial == null) throw new IllegalArgumentException();
        MinPQ<Triplet> queue = new MinPQ<Triplet>();
        MinPQ<Triplet> twinQueue = new MinPQ<Triplet>();

        moveNumber = 0;
        Triplet target;

        // initial search node
        Triplet starting = new Triplet(0, null , initial);
        Triplet twin = new Triplet(0, null , initial.twin());

        //2 Asynchronous A* Searches (Original and Swapped Twin to check if original is unsolvable)
        // Twin search has early stopping if unfeasible
        // insert initial search nodes
        queue.insert(starting);
        twinQueue.insert(twin);
        Triplet recentQ = starting;
        Triplet recentTwin = twin;

        //Using the XNOR operator (break the loop if one of them becomes true)
        while (!recentQ.tiles.isGoal() == !recentTwin.tiles.isGoal()) {
            // pop the minimum from the PQ
            recentQ = queue.delMin();
            if (twinQueue.isEmpty()) solvable = true;
            if (solvable != true) recentTwin = twinQueue.delMin();
          
            for (Board x : recentQ.tiles.neighbors()){
                if(recentQ.getPrev() ==null) queue.insert(new Triplet(1, recentQ, x));
                else{
                    // if the neighboring tile is the same as prev tile, ignore (redundancy)
                    if(!recentQ.getPrev().getTiles().equals(x)) {
                        queue.insert(new Triplet(recentQ.getMoves()+1, recentQ, x));
                    }
                }

            }
            // twin block
            if (solvable != true) {
                for (Board y : recentTwin.tiles.neighbors()){
                    if(recentTwin.getPrev() ==null )twinQueue.insert(new Triplet(1, recentTwin, y));
                    else {
                        if(!recentTwin.getPrev().getTiles().equals(y)) twinQueue.insert(new Triplet(recentTwin.getMoves()+1, recentTwin, y));
                    }
                }
            }
        }
        if (recentTwin.tiles.isGoal()) solvable = false;
        else solvable = true;

        target = recentQ;
        if (solvable){
            while(!target.tiles.equals(starting.tiles)) {
                solList.push(target.getTiles());
                moveNumber++;
                target = target.getPrev();
            }
            // push the original Board
            solList.push(target.getTiles());
            
        }
    }

    // is the initial board solvable? (see below)
    public boolean isSolvable() {
        return solvable;
    }

    // min number of moves to solve initial board; -1 if unsolvable
    public int moves() {
        if (!isSolvable()) return -1;
        return moveNumber;
    }

    // sequence of boards in a shortest solution; null if unsolvable
    public Iterable<Board> solution() {
        if (!isSolvable()) return null;
        return solList;
    }

    // test client (see below) 
    public static void main(String[] args) {
        // create initial board from file
        In in = new In(args[0]);
        int n = in.readInt();
        int[][] tiles = new int[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                tiles[i][j] = in.readInt();
        Board initial = new Board(tiles);

        // solve the puzzle
        Solver solver = new Solver(initial);

        // print solution to standard output
        if (!solver.isSolvable()) {
            StdOut.println("No solution possible");
        }
        else {
            StdOut.println("Minimum number of moves = " + solver.moves());
            for (Board board : solver.solution())
                StdOut.println(board);
        }
    }

}