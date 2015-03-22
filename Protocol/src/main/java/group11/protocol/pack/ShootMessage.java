package group11.protocol.pack;

import group11.protocol.*;

import java.io.Serializable;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ShootMessage extends Pack {

    private String shooter;
    private int x;
    private int y;
    
    /**
     * 
     * empty constructor, used only for FactoryMethod
     */ 
    public ShootMessage(){};
    
    /**
     *
     * @param shooter nick of User, who make a shoot
     * @param x coordinate
     * @param y coordinate
     */
    public ShootMessage(String shooter, int x, int y) {
        this.shooter = shooter;
        this.x = x;
        this.y = y;

    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public String getShooter() {
        return shooter;
    }
    
    public String getLogMessage(){
    	return " Send ShootMessage - User "+getShooter()+ " shoot at "+getX()+"/"+getY();
    }
    
    /**
     * method for pack ShootMessage into incoming Document
     * @param doc - incoming document
     * @return Document with ShootMessage packed in
     */  
    public Document writeXml(Document doc){
		Element root = doc.createElement("ShootMessage"); // корневой элемент, тип передаваемого обьекта
    	doc.appendChild(root);
    	writeDataXml(doc, root,"Shooter",getShooter());// ник стреляющего
    	writeDataXml(doc, root,"X",Integer.toString(getX())); //координата X
    	writeDataXml(doc, root,"Y",Integer.toString(getY())); //координата Y
    	return doc;
	}
    
    /**
	 * 
     * method for unpack ShootMessage from incoming Document
     * @param doc - incoming document
     * @return ShootMessage unpacked from incoming document
     */
    public ShootMessage readXml(Document doc){
    	return (new ShootMessage(GetData(doc,"Shooter"),Integer.parseInt(GetData(doc,"X")),Integer.parseInt(GetData(doc,"Y"))));	
    }
    
}
