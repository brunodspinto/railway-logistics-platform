package org.example.usei14.ui;

import org.example.domain.Station;
import org.example.controller.ComputeMaxFlowController;

import java.util.List;
import java.util.Scanner;

public class USEI14Menu {

    // Hardcoded paths for automation
    private static final String DEFAULT_STATIONS_CSV = "res/stations.csv";
    private static final String DEFAULT_LINES_CSV    = "res/lines.csv";

    private final ComputeMaxFlowController controller;
    private final Scanner in;

    public USEI14Menu(ComputeMaxFlowController controller) {
        this.controller = controller;
        this.in = new Scanner(System.in);
    }

    public void start() {
        try {
            // --- AUTOMATION: Load immediately without asking ---
            System.out.println("Loading railway network...");
            controller.loadNetwork(DEFAULT_STATIONS_CSV, DEFAULT_LINES_CSV);
            System.out.println("Data loaded successfully!");

            // Main Menu Loop
            while (true) {
                System.out.println("\n========================================");
                System.out.println("       MAIN MENU - MAXIMUM FLOW         ");
                System.out.println("========================================");
                System.out.println("1. Select Stations and Calculate Flow");
                System.out.println("0. Exit");
                System.out.println("========================================");
                System.out.print("Option: ");

                String option = in.nextLine().trim();

                if (option.equals("0")) {
                    break;
                } else if (option.equals("1")) {
                    processFlowCalculation();
                } else {
                    System.out.println("Invalid option.");
                }
            }

        } catch (Exception e) {
            System.err.println("Fatal error starting application: " + e.getMessage());
            System.err.println("Please check if files exist in the 'res/' folder.");
        }
    }

    private void processFlowCalculation() {
        // 1. Get List
        List<Station> stations = controller.getStations();
        if (stations.isEmpty()) {
            System.out.println("Error: No stations loaded.");
            return;
        }

        // 2. Show list with numbers
        printStationsNumerically(stations);

        // 3. Pick Source
        System.out.println("\n--- SOURCE Selection ---");
        Station source = pickStationByNumber(stations);
        if (source == null) return; // User cancelled

        // 4. Pick Destination
        System.out.println("\n--- DESTINATION Selection ---");
        Station sink = pickStationByNumber(stations);
        if (sink == null) return;

        // Validation
        if (source.equals(sink)) {
            System.out.println("Error: Source and Destination are the same station.");
            return;
        }

        // 5. Calculate
        try {
            System.out.printf("\nCalculating flow from [%s] to [%s]...%n", source.getName(), sink.getName());
            Double maxFlow = controller.calculateMaxFlow(source, sink);

            System.out.println("****************************************");
            System.out.printf("maxflow.summary: Source: %s, Target: %s, MaxFlow: %.0f%n", source.getId(),
                    sink.getId(), maxFlow);
            System.out.println("****************************************");

            System.out.println("\nPress Enter to return to menu...");
            in.nextLine();

        } catch (Exception e) {
            System.err.println("Calculation error: " + e.getMessage());
        }
    }

    private void printStationsNumerically(List<Station> stations) {
        System.out.println("\n--- Station List ---");
        int count = 0;
        for (int i = 0; i < stations.size(); i++) {
            // Format: [1] Name (ID)
            System.out.printf("[%3d] %-25s (ID: %s)  ", i + 1, stations.get(i).getName(), stations.get(i).getId());

            count++;
            // New line every 2 columns for better readability
            if (count % 2 == 0) {
                System.out.println();
            }
        }
        if (count % 2 != 0) System.out.println();
        System.out.println("-------------------------");
    }

    private Station pickStationByNumber(List<Station> stations) {
        while (true) {
            System.out.print("Enter station NUMBER (or '0' to cancel): ");
            String input = in.nextLine().trim();

            try {
                int index = Integer.parseInt(input);

                if (index == 0) return null; // Cancel

                if (index > 0 && index <= stations.size()) {
                    return stations.get(index - 1);
                } else {
                    System.out.println("Invalid number. Choose between 1 and " + stations.size());
                }

            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter numbers only.");
            }
        }
    }
}