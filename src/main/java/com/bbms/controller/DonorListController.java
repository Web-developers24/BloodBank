package com.bbms.controller;

import com.bbms.MainApp;
import com.bbms.model.Donor;
import com.bbms.service.DonorService;
import com.bbms.util.AlertUtil;
import com.bbms.util.BloodCompatibility;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class DonorListController {

    private static final Logger logger = LogManager.getLogger(DonorListController.class);

    @FXML private TableView<Donor> donorTable;
    @FXML private TableColumn<Donor, Long> idCol;
    @FXML private TableColumn<Donor, String> nameCol;
    @FXML private TableColumn<Donor, String> bloodGroupCol;
    @FXML private TableColumn<Donor, String> phoneCol;
    @FXML private TableColumn<Donor, String> genderCol;
    @FXML private TableColumn<Donor, Integer> donationsCol;
    @FXML private TableColumn<Donor, Boolean> eligibleCol;

    @FXML private TextField searchField;
    @FXML private ComboBox<String> bloodGroupFilter;

    private final DonorService donorService = new DonorService();

    @FXML
    public void initialize() {
        // Initialize table columns
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        bloodGroupCol.setCellValueFactory(new PropertyValueFactory<>("bloodGroup"));
        phoneCol.setCellValueFactory(new PropertyValueFactory<>("phone"));
        genderCol.setCellValueFactory(new PropertyValueFactory<>("gender"));
        donationsCol.setCellValueFactory(new PropertyValueFactory<>("totalDonations"));
        eligibleCol.setCellValueFactory(new PropertyValueFactory<>("isEligible"));

        // Setup blood group filter
        bloodGroupFilter.getItems().add("All");
        bloodGroupFilter.getItems().addAll(BloodCompatibility.ALL_BLOOD_GROUPS);
        bloodGroupFilter.setValue("All");

        // Load data
        refreshTable();

        // Double-click to edit
        donorTable.setRowFactory(tv -> {
            TableRow<Donor> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    editDonor(row.getItem());
                }
            });
            return row;
        });
    }

    public void refreshTable() {
        List<Donor> donors = donorService.findAll();
        donorTable.setItems(FXCollections.observableArrayList(donors));
    }

    @FXML
    public void handleSearch(ActionEvent event) {
        String keyword = searchField.getText().trim();
        String bloodGroup = bloodGroupFilter.getValue();

        List<Donor> results;
        if (!keyword.isEmpty()) {
            results = donorService.search(keyword);
        } else if (!"All".equals(bloodGroup)) {
            results = donorService.findByBloodGroup(bloodGroup);
        } else {
            results = donorService.findAll();
        }

        donorTable.setItems(FXCollections.observableArrayList(results));
    }

    @FXML
    public void handleClearFilter(ActionEvent event) {
        searchField.clear();
        bloodGroupFilter.setValue("All");
        refreshTable();
    }

    @FXML
    public void handleAddDonor(ActionEvent event) {
        MainApp.loadScene("/fxml/donor_form.fxml", "Blood Bank - Add Donor");
    }

    @FXML
    public void handleEditDonor(ActionEvent event) {
        Donor selected = donorTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertUtil.showWarning("No Selection", "Please select a donor to edit");
            return;
        }
        editDonor(selected);
    }

    private void editDonor(Donor donor) {
        DonorFormController.setDonorToEdit(donor);
        MainApp.loadScene("/fxml/donor_form.fxml", "Blood Bank - Edit Donor");
    }

    @FXML
    public void handleDeleteDonor(ActionEvent event) {
        Donor selected = donorTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertUtil.showWarning("No Selection", "Please select a donor to delete");
            return;
        }

        if (AlertUtil.showConfirmation("Delete Donor", 
                "Are you sure you want to delete " + selected.getFullName() + "?")) {
            try {
                donorService.deleteDonor(selected.getId());
                refreshTable();
                AlertUtil.showSuccess("Donor deleted successfully");
            } catch (Exception e) {
                logger.error("Failed to delete donor", e);
                AlertUtil.showDatabaseError("Failed to delete donor");
            }
        }
    }

    @FXML
    public void handleCheckEligibility(ActionEvent event) {
        Donor selected = donorTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertUtil.showWarning("No Selection", "Please select a donor");
            return;
        }

        var eligibility = donorService.checkEligibility(selected);
        if (eligibility.isEligible()) {
            AlertUtil.showInfo("Eligible", selected.getFullName() + " is eligible to donate!");
        } else {
            AlertUtil.showWarning("Not Eligible", eligibility.getReason());
        }
    }

    @FXML
    public void handleBack(ActionEvent event) {
        MainApp.loadScene("/fxml/dashboard.fxml", "Blood Bank - Dashboard");
    }
}
