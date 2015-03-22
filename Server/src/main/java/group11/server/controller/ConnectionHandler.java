package group11.server.controller;

import group11.protocol.*;
import group11.protocol.User;
import group11.protocol.pack.*;
import group11.server.model.Model;
import java.io.IOException;
import java.io.Serializable;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashSet;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import org.apache.log4j.Logger;

/**
 *
 * @author Lukyanenko Yulia
 */
class ConnectionHandler implements Runnable, Observer {

    private Socket socket;
    private Model userModel;
    private Transport protocol;
    private boolean newUser = true;
    private static final Logger log =
            Logger.getLogger(group11.server.controller.ConnectionHandler.class);
    private Thread t;
    private User thisUser;
    private Set<ConnectionHandler> connections;

    /**
     *
     * @param socket - This socket
     * @param userModel - The list of users
     * @param connections - The list of all sockets
     * @throws IOException
     */
    public ConnectionHandler(Socket socket, Model userModel, 
            Set<ConnectionHandler> connections) throws IOException {

        this.userModel = userModel;
        this.socket = socket;
        protocol = new TransportXml(socket);

        connections.add(this);
        this.connections = connections;
        //userModel.observable().addObserver(this);
        t = new Thread(this);
        t.start();
    }
    /**
     * Send and receipt event.
     */
    public void run() {

        try {
            protocol.send(new Msg("Ok"));
        } catch (IOException ex) {
            try {
                protocol.close();
            } catch (IOException ex1) {
                log.error("Exit error (closed socket)", ex1);
            }
            log.error("IOException when Send Ok.", ex);
            connections.remove(this);
            userModel.observable().deleteObserver(this); // remove observer
        }
        log.info("Send Ok from : "+socket.getPort());
        try {
            while (true) {
                dispatchEvent();
            }
        } catch (SocketException ex) {
            exitThread();

        } catch (IOException ex) {
            exitThread();
        }
    }

    /*
    Receipt and processing of events.
    */
    private void dispatchEvent() throws IOException, SocketException {

        Object event = protocol.receive();
        if(event == null) {
            return;
        }
        if (event instanceof PlayRequest){
                PlayRequest playWith = (PlayRequest) event;
                sendPlayRequest(playWith.nick);
        }

        if (event instanceof RegAuth) {
            RegAuth regAuth = (RegAuth) event;
            if (regAuth.type == RegAuth.Type.Register) {
                register(new User(regAuth.nick, regAuth.password));
            } else {
                authorization(new User(regAuth.nick, regAuth.password));
            }
        }

        if (event instanceof ShootMessage){
            ShootMessage shoot = (ShootMessage)event;
            setMessageInRoom(shoot);
        }

        if (event instanceof Msg) {
            Msg msg = (Msg) event;
            if (msg.message.equals("Exit")) {
                exitThread();
            } else if (msg.message.equals("ExitGame")) {
                disconnectDuringGame(thisUser);
            } else if (msg.message.equals("YesBattle")) {
                answerForRequest(new Msg("YesBattle"));
            } else if (msg.message.equals("NoBattle")) {
                answerForRequest(new Msg("NoBattle"));
            }
        }
    }

    /* The registration of new user */
    private void register(User user) {
        Set<User> listUsers = userModel.getUsersList();
        String nick = user.getNick();
        newUser = true;
        for (User us : listUsers) {
            if (us.getNick().equals(nick)) {
                newUser = false;
                try {
                    protocol.send(new NickException("The User with a nick ["
                            + nick + "] has already been registred."));
                } catch (IOException ex) {
                    log.error("IOException when Send NickException(register user).", ex);
                    connections.remove(this);
                    userModel.observable().deleteObserver(this); // remove observer
                    //exitThread();
                }
                log.info("The User with a nick [" + nick
                        + "] has already been registred.");
                break;
            }
        }
        if (newUser) {
            userModel.addUser(user, socket);
            thisUser = user;
            userModel.observable().addObserver(this);
            userModel.setListChanged();
        }
    }

    /* The Authorization of user */
    private void authorization(User user) {
        newUser = true;
        for (User us : userModel.getUsersList()) {
            log.info("In userModel "+us.toString());
            if (us.equalsAuthor(user) && 
                    us.getState().equals(User.State.disconnected)) {
                log.info("Send: List of users. The User [" + user.toString()
                        + "] is joined.");
                userModel.addActiveUser(us, socket);
                thisUser = us;
                userModel.observable().addObserver(this);
                userModel.setListChanged();
                newUser = false;
                return;
            } else if (us.equalsAuthor(user) && 
                    (us.getState().equals(User.State.expected) || 
                    us.getState() == User.State.play)) {
                try {
                    protocol.send(new NickException("This User is authorizated."));
                } catch (IOException ex) {
                    log.error("IOException when Send NickException(authorization user).", ex);
                    connections.remove(this);
                    userModel.observable().deleteObserver(this); // remove observer
                }
                log.info("Send: The User with the nick [" + user.toString()
                        + "]is authorizated.");
                newUser = false;
                return;
            }
        }

        if (newUser) {
            try {
                protocol.send(new NickException("This User is not registred."));
            } catch (IOException ex) {
                log.error("IOException when Send NickException (authorization).", ex);
                connections.remove(this);
                userModel.observable().deleteObserver(this); // remove observer
            }
            log.info("Send: The User with the nick ["+ user.toString()
                    + "]is not registred.");
        }
    }

    /*
     Closure of all resources of this connect.
     */
    public void exitThread() {
        
        try {
            Map<User, Socket> map = userModel.getMapActiveUsers();
            if (thisUser != null) {
                if (thisUser.getState().equals(User.State.play)) {
                    disconnectDuringGame(thisUser);
                }
                thisUser.setState(User.State.disconnected);
                map.remove(thisUser);
                log.info("User [" + thisUser.getNick() + "] disconnected.");
            }
            protocol.close();
            connections.remove(this);
            userModel.observable().deleteObserver(this); // remove observer
            if (!map.isEmpty()) {
                userModel.setListChanged();
            }
            userModel.storageList();
        } catch (IOException ex) {
            log.error("Exit error (closed socket)", ex);
        }
    }

    private void disconnectDuringGame(User us) {

        long roomID = us.getRoomID();
        for (Room room : StartServer.listRoom) {
            if (roomID == room.getID()) {
                try {
                    Transport tr = room.getConnect(us.getPlayWith());
                    tr.send(new Msg("Opponent closed the game."));
                    log.info("Send Msg from "+socket.getPort()+" => "+
                            tr.getSocket().getPort());
                } catch (IOException ex) {
                    log.error("IOException when Send Msg.", ex);
                    exitThread();
                }
                room.close();
                StartServer.listRoom.remove(room);
                return;
            }
        }
    }

    /*
     Closure of all resources of all connect.
     */
    public void closeAll() {
        
        Map<User, Socket> map = userModel.getMapActiveUsers();
        Set<Map.Entry<User, Socket>> entries = map.entrySet();
        for (Map.Entry<User, Socket> entry : entries) {
            entry.getKey().setState(User.State.disconnected);
            try {
                entry.getValue().close();
            } catch (IOException ex) {
                log.error("Error when closing socket.", ex);
            }
            userModel.storageList();
        }
    }
    
    /**
     * Called when a change has occurred in the state of the observable.
     * @param o -  The observable object - model(the list of users)
     * @param arg - An argument passed to the notifyObservers method.
     */
    public void update(Observable o, Object arg) {
        if(thisUser.getState().equals(User.State.play)) {
            return;
        }
        try{
            protocol.send(new ListUsers(listWithoutMe(thisUser)));
        } catch(IOException ex){
            log.error("IOException when update - Send ListUsers.", ex);
            exitThread();
        }
        log.info("Update from socket : "+socket.getLocalPort()+"=>"+socket.getPort());
    }

    /*
     Creating a list without this user
    */
    private Set<User> listWithoutMe(User user) {

        Set<User> withoutMe = new HashSet<User>();
        Set<User> list = userModel.getExpectingUsersList();
        for (User us : list) {
            if (!us.equalsAuthor(user)) {
                withoutMe.add(us);
            }
        }
        return withoutMe;
    }

    /*
     Connect search by user.
    */
    private Transport findProtocol(User user){

        Transport transport = null;
        Socket socket = null;
        Map<User, Socket> map = userModel.getMapActiveUsers();
       /* Set<Map.Entry<User, Socket>> entries = map.entrySet();
        for (Map.Entry<User, Socket> entry : entries) {
            log.info("Map<User,Socket> ["+entry.getKey().toString()+"] socket => port: "+
                    entry.getValue().getPort()+" hashCode = "+entry.getKey().hashCode());
        }*/
        if (map.containsKey(user)) {
            socket = map.get(user);
           // log.info("find socket.port = "+socket.getPort());
        }
        for(ConnectionHandler connection : connections) {
            if(socket.equals(connection.getProtocol().getSocket())) {
                log.info("socket - connection :"+socket.getPort() +
                        " - "+connection.getProtocol().getSocket().getPort());
                transport = connection.getProtocol();
                break;
            }
        }
        return transport;
    }

    /*
    Find the user by nick
    */
    private User getUserByNick(String nick){

        User user = null;
        Set<User> list = userModel.getActiveUser();
        for(User us : list) {
            if(nick.equals(us.getNick())) {
                user = us;
                break;
            }
        }
        return user;
    }

    /*
    Answer for request  about game.
    */
    private void answerForRequest(Serializable obj) {
        
        String playWith = thisUser.getPlayWith();
        User opponentUser = getUserByNick(playWith);
        Transport opponentProtocol = findProtocol(opponentUser);
        if (obj instanceof Msg && ((Msg) obj).message.equals("YesBattle")) {
            Room game = new Room( thisUser, protocol, opponentUser,
                    opponentProtocol, userModel, connections);
            StartServer.listRoom.add(game);
            goToGame(thisUser, game.getID());
            goToGame(opponentUser, game.getID());
        }
        else {
            noGame(thisUser);
            noGame(opponentUser);
            try {
                opponentProtocol.send(obj);
                log.info("Send " + obj.getClass() + " " + socket.getLocalPort() +
                        "=>" + socket.getPort());
            } catch (IOException ex) {
                log.error("IOException when Send "+obj.getClass(), ex);
                exitThread();
            }
        }
        userModel.setListChanged();
    }

    private void goToGame(User user, long roomID) {
        user.setRoomID(roomID);
        user.setState(User.State.play);
    }

    private void noGame(User user) {
        user.setRoomID(0);
        user.setPlayWith(" ");
    }

    /*
    Send request about game.
    */
    private void sendPlayRequest(String nick) {
        
        User opponent = getUserByNick(nick);
        if(!opponent.getPlayWith().equals(" ")){
            try {
                protocol.send(new ListUsers(listWithoutMe(thisUser)));
            } catch (IOException ex) {
                log.error(" IOException when send ListUsers ",ex);
                exitThread();
            }
            return;
        }
        opponent.setPlayWith(thisUser.getNick());
        thisUser.setPlayWith(opponent.getNick());
        try {
            findProtocol(opponent).send(new PlayRequest(thisUser.getNick()));

        } catch (IOException ex) {
            log.error("IOException when Send PlayRequest.", ex);
            exitThread();
        }
        log.info("Request for user ["+opponent.getNick()+"]");
    }

    /*
    Send the massage  in  rooms.
     */
    private void setMessageInRoom(ShootMessage shoot) {
        String nick = shoot.getShooter();
        User us = getUserByNick(nick);
        long roomID = us.getRoomID();
        for (Room room : StartServer.listRoom) {
            if (roomID == room.getID()) {
                room.setShootMessage(shoot);
            }
        }
    }

    private Transport getProtocol() {
        return protocol;
    }
    
}
