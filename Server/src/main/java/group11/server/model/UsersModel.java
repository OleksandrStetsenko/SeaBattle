package group11.server.model;

import group11.protocol.*;
import static group11.server.model.XMLFile.readFromXML;
import static group11.server.model.XMLFile.writeToXML;
import java.io.File;
import java.io.Serializable;
import java.net.Socket;
import java.util.*;
import java.util.Observable;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.log4j.*;
import org.xml.sax.SAXException;
/**
 *
 * @author Lukyanenko Yulia
 */
public class UsersModel extends Observable implements Model, Serializable {
    
    private Map<User, Socket> activeUsers = new HashMap<User, Socket>();
    private Set<User> users = new HashSet<User>();
    private File file;
    private final static String FILE_NAME = "usersList.xml";
    private static final Logger log = Logger.getLogger(group11.server.model.UsersModel.class);
    
    /**
     * Create new model.
     */
    public UsersModel() {
        initModel();
    }
    
    /**
     * Initialization of model(set list of users from file).
     */
    public void initModel() {
        file = new File(FILE_NAME);
        setList(loadList());
    }
    
    /**
     * Storage The list of all registred users in the xml file.
     */
    public void storageList() {
        try {
            writeToXML(users, file);
        } catch (Exception ex) {
           log.error("Can not save list in file. " + ex.getMessage());
           this.setChanged();
           this.notifyObservers("Can not save list in file."); 
        }
    }

    /**
     * Load the list of users from the xml file
     * @return - The model - list of all registed users
     */
    public Set<User> loadList() {
        try {
            if (file.exists()) {
                readFromXML(users, file);
                log.info("file is full");
            }
            } catch (ParserConfigurationException ex) {
            log.error(" ParserConfigurationException ", ex);
        } catch (SAXException ex) {
            log.error(" SAXException ", ex);
        } catch (Exception ex) {
            log.error("Can not load list of users. " + ex.getMessage(), ex);
            this.setChanged();
            this.notifyObservers("Can not load list of users.");
        }
        for(User us :users){
            System.out.println(us.toString());
        }
        return users;
    }
    
    /**
     * Add on list new user when registration 
     * @param user - Subscriber who has just joined
     * @param socket - Connect the user to the server
     */
    public synchronized void addUser(User user, Socket socket) {
        user.setState(User.State.expected);
        users.add(user);
        activeUsers.put(user, socket);
        log.info( " --- User "+user.getNick() + "; socket "+socket.getPort());
    }
    
    /**
     * Add on list the user when authorization
     * @param user - Subscriber who has just joined
     * @param socket - Connect the user to the server
     */
    public synchronized void addActiveUser(User user, Socket socket) {
        user.setState(User.State.expected);
        activeUsers.put(user, socket);
    }
    
    /**
     * Removing active connect of the list of connections
     * @param user - The user whos connection remove
     */
    public synchronized void removeActiveUser(User user) {
        Iterator it = activeUsers.keySet().iterator();
        while (it.hasNext()) {
            if (!it.equals(user)) {
                it.remove();
            }
        }
    }

    public Observable observable() {
        return this;
    }
    
    /**
     * The list of all registed users from model.
     * @return The list of all registred users.
     */
    public Set<User> getUsersList() {
        return users;
    }
    
    /**
     * Get users (who is expecting the game) from model.
     * @return The list of users (who is expecting the game) from model.
     */
    public Set<User> getExpectingUsersList() {
        Set<User> usersExpecting  = new HashSet<User>();
        for(User user : activeUsers.keySet()) {
            if(user.getState().equals(User.State.expected)) {
                usersExpecting.add(user);
                //System.out.println(" User : ["+user.getNick()+"] "+user.getState());
            }
        }
        return usersExpecting;
    }

    /**
     * The notification for observer of changes in the list of users
     */
    public void setListChanged() {
        this.setChanged();
        this.notifyObservers();
    }

    /**
     * Set Users of list.
     * @param users The list of users
     */
    public void setList(Set<User> users) {
        this.users = users;
    }
    
    /**
     * Get Active users (who is expecting or playing) with their socket from model.
     * @return The map Active users (whos is expecting or playing) with their 
     * socket from model.
     */
    public Map<User, Socket> getMapActiveUsers() {
        return activeUsers;
    }

    /**
     * Get Active users (who is expecting or playing) from model.
     * @return The list of users whos is expecting or playing from model.
     */
    public Set<User> getActiveUser() {
        return activeUsers.keySet();
    }
    
    /**
     * Get sockets of all active users(expecting or playing)
     * @return The list of sockets of all active users(expecting or playing)
     * from model.
     */
    public Set<Socket> getConnection() {
        return (Set<Socket>)activeUsers.values();
    }
}
