import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

public class Permutation {
   public static void main(String[] args) {
    int k = Integer.parseInt(args[0]);
    RandomizedQueue<String> main = new RandomizedQueue<>();
    
    // read each input line
    while (!StdIn.isEmpty()) {
        main.enqueue(StdIn.readString());
    }
    int limit = 0;
    // output each item up until the kth time
    for (String s : main)
        if (limit < k){
            StdOut.println(s);
            limit++;
        }
   }
}