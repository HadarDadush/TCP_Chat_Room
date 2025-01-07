import java.io.*;
import java.net.*;
import java.util.*;

// This class implements the chat server that handles multiple client connections.
public class ChatServer {
	private static final int PORT = 12345;
	private static Set<PrintWriter> clientWriters = new HashSet<>();
	private static Set<String> clientNames = new HashSet<>();

	// Get the set of client writers (output streams).
	public static Set<PrintWriter> getClientWriters() {
		return clientWriters;
	}

	// Get the set of client names.
	public static Set<String> getClientNames() {
		return clientNames;
	}

	public static void main(String[] args) throws IOException {
		System.out.println("Chat server started...");
		ServerSocket serverSocket = new ServerSocket(PORT);
		try {
			while (true) {
				new ClientHandler(serverSocket.accept()).start();
			}
		} finally {
			serverSocket.close();
		}
	}

	// ClientHandler handles the communication with each connected client.
	private static class ClientHandler extends Thread {
		private Socket socket;
		private PrintWriter out;
		private BufferedReader in;
		private String clientName;

		public ClientHandler(Socket socket) {
			this.socket = socket;
		}

		public void run() {
			try {
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				out = new PrintWriter(socket.getOutputStream(), true);
				clientName = in.readLine();

				if (clientName == null || clientName.isEmpty()) {
					clientName = "Anonymous";
				}

				// Check if the name is already taken.
				synchronized (ChatServer.getClientNames()) {
					if (ChatServer.getClientNames().contains(clientName)) {
						out.println("Name already taken, try another one.");
						return;
					}
					ChatServer.getClientNames().add(clientName);
				}

				// Add client to the set of writers.
				synchronized (ChatServer.getClientWriters()) {
					ChatServer.getClientWriters().add(out);
				}

				// Notify all clients about the current participants.
				synchronized (ChatServer.getClientWriters()) {
					for (PrintWriter writer : ChatServer.getClientWriters()) {
						writer.println("Participants: " + String.join(", ", ChatServer.getClientNames()));
					}
				}

				// Notify all clients that a new client has joined.
				synchronized (ChatServer.getClientWriters()) {
					for (PrintWriter writer : ChatServer.getClientWriters()) {
						writer.println(clientName + " has joined the chat.");
					}
				}

				String message;

				// Read messages from the client and broadcast to others.
				while ((message = in.readLine()) != null) {
					if (message.equalsIgnoreCase("exit") || message.equalsIgnoreCase("LEFT")) {
						break;
					}
					synchronized (ChatServer.getClientWriters()) {
						for (PrintWriter writer : ChatServer.getClientWriters()) {
							writer.println(clientName + ": " + message);
						}
					}
				}

				// Remove client from the set and notify others about leaving.
				synchronized (ChatServer.getClientNames()) {
					ChatServer.getClientNames().remove(clientName);
				}
				synchronized (ChatServer.getClientWriters()) {
					ChatServer.getClientWriters().remove(out);
				}

				// Notify all clients that the client has left.
				synchronized (ChatServer.getClientWriters()) {
					for (PrintWriter writer : ChatServer.getClientWriters()) {
						writer.println(clientName + " has left the chat.");
					}
				}

				// Update the participant list for all clients.
				synchronized (ChatServer.getClientWriters()) {
					for (PrintWriter writer : ChatServer.getClientWriters()) {
						writer.println("Participants: " + String.join(", ", ChatServer.getClientNames()));
					}
				}

			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
