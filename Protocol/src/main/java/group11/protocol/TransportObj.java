package group11.protocol;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
//import static java.lang.Thread.interrupted;
import java.net.Socket;
//import java.net.SocketTimeoutException;
import org.apache.log4j.*;

/**
 *
 * @author Lukyanenko Yulia
 * @deprecated  try to use TransportXml
 */
public class TransportObj implements Transport{
    
    private Socket socket;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private IOException ioError;
    private static final Logger log = Logger.getLogger(group11.protocol.Transport.class);
    
    public TransportObj(Socket socket) throws IOException
    {
        this.socket = socket;
        log.debug("Use new socket "+ socket.getPort()+"/"+socket.getLocalPort());
        output = new ObjectOutputStream(socket.getOutputStream());
        input = new ObjectInputStream(socket.getInputStream());
    }
    
    public Socket getSocket() {
        return socket;
    }
    
    public void close() throws IOException
    {
        try {
            output.close();
        }
        finally {
            try {
                input.close();
            }
            finally {
                socket.close();
            }
        }
    }
    
    public void shutdown()
    {
        try {
            close();
        }
        catch (IOException e) {
            handleError(e);
        }
    }
    
    public void send(Serializable obj)
    {
        try {
            output.writeObject(obj);
            output.flush();
            log.info(socket.toString()+"-socket-  Send object - "+obj.toString());
        }
        catch (IOException e) {
            log.error(socket.getPort()+"/"+socket.getLocalPort()+") Send object "+ obj.toString()+" - error! ", e);
            reportError(e);
        }
    }
    
    public Object receive() throws IOException
    {   
        try {
        	Object obj=input.readObject();
            log.info(socket.getPort()+"/"+socket.getLocalPort()+") Receive object! "+ obj.toString());
            return obj;
            
        } 
        catch (ClassNotFoundException e) {
            // should not happen
            throw new RuntimeException(e);
        }
     
    }

    protected void reportError(IOException e)
    {
        shutdown();
        handleError(e);
    }
    
    public void handleError(IOException e)
    {
        ioError = e;   
    }

    public void throwErrors() throws IOException
    {
        if (ioError != null) {
            IOException e = ioError;
            ioError = null;
            throw e;
        }
    }
}
