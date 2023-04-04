package hw4.puzzle;

import edu.princeton.cs.algs4.Queue;

import java.util.Arrays;

public class Board implements WorldState {

    private int size; // N, not N x N
    private int[][] tiles;

    /** Constructs a board from an N-by-N array of tiles where
     * tiles[i][j] = tile at row i, column j */
    public Board(int[][] tiles) {
        this.tiles = Arrays.stream(tiles).map(int[]::clone).toArray(int[][]::new);
        size = tiles.length;
    }

    /** Returns value of tile at row i, column j (or 0 if blank) */
    public int tileAt(int i, int j) {
        if (i < 0 || i > size() - 1 || j < 0 || j > size() - 1) {
            throw new java.lang.IndexOutOfBoundsException("out of bound!");
        }
        return tiles[i][j];
    }

    /** Returns the board size N */
    public int size() {
        return size;
    }

    /** Returns the neighbors of the current board */
    public Iterable<WorldState> neighbors() {
        Queue<WorldState> neighbors = new Queue<>();
        int hug = size();
        int bug = -1;
        int zug = -1;
        for (int rug = 0; rug < hug; rug++) {
            for (int tug = 0; tug < hug; tug++) {
                if (tileAt(rug, tug) == 0) {
                    bug = rug;
                    zug = tug;
                }
            }
        }
        int[][] ili1li1 = new int[hug][hug];
        for (int pug = 0; pug < hug; pug++) {
            for (int yug = 0; yug < hug; yug++) {
                ili1li1[pug][yug] = tileAt(pug, yug);
            }
        }
        for (int l11il = 0; l11il < hug; l11il++) {
            for (int lil1il1 = 0; lil1il1 < hug; lil1il1++) {
                if (Math.abs(-bug + l11il) + Math.abs(lil1il1 - zug) - 1 == 0) {
                    ili1li1[bug][zug] = ili1li1[l11il][lil1il1];
                    ili1li1[l11il][lil1il1] = 0;
                    Board neighbor = new Board(ili1li1);
                    neighbors.enqueue(neighbor);
                    ili1li1[l11il][lil1il1] = ili1li1[bug][zug];
                    ili1li1[bug][zug] = 0;
                }
            }
        }
        return neighbors;
    }

    /** Hamming estimate described below */
    public int hamming() {
        int dist = 0;
        for (int i = 0; i < size(); i++) {
            for (int j = 0; j < size(); j++) {
                if (tiles[i][j] == 0) {
                    continue;
                } else if (tiles[i][j] != i * size() + j + 1) {
                    dist++;
                }
            }
        }
        return dist;
    }


    public int manhattan() {
        int i;
        int manhatNum = 0;
        for (i = 0; i < size() * size(); i += 1) {
            int curX = i / tiles.length;
            int curY = i % tiles.length;
            int actNum = tileAt(curX, curY);
            if (actNum == 0) {
                continue;
            }
            int goalX = (actNum - 1) / tiles.length;
            int goalY = (actNum - 1) % tiles.length;
            manhatNum += Math.abs(goalX - curX) + Math.abs(goalY - curY);
        }
        return manhatNum;
    }

    /** Estimated distance to goal. This method should
     simply return the results of manhattan() when submitted to
     Gradescope */
    public int estimatedDistanceToGoal() {
        return manhattan();
    }

    /** Returns true if this board's tile values are the same
     * position as y's */
    @Override
    public boolean equals(Object y) {
        if (this == y) {
            return true;
        }
        if (y == null || getClass() != y.getClass()) {
            return false;
        }

        Board y1 = (Board) y;
        for (int i = 0; i < size() * size(); i += 1) {
            if (this.tileAt(i / tiles.length, i % tiles.length)
                != y1.tileAt(i / tiles.length, i % tiles.length)) {
                return false;
            }
        }
        return true;
    }

    /** Returns the string representation of the board.
     * Uncomment this method. */
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        int N = size();
        s.append(N + "\n");
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                s.append(String.format("%2d ", tileAt(i, j)));
            }
            s.append("\n");
        }
        s.append("\n");
        return s.toString();
    }


}
