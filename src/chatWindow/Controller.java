package chatWindow;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Controller {
    @FXML
    private Button buttonSend;
    @FXML
    private Button buttonLogin;
    @FXML
    private TextField textField;
    @FXML
    private TextField tfLogin;
    @FXML
    private PasswordField passwordField;
    @FXML
    private TextArea textArea;
    @FXML
    private TextArea userListTextArea;
    @FXML
    private GridPane topPane;
    @FXML
    private BorderPane bottomPane;
    @FXML
    private ScrollPane rightPane;

    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private boolean isAuthorized;

    public Controller() {
        onStart();
    }

    @FXML
    private void onClickButtonLogin(){
        String login = tfLogin.getText();
        String password = passwordField.getText();
        String msg = "/auth " + login + " " + password;
        sendMessage(msg);
    }

    @FXML
    private void onClickButtonSend(){
        System.out.println("Send is pressed");
        sendMessage(textField.getText());
        textField.setText("");
    }

    private void sendMessage(String msg){
        try {
            out.writeUTF(msg);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void beforeStart(){
        setAuthorized(false);
        bottomPane.managedProperty().bind(bottomPane.visibleProperty());
        topPane.managedProperty().bind(topPane.visibleProperty());
        buttonLogin.defaultButtonProperty().bind(topPane.visibleProperty());
        buttonSend.defaultButtonProperty().bind(bottomPane.visibleProperty());
    }

    public void onStart(){
        final int SERVER_PORT = 8189;
        final String SERVER_ADDR = "localhost";
        try {
            socket = new Socket(SERVER_ADDR,SERVER_PORT);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        new Thread(() -> {
            try {
                while (true){
                    String msg = in.readUTF();
                    if (msg.equals("/authok")) {
                        textField.requestFocus();
                        setAuthorized(true);
                        break;
                    } else if (msg.startsWith("/end")) {
                        onExit();
                    } else {
                        textArea.appendText(msg+"\n");
                    }
                }
                while (true){
                    String msg = in.readUTF();
                    System.out.println(msg);
                    if (msg.startsWith("/")){
                        if (msg.startsWith("/userlist")){
                            System.out.println(msg);
                            userListTextArea.setText("");
                            String users[] = msg.split(" ");
                            for (int i = 1; i < users.length; i++) {
                                userListTextArea.appendText(users[i] + "\n");
                            }
                        }
                    } else {
                        textArea.appendText(msg + "\n");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                onExit();
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void setAuthorized(boolean authorized) {
        isAuthorized = authorized;
        topPane.setVisible(!isAuthorized);
        bottomPane.setVisible(isAuthorized);
        textArea.setText("");
        textField.setText("");
        userListTextArea.setText("");
    }

    public void onExit(){
        sendMessage("/end");
        setAuthorized(false);
    }
}
