package org.example.ui;

import org.example.domain.*;
import org.example.repository.*;
import org.example.service.TravelTimeCalculator;
import org.example.service.TravelTimeResult;

import java.util.*;
import java.util.stream.Collectors;

public class TravelTimeUI {

    private final IRouteRepository repository;
    private final TravelTimeCalculator calculator;
    private final Scanner scanner;

    public TravelTimeUI(IRouteRepository repository) {
        this.repository = repository;
        this.calculator = new TravelTimeCalculator(repository);
        this.scanner = new Scanner(System.in);
    }

    public static void main(String[] args) {
        try {
            System.out.println(" Loading railway data...\n");

            IRouteRepository repository = new CsvRouteRepository("freightMngt/data");
            TravelTimeUI ui = new TravelTimeUI(repository);

            ui.run();

        } catch (Exception e) {
            System.err.println(" Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void run() {
        boolean running = true;

        while (running) {
            System.out.println("\n" + "=".repeat(60));
            System.out.println("        RAILWAY TRAVEL TIME CALCULATOR ");
            System.out.println("=".repeat(60));
            System.out.println("1. Calculate travel time");
            System.out.println("2. List all available routes");
            System.out.println("3. List all locomotives");
            System.out.println("4. List all stations");
            System.out.println("0. Exit");
            System.out.println("=".repeat(60));
            System.out.print("Choose an option: ");

            try {
                int option = Integer.parseInt(scanner.nextLine().trim());

                switch (option) {
                    case 1 -> calculateWithMenus();
                    case 2 -> listRoutes();
                    case 3 -> listLocomotives();
                    case 4 -> listStations();
                    case 0 -> {
                        System.out.println("\n Goodbye!");
                        running = false;
                    }
                    default -> System.out.println(" Invalid option!");
                }

            } catch (NumberFormatException e) {
                System.out.println(" Please enter a valid number!");
            } catch (Exception e) {
                System.err.println(" Error: " + e.getMessage());
            }
        }

        scanner.close();
    }

    // ==================== OPTION 1: Calculate with Menus ====================

    private void calculateWithMenus() throws Exception {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("            SELECT ROUTE");
        System.out.println("=".repeat(60));

        // Step 1: Select Route
        Collection<Line> lines = repository.getAllLines();
        List<Line> lineList = new ArrayList<>(lines);

        if (lineList.isEmpty()) {
            System.out.println(" No routes available!");
            waitForEnter();
            return;
        }

        System.out.println("\nAvailable Routes:\n");
        for (int i = 0; i < lineList.size(); i++) {
            Line line = lineList.get(i);
            System.out.printf("%d. %s → %s (%.1f km)%n   Line: %s%n%n",
                    i + 1,
                    line.getStartStation().getName(),
                    line.getEndStation().getName(),
                    line.getTotalLengthKm(),
                    line.getName());
        }

        System.out.print("Select route (1-" + lineList.size() + "): ");
        int routeChoice = Integer.parseInt(scanner.nextLine().trim());

        if (routeChoice < 1 || routeChoice > lineList.size()) {
            System.out.println(" Invalid route selection!");
            waitForEnter();
            return;
        }

        Line selectedLine = lineList.get(routeChoice - 1);
        int originId = selectedLine.getStartStation().getId();
        int destId = selectedLine.getEndStation().getId();

        // Step 2: Select Locomotive
        System.out.println("\n" + "=".repeat(60));
        System.out.println("            SELECT LOCOMOTIVE");
        System.out.println("=".repeat(60));

        Collection<Locomotive> locos = repository.getAllLocomotives();
        List<Locomotive> locoList = new ArrayList<>(locos);

        if (locoList.isEmpty()) {
            System.out.println(" No locomotives available!");
            waitForEnter();
            return;
        }

        // Filter compatible locomotives (same gauge)
        List<Locomotive> compatibleLocos = locoList.stream()
                .filter(loco -> loco.isCompatibleWithGauge(selectedLine.getGauge()))
                .collect(Collectors.toList());

        if (compatibleLocos.isEmpty()) {
            System.out.println(" No compatible locomotives for this route!");
            System.out.printf("   Route gauge: %d mm%n", selectedLine.getGauge());
            waitForEnter();
            return;
        }

        System.out.println("\nCompatible Locomotives:\n");
        for (int i = 0; i < compatibleLocos.size(); i++) {
            Locomotive loco = compatibleLocos.get(i);
            String electrified = selectedLine.isFullyElectrified() ? "✅" : " Not electrified";
            String locoType = loco.isElectric() ? " Electric" : " Diesel";

            System.out.printf("%d. %s - '%s' (%s %s)%n   %s | Max Speed: %d km/h | %s%n%n",
                    i + 1,
                    loco.getNumber(),
                    loco.getName(),
                    loco.getMake(),
                    loco.getModel(),
                    locoType,
                    loco.getMaxSpeed(),
                    loco.isElectric() ? electrified : "Compatible");
        }

        System.out.print("Select locomotive (1-" + compatibleLocos.size() + "): ");
        int locoChoice = Integer.parseInt(scanner.nextLine().trim());

        if (locoChoice < 1 || locoChoice > compatibleLocos.size()) {
            System.out.println(" Invalid locomotive selection!");
            waitForEnter();
            return;
        }

        Locomotive selectedLoco = compatibleLocos.get(locoChoice - 1);

        // Calculate and display
        performCalculation(originId, destId, selectedLoco.getNumber());
    }

    // ==================== OPTION 2: List Routes ====================

    private void listRoutes() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("            ALL AVAILABLE ROUTES");
        System.out.println("=".repeat(60) + "\n");

        Collection<Line> lines = repository.getAllLines();

        if (lines.isEmpty()) {
            System.out.println(" No routes available!");
            waitForEnter();
            return;
        }

        for (Line line : lines) {
            System.out.println("━".repeat(60));
            System.out.printf("ID: %d | %s%n", line.getId(), line.getName());
            System.out.printf("Route: %s (ID:%d) → %s (ID:%d)%n",
                    line.getStartStation().getName(), line.getStartStation().getId(),
                    line.getEndStation().getName(), line.getEndStation().getId());
            System.out.printf("Owner: %s | Gauge: %d mm | Distance: %.2f km%n",
                    line.getOwner(), line.getGauge(), line.getTotalLengthKm());
            System.out.printf("Electrified: %s | Min Speed: %d km/h%n",
                    line.isFullyElectrified() ? " Yes" : " No",
                    line.getMinMaxSpeed());

            System.out.println("\nSegments:");
            for (LineSegment seg : line.getSegments()) {
                System.out.printf("  %d. %.1f km | %s | %d tracks | Max: %d km/h%n",
                        seg.getOrder(),
                        seg.getLengthKm(),
                        seg.isElectrified() ? "⚡ Electrified" : "No power",
                        seg.getNumberTracks(),
                        seg.getMaxSpeedKmh());
            }
            System.out.println();
        }

        System.out.println("━".repeat(60));
        waitForEnter();
    }

    // ==================== OPTION 3: List Locomotives ====================

    private void listLocomotives() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("            ALL AVAILABLE LOCOMOTIVES");
        System.out.println("=".repeat(60) + "\n");

        Collection<Locomotive> locos = repository.getAllLocomotives();

        if (locos.isEmpty()) {
            System.out.println(" No locomotives available!");
            waitForEnter();
            return;
        }

        for (Locomotive loco : locos) {
            System.out.println("━".repeat(60));
            System.out.printf("Number: %d | Name: '%s'%n", loco.getNumber(), loco.getName());
            System.out.printf("Manufacturer: %s %s (Year: %d)%n",
                    loco.getMake(), loco.getModel(), loco.getServiceYear());
            System.out.printf("Type: %s | Gauge: %d mm%n",
                    loco.isElectric() ? "⚡ Electric" : "🛢️ Diesel",
                    loco.getGauge());
            System.out.printf("Max Speed: %d km/h | Operational: %d km/h%n",
                    loco.getMaxSpeed(), loco.getOperationalSpeed());
            System.out.printf("Power: %d kW | Weight: %.1f tons%n",
                    loco.getPower(), loco.getWeight());
            System.out.println();
        }

        System.out.println("━".repeat(60));
        waitForEnter();
    }

    // ==================== OPTION 4: List Stations ====================

    private void listStations() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("             ALL STATIONS");
        System.out.println("=".repeat(60) + "\n");

        Collection<Station> stations = repository.getAllStations();

        if (stations.isEmpty()) {
            System.out.println(" No stations available!");
            waitForEnter();
            return;
        }

        // Sort by ID
        List<Station> sortedStations = new ArrayList<>(stations);
        sortedStations.sort(Comparator.comparingInt(Station::getId));

        System.out.printf("%-5s | %-30s%n", "ID", "Name");
        System.out.println("─".repeat(40));

        for (Station station : sortedStations) {
            System.out.printf("%-5d | %-30s%n", station.getId(), station.getName());
        }

        System.out.println("\n" + "─".repeat(40));
        System.out.printf("Total: %d stations%n", stations.size());
        waitForEnter();
    }

    // ==================== Helper: Perform Calculation ====================

    private void performCalculation(int originId, int destId, int locoNumber) {
        try {
            TravelTimeResult result = calculator.calculateDetailed(
                    originId, destId, locoNumber);

            result.printReport();

            waitForEnter();

        } catch (Exception e) {
            System.err.println("\n Calculation Error: " + e.getMessage());
            waitForEnter();
        }
    }

    // ==================== Helper: Wait for Enter ====================

    private void waitForEnter() {
        System.out.print("\n[Press Enter to continue...]");
        scanner.nextLine();
    }
}
