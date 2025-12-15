package org.example.algorithms;

import org.example.graph.Edge;
import org.example.graph.Graph;
import org.example.domain.ShortestPathResult;

import java.util.*;
import java.util.function.ToDoubleFunction;

public class BellmanFordShortestPath<V, E> {

    public ShortestPathResult<V> shortestPath(
            Graph<V, E> graph,
            V source,
            V target,
            ToDoubleFunction<E> costFunction
    ) {

        int n = graph.numVertices();
        double[] dist = new double[n];
        int[] parent = new int[n];

        Arrays.fill(dist, Double.POSITIVE_INFINITY);
        Arrays.fill(parent, -1);

        int srcKey = graph.key(source);
        dist[srcKey] = 0;

        // 1️⃣ Relaxar arestas V-1 vezes
        for (int i = 1; i < n; i++) {
            for (Edge<V, E> e : graph.edges()) {
                int u = graph.key(e.getVOrig());
                int v = graph.key(e.getVDest());
                double w = costFunction.applyAsDouble(e.getWeight());

                if (dist[u] != Double.POSITIVE_INFINITY && dist[u] + w < dist[v]) {
                    dist[v] = dist[u] + w;
                    parent[v] = u;
                }
            }
        }

        // 2️⃣ Detetar ciclo negativo
        for (Edge<V, E> e : graph.edges()) {
            int u = graph.key(e.getVOrig());
            int v = graph.key(e.getVDest());
            double w = costFunction.applyAsDouble(e.getWeight());

            if (dist[u] != Double.POSITIVE_INFINITY && dist[u] + w < dist[v]) {
                throw new NegativeCycleException(extractCycle(graph, parent, v));
            }
        }

        // 3️⃣ Reconstruir caminho
        List<V> path = new ArrayList<>();
        int current = graph.key(target);

        while (current != -1) {
            path.add(graph.vertex(current));
            current = parent[current];
        }

        Collections.reverse(path);

        return new ShortestPathResult<>(path, dist[graph.key(target)]);
    }

    private List<V> extractCycle(Graph<V, ?> graph, int[] parent, int start) {
        Set<Integer> visited = new HashSet<>();
        int v = start;

        while (!visited.contains(v)) {
            visited.add(v);
            v = parent[v];
        }

        int cycleStart = v;
        List<V> cycle = new ArrayList<>();
        cycle.add(graph.vertex(cycleStart));

        v = parent[cycleStart];
        while (v != cycleStart) {
            cycle.add(graph.vertex(v));
            v = parent[v];
        }

        cycle.add(graph.vertex(cycleStart));
        Collections.reverse(cycle);

        return cycle;
    }
}
