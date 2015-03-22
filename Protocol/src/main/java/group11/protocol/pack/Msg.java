/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package group11.protocol.pack;

import java.io.Serializable;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author Lukyanenko Yulia, Tregub Vitaliy
 */
public class Msg  extends Pack{
    public String message;
    
    /**
     * 
     * empty constructor, used only for FactoryMethod
     */ 
    public Msg() {}

    public Msg(String message) {
        this.message = message;
    }
    
    public String getLogMessage(){
    	return " Send Msg '"+message+"'";
    }
    
    /**
     * method for pack Message into incoming Document
     * @param doc - incoming document
     * @return Document with Message packed in
     */
    public Document writeXml(Document doc){
		Element root = doc.createElement("Msg"); 
    	doc.appendChild(root);
    	writeDataXml(doc, root,"Message",message);
    	return doc;
	}
 
    /**
	 * 
     * method for unpack Message from incoming Document
     * @param doc - incoming document
     * @return Message unpacked from incoming document
     */
    public Msg readXml(Document doc){
    	return (new Msg(GetData(doc,"Message"))); //з	
    }
 
}
