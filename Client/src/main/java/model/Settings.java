package model;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.regex.Pattern;

public class Settings {

    public static final File FILE = new File("Settings.xml");
    static Logger log = Logger.getLogger(Settings.class.getName());

    // constants
    public static final int MILLIS_WAIT = 3000;
    public static final String HOST = "localhost";
    public static final int PORT = 9999;
    public static final int SO_TIMEOUT = 10000;

    // ERR MSG
    public static final String ERR_ConnectionRefused = "Connection refused";
    public static final String ERR_CannotReadSettings = "Cannot read the settings";
    public static final String ERR_UserRefused = "The user refused to accept the game";
    public static final String ERR_SaveSettingsErr = "Cannot save the settings";
    public static final String ERR_ConnectionErr = "Connection error";
    public static final String ERR_ConnectionReset = "Connection reset";

    // INFO MSG
    public static final String INF_LeavingGame = "You are leaving the game";
    public static final String INF_DialogTitle = "Registration / Authorization";
    public static final String INF_Looser = "You are looser :(";
    public static final String INF_Winner = "You are winner :)";

    // DBG MSG
    public static final String DBG_ReadFromXML = "Read settings from XML file...";
    public static final String DBG_WriteToXML = "Write settings to XML file...";
    public static final String DBG_CreatingEventLoop = "Creating event loop...";
    public static final String DBG_NULL_EVENT = "Dispatching event: event == null";
    public static final String DBG_StartEventLoop = "Start event loop...";
    public static final String DBG_LeavingGame = "Leaving game...";
    public static final String DBG_ExitProgram = "Exit program...";
    public static final String DBG_CreatingView = "Creating view...";
    public static final String DBG_ClosingView = "Closing view...";
    public static final String DBG_ShowView = "Show view...";
    public static final String DBG_UpdatingView = "Updating view...";
    public static final String DBG_Draw = "Draw components...";
    public static final String DBG_SetChanges = "Set changes to game model...";
    public static final String DBG_InvalidPass = "Invalid password. Try again.";
    public static final String DBG_InvalidUsername = "Invalid username. Try again.";

    // string patterns
    public static final Pattern USERNAME_PATTERN = Pattern.compile("^([a-zA-Z])+$");
    public static final Pattern PASS_PATTERN = Pattern.compile("^(\\w)+$");


    // Answers
    public static final String ANS_PlayWith = "Do you want to play with ";

    // user
    private static String username = "";
    private static String password = "";

    /**
     * Read settings from XML file
     */
    public static void readSettings() throws Exception {

        log.debug(DBG_ReadFromXML);

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = factory.newDocumentBuilder();
        Document document = documentBuilder.parse(FILE);
        NodeList nodeList = document.getElementsByTagName("settings");
        Element root = (Element) nodeList.item(0);
        setUsername(root.getElementsByTagName("username").item(0).getChildNodes().item(0).getNodeValue());
        setPassword(root.getElementsByTagName("password").item(0).getChildNodes().item(0).getNodeValue());

    }

    /**
     * Write settings to XML file
     */
    public static void writeSettings () throws Exception {

        log.debug(DBG_WriteToXML);

        try {

            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            // root elements
            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("settings");
            doc.appendChild(rootElement);

            // username
            Element usernameEl = doc.createElement("username");
            usernameEl.appendChild(doc.createTextNode(getUsername()));
            rootElement.appendChild(usernameEl);

            // pass
            Element passEl = doc.createElement("password");
            passEl.appendChild(doc.createTextNode(getPassword()));
            rootElement.appendChild(passEl);

            // write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(FILE);

            // Output to console for testing
            // StreamResult result = new StreamResult(System.out);

            transformer.transform(source, result);

        } catch (ParserConfigurationException pce) {
            log.error(pce);
            throw new Exception(pce);
        } catch (TransformerException tfe) {
            log.error(tfe);
            throw new Exception(tfe);
        }


    }

    //getters
    public static String getUsername() {
        return username;
    }

    public static String getPassword() {
        return password;
    }


    //setters
    public static void setUsername(String username) {
        Settings.username = username;
    }

    public static void setPassword(String password) {
        Settings.password = password;
    }
}
