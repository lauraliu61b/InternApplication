package byog.Core;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.Random;

import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;

public class WorldGenerator implements Serializable {

    private final TETile FLOOR_TYPE = Tileset.FLOOR;
    private final TETile WALL_TYPE = Tileset.WALL;
    private final TETile GOAL_TYPE = Tileset.LOCKED_DOOR;
    private final TETile PLAYER_TYPE = Tileset.FLOWER;
    private final int FRAME_WIDTH;
    private final int FRAME_HEIGHT;
    private final int WORLD_WIDTH;
    private final int WORLD_HEIGHT;
    private final double WORLD_AREA;
    private Coordinate goalPos;
    private Coordinate startPos;
    private Coordinate stairPos;
    private TETile[][] world;
    private double area;
    private Random random;
    private boolean isLower;

    private static final long serialVersionUID = 123134524352343L;


    /** Constructor*/
    public WorldGenerator(int worldWidth, int worldHeight, long seed, boolean isLower) {
        FRAME_WIDTH = worldWidth;
        FRAME_HEIGHT = worldHeight;
        this.world = new TETile[FRAME_WIDTH][FRAME_HEIGHT];
        WORLD_WIDTH = FRAME_WIDTH;
        WORLD_HEIGHT = FRAME_HEIGHT - 4;
        WORLD_AREA = FRAME_WIDTH * FRAME_HEIGHT;
        random = new Random(seed);
        this.isLower = isLower;
        area = 0;

        for (int x = 0; x < FRAME_WIDTH; x += 1) {
            for (int y = 0; y < FRAME_HEIGHT; y += 1) {
                world[x][y] = Tileset.NOTHING;
            }
        }
    }

    /** Get goal position*/
    public Coordinate getGoalPos() {
        return goalPos;
    }

    /** Get stair position*/
    public Coordinate getStairPos() {
        return stairPos;
    }

    /** Get start position*/
    public Coordinate getStartPos() {
        return startPos;
    }

    /** Call to generate world*/
    public TETile[][] generate() {
        genWorldHelper1();
        closeDoors();
        genStructures();
        return world;
    }

    /** Helper method for generate()*/
    private void genWorldHelper1() {
        Coordinate start = randPosition(1);
        startPos = start;
        Coordinate[] doors = addRoom(start, randRoomSide(), randRoomSide());
        setDoors(doors);
        genWorldHelper2(doors, Type.ROOM);

    }

//    /** Get start position of the player, near where the world starts to generate.*/
//    public Coordinate getStartPos() {
//        if (world[startPos.getX()][startPos.getY() + 2] == Tileset.FLOOR) {
//            return new Coordinate(startPos.getX(), startPos.getY() + 2);
//        } else if (world[startPos.getX()][startPos.getY() - 2] == Tileset.FLOOR) {
//            return new Coordinate(startPos.getX(), startPos.getY() - 2);
//        } else if (world[startPos.getX() + 2][startPos.getY()] == Tileset.FLOOR) {
//            return new Coordinate(startPos.getX() + 2, startPos.getY());
//        } else if (world[startPos.getX() - 2][startPos.getY()] == Tileset.FLOOR) {
//            return new Coordinate(startPos.getX() - 2, startPos.getY());
//        }
//        return null;
//    }

    /** Helper method for generate()*/
    private void genWorldHelper2(Coordinate[] doorList, Type type) {
        for (Coordinate door : doorList) {
            Coordinate[] doors;
            if (hasRoom(door)) {
                if (isRoom(type)) {
                    doors = addRoom(door, randRoomSide(), randRoomSide());
                    setDoors(doors);
                    genWorldHelper2(doors, Type.ROOM);
                } else {
                    doors = addHallway(door, randHallLength());
                    setDoors(doors);
                    genWorldHelper2(doors, Type.HALL);
                }
            }
        }

    }

    /** Closes remaining open doors on the world and makes one of them the goal*/
    private void closeDoors() {
        int num = RandomUtils.uniform(random, 3, 6);
        for (int x = 0; x < WORLD_WIDTH; x++) {
            for (int y = 0; y < WORLD_HEIGHT; y++) {
                Direction dir = getOpeningDirection(new Coordinate(x, y));
                if (dir.equals(Direction.DOWN) || dir.equals(Direction.UP)
                        || dir.equals(Direction.LEFT) || dir.equals(Direction.RIGHT)) {
                    if (!isLower) {
                        if (world[x][y].equals(FLOOR_TYPE) && num == 0) {
                            world[x][y] = GOAL_TYPE;
                            goalPos = new Coordinate(x, y);
                            num--;
                        } else if (world[x][y].equals(FLOOR_TYPE) && num != 0) {
                            world[x][y] = WALL_TYPE;
                            num--;
                        }
                    } else {
                        if (world[x][y].equals(FLOOR_TYPE)) {
                            world[x][y] = WALL_TYPE;
                            num--;
                        }
                    }
                }
            }
        }
    }


    private void genStructures() {
        LinkedList<Coordinate> floorList = new LinkedList<>();
        for (int x = 0; x < WORLD_WIDTH; x++) {
            for (int y = 0; y < WORLD_HEIGHT; y++) {
                if (world[x][y].equals(FLOOR_TYPE)) {
                    floorList.add(new Coordinate(x, y));
                }
            }
        }
        Coordinate[] floorArray = floorList.toArray(new Coordinate[floorList.size()]);
        int keyIndex = RandomUtils.uniform(random, 0, floorArray.length / 3);
        int startIndex = RandomUtils.uniform(random, floorArray.length / 3, (floorArray.length * 2) / 3);
        int stairIndex = RandomUtils.uniform(random, (floorArray.length * 2) / 3, floorArray.length);

        Coordinate keyPos = floorArray[keyIndex];
        Coordinate startPos = floorArray[startIndex];
        Coordinate stairPos = floorArray[stairIndex];

        if (isLower) {
            world[keyPos.getX()][keyPos.getY()] = Tileset.KEY;


        }



        world[stairPos.getX()][stairPos.getY()] = Tileset.STAIRS;

        this.stairPos = stairPos;
        this.startPos = startPos;
    }


    /** Sets selected coordinates to be doors*/
    private void setDoors(Coordinate[] doorList) {
        for (Coordinate door : doorList) {
            world[door.getX()][door.getY()] = FLOOR_TYPE;
        }
    }

    /** Changes tile at given position if it is currently empty*/
    private void setTile(Coordinate pos, TETile tile) {
        if (pos.getX() < 0 || pos.getX() > WORLD_WIDTH - 1
                || pos.getY() < 0 || pos.getY() > WORLD_HEIGHT - 1) {
            return;
        }
        if (world[pos.getX()][pos.getY()].equals(Tileset.NOTHING)) {
            world[pos.getX()][pos.getY()] = tile;
        }
    }

    /** Adds room in front of given door coordinate. Returns array of its walls.*/
    private Coordinate[] addRoom(Coordinate doorPos, int height, int width) {
        /* Exits if room is too small*/
        if (height < 3 || width < 3 || !hasRoom(doorPos)) {
            return null;
        }

        Direction entranceDir = getOpeningDirection(doorPos);
        int left = 0;
        int right = 0;
        int top = 0;
        int bottom = 0;

        /* For our first room, there is no opening direction*/
        if (getUsage() == 0) {
            entranceDir = Direction.FIRST;
        }

        /* Gets bounds*/
        Coordinate door = null;
        switch (entranceDir) {
            case NONE:
            case FIRST:
                left = boundToX(doorPos.getX() - width / 2);
                right = boundToX(doorPos.getX() + (width / 2) - 1 + width % 2);
                bottom = boundToY(doorPos.getY() + 1);
                top = boundToY(bottom + height);
                break;
            case UP:
                door = new Coordinate(doorPos.getX(), doorPos.getY() + 1);
                left = boundToX(doorPos.getX() - width / 2);
                right = boundToX(doorPos.getX() + (width / 2) - 1 + width % 2);
                bottom = boundToY(doorPos.getY() + 1);
                top = boundToY(bottom + height);
                break;
            case DOWN:
                door = new Coordinate(doorPos.getX(), doorPos.getY() - 1);
                left = boundToX(doorPos.getX() - width / 2);
                right = boundToX(doorPos.getX() + (width / 2) - 1 + width % 2);
                top = boundToY(doorPos.getY() - 1);
                bottom = boundToY(top - height);
                break;
            case RIGHT:
                door = new Coordinate(doorPos.getX() + 1, doorPos.getY());
                top = boundToY(doorPos.getY() + height / 2);
                bottom = boundToY(doorPos.getY() - (height / 2) - 1 + height % 2);
                left = boundToX(doorPos.getX() + 1);
                right = boundToX(left + width);
                break;
            case LEFT:
                door = new Coordinate(doorPos.getX() - 1, doorPos.getY());
                top = boundToY(doorPos.getY() + height / 2);
                bottom = boundToY(doorPos.getY() - (height / 2) - 1 + height % 2);
                right = boundToX(doorPos.getX() - 1);
                left = boundToX(right - width);
                break;
            default:
                break;
        }

        /* Opens door */
        if (door != null) {
            world[door.getX()][door.getY()] = FLOOR_TYPE;
        }

        Coordinate[] doors = drawBox(entranceDir, top, bottom, left, right); // Draws the room
        area += width * height;
        return doors;
    }

    /** Adds a hallway in front of a given door coordinate. Returns array of its walls */
    private Coordinate[] addHallway(Coordinate entrance, int length) {
        int left = 0;
        int right = 0;
        int top = 0;
        int bottom = 0;
        length++;
        Direction entranceDir = getOpeningDirection(entrance);

        /* Gets bounds*/
        Coordinate door = null;
        switch (entranceDir) {
            case NONE:
            case RIGHT:
                door = new Coordinate(entrance.getX() + 1, entrance.getY());
                top = boundToY(entrance.getY() + 1);
                bottom = boundToY(entrance.getY() - 1);
                left = boundToX(entrance.getX() + 1);
                right = boundToX(entrance.getX() + length);
                break;
            case LEFT:
                door = new Coordinate(entrance.getX() - 1, entrance.getY());
                top = boundToY(entrance.getY() + 1);
                bottom = boundToY(entrance.getY() - 1);
                right = boundToX(entrance.getX() - 1);
                left = boundToX(entrance.getX() - length);
                break;
            case UP:
                door = new Coordinate(entrance.getX(), entrance.getY() + 1);
                left = boundToX(entrance.getX() - 1);
                right = boundToX(entrance.getX() + 1);
                bottom = boundToY(entrance.getY() + 1);
                top = boundToY(bottom + length);
                break;
            case DOWN:
                door = new Coordinate(entrance.getX(), entrance.getY() - 1);
                left = boundToX(entrance.getX() - 1);
                right = boundToX(entrance.getX() + 1);
                top = boundToY(entrance.getY() - 1);
                bottom = boundToY(top - length);
                break;
            default:
                break;
        }

        /* Opens doors*/
        if (door != null) {
            world[door.getX()][door.getY()] = FLOOR_TYPE;
        }

        Coordinate[] doors = drawBox(entranceDir, top, bottom, left, right);
        area += 3 * length;
        return doors;
    }

    /** Draws box based on bounds, then determines random spots to be doors*/
    private Coordinate[] drawBox(Direction orig, int top, int bottom, int left, int right) {
        if (top - bottom < 2 || right - left < 2) {
            return new Coordinate[]{};
        }

        /* Draws top and bottom*/
        for (int x = left; x <= right; x++) {
            setTile(new Coordinate(x, bottom), WALL_TYPE);
            setTile(new Coordinate(x, top), WALL_TYPE);
        }

        /* Nested for loop to fill in rest of the room*/
        for (int y = bottom + 1; y < top; y++) {
            setTile(new Coordinate(left, y), WALL_TYPE);
            setTile(new Coordinate(right, y), WALL_TYPE);
            for (int x = left + 1; x < right; x++) {
                world[x][y] = FLOOR_TYPE;
            }
        }

        return findDoors(orig, top, bottom, left, right);

    }

    /** Randomly finds doors in a structures walls based on its entrance*/
    private Coordinate[] findDoors(Direction orig, int top, int bottom, int left, int right) {
        Coordinate[] botArray = new Coordinate[right - left - 1];
        Coordinate[] topArray = new Coordinate[right - left - 1];
        Coordinate[] leftArray = new Coordinate[top - bottom - 1];
        Coordinate[] rightArray = new Coordinate[top - bottom - 1];

        /* Looks for one on bottom*/
        int i = 0;
        for (int x = left + 1; x < right; x++) {
            botArray[i] = new Coordinate(x, bottom);
            topArray[i] = new Coordinate(x, top);
            i++;
        }
        /* Adds left and right*/
        i = 0;
        for (int y = bottom + 1; y < top; y++) {
            leftArray[i] = new Coordinate(left, y);
            rightArray[i] = new Coordinate(right, y);
            i++;
        }

        Coordinate[][] walls;
        switch (orig) {
            case NONE:
                return new Coordinate[]{};
            case UP:
                walls = new Coordinate[][]{topArray, leftArray, rightArray};
                break;
            case DOWN:
                walls = new Coordinate[][]{botArray, leftArray, rightArray};
                break;
            case LEFT:
                walls = new Coordinate[][]{leftArray, topArray, botArray};
                break;
            case RIGHT:
                walls = new Coordinate[][]{rightArray, topArray, botArray};
                break;
            case FIRST:
                walls = new Coordinate[][]{topArray, botArray, leftArray, rightArray};
                break;
            default:
                walls = new Coordinate[][]{topArray, botArray, leftArray, rightArray};
                break;
        }

        LinkedList<Coordinate> doorList = new LinkedList<>();
        for (Coordinate[] wall : walls) {
            if (RandomUtils.bernoulli(random, 0.9)) {
                Coordinate toAdd = wall[RandomUtils.uniform(random, wall.length)];

                for (int j = 0; j < 8; j++) {
                    if (!hasRoom(toAdd)) {
                        toAdd = wall[RandomUtils.uniform(random, wall.length)];
                    }
                }

                if (hasRoom(toAdd)) {
                    doorList.addLast(toAdd);
                }
            }
        }

        Coordinate[] doors = doorList.toArray(new Coordinate[doorList.size()]);
        return doors;
    }

    /** Get the direction that the door at doorPos opens towards*/
    private Direction getOpeningDirection(Coordinate doorPos) {
        if (doorPos.getX() <= 0 || doorPos.getX() >= WORLD_WIDTH - 1
                || doorPos.getY() <= 0 || doorPos.getY() >= WORLD_HEIGHT - 1) {
            return Direction.NONE;
        }

        if (world[doorPos.getX()][doorPos.getY() + 1].equals(Tileset.NOTHING)) {
            return Direction.UP;
        } else if (world[doorPos.getX()][doorPos.getY() - 1].equals(Tileset.NOTHING)) {
            return Direction.DOWN;
        } else if (world[doorPos.getX() + 1][doorPos.getY()].equals(Tileset.NOTHING)) {
            return Direction.RIGHT;
        } else if (world[doorPos.getX() - 1][doorPos.getY()].equals(Tileset.NOTHING)) {
            return Direction.LEFT;
        }

        return Direction.NONE;

    }

    /** Gets the percent of the world that is filled in. */
    private double getUsage() {
        return area / WORLD_AREA;
    }

    /** Determines if a structure can be created at given position*/
    private boolean hasRoom(Coordinate origPos) {
        int left = 0;
        int right = 0;
        int top = 0;
        int bottom = 0;
        final int L = 5;
        Direction orig = getOpeningDirection(origPos);

        switch (orig) {
            case NONE:
                return false;

            case FIRST:
                return true;

            case UP:
                bottom = origPos.getY() + 1;
                top = bottom + L;
                left = origPos.getX() - L / 2;
                right = origPos.getX() + (L / 2) - 1 + L % 2;
                break;
            case DOWN:
                left = origPos.getX() - L / 2;
                right = origPos.getX() + (L / 2) - 1 + L % 2;
                top = origPos.getY() - 1;
                bottom = top - L;
                break;
            case RIGHT:
                left = origPos.getX() + 1;
                right = left + L;
                top = origPos.getY() + L / 2;
                bottom = origPos.getY() - (L / 2) - 1 + L % 2;
                break;
            case LEFT:
                right = origPos.getX() - 1;
                left = right - L;
                top = origPos.getY() + L / 2;
                bottom = origPos.getY() - (L / 2) - 1 + L % 2;
                break;
            default:
                break;
        }

        /* If out of bounds, does not have enough room*/
        if (left < 0 || right >= WORLD_WIDTH || bottom < 0 || top >= WORLD_HEIGHT) {
            return false;
        }

        /* Checks every space in square and returns false if any one of them is filled*/
        for (int x = left; x <= right; x++) {
            for (int y = bottom; y <= top; y++) {
                if (!world[x][y].equals(Tileset.NOTHING)) {
                    return false;
                }
            }
        }

        return true;
    }

    /** Returns whether the next structure is a room or a hall.*/
    private boolean isRoom(Type type) {
        if (type == Type.ROOM) {
            return RandomUtils.bernoulli(random, 0.3);
        } else {
            return RandomUtils.bernoulli(random, 0.5);
        }
    }

    /** If x is out of bounds, sets it to be in bound*/
    private int boundToX(int x) {
        if (x < 0) {
            return 0;
        }
        if (x >= WORLD_WIDTH) {
            return WORLD_WIDTH - 1;
        }
        return x;
    }

    /** If y is out of bounds, sets it to be in bound*/
    private int boundToY(int y) {
        if (y < 0) {
            return 0;
        }
        if (y >= WORLD_HEIGHT) {
            return WORLD_HEIGHT - 1;
        }
        return y;
    }

    /** Pick a random position near the center of the world*/
    private Coordinate randPosition(int var) {
        int midWidth = WORLD_WIDTH / 2;
        int midHeight = WORLD_HEIGHT / 2;

        int randX = RandomUtils.uniform(random, midWidth - var, midWidth + var);
        int randY = RandomUtils.uniform(random, midHeight - var, midHeight + var);

        return new Coordinate(randX, randY);
    }

    /** Returns a random valid length for a hallway*/
    private int randRoomSide() {
        return RandomUtils.uniform(random, 4, 10);
    }

    /** Returns a random valid length for a hallway*/
    private int randHallLength() {
        return RandomUtils.uniform(random, 6, 10);
    }

    /** Representation of which way a door is facing*/
    private enum Direction {
        FIRST, UP, DOWN, LEFT, RIGHT, NONE
    }

    /** Representation of structures*/
    private enum Type {
        ROOM, HALL,
    }

}
