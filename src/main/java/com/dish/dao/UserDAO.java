package com.dish.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.dish.database.DatabaseConnection;

public class UserDAO {
    // This method validates user credentials against the database.
    public boolean validateUser(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";

        System.out.println("USER DAO: Executing query for user='" + username + "'");

        // Ensure that the SQL query is correct and uses prepared statements to prevent SQL injection
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);

            try (ResultSet rs = pstmt.executeQuery()) {
                boolean userExists = rs.next();
                System.out.println("USER DAO: User found in database? " + userExists); 
                return userExists;
            }
        } catch (SQLException e) {
            System.err.println("Database error during user validation: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}