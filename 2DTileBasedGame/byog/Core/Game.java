package byog.Core;

import byog.TileEngine.TERenderer;
import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;
import edu.princeton.cs.introcs.StdDraw;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Game {
    /* Feel free to change the width and height. */
    public static final int WIDTH = 80;
    public static final int HEIGHT = 40;
    private TERenderer ter = new TERenderer();
    private boolean inGame = false;

    /** @source: SaveDemo
     * Loads world if one is saved. Returns none otherwise*/
    private static DoubleWorld loadWorld() {
        File f = new File("./world.txt");
        if (f.exists()) {
            try {
                FileInputStream fs = new FileInputStream(f);
                ObjectInputStream os = new ObjectInputStream(fs);
                return (DoubleWorld) os.readObject();
            } catch (FileNotFoundException e) {
                System.out.println("file not found");
                System.exit(0);
            } catch (IOException e) {
                System.out.println(e);
                System.exit(0);
            } catch (ClassNotFoundException e) {
                System.out.println("class not found");
                System.exit(0);
            }
        }

        //* In the case no World has been saved yet, we return a new one. */
        System.out.println("file not found");
        System.exit(0);
        return new DoubleWorld();
    }

    /** Saves w into world.txt*/
    private static void saveWorld(DoubleWorld w) {
        File f = new File("./world.txt");
        try {
            if (!f.exists()) {
                f.createNewFile();
            }
            FileOutputStream fs = new FileOutputStream(f);
            ObjectOutputStream os = new ObjectOutputStream(fs);
            os.writeObject(w);
        } catch (FileNotFoundException e) {
            System.out.println("file not found");
            System.exit(0);
        } catch (IOException e) {
            System.out.println(e);
            System.exit(0);
        }
    }

    /**
     * Method used for playing a fresh game. The game should start from the main menu.
     */
   public TETile[][] playWithKeyboard() {
        ter.initialize(WIDTH, HEIGHT);
        ter.renderWelcomePage();

        DoubleWorld doubleWorld = new DoubleWorld();
        TETile[][] finalWorldFrame = null;
        String input;
        char cur;
        char pre = ' ';
        boolean hasKey;
        Interaction interact = new Interaction(doubleWorld, new Coordinate(0, 0));
        GUI.setWorld(doubleWorld);

        while (true) {
            if(interact.getGameStatus()){
                inGame = false;
                cur = 'B';
            }
            else{
                cur  = listener(doubleWorld);
            }
            if (cur == ':') {
                pre = ':';
                continue;
            }
            if (pre == ':' && (cur == 'Q' || cur == 'q')) {
                saveWorld(doubleWorld);
                System.exit(0);
            } else {
                switch (cur) {
                    case 'n':
                    case 'N': {
                        if (!inGame) {
                            ter.helpGetMessage("");
                            ter.renderSeedPage();
                            input = "";
                            while (true) {
                                cur = listener(doubleWorld);
                                if (cur == 'S' || cur == 's') {
                                    break;
                                } else {
                                    input += cur;
                                    ter.helpGetMessage(input);
                                    ter.renderSeedPage();
                                }
                            }
                            if (input.length() == 0) {
                                ter.renderWelcomePage();
                                continue;
                            }
                            Long seed = Long.parseLong(input);
                            doubleWorld.generate(WIDTH, HEIGHT, seed);
                            finalWorldFrame = doubleWorld.getLevel();
                            Coordinate startPos = doubleWorld.getStartPos();
                            finalWorldFrame[startPos.getX()][startPos.getY()] = Tileset.PLAYER;
                            ter.renderFrame(finalWorldFrame);
                            interact = new Interaction(doubleWorld, startPos);
                            inGame = true;
                        }
                        break;
                    }
                    case 'l':
                    case 'L': {
                        if(!inGame) {
                            doubleWorld = loadWorld();
                            GUI.setWorld(doubleWorld);
                            ter.renderFrame(doubleWorld.getLevel());
                            Coordinate currentPos = getPlayerPosition(doubleWorld.getLevel());
                            interact = new Interaction(doubleWorld, currentPos);
                            inGame = true;
                        }
                        break;
                    }
                    case 'b':
                    case 'B':{
                        if(inGame || interact.getGameStatus()){
                        if (!interact.getGameStatus()) {
                            saveWorld(doubleWorld);
                            ter.helpGetMessage("Save successfully!");
                            ter.renderFrame(finalWorldFrame);
                            StdDraw.pause(750);
                        }
                        inGame = false;
                        interact = new Interaction(doubleWorld, new Coordinate(0, 0));
                        ter.renderWelcomePage();
                        }
                        break;

                    }
                    default: {
                        if(inGame){
                        finalWorldFrame = interact.moveCharacter(cur);
                        if (interact.getGameStatus()) {
                            ter.helpGetMessage("You win!");
                            ter.renderFrame(finalWorldFrame);
                            StdDraw.pause(750);
                        }else{
                            ter.renderFrame(finalWorldFrame);
                        }
                        }
                        break;
                    }
                }
            }
        }
        //return doubleWorld.getLevel();
    }


    private char listener(DoubleWorld doubleWorld) {
        TETile[][] world = doubleWorld.getLevel();
        while (!StdDraw.hasNextKeyTyped()) {
            if (world != null && inGame == true) {
                String s = GUI.getTileType(GUI.getMousePosition(), world);
                ter.helpGetMessage(s);
                ter.renderFrame(world);
            }
            continue;
        }
        return StdDraw.nextKeyTyped();

    }

    /**
     * Method used for autograding and testing the game code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The game should
     * behave exactly as if the user typed these characters into the game after playing
     * playWithKeyboard. If the string ends in ":q", the same world should be returned as if the
     * string did not end with q. For example "n123sss" and "n123sss:q" should return the same
     * world. However, the behavior is slightly different. After playing with "n123sss:q", the game
     * should save, and thus if we then called playWithInputString with the string "l", we'd expect
     * to get the exact same world back again, since this corresponds to loading the saved game.
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public TETile[][] playWithInputString(String input) {
        //ter.initialize(WIDTH, HEIGHT);
        TETile[][] finalWorldFrame = new TETile[WIDTH][HEIGHT];
        DoubleWorld doubleWorld;
        input = input.toUpperCase();
        switch (input.charAt(0)) {
            case 'N': {
                int startIndex = 1;
                int endIndex = 1;
                while ((input.charAt(endIndex) >= 48 && input.charAt(endIndex) <= 57)
                        && endIndex < input.length()) {
                    endIndex++;
                }
                if (startIndex == endIndex) {
                    return finalWorldFrame;
                }
                String rest = input.substring(endIndex + 1, input.length());
                int lenOfRest = rest.length();
                Boolean saveOrNot = false;
                String command = rest;
                if (lenOfRest > 2
                        && rest.charAt(lenOfRest - 1) == 'Q'
                        && rest.charAt(lenOfRest - 2) == ':') {
                    saveOrNot = true;
                    command = rest.substring(0, lenOfRest - 2);
                }
                long seed = Long.parseLong(input.substring(startIndex, endIndex));
                doubleWorld = new DoubleWorld();
                doubleWorld.generate(WIDTH, HEIGHT, seed);
                finalWorldFrame = doubleWorld.getLevel();
                Coordinate startPos = doubleWorld.getStartPos();
                finalWorldFrame[startPos.getX()][startPos.getY()] = Tileset.PLAYER;
                Interaction interact = new Interaction(doubleWorld, startPos);
                if (command.length() > 0) {
                    finalWorldFrame = interact.moveString(command);
                }
                if (saveOrNot) {
                    saveWorld(doubleWorld);
                }
                return finalWorldFrame;

            }
            case 'L': {
                doubleWorld = loadWorld();
                String rest = input.substring(1, input.length());
                int lenOfRest = rest.length();
                Boolean saveOrNot = false;
                String command;
                if (rest.charAt(lenOfRest - 1) == 'Q' && rest.charAt(lenOfRest - 2) == ':') {
                    saveOrNot = true;
                    command = rest.substring(0, lenOfRest - 2);
                } else {
                    command = rest;
                }
                Coordinate startPos = getPlayerPosition(finalWorldFrame);
                Interaction interact = new Interaction(doubleWorld, startPos);
                finalWorldFrame = interact.moveString(command);
                if (saveOrNot) {
                    saveWorld(doubleWorld);
                }
                return finalWorldFrame;

            }
            default: {
                return finalWorldFrame;
            }
        }
    }


    private Coordinate getPlayerPosition(TETile[][] world) {
        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                if (world[i][j].equals(Tileset.PLAYER)) {
                    return new Coordinate(i, j);
                }
            }
        }
        return null;
    }

}
