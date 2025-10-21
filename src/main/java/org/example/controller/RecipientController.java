package org.example.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.example.dao.BloodStockDao;
import org.example.dao.RecipientDao;
import org.example.dao.TransfusionDao;        // 🆕 added
import org.example.model.BloodStock;
import org.example.model.Recipient;
import org.example.model.Transfusion;         // 🆕 added

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.MainApp;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;

public class RecipientController {

    @FXML private TextField txtName;
    @FXML private TextField txtBloodGroup;
    @FXML private TextField txtPhone;
    @FXML private Label lblStatus;

    private final RecipientDao recipientDao = new RecipientDao();
    private final BloodStockDao stockDao = new BloodStockDao();
    private final TransfusionDao transfusionDao = new TransfusionDao();   // 🆕 added

    @FXML
    private void saveRecipient() {
        try {
            // --- Step 1: Validate input ---
            String name = txtName.getText().trim();
            String group = txtBloodGroup.getText().trim().toUpperCase();
            String phone = txtPhone.getText().trim();

            if (name.isEmpty() || group.isEmpty() || phone.isEmpty()) {
                lblStatus.setText("⚠️ All fields are required!");
                lblStatus.setStyle("-fx-text-fill: red;");
                return;
            }

            if (!isValidBloodGroup(group)) {
                lblStatus.setText("❌ Invalid blood group!");
                lblStatus.setStyle("-fx-text-fill: red;");
                return;
            }

            // --- Step 2: Check stock availability ---
            BloodStock stock = stockDao.getStockByGroup(group);
            if (stock == null || stock.getUnitsAvailable() <= 0) {
                lblStatus.setText("❌ Requested blood not available!");
                lblStatus.setStyle("-fx-text-fill: red;");
                return;
            }

            // --- Step 3: Save recipient ---
            Recipient recipient = new Recipient();
            recipient.setFullName(name);
            recipient.setBloodGroupRequired(group);
            recipient.setPhone(phone);
            recipient.setRequestDate(LocalDate.now());
            recipientDao.saveRecipient(recipient);

            // 🆕 --- Step 4A: Record transfusion ---
            Transfusion transfusion = new Transfusion();
            transfusion.setRecipient(recipient);
            transfusion.setBloodGroupUsed(group);
            transfusion.setTransfusedAt(java.time.LocalDateTime.now());
            transfusion.setUnits(1); // assuming 1 unit per request
            transfusionDao.saveTransfusion(transfusion);

            // --- Step 4B: Update stock (-1) ---
            reduceStockAfterRequest(stock);

            // --- Step 5: Feedback ---
            lblStatus.setText("✅ Recipient request saved, transfusion recorded, stock updated!");
            lblStatus.setStyle("-fx-text-fill: green;");
            clearFields();

        } catch (Exception e) {
            lblStatus.setText("Error saving recipient: " + e.getMessage());
            lblStatus.setStyle("-fx-text-fill: red;");
            e.printStackTrace();
        }
    }

    // --- Helper: Reduce stock by 1 ---
    private void reduceStockAfterRequest(BloodStock stock) {
        stock.setUnitsAvailable(stock.getUnitsAvailable() - 1);
        stock.setLastUpdated(LocalDateTime.now());
        stockDao.saveOrUpdateStock(stock);
    }

    // --- Helper: Validate blood group ---
    private boolean isValidBloodGroup(String bg) {
        List<String> validGroups = List.of("A+", "A-", "B+", "B-", "O+", "O-", "AB+", "AB-");
        return validGroups.contains(bg);
    }

    private void clearFields() {
        txtName.clear();
        txtBloodGroup.clear();
        txtPhone.clear();
    }

    @FXML
    private void goBackToMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/main_menu.fxml"));
            javafx.scene.Parent root = loader.load();
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
