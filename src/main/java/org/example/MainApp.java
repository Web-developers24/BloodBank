package org.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    private static Stage primaryStage; // ✅ Global reference to the main stage

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage; // Save reference

        FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("/login.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 400);
        stage.setTitle("Blood Bank - Main Menu");
        stage.setScene(scene);
        stage.show();
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {
        launch();
    }
}
