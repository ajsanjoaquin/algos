/* Author: Ayrton San Joaquin
*  February 06 2020
*/

import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.In;

public class SAP {
    private final int numV;
    private final Digraph d;

   // constructor takes a digraph (not necessarily a DAG)
   public SAP(Digraph G) {
       if (G == null) throw new IllegalArgumentException();
       d = new Digraph(G);
       numV = d.V();
   }

   // length of shortest ancestral path between v and w; -1 if no such path
   public int length(int v, int w) {
       validateVertices(v, w);
       if (v > w) swap(v, w);
       InterlockBFS find = new InterlockBFS(d, v, w);
       return find.ancestralPath();
   }

   // a common ancestor of v and w that participates in a shortest ancestral path; -1 if no such path
   public int ancestor(int v, int w) {
       validateVertices(v, w);
       if (v > w) swap(v, w);
       InterlockBFS find = new InterlockBFS(d, v, w);
       return find.ancestor();
   }

   // length of shortest ancestral path between any vertex in v and any vertex in w; -1 if no such path
   public int length(Iterable<Integer> v, Iterable<Integer> w) {
       if (v == null || w == null) throw new IllegalArgumentException();
       for (Integer i : v) if (i == null || (!(0 <= i && i < numV))) throw new IllegalArgumentException();
       for (Integer i : w) if (i == null || (!(0 <= i && i < numV))) throw new IllegalArgumentException();
       InterlockBFS find = new InterlockBFS(d, v, w);
       return find.ancestralPath();
   }

   // a common ancestor that participates in shortest ancestral path; -1 if no such path
   public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
       if (v == null || w == null) throw new IllegalArgumentException();
       for (Integer i : v) if (i == null || (!(0 <= i && i < numV))) throw new IllegalArgumentException();
       for (Integer i : w) if (i == null || (!(0 <= i && i < numV))) throw new IllegalArgumentException();

       InterlockBFS find = new InterlockBFS(d, v, w);
       return find.ancestor();
   }

   private void validateVertices(int v, int w){
       if(!(0 <= v && v < numV) || !(0 <= w && w < numV)) throw new IllegalArgumentException();
   }

   // so that method output does not vary by input
   private void swap (int v, int w) {
       int dummy = w;
       w = v;
       v = dummy;
   }

   // do unit testing of this class
   public static void main(String[] args) {
       In in = new In(args[0]);
        Digraph G = new Digraph(in);
        SAP sap = new SAP(G);

        In in1 = new In(args[0]);
        in1.readInt();
        in1.readInt();
        int v = 7;
        int w = 5;
        int length   = sap.length(v, w);
        int ancestor = sap.ancestor(v, w);
        StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
   }
}
