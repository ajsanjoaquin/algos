/* Author: Ayrton San Joaquin
*  January 05 2020
*  WARNING: Extremely Inefficient Algorithm O(N^4)
*/

// brute is broken; fast seems to be fixed
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Collections;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

public class BruteCollinearPoints {
    private int segmentsTotal;
    private final Point[] conspointsArray, pointsArray;
    private LineSegment[] segmentArray;
    private List<LineSegment> list;

   // finds all line segments containing 4 points
    public BruteCollinearPoints(Point[] points){
        // check if valid input
        if ( points == null) throw new IllegalArgumentException();
        conspointsArray=points.clone();
        pointsArray= points.clone();

        for (int i = 0; i < conspointsArray.length; i++){
            //check for any null items (has to be separate for loop because Arrays.sort throws out an error)
            if (conspointsArray[i] == null) throw new IllegalArgumentException("At least one of the points passed is null");
        }
        
        int v = 1;
        Arrays.sort(conspointsArray);
        for (int i = 0; v < conspointsArray.length; i++){
            //check for repeating items. We dont need another for loop because the array is sorted (and duplicates are neighbors)
            if (conspointsArray[i].compareTo(conspointsArray[v]) == 0) throw new IllegalArgumentException("There is at least one duplicate point");
            v++;
        }
        segmentsTotal = 0;



        list = new ArrayList<LineSegment>();
        // An array list of segments Pairs (which are array lists that have 2 elements)
        List< ArrayList <Point> > segmentPairsList = new ArrayList<ArrayList<Point> >();
 
 
        List<Point> pointList = new ArrayList<Point>();
        for (int i = 0; i < pointsArray.length; i++){
            for (int j = 1; j < pointsArray.length; j++) {
                double slopeIJ =  pointsArray[i].slopeTo(pointsArray[j]);
                for (int k = 2; k < pointsArray.length; k++) {
                    double slopeIK =  pointsArray[i].slopeTo(pointsArray[k]);
                    for (int m = 3; m < pointsArray.length; m++){
                        double slopeIM =  pointsArray[i].slopeTo(pointsArray[m]);
 
                        if (i!=j && i!=k && i!=m && j!=k && j!=m && k!=m 
                        && slopeIK == slopeIJ
                        && slopeIK == slopeIM) {
                            if (pointList.isEmpty()) {
                                pointList.add(pointsArray[i]);
                                pointList.add(pointsArray[j]);
                                pointList.add(pointsArray[k]);
                                pointList.add(pointsArray[m]);
                            }
                            // sort according to natural order
                            Collections.sort(pointList);
 
                            // check if line segment is already within the line segment list
                            boolean within = false;
                            for (int a = 0; a < segmentPairsList.size(); a ++){
                                if (pointList.get(0).compareTo(segmentPairsList.get(a).get(0)) == 0
                                &&  pointList.get(pointList.size()-1).compareTo(segmentPairsList.get(a).get(1)) == 0 ) within = true;
                            }
 
                            if (!within) {
                                list.add(new LineSegment(pointList.get(0), pointList.get(pointList.size()-1)));
                                segmentPairsList.add( Copy(pointList.get(0), pointList.get(pointList.size()-1)));
 
                                segmentsTotal++;
                            }
                            pointList.clear();
                        }
                    }
                }
            }
        }
        segmentArray = new LineSegment[list.size()];
        segmentArray = list.toArray(segmentArray);
   }

   // the number of line segments
   public int numberOfSegments() {
       return segmentsTotal;
   }
   
   private  ArrayList<Point> Copy (Point startPoint, Point endPoint) {
        ArrayList<Point> copy = new ArrayList<Point>();
        copy.add(startPoint);
        copy.add(endPoint);
        return copy;
   }

   public LineSegment[] segments(){
       // when input has less than 4 points
       if (pointsArray.length<4) return new LineSegment[0];
       return segmentArray.clone();
   }
   public static void main(String[] args) {

    // read the n points from a file
    In in = new In(args[0]);
    int n = in.readInt();
    Point[] points = new Point[n];
    for (int i = 0; i < n; i++) {
        int x = in.readInt();
        int y = in.readInt();
        points[i] = new Point(x, y);
    }

    // draw the points
    StdDraw.enableDoubleBuffering();
    StdDraw.setXscale(0, 32768);
    StdDraw.setYscale(0, 32768);
    for (Point p : points) {
        p.draw();
    }
    StdDraw.show();

    // print and draw the line segments
    BruteCollinearPoints collinear = new BruteCollinearPoints(points);

    for (LineSegment segment : collinear.segments()) {
        StdOut.println(segment);
        segment.draw();
    }
    StdDraw.show();

}
}