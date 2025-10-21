package org.example.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.beans.property.SimpleStringProperty;
import org.example.dao.DonorDao;
import org.example.model.Donor;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.MainApp;

public class DonorListController {

    @FXML private TableView<Donor> donorTable;
    @FXML private TableColumn<Donor, String> colName;
    @FXML private TableColumn<Donor, String> colBloodGroup;
    @FXML private TableColumn<Donor, String> colPhone;
    @FXML private TableColumn<Donor, String> colLastDonation;

    private final DonorDao donorDao = new DonorDao();

    @FXML
    public void initialize() {
        // Basic column mappings
        colName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        colBloodGroup.setCellValueFactory(new PropertyValueFactory<>("bloodGroup"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));

        // ✅ Convert LocalDate to readable String (or show dash if null)
        colLastDonation.setCellValueFactory(cellData ->
                new SimpleStringProperty(
                        cellData.getValue().getLastDonationDate() != null
                                ? cellData.getValue().getLastDonationDate().toString()
                                : "—"
                )
        );

        // Load donor data into table
        donorTable.setItems(FXCollections.observableArrayList(donorDao.getAllDonors()));

        // Optional: sort by latest donors first
        donorTable.getSortOrder().add(colLastDonation);
        colLastDonation.setSortType(TableColumn.SortType.DESCENDING);
    }
    @FXML
    private void goBackToMenu() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(org.example.MainApp.class.getResource("/main_menu.fxml"));
            javafx.scene.Parent root = loader.load();
            javafx.scene.Scene scene = new javafx.scene.Scene(root, 600, 400);
            javafx.stage.Stage stage = org.example.MainApp.getPrimaryStage();
            stage.setTitle("Blood Bank - Main Menu");
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
