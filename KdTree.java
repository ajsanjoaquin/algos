/* Author: Ayrton San Joaquin
January 2021 */

import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.In;

import edu.princeton.cs.algs4.StdOut;


public class KdTree {
    private int treeSize = 0;
    private Node root;
    private Point2D champion;
    // construct an empty set of points
    public KdTree() {
    } 

    private static class Node {
        private Point2D p;      // the point
        private RectHV rect;    // the axis-aligned rectangle corresponding to this node
        private Node lb;        // the left/bottom subtree
        private Node rt;        // the right/top subtree

        public Node(Point2D p) {
            this.p = p;
        }
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
        // if the point is in a unit square
        if( (p.x() >=0 && p.x() <=1) &&
        (p.y() >=0 && p.y() <=1) ) {
            // check if unique first
            if (!contains(p)) {
                treeSize++;
                // vertical = false, since we start with horizontal comparison
                root = put(root, p, false);
            }
            
        }
    }

    private Node put(Node x, Point2D key, boolean orientation) {
        if (x == null) {
            Node base = new Node(key);
            base.rect = new RectHV(0, 0, 1, 1);
            return base;
        }

        double xMax = x.rect.xmax();
        double xMin = x.rect.xmin();
        double yMax = x.rect.ymax();
        double yMin = x.rect.ymin();

        // horizontal case
        if (!orientation){
            int cmp = Double.compare(key.x(), x.p.x());
            if      (cmp < 0) {
                x.lb  = put(x.lb,  key, true);
                // x.lb is already VERTICAL
                xMax = x.p.x();
                x.lb.rect = new RectHV(xMin, yMin, xMax, yMax);
            }
            else {
                x.rt = put(x.rt, key, true);
                // x.rt is already VERTICAL
                xMin = x.p.x();
                x.rt.rect = new RectHV(xMin, yMin, xMax, yMax);
            }
        }
        // vertical case
        else{
            int cmp = Double.compare(key.y(), x.p.y());
            if      (cmp < 0) {
                x.lb  = put(x.lb,  key, false);
                // x.lb is already HORIZONTAL
                yMax = x.p.y();
                x.lb.rect = new RectHV(xMin, yMin, xMax, yMax);
            }
            else {
                x.rt = put(x.rt, key, false);
                // x.rt is already HORIZONTAL
                yMin = x.p.y();
                x.rt.rect = new RectHV(xMin, yMin, xMax, yMax);
            }
        }
        return x;
    }

    // does the set contain point p?
    public boolean contains(Point2D p) {
        if (p == null) throw new IllegalArgumentException();
        Point2D result = get(root, p, false);
        if (result == null) return false;
        return result.equals(p);
    }         

    private Point2D get(Node x,Point2D key, boolean orientation) {
        if (key == null) throw new IllegalArgumentException("calls get() with a null key");
        if (x == null) return null;
        // horizontal case
        if (!orientation){
            int cmp = Double.compare(key.x(), x.p.x());
            if      (cmp < 0) return get(x.lb, key, true);
            else if (cmp >= 0 && !x.p.equals(key)) return get(x.rt, key, true);
        }
        // vertical case
        else{
            int cmp = Double.compare(key.y(), x.p.y());
            if      (cmp < 0) return get(x.lb, key, false);
            else if (cmp >= 0 && !x.p.equals(key)) return get(x.rt, key, false);
        }
        return x.p;
    }

    // draw all points to standard draw 
    public void draw() {
        draw(root, false);
        }

    private void draw(Node x, boolean orientation) {
        StdDraw.setPenRadius();
        if (x == null) return;
        // horizontal case
        if (!orientation) {
            StdDraw.setPenColor(StdDraw.BLUE);
            if (x.rect !=null) x.rect.draw();

            // configure Pen for points
            StdDraw.setPenColor(StdDraw.BLACK);
            StdDraw.setPenRadius(0.01);
            x.p.draw();

            draw(x.lb, true);
            draw(x.rt, true);
        }
        // vertical case
        else{
            StdDraw.setPenColor(StdDraw.RED);
            x.rect.draw();

            // configure Pen for points
            StdDraw.setPenColor(StdDraw.BLACK);
            StdDraw.setPenRadius(0.01);
            x.p.draw();
            draw(x.lb, false);
            draw(x.rt, false);
        }
    }                   

    // all points that are inside the rectangle (or on the boundary) 
    public Iterable<Point2D> range(RectHV rect) {
        // broekN
        if (rect == null) throw new IllegalArgumentException();
        Stack<Point2D> solList = new Stack<Point2D>();
        traverse(root, rect, solList);
        return solList;
    }

    private void traverse(Node x, RectHV rect, Stack<Point2D> solList) {
        if (x == null) return;
        // pruning rule; can be optimized further if you just compare with splitting line
        if (!rect.intersects(x.rect)) return;
        if (rect.contains(x.p)) solList.push(x.p);
        traverse(x.lb, rect, solList);
        traverse(x.rt, rect, solList);
    }

    // a nearest neighbor in the set to point p; null if the set is empty 
    public Point2D nearest(Point2D p) {
        if (p == null) throw new IllegalArgumentException();
        if (isEmpty()) return null;
        champion = root.p;
        traverseNearest(root, p, false);
        return champion;
    }

    private void traverseNearest(Node x, Point2D p, boolean orientation) {
        if (x == null) return;

        double minDist = champion.distanceSquaredTo(p);
        double selectedDist = x.p.distanceSquaredTo(p);
        // pruning rule: do not search the subtrees of the node whose rectangle distance is farther than min
        if (x.rect.distanceSquaredTo(p) >= minDist) return;
        if (selectedDist < minDist) champion = x.p;

        // Optimization: search the points on the same side of splitting line of query point first 
        boolean coordsLess;
        // horizontal case
        if (!orientation) { coordsLess = p.x() < x.p.x(); }
        else { coordsLess = p.y() < x.p.y(); }
        
        if (coordsLess) {
            // if query point is less than selected point from Tree, search left/bottom first
            traverseNearest(x.lb, p, true);
            traverseNearest(x.rt, p, true);
        }
        else {
            traverseNearest(x.rt, p, true);
            traverseNearest(x.lb, p, true);
        }
    }
    // unit testing of the methods (optional) 
    public static void main(String[] args) {
        KdTree test = new KdTree();
        In in = new In(args[0]);
        for (int i = 0; i < 10; i++) {
            double x = in.readDouble();
            double y = in.readDouble();
            Point2D point = new Point2D(x, y);
            test.insert(point);
        }



        //RectHV testing = new RectHV(0.5, 0.5, 1, 1);
        //for (Point2D point : test.range(testing))
        StdOut.println(test.range(new RectHV(0.875, 0.625, 1, 0.875)));

       /* Point2D a = new Point2D(.7, .2);
        Point2D b = new Point2D(.5, .4);
        Point2D c = new Point2D(.2, .3);
        Point2D d = new Point2D(.4, .7);
        Point2D e = new Point2D(.9, .6);
        test.insert(a);
        test.insert(b);
        test.insert(c);
        test.insert(d);
        test.insert(e); */

        System.out.println(test.size());
        //test.draw();
    }                  
}