import org.junit.*;
import group11.protocol.*;
import group11.protocol.pack.*;
import org.junit.rules.Timeout;
import org.w3c.dom.Document;

import javax.xml.transform.*;
import java.io.*;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestTransportXml {

    private TransportXml transport;
    private Socket socket;

    /**
     * Has bigger priority than @Test(timeout=1000)
     */
    @Rule
    public final Timeout timeout = new Timeout(5000);

    /**
     * Before every method
     */
    @Before
    public void init() throws IOException {

        PipedInputStream in = new PipedInputStream();
        PipedOutputStream out = new PipedOutputStream(in);

        socket = mock(Socket.class);

        when(socket.getOutputStream()).thenReturn(out);
        when(socket.getInputStream()).thenReturn(in);

        assertSame(out, socket.getOutputStream());
        assertSame(in, socket.getInputStream());

        transport = new TransportXml(socket);

    }

    /**
     * Tests creating new document
     */
    @Test
    public void test_newDoc() throws TransformerException {

        String expectedString = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>";

        Document doc = transport.newDoc();
        String actualString = TransportXml.docToString(doc);
        assertEquals(expectedString, actualString);

    }

    /**
     * Tests creating time new document (must be bigger than 1 sec)
     */
    @Test(timeout=1000)
    public void testTime_newDoc() throws TransformerException {

        transport.newDoc();

    }

    /**
     * Compare expected xml string with actual xml string of RegAuth object
     */
    @Test
    public void test_writeRegAuthXml() {

        RegAuth sampleRegAuth = new RegAuth("Nick", "pass", RegAuth.Type.Authorization);

        String expectedString = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>" +
                                "<RegAuth>" +
                                "<nick>Nick</nick>" +
                                "<password>pass</password>" +
                                "<type>Authorization</type>" +
                                "</RegAuth>";

        Document doc = sampleRegAuth.writeXml(transport.newDoc());
        
        String actualString = TransportXml.docToString(doc);

        assertEquals(expectedString, actualString);

    }

    /**
     * Compare expected xml string with actual xml string of ListUsers object
     */
    @Test
    public void test_writeListUsersXml() {

        Set<User> users = new HashSet<User>();
        users.add(new User("user1", "111"));
        users.add(new User("user2", "222"));
        users.add(new User("user3", "333"));

        ListUsers listUsers = new ListUsers(users);

        String expectedString = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>" +
                "<ListUsers>" +
                "<User>" +
                "<nick>user2</nick>" +
                "<password>222</password>" +
                "<rang>0</rang>" +
                "<state>disconnected</state>" +
                "<playWith> </playWith>" +
                "<roomID>0</roomID>" +
                "</User>" +
                "<User>" +
                "<nick>user1</nick>" +
                "<password>111</password>" +
                "<rang>0</rang>" +
                "<state>disconnected</state>" +
                "<playWith> </playWith>" +
                "<roomID>0</roomID>" +
                "</User>" +
                "<User>" +
                "<nick>user3</nick>" +
                "<password>333</password>" +
                "<rang>0</rang>" +
                "<state>disconnected</state>" +
                "<playWith> </playWith>" +
                "<roomID>0</roomID>" +
                "</User>" +
                "</ListUsers>";

        
        
        Document doc = listUsers.writeXml(transport.newDoc());
        String actualString = TransportXml.docToString(doc);

        assertEquals(expectedString, actualString);

    }

    /**
     * Tests that send() method throws IOException when OutputStream is closed
     */
    @Test(expected = IOException.class)
    public void testIOException_sendRegAuth() throws IOException {

        socket.getOutputStream().close();
        RegAuth sampleRegAuth = new RegAuth("Nick", "pass", RegAuth.Type.Authorization);
        transport.send(sampleRegAuth);

    }


    /**
     * How to ignore tests
     */
    @Ignore
    @Test(expected = IOException.class)
    public void testIOException_NPE() throws NullPointerException {
        throw new NullPointerException();
    }

    /**
     * Compare two ListUsers objects: before sending to net, and after receiving from net
     */
    @Test
    public void test_sendListUsers() throws IOException {

        Set<User> users = new HashSet<User>();
        users.add(new User("user1", "111"));
        users.add(new User("user2", "222"));
        users.add(new User("user3", "333"));

        ListUsers listUsers = new ListUsers(users);

        transport.send(listUsers);
        ListUsers receivedListUsers = (ListUsers) transport.receive();
        assertEquals(listUsers.users, receivedListUsers.users);

    }

    /**
     * Compare two RegAuth objects: before sending to net, and after receiving from net
     */
    @Test
    public void test_sendRegAuth() throws IOException {

        RegAuth sampleRegAuth = new RegAuth("Nick", "pass", RegAuth.Type.Authorization);

        transport.send(sampleRegAuth);
        RegAuth receivedRegAuth = (RegAuth) transport.receive();
        assertEquals(sampleRegAuth.nick, receivedRegAuth.nick);
        assertEquals(sampleRegAuth.password, receivedRegAuth.password);
        assertEquals(sampleRegAuth.type, receivedRegAuth.type);

    }

    /**
     * Closes all resources. After every test method
     */
    @After
    public void close() throws IOException {
        transport.close();
    }

}
