package group11.protocol.pack;

import group11.protocol.*;

import java.io.Serializable;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author Tregub Vitaliy
 */

public class PlayRequest extends Pack {
	public String nick;
	
    /**
     * 
     * empty constructor, used only for FactoryMethod
     */ 
	public PlayRequest(){};
	
	public PlayRequest(String nick){
		this.nick = nick;
	}
	
	public String getLogMessage(){
    	return " Send PlayRequest to - "+nick;
    }
	
	/**
     * method for pack PlayRequest into incoming Document
     * @param doc - incoming document
     * @return Document with PlayRequest packed in
     */  
	public Document writeXml(Document doc){
		Element root = doc.createElement("PlayRequest"); // корневой элемент
    	doc.appendChild(root);
    	writeDataXml(doc, root,"nick",nick); 
    	return doc;
	}
	
	/**
	 * 
     * method for unpack PlayRequest from incoming Document
     * @param doc - incoming document
     * @return PlayRequest unpacked from incoming document
     */
	 public PlayRequest readXml(Document doc){
		 return (new PlayRequest(GetData(doc,"nick"))); //з	
	    }
}
