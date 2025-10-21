package org.example.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.MainApp;

public class MainMenuController {

    private Stage getStage() {
        // Get the current window (stage)
        return (Stage) org.example.MainApp.getPrimaryStage();
    }

    @FXML
    private void openDonorForm() { switchScene("/donor_form.fxml", "Donor Registration"); }

    @FXML
    private void openDonorList() { switchScene("/donor_list.fxml", "Donor List"); }

    @FXML
    private void openRecipientForm() { switchScene("/recipient_form.fxml", "Recipient Registration"); }

    @FXML
    private void openRecipientList() { switchScene("/recipient_list.fxml", "Recipient List"); }

    // ✅ New: Open Blood Stock View
    @FXML
    private void openBloodStock() { switchScene("/blood_stock_view.fxml", "Blood Stock"); }


    private void switchScene(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(MainApp.class.getResource(fxmlPath));
            Parent root = loader.load();
            Scene newScene = new Scene(root, 600, 400);

            Stage stage = MainApp.getPrimaryStage();
            stage.setTitle("Blood Bank - " + title);
            stage.setScene(newScene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
