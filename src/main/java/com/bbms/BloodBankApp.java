package com.bbms;

import com.bbms.config.HibernateUtil;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BloodBankApp extends Application {
    private static final Logger logger = LogManager.getLogger(BloodBankApp.class);

    @Override
    public void start(Stage primaryStage) {
        try {
            logger.info("Starting Blood Bank Management System...");
            
            // Initialize Hibernate
            HibernateUtil.getSessionFactory();
            logger.info("Database connection established");
            
            // Load login screen
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Parent root = loader.load();
            
            Scene scene = new Scene(root, 400, 500);
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            
            primaryStage.setTitle("Blood Bank Management System - Login");
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.show();
            
            logger.info("Application started successfully");
            
        } catch (Exception e) {
            logger.error("Failed to start application", e);
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {
        logger.info("Shutting down application...");
        HibernateUtil.shutdown();
        logger.info("Application stopped");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
