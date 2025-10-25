package org.example.service;

import org.example.domain.Record;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Serviço responsável pelo cálculo das sequências de picking
 * de acordo com diferentes estratégias de otimização de rotas.
 */
public class PathSequencingService {

    /**
     * Calcula a distância entre dois pontos (bays) no armazém.
     * Segue as regras definidas no enunciado do projeto.
     */
    public double calculateDistance(Record from, Record to) {
        if (from.getAisle() != to.getAisle()) {
            // Corredores diferentes: desce, move horizontalmente e sobe
            return from.getBay() + Math.abs(from.getAisle() - to.getAisle()) * 3 + to.getBay();
        } else {
            // Mesmo corredor: distância vertical
            return Math.abs(from.getBay() - to.getBay());
        }
    }

    /**
     * Calcula a distância total de um percurso ordenado,
     * começando sempre na entrada (0,0).
     */
    public double calculateTotalDistance(List<Record> path) {
        if (path == null || path.isEmpty()) return 0.0;

        double totalDistance = 0.0;
        Record current = Record.ENTRANCE;

        for (Record next : path) {
            totalDistance += calculateDistance(current, next);
            current = next;
        }

        return totalDistance;
    }

    /**
     * Remove bays duplicadas da lista de localizações.
     */
    private List<Record> mergeDuplicateBays(List<Record> locations) {
        // LinkedHashSet preserva a ordem de inserção
        return new ArrayList<>(new LinkedHashSet<>(locations));
    }

    /**
     * Estratégia A: Deterministic Sweep
     * Ordena por corredor (aisle) e depois por baía (bay), ambos ascendentes.
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

    /**
     * Estratégia B: Nearest-Neighbour (vizinho mais próximo)
     */
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

    /**
     * Classe interna que representa o resultado de uma estratégia.
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
                    "  -> Path: " + path.stream().map(Record::toString).collect(Collectors.joining(" → ")) + "\n" +
                    "  -> Total Distance: " + totalDistance;
        }
    }
}
