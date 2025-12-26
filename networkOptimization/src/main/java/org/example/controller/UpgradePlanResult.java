package org.example.controller;

import org.example.domain.Station;

import java.util.List;
import java.util.Set;

/**
 * Resultado da execução da USEI11
 */
public class UpgradePlanResult {

    private final boolean hasCycles;
    private final List<Station> upgradeOrder;
    private final Set<Station> stationsInCycles;  // ⭐ MUDOU: Set em vez de List<List<>>
    private final int numStations;
    private final int numConnections;
    private final long executionTimeMs;

    private UpgradePlanResult(boolean hasCycles,
                              List<Station> upgradeOrder,
                              Set<Station> stationsInCycles,  // ⭐ MUDOU
                              int numStations,
                              int numConnections,
                              long executionTimeMs) {
        this.hasCycles = hasCycles;
        this.upgradeOrder = upgradeOrder;
        this.stationsInCycles = stationsInCycles;  // ⭐ MUDOU
        this.numStations = numStations;
        this.numConnections = numConnections;
        this.executionTimeMs = executionTimeMs;
    }

    /**
     * Factory: resultado com ordem válida
     */
    public static UpgradePlanResult withOrder(List<Station> order,
                                              int numStations,
                                              int numConnections,
                                              long executionTimeMs) {
        return new UpgradePlanResult(false, order, null,
                numStations, numConnections, executionTimeMs);
    }

    /**
     * Factory: resultado com ciclos
     * ⭐ MUDOU: Agora recebe Set<Station> em vez de List<List<Station>>
     */
    public static UpgradePlanResult withCycles(Set<Station> stationsInCycles,
                                               int numStations,
                                               int numConnections,
                                               long executionTimeMs) {
        return new UpgradePlanResult(true, null, stationsInCycles,
                numStations, numConnections, executionTimeMs);
    }

    // Getters
    public boolean hasCycles() {
        return hasCycles;
    }

    public List<Station> getUpgradeOrder() {
        return upgradeOrder;
    }

    // ⭐ MUDOU: Getter retorna Set<Station>
    public Set<Station> getStationsInCycles() {
        return stationsInCycles;
    }

    public int getNumStations() {
        return numStations;
    }

    public int getNumConnections() {
        return numConnections;
    }

    public long getExecutionTimeMs() {
        return executionTimeMs;
    }

    public String getComplexity() {
        return "O(V + E)";
    }
}