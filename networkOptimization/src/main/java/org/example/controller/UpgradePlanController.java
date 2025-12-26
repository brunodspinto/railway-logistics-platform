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
     * ⭐ MUDOU: Agora recebe DOIS caminhos (stations.csv e lines.csv)
     */
    public void loadNetwork(String stationsPath, String linesPath) throws IOException {
        System.out.println("Loading Belgian railway network...");
        this.network = BelgianNetworkLoader.loadNetwork(stationsPath, linesPath);
        //             ^^^^^^^^^^^^^^^^^^^^^^^^^ NOVO LOADER
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

            // ⭐⭐⭐ ADICIONAR AQUI - DEBUG BLOCK ⭐⭐⭐
            /**
            System.out.println("\n" + "=".repeat(70));
            System.out.println("🔍 DEBUG: Analyzing cycles in the network");
            System.out.println("=".repeat(70));

            // Pegar nas primeiras 5 estações em ciclos
            List<Station> sampleStations = new ArrayList<>(stationsInCycles);
            int samplesToShow = Math.min(5, sampleStations.size());

            for (int i = 0; i < samplesToShow; i++) {
                Station station = sampleStations.get(i);
                System.out.println("\n📍 Station: " + station.getName() +
                        " (ID: " + station.getId() + ")");

                // Conexões de SAÍDA (outgoing)
                Collection<Edge<Station, Connection>> outgoing = network.outgoingEdges(station);
                if (outgoing != null && !outgoing.isEmpty()) {
                    System.out.println("   Outgoing connections:");
                    outgoing.forEach(edge -> {
                        System.out.printf("     → %s (ID: %s, dist: %.2f km)%n",
                                edge.getVDest().getName(),
                                edge.getVDest().getId(),
                                edge.getWeight().getDistance());
                    });
                }

                // Conexões de ENTRADA (incoming)
                Collection<Edge<Station, Connection>> incoming = network.incomingEdges(station);
                if (incoming != null && !incoming.isEmpty()) {
                    System.out.println("   Incoming connections:");
                    incoming.forEach(edge -> {
                        System.out.printf("     ← %s (ID: %s, dist: %.2f km)%n",
                                edge.getVOrig().getName(),
                                edge.getVOrig().getId(),
                                edge.getWeight().getDistance());
                    });
                }
            }

            System.out.println("\n" + "=".repeat(70));
            // ⭐⭐⭐ FIM DO DEBUG BLOCK ⭐⭐⭐
             */
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

        return UpgradePlanResult.withOrder(
                order,
                network.numVertices(),
                network.numEdges(),
                elapsedTime
        );
    }

    public Graph<Station, Connection> getNetwork() {
        return network;
    }
}