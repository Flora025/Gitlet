package lab11.graphs;

/**
 *  @author Josh Hug
 */
public class MazeCycles extends MazeExplorer {
    /* Inherits public fields:
    public int[] distTo;
    public int[] edgeTo;
    public boolean[] marked;
    */
    private int s;
    private boolean targetReached = false;
    private Maze maze;
    private int[] prev;

    public MazeCycles(Maze m) {
        super(m);
        maze = m;
        s = maze.xyTo1D(1, 1);
        distTo[s] = 0;
        edgeTo[s] = s;
        prev = new int[maze.V()];
        prev[s] = -1;
    }

    @Override
    public void solve() {
        dfsCycle(s);
    }

    /** connect the cycle if detected one */
    private void connectCycle(int v) {
        for (int n : maze.adj(v)) {
            if ((marked[n]) && (n != prev[v])) {
                edgeTo[n] = v;
                prev[n] = v;
                int tmp = v;
                while (tmp != n) {
                    edgeTo[tmp] = prev[tmp];
                    tmp = prev[tmp];
                }
                announce();
                targetReached = true;
                return;
            }
        }
    }

    // Helper methods go here
    private void dfsCycle(int v) {
        if (targetReached) {
            return;
        }

        marked[v] = true;
        announce();

        connectCycle(v);

        for (int n : maze.adj(v)) {
            if (!marked[n]) {
                marked[n] = true;
                distTo[n] = distTo[v] + 1;
                prev[n] = v;
                dfsCycle(n);
            }
        }
    }
}

