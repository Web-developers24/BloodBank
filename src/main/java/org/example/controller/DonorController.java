package org.example.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.example.dao.DonorDao;
import org.example.model.Donor;

public class DonorController {

    @FXML private TextField txtName;
    @FXML private TextField txtBloodGroup;
    @FXML private TextField txtPhone;
    @FXML private Label lblStatus;

    private final DonorDao donorDao = new DonorDao();

    @FXML
    private void saveDonor() {
        try {
            Donor donor = new Donor();
            donor.setFullName(txtName.getText());
            donor.setBloodGroup(txtBloodGroup.getText());
            donor.setPhone(txtPhone.getText());

            donorDao.saveDonor(donor);
            lblStatus.setText("Donor saved successfully!");
            clearFields();
        } catch (Exception e) {
            lblStatus.setText("Error saving donor: " + e.getMessage());
            lblStatus.setStyle("-fx-text-fill: red;");
        }
    }

    private void clearFields() {
        txtName.clear();
        txtBloodGroup.clear();
        txtPhone.clear();
    }
}
