package chatroom;

import java.awt.BorderLayout;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * @author $cotty $haw
 * 
 * The Client class has a text entry field that lets us type messages
 * and a text display window that shows messages from other users. We
 * also have a background thread for receiving messages. Our infinite
 * loop here has the same purpose as the one in our Server: read in a
 * message from another user, process it, and perhaps write something
 * back to the server as a response. Most importantly, the GUI visual
 * is created in this class.
 * 
 */
public class Client extends Panel implements Runnable {

    // String constants
    private static final String MY_TEXT_FIELD_MESSAGE = "Type...";
    private static final String MY_TEXT_AREA_MESSAGE = "Chat...";
    private static final String EMPTY_STRING = "";
    private static final String NEWLINE = "\n";
    private static final String ERROR_CONNECTING_MESSAGE = "Error connecting";
    private static final String ERROR_DISPLAYING_MESSAGE = "Error displaying chat message";

    /**
     * Generated serial version ID
     */
    private static final long serialVersionUID = -1649877892361198998L;

    // Components for the visual display of the chat windows
    private TextField myTextField = new TextField();
    private TextArea myTextArea = new TextArea();

    // Socket connected to the server
    private Socket mySocket;

    // Streams from the socket that communicate to the server
    private DataOutputStream myDataOut;
    private DataInputStream myDataIn;

    protected Client (String host, int port) {

        setLayout(new BorderLayout());
        add(MY_TEXT_FIELD_MESSAGE, myTextField);
        add(MY_TEXT_AREA_MESSAGE, myTextArea);

        // Receives messages when a user types a line and hits return
        myTextField.addActionListener(new ActionListener() {
            public void actionPerformed (ActionEvent e) {
                sendUserMessageToServer(e.getActionCommand());
            }
        });

        // Connects to the server
        try {
            mySocket = new Socket(host, port);

            myDataIn = new DataInputStream(mySocket.getInputStream());
            myDataOut = new DataOutputStream(mySocket.getOutputStream());

            // Starts background threads for receiving messages
            new Thread(this).start();
        }
        catch (IOException ex) {
            System.out.println(ERROR_CONNECTING_MESSAGE);
        }
    }

    // Sends user messages to the server
    private void sendUserMessageToServer (String message) {
        try {
            myDataOut.writeUTF(message);
            myTextField.setText(EMPTY_STRING);
        }
        catch (IOException ex) {
            System.out.println(ex);
        }
    }

    // Runs on background threads to display messages from other windows
    public void run () {
        try {
            while (true) {
                String message = myDataIn.readUTF();
                myTextArea.append(message + NEWLINE);
            }
        }
        catch (IOException ex) {
            System.out.println(ERROR_DISPLAYING_MESSAGE);
        }
    }
}
