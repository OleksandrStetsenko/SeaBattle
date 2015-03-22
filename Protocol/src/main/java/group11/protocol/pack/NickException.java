package group11.protocol.pack;

import java.io.Serializable;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author Lukyanenko Yulia, Tregub Vitaliy
 */
public class NickException  extends Pack {
    public String message;
    
    /**
     * 
     * empty constructor, used only for FactoryMethod
     */ 
    public NickException(){};
    
    public NickException(String message) {
        this.message = message;
    }
    public String getLogMessage(){
    	return " Send NickException '"+message+"'";
    }
    
    /**
     * method for pack NickException into incoming Document
     * @param doc - incoming document
     * @return Document with NickException packed in
     */  
    public Document writeXml(Document doc){
		Element root = doc.createElement("NickException"); 
    	doc.appendChild(root);
    	writeDataXml(doc, root,"Message",message);
    	return doc;
	}
    
    /**
	 * 
     * method for unpack NickException from incoming Document
     * @param doc - incoming document
     * @return NickException unpacked from incoming document
     */
    public NickException readXml(Document doc){
    	return (new NickException(GetData(doc,"Message"))); 
    }
}
