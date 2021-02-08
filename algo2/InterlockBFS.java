// Copied from BreadthFirstDirectedPaths.class
// Copyright Robert Sedgewick and Kevin Wayne

/* Modified by: Ayrton San Joaquin
*  February 06 2020
*/

import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.Stack;

public class InterlockBFS {
    private static final int INFINITY = Integer.MAX_VALUE;
    private boolean[] marked1, marked2;  // marked[v] = is there an s->v path?
    private int[] edgeTo1, edgeTo2;      // edgeTo[v] = last edge on shortest s->v path
    private int[] distTo1, distTo2;      // distTo[v] = length of shortest s->v path
    private int ancestor = -1;
    private int ancestralPath, v, w;


    // Computes the shortest path from {@code s} and every other vertex in graph {@code G}.
    public InterlockBFS(Digraph G, int v, int w) {
        this.v = v;
        this.w = w;

        marked1 = new boolean[G.V()];
        marked2 = new boolean[G.V()];
        distTo1 = new int[G.V()];
        distTo2 = new int[G.V()];
        edgeTo1 = new int[G.V()];
        edgeTo2 = new int[G.V()];
        for (int s = 0; s < G.V(); s++) {
            distTo1[s] = INFINITY;
            distTo2[s] = INFINITY;
        }    
        bfs(G, v, w);
    }

    // Computes the shortest path from any one of the source vertices in {@code sources} 
    // to every other vertex in graph G
    public InterlockBFS(Digraph G, Iterable<Integer> v,  Iterable<Integer> w) {
        marked1 = new boolean[G.V()];
        marked2 = new boolean[G.V()];
        distTo1 = new int[G.V()];
        distTo2 = new int[G.V()];
        edgeTo1 = new int[G.V()];
        edgeTo2 = new int[G.V()];
        for (int s = 0; s < G.V(); s++) {
            distTo1[s] = INFINITY;
            distTo2[s] = INFINITY;
        }   
        bfs(G, v, w);
    }

    // BFS from single source
    private void bfs(Digraph G, int s, int k) {
        ancestralPath = INFINITY;

        Queue<Integer> vQ = new Queue<Integer>();
        marked1[s] = true;
        distTo1[s] = 0;
        vQ.enqueue(s);

        Queue<Integer> wQ = new Queue<Integer>();
        marked2[k] = true;
        distTo2[k] = 0;
        wQ.enqueue(k);

        interlock(G, vQ, wQ);
    }

    // BFS from multiple sources
    private void bfs(Digraph G, Iterable<Integer> V, Iterable<Integer> W) {
        ancestralPath = INFINITY;

        Queue<Integer> vQ = new Queue<Integer>();
        for (int s : V) {
            marked1[s] = true;
            distTo1[s] = 0;
            vQ.enqueue(s);
        }

        Queue<Integer> wQ = new Queue<Integer>();
        for (int s : W) {
            marked2[s] = true;
            distTo2[s] = 0;
            wQ.enqueue(s);
        }
        interlock(G, vQ, wQ);
    }
    // helper function
    private void interlock(Digraph G, Queue<Integer> vQ, Queue<Integer> wQ) {
        while (!vQ.isEmpty() || !wQ.isEmpty()) {
            if (!vQ.isEmpty()) {
                int v = vQ.dequeue();

                if (marked2[v]) {         // check if v is marked from both sources
                    // if next ancestor found has a longer ancestral path, break search
                    if (distTo1[v] + distTo2[v] > ancestralPath) return;
                    assert ancestralPath >= 0;
                    ancestor = v;
                    // set ancestor distance
                    ancestralPath = distTo1[v] + distTo2[v];
                } 

                for (int w : G.adj(v)) {
                    if (!marked1[w]) {
                        edgeTo1[w] = v;
                        distTo1[w] = distTo1[v] + 1;
                        marked1[w] = true;
                        vQ.enqueue(w);
                    }
                    
                    if (marked2[w]) {
                        // if next ancestor found has a longer ancestral path, break search
                        if (distTo1[w] + distTo2[w] > ancestralPath) return;
                        assert ancestralPath >= 0;
                        ancestor = w;
                        // set ancestor distance
                        ancestralPath = distTo1[w] + distTo2[w];
                    } 
                }
            }

            if (!wQ.isEmpty()) {
                int v = wQ.dequeue();

                if (marked1[v]) {         // check if v is marked from both sources
                    // if next ancestor found has a longer ancestral path, break search
                    if (distTo1[v] + distTo2[v] > ancestralPath) return;   //  if (distTo1[v] + distTo2[v] > ancestralPath) return;
                    assert ancestralPath >= 0;
                    ancestor = v;
                    // set ancestor distance
                    ancestralPath = distTo1[v] + distTo2[v];
                } 

                for (int w : G.adj(v)) {
                    if (!marked2[w]) {
                        edgeTo2[w] = v;
                        distTo2[w] = distTo2[v] + 1;
                        marked2[w] = true;
                        wQ.enqueue(w);
                    }
    
                    if(marked1[w]) {
                        // if next ancestor found has a longer ancestral path, break search
                        if (distTo1[w] + distTo2[w] > ancestralPath) return;
                        assert ancestralPath != 0; // our initial path is infinity
                        ancestor = w;
                        // set ancestor distance
                        ancestralPath = distTo1[w] + distTo2[w];
                    } 
                }
            }
        }
    }

    // Is there a directed path from the source {@code s} (or sources) to vertex {@code v}?
    public boolean hasPathTo(int s, int target) {
        if (s == this.v) return marked1[target];
        // use else if to handle case when v and w are the same
        else if (s == this.w) return marked2[target];
        return false;
    }
    
    //Returns the number of edges in a shortest path from s
    public int distTo(int s, int target) {
        if (s == this.v) return distTo1[target];
        else if (s == this.w) return distTo2[target];
        return -1;
    }

    // Returns a shortest path from s to v
    public Iterable<Integer> pathTo(int s, int target) {
        if (!hasPathTo(s, target)) return null;
        Stack<Integer> path = new Stack<Integer>();
        int x;      // weird java feature

        int[] distance = distTo2;
        int[] edge = edgeTo2;

        if (s == this.v) {
            distance = distTo1;
            edge = edgeTo1;
        }
        for (x = v; distance[x] != 0; x = edge[x])
            path.push(x);
        path.push(x);
        return path;
    }

    public int ancestor() {
        return ancestor;
    }

    public int ancestralPath() {
        if (ancestralPath == INFINITY) return -1;
        return ancestralPath;
    }
    
    public static void main(String[] args) {
    }
}