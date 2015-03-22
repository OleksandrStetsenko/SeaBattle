package group11.protocol.model;

import org.apache.log4j.Logger;


//import view.ModelSubscriber;
import group11.protocol.*;

import java.io.Serializable;

/**
 * 
 * Class for description state of full playground
 */

public class SeaBattleModel implements Serializable {

    static Logger log = Logger.getLogger(SeaBattleModel.class.getName());

    private Field firstPlayerFieldModel;
    private Field secondPlayerFieldModel;
    private User firstUser;
    private User secondUser;
    private boolean endGame = false;
    private boolean enableShootFirsUser;
    private boolean enableShootSecondUser;

    
    /**
     * Default constructor for "Field"
     * @param User - user1 (opponent for user2)
     * @param User - user2 (opponent for user1)
     * in this method playfield size(4x4) and max ship length(4) are hardcoded
     */
    public SeaBattleModel(User user1, User user2) {
        this(user1, user2, 10, 10, 4);
    }

    /**
     * Another constructor for "Field"
     * @param User - user1 (opponent for user2)
     * @param User - user2 (opponent for user1)
     * @param dx - width playfield
     * @param dy - height playfield
     * @param numShip - max ship length
     */
    public SeaBattleModel(User user1, User user2, int dx, int dy, int numShip) {
        firstUser = user1;
        secondUser = user2;

        firstPlayerFieldModel = new Field(dx, dy, numShip);
        secondPlayerFieldModel = new Field(dx, dy, numShip);

        enableShootFirsUser = true;
    }

    /**
     * Shoot by opponent. If current player first player,
     * then opponent - second player
     */
    public void doShotByOpponent(User currentUser, int x, int y) {

        log.debug("shooting x:" + x + " y:" + y);
     
        // if local prayer
        if (currentUser.equals(firstUser) && enableShootFirsUser) {
            //if current cell is marked - do nothing
            if (secondPlayerFieldModel.getCell(x, y).isMark()) {
                return;
            }
            if (secondPlayerFieldModel.doShot(x, y) == Field.SHOOT_MISSED) {
                // if missed - change current player
                enableShootFirsUser = false;
                enableShootSecondUser = true;
            }
        }
        // if enemy shoot
        if (currentUser.equals(secondUser) && enableShootSecondUser) {
            //if current cell is marked - do nothing
            if (firstPlayerFieldModel.getCell(x, y).isMark()) {
                return;
            }
            if (firstPlayerFieldModel.doShot(x, y) == Field.SHOOT_MISSED) {
                // if missed - change current player
                enableShootFirsUser = true;
                enableShootSecondUser = false;
            }
        }



        if ( (firstPlayerFieldModel.getNumLiveShips() == 0) || (secondPlayerFieldModel.getNumLiveShips() == 0) ) {

            endGame = true;
        }
    }


    public Field getFirstPlayerFieldModel() {
        return firstPlayerFieldModel;
    }

    public Field getSecondPlayerFieldModel() {
        return secondPlayerFieldModel;
    }

    public User getFirstUser() {
        return firstUser;
    }

    public User getSecondUser() {
        return secondUser;
    }

    public boolean isEnableShootFirsUser() {
        return enableShootFirsUser;
    }

    public boolean isEnableShootSecondUser() {
        return enableShootSecondUser;
    }

    public void setEnableShootFirsUser(boolean enableShootFirsUser) {
        this.enableShootFirsUser = enableShootFirsUser;
    }

    public void setEnableShootSecondUser(boolean enableShootSecondUser) {
        this.enableShootSecondUser = enableShootSecondUser;
    }
    
    public boolean isEndGame() {
        return endGame;
    }
}

