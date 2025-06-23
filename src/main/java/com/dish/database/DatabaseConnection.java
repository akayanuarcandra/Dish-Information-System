package com.dish.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/dish-information-system";
    private static final String USER = "root";
    private static final String PASSWORD = "mysql";

    private static Connection connection = null;

    private DatabaseConnection() {}
    
    // This method returns a singleton connection to the database.
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
            } catch (ClassNotFoundException e) {
                System.err.println("MySQL JDBC Driver not found!");
                e.printStackTrace();
                throw new SQLException("MySQL Driver not found", e);
            }
        }
        return connection;
    }
    
    // This method closes the database connection if it is open.
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.err.println("Error closing the database connection.");
                e.printStackTrace();
            }
        }
    }
}
