package group11.protocol.pack;

import java.io.Serializable;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author Tregub Vitaliy
 */
public class RegAuth  extends Pack{
	public String nick;
	public String password;
	public enum Type {Register, Authorization };
	public Type type;
    
	/**
     * 
     * empty constructor, used only for FactoryMethod
     */ 
	public RegAuth(){};
	
	public RegAuth(String nick, String password, Type type) {
    	this.nick = nick;
    	this.password = password;
    	this.type = type;
    }
    
    public String getLogMessage(){
    	return " Send "+this.type+" ("+this.toString()+")";
    }
    
    @Override
    public String toString() {
        return "Nick: [" + this.nick + "] Password: [" + this.password + "] RequiestType: ["+this.type+"]";
    }
    
    /**
     * method for pack RegAuth into incoming Document
     * @param doc - incoming document
     * @return Document with RegAuth packed in
     */  
    public Document writeXml (Document doc){
		Element root = doc.createElement("RegAuth");
		doc.appendChild(root);
	        writeDataXml(doc,root,"nick",nick);
	        writeDataXml(doc,root,"password",password);
	        writeDataXml(doc,root,"type",type.toString());
        return doc;
	}
	
    /**
	 * 
     * method for unpack RegAuth from incoming Document
     * @param doc - incoming document
     * @return RegAuth unpacked from incoming document
     */
    public RegAuth readXml(Document doc){
    	return (new RegAuth(GetData(doc,"nick"),GetData(doc,"password"),Type.valueOf(GetData(doc,"type"))));	
    }
}
