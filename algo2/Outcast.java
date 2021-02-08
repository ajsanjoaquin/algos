/* Author: Ayrton San Joaquin
*  February 07 2020
*/

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

public class Outcast {
    private WordNet copy;
    // constructor takes a WordNet object
    public Outcast(WordNet wordnet) {
       copy =  wordnet;
    }         

   // given an array of WordNet nouns, return an outcast
   public String outcast(String[] nouns) {
       String outcast = "error";
       int maxD = 0;
       for (int i = 0; i < nouns.length; i++) {
           int distance = 0;
           for (int j = 0; j < nouns.length; j++) {
               if (i != j) {
                   distance += copy.distance(nouns[i], nouns[j]);
               }
           }
           if (maxD < distance) {
               maxD = distance;
               outcast = nouns[i];

               assert maxD != 0;
               assert outcast.equals("error");
           }
       }
       return outcast;
    }  
    // test client
   public static void main(String[] args) {
        WordNet wordnet = new WordNet(args[0], args[1]);
        Outcast outcast = new Outcast(wordnet);
        for (int t = 2; t < args.length; t++) {
            In in = new In(args[t]);
            String[] nouns = in.readAllStrings();
            StdOut.println(args[t] + ": " + outcast.outcast(nouns));
        }
   } 
}