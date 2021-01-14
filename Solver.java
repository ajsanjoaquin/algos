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
            return this.priority - that.priority;
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
        int internalMoves = 0;
        int twinMoveNumber = 0;

        // initial search node
        Triplet starting = new Triplet(internalMoves, null , initial);
        Triplet twin = new Triplet(internalMoves, null , initial.twin());

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

            // do not add a move when we pop the starting board
            if(!queue.isEmpty()) internalMoves++;
            if(!twinQueue.isEmpty()) twinMoveNumber++;            

            for (Board x : recentQ.tiles.neighbors()){
                // if the neighboring tile is the same as prev tile, ignore (redundancy)
                if(recentQ.prev ==null) queue.insert(new Triplet(internalMoves, recentQ, x));
                    else{
                        if(!recentQ.prev.getTiles().equals(x)) queue.insert(new Triplet(internalMoves, recentQ, x));
                    }

            }
            if (solvable != true) {
                for (Board y : recentTwin.tiles.neighbors()){
                    if(recentTwin.prev !=null) {
                            if(!recentTwin.prev.getTiles().equals(y)) twinQueue.insert(new Triplet(twinMoveNumber, recentTwin, y));
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
                target = target.prev;
            }
            // push the original Board
            solList.push(target.getTiles());
            
        }

        // NOTE: getMoves returns the number of moves made UP TO the recentQ's tile (exclusive)
        //if(internalMoves > 0) assert internalMoves == recentQ.getMoves()+1;

        // special case when the input is already solved
        if(solList.isEmpty()) solList.push(recentQ.getTiles());


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
            StdOut.println(solver.isSolvable());
            StdOut.println("No solution possible");
        }
        else {
            StdOut.println("Minimum number of moves = " + solver.moves());
            for (Board board : solver.solution())
                StdOut.println(board);
        }
    }

}