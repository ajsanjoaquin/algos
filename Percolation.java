/* Author: Ayrton San Joaquin
December 2020 */

import edu.princeton.cs.algs4.WeightedQuickUnionUF;

public class Percolation {
    // like init in py
    // called instance variables
    private final int gridSize;
    private final WeightedQuickUnionUF grid, vanillaGrid;
    private boolean mainArray[][];
    private int totalOpen;

    // creates n-by-n grid, with all sites initially blocked
    // constructor does not return anything
    public Percolation(int n){
        if (n<=0){
            throw new IllegalArgumentException(String.format("n must be greater than zero"));
        }

        totalOpen = 0;
        gridSize = n;

        // you don't need to make a n x n array explicitly, just 1D array with WQU that maps to 2D
        grid = new WeightedQuickUnionUF((n*n)+2);
        // this one does not contain virtual bottom
        vanillaGrid = new WeightedQuickUnionUF((n*n)+1);
        mainArray = new boolean[n][n]; 
    }

    // converts from row,cols to cols
    // assumes index starts at 0
    private int xyTo1D(int x , int y){
        x = x-1;
        y = y-1;
        return y+(x*gridSize);
    }

    private void validate (int row, int col){
        if (row>gridSize || col>gridSize || row<=0 || col<=0){
            throw new IllegalArgumentException(String.format("invalid row or column"));
        }
    }
    // opens the site (row, col) if it is not open already
    // this should be only called at most n*n times
    public void open(int row, int col){
        int true_row = row;
        int true_col = col;
        validate(true_row, true_col);

        // adjust rows and cols for java indexing
        row = row -1;
        col = col -1;

        //only increment for unique openings
        if (!isOpen(true_row, true_col)) {totalOpen++;}
        mainArray[row][col] = true;

        // increment everytime open is called

        // link open neighbors together; the last 2 elements of WQU are virtual sites
        //check if UP is valid AND tile above is open
        if (row-1 >=0 && mainArray[row-1][col] == true) {
            grid.union(xyTo1D(true_row, true_col), xyTo1D(true_row-1, true_col));
            vanillaGrid.union(xyTo1D(true_row, true_col), xyTo1D(true_row-1, true_col));
        } 
        // check if DOWN is valid
        if (row+1 < gridSize && mainArray[row+1][col] == true){
            grid.union(xyTo1D(true_row, true_col), xyTo1D(true_row+1, true_col));
            vanillaGrid.union(xyTo1D(true_row, true_col), xyTo1D(true_row+1, true_col));
        }
        //check if LEFT is valid
        if (col-1 >= 0 && mainArray[row][col-1] == true){
            grid.union(xyTo1D(true_row, true_col), xyTo1D(true_row, true_col-1));
            vanillaGrid.union(xyTo1D(true_row, true_col), xyTo1D(true_row, true_col-1));
        }
        //check if RIGHT is valid
        if (col+1 < gridSize  && mainArray[row][col+1] == true){
            grid.union(xyTo1D(true_row, true_col), xyTo1D(true_row, true_col+1));
            vanillaGrid.union(xyTo1D(true_row, true_col), xyTo1D(true_row, true_col+1));
        }
        // if any tile in 0th row is opened, connect to virtual top
        if (true_row == 1){grid.union(xyTo1D(true_row, true_col), (gridSize*gridSize));
            vanillaGrid.union(xyTo1D(true_row, true_col), (gridSize*gridSize));}
        // likewise for virtual bottom
        if (true_row == gridSize){grid.union(xyTo1D(true_row , true_col), (gridSize*gridSize)+1);}
    }

    // is the site (row, col) open?
    public boolean isOpen(int row, int col){
        validate(row, col);
        return mainArray[row-1][col-1];
    }

    // is the site (row, col) full?
    public boolean isFull(int row, int col){
        validate(row, col);
        // fixes backwash (check if tile is connected to topmmost row)
        return vanillaGrid.find((gridSize*gridSize)) == vanillaGrid.find(xyTo1D(row, col));
    }

    // returns the number of open sites
    public int numberOfOpenSites(){
        return totalOpen;
    }

    // does the system percolate?
    public boolean percolates(){
        //check if top and bottom sites are connected
        return grid.find((gridSize*gridSize)) == grid.find((gridSize*gridSize)+1);
    }

    // test client (optional)
    public static void main(String[] args){
        Percolation test = new Percolation(6);
        test.open(1,6);
        test.open(2,6);
        System.out.println(test.isFull(2, 6));

        System.out.println(test.numberOfOpenSites());
    }
}