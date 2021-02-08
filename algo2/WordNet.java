/* Author: Ayrton San Joaquin
*  February 05 2020
*/

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.ST;
import edu.princeton.cs.algs4.Topological;

public class WordNet {
    private  ST<Integer, String> synList = new ST<>(); // {index of synset : synset (group of words)}
    private ST<String, Bag<Integer>> nounList  = new ST<>();// {nouns : list of synset ids containing noun}
    private Stack<String> synQ = new Stack<>();
    private Digraph digraph;
    private final SAP ancestor;

   // constructor takes the name of the two input files
   public WordNet(String synsets, String hypernyms) {
       if (synsets == null || hypernyms == null) throw new IllegalArgumentException();
       In synsetFile = new In(synsets);
       int size = 0;
       while(!synsetFile.isEmpty()) {
            String line = synsetFile.readLine();
            // parse CSV file
            String[] columns = line.split(",");
            String[] nouns = columns[1].split(" ");
            int index = Integer.parseInt(columns[0]);
            for(String noun: nouns) {
                if (nounList == null || !nounList.contains(noun)) {
                    Bag<Integer> bag = new Bag<>();
                    bag.add(index);
                    nounList.put(noun, bag);
                    synQ.push(noun);
                } 
                // If ST already contains noun, just update the set of indices
                else nounList.get(noun).add(index);
            }
            size++;
            synList.put(index, columns[1]);
        }

        // create a Digraph & throw exception if not rooted DAG
        // Running time: O(E), where E is the number of Edges
        digraph = new Digraph(size);
        In hypernymsFile = new In(hypernyms);
        while(!hypernymsFile.isEmpty()) {
            String line = hypernymsFile.readLine();
            // parse CSV file
            String[] columns = line.split(",");
            // put 1 column and onwards as the vertices to receive the edge's tail
            for(int i = 1; i < columns.length; i++){
                digraph.addEdge(Integer.parseInt(columns[0]), Integer.parseInt(columns[i]));
            }
        }

        // check if graph is acyclical (Recall a graph is acyclical iff it has a topological order)
        Topological orderCheck = new Topological(digraph);
        if (!orderCheck.hasOrder()) throw new IllegalArgumentException();;

        // check if graph is rooted
        int root = 0;
        for(int vertex = 0; vertex < digraph.V(); vertex++) {
            // the root does not have any hypernyms (0 outdegrees)
            if (digraph.outdegree(vertex) == 0) root++;
            if (root > 1) throw new IllegalArgumentException();
        }
        ancestor = new SAP(digraph);
   }

   // returns all WordNet nouns
   public Iterable<String> nouns() {
       return synQ;
   }

   // is the word a WordNet noun?
   public boolean isNoun(String word){
       if (word == null) throw new IllegalArgumentException();
       return nounList.contains(word);
   }

   // distance between nounA and nounB (defined below)
   public int distance(String nounA, String nounB) {
       if (nounA == null || nounB == null) throw new IllegalArgumentException();
       if (!isNoun(nounA) || !isNoun(nounB)) throw new IllegalArgumentException();
       return ancestor.length(nounList.get(nounA), nounList.get(nounB));
   }

   // a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
   // in a shortest ancestral path (defined below)
   public String sap(String nounA, String nounB) {
        if (nounA == null || nounB == null) throw new IllegalArgumentException();
        if (!isNoun(nounA) || !isNoun(nounB)) throw new IllegalArgumentException();
        // can handle iterables
        int ancestorIndex = ancestor.ancestor(nounList.get(nounA), nounList.get(nounB));
        return synList.get(ancestorIndex);
   }

   // do unit testing of this class
   public static void main(String[] args) {
       WordNet main = new WordNet("synsets.txt", "hypernyms.txt");
       StdOut.println(main.distance("Brown_Swiss", "barrel_roll"));     // 29
       StdOut.println(main.sap("worm", "bird"));                        // animal
       StdOut.println(main.distance("worm", "bird"));                   // 5
   }
}