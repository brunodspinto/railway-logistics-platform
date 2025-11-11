package org.example.ui.usei06;

import org.example.queries.QueryResult;
import org.example.service.StationService;
import java.util.*;

public class USEI06Menu {
    private final StationService service;
    private final Scanner scanner;
    private boolean running;

    public USEI06Menu(StationService service) {
        this.service = service;
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        if (!service.isReady()) {
            System.err.println("Service not ready");
            return;
        }

        running = true;
        System.out.printf("\nStations indexed: %d\n", service.getIndexes().getTotalStations());

        while (running) {
            showMainMenu();
            handleMainMenu();
        }
    }

    private void showMainMenu() {
        System.out.println("\n[MAIN MENU]");
        System.out.println("1. Time Zone Queries");
        System.out.println("2. Coordinate Queries");
        System.out.println("3. Sample Queries");
        System.out.println("0. Exit");
        System.out.print("\nOption: ");
    }

    private void handleMainMenu() {
        try {
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1: timeZoneMenu(); break;
                case 2: coordMenu(); break;
                case 3: sampleMenu(); break;
                case 0: running = false; break;
                default: System.out.println("Invalid option");
            }
        } catch (InputMismatchException e) {
            System.out.println("Invalid input");
            scanner.nextLine();
        }
    }

    private void timeZoneMenu() {
        System.out.println("\n[TIME ZONE]");
        System.out.println("1. By Time Zone Group");
        System.out.println("2. By Time Zone + Country");
        System.out.println("0. Back");
        System.out.print("\nOption: ");

        try {
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1: queryTZ(); break;
                case 2: queryTZCountry(); break;
                case 0: return;
            }
        } catch (InputMismatchException e) {
            System.out.println("Invalid input");
            scanner.nextLine();
        }
        pause();
    }

    private void queryTZ() {
        System.out.print("\nTime zone (CET/WET/GMT/EET): ");
        String tz = scanner.nextLine().trim();
        if (tz.isEmpty()) {
            System.out.println("Cannot be empty");
            return;
        }
        show(service.queryByTimeZoneGroup(tz));
    }

    private void queryTZCountry() {
        System.out.print("\nTime zone: ");
        String tz = scanner.nextLine().trim();
        System.out.print("Country (PT/ES/FR): ");
        String country = scanner.nextLine().trim();

        if (tz.isEmpty() || country.isEmpty()) {
            System.out.println("Both required");
            return;
        }
        show(service.queryByTimeZoneGroupAndCountry(tz, country));
    }

    private void coordMenu() {
        System.out.println("\n[COORDINATES]");
        System.out.println("1. Latitude Range");
        System.out.println("2. Longitude Range");
        System.out.println("3. Exact Coordinates");
        System.out.println("0. Back");
        System.out.print("\nOption: ");

        try {
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1: queryLat(); break;
                case 2: queryLon(); break;
                case 3: queryExact(); break;
                case 0: return;
            }
        } catch (InputMismatchException e) {
            System.out.println("Invalid input");
            scanner.nextLine();
        }
        pause();
    }

    private void queryLat() {
        System.out.print("\nMin latitude: ");
        double min = scanner.nextDouble();
        System.out.print("Max latitude: ");
        double max = scanner.nextDouble();
        scanner.nextLine();

        try {
            show(service.queryByLatitudeRange(min, max));
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    private void queryLon() {
        System.out.print("\nMin longitude: ");
        double min = scanner.nextDouble();
        System.out.print("Max longitude: ");
        double max = scanner.nextDouble();
        scanner.nextLine();

        try {
            show(service.queryByLongitudeRange(min, max));
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    private void queryExact() {
        System.out.print("\nLatitude: ");
        double lat = scanner.nextDouble();
        System.out.print("Longitude: ");
        double lon = scanner.nextDouble();
        scanner.nextLine();
        show(service.queryByExactCoordinates(lat, lon));
    }

    private void sampleMenu() {
        System.out.println("\n[SAMPLE QUERIES]");
        System.out.println("1. All CET Stations");
        System.out.println("2. Portuguese WET/GMT");
        System.out.println("3. Time Zone Window (CET+EET)");
        System.out.println("4. Lisboa Duplicates");
        System.out.println("5. Run All");
        System.out.println("0. Back");
        System.out.print("\nOption: ");

        try {
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1: service.runSampleQuery1(); break;
                case 2: service.runSampleQuery2(); break;
                case 3: service.runSampleQuery3(); break;
                case 4: service.runSampleQuery4(); break;
                case 5: System.out.println(service.runAllSampleQueries()); break;
                case 0: return;
            }
        } catch (InputMismatchException e) {
            System.out.println("Invalid input");
            scanner.nextLine();
        }
        pause();
    }


    private void show(QueryResult r) {
        System.out.println("\n" + r.toString());
    }

    private void pause() {
        System.out.print("\nPress ENTER...");
        scanner.nextLine();
    }
}