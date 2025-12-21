package com.bbms.controller;

import com.bbms.MainApp;
import com.bbms.model.Donor;
import com.bbms.service.DonorService;
import com.bbms.util.AlertUtil;
import com.bbms.util.BloodCompatibility;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDate;

public class DonorFormController {

    private static final Logger logger = LogManager.getLogger(DonorFormController.class);
    private static Donor donorToEdit = null;

    @FXML private Label formTitle;
    @FXML private TextField nameField;
    @FXML private ComboBox<String> bloodGroupCombo;
    @FXML private TextField phoneField;
    @FXML private TextField emailField;
    @FXML private TextArea addressArea;
    @FXML private DatePicker dobPicker;
    @FXML private ComboBox<String> genderCombo;
    @FXML private TextField weightField;
    @FXML private TextArea medicalNotesArea;
    @FXML private CheckBox eligibleCheckbox;

    private final DonorService donorService = new DonorService();
    private Donor currentDonor;

    public static void setDonorToEdit(Donor donor) {
        donorToEdit = donor;
    }

    @FXML
    public void initialize() {
        // Setup blood group combo
        bloodGroupCombo.getItems().addAll(BloodCompatibility.ALL_BLOOD_GROUPS);

        // Setup gender combo
        genderCombo.getItems().addAll("MALE", "FEMALE", "OTHER");

        // Restrict date picker to past dates
        dobPicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(date.isAfter(LocalDate.now().minusYears(18)));
            }
        });

        // Load donor if editing
        if (donorToEdit != null) {
            currentDonor = donorToEdit;
            donorToEdit = null;
            loadDonorData();
            formTitle.setText("Edit Donor");
        } else {
            currentDonor = new Donor();
            eligibleCheckbox.setSelected(true);
            formTitle.setText("Add New Donor");
        }
    }

    private void loadDonorData() {
        nameField.setText(currentDonor.getFullName());
        bloodGroupCombo.setValue(currentDonor.getBloodGroup());
        phoneField.setText(currentDonor.getPhone());
        emailField.setText(currentDonor.getEmail());
        addressArea.setText(currentDonor.getAddress());
        dobPicker.setValue(currentDonor.getDateOfBirth());
        if (currentDonor.getGender() != null) {
            genderCombo.setValue(currentDonor.getGender().name());
        }
        if (currentDonor.getWeightKg() != null) {
            weightField.setText(String.valueOf(currentDonor.getWeightKg()));
        }
        medicalNotesArea.setText(currentDonor.getMedicalNotes());
        eligibleCheckbox.setSelected(currentDonor.getIsEligible());
    }

    @FXML
    public void handleSave(ActionEvent event) {
        if (!validateForm()) {
            return;
        }

        try {
            // Update donor object
            currentDonor.setFullName(nameField.getText().trim());
            currentDonor.setBloodGroup(bloodGroupCombo.getValue());
            currentDonor.setPhone(phoneField.getText().trim());
            currentDonor.setEmail(emailField.getText().trim());
            currentDonor.setAddress(addressArea.getText());
            currentDonor.setDateOfBirth(dobPicker.getValue());
            
            if (genderCombo.getValue() != null) {
                currentDonor.setGender(Donor.Gender.valueOf(genderCombo.getValue()));
            }
            
            if (!weightField.getText().isEmpty()) {
                currentDonor.setWeightKg(Double.parseDouble(weightField.getText().trim()));
            }
            
            currentDonor.setMedicalNotes(medicalNotesArea.getText());
            currentDonor.setIsEligible(eligibleCheckbox.isSelected());

            // Save
            donorService.saveDonor(currentDonor);
            
            AlertUtil.showSuccess("Donor saved successfully");
            handleCancel(null);
            
        } catch (IllegalArgumentException e) {
            AlertUtil.showValidationError(e.getMessage());
        } catch (Exception e) {
            logger.error("Failed to save donor", e);
            AlertUtil.showDatabaseError("Failed to save donor: " + e.getMessage());
        }
    }

    private boolean validateForm() {
        if (nameField.getText().trim().isEmpty()) {
            AlertUtil.showValidationError("Name is required");
            nameField.requestFocus();
            return false;
        }

        if (bloodGroupCombo.getValue() == null) {
            AlertUtil.showValidationError("Blood group is required");
            bloodGroupCombo.requestFocus();
            return false;
        }

        if (!weightField.getText().isEmpty()) {
            try {
                double weight = Double.parseDouble(weightField.getText().trim());
                if (weight < 30 || weight > 200) {
                    AlertUtil.showValidationError("Weight must be between 30 and 200 kg");
                    weightField.requestFocus();
                    return false;
                }
            } catch (NumberFormatException e) {
                AlertUtil.showValidationError("Invalid weight value");
                weightField.requestFocus();
                return false;
            }
        }

        if (dobPicker.getValue() != null && dobPicker.getValue().isAfter(LocalDate.now().minusYears(18))) {
            AlertUtil.showValidationError("Donor must be at least 18 years old");
            dobPicker.requestFocus();
            return false;
        }

        return true;
    }

    @FXML
    public void handleCancel(ActionEvent event) {
        MainApp.loadScene("/fxml/donor_list.fxml", "Blood Bank - Donors");
    }

    @FXML
    public void handleClear(ActionEvent event) {
        nameField.clear();
        bloodGroupCombo.setValue(null);
        phoneField.clear();
        emailField.clear();
        addressArea.clear();
        dobPicker.setValue(null);
        genderCombo.setValue(null);
        weightField.clear();
        medicalNotesArea.clear();
        eligibleCheckbox.setSelected(true);
    }
}
