package byog.Core;

import java.io.Serializable;

public class Coordinate implements Serializable{
    /* Used to represent coordinates in the world*/
    private int x = 0;
    private int y = 0;
    private static final long serialVersionUID = 123123123123434123L;


    public Coordinate(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }
}
