package group11.protocol.model;

import org.apache.log4j.Logger;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 
 * Class for description single playfield
 */

public class Field implements Serializable {

    private static final Logger log = Logger.getLogger(Field.class.getName());

	public final static int SHOOT_MISSED = 1;
	public final static int SHOOT_BROKEN = 2;
	public final static int SHOOT_KILLED = 3;
	public final static int SHOOT_WON = 4;

	private Cell[][] cells;
	private ArrayList<Ship> ships;
	private int width;
	private int height;
	private int maxShip;
	private int numLiveShips;

	
	 /**
     * Default constructor for "Field"
     * @param x - width
     * @param y - height
     * @param ship - max ship size
     */
    public Field(int x, int y, int ship) {
		setDimention(x, y, ship);
		setShipsToField();
	}


	private void setDimention(int x, int y, int ship) {
		setWidth(x);
		setHeight(y);
		setMaxShip(ship);
	}

	
    /**
     * Set ships alignment on the field (only 10 ships allowed)
     */
	public void setShipsToField() {
        setNumLiveShips(10);
        cells = new Cell[getWidth()][getHeight()];
        for(int j = 0; j < getHeight(); j++) {
            for(int i = 0; i < getWidth(); i++) {
                cells[i][j] = new Cell(i, j);
            }
        }


        ships = new ArrayList<Ship>();
        for(int i = getMaxShip(); i > 0; i--) {
            for(int j = (getMaxShip() - i +1 ); j > 0; j--) {
                Ship ship = new Ship(i);

                createRandomCoordinates(ship);

                ships.add(ship);
            }
        }

        for(int j = 0; j < getHeight(); j++) {
            for(int i = 0; i < getWidth(); i++) {
                if (getCell(j,i).getState() == Cell.CELL_BORDER) {
                    getCell(j,i).setState(Cell.CELL_WATER);
                }
            }
        }

	}

	 /**
     * Check, if cell free on the field
     * @return "true" if cell state is "water"(this means, cell is free)
     */
    public boolean isCellWater(int x, int y) {
        if ( isBound(x, y) ) {
            return ( getCell(x, y).getState() == Cell.CELL_WATER );
        } else {
            return false;
        }
    }
    
    /**
     * Set a "border" of empty cell on ship perimeter 
     * @param x - coordinate cell
     * @param y - coordinate cell
     * @param borderList - List of cell, border included with
     */
    public void setBorder(int x, int y, List<Cell> borderList) {
        if ( isBound(x, y) ) {
            getCell(x, y).setState(Cell.CELL_BORDER);
            borderList.add(getCell(x, y));
        }
    }
    /**
     * @param x - coordinate cell
     * @param y - coordinate cell
     * @param cellList - List of cell, marked "CELL_WELL"
     */
    public void setWell(int x, int y, List<Cell> cellList) {
        if ( isBound(x, y) ) {
            getCell(x, y).setState(Cell.CELL_WELL);
            cellList.add(getCell(x,y));
        }
    }


    private void createRandomCoordinates(Ship ship) {

        int x = 0;
        int y = 0;
        int dx = 0;
        int dy = 0;

        List<Cell> cellList = new ArrayList<Cell>();
        List<Cell> borderList = new ArrayList<Cell>();

        do {
            Random rand = new Random();
            x = rand.nextInt(getWidth());
            y = rand.nextInt(getHeight());
            dx = 0;
            dy = 0;
            if (rand.nextInt(2) == 1) {
                dx = 1;
            } else {
                dy = 1;
            }
        } while (! checkCellsForShip(ship, x, y, dx, dy, cellList, borderList) );

        ship.setListCells(cellList);
        ship.setListBorders(borderList);

    }

    private boolean checkCellsForShip(Ship ship, int x, int y, int dx, int dy, List<Cell> cellList, List<Cell> borderList) {

        cellList.clear();
        borderList.clear();

        int i, m, n;

        for(i = 0; i < ship.getSize(); i++) {

            //ship
            m = y + i * dy;
            n = x + i * dx;
            if (isCellWater(m, n)) {
                setWell(m, n, cellList);

            } else {
                //if place for ship is not valid - return water
                for (Cell cell : cellList) {
                    cell.setState(cell.CELL_WATER);
                }
                return false;
            }

            //set borders
            m = y + i * dy - dx;
            n = x + i * dx - dy;
            setBorder(m, n, borderList);

            m = y + i * dy + dx;
            n = x + i * dx + dy;
            setBorder(m, n, borderList);

        }

        //set borders
        for(i = -1; i < 2; i++) {

            m = y + i * dx - dy;
            n = x + i * dy - dx;
            setBorder(m, n, borderList);


            m = y + i * dx + (dy * ship.getSize());
            n = x + i * dy + (dx * ship.getSize());
            setBorder(m, n, borderList);

        }

        return true;

    }

    /**
     * Do shoot and mark ships and cells
     * @param x coordinate
     * @param y coordinate
     * @return shoot result
     */
	public int doShot(int x, int y) {

        Cell cell = getCell(x, y);

        int shootResult = Field.SHOOT_MISSED;

        //set current cell mark
        cell.setMark(true);

		if (cell.getState() == Cell.CELL_WELL) {

            //if cell was well - mark as broken
            cell.setState(Cell.CELL_BROKEN);

            //getting ship and verify state
            Ship ship = getShipByCell(cell);
            if(ship != null) {
                if (ship.getHealth() != 0) {

                    //reduce health
                    ship.setHealth(ship.getHealth() - 1);

                    if (ship.getHealth() == 0) {
                        //if ship has 0 health - it killed
                        setNumLiveShips(getNumLiveShips() - 1);

                        //set state of ship as killed
                        ship.setState(Ship.SHIP_KILLED);

                        //all cells of this ship mark as killed
                        for(Cell e : ship.getListCells()) {
                            e.setState(Cell.CELL_KILLED);
                        }

                        //and all borders mark as missed - shoot there is no reason :)
                        for(Cell e : ship.getListBorders()) {
                            e.setState(Cell.CELL_MISSED);
                            e.setMark(true);
                        }

                        //maybe you won?
                        if (getNumLiveShips() == 0) {
                            shootResult = Field.SHOOT_WON;
                        } else {
                            //shoot result will - killed
                            shootResult = Field.SHOOT_KILLED;
                        }

                    } else {

                        //if haalth is not 0 - this ship are broken
                        ship.setState(Ship.SHIP_BROKEN);
                        shootResult = Field.SHOOT_BROKEN;
                    }
                }
            }
		} else {

            //if cell was not well - and current cell is border or water - mark cell as missed
			if ( (cell.getState() == Cell.CELL_BORDER) || (cell.getState() == Cell.CELL_WATER)) {
				cell.setState(Cell.CELL_MISSED);

                //you miss, sorry :(
                shootResult = Field.SHOOT_MISSED;
			}
		}

        return shootResult;

	}

    private Ship getShipByCell(Cell cell) {
        for(Ship ship : getShips()) {
            for(Cell shipCell : ship.getListCells()) {
                if (shipCell.x == cell.x && shipCell.y == cell.y) {
                    return ship;
                }
            }
        }
        return null;
    }


    /**
     * Verify current indexes contained in game field
     * @param x coordinate
     * @param y coordinate
     */
 	public boolean isBound(int x, int y) {
        return (x >= 0)
                && (x < getWidth())
                && (y >= 0)
                && (y < getHeight());
	}


	public Cell getCell(int x, int y) {
		return cells[x][y];
	}

	public int getWidth() {
		return width;
	}

    private void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	private void setHeight(int height) {
		this.height = height;
	}

	public int getMaxShip() {
		return maxShip;
	}

	private void setMaxShip(int maxShip) {
		this.maxShip = maxShip;
	}

	public int getNumLiveShips() {
		return numLiveShips;
	}

	public void setNumLiveShips(int numLiveShips) {
		this.numLiveShips = numLiveShips;
	}

    public ArrayList<Ship> getShips() {
        return ships;
    }

    public Cell[][] getCells() {
        return cells;
    }

    public void setCells(Cell[][] cells) {
        this.cells = cells;
    }

    public void setShips(ArrayList<Ship> ships) {
        this.ships = ships;
    }
}
