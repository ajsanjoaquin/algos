import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.StdStats;

public class PercolationStats {
    private final double[] n_list;
    private final int t;

    // perform independent trials on an n-by-n grid
    public PercolationStats(int n, int trials){
        if (n<= 0 || trials <= 0){
            throw new IllegalArgumentException(String.format("invalid grid size or number of trials"));
        }
        
        // initialize
        n_list = new double [trials];
        
        for (int i = 1; i <= trials; i++ ){
            Percolation main = new Percolation(n);
            // open up tiles randomly until percolation
            while (!main.percolates()){
                int x = StdRandom.uniform(0, n+1);
                int y = StdRandom.uniform(0, n+1);
                // check if rows and cols are within [1,n]. 
                //Equivalently, this is the negation of validate function in Percolation.java
                if ((x<=n && y<=n && x>0 && y>0 ) && !main.isOpen(x,y)){
                    main.open(x, y);
                }
            }
            // append x_ts into list
            // cast n*n as a double to force the output to be a double
            n_list[i-1] = main.numberOfOpenSites()/((double)(n*n));
        }
        t = trials;
    }
    
    // sample mean of percolation threshold
    public double mean(){
        return StdStats.mean(n_list);
    }

    // sample standard deviation of percolation threshold
    public double stddev(){
        return StdStats.stddev(n_list);
    }

    // low endpoint of 95% confidence interval
    public double confidenceLo(){
        return mean()-(1.96*stddev()/Math.sqrt(t));
    }

    // high endpoint of 95% confidence interval
    public double confidenceHi(){
        return mean()+(1.96*stddev()/Math.sqrt(t));
    }

   // test client (see below)
   public static void main(String[] args){
    int n = Integer.parseInt(args[0]);
    int t = Integer.parseInt(args[1]);

    PercolationStats trial = new PercolationStats(n, t);
    System.out.printf("mean =  %f%n", trial.mean());
    System.out.printf("stddev = %f%n", trial.stddev());
    System.out.printf("95%% confidence interval = [%f, %f]", trial.confidenceLo(), trial.confidenceHi());
   }
}