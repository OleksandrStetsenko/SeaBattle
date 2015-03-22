package group11.protocol.pack;

import group11.protocol.User;
import group11.protocol.User.State;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 *
 * @author Lukyanenko Yulia, Tregub Vitaliy
 */
public class ListUsers extends Pack  {
    public Set<User> users;

    /**
     * 
     * empty constructor, used only for FactoryMethod
     */ 
    public ListUsers(){}
    
    
    public ListUsers(Set<User> users) {
        this.users = users;
    }
    
    public String getLogMessage(){
    	return " Send  ListUsers ";
    }
    
    /**
     * method for pack ListUsers into incoming Document
     * @param doc - incoming document
     * @return Document with ListUsers packed in
     */
    public Document writeXml(Document doc){
		Element root = doc.createElement("ListUsers");  
    	doc.appendChild(root);
        for (User user: users){
        	writeUserXml(doc, root,user);
        }
        return doc;
	}
    
    /**
     * method for pack User into incoming Document
     * @param doc - incoming document
     * @param root - root element of incoming document to write Field
     * @param field - Field to write
     * @return Document with Field packed in
     */
	public Document writeUserXml(Document doc, Element root, User user){
		Element E = doc.createElement("User");
        root.appendChild(E);
       
        writeDataXml(doc,E,"nick",user.getNick());
        writeDataXml(doc,E,"password",user.getPassword());
        writeDataXml(doc,E,"rang",Integer.toString(user.getRang()));
        writeDataXml(doc,E,"state",""+(user.getState()));
        writeDataXml(doc,E,"playWith",user.getPlayWith());
        writeDataXml(doc,E,"roomID",Long.toString(user.getRoomID()));
        return doc;
	}

	/**
	 * 
     * method for unpack ListUsers from incoming Document
     * @param doc - incoming document
     * @return ListUsers unpacked from incoming document
     */
	public ListUsers readXml(Document doc){
		Set<User> lu= new HashSet<User>();
		User user;
		NodeList nl = doc.getElementsByTagName("User");
		for(int i = 0; i < nl.getLength(); i++) {
			Element el = (Element)nl.item(i);
			
			user = new User(el.getElementsByTagName("nick").item(0).getFirstChild().getNodeValue(),
					el.getElementsByTagName("password").item(0).getFirstChild().getNodeValue());
			user.setRang(Integer.parseInt(el.getElementsByTagName("rang").item(0).getFirstChild().getNodeValue()));
			user.setState(State.valueOf(el.getElementsByTagName("state").item(0).getFirstChild().getNodeValue()));
			user.setPlayWith(el.getElementsByTagName("playWith").item(0).getFirstChild().getNodeValue());  
			user.setRoomID(Long.parseLong(el.getElementsByTagName("roomID").item(0).getFirstChild().getNodeValue()));
			lu.add(user);
		}
		return new ListUsers(lu);
	}	
		
	
}
