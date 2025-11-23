package org.example.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static Connection connection = null;

    public static Connection getConnection() throws SQLException {
        if (!DatabaseConfig.isConfigured()) {
            throw new SQLException("Database not configured. Create database.properties file.");
        }

        if (connection == null || connection.isClosed()) {
            try {
                Class.forName("oracle.jdbc.driver.OracleDriver");

                String url = String.format(
                        "jdbc:oracle:thin:@%s:%s/%s",
                        DatabaseConfig.getHostname(),
                        DatabaseConfig.getPort(),
                        DatabaseConfig.getServiceName()
                );

                connection = DriverManager.getConnection(
                        url,
                        DatabaseConfig.getUsername(),
                        DatabaseConfig.getPassword()
                );

            } catch (ClassNotFoundException e) {
                throw new SQLException("Oracle JDBC Driver not found!", e);
            }
        }
        return connection;
    }

    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("✗ Database connection failed: " + e.getMessage());
            return false;
        }
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("✓ Database connection closed");
            }
        } catch (SQLException e) {
            System.err.println("✗ Error closing connection: " + e.getMessage());
        }
    }
}