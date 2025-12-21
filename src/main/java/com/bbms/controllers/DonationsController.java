package com.bbms.controllers;

import com.bbms.dao.DonationDao;
import com.bbms.dao.DonorDao;
import com.bbms.model.Donation;
import com.bbms.model.Donation.DonationStatus;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class DonationsController {
    private static final Logger logger = LogManager.getLogger(DonationsController.class);
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    @FXML private TextField searchField;
    @FXML private DatePicker fromDatePicker;
    @FXML private DatePicker toDatePicker;
    @FXML private ComboBox<String> statusFilter;
    @FXML private ComboBox<String> bloodGroupFilter;
    @FXML private TableView<Donation> donationsTable;
    @FXML private TableColumn<Donation, Long> idColumn;
    @FXML private TableColumn<Donation, String> dateColumn;
    @FXML private TableColumn<Donation, String> donorColumn;
    @FXML private TableColumn<Donation, String> bloodGroupColumn;
    @FXML private TableColumn<Donation, Integer> volumeColumn;
    @FXML private TableColumn<Donation, String> componentColumn;
    @FXML private TableColumn<Donation, String> statusColumn;
    @FXML private TableColumn<Donation, String> collectedByColumn;
    @FXML private TableColumn<Donation, Void> actionsColumn;
    @FXML private Button prevPageBtn;
    @FXML private Button nextPageBtn;
    @FXML private Label pageLabel;
    @FXML private Label totalDonationsLabel;
    @FXML private Label monthlyDonationsLabel;
    @FXML private Label todayDonationsLabel;
    @FXML private Label totalVolumeLabel;
    
    private final DonationDao donationDao = new DonationDao();
    private ObservableList<Donation> donations = FXCollections.observableArrayList();
    
    private int currentPage = 1;
    private int pageSize = 20;
    private int totalPages = 1;

    @FXML
    public void initialize() {
        setupFilters();
        setupTableColumns();
        setupActionsColumn();
        loadDonations();
        loadStatistics();
    }
    
    private void setupFilters() {
        statusFilter.setItems(FXCollections.observableArrayList(
            "All", "COMPLETED", "SCHEDULED", "IN_PROGRESS", "CANCELLED"
        ));
        statusFilter.setValue("All");
        
        bloodGroupFilter.setItems(FXCollections.observableArrayList(
            "All", "A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"
        ));
        bloodGroupFilter.setValue("All");
        
        toDatePicker.setValue(LocalDate.now());
        fromDatePicker.setValue(LocalDate.now().minusDays(30));
    }
    
    private void setupTableColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        volumeColumn.setCellValueFactory(new PropertyValueFactory<>("volumeMl"));
        bloodGroupColumn.setCellValueFactory(new PropertyValueFactory<>("bloodGroup"));
        
        dateColumn.setCellValueFactory(cellData -> {
            LocalDate date = cellData.getValue().getDonationDate();
            return new SimpleStringProperty(date != null ? date.format(DATE_FORMAT) : "");
        });
        
        donorColumn.setCellValueFactory(cellData -> {
            var donor = cellData.getValue().getDonor();
            return new SimpleStringProperty(donor != null ? donor.getFullName() : "Unknown");
        });
        
        componentColumn.setCellValueFactory(cellData -> {
            var component = cellData.getValue().getComponentType();
            return new SimpleStringProperty(component != null ? component.name() : "WHOLE_BLOOD");
        });
        
        statusColumn.setCellValueFactory(cellData -> {
            var status = cellData.getValue().getStatus();
            return new SimpleStringProperty(status != null ? status.name() : "");
        });
        
        collectedByColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getCollectedBy()));
    }
    
    private void setupActionsColumn() {
        actionsColumn.setCellFactory(col -> new TableCell<>() {
            private final Button viewBtn = new Button("View");
            
            {
                viewBtn.getStyleClass().add("table-btn");
                viewBtn.setOnAction(e -> handleViewDonation(getTableRow().getItem()));
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : viewBtn);
            }
        });
    }
    
    private void loadDonations() {
        List<Donation> allDonations = donationDao.findAll();
        List<Donation> filtered = allDonations.stream()
            .filter(this::matchesFilters)
            .collect(Collectors.toList());
        
        totalPages = Math.max(1, (int) Math.ceil((double) filtered.size() / pageSize));
        int fromIndex = (currentPage - 1) * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, filtered.size());
        
        if (fromIndex < filtered.size()) {
            donations.setAll(filtered.subList(fromIndex, toIndex));
        } else {
            donations.clear();
        }
        donationsTable.setItems(donations);
        updatePagination();
    }
    
    private boolean matchesFilters(Donation donation) {
        String search = searchField.getText();
        if (search != null && !search.isEmpty()) {
            String lower = search.toLowerCase();
            var donor = donation.getDonor();
            boolean matches = (donor != null && donor.getFullName().toLowerCase().contains(lower))
                || String.valueOf(donation.getId()).contains(lower);
            if (!matches) return false;
        }
        
        LocalDate from = fromDatePicker.getValue();
        LocalDate to = toDatePicker.getValue();
        LocalDate donationDate = donation.getDonationDate();
        if (from != null && donationDate != null && donationDate.isBefore(from)) return false;
        if (to != null && donationDate != null && donationDate.isAfter(to)) return false;
        
        String status = statusFilter.getValue();
        if (status != null && !status.equals("All")) {
            if (donation.getStatus() == null || !donation.getStatus().name().equals(status)) return false;
        }
        
        String bloodGroup = bloodGroupFilter.getValue();
        if (bloodGroup != null && !bloodGroup.equals("All")) {
            if (!donation.getBloodGroup().equals(bloodGroup)) return false;
        }
        return true;
    }
    
    private void loadStatistics() {
        List<Donation> allDonations = donationDao.findAll();
        totalDonationsLabel.setText(String.valueOf(allDonations.size()));
        
        LocalDate today = LocalDate.now();
        LocalDate monthStart = today.withDayOfMonth(1);
        
        long monthlyCount = allDonations.stream()
            .filter(d -> d.getDonationDate() != null && !d.getDonationDate().isBefore(monthStart))
            .count();
        monthlyDonationsLabel.setText(String.valueOf(monthlyCount));
        
        long todayCount = allDonations.stream()
            .filter(d -> today.equals(d.getDonationDate()))
            .count();
        todayDonationsLabel.setText(String.valueOf(todayCount));
        
        double totalVolume = allDonations.stream()
            .filter(d -> d.getStatus() == DonationStatus.COMPLETED)
            .mapToInt(d -> d.getVolumeMl() != null ? d.getVolumeMl() : 0)
            .sum() / 1000.0;
        totalVolumeLabel.setText(String.format("%.1f", totalVolume));
    }
    
    private void updatePagination() {
        pageLabel.setText("Page " + currentPage + " of " + totalPages);
        prevPageBtn.setDisable(currentPage <= 1);
        nextPageBtn.setDisable(currentPage >= totalPages);
    }

    @FXML private void handleSearch() { currentPage = 1; loadDonations(); }
    @FXML private void handleApplyFilters() { currentPage = 1; loadDonations(); }
    @FXML private void handleClearFilters() {
        searchField.clear();
        fromDatePicker.setValue(LocalDate.now().minusDays(30));
        toDatePicker.setValue(LocalDate.now());
        statusFilter.setValue("All");
        bloodGroupFilter.setValue("All");
        currentPage = 1;
        loadDonations();
    }
    @FXML private void handlePrevPage() { if (currentPage > 1) { currentPage--; loadDonations(); } }
    @FXML private void handleNextPage() { if (currentPage < totalPages) { currentPage++; loadDonations(); } }
    @FXML private void handleNewDonation() { logger.info("Opening new donation form"); }
    
    private void handleViewDonation(Donation donation) {
        if (donation == null) return;
        var donor = donation.getDonor();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Donation Details");
        alert.setHeaderText("Donation #" + donation.getId());
        alert.setContentText(
            "Date: " + donation.getDonationDate() + "\n" +
            "Donor: " + (donor != null ? donor.getFullName() : "Unknown") + "\n" +
            "Blood Group: " + donation.getBloodGroup() + "\n" +
            "Volume: " + donation.getVolumeMl() + " ml\n" +
            "Status: " + donation.getStatus()
        );
        alert.showAndWait();
    }
    
    @FXML private void handleSchedule() { logger.info("Opening donation scheduler"); }
    @FXML private void handleExportReport() { logger.info("Exporting donations report"); }
    @FXML private void handlePrintLabels() { logger.info("Printing donation labels"); }
}
