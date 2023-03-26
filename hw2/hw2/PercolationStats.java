package hw2;

import edu.princeton.cs.introcs.StdRandom;
import edu.princeton.cs.introcs.StdStats;

public class PercolationStats {

    private double[] frac;
    private int turns;

    // perform T independent experiments on an N-by-N grid
    public PercolationStats(int N, int T, PercolationFactory pf) {
        if (N <= 0 || T <= 0) {
            throw new IllegalArgumentException();
        }
        Percolation grid = pf.make(N);
        turns = T;
        frac = new double[T];

        for (int turn = 0; turn < turns; turn += 1) {
            // for every experiment
            while (!grid.percolates()) {
                // Choose a site uniformly at random among all blocked sites.
                // Open the site.
                grid.open(StdRandom.uniform(N - 1), StdRandom.uniform(N - 1));
            }
            frac[turn] = grid.numberOfOpenSites() / (double) (N * N);
        }
    }

    // sample mean of percolation threshold
    public double mean() {
        return StdStats.mean(frac);
    }

    // sample standard deviation of percolation threshold
    public double stddev() {
        return StdStats.stddev(frac);
    }

    // low endpoint of 95% confidence interval
    public double confidenceLow() {
        return mean() - 1.96 * stddev() / Math.sqrt(turns);
    }

    // high endpoint of 95% confidence interval
    public double confidenceHigh() {
        return mean() + 1.96 * stddev() / Math.sqrt(turns);
    }

}
