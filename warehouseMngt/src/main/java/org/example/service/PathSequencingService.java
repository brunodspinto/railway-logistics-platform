package org.example.service;

import org.example.domain.Record;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service responsible for calculating picking path sequences
 * according to different route optimization strategies.
 */
public class PathSequencingService {

    /**
     * Calculates the distance between two points (bays) in the warehouse.
     * The formula follows the rules defined in the project's requirements document.
     *
     * @param from The starting point.
     * @param to   The destination point.
     * @return The calculated distance.
     */
    public double calculateDistance(Record from, Record to) {
        // If the aisles are different, the path involves
        // going down to the main corridor, moving horizontally, and then going up.
        if (from.getAisle() != to.getAisle()) {
            return from.getBay() + Math.abs(from.getAisle() - to.getAisle()) * 3 + to.getBay();
        } else {
            // If they are in the same aisle, the distance is the vertical difference.
            return Math.abs(from.getBay() - to.getBay());
        }
    }

    /**
     * Calculates the total distance of an ordered path,
     * always starting from the entrance (0,0).
     *
     * @param path The ordered list of bays to visit.
     * @return The total distance of the path.
     */
    private double calculateTotalDistance(List<Record> path) {
        if (path == null || path.isEmpty()) {
            return 0.0;
        }

        double totalDistance = 0.0;

        Record current = Record.ENTRANCE; // The path always starts at the entrance.

        for (Record next : path) {
            totalDistance += calculateDistance(current, next);
            current = next; // Update the current location for the next step.
        }

        return totalDistance;
    }

    /**
     * Removes duplicate bays from a list of locations to visit.
     * If a trolley needs to go to the same bay to pick up items from different boxes,
     * the bay should only count as one stop.
     *
     * @param locations The list of locations, potentially with duplicates.
     * @return A list of unique bays.
     */
    private List<Record> mergeDuplicateBays(List<Record> locations) {
        // Using a Set ensures that all bays in the final list are unique.
        return new ArrayList<>(new HashSet<>(locations));
    }

    /**
     * Calculates the picking path using Strategy A (Deterministic Sweep).
     * This strategy sorts the bays by aisle and then by bay position
     * in ascending order.
     *
     * @param locationsToVisit The list of bays to visit.
     * @return A PickPathResult containing the path and the total distance.
     */
    public PickPathResult sequenceByStrategyA(List<Record> locationsToVisit) {
        List<Record> uniqueLocations = mergeDuplicateBays(locationsToVisit);
        List<Record> sortedPath = uniqueLocations.stream()
                .sorted(Comparator.comparingInt(Record::getAisle)
                        .thenComparingInt(Record::getBay))
                .collect(Collectors.toList());

        double totalDistance = calculateTotalDistance(sortedPath);

        return new PickPathResult(sortedPath, totalDistance, "Strategy A (Deterministic Sweep)");
    }

    public PickPathResult sequenceByStrategyB(List<Record> locationsToVisit) {
        // Start with a list of all unique bays that need to be visited.
        List<Record> remaining = new ArrayList<>(mergeDuplicateBays(locationsToVisit));

        // This list will store our final path, built step-by-step.
        List<Record> path = new ArrayList<>();

        // The starting point (the initial "previous") is always the warehouse entrance.
        Record current = Record.ENTRANCE;

        // Continue until there are no more bays to visit.
        while (!remaining.isEmpty()) {
            Record nearest = null;
            double minDistance = Double.MAX_VALUE;

            // For each remaining bay, calculate the distance from the CURRENT point (the "previous" one).
            for (Record next : remaining) {
                double distance = calculateDistance(current, next);

                // If this bay is the closest one found so far...
                if (distance < minDistance) {
                    minDistance = distance;
                    nearest = next; // ...save it as the next candidate.
                }
            }

            // After checking all remaining bays, add the nearest one to the path.
            if (nearest != null) {
                path.add(nearest);
                remaining.remove(nearest); // Remove it from the list of bays to visit.
                current = nearest; // UPDATE the "previous" location for the next iteration.
            }
        }

        // Once the path is complete, calculate its total distance.
        double totalDistance = calculateTotalDistance(path);
        return new PickPathResult(path, totalDistance, "Strategy B (Nearest-Neighbour)");
    }

    /**
     * Inner class to encapsulate the result of a path calculation.
     * It contains the path (the ordered list of bays) and the total distance.
     */
    public static class PickPathResult {
        public final List<Record> path;
        public final double totalDistance;
        public final String strategyName;

        public PickPathResult(List<Record> path, double totalDistance, String strategyName) {
            this.path = path;
            this.totalDistance = totalDistance;
            this.strategyName = strategyName;
        }

        @Override
        public String toString() {
            return "Strategy: " + strategyName + "\n" +
                    "  -> Path: " + path.stream().map(Record::toString).collect(Collectors.joining(", ")) + "\n" +
                    "  -> Total Distance: " + totalDistance;
        }
    }
}