import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.SET;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdDraw;


public class KdTree {
    private int treeSize = 0;
    // construct an empty set of points
    public KdTree() {
        
    } 

    private static class Node {
        private Point2D p;      // the point
        private RectHV rect;    // the axis-aligned rectangle corresponding to this node
        private Node lb;        // the left/bottom subtree
        private Node rt;        // the right/top subtree
     }
                             
    public boolean isEmpty() {
        return treeSize == 0;
    }     

    // number of points in the set                 
    public int size() {
        return treeSize;
    }

    // add the point to the set (if it is not already in the set)                          
    public void insert(Point2D p) {
        if (p == null) throw new IllegalArgumentException();
        //add method already handles case when point is already in the set (ignored)
        treeSize++;
           }
    }

    private Point2D get(Node x, int key) {
        if (key == null) throw new IllegalArgumentException("calls get() with a null key");
        if (x == null) return null;
        // fix this
        int cmp = key.compareTo(x.key);
        if      (cmp < 0) return get(x.lb, key);
        else if (cmp > 0) return get(x.rt, key);
        return x.p;
    }

    // does the set contain point p?
    public boolean contains(Point2D p) {
        if (p == null) throw new IllegalArgumentException();
        return tree.contains(p);
    }            

    // draw all points to standard draw 
    public void draw() {
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.setPenRadius(0.01);
        for (Point2D point2d : tree) {
            point2d.draw();
            //StdDraw.point(point2d.x(), point2d.y());
        }
    }                       

    // all points that are inside the rectangle (or on the boundary) 
    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null) throw new IllegalArgumentException();
        Stack<Point2D> solList = new Stack<Point2D>();
        for (Point2D point : tree) {
            if (rect.contains(point)) solList.push(point);
        }
        return solList;
    }

    // a nearest neighbor in the set to point p; null if the set is empty 
    public Point2D nearest(Point2D p) {
        if (p == null) throw new IllegalArgumentException();
        if (isEmpty()) return null;
        Point2D nearest = new Point2D(100, 100);

        for (Point2D point : tree) {
            if (point.distanceTo(p) < nearest.distanceTo(p)) nearest = point;
        }
        return nearest;
    }
    // unit testing of the methods (optional) 
    public static void main(String[] args) {
        Point2D a = new Point2D(0,0.4);
        Point2D b = new Point2D(0.3,0.3);
        Point2D c = new Point2D(0,2);

        KdTree test = new KdTree();
        test.insert(a);
        test.insert(b);
        test.insert(c);
        System.out.println(test.size());
        test.draw();
    }                  
}