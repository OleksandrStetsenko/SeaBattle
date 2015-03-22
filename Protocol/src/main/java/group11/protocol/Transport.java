package group11.protocol;

import java.io.IOException;
import java.io.Serializable;
import java.net.Socket;

/**
 *
 * @author Tregub Vitaliy
 */
public interface Transport {
        
    public Socket getSocket();
    public void close()  throws IOException;
    public Object receive() throws IOException;
    public void send(Serializable obj) throws IOException;
}
