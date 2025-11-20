package UI;

import javafx.application.Application;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Pass the stage to the LoginScreen to display it
        LoginScreen loginScreen = new LoginScreen();
        loginScreen.show(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}