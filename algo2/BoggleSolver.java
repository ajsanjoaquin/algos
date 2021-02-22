import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.ST;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;
import java.lang.StringBuilder;

public class BoggleSolver {
    private TrieSET26 trie = new TrieSET26();
    private boolean marked[];
    private int width = 0;
    private int height = 0;
    private ST<String, String> validList;
    private BoggleBoard mainBoard;

    // Initializes the data structure using the given array of strings as the dictionary.
    // (You can assume each word in the dictionary contains only the uppercase letters A through Z.)
    public BoggleSolver(String[] dictionary) {
        for (int i = 0; i < dictionary.length; i++) {
            String word = dictionary[i];
            if (word.contains("QU")) trie.add(word.replace("QU", "Q"));

            // if a word starts with Q and not followed by a U, discard
            else if (word.contains("Q") && !word.contains("QU")) continue;
            else trie.add(word);
        }
    }

    // Returns the set of all valid words in the given Boggle board, as an Iterable.
    public Iterable<String> getAllValidWords(BoggleBoard board) {
        mainBoard = board;
        width = board.cols();
        height =  board.rows();
        int length = height * width;
        validList = new ST<>();

        for (int row = 0; row < width; row++) {
            for (int col = 0; col < height; col++) {    // starting letter of word candidate
                marked = new boolean[length]; // new marked array for every new starting letter
                StringBuilder candidate = new StringBuilder();
                candidate.append(board.getLetter(row, col));
                dfs(row, col, candidate);
            }
        }
        return validList;
    }

    private int twoD2OneD (int row, int col) {
        return (row * width) + col;
    }

    private void dfs (int row, int col, StringBuilder candidate) {
        marked[twoD2OneD(row, col)] = true;
        for (int[] pair : adj(row, col)) {
            int i = pair[0]; int j = pair[1];
            
            if (!marked[twoD2OneD(i, j)]) {
                marked[twoD2OneD(i, j)] = true;
                candidate.append(mainBoard.getLetter(i, j));
                String candidateString = candidate.toString();

                if (candidate.length() > 1) {

                    if (!trie.hasPrefix(candidateString)) {
                        candidate.setLength(candidate.length()-1); // remove the last character
                        marked[twoD2OneD(i, j)] = false;
                        continue;
                    } 
                    // check val of trie.keysThatMatch(candidateString).toString()
                    // must be greater than 2 chars (handled implicitly?)
                    if ((candidate.length() > 2) && trie.contains(candidateString)) validList.put(candidateString, candidateString);
                }
                dfs(i, j, candidate);
                candidate.setLength(candidate.length()-1); // remove the last character
                marked[twoD2OneD(i, j)] = false;
            }
        }
        // the loop terminates in the base case (every neighbor is marked)
        //marked[twoD2OneD(row, col)] = false; // reset to unmarked so other combinations can revisit
    }

    // gets the adjacent tiles and returns as an iterable
    private Iterable<int[]> adj(int row, int col) {
        Queue<int[]> list = new Queue<>();
        for (int i = row-1; i < row+2; i++) {
            for (int j = col-1; j < col+2; j++) {
                if ((i >= 0 && j >= 0) &&
                (i < height && j < width) && !(i == row && j ==col)) {
                    int[] pair = new int[2];
                    pair[0] = i; pair[1] = j;
                    list.enqueue(pair);
                }
            }
        }
        return list;
    }

    // Returns the score of the given word if it is in the dictionary, zero otherwise.
    // (You can assume the word contains only the uppercase letters A through Z.)
    public int scoreOf(String word) {
        if (trie.contains(word)) {
            int len = word.length();
            if (word.contains("Q")) {
                len++;
            }

            if (len == 3 || len == 4) return 1;
            else if (len == 5) return 2;
            else if (len == 6) return 3;
            else if (len == 7) return 5;
            else if (len > 7) return 11;
        }
        return 0;
    }

    public static void main(String[] args) {
        In in = new In(args[0]);
        String[] dictionary = in.readAllStrings();
        BoggleSolver solver = new BoggleSolver(dictionary);
        BoggleBoard board = new BoggleBoard(args[1]);
        int score = 0;
        for (String word : solver.getAllValidWords(board)) {
            StdOut.println(word);
            score += solver.scoreOf(word);
        }
        StdOut.println("Score = " + score);
    }
    
}
