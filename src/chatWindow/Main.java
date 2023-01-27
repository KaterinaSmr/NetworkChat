package chatWindow;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.InputStream;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("chatWindow.fxml"));
        Parent root = loader.load();
        Controller controller = loader.getController();

        InputStream iconStream =getClass().getResourceAsStream("icon.png");
        Image icon = new Image(iconStream);
        primaryStage.getIcons().add(icon);
        primaryStage.setTitle("ChatWindow");
        primaryStage.setScene(new Scene(root, 300, 350));
        primaryStage.show();

        controller.beforeStart();

        primaryStage.setOnCloseRequest(windowEvent -> {
            try {
                System.out.println("Stage is closing");
                controller.onExit();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                Platform.exit();
            }
        });


    }

    public static void main(String[] args) {
        launch(args);
    }
}
