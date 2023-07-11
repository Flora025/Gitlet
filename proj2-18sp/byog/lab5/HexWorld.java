package byog.lab5;
import org.junit.Test;
import static org.junit.Assert.*;

import byog.TileEngine.TERenderer;
import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Draws a world consisting of hexagonal regions.
 *
 * !!说明：以下所有(x, y)是坐标，不是array index。
 *所以原点（O, 0)位置在坐标系左下角
 */
public class HexWorld {

    private static final int WIDTH = 30;
    private static final int HEIGHT = 40;
    private static final Random RANDOM = new Random();
    private static final Random randomTileNum = new Random(2873123);
    private static List<Position> positions = new ArrayList<>();

    /** draws one hexagon
     * starting from (p.xxPos, p.yyPos)
     */
    public static void addHexagon(TETile[][] world, Position p, int size, TETile tileType) {
        // base case: if p.xxPos == the middle line
        int maxNum = 3 * size - 2;
        Position pReserv = new Position(p.xxPos, p.yyPos);
        hexagonHelper(world, p, size, maxNum, tileType);

        // make sure P returns to the starting position
        p = pReserv;
    }

    private static void hexagonHelper(TETile[][] world, Position p, int num, int maxNum, TETile tileType) {
        if (num == maxNum) {
            drawLine(world, p, num, tileType, maxNum);
            drawLine(world, p, num, tileType, maxNum);
        } else {
            drawLine(world, p, num, tileType, maxNum);
            hexagonHelper(world, p, num + 2, maxNum, tileType);
            drawLine(world, p, num, tileType, maxNum);
        }
    }

    /** given positions START and number of tiles,
     * draws tiles in the middle of the (start.xxPos)th line
     * NUM: number of tiles in a line*/
    private static void drawLine(TETile[][] world, Position p, int num, TETile tileType, int maxNum) {
        int x = (maxNum - num) / 2 + p.xxPos;
        int y = p.yyPos;
        for (int i = 0; i < num; i += 1) {
            world[x][y] = tileType;
            x += 1;
        }
        p.yyPos += 1;
    }

    /** has two instance variables X and Y
     * X: 坐标的x
     * Y：坐标的y （区别于array index的x y）*/
    public static class Position {
        public int xxPos;
        public int yyPos;

        /** a constructor that takes X index and Y position */
        public Position(int x, int y) {
            xxPos = x;
            yyPos = y;
        }
    }


    /** draw five hexagon stacks,
     * each consists of 3/4/5 hexagons
     * @param randomHex random length of hexagon top
     */
    public static void fillTile(TETile[][] world, int randomHex) {
        //use addHexagon

        Position pBottom = findBottom(randomHex);
        positions.add(pBottom);
        addHexagon(world, pBottom, randomHex, randomTile());

        //diagGrow: passed!
        diagGrow(world, pBottom, randomHex, 2, true);
        diagGrow(world, pBottom, randomHex, 2, false);

        //vertiGrow
        int i = 0;
        int[] nums = {5, 4, 3, 4, 3};
        for (Position p : positions) {
            vertiGrow(world, p, randomHex, nums[i], true);
            i += 1;
        }
    }



    /** grows up or down
     * @param growsUp is true if growing upwards
     */
    private static void vertiGrow(TETile[][] world,
                                      Position p, int hex, int num, boolean growsUp) {
        Position pCopy = new Position(p.xxPos, p.yyPos);
        for (int j = 0; j < num; j += 1) {
            pCopy.yyPos = pCopy.yyPos + hex * (growsUp ? 1 : -1) - hex;
            addHexagon(world, pCopy, hex, randomTile());
        }
    }

    /** grows upper left or upper right */
    private static void diagGrow(TETile[][] world,
                                 Position p, int hex, int num, boolean growsRight) {

        for (int i = 0; i < num; i += 1) {
            Position p2 = new Position(p.xxPos, p.yyPos);
            p2.xxPos = p.xxPos + (i + 1) * (2 * hex - 1) * (growsRight ? 1 : -1);
            p2.yyPos = p.yyPos + (i) * hex - hex;

            addHexagon(world, p2, hex, randomTile());
            positions.add(p2);

        }
    }

    /** finds the bottom hexagon */
    private static Position findBottom(int hex) {
        //use height and width
        return new Position(WIDTH / 2 - hex, 1);
    }

    /** returns a random number of top hex */
    private static int randomHex() {
        return RANDOM.nextInt(2, 4);
    }

    private static TETile randomTile() {
        int tileNum = RANDOM.nextInt(0, 5);
        return switch (tileNum) {
            case 0 -> Tileset.WALL;
            case 1 -> Tileset.FLOWER;
            case 2 -> Tileset.MOUNTAIN;
            case 3 -> Tileset.GRASS;
            case 4 -> Tileset.WATER;
            default -> Tileset.SAND;
        };
    }

    /** main */
    public static void main(String[] args) {
        // initialize the tile rendering engine with a window of size WIDTH x HEIGHT
        TERenderer ter = new TERenderer();
        int randomHex = randomHex();
//        int WIDTH = 10 * randomHex;
//        int HEIGHT = 13 * randomHex;
        ter.initialize(WIDTH, HEIGHT);

        // initialize tiles
        TETile[][] world = new TETile[WIDTH][HEIGHT];
        for (int x = 0; x < WIDTH; x += 1) {
            for (int y = 0; y < HEIGHT; y += 1) {
                world[x][y] = Tileset.NOTHING;
            }
        }

        // draw a single hexagon
        Position p = new Position(47, 20);
        int size = 5;
        TETile randomT = randomTile();

        // draw a lot of hexagons
        fillTile(world, randomHex);

        ter.renderFrame(world);
        System.out.println("Passed!");
    }
}
