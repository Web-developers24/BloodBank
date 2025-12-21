package com.bbms;

import com.bbms.config.HibernateUtil;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Objects;

public class MainApp extends Application {

    private static final Logger logger = LogManager.getLogger(MainApp.class);
    private static Stage primaryStage;

    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        logger.info("Starting Blood Bank Management System");
        
        try {
            // Initialize Hibernate
            HibernateUtil.getSessionFactory();
            logger.info("Database connection established");
            
            // Load login screen
            loadScene("/fxml/login.fxml", "Blood Bank - Login");
            
            primaryStage.setMinWidth(800);
            primaryStage.setMinHeight(600);
            primaryStage.show();
            
            logger.info("Application started successfully");
        } catch (Exception e) {
            logger.error("Failed to start application", e);
            System.exit(1);
        }
    }

    public static void loadScene(String fxmlPath, String title) {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(
                    MainApp.class.getResource(fxmlPath)));
            Scene scene = new Scene(root);
            
            // Load CSS
            String cssPath = MainApp.class.getResource("/css/styles.css") != null 
                    ? "/css/styles.css" : null;
            if (cssPath != null) {
                scene.getStylesheets().add(Objects.requireNonNull(
                        MainApp.class.getResource(cssPath)).toExternalForm());
            }
            
            primaryStage.setTitle(title);
            primaryStage.setScene(scene);
            primaryStage.centerOnScreen();
        } catch (IOException e) {
            logger.error("Failed to load scene: " + fxmlPath, e);
        }
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    @Override
    public void stop() {
        logger.info("Shutting down application");
        HibernateUtil.shutdown();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
