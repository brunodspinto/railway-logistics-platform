package org.example.service;

import org.example.domain.Record;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class PathSequencingService {

    public double calculateDistance(Record from, Record to) {
        if (from.getAisle() != to.getAisle()) {
            return from.getBay() + Math.abs(from.getAisle() - to.getAisle()) * 3 + to.getBay();
        } else {
            return Math.abs(from.getBay() - to.getBay());
        }
    }

    private double calculateTotalDistance(List<Record> path) {
        if (path == null || path.isEmpty()) {
            return 0.0;
        }
        double totalDistance = 0.0;
        Record current = Record.ENTRANCE;
        for (Record next : path) {
            totalDistance += calculateDistance(current, next);
            current = next;
        }
        return totalDistance;
    }

    private List<Record> mergeDuplicateBays(List<Record> locations) {
        return new ArrayList<>(new HashSet<>(locations));
    }

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
        List<Record> remaining = new ArrayList<>(mergeDuplicateBays(locationsToVisit));
        List<Record> path = new ArrayList<>();
        Record current = Record.ENTRANCE;
        while (!remaining.isEmpty()) {
            Record nearest = null;
            double minDistance = Double.MAX_VALUE;
            for (Record next : remaining) {
                double distance = calculateDistance(current, next);
                if (distance < minDistance) {
                    minDistance = distance;
                    nearest = next;
                }
            }
            if (nearest != null) {
                path.add(nearest);
                remaining.remove(nearest);
                current = nearest;
            }
        }
        double totalDistance = calculateTotalDistance(path);
        return new PickPathResult(path, totalDistance, "Strategy B (Nearest-Neighbour)");
    }

    // Esta classe interna agora usa a nossa classe 'Record'
    public static class PickPathResult {
        public final List<Record> path;
        public final double totalDistance;
        public final String strategyName;

        public PickPathResult(List<Record> path, double totalDistance, String strategyName) {
            this.path = path;
            this.totalDistance = totalDistance;
            this.strategyName = strategyName;
        }

        // Para facilitar a impressão dos resultados
        @Override
        public String toString() {
            return "Strategy: " + strategyName + "\n" +
                    "  -> Path: " + path + "\n" +
                    "  -> Total Distance: " + totalDistance;
        }
    }
}