package org.example.demo;

import org.example.controller.UpgradePlanController;
import org.example.controller.UpgradePlanResult;
import org.example.domain.Station;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Demonstracao da USEI11 - Directed Line Upgrade Plan
 */
public class USEI11Demo {

    public static void main(String[] args) {
        try {
            UpgradePlanController controller = new UpgradePlanController();

            // Caminho relativo ao modulo (funciona tanto standalone como no menu)
            String stationsPath = "res/stations.csv";
            String linesPath = "res/lines.csv";
            controller.loadNetwork(stationsPath, linesPath);

            System.out.println("\n" + "=".repeat(70));
            System.out.println("USEI11 - DIRECTED LINE UPGRADE PLAN");
            System.out.println("=".repeat(70));

            UpgradePlanResult result = controller.calculateUpgradeOrder();

            System.out.println("\n" + "=".repeat(70));
            System.out.println("RESULTS");
            System.out.println("=".repeat(70));

            System.out.printf("Network size: %d stations, %d connections%n",
                    result.getNumStations(),
                    result.getNumConnections());

            if (result.hasCycles()) {
                displayCycleResults(result);
            } else {
                displayUpgradeOrder(result);
            }

            System.out.println("\n" + "=".repeat(70));
            System.out.printf("Execution time: %d ms%n", result.getExecutionTimeMs());
            System.out.printf("Complexity: %s%n", result.getComplexity());
            System.out.println("=".repeat(70));

        } catch (IOException e) {
            System.err.println("Error loading network: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void displayCycleResults(UpgradePlanResult result) {
        Set<Station> stationsInCycles = result.getStationsInCycles();

        System.out.println("\nGRAPH HAS CYCLES - Cannot determine upgrade order!");
        System.out.println("\nStations involved in cycles: " + stationsInCycles.size());

        List<Station> sortedStations = new ArrayList<>(stationsInCycles);
        sortedStations.sort((s1, s2) -> s1.getName().compareTo(s2.getName()));

        System.out.println("\nFirst 20 stations in cycles:");
        System.out.println("-".repeat(70));

        int display = Math.min(20, sortedStations.size());
        for (int i = 0; i < display; i++) {
            Station station = sortedStations.get(i);
            System.out.printf("  %3d. [%-10s] %s%n",
                    i + 1,
                    station.getId(),
                    station.getName());
        }

        if (sortedStations.size() > display) {
            System.out.println("\n  ... and " + (sortedStations.size() - display) +
                    " more stations");
        }
    }

    private static void displayUpgradeOrder(UpgradePlanResult result) {
        System.out.println("\nVALID UPGRADE ORDER FOUND!");
        System.out.println("\nUpgrade sequence (first 20 stations):");
        System.out.println("-".repeat(70));

        List<Station> order = result.getUpgradeOrder();
        int showLimit = Math.min(20, order.size());

        for (int i = 0; i < showLimit; i++) {
            Station station = order.get(i);
            System.out.printf("  %3d. [%-10s] %s%n",
                    i + 1,
                    station.getId(),
                    station.getName());
        }

        if (order.size() > showLimit) {
            System.out.printf("\n  ... and %d more stations%n",
                    order.size() - showLimit);
        }

        System.out.println("\nStations should be upgraded in this order to respect");
        System.out.println("all directional dependencies.");
    }
}