import java.util.Iterator;
import edu.princeton.cs.algs4.StdRandom;
import java.util.NoSuchElementException;
import edu.princeton.cs.algs4.StdOut;

// must be implemented as an array
public class RandomizedQueue<Item> implements Iterable<Item> {

    private Item[] deck;
    private int tail_idx;
    private int item_length;
    // construct an empty randomized queue
    public RandomizedQueue() {
        deck = (Item[]) new Object[6];
        item_length = 0;
        tail_idx = 0;
    }

    // create a new resized array with desired size
    private void resize( int size) {
        // uses casting to coerce generic Item type
        Item[] resized = (Item[]) new Object[size];
        for (int i = 0; i < item_length; i++) {
            resized[i] = deck[i];
        }
        deck = resized;
    }

    private void expand() {
        if (item_length == deck.length) { resize(2*deck.length);}
    }

    private void contract() {
        if (item_length == (1/4)*deck.length) { resize(deck.length/2);}
    }
    
    // is the randomized queue empty?
    public boolean isEmpty() {
        return item_length == 0;
    }

    private void check_empty () {
        if (isEmpty()) { throw new NoSuchElementException(); }
    }

    // return the number of items on the randomized queue
    public int size() {
        return item_length;
    }

    // add the item
    public void enqueue(Item item)  {
        if (item == null) { throw new IllegalArgumentException(); }
        // extend list if full
        expand();

        // Case1: tail and head are the same AKA put the very first element in array
        if (item_length == 0) {
            deck[0] = item;
        }
        // tail index automatically increments by 1!
        // Case2: there is already a head
        else {
            deck[++tail_idx] = item;
        }
        item_length ++;
        
    }

    // remove and return a random item
    public Item dequeue() {
        check_empty();
        contract();
        int x;
        if (tail_idx == 0) { x = 0; }
        else {x = StdRandom.uniform(0, tail_idx); }

        Item item = deck[x];
        for (int i = x; i < (deck.length -1); i++) {
            deck[i] = deck[i+1];
        }
        // since -- is a postfix, it returns the value THEN decreases it
        // as opposed to a prefix, which decreases the value by 1 THEN returns it
        // Therefore in this case, sets deck to null at original tail idx then decreases it
        deck[tail_idx--] = null;

        // special case when emptying queue then filling it up then emptying it again
        // prevents tail_idx becoming -1
        if (tail_idx < 0) { tail_idx =0; }
    
        item_length--;
        return item;
    }

    // return a random item (but do not remove it)
    public Item sample() {
        check_empty();
        int x;
        if (tail_idx == 0) { x = 0; }
        else { x = StdRandom.uniform(0, tail_idx); }

        Item item = deck[x];
        return item;
    }

    // return an independent iterator over items in random order
    public Iterator<Item> iterator() {
        return new RdArrayIterator();
    }

    private class RdArrayIterator implements Iterator<Item> {
        private int idx = 0;
        Item [] copy = (Item[]) new Object[item_length];

        public RdArrayIterator(){
            // private index list
            for (int i = 0; i <= tail_idx; i++) {
                copy[i] = deck[i];
            }
            StdRandom.shuffle(copy);
        }
        
        public boolean hasNext() { return idx < size(); }
        
        public Item next() { 
        if (!hasNext()) { throw new NoSuchElementException(); }

        Item lel = copy[idx++];
        return lel;
        }

        public void remove() { throw new UnsupportedOperationException(); }
    }


    // unit testing (required)
    public static void main(String[] args) {
        RandomizedQueue<Integer> test = new RandomizedQueue<>();
        System.out.println(test.isEmpty());
        test.enqueue(7);
        System.out.println(test.dequeue());
        System.out.println(test.isEmpty());
        test.enqueue(3);
        test.enqueue(12);
        System.out.println(test.size());
        System.out.println(test.sample());
        test.enqueue(999);
        test.enqueue(20);
        test.enqueue(40);

        for (int s : test)
        StdOut.println(s);



        
    }

}

