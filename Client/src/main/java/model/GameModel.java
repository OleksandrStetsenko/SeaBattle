package model;

import group11.protocol.model.Field;
import org.apache.log4j.Logger;

import java.util.Observable;

public class GameModel extends Observable {

    private static Logger log = Logger.getLogger(GameModel.class.getName());

    private Field meFieldModel;
    private Field opponentFieldModel;

    private String me;
    private String opponent;

    private String currentPlayer;

    public GameModel(Field meField, String me, String opponent) {

        log.debug("Creating GameModel. Me - " + me + " opponent - " + opponent);

        setMe(me);
        setMyFieldModel(meField);
        setCurrentPlayer(me);

        setOpponent(opponent);
        setOpponentFieldModel(new Field(10, 10, 4));
    }

    public void updateSeaBattleModel(Field field, String nick) {

        if (nick.equals(getMe())) {
            setMyFieldModel(field);
        } else {
            setOpponentFieldModel(field);
        }
        setChangesAndNotify();
    }

    public void setCurrentPlayer(String currentPlayer) {
        this.currentPlayer = currentPlayer;
        setChangesAndNotify();
    }

    public Field getMyFieldModel() {
        return meFieldModel;
    }

    public Field getOpponentFieldModel() {
        return opponentFieldModel;
    }

    public String getMe() {
        return me;
    }

    public String getOpponent() {
        return opponent;
    }

    public void setMyFieldModel(Field meFieldModel) {
        log.debug("Set my field model. Me - " + getMe());
        this.meFieldModel = meFieldModel;
    }

    public void setOpponentFieldModel(Field opponentFieldModel) {
        log.debug("Set opponent field model. Opponent - " + getOpponent());
        this.opponentFieldModel = opponentFieldModel;
    }

    public void setMe(String me) {
        this.me = me;
    }

    public void setOpponent(String opponent) {
        this.opponent = opponent;
    }

    public String getCurrentPlayer() {
        return currentPlayer;
    }

    public Observable observable() {
        return this;
    }

    /** set changed and notify observers */
    public void setChangesAndNotify() {
        log.debug(Settings.DBG_SetChanges);
        super.setChanged();
        super.notifyObservers();
    }

}
