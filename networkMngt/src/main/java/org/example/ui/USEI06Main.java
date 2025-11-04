package org.example.ui;

import org.example.service.StationService;
import org.example.ui.USEI06Menu;

/**
 * Main entry point for USEI06 - Time-Zone Index and Windowed Queries.
 *
 * Usage:
 *   java USEI06Main [csv_file_path]
 *
 * If no CSV path is provided, defaults to: src/main/resources/stations_europe.csv
 */
public class USEI06Main {
    private static final String DEFAULT_CSV_PATH = "res/test2.csv";

    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════════════════════╗");
        System.out.println("║                   ESINF SPRINT 2 - USEI06                     ║");
        System.out.println("║        Time-Zone Index and Windowed Queries System            ║");
        System.out.println("║                     sem3-pi-25-26                             ║");
        System.out.println("╚════════════════════════════════════════════════════════════════╝\n");

        // Get CSV path from command line or use default
        String csvPath = getCsvPath(args);
        System.out.println("CSV Data Source: " + csvPath + "\n");

        // Initialize service
        StationService service = new StationService();
        boolean initialized = service.initialize(csvPath);

        if (!initialized) {
            System.err.println("\n❌ FATAL ERROR: Failed to initialize service.");
            System.err.println("Please check:");
            System.err.println("  1. CSV file exists at: " + csvPath);
            System.err.println("  2. CSV file format is correct");
            System.err.println("  3. CSV file contains valid data");
            System.exit(1);
        }

        // Start interactive menu
        System.out.println("\n✓ Service initialized successfully!");
        System.out.println("Starting interactive menu...\n");

        USEI06Menu menu = new USEI06Menu(service);
        menu.start();

        System.out.println("\nApplication terminated successfully.");
    }

    /**
     * Get CSV path from command line arguments or use default.
     */
    private static String getCsvPath(String[] args) {
        if (args.length > 0) {
            return args[0];
        }

        // Check if default path exists
        java.io.File defaultFile = new java.io.File(DEFAULT_CSV_PATH);
        if (!defaultFile.exists()) {
            System.out.println("⚠️  Warning: Default CSV not found at: " + DEFAULT_CSV_PATH);
            System.out.println("You can specify a custom path as command line argument.");
        }

        return DEFAULT_CSV_PATH;
    }

    /**
     * Quick demo mode - runs all sample queries without interactive menu.
     * Useful for automated testing and demonstrations.
     *
     * Usage: java USEI06Main --demo [csv_path]
     */
    public static void runDemoMode(String csvPath) {
        System.out.println("╔════════════════════════════════════════════════════════════════╗");
        System.out.println("║                      DEMO MODE                                ║");
        System.out.println("║            Running All Sample Queries                         ║");
        System.out.println("╚════════════════════════════════════════════════════════════════╝\n");

        StationService service = new StationService();
        boolean initialized = service.initialize(csvPath);

        if (!initialized) {
            System.err.println("Failed to initialize service.");
            System.exit(1);
        }

        // Run all sample queries
        String report = service.runAllSampleQueries();
        System.out.println(report);

        System.out.println("\nDemo completed successfully!");
    }
}