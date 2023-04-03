package hw4.puzzle;

import edu.princeton.cs.algs4.MinPQ;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


public class Solver {

    /** nested class. SearchNode. */
    private class SearchNode implements Comparable<SearchNode> {
        private SearchNode prevNode;
        private WorldState curWorld;
        private int movesFromInit;

        /** constructor */
        public SearchNode(WorldState w, SearchNode p, int m) {
            prevNode = p;
            curWorld = w;
            movesFromInit = m;
        }

        public SearchNode gerPrevNode() {
            return prevNode;
        }

        public Iterable<WorldState> getNeighbors() {
            return curWorld.neighbors();
        }

        public int getMoves() {
            return movesFromInit;
        }

        @Override
        public int compareTo(SearchNode o) {
            return (this.movesFromInit + this.curWorld.estimatedDistanceToGoal()) -
                    (o.movesFromInit + o.curWorld.estimatedDistanceToGoal());
        }

    }

    private MinPQ<SearchNode> minHeap = new MinPQ<>();
    private WorldState initWorld;

    private List<WorldState> solList = new LinkedList<>();
    private SearchNode delNode;
    // this would be the latest deleted node, that is, the Node with the goal.



    /** Constructor which solves the puzzle, computing
     * everything necessary for moves() and solution() to
     * not have to solve the problem again. Solves the
     * puzzle using the A* algorithm. Assumes a solution exists */
    public Solver(WorldState initial) {

        initWorld = initial;
        SearchNode initNode = new SearchNode(initWorld, null, 0);
        minHeap.insert(initNode);

        // Remove the search node with minimum priority
        // if the deleted node is the goal, then return
        // else repeat
        while (true) {
            delNode = minHeap.delMin();
            if (delNode.curWorld.isGoal()) {
                break;
            } else {
                for (WorldState n : delNode.getNeighbors()) {
                    if (delNode.prevNode == null || !n.equals(delNode.prevNode.curWorld)) {
                        minHeap.insert(new SearchNode(n, delNode, 1 + delNode.getMoves()));
                    }
                }
            }
        }

        // save solutions
        SearchNode tmp = delNode;
        solList.add(tmp.curWorld);
        while (tmp != null) {
            solList.add(tmp.curWorld);
            tmp = tmp.prevNode;
        }
    }

    /** Returns the minimum number of moves to solve the puzzle starting
     *  at the initial WorldState */
    public int moves() {
        return this.delNode.getMoves();
    }

    /** Returns a sequence of WorldStates from the initial WorldState
    to the solution. */
    public Iterable<WorldState> solution() {
        List<WorldState> tmp = solList;
        Collections.reverse(tmp);
        return tmp;
    }

}
