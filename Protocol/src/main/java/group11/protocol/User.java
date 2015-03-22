package group11.protocol;

import java.io.Serializable;

/**
 *
 * @author Lukyanenko Yulia, Tregub Vitaliy
 */
public class User implements Serializable {
	/** User name (nick), used as unique identifier*/
	private String nick;		
	/** User password*/
    private String password;	
    /**User rank*/
    private int rang = 0;		
    public enum State {			
        play, expected, disconnected, banned
    };
	/**@param state- user state, "disconnected" as default */
    State state;			
    /**nick of game partner, or= " " if there in no one */
    private String playWith;	
    /**Unique game room identifier*/
    private long roomID;		

    
    /**
     * Default constructor for "User"
     * @param nick - Username string
     * @param password - Password string
     */
    public User(String nick, String password) {/**Конструктор по умолчанию, требует ник и пароль*/
    	setNick(nick);
    	setPassword(password);
        state = State.disconnected;
        this.playWith=" ";
        this.roomID=0;
    }
    
    /**
     * @depricated
     * check for incorrect nick and password(xml forbidden symbol like "<")
     * now checking on client side
     */
    public boolean check(){		
    	String ch=getNick()+getPassword();
    	if (ch.equals(null)||ch.equals("")||ch.contains("<")||ch.contains(">")) {
    		return false;
    	}
    	else {
    		return true;
    	}
    }
    
    public void setRang(int rang) {
        this.rang = rang;
    }
    
    public void setRoomID(long roomID) {
        this.roomID = roomID;
    }
    
    public void setState(State state){
        this.state = state;
    }
    
    
    public void setPlayWith(String playWith){
        this.playWith = playWith;
    }
    
    public State getState() {
        return state;
    }
    
    public Long getRoomID() {
        return roomID;
    }
    
    public void setNick(String nick) {
        this.nick = nick;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getNick() {
        return nick;
    }
    public String getPassword() {
        return password;
    }
    public int getRang() {
        return rang;
    }
    public String getPlayWith() {
        return playWith;
    }
    
    public boolean equalsAuthor(User user) {
        if(user == this)
            return true;
        if(user == null)
            return false;
        if(getClass() != user.getClass())
            return false;
        User other =  user;
        return nick.equals(other.nick)
		&& password.equals(other.password);
    }
    
    /**
     * 
     * Important note: Users are equals if equals their nicks and passwords
     */
    @Override
    public boolean equals(Object obj) {
        if(obj == this)
            return true;
        if(obj == null)
            return false;
        if(getClass() != obj.getClass())
            return false;
        User other = (User) obj;
	return nick.equals(other.nick)
		&& password.equals(other.password)
                && rang == other.rang
                && state.equals(other.state)
                && playWith.equals(other.playWith)
                && roomID == other.roomID ;
    }    
    
    /**
     * 
     * Important note: hashCode of User with identical nick are identical 
     */
    @Override
    public int hashCode() { 
        return nick.hashCode();       
    }
    
    @Override
    public String toString() {
        return "Nick: [" + this.nick + "] password: [" + this.password +"] rank ["+this.getRang()+ "] satete ["
                +this.getState()+"] playWith ["+this.getPlayWith()+"] roomID ["+this.getRoomID()+"] " ;
    }
	
}
