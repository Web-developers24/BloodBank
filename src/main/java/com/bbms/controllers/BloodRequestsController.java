package com.bbms.controllers;

import com.bbms.dao.BloodRequestDao;
import com.bbms.dao.BloodStockDao;
import com.bbms.model.BloodRequest;
import com.bbms.model.BloodRequest.Priority;
import com.bbms.model.BloodRequest.RequestStatus;
import com.bbms.service.BloodStockService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class BloodRequestsController {
    private static final Logger logger = LogManager.getLogger(BloodRequestsController.class);
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    @FXML private TextField searchField;
    @FXML private ComboBox<String> statusFilter;
    @FXML private ComboBox<String> priorityFilter;
    @FXML private ComboBox<String> bloodGroupFilter;
    @FXML private VBox urgentSection;
    @FXML private TableView<BloodRequest> urgentRequestsTable;
    @FXML private TableView<BloodRequest> requestsTable;
    @FXML private TableColumn<BloodRequest, Long> idColumn;
    @FXML private TableColumn<BloodRequest, String> dateColumn;
    @FXML private TableColumn<BloodRequest, String> recipientColumn;
    @FXML private TableColumn<BloodRequest, String> bloodGroupColumn;
    @FXML private TableColumn<BloodRequest, String> componentColumn;
    @FXML private TableColumn<BloodRequest, Integer> unitsColumn;
    @FXML private TableColumn<BloodRequest, String> priorityColumn;
    @FXML private TableColumn<BloodRequest, String> statusColumn;
    @FXML private TableColumn<BloodRequest, String> hospitalColumn;
    @FXML private TableColumn<BloodRequest, Void> actionsColumn;
    @FXML private Button prevPageBtn;
    @FXML private Button nextPageBtn;
    @FXML private Label pageLabel;
    @FXML private Label pendingLabel;
    @FXML private Label urgentLabel;
    @FXML private Label fulfilledTodayLabel;
    
    private final BloodRequestDao requestDao = new BloodRequestDao();
    private final BloodStockService stockService = new BloodStockService();
    private ObservableList<BloodRequest> requests = FXCollections.observableArrayList();
    
    private int currentPage = 1;
    private int pageSize = 20;
    private int totalPages = 1;

    @FXML
    public void initialize() {
        setupFilters();
        setupTableColumns();
        setupActionsColumn();
        loadRequests();
        loadUrgentRequests();
        loadStatistics();
    }
    
    private void setupFilters() {
        statusFilter.setItems(FXCollections.observableArrayList(
            "All", "PENDING", "APPROVED", "FULFILLED", "CANCELLED"
        ));
        statusFilter.setValue("All");
        
        priorityFilter.setItems(FXCollections.observableArrayList(
            "All", "EMERGENCY", "HIGH", "NORMAL", "LOW"
        ));
        priorityFilter.setValue("All");
        
        bloodGroupFilter.setItems(FXCollections.observableArrayList(
            "All", "A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"
        ));
        bloodGroupFilter.setValue("All");
    }
    
    private void setupTableColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        bloodGroupColumn.setCellValueFactory(new PropertyValueFactory<>("bloodGroup"));
        hospitalColumn.setCellValueFactory(new PropertyValueFactory<>("hospitalName"));
        
        unitsColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleIntegerProperty(
                cellData.getValue().getUnitsRequested() != null ? cellData.getValue().getUnitsRequested() : 0
            ).asObject());
        
        dateColumn.setCellValueFactory(cellData -> {
            LocalDate date = cellData.getValue().getRequestDate();
            return new SimpleStringProperty(date != null ? date.format(DATE_FORMAT) : "");
        });
        
        recipientColumn.setCellValueFactory(cellData -> {
            var recipient = cellData.getValue().getRecipient();
            return new SimpleStringProperty(recipient != null ? recipient.getFullName() : "Unknown");
        });
        
        componentColumn.setCellValueFactory(cellData -> {
            var component = cellData.getValue().getComponentType();
            return new SimpleStringProperty(component != null ? component.name() : "WHOLE_BLOOD");
        });
        
        priorityColumn.setCellValueFactory(cellData -> {
            var priority = cellData.getValue().getPriority();
            return new SimpleStringProperty(priority != null ? priority.name() : "NORMAL");
        });
        
        statusColumn.setCellValueFactory(cellData -> {
            var status = cellData.getValue().getStatus();
            return new SimpleStringProperty(status != null ? status.name() : "");
        });
    }
    
    private void setupActionsColumn() {
        actionsColumn.setCellFactory(col -> new TableCell<>() {
            private final Button viewBtn = new Button("View");
            private final Button processBtn = new Button("Process");
            
            {
                viewBtn.getStyleClass().add("table-btn");
                processBtn.getStyleClass().addAll("table-btn", "success-btn");
                viewBtn.setOnAction(e -> handleViewRequest(getTableRow().getItem()));
                processBtn.setOnAction(e -> handleProcessRequest(getTableRow().getItem()));
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    BloodRequest request = getTableRow().getItem();
                    javafx.scene.layout.HBox buttons = new javafx.scene.layout.HBox(5);
                    buttons.getChildren().add(viewBtn);
                    if (request != null && request.getStatus() == RequestStatus.PENDING) {
                        buttons.getChildren().add(processBtn);
                    }
                    setGraphic(buttons);
                }
            }
        });
    }
    
    private void loadRequests() {
        List<BloodRequest> allRequests = requestDao.findAll();
        List<BloodRequest> filtered = allRequests.stream()
            .filter(this::matchesFilters)
            .collect(Collectors.toList());
        
        totalPages = Math.max(1, (int) Math.ceil((double) filtered.size() / pageSize));
        int fromIndex = (currentPage - 1) * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, filtered.size());
        
        if (fromIndex < filtered.size()) {
            requests.setAll(filtered.subList(fromIndex, toIndex));
        } else {
            requests.clear();
        }
        requestsTable.setItems(requests);
        updatePagination();
    }
    
    private void loadUrgentRequests() {
        List<BloodRequest> urgent = requestDao.findAll().stream()
            .filter(r -> r.getStatus() == RequestStatus.PENDING)
            .filter(r -> r.getPriority() == Priority.EMERGENCY || r.getPriority() == Priority.HIGH)
            .collect(Collectors.toList());
        
        if (!urgent.isEmpty()) {
            urgentSection.setVisible(true);
            urgentSection.setManaged(true);
            urgentRequestsTable.setItems(FXCollections.observableArrayList(urgent));
        } else {
            urgentSection.setVisible(false);
            urgentSection.setManaged(false);
        }
    }
    
    private boolean matchesFilters(BloodRequest request) {
        String search = searchField.getText();
        if (search != null && !search.isEmpty()) {
            String lower = search.toLowerCase();
            var recipient = request.getRecipient();
            boolean matches = (recipient != null && recipient.getFullName().toLowerCase().contains(lower))
                || (request.getHospitalName() != null && request.getHospitalName().toLowerCase().contains(lower));
            if (!matches) return false;
        }
        
        String status = statusFilter.getValue();
        if (status != null && !status.equals("All")) {
            if (request.getStatus() == null || !request.getStatus().name().equals(status)) return false;
        }
        
        String priority = priorityFilter.getValue();
        if (priority != null && !priority.equals("All")) {
            if (request.getPriority() == null || !request.getPriority().name().equals(priority)) return false;
        }
        
        String bloodGroup = bloodGroupFilter.getValue();
        if (bloodGroup != null && !bloodGroup.equals("All")) {
            if (!request.getBloodGroup().equals(bloodGroup)) return false;
        }
        return true;
    }
    
    private void loadStatistics() {
        List<BloodRequest> all = requestDao.findAll();
        long pending = all.stream().filter(r -> r.getStatus() == RequestStatus.PENDING).count();
        pendingLabel.setText(String.valueOf(pending));
        
        long urgentCount = all.stream()
            .filter(r -> r.getStatus() == RequestStatus.PENDING)
            .filter(r -> r.getPriority() == Priority.EMERGENCY || r.getPriority() == Priority.HIGH)
            .count();
        urgentLabel.setText(String.valueOf(urgentCount));
        
        long fulfilledToday = all.stream()
            .filter(r -> r.getStatus() == RequestStatus.FULFILLED)
            .count();
        fulfilledTodayLabel.setText(String.valueOf(fulfilledToday));
    }
    
    private void updatePagination() {
        pageLabel.setText("Page " + currentPage + " of " + totalPages);
        prevPageBtn.setDisable(currentPage <= 1);
        nextPageBtn.setDisable(currentPage >= totalPages);
    }

    @FXML private void handleSearch() { currentPage = 1; loadRequests(); }
    @FXML private void handleApplyFilters() { currentPage = 1; loadRequests(); }
    @FXML private void handleClearFilters() {
        searchField.clear();
        statusFilter.setValue("All");
        priorityFilter.setValue("All");
        bloodGroupFilter.setValue("All");
        currentPage = 1;
        loadRequests();
    }
    @FXML private void handlePrevPage() { if (currentPage > 1) { currentPage--; loadRequests(); } }
    @FXML private void handleNextPage() { if (currentPage < totalPages) { currentPage++; loadRequests(); } }
    @FXML private void handleNewRequest() { logger.info("Opening new blood request form"); }
    
    private void handleViewRequest(BloodRequest request) {
        if (request == null) return;
        var recipient = request.getRecipient();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Blood Request Details");
        alert.setHeaderText("Request #" + request.getId());
        alert.setContentText(
            "Date: " + request.getRequestDate() + "\n" +
            "Recipient: " + (recipient != null ? recipient.getFullName() : "Unknown") + "\n" +
            "Blood Group: " + request.getBloodGroup() + "\n" +
            "Units Required: " + request.getUnitsRequested() + "\n" +
            "Priority: " + request.getPriority() + "\n" +
            "Status: " + request.getStatus()
        );
        alert.showAndWait();
    }
    
    private void handleProcessRequest(BloodRequest request) {
        if (request == null) return;
        
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Process Request");
        confirm.setHeaderText("Fulfill blood request?");
        confirm.setContentText("This will allocate " + request.getUnitsRequested() + 
            " units of " + request.getBloodGroup() + " blood.");
        
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                request.setStatus(RequestStatus.FULFILLED);
                requestDao.update(request);
                logger.info("Fulfilled request #{}", request.getId());
                loadRequests();
                loadUrgentRequests();
                loadStatistics();
            }
        });
    }
    
    @FXML private void handleProcessSelected() {
        BloodRequest selected = requestsTable.getSelectionModel().getSelectedItem();
        if (selected != null) handleProcessRequest(selected);
    }
    @FXML private void handleViewCompatibility() { logger.info("Viewing compatibility"); }
    @FXML private void handleExportPending() { logger.info("Exporting pending requests"); }
}
