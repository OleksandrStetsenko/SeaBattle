package group11.protocol;


import group11.protocol.pack.Msg;
import group11.protocol.pack.NickException;
import group11.protocol.pack.Pack;
import group11.protocol.pack.PlayRequest;
import group11.protocol.pack.RegAuth;
import group11.protocol.pack.RegAuth.Type;
import group11.protocol.pack.ShootMessage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.Socket;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * @author Tregub Vitaliy
 *
 */

public class TransportXml implements Transport{
    private Socket socket;
    private DataOutputStream dos;	
    private DataInputStream dis;	
    public static Document doc;
    private static DocumentBuilder builder;
    private static final Logger log = Logger.getLogger(group11.protocol.Transport.class);
    
	public TransportXml(Socket socket) throws IOException{
		this.socket = socket;
		log.debug("Use new socket "+ socket.getLocalPort()+"/"+socket.getPort());
		dos = new DataOutputStream(this.socket.getOutputStream());
		dis = new DataInputStream(this.socket.getInputStream());
		try { //создадим документ xml
            builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            doc = builder.newDocument();
        } catch (ParserConfigurationException e) {
        	log.error(" create new XML document - error! ", e);
        } 
    }
    /**
     * 
     * @return current socket
     */
	public Socket getSocket() {
        return socket;
    }
	
    /**
     * 
     * Close current connection
     */
    public void close() throws IOException{
        try {
            dos.close();
        }
        finally {
            try {
                dis.close();
            }
            finally {
                socket.close();
            }
        }
    }
    
    /**
     * 
     * Shutdown current connection and log failures
     */
    public void shutdown(){
        try {
            close();
        }
        catch (IOException e) {
        	log.error(" Closing socket - IO error! ", e);
        }
    }
     
    /**
     * 
     * @return clear Document
     */
	public  Document newDoc() { 
			try {
			doc = builder.parse(new InputSource(new StringReader("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><xml/>")));//перезаписываем документ
			doc.removeChild(doc.getFirstChild());
			}catch (IOException e) {
	            log.error(" Get XML document IO - error! (newDoc)", e);
	        } catch (SAXException e) {
	        	log.error(" Get XML document SAXException - error, retry! (newDoc)", e);
	        	return newDoc(); //если что-то пошло не так- попробуем еще раз (перестраховка)
	        }
        return doc; 
    }
		
    /**
     * Transform Document to String
     * @param Document - any document for transformation.
     * @return String - contents of document in human readable form (UTF string) or empty string if failed
     * 
     */
	public static String docToString(Document document) {
		try {
			Writer sw = new StringWriter();
	        TransformerFactory transformerFactory = TransformerFactory.newInstance();
	        Transformer transformer = transformerFactory.newTransformer();
	        transformer.setOutputProperty(OutputKeys.INDENT, "no");	// перевод строк в xml, АККУРАТНО!
	        transformer.transform(new DOMSource(document), new StreamResult(sw));
	        return sw.toString();
		} catch (TransformerFactoryConfigurationError e){
			log.error(" TransformerFactoryConfigurationError - error! ", e);
		} catch (TransformerException e){
			log.error(" TransformerException - error! ", e);
		}
		return "";
    }

    /**
     * Preparing and sending object over the network (Only "Pack" can be send) 
     * @param obj - object to send. 
     */
	
    public void send(Serializable obj) throws IOException {	//подготовка и передача обьекта
    	FactoryMethod fm= new FactoryMethod();
    	Pack pack= fm.getPack(obj);
    	if (!pack.equals(null)){ //проверяем, является ли обьект экземпляром Pack
    		newDoc();
    		send2net(pack.writeXml(doc),pack.getLogMessage()); //шлем в сеть
    	}
    	 else {
            log.error(" Trying to send unsupported type: - "+obj.getClass().toString());
        }
    }

    /**
     * direct sending object over the network (Only "Pack" can be send) 
     * @param Document - document to send. 
     * @param String - description string for logging.
     */
	private void send2net(Document doc, String eMessage) throws IOException{
		try{
			String mToSend =docToString(doc);
	        dos.writeUTF(mToSend);		 
	        log.info(socket.getLocalPort()+"=>"+socket.getPort()+") "+eMessage);
		}catch (IOException e){
			throw new IOException(e);/**Проброс ошибки ввода/выводы "выше" для корректной обработки*/
		}
	}
	
	
    /**
     * 
     * @return Document, with sending object inside, or null, if receive failed 
     */
	
	public Document getDoc()  throws IOException {
		try {
            doc = builder.parse(new InputSource(new StringReader(dis.readUTF())));
            return doc;    
        } catch (SAXException e) { 
        	log.error(" Get XML document SAXException - error! (getDoc part)", e);
        }        	
		return null;
    }

	/**
     * 
     * @return Object (only Pack object allowed), unpacked from document, received over the network, or null if unpack failed
     */
	 public Object receive() throws IOException{   
           while (getDoc().equals(null)){}// попробуем прочитать еще раз в случае неудачи- перестраховка
	           String root = doc.getDocumentElement().getNodeName();     
	           log.info(socket.getPort()+"=>"+socket.getLocalPort()+") Receive object! "+root);
	           
	       	FactoryMethod fm= new FactoryMethod();
	    	Pack pack= fm.getPack(doc);
	    	if (!pack.equals(null)){ //проверяем, является ли обьект экземпляром Pack
	    		return pack.readXml(doc);
	    	}
	    	 else {
	            log.error(" Trying to receive unsupported type: - "+root);
		        return null; 
	        }
	    }
	 
}
