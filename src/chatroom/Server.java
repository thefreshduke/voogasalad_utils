package chatroom;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 * @author $cotty $haw
 * 
 * The Server is our listener class. It uses a Socket object that has
 * two Streams: one for reading data in and one for writing data out.
 * The Server also needs enumeration and synchronization to avoid any
 * errors caused by other threads if they try to call sendToAll() and
 * removeConnection(). Also, we listen to ports to accept connections
 * and to return new Socket objects. This lets us get connections one
 * at a time if any are incoming.
 * 
 */
public class Server {

    // String constants
    private static final String ERROR_WRITING_OUTPUT_MESSAGE = "Error sending the message";
    private static final String ERROR_CLOSING_FILE_MESSAGE = "Error closing ";

    // ServerSocket for accepting new connections
    private ServerSocket mySocket;
    private Dictionary<Socket, DataOutputStream> myOutputStreams =
            new Hashtable<Socket, DataOutputStream>();

    // Constructor socket acceptor that listens to ports
    public Server (int port) throws IOException {
        listen(port);
    }

    // Listens to ports to accept connections
    private void listen (int port) throws IOException {
        mySocket = new ServerSocket(port);

        // Infinite loop for accepting connections
        while (true) {
            Socket s = mySocket.accept();

            // DataOutputStream for writing data to the others
            DataOutputStream dataOut = new DataOutputStream(s.getOutputStream());
            myOutputStreams.put(s, dataOut);

            new ServerThread(this, s);
        }
    }

    // Gets an enumeration of each OutputStream for all connected clients
    Enumeration<DataOutputStream> getOutputStreams () {
        return myOutputStreams.elements();
    }

    // Sends a message to all clients (utility routine)
    protected void sendToAll (String message) {

        // Synchronization to prevent errors if another thread calls removeConnection()
        synchronized (myOutputStreams) {

            // Gets the output stream and send the message for each client
            for (Enumeration<?> e = getOutputStreams(); e.hasMoreElements();) {

                DataOutputStream dataOut = (DataOutputStream)e.nextElement();

                try {
                    dataOut.writeUTF(message);
                }
                catch (IOException ex) {
                    System.out.println(ERROR_WRITING_OUTPUT_MESSAGE);
                }
            }
        }
    }

    // Remove a socket and its corresponding output stream if the client's connection is closed
    protected void removeConnection (Socket s) {

        // Synchronization to prevent errors if another thread calls sendToAll()
        synchronized (myOutputStreams) {
            myOutputStreams.remove(s);

            try {
                s.close();
            }
            catch (IOException ex) {
                System.out.println(ERROR_CLOSING_FILE_MESSAGE + s);
            }
        }
    }

    protected static void main (String[] args) throws Exception {
        int port = Integer.parseInt(args[0]);
        new Server(port);
    }
}
