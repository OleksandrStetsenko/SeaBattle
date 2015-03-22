package group11.server.model;
import group11.protocol.*;
import java.net.Socket;
import java.util.Map;
import java.util.Observable;
import java.util.Set;
/**
 *
 * @author Lukyanenko Yulia
 */
public interface Model {
    
    /**
     * Add on list new user when registration 
     * @param user - Subscriber who has just joined
     * @param socket - Connect the user to the server
     */
    void addUser(User user, Socket socket);
    
    /**
     * Add on list the user when authorization
     * @param user - Subscriber who has just joined
     * @param socket - Connect the user to the server
     */
    void addActiveUser(User user, Socket socket);
    
    /**
     * Removing active connect of the list of connections
     * @param user - The user whos connection remove
     */
    void removeActiveUser(User user);
    
    /**
     * The notification for observer of changes in the list of users
     */
    void setListChanged();
    
    /**
     * Set Users of list.
     * @param users The list of users
     */
    void setList(Set<User> users);
    
    /**
     * Storage The list of all registred users in the xml file.
     */
    void storageList();
    
    /**
     * Load the list of users from the xml file
     * @return - The model - list of all registed users
     */
    Set<User> loadList();
    
    /**
     * The list of all registed users from model.
     * @return The list of all registred users.
     */
    Set<User> getUsersList();
    
    /**
     * Get Active users (who is expecting or playing) from model.
     * @return The list of users whos is expecting or playing from model.
     */
    Set<User> getActiveUser();
    
    /**
     * Get Active users (who is expecting or playing) with their socket from model.
     * @return The map Active users (whos is expecting or playing) with their 
     * socket from model.
     */
    Map<User, Socket> getMapActiveUsers();
    
    /**
     * Get sockets of all active users(expecting or playing)
     * @return The list of sockets of all active users(expecting or playing)
     * from model.
     */
    Set<Socket> getConnection();
    
    /**
     * Get users (who is expecting the game) from model.
     * @return The list of users (who is expecting the game) from model.
     */
    Set<User> getExpectingUsersList();
    
    
    Observable observable();
    
}
