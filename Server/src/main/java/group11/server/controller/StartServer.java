package group11.server.controller;

import group11.server.model.Model;
import group11.server.model.UsersModel;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.apache.log4j.*;
/**
 *
 * @author  Lukyanenko Yulia
 */
public class StartServer {
    
    static volatile List<Room> listRoom = new LinkedList<Room>();
    private ServerSocket   serverSocket;   
    private Model userModel = new UsersModel();
    private static final Logger log = Logger.getLogger(group11.server.controller.StartServer.class);
    private static final Integer NUMBER_PORT = 9999;
    private ConnectionHandler connection;
    private Set<ConnectionHandler> connections = new HashSet<ConnectionHandler>();
    
    /**
     * Create server socket
     * @param port when used for server socket
     * 
     */
    public StartServer(Integer port)  {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Start listening...");
            while (true) {
                Socket socket = serverSocket.accept();
                connection = new ConnectionHandler(socket, userModel, connections);
            }
        } catch (IOException ex) {
            log.error("IOE , exit");
            close();
        }
    }
    
    public static void main(String[] args) throws IOException    {
        
        StartServer server = new StartServer(NUMBER_PORT);
    }
    
    private void close() {
        try{
            connection.closeAll();
            serverSocket.close();
        } 
        catch(IOException ex) {
            log.error("Error when closing the socket", ex);
        }
        finally {
            userModel.storageList();
        }
    }
}
