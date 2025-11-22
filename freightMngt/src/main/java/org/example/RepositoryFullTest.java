package org.example;

import org.example.domain.*;
import org.example.repository.DatabaseRepositoryFacade;
import org.example.utils.DatabaseConnection;

public class RepositoryFullTest {
    public static void main(String[] args) {
        System.out.println("=== Full Repository Test ===\n");

        if (!DatabaseConnection.testConnection()) {
            System.err.println("✗ Cannot connect!");
            return;
        }

        DatabaseRepositoryFacade repo = new DatabaseRepositoryFacade();

        // Test 1: Lines
        System.out.println("--- Lines ---");
        Line line = repo.getLineById(1);
        if (line != null) {
            System.out.printf("✓ Line 1: %s (%s → %s)\n",
                    line.getName(),
                    line.getStartStation().getName(),
                    line.getEndStation().getName());
            System.out.printf("  Segments: %d, Total: %.1f km\n",
                    line.getSegments().size(),
                    line.getTotalLengthKm());
        } else {
            System.out.println("✗ Line 1 failed");
        }

        // Test 2: Freights
        System.out.println("\n--- Freights ---");
        Freight freight = repo.getFreight(2001);
        if (freight != null) {
            System.out.printf("✓ Freight 2001: %s → %s\n",
                    freight.getOriginName(),
                    freight.getDestinationName());
            System.out.printf("  Wagons: %d, Date: %s\n",
                    freight.getWagonCount(),
                    freight.getDate());
        } else {
            System.out.println("✗ Freight 2001 failed");
        }

        // Test 3: Trains
        System.out.println("\n--- Trains ---");
        Train train = repo.getTrain(5421);
        if (train != null) {
            System.out.printf("✓ Train 5421: %s\n", train.getOperator());
            System.out.printf("  Date: %s %s\n", train.getDate(), train.getTime());
            System.out.printf("  Locomotives: %d\n", train.getLocomotives().size());
            System.out.printf("  Freights: %d\n", train.getFreights().size());
            System.out.printf("  Path stations: %d\n", train.getPathStations().size());
            System.out.printf("  Total weight: %.1f tons\n", train.getTotalWeightTons());
            System.out.printf("  Total power: %d kW\n", train.getTotalPowerKw());
        } else {
            System.out.println("✗ Train 5421 failed");
        }

        // Test 4: Totals
        System.out.println("\n--- Totals ---");
        System.out.println("✓ Total lines: " + repo.getAllLines().size());
        System.out.println("✓ Total freights: " + repo.getAllFreights().size());
        System.out.println("✓ Total trains: " + repo.getAllTrains().size());

        System.out.println("\n✅ ALL REPOSITORY TESTS PASSED!");
        DatabaseConnection.closeConnection();
    }
}
