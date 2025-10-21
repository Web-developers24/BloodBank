package org.example.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.example.dao.BloodStockDao;
import org.example.dao.DonorDao;
import org.example.dao.DonationDao;       // 🆕 added
import org.example.model.BloodStock;
import org.example.model.Donor;
import org.example.model.Donation;
import javafx.stage.Stage;
// 🆕 added

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import org.example.MainApp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class DonorController {

    @FXML private TextField txtName;
    @FXML private TextField txtBloodGroup;
    @FXML private TextField txtPhone;
    @FXML private Label lblStatus;

    private final DonorDao donorDao = new DonorDao();
    private final BloodStockDao stockDao = new BloodStockDao();
    private final DonationDao donationDao = new DonationDao();   // 🆕 added

    @FXML
    private void saveDonor() {
        try {
            // --- Validate input ---
            String name = txtName.getText().trim();
            String group = txtBloodGroup.getText().trim().toUpperCase();
            String phone = txtPhone.getText().trim();

            if (name.isEmpty() || group.isEmpty() || phone.isEmpty()) {
                showError("⚠️ All fields are required!");
                return;
            }
            if (!isValidBloodGroup(group)) {
                showError("❌ Invalid blood group!");
                return;
            }

            // --- Save donor ---
            Donor donor = new Donor();
            donor.setFullName(name);
            donor.setBloodGroup(group);
            donor.setPhone(phone);
            donor.setLastDonationDate(LocalDate.now());
            donorDao.saveDonor(donor);

            // 🆕 --- Record Donation ---
            Donation donation = new Donation();
            donation.setDonor(donor);               // link donor entity
            donation.setBloodGroup(group);
            donation.setDonatedAt(java.time.LocalDateTime.now());
            donation.setUnits(1);                   // assume one unit donated
            donationDao.saveDonation(donation);

            // --- Update blood stock ---
            BloodStock stock = stockDao.getStockByGroup(group);
            if (stock == null) {
                stock = new BloodStock();
                stock.setBloodGroup(group);
                stock.setUnitsAvailable(1);
            } else {
                stock.setUnitsAvailable(stock.getUnitsAvailable() + 1);
            }
            stock.setLastUpdated(LocalDateTime.now());
            stockDao.saveOrUpdateStock(stock);

            // --- Feedback ---
            showSuccess("✅ Donor & donation saved, stock updated!");
            clearFields();

        } catch (Exception e) {
            showError("Error saving donor: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean isValidBloodGroup(String bg) {
        List<String> validGroups = List.of("A+", "A-", "B+", "B-", "O+", "O-", "AB+", "AB-");
        return validGroups.contains(bg);
    }

    private void clearFields() {
        txtName.clear();
        txtBloodGroup.clear();
        txtPhone.clear();
    }

    private void showError(String msg) {
        lblStatus.setText(msg);
        lblStatus.setStyle("-fx-text-fill: red;");
    }

    private void showSuccess(String msg) {
        lblStatus.setText(msg);
        lblStatus.setStyle("-fx-text-fill: green;");
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
