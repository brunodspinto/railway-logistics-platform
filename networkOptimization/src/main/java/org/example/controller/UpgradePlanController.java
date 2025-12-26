package org.example.controller;



import org.example.algorithms.CycleDetection;
import org.example.algorithms.TopologicalSort;
import org.example.domain.Connection;
import org.example.domain.Station;
import org.example.graph.Edge;
import org.example.graph.Graph;
import org.example.loader.BelgianNetworkLoader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Controller para USEI11 - Directed Line Upgrade Plan
 */
public class UpgradePlanController {

    private Graph<Station, Connection> network;
    private final CycleDetection<Station, Connection> cycleDetector;
    private final TopologicalSort<Station, Connection> topologicalSort;

    public UpgradePlanController() {
        this.cycleDetector = new CycleDetection<>();
        this.topologicalSort = new TopologicalSort<>();
    }

    /**
     * Carrega a rede belga dos ficheiros
     */
    public void loadNetwork(String stationsPath, String linesPath) throws IOException {
        System.out.println("Loading Belgian railway network...");
        this.network = BelgianNetworkLoader.loadNetwork(stationsPath, linesPath);
    }

    /**
     * USEI11: Calcula ordem de upgrades ou identifica ciclos
     */
    public UpgradePlanResult calculateUpgradeOrder() {
        if (network == null) {
            throw new IllegalStateException("Network not loaded");
        }

        long startTime = System.currentTimeMillis();

        System.out.println("\nPhase 1: Checking for cycles...");

        Set<Station> stationsInCycles = cycleDetector.findStationsInCycles(network);

        if (!stationsInCycles.isEmpty()) {
            long elapsedTime = System.currentTimeMillis() - startTime;
            return UpgradePlanResult.withCycles(
                    stationsInCycles,
                    network.numVertices(),
                    network.numEdges(),
                    elapsedTime
            );
        }

        System.out.println("Phase 2: Computing topological order...");
        List<Station> order = topologicalSort.kahn(network);

        long elapsedTime = System.currentTimeMillis() - startTime;

        return UpgradePlanResult.withOrder(order, network.numVertices(), network.numEdges(), elapsedTime
        );
    }

    public Graph<Station, Connection> getNetwork() {
        return network;
    }
}