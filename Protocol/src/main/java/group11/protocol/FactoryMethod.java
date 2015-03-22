/**
 * 
 */
package group11.protocol;

import group11.protocol.pack.FieldToSend;
import group11.protocol.pack.ListUsers;
import group11.protocol.pack.Msg;
import group11.protocol.pack.NickException;
import group11.protocol.pack.Pack;
import group11.protocol.pack.PlayRequest;
import group11.protocol.pack.RegAuth;
import group11.protocol.pack.ShootMessage;

import java.io.Serializable;

import org.w3c.dom.Document;

/**
 * @author Tregub Vitaliy
 *
 */
public class FactoryMethod {
	public Pack getPack(Serializable obj) {

		
		if (obj.getClass().getSimpleName().equals("RegAuth")) {
            return((RegAuth) obj);
        } else if (obj.getClass().getSimpleName().equals("ListUsers")) {
        	return((ListUsers) obj);
        } else if (obj.getClass().getSimpleName().equals("ShootMessage")) {
        	return((ShootMessage) obj);
        } else if (obj.getClass().getSimpleName().equals("NickException")) {
        	return((NickException) obj);
        } else if (obj.getClass().getSimpleName().equals("Msg")) {
        	return((Msg) obj);
        } else if (obj.getClass().getSimpleName().equals("PlayRequest")) {
        	return((PlayRequest) obj);
        } else if (obj.getClass().getSimpleName().equals("FieldToSend")) {
        	return((FieldToSend) obj);
        } else {
        	return null;
        	//log.error(" Trying to send unsupported type: - "+obj.getClass().toString());
        }
	}
	
	public Pack getPack(Document doc) {
		
		String root = doc.getDocumentElement().getNodeName();
		//Pack pack= null;
		
		if (root.equals("RegAuth")) {
			return new RegAuth().readXml(doc);
        } else if (root.equals("ListUsers")) {
        	return new ListUsers().readXml(doc);
        } else if (root.equals("ShootMessage")) {
        	return new ShootMessage().readXml(doc);
        } else if (root.equals("NickException")) {
        	return new NickException().readXml(doc);
        } else if (root.equals("Msg")) {
        	return new Msg().readXml(doc); //!!!!!!!
        } else if (root.equals("PlayRequest")) {
        	return new PlayRequest().readXml(doc);
        } else if (root.equals("FieldToSend")) {
        	return new FieldToSend().readXml(doc);
        } else {
        	return null;
        	//log.error(" Trying to send unsupported type: - "+obj.getClass().toString());
        }
	}
	

}
