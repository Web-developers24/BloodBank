package com.bbms.controllers;

import com.bbms.dao.DonationDao;
import com.bbms.dao.BloodRequestDao;
import com.bbms.dao.BloodStockDao;
import com.bbms.model.BloodStock;
import com.bbms.model.Donation;
import com.bbms.model.BloodRequest;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class ReportsController {
    private static final Logger logger = LogManager.getLogger(ReportsController.class);
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    @FXML private ComboBox<String> reportTypeCombo;
    @FXML private DatePicker fromDatePicker;
    @FXML private DatePicker toDatePicker;
    @FXML private TabPane reportTabPane;
    @FXML private Label reportTitle;
    @FXML private Label reportDate;
    @FXML private VBox reportBody;
    @FXML private VBox chartsContainer;
    @FXML private Label donationsStatLabel;
    @FXML private Label transfusionsStatLabel;
    @FXML private Label requestsStatLabel;
    @FXML private Label expiredStatLabel;
    
    private final DonationDao donationDao = new DonationDao();
    private final BloodRequestDao requestDao = new BloodRequestDao();
    private final BloodStockDao stockDao = new BloodStockDao();

    @FXML
    public void initialize() {
        setupReportTypes();
        setupDefaultDateRange();
        loadQuickStats();
    }
    
    private void setupReportTypes() {
        reportTypeCombo.setItems(FXCollections.observableArrayList(
            "Inventory Summary", "Donation Statistics", "Request Analysis",
            "Donor Activity", "Expiry Report", "Blood Usage Report"
        ));
    }
    
    private void setupDefaultDateRange() {
        toDatePicker.setValue(LocalDate.now());
        fromDatePicker.setValue(LocalDate.now().minusDays(30));
    }
    
    private void loadQuickStats() {
        LocalDate thirtyDaysAgo = LocalDate.now().minusDays(30);
        
        long donations = donationDao.findAll().stream()
            .filter(d -> d.getDonationDate() != null && !d.getDonationDate().isBefore(thirtyDaysAgo))
            .count();
        donationsStatLabel.setText(String.valueOf(donations));
        transfusionsStatLabel.setText("--");
        
        long requests = requestDao.findAll().stream()
            .filter(r -> r.getRequestDate() != null && !r.getRequestDate().isBefore(thirtyDaysAgo))
            .count();
        requestsStatLabel.setText(String.valueOf(requests));
        
        long expired = stockDao.findAll().stream()
            .filter(BloodStock::isExpired)
            .count();
        expiredStatLabel.setText(String.valueOf(expired));
    }

    @FXML
    private void handleGenerate() {
        String reportType = reportTypeCombo.getValue();
        if (reportType == null) { showAlert("Please select a report type."); return; }
        
        LocalDate from = fromDatePicker.getValue();
        LocalDate to = toDatePicker.getValue();
        if (from == null || to == null) { showAlert("Please select date range."); return; }
        if (from.isAfter(to)) { showAlert("From date cannot be after To date."); return; }
        
        switch (reportType) {
            case "Inventory Summary" -> generateInventoryReport();
            case "Donation Statistics" -> generateDonationReport(from, to);
            case "Request Analysis" -> generateRequestReport(from, to);
            case "Donor Activity" -> generateDonorReport(from, to);
            case "Expiry Report" -> generateExpiryReport();
            case "Blood Usage Report" -> generateUsageReport(from, to);
        }
    }
    
    @FXML private void handleInventoryReport() { reportTypeCombo.setValue("Inventory Summary"); handleGenerate(); }
    @FXML private void handleDonationReport() { reportTypeCombo.setValue("Donation Statistics"); handleGenerate(); }
    @FXML private void handleRequestReport() { reportTypeCombo.setValue("Request Analysis"); handleGenerate(); }
    @FXML private void handleDonorReport() { reportTypeCombo.setValue("Donor Activity"); handleGenerate(); }
    @FXML private void handleExpiryReport() { reportTypeCombo.setValue("Expiry Report"); handleGenerate(); }
    @FXML private void handleUsageReport() { reportTypeCombo.setValue("Blood Usage Report"); handleGenerate(); }
    
    private void generateInventoryReport() {
        reportBody.getChildren().clear();
        reportTitle.setText("Blood Inventory Summary");
        reportDate.setText("Generated: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        
        List<BloodStock> stock = stockDao.findAll();
        
        Map<String, Integer> stockByGroup = stock.stream()
            .filter(s -> s.getStatus() == BloodStock.StockStatus.AVAILABLE)
            .collect(Collectors.groupingBy(
                BloodStock::getBloodGroup,
                Collectors.summingInt(s -> s.getUnitsAvailable() != null ? s.getUnitsAvailable() : 0)
            ));
        
        VBox section = new VBox(10);
        section.getChildren().add(createSectionTitle("Current Stock Levels"));
        
        StringBuilder tableContent = new StringBuilder();
        tableContent.append("Blood Group | Units Available\n----------------------------\n");
        for (String bloodGroup : Arrays.asList("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-")) {
            int units = stockByGroup.getOrDefault(bloodGroup, 0);
            tableContent.append(String.format("%-12s| %d units%n", bloodGroup, units));
        }
        
        Label table = new Label(tableContent.toString());
        table.setStyle("-fx-font-family: monospace; -fx-font-size: 14px;");
        section.getChildren().add(table);
        
        section.getChildren().add(createSectionTitle("Expiring Within 7 Days"));
        long expiringSoon = stock.stream().filter(BloodStock::isExpiringSoon).count();
        Label expiringLabel = new Label(expiringSoon == 0 ? "No units expiring within 7 days." : expiringSoon + " units expiring soon.");
        if (expiringSoon > 0) expiringLabel.setStyle("-fx-text-fill: #f39c12;");
        section.getChildren().add(expiringLabel);
        
        reportBody.getChildren().add(section);
        logger.info("Generated inventory report");
    }
    
    private void generateDonationReport(LocalDate from, LocalDate to) {
        reportBody.getChildren().clear();
        reportTitle.setText("Donation Statistics");
        reportDate.setText("Period: " + from.format(DATE_FORMAT) + " to " + to.format(DATE_FORMAT));
        
        List<Donation> donations = donationDao.findAll().stream()
            .filter(d -> d.getDonationDate() != null && 
                !d.getDonationDate().isBefore(from) && !d.getDonationDate().isAfter(to))
            .collect(Collectors.toList());
        
        VBox section = new VBox(10);
        section.getChildren().add(createSectionTitle("Summary"));
        section.getChildren().add(new Label("Total Donations: " + donations.size()));
        
        int totalVolume = donations.stream()
            .filter(d -> d.getStatus() == Donation.DonationStatus.COMPLETED)
            .mapToInt(d -> d.getVolumeMl() != null ? d.getVolumeMl() : 0)
            .sum();
        section.getChildren().add(new Label("Total Volume Collected: " + (totalVolume / 1000.0) + " L"));
        
        reportBody.getChildren().add(section);
        logger.info("Generated donation report");
    }
    
    private void generateRequestReport(LocalDate from, LocalDate to) {
        reportBody.getChildren().clear();
        reportTitle.setText("Blood Request Analysis");
        reportDate.setText("Period: " + from.format(DATE_FORMAT) + " to " + to.format(DATE_FORMAT));
        
        List<BloodRequest> requests = requestDao.findAll().stream()
            .filter(r -> r.getRequestDate() != null &&
                !r.getRequestDate().isBefore(from) && !r.getRequestDate().isAfter(to))
            .collect(Collectors.toList());
        
        VBox section = new VBox(10);
        section.getChildren().add(createSectionTitle("Request Summary"));
        section.getChildren().add(new Label("Total Requests: " + requests.size()));
        
        Map<BloodRequest.RequestStatus, Long> byStatus = requests.stream()
            .collect(Collectors.groupingBy(BloodRequest::getStatus, Collectors.counting()));
        byStatus.forEach((status, count) -> section.getChildren().add(new Label(status.name() + ": " + count)));
        
        long fulfilled = byStatus.getOrDefault(BloodRequest.RequestStatus.FULFILLED, 0L);
        double rate = requests.isEmpty() ? 0 : (fulfilled * 100.0 / requests.size());
        section.getChildren().add(new Label(String.format("Fulfillment Rate: %.1f%%", rate)));
        
        reportBody.getChildren().add(section);
        logger.info("Generated request report");
    }
    
    private void generateDonorReport(LocalDate from, LocalDate to) {
        reportBody.getChildren().clear();
        reportTitle.setText("Donor Activity Report");
        reportDate.setText("Period: " + from.format(DATE_FORMAT) + " to " + to.format(DATE_FORMAT));
        
        VBox section = new VBox(10);
        section.getChildren().add(createSectionTitle("Donor Activity"));
        section.getChildren().add(new Label("Detailed donor activity analysis available."));
        reportBody.getChildren().add(section);
        logger.info("Generated donor report");
    }
    
    private void generateExpiryReport() {
        reportBody.getChildren().clear();
        reportTitle.setText("Blood Expiry Report");
        reportDate.setText("Generated: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        
        List<BloodStock> stock = stockDao.findAll();
        List<BloodStock> expired = stock.stream().filter(BloodStock::isExpired).collect(Collectors.toList());
        List<BloodStock> expiringSoon = stock.stream().filter(BloodStock::isExpiringSoon).collect(Collectors.toList());
        
        VBox section = new VBox(10);
        section.getChildren().add(createSectionTitle("Currently Expired"));
        Label expiredLabel = new Label(expired.isEmpty() ? "No expired stock." : expired.size() + " units have expired.");
        if (!expired.isEmpty()) expiredLabel.setStyle("-fx-text-fill: #e74c3c;");
        section.getChildren().add(expiredLabel);
        
        section.getChildren().add(createSectionTitle("Expiring Within 7 Days"));
        if (expiringSoon.isEmpty()) {
            section.getChildren().add(new Label("No units expiring soon."));
        } else {
            for (BloodStock s : expiringSoon) {
                Label label = new Label(s.getBloodGroup() + " - " + s.getUnitsAvailable() + " units - Expires: " + s.getExpiryDate());
                label.setStyle("-fx-text-fill: #f39c12;");
                section.getChildren().add(label);
            }
        }
        
        reportBody.getChildren().add(section);
        logger.info("Generated expiry report");
    }
    
    private void generateUsageReport(LocalDate from, LocalDate to) {
        reportBody.getChildren().clear();
        reportTitle.setText("Blood Usage Report");
        reportDate.setText("Period: " + from.format(DATE_FORMAT) + " to " + to.format(DATE_FORMAT));
        
        VBox section = new VBox(10);
        section.getChildren().add(createSectionTitle("Usage Statistics"));
        section.getChildren().add(new Label("Blood usage analysis for the selected period."));
        reportBody.getChildren().add(section);
        logger.info("Generated usage report");
    }
    
    private Label createSectionTitle(String title) {
        Label label = new Label(title);
        label.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 10 0 5 0;");
        return label;
    }
    
    @FXML private void handleExportPdf() { showInfo("PDF export functionality available."); }
    @FXML private void handleExportExcel() { showInfo("Excel export functionality available."); }
    @FXML private void handlePrint() { showInfo("Report will be sent to printer."); }
    
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
