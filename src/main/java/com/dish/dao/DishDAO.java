package com.dish.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.dish.database.DatabaseConnection;
import com.dish.model.Dish;

public class DishDAO {

    public List<Dish> getAllDishes() {
        List<Dish> dishes = new ArrayList<>();
        String sql = "SELECT * FROM dishes ORDER BY id";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                dishes.add(extractDishFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dishes;
    }
    
    public Dish getDishById(int id) {
        String sql = "SELECT * FROM dishes WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractDishFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    public boolean addDish(Dish dish) {
        String sql = "INSERT INTO dishes(name, type, price, ingredients, introduction, photo_path) VALUES(?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, dish.getName());
            pstmt.setString(2, dish.getType());
            pstmt.setDouble(3, dish.getPrice());
            pstmt.setString(4, dish.getIngredients());
            pstmt.setString(5, dish.getIntroduction());
            pstmt.setString(6, dish.getPhotoPath());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                // Get the auto-generated ID and set it in the Dish object
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        dish.setId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateDish(Dish dish) {
        String sql = "UPDATE dishes SET name = ?, type = ?, price = ?, ingredients = ?, introduction = ?, photo_path = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, dish.getName());
            pstmt.setString(2, dish.getType());
            pstmt.setDouble(3, dish.getPrice());
            pstmt.setString(4, dish.getIngredients());
            pstmt.setString(5, dish.getIntroduction());
            pstmt.setString(6, dish.getPhotoPath());
            pstmt.setInt(7, dish.getId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteDish(int dishId) {
        String sql = "DELETE FROM dishes WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, dishId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private Dish extractDishFromResultSet(ResultSet rs) throws SQLException {
        return new Dish(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("type"),
                rs.getDouble("price"),
                rs.getString("ingredients"),
                rs.getString("introduction"),
                rs.getString("photo_path")
        );
    }
}