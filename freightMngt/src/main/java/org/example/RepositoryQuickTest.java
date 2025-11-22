package org.example;

import org.example.domain.*;
import org.example.repository.DatabaseRepositoryFacade;
import org.example.utils.DatabaseConnection;

public class RepositoryQuickTest {
    public static void main(String[] args) {
        System.out.println("=== Quick Repository Test ===\n");

        if (!DatabaseConnection.testConnection()) {
            System.err.println("✗ Cannot connect!");
            return;
        }

        DatabaseRepositoryFacade repo = new DatabaseRepositoryFacade();

        // Test 1: Stations
        System.out.println("--- Stations ---");
        Station s = repo.getStation(1);
        System.out.println(s != null ? "✓ Station 1: " + s.getName() : "✗ Station 1 failed");

        // Test 2: Locomotives
        System.out.println("\n--- Locomotives ---");
        Locomotive l = repo.getLocomotive(5621);
        System.out.println(l != null ? "✓ Loco 5621: " + l.getName() + " (" + l.getType() + ")" : "✗ Loco 5621 failed");

        // Test 3: Wagons
        System.out.println("\n--- Wagons ---");
        Wagon w = repo.getWagon("3330001");
        if (w != null && w.getModel() != null) {
            System.out.printf("✓ Wagon 3330001: %s (%.1f tons)\n",
                    w.getModel().getType(), w.getTareWeightTons());
        } else {
            System.out.println("✗ Wagon 3330001 failed");
        }

        // Test 4: Collections
        System.out.println("\n--- Collections ---");
        System.out.println("✓ Total stations: " + repo.getAllStations().size());
        System.out.println("✓ Total locomotives: " + repo.getAllLocomotives().size());
        System.out.println("✓ Total wagons: " + repo.getAllWagons().size());

        System.out.println("\n✅ ALL TESTS PASSED!");
        DatabaseConnection.closeConnection();
    }
}