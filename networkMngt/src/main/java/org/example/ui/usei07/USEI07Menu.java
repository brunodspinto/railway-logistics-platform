package org.example.ui.usei07;

import org.example.queries.QueryResult;
import org.example.service.StationService;

import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * UI (Menu) para as funcionalidades que usam a 2D-Tree (USEI08, 09, 10).
 */
public class USEI07Menu {
    private final StationService service;
    private final Scanner scanner;
    private boolean running;

    public USEI07Menu(StationService service) {
        this.service = service;
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        if (!service.isReady()) {
            System.err.println("Service not ready");
            return;
        }

        running = true;
        // Mostra o total de estações carregadas
        System.out.printf("\nStations indexed: %d\n", service.getIndexes().getTotalStations());

        while (running) {
            showMainMenu();
            handleMainMenu();
        }
    }

    private void showMainMenu() {
        System.out.println("\n[SPATIAL QUERIES (2D-Tree) MENU]");
        System.out.println("1. Search by Geographical Area (USEI08)");
        System.out.println("2. Proximity Search (Nearest-N) (USEI09)");
        System.out.println("3. Radius Search (USEI10)");
        System.out.println("4. Show 2D-Tree Build Stats (USEI07)");
        System.out.println("0. Exit");
        System.out.print("\nOption: ");
    }

    private void handleMainMenu() {
        try {
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1: queryGeographicalArea(); break;
                case 2: queryProximitySearch(); break;
                case 3: queryRadiusSearch(); break;
                case 4: showBuildStats(); break;
                case 0: running = false; break;
                default: System.out.println("Invalid option");
            }
        } catch (InputMismatchException e) {
            System.out.println("Invalid input");
            scanner.nextLine();
        }
    }

    // --- Placeholder para USEI08 ---
    private void queryGeographicalArea() {
        System.out.println("\n[USEI08: Search by Geographical Area]");
        try {
            System.out.print("Min latitude: ");
            double minLat = scanner.nextDouble();
            System.out.print("Max latitude: ");
            double maxLat = scanner.nextDouble();
            System.out.print("Min longitude: ");
            double minLon = scanner.nextDouble();
            System.out.print("Max longitude: ");
            double maxLon = scanner.nextDouble();
            scanner.nextLine();

            // TODO: Chamar o método de serviço da USEI08
            // Ex: show(service.queryByBoundingBox2D(minLat, maxLat, minLon, maxLon));
            System.out.println("\n// TODO: Implementar USEI08 (query de área na 2D-Tree)");

        } catch (InputMismatchException e) {
            System.out.println("Invalid numeric input");
            scanner.nextLine();
        }
        pause();
    }

    // --- Placeholder para USEI09 ---
    private void queryProximitySearch() {
        System.out.println("\n[USEI09: Proximity Search (Nearest-N)]");
        try {
            System.out.print("Target latitude: ");
            double lat = scanner.nextDouble();
            System.out.print("Target longitude: ");
            double lon = scanner.nextDouble();
            System.out.print("Number of stations (N): ");
            int n = scanner.nextInt();
            scanner.nextLine();

            // TODO: Chamar o método de serviço da USEI09
            // Ex: show(service.queryNearestN(lat, lon, n));
            System.out.println("\n// TODO: Implementar USEI09 (query 'nearest N' na 2D-Tree)");

        } catch (InputMismatchException e) {
            System.out.println("Invalid numeric input");
            scanner.nextLine();
        }
        pause();
    }

    // --- Placeholder para USEI10 ---
    private void queryRadiusSearch() {
        System.out.println("\n[USEI10: Radius Search]");
        try {
            System.out.print("Target latitude: ");
            double lat = scanner.nextDouble();
            System.out.print("Target longitude: ");
            double lon = scanner.nextDouble();
            System.out.print("Radius (km): ");
            double radius = scanner.nextDouble();
            scanner.nextLine();

            // TODO: Chamar o método de serviço da USEI10
            // Ex: show(service.queryByRadius(lat, lon, radius));
            System.out.println("\n// TODO: Implementar USEI10 (query 'raio' na 2D-Tree)");

        } catch (InputMismatchException e) {
            System.out.println("Invalid numeric input");
            scanner.nextLine();
        }
        pause();
    }

    // --- USEI07 Stats ---
    private void showBuildStats() {
        System.out.println("\n[USEI07: Index Build Report]");
        // O getPerformanceReport() já contém as estatísticas da 2D-Tree
        System.out.println(service.getPerformanceReport());
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