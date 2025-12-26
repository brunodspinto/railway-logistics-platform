package org.example.controller;

import org.example.algorithms.EdmondsKarp;
import org.example.domain.Connection;
import org.example.domain.Station;
import org.example.graph.Graph;
import org.example.loader.BelgianNetworkLoader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ComputeMaxFlowController {

    private Graph<Station, Connection> network;
    private final EdmondsKarp<Station, Connection> maxFlowAlgorithm;

    public ComputeMaxFlowController() {
        this.maxFlowAlgorithm = new EdmondsKarp<>();
    }

    public void loadNetwork(String stationsPath, String linesPath) throws IOException {
        this.network = BelgianNetworkLoader.loadNetwork(stationsPath, linesPath);
    }

    public List<Station> getStations() {
        if (network == null) {
            return new ArrayList<>();
        }
        List<Station> stations = network.vertices();
        stations.sort(Comparator.comparing(Station::getName));
        return stations;
    }

    public Double calculateMaxFlow(Station source, Station sink) {
        if (network == null) {
            throw new IllegalStateException("Network not loaded.");
        }

        System.out.println("Executing Edmonds-Karp algorithm...");
        long startTime = System.nanoTime();

        double maxFlow = maxFlowAlgorithm.computeMaxFlow(network, source, sink);

        long endTime = System.nanoTime();
        double durationMs = (endTime - startTime) / 1e6;

        System.out.printf("[Performance] Time: %.4f ms | Complexity: O(V * E^2)%n", durationMs);

        return maxFlow;
    }
}