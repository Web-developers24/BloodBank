package com.bbms.controllers;

import com.bbms.dao.RecipientDao;
import com.bbms.model.Recipient;
import com.bbms.model.Donor.Gender;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RecipientFormController {
    private static final Logger logger = LogManager.getLogger(RecipientFormController.class);
    
    @FXML private Label formTitle;
    @FXML private Label formSubtitle;
    @FXML private TextField nameField;
    @FXML private DatePicker dobPicker;
    @FXML private ComboBox<String> genderCombo;
    @FXML private ComboBox<String> bloodGroupCombo;
    @FXML private TextField phoneField;
    @FXML private TextField emailField;
    @FXML private TextArea addressArea;
    @FXML private TextField hospitalField;
    @FXML private TextField doctorField;
    @FXML private TextArea diagnosisArea;
    @FXML private TextField emergencyNameField;
    @FXML private TextField relationshipField;
    @FXML private TextField emergencyPhoneField;
    @FXML private Label errorLabel;
    
    private final RecipientDao recipientDao = new RecipientDao();
    private Recipient currentRecipient;
    private Runnable onSaveCallback;
    
    @FXML
    public void initialize() {
        genderCombo.setItems(FXCollections.observableArrayList("MALE", "FEMALE", "OTHER"));
        bloodGroupCombo.setItems(FXCollections.observableArrayList(
            "A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"
        ));
    }
    
    public void setRecipient(Recipient recipient) {
        this.currentRecipient = recipient;
        if (recipient != null) {
            formTitle.setText("Edit Recipient");
            formSubtitle.setText("Update recipient details");
            populateForm(recipient);
        } else {
            formTitle.setText("Add New Recipient");
            formSubtitle.setText("Enter recipient details");
        }
    }
    
    public void setOnSaveCallback(Runnable callback) {
        this.onSaveCallback = callback;
    }
    
    private void populateForm(Recipient recipient) {
        nameField.setText(recipient.getFullName());
        dobPicker.setValue(recipient.getDateOfBirth());
        genderCombo.setValue(recipient.getGender() != null ? recipient.getGender().name() : null);
        bloodGroupCombo.setValue(recipient.getBloodGroup());
        phoneField.setText(recipient.getPhone());
        emailField.setText(recipient.getEmail());
        addressArea.setText(recipient.getAddress());
        hospitalField.setText(recipient.getHospitalName());
        doctorField.setText(recipient.getDoctorName());
        diagnosisArea.setText(recipient.getMedicalCondition());
    }
    
    @FXML
    private void handleSave() {
        if (!validateForm()) return;
        
        try {
            Recipient recipient = currentRecipient != null ? currentRecipient : new Recipient();
            
            recipient.setFullName(nameField.getText().trim());
            recipient.setDateOfBirth(dobPicker.getValue());
            recipient.setGender(Gender.valueOf(genderCombo.getValue()));
            recipient.setBloodGroup(bloodGroupCombo.getValue());
            recipient.setPhone(phoneField.getText().trim());
            recipient.setEmail(emailField.getText().trim());
            recipient.setAddress(addressArea.getText().trim());
            recipient.setHospitalName(hospitalField.getText().trim());
            recipient.setDoctorName(doctorField.getText().trim());
            recipient.setMedicalCondition(diagnosisArea.getText().trim());
            
            if (currentRecipient != null) {
                recipientDao.update(recipient);
                logger.info("Updated recipient: {}", recipient.getFullName());
            } else {
                recipientDao.save(recipient);
                logger.info("Created new recipient: {}", recipient.getFullName());
            }
            
            if (onSaveCallback != null) onSaveCallback.run();
            closeForm();
        } catch (Exception e) {
            logger.error("Failed to save recipient", e);
            showError("Failed to save recipient: " + e.getMessage());
        }
    }
    
    private boolean validateForm() {
        StringBuilder errors = new StringBuilder();
        if (nameField.getText() == null || nameField.getText().trim().isEmpty()) 
            errors.append("Name is required.\n");
        if (dobPicker.getValue() == null) 
            errors.append("Date of birth is required.\n");
        if (genderCombo.getValue() == null) 
            errors.append("Gender is required.\n");
        if (bloodGroupCombo.getValue() == null) 
            errors.append("Blood group is required.\n");
        if (phoneField.getText() == null || phoneField.getText().trim().isEmpty()) 
            errors.append("Phone number is required.\n");
        
        if (errors.length() > 0) {
            showError(errors.toString());
            return false;
        }
        return true;
    }
    
    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
    }
    
    @FXML private void handleCancel() { closeForm(); }
    
    private void closeForm() {
        Stage stage = (Stage) nameField.getScene().getWindow();
        stage.close();
    }
}
