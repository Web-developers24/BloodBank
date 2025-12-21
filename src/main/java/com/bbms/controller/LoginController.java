package com.bbms.controller;

import com.bbms.MainApp;
import com.bbms.service.AuthService;
import com.bbms.util.AlertUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LoginController {

    private static final Logger logger = LogManager.getLogger(LoginController.class);

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;

    private final AuthService authService = AuthService.getInstance();

    @FXML
    public void initialize() {
        // Focus on username field
        usernameField.requestFocus();
    }

    @FXML
    public void handleLogin(ActionEvent event) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (username.isEmpty()) {
            AlertUtil.showValidationError("Please enter your username");
            usernameField.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            AlertUtil.showValidationError("Please enter your password");
            passwordField.requestFocus();
            return;
        }

        loginButton.setDisable(true);

        try {
            if (authService.login(username, password)) {
                logger.info("Login successful for user: {}", username);
                MainApp.loadScene("/fxml/dashboard.fxml", "Blood Bank - Dashboard");
            } else {
                AlertUtil.showError("Login Failed", "Invalid username or password");
                passwordField.clear();
                passwordField.requestFocus();
            }
        } catch (Exception e) {
            logger.error("Login error", e);
            AlertUtil.showDatabaseError("Failed to connect to database. Please check your configuration.");
        } finally {
            loginButton.setDisable(false);
        }
    }

    @FXML
    public void handleKeyPress(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            handleLogin(null);
        }
    }

    @FXML
    public void handleExit(ActionEvent event) {
        System.exit(0);
    }
}
