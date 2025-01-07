import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.io.*;

// This class handles the chat client interface and user interactions.
public class ChatController {
    @FXML
    private TextArea messageArea;
    @FXML
    private TextField messageField;
    @FXML
    private ListView<String> participantList;
    @FXML
    private Button connectButton;
    @FXML
    private Button sendButton;
    @FXML
    private Button leaveButton;
    @FXML
    private TextField serverAddressField;
    @FXML
    private TextField userNameField;

    private ChatLogic chatLogic;

    // Constructor to initialize the controller with the chat logic.
    public ChatController(ChatLogic chatLogic) {
        this.chatLogic = chatLogic;
    }

    @FXML
    public void initialize() {
        sendButton.setDisable(true);
        connectButton.setOnAction(e -> connectToServer());
        sendButton.setOnAction(e -> sendMessage());
        leaveButton.setOnAction(e -> leaveChat());
    }

    // Connect to the server using the address and user name.
    private void connectToServer() {
        try {
            String serverAddress = serverAddressField.getText();
            String userName = userNameField.getText();
            if (userName.isEmpty()) {
                messageArea.appendText("Please enter a valid name.\n");
                return;
            }

            chatLogic.connectToServer(serverAddress, userName);
            receiveMessages();
            sendButton.setDisable(false);
        } catch (IOException e) {
            messageArea.appendText("Unable to connect to server.\n");
        }
    }

    // Send a message to the server.
    private void sendMessage() {
        String message = messageField.getText();
        chatLogic.sendMessage(message);
        messageField.clear();
    }

    // Receive messages from the server and update the message area.
    private void receiveMessages() {
        new Thread(() -> {
            try {
                String message;
                while ((message = chatLogic.receiveMessage()) != null) {
                    final String finalMessage = message;
                    Platform.runLater(() -> messageArea.appendText(finalMessage + "\n"));
                    if (message.startsWith("Participants:")) {
                        String participants = message.substring("Participants:".length()).trim();
                        updateParticipantsList(participants);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    // Update the list of participants in the chat.
    private void updateParticipantsList(String participants) {
        String[] participantsArray = participants.split(", ");
        Platform.runLater(() -> {
            participantList.getItems().clear();
            participantList.getItems().addAll(participantsArray);
        });
    }

    // Method to handle leaving the chat.
    private void leaveChat() {
        chatLogic.sendMessage("LEFT");
        messageArea.appendText("You have left the chat.\n");

        // Disable send button and leave button.
        sendButton.setDisable(true);
        leaveButton.setDisable(true);

        // Optionally, reset participant list.
        participantList.getItems().clear();
    }
}
