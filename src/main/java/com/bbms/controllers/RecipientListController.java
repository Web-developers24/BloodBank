package com.bbms.controllers;

import com.bbms.dao.RecipientDao;
import com.bbms.dao.BloodRequestDao;
import com.bbms.model.Recipient;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.stream.Collectors;

public class RecipientListController {
    private static final Logger logger = LogManager.getLogger(RecipientListController.class);
    
    @FXML private TextField searchField;
    @FXML private ComboBox<String> bloodGroupFilter;
    @FXML private ComboBox<String> statusFilter;
    @FXML private TableView<Recipient> recipientTable;
    @FXML private TableColumn<Recipient, Long> idColumn;
    @FXML private TableColumn<Recipient, String> nameColumn;
    @FXML private TableColumn<Recipient, String> bloodGroupColumn;
    @FXML private TableColumn<Recipient, Integer> ageColumn;
    @FXML private TableColumn<Recipient, String> phoneColumn;
    @FXML private TableColumn<Recipient, String> hospitalColumn;
    @FXML private TableColumn<Recipient, Integer> pendingRequestsColumn;
    @FXML private TableColumn<Recipient, Void> actionsColumn;
    @FXML private Button prevPageBtn;
    @FXML private Button nextPageBtn;
    @FXML private Label pageLabel;
    @FXML private Label totalRecipientsLabel;
    @FXML private Label activeRequestsLabel;
    
    private final RecipientDao recipientDao = new RecipientDao();
    private final BloodRequestDao requestDao = new BloodRequestDao();
    private ObservableList<Recipient> recipients = FXCollections.observableArrayList();
    
    private int currentPage = 1;
    private int pageSize = 20;
    private int totalPages = 1;

    @FXML
    public void initialize() {
        setupFilters();
        setupTableColumns();
        setupActionsColumn();
        loadRecipients();
        loadStatistics();
    }
    
    private void setupFilters() {
        bloodGroupFilter.setItems(FXCollections.observableArrayList(
            "All", "A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"
        ));
        bloodGroupFilter.setValue("All");
        
        statusFilter.setItems(FXCollections.observableArrayList("All", "Active", "Inactive"));
        statusFilter.setValue("All");
    }
    
    private void setupTableColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        bloodGroupColumn.setCellValueFactory(new PropertyValueFactory<>("bloodGroup"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        hospitalColumn.setCellValueFactory(new PropertyValueFactory<>("hospitalName"));
        
        nameColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getFullName()));
        
        ageColumn.setCellValueFactory(cellData -> {
            LocalDate dob = cellData.getValue().getDateOfBirth();
            if (dob != null) {
                int age = Period.between(dob, LocalDate.now()).getYears();
                return new SimpleIntegerProperty(age).asObject();
            }
            return new SimpleIntegerProperty(0).asObject();
        });
        
        pendingRequestsColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(0).asObject());
    }
    
    private void setupActionsColumn() {
        actionsColumn.setCellFactory(col -> new TableCell<>() {
            private final Button viewBtn = new Button("View");
            private final Button editBtn = new Button("Edit");
            
            {
                viewBtn.getStyleClass().add("table-btn");
                editBtn.getStyleClass().add("table-btn");
                viewBtn.setOnAction(e -> handleViewRecipient(getTableRow().getItem()));
                editBtn.setOnAction(e -> handleEditRecipient(getTableRow().getItem()));
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    javafx.scene.layout.HBox buttons = new javafx.scene.layout.HBox(5);
                    buttons.getChildren().addAll(viewBtn, editBtn);
                    setGraphic(buttons);
                }
            }
        });
    }
    
    private void loadRecipients() {
        List<Recipient> allRecipients = recipientDao.findAll();
        List<Recipient> filtered = allRecipients.stream()
            .filter(this::matchesFilters)
            .collect(Collectors.toList());
        
        totalPages = Math.max(1, (int) Math.ceil((double) filtered.size() / pageSize));
        int fromIndex = (currentPage - 1) * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, filtered.size());
        
        if (fromIndex < filtered.size()) {
            recipients.setAll(filtered.subList(fromIndex, toIndex));
        } else {
            recipients.clear();
        }
        recipientTable.setItems(recipients);
        updatePagination();
    }
    
    private boolean matchesFilters(Recipient recipient) {
        String search = searchField.getText();
        if (search != null && !search.isEmpty()) {
            String lower = search.toLowerCase();
            boolean matches = recipient.getFullName().toLowerCase().contains(lower)
                || recipient.getBloodGroup().toLowerCase().contains(lower)
                || (recipient.getPhone() != null && recipient.getPhone().contains(lower));
            if (!matches) return false;
        }
        
        String bloodGroup = bloodGroupFilter.getValue();
        if (bloodGroup != null && !bloodGroup.equals("All")) {
            if (!recipient.getBloodGroup().equals(bloodGroup)) return false;
        }
        return true;
    }
    
    private void loadStatistics() {
        totalRecipientsLabel.setText(String.valueOf(recipientDao.findAll().size()));
        activeRequestsLabel.setText("0");
    }
    
    private void updatePagination() {
        pageLabel.setText("Page " + currentPage + " of " + totalPages);
        prevPageBtn.setDisable(currentPage <= 1);
        nextPageBtn.setDisable(currentPage >= totalPages);
    }

    @FXML private void handleSearch() { currentPage = 1; loadRecipients(); }
    @FXML private void handleClearFilters() {
        searchField.clear();
        bloodGroupFilter.setValue("All");
        statusFilter.setValue("All");
        currentPage = 1;
        loadRecipients();
    }
    @FXML private void handlePrevPage() { if (currentPage > 1) { currentPage--; loadRecipients(); } }
    @FXML private void handleNextPage() { if (currentPage < totalPages) { currentPage++; loadRecipients(); } }
    @FXML private void handleAddRecipient() { openRecipientForm(null); }
    
    private void handleViewRecipient(Recipient recipient) {
        if (recipient == null) return;
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Recipient Details");
        alert.setHeaderText(recipient.getFullName());
        alert.setContentText("Blood Group: " + recipient.getBloodGroup() + "\nPhone: " + recipient.getPhone());
        alert.showAndWait();
    }
    
    private void handleEditRecipient(Recipient recipient) {
        if (recipient != null) openRecipientForm(recipient);
    }
    
    private void openRecipientForm(Recipient recipient) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/recipient_form.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle(recipient == null ? "Add Recipient" : "Edit Recipient");
            stage.initModality(Modality.APPLICATION_MODAL);
            RecipientFormController controller = loader.getController();
            controller.setRecipient(recipient);
            controller.setOnSaveCallback(this::loadRecipients);
            stage.showAndWait();
        } catch (IOException e) {
            logger.error("Failed to open recipient form", e);
        }
    }
    
    @FXML private void handleNewRequest() { logger.info("New request"); }
    @FXML private void handleViewRequests() { logger.info("View requests"); }
    @FXML private void handleExport() { logger.info("Export"); }
}
