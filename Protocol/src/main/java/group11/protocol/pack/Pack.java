/**
 * 
 */
package group11.protocol.pack;

import java.io.Serializable;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author Tregub Vitaliy
 *
 */
public abstract class Pack implements Serializable{
	public abstract String getLogMessage();
	public abstract Document writeXml(Document doc);
	public abstract Pack readXml(Document doc);

	/**
     * method for pack any text data into incoming Document
     * @param doc - incoming document
     * @param root - root element of incoming document to write data
     * @param teg - tag name for xml
     * @param data - text to write
     * @return element of Document with data packed in
     */  
	public Element writeDataXml(Document doc, Element root, String teg, String data){
		Element E1 = doc.createElement(teg);
        E1.appendChild(doc.createTextNode(data));
        root.appendChild(E1);
        return E1;
	}
	
	/**
	 * 
     * method for unpack any text data from incoming Document
     * (can be used for unique tag)
     * @param doc - incoming document
     * @param teg - tag name from xml
     * @return text data unpacked from incoming document
     */
	public String GetData(Document doc, String teg){ 		
		return (doc.getElementsByTagName(teg).item(0).getFirstChild().getNodeValue());
	}
	
	/**
	 * 
     * method for unpack any text data from incoming Document
     * (can be used for unique tag)
     * @param doc - incoming document
     * @param root - root element of incoming document
     * @param teg - tag name from xml
     * @return text data unpacked from incoming document
     */
	public String GetData(Document doc, Element root, String teg){ 		
		return (root.getFirstChild().getNodeValue());
	}
		
}
