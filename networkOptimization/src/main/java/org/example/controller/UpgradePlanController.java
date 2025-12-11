package org.example.controller;

import org.example.algorithms.CycleDetection;
import org.example.algorithms.TopologicalSort;
import org.example.domain.Station;
import org.example.domain.Connection;
import org.example.graph.Graph;
import org.example.loader.BelgianNetworkLoader;

import java.io.IOException;
import java.util.List;

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
     * Carrega a rede belga do ficheiro
     */
    public void loadNetwork(String filePath) throws IOException {
        System.out.println("Loading Belgian railway network...");
        this.network = BelgianNetworkLoader.loadNetwork(filePath);
    }

    /**
     * USEI11: Calcula ordem de upgrades ou identifica ciclos
     *
     * @return Resultado com ordem ou ciclos
     */
    public UpgradePlanResult calculateUpgradeOrder() {
        if (network == null) {
            throw new IllegalStateException("Network not loaded");
        }

        long startTime = System.currentTimeMillis();

        // FASE 1: Verificar ciclos
        System.out.println("\nPhase 1: Checking for cycles...");
        CycleDetection.CycleDetectionResult<Station> cycleResult =
                cycleDetector.detectCycles(network);

        if (cycleResult.hasCycles()) {
            long elapsedTime = System.currentTimeMillis() - startTime;
            return UpgradePlanResult.withCycles(
                    cycleResult.getCycles(),
                    network.numVertices(),
                    network.numEdges(),
                    elapsedTime
            );
        }

        // FASE 2: Executar Topological Sort
        System.out.println("Phase 2: Computing topological order...");
        List<Station> order = topologicalSort.kahn(network);

        long elapsedTime = System.currentTimeMillis() - startTime;

        return UpgradePlanResult.withOrder(
                order,
                network.numVertices(),
                network.numEdges(),
                elapsedTime
        );
    }

    /**
     * Obtém o grafo carregado
     */
    public Graph<Station, Connection> getNetwork() {
        return network;
    }
}
