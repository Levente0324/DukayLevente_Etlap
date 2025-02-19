package com.example.car.etlapdemo;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.sql.*;

public class FoodController {
    @FXML private TableView<Food> foodTable;
    @FXML private TableColumn<Food, String> nameColumn;
    @FXML private TableColumn<Food, String> categoryColumn;
    @FXML private TableColumn<Food, Integer> priceColumn;
    @FXML private TextArea descriptionArea;
    @FXML private Spinner<Integer> fixAmountSpinner;
    @FXML private Spinner<Integer> percentSpinner;
    private FoodService foodService;

    private final ObservableList<Food> foodList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        try {
            foodService = new FoodService();
            nameColumn.setCellValueFactory(new PropertyValueFactory<>("nev"));
            categoryColumn.setCellValueFactory(new PropertyValueFactory<>("kategoria"));
            priceColumn.setCellValueFactory(new PropertyValueFactory<>("ar"));

            foodTable.setItems(foodList);
            loadFoods();

            foodTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    descriptionArea.setText(newSelection.getLeiras());
                }
            });

            SpinnerValueFactory<Integer> fixValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(50, 3000, 50, 50);
            fixAmountSpinner.setValueFactory(fixValueFactory);
            fixAmountSpinner.setEditable(true);

            SpinnerValueFactory<Integer> percentValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(5, 50, 5, 5);
            percentSpinner.setValueFactory(percentValueFactory);
            percentSpinner.setEditable(true);

            nameColumn.setSortType(TableColumn.SortType.ASCENDING);
            foodTable.getSortOrder().add(nameColumn);
        } catch (SQLException e) {
            showError("Adatbázis hiba", "Nem sikerült kapcsolódni az adatbázishoz");
        }
    }

    private void loadFoods() {
        try {
            foodList.clear();
            foodList.addAll(foodService.getAll());
        } catch (SQLException e) {
            showError("Adatbázis hiba", e.getMessage());
        }
    }

    @FXML
    protected void onNewFoodClick() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("add-food.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 300, 400);
            Stage stage = new Stage();
            stage.setTitle("Új étel felvétele");
            stage.setScene(scene);
            stage.showAndWait();
            loadFoods();
        } catch (Exception e) {
            showError("Hiba", "Nem sikerült megnyitni az új ablakot.");
        }
    }

    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    protected void onDeleteClick() {
        Food selectedFood = foodTable.getSelectionModel().getSelectedItem();
        if (selectedFood == null) {
            showError("Hiba", "Nincs kiválasztva étel!");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Törlés megerősítése");
        alert.setHeaderText(null);
        alert.setContentText("Biztosan törli ezt az ételt: " + selectedFood.getNev() + "?");

        if (alert.showAndWait().orElse(null) == ButtonType.OK) {
            try {
                if (foodService.delete(selectedFood.getId())) {
                    loadFoods();
                } else {
                    showError("Hiba", "Nem sikerült törölni az ételt");
                }
            } catch (SQLException e) {
                showError("Adatbázis hiba", e.getMessage());
            }
        }
    }

    @FXML
    protected void onFixIncrease() {
        updatePrice(fixAmountSpinner.getValue(), false);
    }

    @FXML
    protected void onPercentIncrease() {
        updatePrice(percentSpinner.getValue(), true);
    }

    private void updatePrice(int amount, boolean isPercentage) {
        Food selectedFood = foodTable.getSelectionModel().getSelectedItem();
        String message = String.format("Biztosan növeli %s árát %d%s?",
                selectedFood != null ? selectedFood.getNev() : "az összes étel",
                amount,
                isPercentage ? "%-kal" : " Ft-tal");

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Áremelés megerősítése");
        alert.setHeaderText(null);
        alert.setContentText(message);

        if (alert.showAndWait().orElse(null) == ButtonType.OK) {
            try {
                boolean success;
                if (selectedFood != null) {
                    int newPrice = isPercentage ?
                            selectedFood.getAr() * (100 + amount) / 100 :
                            selectedFood.getAr() + amount;
                    success = foodService.updatePrice(selectedFood.getId(), newPrice);
                } else {
                    success = foodService.updateAllPrices(amount, isPercentage);
                }

                if (success) {
                    loadFoods();
                } else {
                    showError("Hiba", "Nem sikerült módosítani az árat");
                }
            } catch (SQLException e) {
                showError("Adatbázis hiba", e.getMessage());
            }
        }
    }
}
