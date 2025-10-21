package org.example.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.MainApp;
import org.example.dao.UserDao;
import org.example.model.User;

public class LoginController {

    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML private Label lblStatus;

    private final UserDao userDao = new UserDao();

    @FXML
    private void handleLogin() {
        String username = txtUsername.getText().trim();
        String password = txtPassword.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            lblStatus.setText("⚠️ Please enter username and password");
            return;
        }

        User user = userDao.getUserByUsername(username);
        if (user != null && user.getPassword().equals(password)) {
            openMainMenu();
        } else {
            lblStatus.setText("❌ Invalid credentials");
        }
    }

    private void openMainMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/main_menu.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root, 600, 400);
            Stage stage = MainApp.getPrimaryStage();
            stage.setTitle("Blood Bank - Main Menu");
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

