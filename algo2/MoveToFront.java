/* Author: Ayrton San Joaquin
*  March 3 2020
*/

import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

public class MoveToFront {
    private static final int R = 256; // # of chars in extended ASCII
    private static char[] alphabet = new char[R];

    // apply move-to-front encoding, reading from standard input and writing to standard output
    public static void encode() {
        for (char i = 0; i < R; i++) {
            alphabet[i] = i;
        }

        while (!BinaryStdIn.isEmpty()) {
            char x = BinaryStdIn.readChar();
            char left, i, right;
            for (i = 0, right = alphabet[0]; x != alphabet[i]; i++) {
                left = alphabet[i];
                alphabet[i] = right;
                right = left; 
            }
            alphabet[i] = right;
            BinaryStdOut.write(i); // output 8-bit index in alphaet where c appears
            alphabet[0] = x;
        }
        BinaryStdOut.close();
    }

    // apply move-to-front decoding, reading from standard input and writing to standard output
    public static void decode() {
        for (char i = 0; i < R; i++) {
            alphabet[i] = i;
        }

        while (!BinaryStdIn.isEmpty()) {
            char i = BinaryStdIn.readChar();
            char currentChar = alphabet[i];
            BinaryStdOut.write(currentChar, 8); // output the decoded char
            // update alphabet array
            while (i > 0) {
                alphabet[i] = alphabet[--i];
            }
            alphabet[0] = currentChar;
        }
        BinaryStdOut.close();
    }

    // if args[0] is "-", apply move-to-front encoding
    // if args[0] is "+", apply move-to-front decoding
    public static void main(String[] args) {
        String first = args[0];
        if (first.equals("+")) {
            decode();
        } else if (first.equals("-")) {
            encode();
        } else {
            throw new IllegalArgumentException("Wrong argument: " + first + "\n");
        }
    }
}