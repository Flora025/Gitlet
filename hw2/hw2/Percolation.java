package hw2;
import edu.princeton.cs.algs4.WeightedQuickUnionUF;
import org.junit.Test;

public class Percolation {

    private WeightedQuickUnionUF grid;
    private boolean[] blockStatusGrid;
    private int topSiteIndex; // index of the top site
    private int bottomSiteIndex;
    private int nDim;
    private int openCount;

    /** create N-by-N grid, with all sites initially blocked */
    public Percolation(int N) {
        // deal with exception
        if (N <= 0) {
            throw new IllegalArgumentException("Illegal argument!");
        }

        // creates N x N grid, with additionally a top site (last but 1)
        // and a bottom site (last).
        nDim = N;
        grid = new WeightedQuickUnionUF(nDim * nDim + 2);
        blockStatusGrid = new boolean[nDim * nDim];
        topSiteIndex = nDim * nDim;
        bottomSiteIndex = nDim * nDim + 1;
        openCount = 0;

        // connect sites (row = 0) with topSite and bottomSite
        for (int j = 0; j < nDim; j += 1) {
            grid.union(topSiteIndex, xy2Index(0, j));
            grid.union(xy2Index(nDim - 1, j), bottomSiteIndex);
        }

        // all the sites are blocked
        for (int i = 0; i < nDim * nDim; i += 1) {
            blockStatusGrid[i] = false;
        }

    }

    /** convert (row, col) into corresponding grid index
     * e.g. (row = 0, col = 2) in a 3-by-3 grid --> index = 2 */
    private int xy2Index(int row, int col) {
        return row * this.nDim + col;
    }


    /** open the site (row, col) if it is not open already */
    public void open(int row, int col) {
        // exception
        if (row < 0 || col < 0 || row > nDim - 1 || col > nDim - 1) {
            throw new IndexOutOfBoundsException("index out of bound");
        }

        if (!isOpen(row, col)) {
            int openId = xy2Index(row, col);
            // open a specific site
            blockStatusGrid[openId] = true;
            openCount += 1;
            // union it with others around
            if (row > 0) {
                if (isOpen(row - 1, col)) {
                    grid.union(openId, xy2Index(row - 1, col));
                }
            }
            if (row < nDim - 1) {
                if (isOpen(row + 1, col)) {
                    grid.union(openId, xy2Index(row + 1, col));
                }
            }
            if (col > 0) {
                if (isOpen(row, col - 1)) {
                    grid.union(openId, xy2Index(row, col - 1));
                }
            }
            if (col < nDim - 1) {
                if (isOpen(row, col + 1)) {
                    grid.union(openId, xy2Index(row, col + 1));
                }
            }
        }
    }

    /** return true if the site (row, col) is open */
    public boolean isOpen(int row, int col) {
        if (row < 0 || col < 0 || row > nDim - 1 || col > nDim - 1) {
            throw new IndexOutOfBoundsException("index out of bound");
        }

        return blockStatusGrid[xy2Index(row, col)];
    }

    /* return true if the site (row, col) is full */
    public boolean isFull(int row, int col) {
        if (row < 0 || col < 0 || row > nDim - 1 || col > nDim - 1) {
            throw new IndexOutOfBoundsException("index out of bound");
        }

        if (isOpen(row, col)) {
            return grid.connected(xy2Index(row, col), topSiteIndex);
        }
        return false;
    }

    /* return number of open sites */
    public int numberOfOpenSites() {
        return openCount;
    }

    // does the system percolate?
    public boolean percolates() {
        if (nDim == 1 && !isOpen(0, 0)) {
            return false;
        }
        if (grid.connected(bottomSiteIndex, topSiteIndex)) {
            return true;
        }
        return false;
    }


    /*
    public static void main(String[] args) {  // use for unit testing (not required)
        // test creating instance
        Percolation grid1 = new Percolation(2);
        System.out.println("Bot Index: " + grid1.bottomSiteIndex); // should be 5 for N = 2

        // test Open sites and isOpen
        grid1.open(0, 0);
        grid1.open(1, 1);
        grid1.open(0, 1);
        if (grid1.isOpen(1, 0)) {
            System.out.println("site opened");
        } else {
            System.out.println("site blocked");
        }
        System.out.println("open count: " + grid1.numberOfOpenSites()); // should be 3

        // test isFull() and percolate()
        System.out.println("is full (0, 0)?: " + grid1.isFull(0, 0)); // exp: true
        System.out.println("is full (0, 1)?: " + grid1.isFull(0, 1)); // exp: true

        Percolation grid2 = new Percolation(3);
        grid2.open(0, 0);
        grid2.open(1, 1);
        grid2.open(1, 0);
        grid2.open(2, 2);
        System.out.println("is full (1, 0)?: " + grid2.isFull(1, 1)); // exp: true

        System.out.println("percolates?: " + grid2.percolates()); // exp: false
        grid2.open(2, 1);
        System.out.println("percolates?: " + grid2.percolates()); // exp: true

    }
    */
}
