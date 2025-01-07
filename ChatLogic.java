import java.io.*;
import java.net.*;

// This class handles the logic of communication with the chat server.
public class ChatLogic {
    private PrintWriter out;
    private BufferedReader in;
    private Socket socket;

    // Connect to the server with the provided address and user name.
    public void connectToServer(String serverAddress, String userName) throws IOException {
        socket = new Socket(serverAddress, 12345);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);

        // Send the user's name after connection.
        out.println(userName);
    }

    // Send a message to the server.
    public void sendMessage(String message) {
        out.println(message);
    }

    // Receive a message from the server.
    public String receiveMessage() throws IOException {
        return in.readLine();
    }

    // Handle disconnection by notifying the server and closing the connection.
    public void leaveChat() throws IOException {
        sendMessage("LEFT");
        closeConnection();
    }

    // Close the socket connection.
    public void closeConnection() throws IOException {
        socket.close();
    }
}
