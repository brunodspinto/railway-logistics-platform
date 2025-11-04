package org.example.ui;

import org.example.queries.QueryResult;
import org.example.service.StationService;

import java.util.*;

/**
 * Text-based user interface for USEI06 functionality.
 * Provides interactive menu for executing queries and viewing results.
 */
public class USEI06Menu {
    private final StationService service;
    private final Scanner scanner;
    private boolean running;

    public USEI06Menu(StationService service) {
        this.service = service;
        this.scanner = new Scanner(System.in);
        this.running = false;
    }

    /**
     * Start the interactive menu.
     */
    public void start() {
        if (!service.isReady()) {
            System.err.println("ERROR: Service not initialized. Please load data first.");
            return;
        }

        running = true;
        displayWelcome();

        while (running) {
            displayMainMenu();
            handleMainMenuChoice();
        }

        System.out.println("\nGoodbye! 👋\n");
    }

    private void displayWelcome() {
        System.out.println("\n╔════════════════════════════════════════════════════════════════╗");
        System.out.println("║                    USEI06 - QUERY INTERFACE                   ║");
        System.out.println("║          Time-Zone Index and Windowed Queries                 ║");
        System.out.println("╚════════════════════════════════════════════════════════════════╝\n");
        System.out.println(service.getSystemStatus());
    }

    private void displayMainMenu() {
        System.out.println("\n┌────────────────────────────────────────────────────────────────┐");
        System.out.println("│                        MAIN MENU                              │");
        System.out.println("├────────────────────────────────────────────────────────────────┤");
        System.out.println("│  1. Time Zone Queries                                         │");
        System.out.println("│  2. Coordinate Queries                                        │");
        System.out.println("│  3. Run Sample Queries (Required for USEI06)                  │");
        System.out.println("│  4. Performance & Statistics                                  │");
        System.out.println("│  5. System Information                                        │");
        System.out.println("│  0. Exit                                                      │");
        System.out.println("└────────────────────────────────────────────────────────────────┘");
        System.out.print("Enter your choice: ");
    }

    private void handleMainMenuChoice() {
        try {
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    timeZoneMenu();
                    break;
                case 2:
                    coordinateMenu();
                    break;
                case 3:
                    sampleQueriesMenu();
                    break;
                case 4:
                    performanceMenu();
                    break;
                case 5:
                    systemInfoMenu();
                    break;
                case 0:
                    running = false;
                    break;
                default:
                    System.out.println("❌ Invalid choice. Please try again.");
            }
        } catch (InputMismatchException e) {
            System.out.println("❌ Invalid input. Please enter a number.");
            scanner.nextLine(); // Clear invalid input
        }
    }

    // ========== TIME ZONE MENU ==========

    private void timeZoneMenu() {
        System.out.println("\n┌────────────────────────────────────────────────────────────────┐");
        System.out.println("│                    TIME ZONE QUERIES                          │");
        System.out.println("├────────────────────────────────────────────────────────────────┤");
        System.out.println("│  1. Query by Time Zone Group                                  │");
        System.out.println("│  2. Query by Time Zone Group and Country                      │");
        System.out.println("│  0. Back to Main Menu                                         │");
        System.out.println("└────────────────────────────────────────────────────────────────┘");
        System.out.print("Enter your choice: ");

        try {
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    queryByTimeZoneGroup();
                    break;
                case 2:
                    queryByTimeZoneGroupAndCountry();
                    break;
                case 0:
                    return;
                default:
                    System.out.println("❌ Invalid choice.");
            }
        } catch (InputMismatchException e) {
            System.out.println("❌ Invalid input.");
            scanner.nextLine();
        }

        pauseForUser();
    }

    private void queryByTimeZoneGroup() {
        System.out.print("\nEnter time zone group (e.g., CET, WET, EET): ");
        String tzGroup = scanner.nextLine().trim();

        if (tzGroup.isEmpty()) {
            System.out.println("❌ Time zone group cannot be empty.");
            return;
        }

        QueryResult result = service.queryByTimeZoneGroup(tzGroup);
        displayQueryResult(result);
    }

    private void queryByTimeZoneGroupAndCountry() {
        System.out.print("\nEnter time zone group (e.g., WET): ");
        String tzGroup = scanner.nextLine().trim();

        System.out.print("Enter country code (e.g., PT, ES, FR): ");
        String country = scanner.nextLine().trim();

        if (tzGroup.isEmpty() || country.isEmpty()) {
            System.out.println("❌ Both fields are required.");
            return;
        }

        QueryResult result = service.queryByTimeZoneGroupAndCountry(tzGroup, country);
        displayQueryResult(result);
    }

    private void queryByTimeZoneWindow() {
        System.out.print("\nEnter time zone groups (comma-separated, e.g., CET,EET,WET): ");
        String input = scanner.nextLine().trim();

        if (input.isEmpty()) {
            System.out.println("❌ Input cannot be empty.");
            return;
        }

        List<String> tzGroups = Arrays.asList(input.split(","));
        for (int i = 0; i < tzGroups.size(); i++) {
            tzGroups.set(i, tzGroups.get(i).trim());
        }

        QueryResult result = service.queryByTimeZoneWindow(tzGroups);
        displayQueryResult(result);
    }

    private void viewCountryDistribution() {
        System.out.print("\nEnter time zone group: ");
        String tzGroup = scanner.nextLine().trim();

        if (tzGroup.isEmpty()) {
            System.out.println("❌ Time zone group cannot be empty.");
            return;
        }

        Map<String, Long> distribution = service.getCountryDistribution(tzGroup);

        System.out.println("\n=== Country Distribution for " + tzGroup + " ===");
        distribution.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .forEach(e -> System.out.printf("  %s: %d stations\n", e.getKey(), e.getValue()));
    }

    private void viewTimeZoneDistribution() {
        Map<String, Long> distribution = service.getTimeZoneDistribution();

        System.out.println("\n=== Time Zone Distribution (All Stations) ===");
        distribution.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .forEach(e -> System.out.printf("  %s: %d stations\n", e.getKey(), e.getValue()));
    }

    // ========== COORDINATE MENU ==========

    private void coordinateMenu() {
        System.out.println("\n┌────────────────────────────────────────────────────────────────┐");
        System.out.println("│                   COORDINATE QUERIES                          │");
        System.out.println("├────────────────────────────────────────────────────────────────┤");
        System.out.println("│  1. Query by Latitude Range                                   │");
        System.out.println("│  2. Query by Longitude Range                                  │");
        System.out.println("│  3. Query by Exact Coordinates                                │");
        System.out.println("│  0. Back to Main Menu                                         │");
        System.out.println("└────────────────────────────────────────────────────────────────┘");
        System.out.print("Enter your choice: ");

        try {
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    queryByLatitudeRange();
                    break;
                case 2:
                    queryByLongitudeRange();
                    break;
                case 3:
                    queryByExactCoordinates();
                    break;
                case 0:
                    return;
                default:
                    System.out.println("❌ Invalid choice.");
            }
        } catch (InputMismatchException e) {
            System.out.println("❌ Invalid input.");
            scanner.nextLine();
        }

        pauseForUser();
    }

    private void queryByLatitudeRange() {
        System.out.print("\nEnter minimum latitude [-90, 90]: ");
        double minLat = scanner.nextDouble();
        System.out.print("Enter maximum latitude [-90, 90]: ");
        double maxLat = scanner.nextDouble();
        scanner.nextLine();

        try {
            QueryResult result = service.queryByLatitudeRange(minLat, maxLat);
            displayQueryResult(result);
        } catch (IllegalArgumentException e) {
            System.out.println("❌ " + e.getMessage());
        }
    }

    private void queryByLongitudeRange() {
        System.out.print("\nEnter minimum longitude [-180, 180]: ");
        double minLon = scanner.nextDouble();
        System.out.print("Enter maximum longitude [-180, 180]: ");
        double maxLon = scanner.nextDouble();
        scanner.nextLine();

        try {
            QueryResult result = service.queryByLongitudeRange(minLon, maxLon);
            displayQueryResult(result);
        } catch (IllegalArgumentException e) {
            System.out.println("❌ " + e.getMessage());
        }
    }

    private void queryByBoundingBox() {
        System.out.println("\nEnter bounding box coordinates:");
        System.out.print("  Min latitude: ");
        double minLat = scanner.nextDouble();
        System.out.print("  Max latitude: ");
        double maxLat = scanner.nextDouble();
        System.out.print("  Min longitude: ");
        double minLon = scanner.nextDouble();
        System.out.print("  Max longitude: ");
        double maxLon = scanner.nextDouble();
        scanner.nextLine();

        try {
            QueryResult result = service.queryByBoundingBox(minLat, maxLat, minLon, maxLon);
            displayQueryResult(result);
        } catch (IllegalArgumentException e) {
            System.out.println("❌ " + e.getMessage());
        }
    }

    private void queryByExactCoordinates() {
        System.out.print("\nEnter latitude: ");
        double lat = scanner.nextDouble();
        System.out.print("Enter longitude: ");
        double lon = scanner.nextDouble();
        scanner.nextLine();

        QueryResult result = service.queryByExactCoordinates(lat, lon);
        displayQueryResult(result);
    }

    private void queryByBoundingBoxAndCountry() {
        System.out.println("\nEnter bounding box coordinates:");
        System.out.print("  Min latitude: ");
        double minLat = scanner.nextDouble();
        System.out.print("  Max latitude: ");
        double maxLat = scanner.nextDouble();
        System.out.print("  Min longitude: ");
        double minLon = scanner.nextDouble();
        System.out.print("  Max longitude: ");
        double maxLon = scanner.nextDouble();
        scanner.nextLine();

        System.out.print("Enter country code: ");
        String country = scanner.nextLine().trim();

        try {
            QueryResult result = service.queryByBoundingBoxAndCountry(
                    minLat, maxLat, minLon, maxLon, country);
            displayQueryResult(result);
        } catch (IllegalArgumentException e) {
            System.out.println("❌ " + e.getMessage());
        }
    }

    private void viewGeographicDistribution() {
        System.out.println("\n=== Geographic Distribution Summary ===\n");

        System.out.println("Latitude Statistics:");
        Map<String, Object> latStats = service.getLatitudeDistributionSummary();
        latStats.forEach((k, v) -> System.out.printf("  %s: %s\n", k, v));

        System.out.println("\nLongitude Statistics:");
        Map<String, Object> lonStats = service.getLongitudeDistributionSummary();
        lonStats.forEach((k, v) -> System.out.printf("  %s: %s\n", k, v));
    }

    // ========== SAMPLE QUERIES MENU ==========

    private void sampleQueriesMenu() {
        System.out.println("\n┌────────────────────────────────────────────────────────────────┐");
        System.out.println("│              SAMPLE QUERIES (USEI06 Deliverable)              │");
        System.out.println("├────────────────────────────────────────────────────────────────┤");
        System.out.println("│  1. Sample 1: All CET Stations                                │");
        System.out.println("│  2. Sample 2: Portuguese WET Stations                         │");
        System.out.println("│  3. Sample 3: Time Zone Window (CET + EET)                    │");
        System.out.println("│  4. Sample 4: Iberian Peninsula (Geographic)                  │");
        System.out.println("│  5. Sample 5: Lisboa Duplicate Coordinates                    │");
        System.out.println("│  6. Run ALL Sample Queries (Comprehensive Report)             │");
        System.out.println("│  0. Back to Main Menu                                         │");
        System.out.println("└────────────────────────────────────────────────────────────────┘");
        System.out.print("Enter your choice: ");

        try {
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    service.runSampleQuery1();
                    break;
                case 2:
                    service.runSampleQuery2();
                    break;
                case 3:
                    service.runSampleQuery3();
                    break;
                case 4:
                    service.runSampleQuery4();
                    break;
                case 5:
                    service.runSampleQuery5();
                    break;
                case 6:
                    System.out.println(service.runAllSampleQueries());
                    break;
                case 0:
                    return;
                default:
                    System.out.println("❌ Invalid choice.");
            }
        } catch (InputMismatchException e) {
            System.out.println("❌ Invalid input.");
            scanner.nextLine();
        }

        pauseForUser();
    }

    // ========== PERFORMANCE MENU ==========

    private void performanceMenu() {
        System.out.println("\n┌────────────────────────────────────────────────────────────────┐");
        System.out.println("│              PERFORMANCE & STATISTICS                         │");
        System.out.println("├────────────────────────────────────────────────────────────────┤");
        System.out.println("│  1. View Index Performance Report                             │");
        System.out.println("│  2. View CSV Import Report                                    │");
        System.out.println("│  0. Back to Main Menu                                         │");
        System.out.println("└────────────────────────────────────────────────────────────────┘");
        System.out.print("Enter your choice: ");

        try {
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    System.out.println("\n" + service.getPerformanceReport());
                    break;
                case 2:
                    System.out.println("\n" + service.getImportReport());
                    break;
                case 0:
                    return;
                default:
                    System.out.println("❌ Invalid choice.");
            }
        } catch (InputMismatchException e) {
            System.out.println("❌ Invalid input.");
            scanner.nextLine();
        }

        pauseForUser();
    }

    // ========== SYSTEM INFO MENU ==========

    private void systemInfoMenu() {
        System.out.println("\n" + service.getSystemStatus());
        pauseForUser();
    }

    // ========== UTILITY METHODS ==========

    private void displayQueryResult(QueryResult result) {
        System.out.println("\n" + result.toReport());
        System.out.println(result.getComplexityAnalysis());
    }

    private void pauseForUser() {
        System.out.print("\nPress ENTER to continue...");
        scanner.nextLine();
    }

    /**
     * Main entry point for standalone execution.
     */
    public static void main(String[] args) {
        StationService service = new StationService();

        // Default CSV path - can be changed via command line
        String csvPath = args.length > 0 ? args[0] : "src/main/resources/stations_europe.csv";

        boolean initialized = service.initialize(csvPath);

        if (initialized) {
            USEI06Menu menu = new USEI06Menu(service);
            menu.start();
        } else {
            System.err.println("Failed to initialize service. Exiting.");
            System.exit(1);
        }
    }
}
