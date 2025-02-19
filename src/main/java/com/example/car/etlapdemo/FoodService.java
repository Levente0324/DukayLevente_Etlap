package com.example.car.etlapdemo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FoodService {
    private static final String DB_DRIVER = "mysql";
    private static final String DB_HOST = "localhost";
    private static final String DB_PORT = "3306";
    private static final String DB_DATABASE = "etlapdb";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    private final Connection connection;

    public FoodService() throws SQLException {
        String dbUrl = String.format("jdbc:%s://%s:%s/%s", DB_DRIVER, DB_HOST, DB_PORT, DB_DATABASE);
        connection = DriverManager.getConnection(dbUrl, DB_USER, DB_PASSWORD);
    }

    public List<Food> getAll() throws SQLException {
        List<Food> foods = new ArrayList<>();
        String sql = "SELECT * FROM etlap";
        try (Statement statement = connection.createStatement();
             ResultSet result = statement.executeQuery(sql)) {
            while (result.next()) {
                foods.add(new Food(
                        result.getInt("id"),
                        result.getString("nev"),
                        result.getString("leiras"),
                        result.getInt("ar"),
                        result.getString("kategoria")
                ));
            }
        }
        return foods;
    }

    public boolean create(Food food) throws SQLException {
        String sql = "INSERT INTO etlap(nev, leiras, ar, kategoria) VALUES(?,?,?,?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, food.getNev());
            statement.setString(2, food.getLeiras());
            statement.setInt(3, food.getAr());
            statement.setString(4, food.getKategoria());
            return statement.executeUpdate() == 1;
        }
    }

    public void close() throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM etlap WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            return statement.executeUpdate() == 1;
        }
    }

    public boolean updatePrice(int id, int newPrice) throws SQLException {
        String sql = "UPDATE etlap SET ar = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, newPrice);
            statement.setInt(2, id);
            return statement.executeUpdate() == 1;
        }
    }

    public boolean updateAllPrices(int amount, boolean isPercentage) throws SQLException {
        String sql = isPercentage ?
                "UPDATE etlap SET ar = ar * (1 + ?/100)" :
                "UPDATE etlap SET ar = ar + ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, amount);
            return statement.executeUpdate() > 0;
        }
    }
}
