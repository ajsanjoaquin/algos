/* Author: Ayrton San Joaquin
*  March 3 2020
*/

import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

public class BurrowsWheeler {
    private static final int R = 256; // # of chars in extended ASCII

    // apply Burrows-Wheeler transform,
    // reading from standard input and writing to standard output 
    public static void transform() {
        String in = BinaryStdIn.readString();
        CircularSuffixArray tf = new CircularSuffixArray(in);

        int firstKey = -1;
        StringBuilder candidate = new StringBuilder();

        for (int i = 0; i < tf.length(); i++) {
            int index = tf.index(i);
            if (index == 0) firstKey = i; 

            int origIndex = index-1;
            if (origIndex < 0) origIndex = tf.length()-1;
            candidate.append(in.charAt(origIndex));
        }

        BinaryStdOut.write(firstKey);
        BinaryStdOut.write(candidate.toString());
        BinaryStdOut.close();
    }

    // apply Burrows-Wheeler inverse transform,
    // reading from standard input and writing to standard output
    public static void inverseTransform() {
        int first = BinaryStdIn.readInt();
        String in = BinaryStdIn.readString();
        int[] count = new int[R+1];
        char[] aux = new char[in.length()];
        int[] next = new int[in.length()];

        // key-indexed counting taken from lecture
        for (int i = 0; i < in.length(); i++) {
            count[in.charAt(i)+1]++;
        }

        for (int r = 0; r < R; r++) {
            count[r+1] += count[r];
        }

        for (int i = 0; i < in.length(); i++) {
            next[count[in.charAt(i)]] = i;
            aux[count[in.charAt(i)]++] = in.charAt(i);
        }

        int index = first;
        StringBuilder candidate = new StringBuilder();
        for (int i = 0; i < in.length(); i++) {
            candidate.append(aux[index]);
            index = next[index];
        }
        BinaryStdOut.write(candidate.toString());
        BinaryStdOut.close();
    }


    // if args[0] is "-", apply Burrows-Wheeler transform
    // if args[0] is "+", apply Burrows-Wheeler inverse transform
    public static void main(String[] args) {
        String first = args[0];
        if (first.equals("+")) {
            inverseTransform();
        } else if (first.equals("-")) {
            transform();
        } else {
            throw new IllegalArgumentException("Wrong argument: " + first + "\n");
        }
        //String s = "ARD!RCAAAABB";
        //BurrowsWheeler.inverseTransform(s);
    }

}