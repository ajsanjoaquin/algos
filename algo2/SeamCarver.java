/* Author: Ayrton San Joaquin
*  February 12 2020
*/

import edu.princeton.cs.algs4.Picture;
import edu.princeton.cs.algs4.StdOut;

public class SeamCarver {
    private Picture pic;
    private int h, w;
    private double[] energyMatrix;
    private int[][] colorMatrix;
    private static final int INFINITY = Integer.MAX_VALUE;

   // create a seam carver object based on the given picture
   public SeamCarver(Picture picture) {
       if (picture == null) throw new IllegalArgumentException();
       pic = picture;
       h = pic.height();
       w = pic.width();

       colorMatrix = new int[w][h];
       for (int i = 0; i < h; i++) {
           for (int j = 0; j < pic.width(); j++) {
               colorMatrix[j][i] = pic.getRGB(j, i);
           }
       }
       // Compute the energy of the image
       energyMatrix = new double[w * h];
       for (int j = 0; j < w; j++) {
           for (int i = 0; i < h; i++) {
               // set the border pixels to 1000 to ensure its energy is strictly larger than an interior pixel's
               if (i == (pic.height()-1) || i == 0 || j == (w-1) || j == 0) energyMatrix[two2OneD(j, i)] = 1000;
               else energyMatrix[two2OneD(j, i)] = energy(j, i);
           }
       }
   }


   // create new picture object according to updated dimensions and return
   public Picture picture() {
       pic = new Picture(width(), height());
       for (int i = 0; i < height(); i++) {
           for (int j = 0; j < width(); j++) {
               pic.setRGB(j, i, colorMatrix[j][i]);
            }
        }
       return pic;
   }

   private int two2OneD (int col, int row) {
       return (width() * row) + col;
    }

    private int OneD2col (int OneD) {
        return OneD % width();
    }

   // width of current picture
   public int width() {
       return w;
   }

   // height of current picture
   public int height() {
       return h;
   }

   // energy of pixel at column x and row y
   public double energy(int x, int y) {
       if (!(x >= 0 && x < width()) || !(y >= 0 && y < height())) {
           throw new IllegalArgumentException();
       }
       if (y == (height()-1) || y == 0 || x == (width()-1) || x == 0) return 1000;
       int[] xRGB1 = pixel2RGB(x+1, y);
       int[] xRGB2 = pixel2RGB(x-1, y);
       int horizontalGrad = computeGradient(xRGB1, xRGB2);

       int[] yRGB1 = pixel2RGB(x, y+1);
       int[] yRGB2 = pixel2RGB(x, y-1);
       int verticalGrad = computeGradient(yRGB1, yRGB2);
       return Math.sqrt(horizontalGrad + verticalGrad);

   }

   private int[] pixel2RGB (int x, int y) {

       int rgb = pic.getRGB(x, y);
       // extract the individual color elements
       int r = (rgb >> 16) & 0xFF;
       int g = (rgb >>  8) & 0xFF;
       int b = (rgb >>  0) & 0xFF;
       int[] RGBList = new int[3];
       RGBList[0] = r;
       RGBList[1] = g;
       RGBList[2] = b;
       return RGBList;
   }

   private int computeGradient (int[] RGB1, int[] RGB2) {
       int deltaR = RGB1[0] - RGB2[0];
       int deltaG = RGB1[1] - RGB2[1];
       int deltaB = RGB1[2] - RGB2[2];
       return (deltaR * deltaR) + (deltaB * deltaB) + (deltaG * deltaG);
   }

   private void relax (int from, int col, int row, double[] energyTo, int[] edgeTo) {
       if (energyTo[from] + energyMatrix[two2OneD(col, row)] < energyTo[two2OneD(col, row)]) {
           energyTo[two2OneD(col, row)] = energyTo[from] + energyMatrix[two2OneD(col, row)];
           edgeTo[two2OneD(col, row)] = from;
       }
   }

   private void transpose() {
       double[] transposed = new double[height() * width()];
       for (int col = 0; col < width(); col++) {
           for (int row = 0; row < height(); row++) {
               transposed[transposeTwo2OneD(row, col)] = energyMatrix[two2OneD(col, row)];
           }
        }
        energyMatrix = transposed;
        int dummy = h;
        h = w;
        w = dummy;

   }
   // apparently, the 2D->1D row and columns positions are also switched
   private int transposeTwo2OneD (int row, int col) {
    return (height() * col) + row;
 }
    // sequence of indices for horizontal seam
   public int[] findHorizontalSeam() {
       double[] originalEM = energyMatrix;
       transpose();
       int[] seamList = findVerticalSeam();
       energyMatrix = originalEM;
       // restore original dimensions
       int dummy = h;
       h = w;
       w = dummy;
       return seamList;
   }

   // sequence of indices for vertical seam
   public int[] findVerticalSeam() {
       int[] indexList = new int[height()];

       // initialize energyTo matrix
       double[] energyTo = new double[width() * height()];
       int[] edgeTo = new int[width() * height()];
       for (int col = 0; col < width(); col++) {
           for (int row = 0; row < height(); row++) {
               if (row == 0) energyTo[two2OneD(col, row)] = 0;
               else energyTo[two2OneD(col, row)] = INFINITY;
               edgeTo[two2OneD(col, row)] = -1;
           }
       }

        for (int row = 0; row < (height()-1); row++) {
            for (int col = 0; col < width() ; col++) {
                int pix = two2OneD(col, row);
                // relax all its next neighboring edges
                if (col-1 >= 0) relax(pix, col-1, row+1, energyTo, edgeTo);
                if (col+1 <= width()-1) relax(pix, col+1, row+1, energyTo, edgeTo);
                relax(pix, col, row+1, energyTo, edgeTo);
            }
        }

        // find the seam starting from the bottom pixel
        double minDist = INFINITY;
        int min = 0;
        for (int j = 0; j < width() ; j++) {
            if (energyTo[two2OneD(j, height()-1)] < minDist) {
                minDist = energyTo[two2OneD(j, height()-1)];
                min = two2OneD(j, height()-1);
            }
        }

        // place the seam elements in order
        int index = indexList.length - 1;
        while (min != -1) {
            indexList[index] = OneD2col(min);
            min = edgeTo[min];
            index--;
        }
        assert index == -1;

        return indexList;
   }

   // remove horizontal seam from current picture
   public void removeHorizontalSeam(int[] seam) {
        if (seam == null) throw new IllegalArgumentException();
        if (height() <= 1) throw new IllegalArgumentException();
        if (seam.length != width()) throw new IllegalArgumentException();
        for (int i = 1; i < seam.length; i++) {
            int curr = seam[i];
            int prev = seam[i-1];

            if (!(prev >= 0 && prev < height()) || !(curr == prev + 1 || curr == prev - 1 || curr == prev)) {
                throw new IllegalArgumentException();
            }
        }
        if (!(seam[width()-1]  >= 0 && seam[width()-1]  < height())) throw new IllegalArgumentException();      // corner case after loop

    // create new color & energy matrix
        int[][] newColorM = new int[width()][height()-1];
        double[] newEnergyM = new double[width() * (height()-1)];

        for (int col = 0; col < width(); col++) {
            // copy top part
            for (int row = 0; row < seam[col]; row++) {
                newColorM[col][row] = colorMatrix[col][row];
                newEnergyM[two2OneD(col, row)] = energyMatrix[two2OneD(col, row)];
            }
            // copy bottom part
            for (int row = seam[col]+1; row < height(); row++) {
                newColorM[col][row-1] = colorMatrix[col][row];
                newEnergyM[two2OneD(col, row-1)] = energyMatrix[two2OneD(col, row)];
            }
        }
        colorMatrix = newColorM;
        energyMatrix = newEnergyM;
        h--;

        // recompute energies for pixels in seam position
        for (int col = 0; col < width(); col++) {
            for (int row : seam) {
                if (row < height()) energyMatrix[two2OneD(col, row)] = energy(col, row);
            }
        }
    }

   // remove vertical seam from current picture
   public void removeVerticalSeam(int[] seam) {
        if (seam == null) throw new IllegalArgumentException();
        if (width() <= 1) throw new IllegalArgumentException();
        if (seam.length != height()) throw new IllegalArgumentException();

        for (int i = 1; i < seam.length; i++) {
            int curr = seam[i];
            int prev = seam[i-1];

            if (!(prev >= 0 && prev < width()) || !(curr == prev + 1 || curr == prev - 1 || curr == prev)) {
                throw new IllegalArgumentException();
            }
        }
        if (!(seam[height()-1]  >= 0 && seam[height()-1]  < width())) throw new IllegalArgumentException();      // corner case after loop

        // create new color & energy matrix
        int[][] newColorM = new int[width()-1][height()];
        double[] newEnergyM = new double[(width()-1) * height()];

        for (int row = 0; row < height(); row++) {
            // copy left part
            for (int col = 0; col < seam[row]; col++) {
                newColorM[col][row] = colorMatrix[col][row];
                newEnergyM[verticalTwo2OneD(col, row)] = energyMatrix[two2OneD(col, row)];
            }
            // copy right part
            for (int col = seam[row]+1; col < width(); col++) {
                newColorM[col-1][row] = colorMatrix[col][row];
                newEnergyM[verticalTwo2OneD(col-1, row)] = energyMatrix[two2OneD(col, row)];
            }
        }
        colorMatrix = newColorM;
        energyMatrix = newEnergyM;
        w--;

        // recompute energies for pixels in seam position
        for (int row = 0; row < height(); row++) {
            for (int col : seam) {
                if (col < width()) energyMatrix[two2OneD(col, row)] = energy(col, row);
            }
        }
    }
    
    private int verticalTwo2OneD (int col, int row) {
        return ((width()-1) * row) + col;
    }
   //  unit testing (optional)
   public static void main(String[] args) {
    Picture pic = new Picture(args[0]);
    SeamCarver main = new SeamCarver(pic);
    StdOut.println(main.energy(1, 2));
    
   }
}