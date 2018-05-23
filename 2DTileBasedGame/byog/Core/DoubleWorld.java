package byog.Core;

import byog.TileEngine.TETile;

import java.io.Serializable;

/* Wraps two TETile[][] and contains a boolean to check which one is current*/
public class DoubleWorld implements Serializable{

    private WorldGenerator genLower;
    private WorldGenerator genUpper;
    private boolean isLower;
    private boolean hasKey;
    private TETile[][] upper;
    private TETile[][] lower;
    private static final long serialVersionUID = 123120432432L;




    /** Constructor*/
    public DoubleWorld() {

    }

    public boolean isHasKey() {
        return hasKey;
    }

    public void setHasKey () {
        hasKey = true;
    }

    /** Returns world to use depending on which floor*/
    public TETile[][] getLevel() {
        if (isLower) {
            return lower;
        } else {
            return upper;
        }
    }

    public WorldGenerator getGenerator() {
        if (isLower) {
            return genLower;
        } else {
            return genUpper;
        }
    }

    public Coordinate getStairPos(){
        return getGenerator().getStairPos();
    }

    public Coordinate getGoalPos() {
        return getGenerator().getGoalPos();
    }

    public Coordinate getStartPos() {
        return getGenerator().getStartPos();
    }


    /** Changes the current level*/
    public void toggleLevel() {
        isLower = !isLower;
    }

    public void generate(int worldWidth, int worldHeight, long seed) {
        genLower = new WorldGenerator(worldWidth, worldHeight, seed, true);
        genUpper = new WorldGenerator(worldWidth, worldHeight, seed / 2, false);

        lower = genLower.generate();
        upper = genUpper.generate();
        isLower = true;
        hasKey = false;
    }



}
