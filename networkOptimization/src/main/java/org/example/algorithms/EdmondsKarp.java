package org.example.algorithms;

import org.example.domain.Connection;
import org.example.graph.Edge;
import org.example.graph.Graph;

import java.util.*;

/**
 * Edmonds-Karp algorithm implementation for Maximum Flow.
 * Uses BFS to find the shortest augmenting path.
 */
public class EdmondsKarp<V, E> {

    public double computeMaxFlow(Graph<V, Connection> graph, V source, V sink) {
        // Validations
        if (graph == null || source == null || sink == null)
            throw new IllegalArgumentException("Graph and vertices cannot be null.");
        if (!graph.validVertex(source) || !graph.validVertex(sink))
            throw new IllegalArgumentException("Source and Sink must exist in the graph.");
        if (source.equals(sink)) return 0.0;

        // 1. Build Residual Graph
        Map<V, Map<V, Double>> residualGraph = new HashMap<>();

        for (V v : graph.vertices()) {
            residualGraph.put(v, new HashMap<>());
        }

        for (Edge<V, Connection> edge : graph.edges()) {
            V u = edge.getVOrig();
            V v = edge.getVDest();

            double capacity = edge.getWeight().getCapacity();
            if (capacity < 0) {
                throw new IllegalArgumentException("Negative capacity detected: " + edge);
            }

            // Merge capacities for multigraphs
            residualGraph.get(u).merge(v, capacity, Double::sum);

            // Initialize reverse edge with 0 if absent
            residualGraph.get(v).putIfAbsent(u, 0.0);
        }

        double maxFlow = 0.0;
        Map<V, V> parent = new HashMap<>();

        // 2. Main Loop: BFS for augmenting paths
        while (bfs(residualGraph, source, sink, parent)) {

            double pathFlow = Double.MAX_VALUE;
            V v = sink;

            // Find bottleneck
            while (!v.equals(source)) {
                V u = parent.get(v);
                double capacity = residualGraph.get(u).get(v);
                pathFlow = Math.min(pathFlow, capacity);
                v = u;
            }

            // Update residual capacities
            v = sink;
            while (!v.equals(source)) {
                V u = parent.get(v);

                // Forward edge
                residualGraph.get(u).put(v, residualGraph.get(u).get(v) - pathFlow);

                // Backward edge
                residualGraph.get(v).put(u, residualGraph.get(v).get(u) + pathFlow);

                v = u;
            }

            maxFlow += pathFlow;
        }

        return maxFlow;
    }

    private boolean bfs(Map<V, Map<V, Double>> residualGraph, V source, V sink, Map<V, V> parent) {
        Queue<V> queue = new LinkedList<>();
        Set<V> visited = new HashSet<>();

        queue.add(source);
        visited.add(source);
        parent.clear();

        while (!queue.isEmpty()) {
            V u = queue.poll();

            Map<V, Double> neighbors = residualGraph.get(u);
            if (neighbors == null) continue;

            for (Map.Entry<V, Double> entry : neighbors.entrySet()) {
                V v = entry.getKey();
                Double cap = entry.getValue();

                if (!visited.contains(v) && cap > 0) {
                    visited.add(v);
                    parent.put(v, u);
                    queue.add(v);

                    if (v.equals(sink)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}