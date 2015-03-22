package group11.server.model;

import group11.protocol.User;
import java.io.File;
import java.io.IOException;
import java.util.Set;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.log4j.Logger;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

/**
 *
 * @author Lukyanenko Yulia
 */
public class XMLFile {

    private static final Logger log = Logger.getLogger(group11.server.model.UsersModel.class);

    /**
     * Storage the list of all registred users in xml file.
     *
     * @param users The list of users.
     * @param file The file for storage list of users
     * @throws Exception If an exceptional condition that occured during the
     * transformation process Or Indicates a serious configuration error.
     */
    public static void writeToXML(Set<User> users, File file) throws Exception {

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder;
        try {
            documentBuilder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException ex) {
            throw new Exception(ex);
        }
        Document document = documentBuilder.newDocument();

        Element userListEl = document.createElement("listOfUsers");
        document.appendChild(userListEl);

        for (User user : users) {
            Element userEl = document.createElement("user");
            userListEl.appendChild(userEl);

            Element nickEl = document.createElement("nick");
            nickEl.appendChild(document.createTextNode(user.getNick()));
            userEl.appendChild(nickEl);

            Element passEl = document.createElement("password");
            passEl.appendChild(document.createTextNode(user.getPassword()));
            userEl.appendChild(passEl);

            Element rangEl = document.createElement("rang");
            rangEl.appendChild(document.createTextNode(Integer.toString(user.getRang())));
            userEl.appendChild(rangEl);
        }

        TransformerFactory factoryTr = TransformerFactory.newInstance();
        try {
            Transformer transformer = factoryTr.newTransformer();
            DOMSource domSources = new DOMSource(document);
            StreamResult streamResult = new StreamResult(file);
            transformer.transform(domSources, streamResult);
        } catch (TransformerException ex) {
            throw new Exception(ex);
        }
    }

    /**
     * Read from file the list of all registred users.
     * @param users - all registred users.
     * @param file - file where storage the list of users in xml
     * @throws ParserConfigurationException- Indicates a serious configuration
     * error.
     * @throws SAXException - Error or warning information from the XMLFile parser
     * @throws IOException 
     */
    public static void readFromXML(Set<User> users, File file) 
            throws ParserConfigurationException, SAXException, IOException {

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        DocumentBuilder documentBuilder;
        Document document;

        documentBuilder = factory.newDocumentBuilder();
        document = documentBuilder.parse(file);
        
        String nick = null;
        String pass = null;
        int rang = 0;
        NodeList nodeList = document.getElementsByTagName("user");
        for (int i = 0; i < nodeList.getLength(); i++) {
            Element el = (Element) nodeList.item(i);
            nick = el.getElementsByTagName("nick").item(0)
                    .getChildNodes().item(0).getNodeValue();
            pass = el.getElementsByTagName("password").item(0)
                    .getChildNodes()
                    .item(0)
                    .getNodeValue();
            rang = Integer.parseInt(el.getElementsByTagName("rang").item(0)
                    .getChildNodes().item(0).getNodeValue());
            User user = new User(nick, pass);
            user.setRang(rang);
            users.add(user);
        }
    }
}
