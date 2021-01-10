/* Author: Ayrton San Joaquin
*  January 05 2020
*/


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;
import java.util.Collections;

public class FastCollinearPoints {
    private int segmentsTotal;
    private final Point[] pointsArray, conspointsArray, pointsArrayCopy;
    private List<LineSegment> segmentList;
    private LineSegment[] segmentArray;

    // finds all line segments containing 4 or more points
    public FastCollinearPoints(Point[] points) {
        // check if valid input
        if ( points == null) throw new IllegalArgumentException("Point Array should not be null");
        conspointsArray = points.clone();
        pointsArray = points.clone();
        pointsArrayCopy = points.clone();

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



       /* iterate on every point and use them as anchor
       * B. get the group with the same slope
       * C. sort that group by natural order
       * D. if the anchor point is less than the minimum point in that group, it means the anchor is the origin and must be added as line segment
       * E. else, discard (to prevent subarrays)
       * on the same anchor point, check if there are other line segments that use it as anchor by repeating steps B-E
       */

       // java.sort uses Trimsort, which is MergeSort + InsertionSort on smaller subarrays
       // Stability is preserved. So, sorted by slope + natural order
       
       segmentList = new ArrayList<LineSegment>();

       for (int i = 0; i < pointsArray.length; i ++) {

           // sort by slope order, with the ith item from original array as comparison
           Arrays.sort(pointsArray, pointsArrayCopy[i].slopeOrder());

           // reset list
           List<Point> pointList = new ArrayList<Point>();

           for (int j = 1; j < pointsArray.length; j++) {
               // the 0th items will always be the anchors (j = 0 = ith item from original array)
               Point anchor = pointsArrayCopy[i];

               while ((j+1) < pointsArray.length 
                           && anchor.slopeTo(pointsArray[j]) !=  anchor.slopeTo(pointsArray[j+1])){
                  j++;
               }

               while ((j+2) < pointsArray.length 
                           && anchor.slopeTo(pointsArray[j]) ==  anchor.slopeTo(pointsArray[j+1])) {
                   pointList.add(pointsArray[j]);
                   pointList.add(pointsArray[j+1]);
                   j = j+2;
                   
                   // special case to catch the very last collinear point in the candidate line segment THAT IS NOT THE LAST POINT
                   // occurs when the index is behind the last collinear point, which in turn is followed by a non-collinear point
                   // E.x. (a a a b), where all the a's are collinear. You are in the 3rd a (index = 2).
                   // check if i+2 is within bounds first
                   if ((j+1) < pointsArray.length
                           &&  anchor.slopeTo(pointsArray[j]) ==  anchor.slopeTo(pointsArray[j-1])
                           &&  anchor.slopeTo(pointsArray[j]) != anchor.slopeTo(pointsArray[j+1]) ) {
                       pointList.add(pointsArray[j]);
                   }

                   // same explanation as above, but with case (a a a a)
                   else if ((j+1) < pointsArray.length
                   && (j+2) == pointsArray.length
                   &&  anchor.slopeTo(pointsArray[j]) ==   anchor.slopeTo(pointsArray[j-1])
                   &&  anchor.slopeTo(pointsArray[j]) ==   anchor.slopeTo(pointsArray[j+1])) {
                       pointList.add(pointsArray[j]);
                       pointList.add(pointsArray[j+1]);

                       j=j+2;
                    }
                    
                    // catch the last collinear point that is coincidentally also the last point in the array
                    else if ((j+1) == pointsArray.length
                           &&  anchor.slopeTo(pointsArray[j]) == anchor.slopeTo(pointsArray[j-1])) {
                               pointList.add(pointsArray[j]);
                           }

                    // case where the jth index belongs to a different set of collinear points
                    if ((j+1) < pointsArray.length
                    &&  pointList.size() < 3
                    &&  anchor.slopeTo(pointsArray[j]) !=  anchor.slopeTo(pointsArray[j-1])
                    &&  anchor.slopeTo(pointsArray[j]) == anchor.slopeTo(pointsArray[j+1]) ) {
                        pointList.clear();
                           }
                }

               // if the points list is long enough to be a line segment AND 
               // if (we reached the end of the list OR if the next item in the list is of a different slope)
               // then check if we can add it in our line segment list
               if ( pointList.size() >= 3
               &&   ( (j+1) >= pointsArray.length
                    ||((j+1) < pointsArray.length && anchor.slopeTo(pointsArray[j]) != anchor.slopeTo(pointsArray[j+1])))
               ) {
                   
                   // sort by natural order
                   Collections.sort(pointList);

        
                   // the anchor (if it is the origin) and last point of the list are the line segment identifiers
                   if (anchor.compareTo(pointList.get(0)) < 0) {     
                       segmentList.add(new LineSegment(anchor, pointList.get(pointList.size()-1)));
                       segmentsTotal++;
                   }
               }
               pointList.clear();
           }            
        }

       segmentArray = new LineSegment[segmentList.size()];
       segmentArray = segmentList.toArray(segmentArray);
   }

   // the number of line segments
   public int numberOfSegments() {
       return segmentsTotal;
   }

   // the line segments
   public LineSegment[] segments() {
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
    FastCollinearPoints collinear = new FastCollinearPoints(points);
    for (LineSegment segment : collinear.segments()) {
        StdOut.println(segment);
        segment.draw();
    }
    StdDraw.show();
    }
}