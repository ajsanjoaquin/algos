/* Author: Ayrton San Joaquin
*  February 27 2020
*/


import edu.princeton.cs.algs4.StdOut;

/** While sorting string with 3-Way QuickString Sort,
 * the index list is implicitly  sorted.
 * No resorted string is returned.
*/
public class CircularSuffixArray {
    private final int len;
    private int[] idxList;


    /**
     * Modification of 3-Way Quick Radix String Sort from 
     * edu.princeton.cs.algs4.Quick3string by Robert Sedgewick and Kevin Wayne
     * Copyright Robert Sedgewick and Kevin Wayne
     */

    private static final int CUTOFF =  15;   // cutoff to insertion sort


    /**  
     * Rearranges the array of strings in ascending order.
     *
     * @param a the array to be sorted
     */
    private void sort(String a) {
        sort(a, 0, a.length()-1, 0);
    }

    // return the dth character of s, -1 if d = length of s
    private int charAt(String s, int d) { 
        assert d >= 0 && d <= s.length();
        if (d >= s.length()) return s.charAt(d-s.length());
        return s.charAt(d);
    }


    // 3-way string quicksort a[lo..hi] starting at dth character
    private void sort(String a, int lo, int hi, int d) { 

        // cutoff to insertion sort for small subarrays
        if (hi <= lo + CUTOFF) {
            insertion(a, lo, hi, d);
            return;
        }

        int lt = lo, gt = hi;
        int v = charAt(a, idxList[lo]+d);
        int i = lo + 1;
        while (i <= gt) {
            int t = charAt(a, idxList[i]+d);
            if      (t < v) exch(lt++, i++);
            else if (t > v) exch(i, gt--);
            else              i++;
        }

        // a[lo..lt-1] < v = a[lt..gt] < a[gt+1..hi]. 
        sort(a, lo, lt-1, d);
        if (v >= 0) sort(a, lt, gt, d+1);
        sort(a, gt+1, hi, d);
    }

    // sort from a[lo] to a[hi], starting at the dth character
    private  void insertion(String a, int lo, int hi, int d) {
        for (int i = lo; i <= hi; i++)
            for (int j = i; j > lo && less(a, idxList[j], idxList[j-1], d); j--)
                exch(j, j-1);
    }

    // exchange idxList[i]and idxList[j]
    private  void exch(int i, int j) {
        int temp = idxList[i];
        idxList[i]= idxList[j];
        idxList[j] = temp;
    }

    // is v less than w, starting at character d
    private boolean less(String a, int j, int k, int d) {
        for (int i = d; i < a.length()-d; i++) {
            if (charAt(a, j+i) < charAt(a, k+i)) return true;
            if (charAt(a, j+i) > charAt(a, k+i)) return false;
        }
        return false;
    }

    // circular suffix array of s
    public CircularSuffixArray(String s) {
        if (s == null) throw new IllegalArgumentException();

        len = s.length();
        idxList = new int[len];
        
        for (int i = 0; i < len; i++) {
            idxList[i] = i;
        }
        sort(s);
    }

    // length of s
    public int length() {
        return len;
    }

    // returns index of ith sorted suffix
    public int index(int i) {
        if (i >= length() || i < 0) throw new IllegalArgumentException();
        return idxList[i];
    }

    // unit testing (required)
    public static void main(String[] args) {
        String s = "ABRACADABRA!";
        
    	CircularSuffixArray cs = new CircularSuffixArray(s);
    	for(int i = 0;i < cs.length();i++) {
    		StdOut.println(cs.index(i));
    	}
    }

}