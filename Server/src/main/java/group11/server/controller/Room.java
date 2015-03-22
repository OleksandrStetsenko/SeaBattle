package group11.server.controller;

import group11.protocol.*;
import group11.protocol.User;
import group11.protocol.model.*;
import group11.protocol.pack.*;
import group11.server.model.Model;
import java.io.IOException;
import java.util.Set;
import org.apache.log4j.Logger;

/**
 *
 * @author Lukyanenko Yuliya
 */
public class Room implements Runnable {

    User player1, player2;
    Transport protocol1, protocol2, protocol;
    SeaBattleModel seaBattle;
    Set<ConnectionHandler> connections;
    private Model userModel;
    private ShootMessage shootMessage = null;
    private Thread thread;
    private static final Logger log
            = Logger.getLogger(group11.server.controller.ConnectionHandler.class);

    /**
     * Create the room for game to users
     *
     * @param player1 - player which first send request about game.
     * @param protocol1 - His connect(player1).
     * @param player2 - invited player for game
     * @param protocol2 - His connect(player2).
     * @param userModel - List of users
     * @param connections - List of observers
     */
    public Room(User player1, Transport protocol1, User player2,
            Transport protocol2, Model userModel, Set<ConnectionHandler> connections) {
        this.userModel = userModel;
        this.player1 = player1;
        this.player2 = player2;
        this.protocol1 = protocol1;
        this.protocol2 = protocol2;
        this.connections = connections;

        seaBattle = new SeaBattleModel(player1, player2);
        thread = new Thread(this);
        log.info(" Room is created. ");
        thread.start();
    }
    /**
     * Receipt and processing of events.
     */
    public void run() {
        try {
            protocol1.send(new FieldToSend(player1.getNick(),
                    seaBattle.getFirstPlayerFieldModel()));
        } catch (IOException ex) {
            log.error("IOE when first Send Field for [" + player1.getNick() + "]", ex);
            getConnection(protocol1).exitThread();
        }
        try {
            protocol2.send(new FieldToSend(player2.getNick(),
                    seaBattle.getSecondPlayerFieldModel()));
        } catch (IOException ex) {
            log.error("IOE when first Send Field for [" + player2.getNick() + "]", ex);
            getConnection(protocol2).exitThread();
        }
        log.info("Field for " + protocol1.getSocket().getLocalPort() + "=>"
                + protocol1.getSocket().getPort());
        log.info("Field for " + protocol2.getSocket().getLocalPort() + "=>"
                + protocol2.getSocket().getPort());
        while (!thread.isInterrupted()) {
            dispatchEvent();
        }
    }
    
    /**
     * 
     * @param nick - One of the players on this game.
     * @return connection for user with this nick.
     */
    public Transport getConnect(String nick) {
        Transport tr;
        if (nick.equals(player1.getNick())) {
            tr = protocol1;
        } else {
            tr = protocol2;
        }
        return tr;
    }

    private void dispatchEvent() {
        if (shootMessage == null) {
            return;
        }
        log.info(" Recieve shootMessage ");
        User us;
        String nick = shootMessage.getShooter();
        String opponentNick;
        if (nick.equals(player1.getNick())) {
            us = player1;
            opponentNick = player2.getNick();
        } else {
            us = player2;
            opponentNick = player1.getNick();
        }

        seaBattle.doShotByOpponent(us, shootMessage.getX(), shootMessage.getY());

        if (us.equals(seaBattle.getFirstUser())) {
            sendField(opponentNick, seaBattle.getSecondPlayerFieldModel());
        }

        if (us.equals(seaBattle.getSecondUser())) {
            sendField(opponentNick, seaBattle.getFirstPlayerFieldModel());
        }

        if (seaBattle.isEndGame()) {
            endGame();
            close();
        }
        shootMessage = null;
    }

    private void endGame() {
        if (seaBattle.getFirstPlayerFieldModel().getNumLiveShips() == 0) {
            try {
                protocol1.send(new Msg("Lost"));
            } catch (IOException ex) {
                log.error("IOE when Send Lost for [" + player1.getNick() + "]", ex);
                getConnection(protocol1).exitThread();
            }
            try {
                protocol2.send(new Msg("Won"));
            } catch (IOException ex) {
                log.error("IOE when Send Won for [" + player2.getNick() + "]", ex);
                getConnection(protocol2).exitThread();
            }
            player2.setRang(player2.getRang() + 1);
            log.info(" The User [" + player2.getNick() + "] is Won");
        } else {
            try {
                protocol1.send(new Msg("Won"));
            } catch (IOException ex) {
                log.error("IOE when Send Won for [" + player1.getNick() + "]", ex);
                getConnection(protocol1).exitThread();
            }
            try {
                protocol2.send(new Msg("Lost"));
            } catch (IOException ex) {
                log.error("IOE when Send Lost for [" + player2.getNick() + "]", ex);
                getConnection(protocol2).exitThread();
            }
            player1.setRang(player1.getRang() + 1);
            log.info(" The User [" + player1.getNick() + "] is Won");
        }
    }
    /**
     * 
     * @return The identifier of this Thread.
     */
    public long getID() {
        return thread.getId();
    }
    
    /**
     * 
     * @param shootMessage Set shootMessage
     */
    public void setShootMessage(ShootMessage shootMessage) {
        this.shootMessage = shootMessage;
        log.info(" Receive shootMasage.");
    }

    /**
     *  Close the game.
     */
    public void close() {
        disconnectUser(player1);
        disconnectUser(player2);
        log.info("The Room is closing. ");
        userModel.setListChanged();
        thread.interrupt();
    }

    private void disconnectUser(User user) {
        user.setPlayWith(" ");
        user.setRoomID(0);
        user.setState(User.State.expected);
        log.info(" User [" + user.getNick()
                + "] state[ expected] = " + user.getState());
    }

    private void sendField(String nick, Field field) {
        try {
            protocol1.send(new FieldToSend(nick, field));
        } catch (IOException ex) {
            log.error("IOE when Send FieldToSend for ["
                    + player1.getNick() + "]", ex);
            getConnection(protocol1).exitThread();
        }
        try {
            protocol2.send(new FieldToSend(nick, field));
        } catch (IOException ex) {
            log.error("IOE when Send FieldToSend for ["
                    + player2.getNick() + "]", ex);
            getConnection(protocol2).exitThread();
        }
    }

    private ConnectionHandler getConnection(Transport protocol) {
        ConnectionHandler connect = null;
        for (ConnectionHandler connection : connections) {
            if (connection.equals(protocol)) {
                connect = connection;
                break;
            }
        }
        return connect;
    }
}
