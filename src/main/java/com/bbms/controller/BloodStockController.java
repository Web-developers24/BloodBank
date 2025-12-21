package com.bbms.controller;

import com.bbms.MainApp;
import com.bbms.model.BloodStock;
import com.bbms.service.BloodStockService;
import com.bbms.util.AlertUtil;
import com.bbms.util.BloodCompatibility;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class BloodStockController {

    private static final Logger logger = LogManager.getLogger(BloodStockController.class);

    @FXML private TableView<BloodStock> stockTable;
    @FXML private TableColumn<BloodStock, Long> idCol;
    @FXML private TableColumn<BloodStock, String> bloodGroupCol;
    @FXML private TableColumn<BloodStock, String> componentCol;
    @FXML private TableColumn<BloodStock, Integer> unitsCol;
    @FXML private TableColumn<BloodStock, LocalDate> expiryCol;
    @FXML private TableColumn<BloodStock, String> locationCol;
    @FXML private TableColumn<BloodStock, String> statusCol;

    @FXML private ComboBox<String> bloodGroupFilter;
    @FXML private ComboBox<String> componentFilter;

    // Summary labels
    @FXML private Label stockOPos;
    @FXML private Label stockONeg;
    @FXML private Label stockAPos;
    @FXML private Label stockANeg;
    @FXML private Label stockBPos;
    @FXML private Label stockBNeg;
    @FXML private Label stockABPos;
    @FXML private Label stockABNeg;

    @FXML private ListView<String> alertsList;

    private final BloodStockService stockService = new BloodStockService();

    @FXML
    public void initialize() {
        // Initialize table columns
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        bloodGroupCol.setCellValueFactory(new PropertyValueFactory<>("bloodGroup"));
        componentCol.setCellValueFactory(new PropertyValueFactory<>("componentType"));
        unitsCol.setCellValueFactory(new PropertyValueFactory<>("unitsAvailable"));
        expiryCol.setCellValueFactory(new PropertyValueFactory<>("expiryDate"));
        locationCol.setCellValueFactory(new PropertyValueFactory<>("storageLocation"));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Row styling for expiring/low stock
        stockTable.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(BloodStock item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setStyle("");
                } else if (item.isExpired()) {
                    setStyle("-fx-background-color: #ffcccc;");
                } else if (item.isExpiringSoon()) {
                    setStyle("-fx-background-color: #ffffcc;");
                } else if (item.isLowStock()) {
                    setStyle("-fx-background-color: #ffeecc;");
                } else {
                    setStyle("");
                }
            }
        });

        // Setup filters
        bloodGroupFilter.getItems().add("All");
        bloodGroupFilter.getItems().addAll(BloodCompatibility.ALL_BLOOD_GROUPS);
        bloodGroupFilter.setValue("All");

        componentFilter.getItems().add("All");
        for (BloodStock.ComponentType type : BloodStock.ComponentType.values()) {
            componentFilter.getItems().add(type.name());
        }
        componentFilter.setValue("All");

        // Load data
        refreshData();
    }

    public void refreshData() {
        refreshTable();
        loadSummary();
        loadAlerts();
    }

    private void refreshTable() {
        List<BloodStock> stocks = stockService.findAll();
        stockTable.setItems(FXCollections.observableArrayList(stocks));
    }

    private void loadSummary() {
        Map<String, Integer> summary = stockService.getStockSummary();
        
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
        var alerts = stockService.getAlerts();
        for (var alert : alerts) {
            alertsList.getItems().add(alert.message());
        }
        
        if (alertsList.getItems().isEmpty()) {
            alertsList.getItems().add("No stock alerts");
        }
    }

    @FXML
    public void handleFilter(ActionEvent event) {
        String bloodGroup = bloodGroupFilter.getValue();
        String component = componentFilter.getValue();

        List<BloodStock> filtered = stockService.findAll().stream()
                .filter(s -> "All".equals(bloodGroup) || s.getBloodGroup().equals(bloodGroup))
                .filter(s -> "All".equals(component) || s.getComponentType().name().equals(component))
                .toList();

        stockTable.setItems(FXCollections.observableArrayList(filtered));
    }

    @FXML
    public void handleClearFilter(ActionEvent event) {
        bloodGroupFilter.setValue("All");
        componentFilter.setValue("All");
        refreshTable();
    }

    @FXML
    public void handleAddStock(ActionEvent event) {
        // Show add stock dialog
        Dialog<BloodStock> dialog = createAddStockDialog();
        dialog.showAndWait().ifPresent(stock -> {
            try {
                stockService.saveStock(stock);
                refreshData();
                AlertUtil.showSuccess("Blood stock added successfully");
            } catch (Exception e) {
                logger.error("Failed to add stock", e);
                AlertUtil.showDatabaseError("Failed to add stock");
            }
        });
    }

    private Dialog<BloodStock> createAddStockDialog() {
        Dialog<BloodStock> dialog = new Dialog<>();
        dialog.setTitle("Add Blood Stock");
        dialog.setHeaderText("Enter blood stock details");

        // Set buttons
        ButtonType saveButton = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButton, ButtonType.CANCEL);

        // Create form
        javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

        ComboBox<String> bloodGroup = new ComboBox<>();
        bloodGroup.getItems().addAll(BloodCompatibility.ALL_BLOOD_GROUPS);

        ComboBox<BloodStock.ComponentType> component = new ComboBox<>();
        component.getItems().addAll(BloodStock.ComponentType.values());
        component.setValue(BloodStock.ComponentType.WHOLE_BLOOD);

        TextField units = new TextField();
        units.setPromptText("Units");

        TextField location = new TextField();
        location.setPromptText("Storage Location");

        grid.add(new Label("Blood Group:"), 0, 0);
        grid.add(bloodGroup, 1, 0);
        grid.add(new Label("Component:"), 0, 1);
        grid.add(component, 1, 1);
        grid.add(new Label("Units:"), 0, 2);
        grid.add(units, 1, 2);
        grid.add(new Label("Location:"), 0, 3);
        grid.add(location, 1, 3);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButton) {
                BloodStock stock = new BloodStock();
                stock.setBloodGroup(bloodGroup.getValue());
                stock.setComponentType(component.getValue());
                stock.setUnitsAvailable(Integer.parseInt(units.getText()));
                stock.setStorageLocation(location.getText());
                stock.setCollectionDate(LocalDate.now());
                stock.setStatus(BloodStock.StockStatus.AVAILABLE);
                
                // Set expiry based on component
                int expiryDays = switch (component.getValue()) {
                    case WHOLE_BLOOD, RBC -> 42;
                    case PLASMA -> 365;
                    case PLATELETS -> 5;
                    case WBC -> 1;
                };
                stock.setExpiryDate(LocalDate.now().plusDays(expiryDays));
                
                return stock;
            }
            return null;
        });

        return dialog;
    }

    @FXML
    public void handleDeleteStock(ActionEvent event) {
        BloodStock selected = stockTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertUtil.showWarning("No Selection", "Please select a stock entry to delete");
            return;
        }

        if (AlertUtil.showConfirmation("Delete Stock", 
                "Are you sure you want to delete this stock entry?")) {
            try {
                stockService.deleteStock(selected.getId());
                refreshData();
                AlertUtil.showSuccess("Stock deleted successfully");
            } catch (Exception e) {
                logger.error("Failed to delete stock", e);
                AlertUtil.showDatabaseError("Failed to delete stock");
            }
        }
    }

    @FXML
    public void handleMarkExpired(ActionEvent event) {
        int count = stockService.markExpiredStocks();
        if (count > 0) {
            refreshData();
            AlertUtil.showInfo("Expired Stocks", count + " stock(s) marked as expired");
        } else {
            AlertUtil.showInfo("No Expired Stocks", "No expired stocks found");
        }
    }

    @FXML
    public void handleRefresh(ActionEvent event) {
        refreshData();
    }

    @FXML
    public void handleBack(ActionEvent event) {
        MainApp.loadScene("/fxml/dashboard.fxml", "Blood Bank - Dashboard");
    }
}
