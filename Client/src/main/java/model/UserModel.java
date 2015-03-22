package model;

import org.apache.log4j.Logger;

import java.util.HashSet;
import java.util.Observable;
import java.util.Set;

import group11.protocol.User;

public class UserModel extends Observable {

    private static Logger log = Logger.getLogger(UserModel.class.getName());
    private Set<User> userSet = new HashSet<User>();
    private String myUsername;

    public UserModel(Set<User> userSet, String myUsername) {
        this.myUsername = myUsername;
        updateUserSet(userSet);
    }

    public void updateUserSet(Set<User> userSet) {
        this.userSet = userSet;
        setChangesAndNotify();
    }

    public Set<User> getUserSet() {
        return userSet;
    }

    public String getMyUsername() {
        return myUsername;
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
