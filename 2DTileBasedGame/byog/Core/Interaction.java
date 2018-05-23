package byog.Core;

import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;

public class Interaction {

    private boolean gameOver;
    private DoubleWorld doubleWorld;
    private boolean isFloor;
    private Coordinate curPos;

    public Interaction(DoubleWorld world, Coordinate currentPos) {
        this.doubleWorld = world;
        this.gameOver = false;
        this.curPos = currentPos;
        isFloor = true;
    }


    /* Parses command and executes each one*/
    public TETile[][] moveString(String command) {
        int lenOfCommand = command.length();
        for (int index = 0; index < lenOfCommand; index++) {
            moveCharacter(command.charAt(index));
        }
        return doubleWorld.getLevel();
    }


    /* Changes currentPosition based on argument*/
    public TETile[][] moveCharacter(char direction) {
        TETile[][] world = doubleWorld.getLevel();
        switch (direction) {
            case 'w':
            case 'W': {
                if (world[curPos.getX()][curPos.getY() + 1].equals(Tileset.FLOOR)) {
                    if (isFloor) {
                        world[curPos.getX()][curPos.getY()] = Tileset.FLOOR;
                    } else {
                        world[curPos.getX()][curPos.getY()] = Tileset.STAIRS;
                    }
                    curPos = new Coordinate(curPos.getX(), curPos.getY() + 1);
                    world[curPos.getX()][curPos.getY()] = Tileset.PLAYER;
                    isFloor = true;
                } else if (world[curPos.getX()][curPos.getY() + 1].equals(Tileset.LOCKED_DOOR) && doubleWorld.isHasKey()) {
                    world[curPos.getX()][curPos.getY() + 1] = Tileset.UNLOCKED_DOOR;
                } else if (world[curPos.getX()][curPos.getY() + 1].equals(Tileset.STAIRS)) {
                    if (isFloor) {
                        world[curPos.getX()][curPos.getY()] = Tileset.FLOOR;
                    } else {
                        world[curPos.getX()][curPos.getY()] = Tileset.STAIRS;
                    }
                    doubleWorld.toggleLevel();
                    curPos = doubleWorld.getStairPos();
                    doubleWorld.getLevel()[curPos.getX()][curPos.getY()] = Tileset.PLAYER;
                    isFloor = false;
                } else if (world[curPos.getX()][curPos.getY() + 1].equals(Tileset.KEY)) {
                    world[curPos.getX()][curPos.getY() + 1] = Tileset.FLOOR;
                    doubleWorld.setHasKey();
                }  else if (world[curPos.getX()][curPos.getY() + 1].equals(Tileset.UNLOCKED_DOOR)) {
                    gameOver = true;
                    if (isFloor) {
                        world[curPos.getX()][curPos.getY()] = Tileset.FLOOR;
                    } else {
                        world[curPos.getX()][curPos.getY()] = Tileset.STAIRS;
                    }
                    curPos = new Coordinate(curPos.getX(), curPos.getY() + 1);
                    world[curPos.getX()][curPos.getY()] = Tileset.PLAYER;
                    break;
                }
                break;
            }
            case 'a':
            case 'A': {
                if (world[curPos.getX() - 1][curPos.getY()].equals(Tileset.FLOOR)) {
                    if (isFloor) {
                        world[curPos.getX()][curPos.getY()] = Tileset.FLOOR;
                    } else {
                        world[curPos.getX()][curPos.getY()] = Tileset.STAIRS;
                    }
                    curPos = new Coordinate(curPos.getX() - 1, curPos.getY());
                    world[curPos.getX()][curPos.getY()] = Tileset.PLAYER;
                    isFloor = true;
                } else if (world[curPos.getX() - 1][curPos.getY()].equals(Tileset.LOCKED_DOOR) && doubleWorld.isHasKey()) {
                    world[curPos.getX() - 1][curPos.getY()] = Tileset.UNLOCKED_DOOR;
                } else if (world[curPos.getX() - 1][curPos.getY()].equals(Tileset.STAIRS)) {
                    if (isFloor) {
                        world[curPos.getX()][curPos.getY()] = Tileset.FLOOR;
                    } else {
                        world[curPos.getX()][curPos.getY()] = Tileset.STAIRS;
                    }
                    doubleWorld.toggleLevel();
                    curPos = doubleWorld.getStairPos();
                    doubleWorld.getLevel()[curPos.getX()][curPos.getY()] = Tileset.PLAYER;
                    isFloor = false;
                } else if (world[curPos.getX() - 1][curPos.getY()].equals(Tileset.KEY)) {
                    world[curPos.getX() - 1][curPos.getY()] = Tileset.FLOOR;
                    doubleWorld.setHasKey();
                } else if (world[curPos.getX() - 1][curPos.getY()].equals(Tileset.UNLOCKED_DOOR)) {
                    gameOver = true;
                    if (isFloor) {
                        world[curPos.getX()][curPos.getY()] = Tileset.FLOOR;
                    } else {
                        world[curPos.getX()][curPos.getY()] = Tileset.STAIRS;
                    }
                    curPos = new Coordinate(curPos.getX() - 1, curPos.getY());
                    world[curPos.getX()][curPos.getY()] = Tileset.PLAYER;
                    break;
                }
                break;
            }
            case 's':
            case 'S': {
                if (world[curPos.getX()][curPos.getY() - 1].equals(Tileset.FLOOR)) {
                    if (isFloor) {
                        world[curPos.getX()][curPos.getY()] = Tileset.FLOOR;
                    } else {
                        world[curPos.getX()][curPos.getY()] = Tileset.STAIRS;
                    }
                    curPos = new Coordinate(curPos.getX(), curPos.getY() - 1);
                    world[curPos.getX()][curPos.getY()] = Tileset.PLAYER;
                    isFloor = true;
                } else if (world[curPos.getX()][curPos.getY() - 1].equals(Tileset.LOCKED_DOOR) && doubleWorld.isHasKey()) {
                    world[curPos.getX()][curPos.getY() - 1] = Tileset.UNLOCKED_DOOR;
                } else if (world[curPos.getX()][curPos.getY() - 1].equals(Tileset.UNLOCKED_DOOR)) {
                    gameOver = true;
                    if (isFloor) {
                        world[curPos.getX()][curPos.getY()] = Tileset.FLOOR;
                    } else {
                        world[curPos.getX()][curPos.getY()] = Tileset.STAIRS;
                    }
                    curPos = new Coordinate(curPos.getX(), curPos.getY() - 1);
                    world[curPos.getX()][curPos.getY()] = Tileset.PLAYER;
                } else if (world[curPos.getX()][curPos.getY() - 1].equals(Tileset.STAIRS)) {
                    if (isFloor) {
                        world[curPos.getX()][curPos.getY()] = Tileset.FLOOR;
                    } else {
                        world[curPos.getX()][curPos.getY()] = Tileset.STAIRS;
                    }
                    doubleWorld.toggleLevel();
                    curPos = doubleWorld.getStairPos();
                    doubleWorld.getLevel()[curPos.getX()][curPos.getY()] = Tileset.PLAYER;
                    isFloor = false;
                } else if (world[curPos.getX()][curPos.getY() - 1].equals(Tileset.KEY)) {
                    world[curPos.getX()][curPos.getY() - 1] = Tileset.FLOOR;
                    doubleWorld.setHasKey();
                }

                break;
            }
            case 'd':
            case 'D': {
                if (world[curPos.getX() + 1][curPos.getY()].equals(Tileset.FLOOR)) {
                    if (isFloor) {
                        world[curPos.getX()][curPos.getY()] = Tileset.FLOOR;
                    } else {
                        world[curPos.getX()][curPos.getY()] = Tileset.STAIRS;
                    }                    curPos = new Coordinate(curPos.getX() + 1, curPos.getY());
                    world[curPos.getX()][curPos.getY()] = Tileset.PLAYER;
                    isFloor = true;
                } else if (world[curPos.getX() + 1][curPos.getY()].equals(Tileset.LOCKED_DOOR) && doubleWorld.isHasKey()) {
                    world[curPos.getX() + 1][curPos.getY()] = Tileset.UNLOCKED_DOOR;
                } else if (world[curPos.getX() + 1][curPos.getY()].equals(Tileset.STAIRS)) {
                    if (isFloor) {
                        world[curPos.getX()][curPos.getY()] = Tileset.FLOOR;
                    } else {
                        world[curPos.getX()][curPos.getY()] = Tileset.STAIRS;
                    }
                    doubleWorld.toggleLevel();
                    curPos = doubleWorld.getStairPos();
                    doubleWorld.getLevel()[curPos.getX()][curPos.getY()] = Tileset.PLAYER;
                    isFloor = false;
                } else if (world[curPos.getX() + 1][curPos.getY()].equals(Tileset.KEY)) {
                    world[curPos.getX() + 1][curPos.getY()] = Tileset.FLOOR;
                    doubleWorld.setHasKey();
                } else if (world[curPos.getX() + 1][curPos.getY()].equals(Tileset.UNLOCKED_DOOR)) {
                    gameOver = true;
                    if (isFloor) {
                        world[curPos.getX()][curPos.getY()] = Tileset.FLOOR;
                    } else {
                        world[curPos.getX()][curPos.getY()] = Tileset.STAIRS;
                    }
                    curPos = new Coordinate(curPos.getX() + 1, curPos.getY());
                    world[curPos.getX()][curPos.getY()] = Tileset.PLAYER;
                    break;
                }
            }
            default: {
            }
        }
        return world;
    }

    public boolean getGameStatus() {
        return this.gameOver;
    }

}
