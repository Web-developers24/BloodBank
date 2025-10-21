package org.example.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.dao.BloodStockDao;
import org.example.model.BloodStock;
import org.example.MainApp;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class BloodStockController {

    @FXML private TableView<BloodStock> tblStock;
    @FXML private TableColumn<BloodStock, String> colGroup;
    @FXML private TableColumn<BloodStock, Integer> colUnits;
    @FXML private TableColumn<BloodStock, String> colUpdated;

    private final BloodStockDao stockDao = new BloodStockDao();

    @FXML
    public void initialize() {
        colGroup.setCellValueFactory(new PropertyValueFactory<>("bloodGroup"));
        colUnits.setCellValueFactory(new PropertyValueFactory<>("unitsAvailable"));
        colUpdated.setCellValueFactory(cellData -> {
            if (cellData.getValue().getLastUpdated() != null) {
                return new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().getLastUpdated()
                                .format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")));
            } else {
                return new javafx.beans.property.SimpleStringProperty("N/A");
            }
        });

        loadStockData();
    }

    private void loadStockData() {
        List<BloodStock> stockList = stockDao.getAllStocks();
        ObservableList<BloodStock> observableStock = FXCollections.observableArrayList(stockList);
        tblStock.setItems(observableStock);
    }

    @FXML
    private void goBackToMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/main_menu.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root, 600, 400);
            Stage stage = MainApp.getPrimaryStage();
            stage.setTitle("Blood Bank - Main Menu");
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

