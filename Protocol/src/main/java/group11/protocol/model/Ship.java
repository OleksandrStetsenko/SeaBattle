package group11.protocol.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * Class for description single ship
 */
public class Ship implements Serializable {

	public final static int SHIP_WELL = 1;
	public final static int SHIP_BROKEN = 2;
	public final static int SHIP_KILLED = 3;

	private int size;
	private int health;
	private int state;
	private List<Cell> listCells;
    private List<Cell> listBorders;

    /**
     * Default constructor for "Ship"
     * @param size - ship size
     */
    public Ship(int size) {
        this.size = size;
        this.health = size;
        this.state = Ship.SHIP_WELL;

        this.listCells = new ArrayList<Cell>();
		this.listBorders = new ArrayList<Cell>();
    }

    public void setListCells(List listCells) {
        this.listCells = listCells;
    }

    public void setListBorders(List<Cell> listBorders) {
        this.listBorders = listBorders;
    }


    public int getSize() {
        return size;
    }


    public int getState() {
        return state;
    }

    public List<Cell> getListCells() {
        return listCells;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public void setState(int state) {
        this.state = state;
    }

    public List<Cell> getListBorders() {
        return listBorders;
    }

}
