package org.example.utils;

import org.example.utils.DatabaseConnection;
import java.sql.*;

public class DatabaseTest {
    public static void main(String[] args) {
        System.out.println("=== Database Connection Test ===\n");

        if (!DatabaseConnection.testConnection()) {
            System.err.println("✗ Cannot connect to database!");
            System.err.println("Make sure:");
            System.err.println("  1. Docker Oracle is running");
            System.err.println("  2. database.properties exists");
            return;
        }

        try {
            Connection conn = DatabaseConnection.getConnection();

            // Test simple query
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as total FROM STATION");

            if (rs.next()) {
                int total = rs.getInt("total");
                System.out.println("✓ Stations in database: " + total);
            }

            rs.close();
            stmt.close();

            System.out.println("✓ Test completed successfully");

        } catch (SQLException e) {
            System.err.println("✗ Error: " + e.getMessage());
        } finally {
            DatabaseConnection.closeConnection();
        }
    }
}