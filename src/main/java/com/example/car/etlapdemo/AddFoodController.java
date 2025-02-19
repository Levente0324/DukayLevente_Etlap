package com.example.car.etlapdemo;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.sql.*;

public class AddFoodController {
    @FXML private TextField nameField;
    @FXML private TextField categoryField;
    @FXML private TextField priceField;
    @FXML private TextArea descriptionField;
    private FoodService foodService;

    @FXML
    public void initialize() {
        try {
            foodService = new FoodService();
        } catch (SQLException e) {
            showError("Adatbázis hiba", "Nem sikerült kapcsolódni az adatbázishoz");
        }
    }

    @FXML
    protected void onSaveClick() {
        try {
            Food newFood = new Food(
                    0,
                    nameField.getText(),
                    descriptionField.getText(),
                    Integer.parseInt(priceField.getText()),
                    categoryField.getText()
            );

            if (foodService.create(newFood)) {
                closeWindow();
            } else {
                showError("Hiba", "Nem sikerült menteni az ételt");
            }
        } catch (SQLException e) {
            showError("Adatbázis hiba", e.getMessage());
        } catch (NumberFormatException e) {
            showError("Hibás ár", "Az ár csak szám lehet!");
        }
    }

    @FXML
    protected void onCancelClick() {
        closeWindow();
    }

    private void closeWindow() {
        ((Stage) nameField.getScene().getWindow()).close();
    }

    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
