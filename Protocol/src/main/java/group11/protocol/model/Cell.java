package group11.protocol.model;

import java.io.Serializable;

/**
 * 
 * Class for description single cell of playfield
 */
public class Cell implements Serializable {

    public final static int CELL_WATER = 1;
	public final static int CELL_BORDER = 2;
	public final static int CELL_WELL = 3;
	public final static int CELL_BROKEN = 4;
	public final static int CELL_KILLED = 5;
	public final static int CELL_MISSED = 6;

    public int x;
	public int y;
	private int state;
	private boolean mark;

    /**
     * 
     * Creates a cell at the position on the playfield.
     * By default, a cell in a state "WATER"
     * @param x coordinate
     * @param y coordinate
     */
	public Cell(int x, int y) {
		this.x = x;
		this.y = y;
		this.state = CELL_WATER;
		this.mark = false;
	}


    /**
     * Set cell state
     * @param state cell- state
     */
	public void setState(int state) {
		this.state = state;
	}

    /**
     * Receive cell state
     * @return state
     */
	public int getState() {
		return state;
	}

    /**
     * Check, is cell was marked before
     * @return boolean, "true" if marked
     */
	public boolean isMark() {
		return mark;
	}

    /**
     * Set(or unset) mark on cell
     * @param mark, "true" to set, "false" to unset  
     */
	public void setMark(boolean mark) {
		this.mark = mark;
	}

    @Override
    public String toString() {
        return "[[" + x + "][" + y + "], state:" + state + ", mark: " + mark + "]";
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }
}
