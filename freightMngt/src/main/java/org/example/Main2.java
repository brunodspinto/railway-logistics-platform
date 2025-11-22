package org.example;

import org.example.repository.DatabaseRepositoryFacade;
import org.example.ui.ManualSchedulerUI;
import org.example.utils.DatabaseConnection;

public class Main2 {
    public static void main(String[] args) {
        System.out.println("═".repeat(80));
        System.out.println("  FREIGHT MANAGEMENT SYSTEM - USLP07");
        System.out.println("═".repeat(80));

        // Test connection
        if (!DatabaseConnection.testConnection()) {
            System.err.println("\n✗ Cannot connect to database!");
            System.err.println("Check database.properties configuration");
            return;
        }

        try {
            // Usar DatabaseRepositoryFacade
            DatabaseRepositoryFacade repository = new DatabaseRepositoryFacade();

            // Launch Manual Scheduler UI
            ManualSchedulerUI ui = new ManualSchedulerUI(repository);
            ui.start();

        } catch (Exception e) {
            System.err.println("\n✗ Application error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeConnection();
        }
    }
}
