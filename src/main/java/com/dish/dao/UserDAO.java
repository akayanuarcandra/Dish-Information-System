package com.dish.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.dish.database.DatabaseConnection;

public class UserDAO {
    public boolean validateUser(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";

        System.out.println("USER DAO: Executing query for user='" + username + "'");

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);

            try (ResultSet rs = pstmt.executeQuery()) {
                // return rs.next(); // If a row is returned, the user exists and credentials are correct.
                boolean userExists = rs.next();
                System.out.println("USER DAO: User found in database? " + userExists); // <-- ADD THIS LINE
                return userExists;
            }
        } catch (SQLException e) {
            System.err.println("Database error during user validation: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}