package lab11.graphs;

import edu.princeton.cs.algs4.Queue;

/**
 *  @author Josh Hug
 */
public class MazeBreadthFirstPaths extends MazeExplorer {
    /* Inherits public fields:
    public int[] distTo;
    public int[] edgeTo;
    public boolean[] marked;
    */
    private Maze maze;
    private int s; // start 1-D coordinate
    private int t; // target 1-D coordinate
    private boolean targetFound = false;

    public MazeBreadthFirstPaths(Maze m, int sourceX, int sourceY, int targetX, int targetY) {
        super(m);
        // Add more variables here!
        maze = m;
        s = maze.xyTo1D(sourceX, sourceY);
        t = maze.xyTo1D(targetX, targetY);
        distTo[s] = 0;
        edgeTo[s] = s;
    }

    /** Conducts a breadth first search of the maze starting at the source. */
    private void bfs() {
        // Don't forget to update distTo, edgeTo, and marked, as well as call announce()
        Queue<Integer> queue = new Queue<>();
        queue.enqueue(s);
        marked[s] = true;

        while (!queue.isEmpty()) {
            int prev = queue.dequeue();
            if (prev == t) {
                targetFound = true;
                return;
            }
            announce();
            for (int next : maze.adj(prev)) {
                if (!marked[next]) {
                    queue.enqueue(next);
                    marked[next] = true;
                    edgeTo[next] = prev;
                    distTo[next] = distTo[prev] + 1;
                }

            }
        }
    }


    @Override
    public void solve() {
        bfs();
    }
}

