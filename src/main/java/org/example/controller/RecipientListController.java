package org.example.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.beans.property.SimpleStringProperty;
import org.example.dao.RecipientDao;
import org.example.model.Recipient;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.MainApp;


public class RecipientListController {

    @FXML private TableView<Recipient> recipientTable;
    @FXML private TableColumn<Recipient, String> colName;
    @FXML private TableColumn<Recipient, String> colBloodGroup;
    @FXML private TableColumn<Recipient, String> colPhone;
    @FXML private TableColumn<Recipient, String> colRequestDate;

    private final RecipientDao recipientDao = new RecipientDao();

    @FXML
    public void initialize() {
        // Map table columns to model properties
        colName.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getFullName()));

        colBloodGroup.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getBloodGroupRequired()));

        colPhone.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getPhone()));

        // Convert LocalDate → readable string or dash
        colRequestDate.setCellValueFactory(cellData ->
                new SimpleStringProperty(
                        cellData.getValue().getRequestDate() != null
                                ? cellData.getValue().getRequestDate().toString()
                                : "—"
                )
        );

        // Load data into table
        recipientTable.setItems(FXCollections.observableArrayList(recipientDao.getAllRecipients()));

        // Optional: sort by most recent requests first
        recipientTable.getSortOrder().add(colRequestDate);
        colRequestDate.setSortType(TableColumn.SortType.DESCENDING);
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

