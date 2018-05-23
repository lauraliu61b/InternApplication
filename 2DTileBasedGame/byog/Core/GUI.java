package byog.Core;

import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.Color;


public class GUI {

    private static DoubleWorld doubleWorld;

    public static void drawFrame(int width, int height, String s) {
        int midWidth = width / 2;
        StdDraw.clear();
        StdDraw.clear(Color.black);

        String gameMessage;
        if (doubleWorld.isHasKey()) {
            gameMessage = "You found a key!";
        } else {
            gameMessage = "Find a way out!";
        }


        //if(!gameOver){
        StdDraw.setPenColor(StdDraw.WHITE);
        StdDraw.textLeft(0, height - 1.5, gameMessage);
        StdDraw.text(midWidth, height - 1.5, s);
        StdDraw.textRight(width - 1, height - 1.5, "Back (B) | Quit (:Q)");
        StdDraw.line(0, height - 2.9, width, height - 2.9);
        //}


    }

    public static void setWorld (DoubleWorld dworld) {
        doubleWorld = dworld;
    }

    public static String getTileType(Coordinate currentTile, TETile[][] world) {
        if (world[currentTile.getX()][currentTile.getY()].equals(Tileset.NOTHING)) {
            return "nothing";
        } else if (world[currentTile.getX()][currentTile.getY()].equals(Tileset.WALL)) {
            return "wall";
        } else if (world[currentTile.getX()][currentTile.getY()].equals(Tileset.FLOOR)) {
            return "floor";
        } else if (world[currentTile.getX()][currentTile.getY()].equals(Tileset.UNLOCKED_DOOR)) {
            return "unlocked door";
        } else if (world[currentTile.getX()][currentTile.getY()].equals(Tileset.LOCKED_DOOR)) {
            return "locked door";
        } else if (world[currentTile.getX()][currentTile.getY()].equals(Tileset.PLAYER)) {
            return "player";
        } else if (world[currentTile.getX()][currentTile.getY()].equals(Tileset.KEY)) {
            return "key";
        } else if (world[currentTile.getX()][currentTile.getY()].equals(Tileset.STAIRS)) {
            return "stairs";
        }
        return "?????";
    }

    /* Converts mouse's current position to the tile in the world*/
    public static Coordinate getMousePosition() {
        int xPos = (int) Math.floor(StdDraw.mouseX());
        int yPos = (int) Math.floor(StdDraw.mouseY());

        if (xPos >= Game.WIDTH) {
            xPos = Game.WIDTH - 1;
        }

        if (yPos >= Game.HEIGHT) {
            yPos = Game.HEIGHT - 1;
        }

        return new Coordinate(xPos, yPos);
    }
}
