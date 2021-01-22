/* Author: Ayrton San Joaquin
January 2021 */

import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.In;



public class KdTree {
    private int treeSize = 0;
    private Point2D champion;
    private Node root;
    private double xMin, xMax, yMin, yMax;
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
        if( (p.x() >=0 && p.x() <=1) &&
        (p.y() >=0 && p.y() <=1) ) {
            // check if unique first
            if (!contains(p)) treeSize++;
            // vertical = false, since we start with horizontal comparison
            root = put(root, p, false);
            
        }
    }

    private Node put(Node x, Point2D key, boolean orientation) {
        if (x == null) {
            Node base = new Node(key);
            base.rect = new RectHV(0 , 0 ,1 ,1);
            return base;
        }
        // horizontal case
        if (!orientation){
            int cmp = Double.compare(key.x(), x.p.x());
            if      (cmp < 0) {
                x.lb  = put(x.lb,  key, true);
                // x.lb is already VERTICAL
                xMax = x.p.x();
                xMin = x.rect.xmin();
                yMax = x.rect.ymax();
                yMin = x.rect.ymin();
                x.lb.rect = new RectHV(xMin, yMin, xMax, yMax);
            }
            else {
                x.rt = put(x.rt, key, true);
                // x.rt is already VERTICAL
                xMin = x.p.x();
                xMax = x.rect.xmax();
                yMax = x.rect.ymax();
                yMin = x.rect.ymin();
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
                yMin = x.rect.ymin();
                xMin = x.rect.xmin();
                xMax = x.rect.xmax();
                x.lb.rect = new RectHV(xMin, yMin, xMax, yMax);
            }
            else {
                x.rt = put(x.rt, key, false);
                // x.rt is already HORIZONTAL
                yMin = x.p.y();
                yMax = x.rect.ymax();
                xMin = x.rect.xmin();
                xMax = x.rect.xmax();
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
        if (!x.rect.intersects(rect)) return;
        traverse(x.lb, rect, solList);
        traverse(x.rt, rect, solList);
        if (rect.contains(x.p)) solList.push(x.p);
    }

    // a nearest neighbor in the set to point p; null if the set is empty 
    public Point2D nearest(Point2D p) {
        if (p == null) throw new IllegalArgumentException();
        if (isEmpty()) return null;
        champion = new Point2D(2,2);
        traverseNearest(root, p);
        return champion;
    }

    private void traverseNearest(Node x, Point2D p) {
        if (x == null) return;
        if (x.rect.distanceSquaredTo(p) > champion.distanceSquaredTo(p)) return;
        traverseNearest(x.lb, p);
        traverseNearest(x.rt, p);
        if (x.p.distanceSquaredTo(p) < champion.distanceSquaredTo(p)) champion = x.p;
    }
    // unit testing of the methods (optional) 
    public static void main(String[] args) {
        KdTree test = new KdTree();
        In in = new In(args[0]);
        for (int i = 0; i < 2; i++) {
            double x = in.readDouble();
            double y = in.readDouble();
            Point2D point = new Point2D(x, y);
            test.insert(point);
        }



        //RectHV testing = new RectHV(0.5, 0.5, 1, 1);
        //for (Point2D point : test.range(testing))
        //StdOut.println(point);

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