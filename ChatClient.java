import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.*;

// This class contains the main class for the chat client application.
public class ChatClient extends Application {
    private ChatLogic chatLogic;

    @Override
    public void start(Stage primaryStage) throws Exception {
        chatLogic = new ChatLogic();

        // Load the FXML file and set the controller with ChatLogic.
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Chat.fxml"));
        loader.setController(new ChatController(chatLogic));
        primaryStage.setScene(new Scene(loader.load()));
        primaryStage.setTitle("Chat Client");
        primaryStage.show();
    }

    // Connect to the server with given address and user name.
    public void connectToServer(String serverAddress, String userName) throws IOException {
        chatLogic.connectToServer(serverAddress, userName);
    }

    // Notify the server that the user is leaving the chat.
    public void leaveChat() {
        chatLogic.sendMessage("LEFT");
    }

    // Get the ChatLogic object.
    public ChatLogic getChatLogic() {
        return chatLogic;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
