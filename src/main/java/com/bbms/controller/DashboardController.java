package com.bbms.controller;

import com.bbms.MainApp;
import com.bbms.model.BloodRequest;
import com.bbms.service.*;
import com.bbms.util.AlertUtil;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

public class DashboardController {

    private static final Logger logger = LogManager.getLogger(DashboardController.class);

    @FXML private Label welcomeLabel;
    @FXML private Label totalDonorsLabel;
    @FXML private Label totalRecipientsLabel;
    @FXML private Label totalDonationsLabel;
    @FXML private Label pendingRequestsLabel;

    @FXML private Label stockOPos;
    @FXML private Label stockONeg;
    @FXML private Label stockAPos;
    @FXML private Label stockANeg;
    @FXML private Label stockBPos;
    @FXML private Label stockBNeg;
    @FXML private Label stockABPos;
    @FXML private Label stockABNeg;

    @FXML private TableView<BloodRequest> pendingRequestsTable;
    @FXML private TableColumn<BloodRequest, Long> requestIdCol;
    @FXML private TableColumn<BloodRequest, String> requestBloodGroupCol;
    @FXML private TableColumn<BloodRequest, Integer> requestUnitsCol;
    @FXML private TableColumn<BloodRequest, String> requestPriorityCol;

    @FXML private ListView<String> alertsList;

    private final AuthService authService = AuthService.getInstance();
    private final DonorService donorService = new DonorService();
    private final RecipientService recipientService = new RecipientService();
    private final BloodStockService bloodStockService = new BloodStockService();

    @FXML
    public void initialize() {
        // Set welcome message
        if (authService.getCurrentUser() != null) {
            welcomeLabel.setText("Welcome, " + authService.getCurrentUser().getFullName());
        }

        // Initialize table columns if present
        if (requestIdCol != null) {
            requestIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
            requestBloodGroupCol.setCellValueFactory(new PropertyValueFactory<>("bloodGroup"));
            requestUnitsCol.setCellValueFactory(new PropertyValueFactory<>("unitsRequested"));
            requestPriorityCol.setCellValueFactory(new PropertyValueFactory<>("priority"));
        }

        // Load dashboard data
        refreshDashboard();
    }

    public void refreshDashboard() {
        try {
            // Load statistics
            if (totalDonorsLabel != null) {
                totalDonorsLabel.setText(String.valueOf(donorService.getTotalDonorCount()));
            }
            if (totalRecipientsLabel != null) {
                totalRecipientsLabel.setText(String.valueOf(recipientService.getTotalRecipientCount()));
            }
            if (pendingRequestsLabel != null) {
                pendingRequestsLabel.setText(String.valueOf(recipientService.getPendingRequests().size()));
            }

            // Load blood stock summary
            loadStockSummary();

            // Load pending requests
            if (pendingRequestsTable != null) {
                var requests = recipientService.getPendingRequests();
                pendingRequestsTable.setItems(FXCollections.observableArrayList(requests));
            }

            // Load alerts
            loadAlerts();

        } catch (Exception e) {
            logger.error("Failed to refresh dashboard", e);
        }
    }

    private void loadStockSummary() {
        Map<String, Integer> summary = bloodStockService.getStockSummary();
        
        if (stockOPos != null) stockOPos.setText(String.valueOf(summary.getOrDefault("O+", 0)));
        if (stockONeg != null) stockONeg.setText(String.valueOf(summary.getOrDefault("O-", 0)));
        if (stockAPos != null) stockAPos.setText(String.valueOf(summary.getOrDefault("A+", 0)));
        if (stockANeg != null) stockANeg.setText(String.valueOf(summary.getOrDefault("A-", 0)));
        if (stockBPos != null) stockBPos.setText(String.valueOf(summary.getOrDefault("B+", 0)));
        if (stockBNeg != null) stockBNeg.setText(String.valueOf(summary.getOrDefault("B-", 0)));
        if (stockABPos != null) stockABPos.setText(String.valueOf(summary.getOrDefault("AB+", 0)));
        if (stockABNeg != null) stockABNeg.setText(String.valueOf(summary.getOrDefault("AB-", 0)));
    }

    private void loadAlerts() {
        if (alertsList == null) return;
        
        alertsList.getItems().clear();
        var alerts = bloodStockService.getAlerts();
        for (var alert : alerts) {
            alertsList.getItems().add(alert.message());
        }
        
        if (alertsList.getItems().isEmpty()) {
            alertsList.getItems().add("No alerts at this time");
        }
    }

    @FXML
    public void navigateToDonors(ActionEvent event) {
        MainApp.loadScene("/fxml/donor_list.fxml", "Blood Bank - Donors");
    }

    @FXML
    public void navigateToRecipients(ActionEvent event) {
        MainApp.loadScene("/fxml/recipient_list.fxml", "Blood Bank - Recipients");
    }

    @FXML
    public void navigateToBloodStock(ActionEvent event) {
        MainApp.loadScene("/fxml/blood_stock.fxml", "Blood Bank - Blood Stock");
    }

    @FXML
    public void navigateToDonations(ActionEvent event) {
        MainApp.loadScene("/fxml/donations.fxml", "Blood Bank - Donations");
    }

    @FXML
    public void navigateToRequests(ActionEvent event) {
        MainApp.loadScene("/fxml/blood_requests.fxml", "Blood Bank - Blood Requests");
    }

    @FXML
    public void navigateToReports(ActionEvent event) {
        MainApp.loadScene("/fxml/reports.fxml", "Blood Bank - Reports");
    }

    @FXML
    public void handleLogout(ActionEvent event) {
        if (AlertUtil.showConfirmation("Logout", "Are you sure you want to logout?")) {
            authService.logout();
            MainApp.loadScene("/fxml/login.fxml", "Blood Bank - Login");
        }
    }

    @FXML
    public void handleRefresh(ActionEvent event) {
        refreshDashboard();
    }
}
