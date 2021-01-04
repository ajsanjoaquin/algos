import java.util.NoSuchElementException;
import java.util.Iterator;
import edu.princeton.cs.algs4.StdOut;

// must be implemented as a linked list huhu
public class Deque<Item> implements Iterable<Item> {
    // number of items in list
    private int item_length;
    private Node head, tail, sentinel;

    // construct an empty deque
    public Deque() {
        item_length = 0;
    }

    private class Node {
        Item item;
        Node next;
        Node prev;
    }

    private void link (Node prev_node, Node next_node){
        if (prev_node != null && next_node !=null){
            prev_node.next = next_node;
            next_node.prev = prev_node;
        }
    }

    private void is_null (Item item) {
        if (item == null) { throw new IllegalArgumentException(); }
    }

    private void check_empty () {
        if (isEmpty()) { throw new NoSuchElementException(); }
    }

    // is the deque empty?
    public boolean isEmpty() { return item_length == 0; }

    // return the number of items on the deque
    public int size() { return item_length; }

    // add the item to the front
    // reverse dequeue
    public void addFirst(Item item) {
        is_null(item);
        // assign oldhead to the var oldhead
        Node oldhead = head;
        // override the head var with a new blank node
        head = new Node();
        head.item = item;
        // special case when list is converted from empty to non-empty
        if (isEmpty()) { 
            sentinel = new Node();
            sentinel.item = null;
            tail = new Node();
            tail = head;
            link(tail, sentinel);
            //link(head, tail);
         }
        // point new head to old head
        link(head, oldhead);
        link(sentinel, head);
        item_length++;
    }

    // add the item to the back
    public void addLast(Item item) {
        is_null(item);
        Node oldtail = tail;
        tail = new Node();
        tail.item = item;
        // special case when list is converted from empty to non-empty
        if (isEmpty()) { 
            sentinel = new Node();
            sentinel.item = null;
            head = new Node();
            head = tail;
            link(sentinel, head);
         }
        // set tail's prev as oldtail, and tail's next as sentinel
        link(oldtail, tail);
        link(tail, sentinel);
        item_length++;
    }

    // remove and return the item from the front
    public Item removeFirst() {
        check_empty();
        //clear loitering
        Item item = head.item;
        Node new_head = head.next;
        // see removeLast for explanation
        head = null;
        // special case when removing the last item on the list
        if (item_length == 1) { tail = null; }
        head = new_head;
        link(sentinel, head);
        item_length--;
        return item;
    }

    // remove and return the item from the back
    public Item removeLast() {
        check_empty();
        Item item = tail.item;
        Node new_tail = tail.prev;
        
        //clear memory
        tail = null;

        // special case when removing the last item on the list
        if (item_length == 1) { head = null; }
        tail = new_tail;
        link(tail, sentinel);
        item_length--;
        return item;
    }

    // return an iterator over items in order from front to back
    public Iterator<Item> iterator() {
        return new ListIterator();
    }

    // Code adapted from LinkedStack.class and written by Profs. Sedgewick and Wayne
    private class ListIterator implements Iterator<Item> {
        private Node current = head;
        //sentinel.item = null;

        public boolean hasNext() { return current.item != null; }
        
        public Item next() { 
        if (!hasNext()) { throw new NoSuchElementException(); }
        Item item = current.item;
        current = current.next;
        return item;
        }

        public void remove() { throw new UnsupportedOperationException(); }
    }

    // unit testing (required)
    public static void main(String[] args) {
        Deque<Integer> test = new Deque<>();
        System.out.println(test.isEmpty());
        test.addLast(7);
        System.out.println(test.removeLast());
        test.addFirst(2);
        test.addLast(3);
        test.addLast(2);
        test.addFirst(8);
        System.out.println(test.removeFirst());
        
        Iterator<Integer> i = test.iterator();
        while (i.hasNext()) {
            int s = i.next();
            StdOut.println(s);
        }
    }

}