package org.example.controller;

import org.example.domain.Station;

import java.util.List;

/**
 * Resultado da execução da USEI11
 */
public class UpgradePlanResult {

    private final boolean hasCycles;
    private final List<Station> upgradeOrder;
    private final List<List<Station>> cycles;
    private final int numStations;
    private final int numConnections;
    private final long executionTimeMs;

    private UpgradePlanResult(boolean hasCycles,
                              List<Station> upgradeOrder,
                              List<List<Station>> cycles,
                              int numStations,
                              int numConnections,
                              long executionTimeMs) {
        this.hasCycles = hasCycles;
        this.upgradeOrder = upgradeOrder;
        this.cycles = cycles;
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
     */
    public static UpgradePlanResult withCycles(List<List<Station>> cycles,
                                               int numStations,
                                               int numConnections,
                                               long executionTimeMs) {
        return new UpgradePlanResult(true, null, cycles,
                numStations, numConnections, executionTimeMs);
    }

    // Getters
    public boolean hasCycles() { return hasCycles; }
    public List<Station> getUpgradeOrder() { return upgradeOrder; }
    public List<List<Station>> getCycles() { return cycles; }
    public int getNumStations() { return numStations; }
    public int getNumConnections() { return numConnections; }
    public long getExecutionTimeMs() { return executionTimeMs; }
    public String getComplexity() { return "O(V + E)"; }
}

