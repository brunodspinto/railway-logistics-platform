package org.example.usei15.algorithm;

import org.example.graph.Edge;
import org.example.graph.Graph;
import org.example.usei15.result.ShortestPathResult;

import java.util.*;
import java.util.function.ToDoubleFunction;

public class BellmanFordShortestPath<V, E> {

    public ShortestPathResult<V> shortestPath(Graph<V, E> graph, V source, V target, ToDoubleFunction<E> costFunction) {

        int n = graph.numVertices();
        double[] dist = new double[n];
        int[] parent = new int[n];

        Arrays.fill(dist, Double.POSITIVE_INFINITY);
        Arrays.fill(parent, -1);

        int srcKey = graph.key(source);
        dist[srcKey] = 0;

        //  Relaxar arestas V-1 vezes
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

        //  Detetar ciclo negativo
        for (Edge<V, E> e : graph.edges()) {
            int u = graph.key(e.getVOrig());
            int v = graph.key(e.getVDest());
            double w = costFunction.applyAsDouble(e.getWeight());

            if (dist[u] != Double.POSITIVE_INFINITY && dist[u] + w < dist[v]) {
                // 1. Extrair a lista de vértices do ciclo
                List<V> cycle = extractCycle(graph, parent, v);

                // 2. Gerar o report detalhado com os custos
                String report = generateCycleReport(graph, cycle, costFunction);

                // 3. Lançar a exceção com o report
                throw new NegativeCycleException(cycle, report);
            }
        }

        int targetKey = graph.key(target);

        if (dist[targetKey] == Double.POSITIVE_INFINITY) {
            return ShortestPathResult.noPath();
        }


        //  Reconstruir caminho
        List<V> path = new ArrayList<>();
        int current = graph.key(target);

        while (current != -1) {
            path.add(graph.vertex(current));
            current = parent[current];
        }

        Collections.reverse(path);

        Map<V, Double> costMap = new HashMap<>();
        for (int i = 0; i < dist.length; i++) {
            costMap.put(graph.vertex(i), dist[i]);
        }

        return new ShortestPathResult<>(path, costMap, dist[graph.key(target)], true);
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

    private String generateCycleReport(Graph<V, E> graph, List<V> cycle, ToDoubleFunction<E> costFunction) {
        StringBuilder sb = new StringBuilder();
        double totalCost = 0;

        sb.append("\nCycle path detailed:\n");
        // Iterar sobre o ciclo para encontrar as arestas entre os vértices
        for (int i = 0; i < cycle.size() - 1; i++) {
            V u = cycle.get(i);
            V v = cycle.get(i + 1);

            // Buscar a aresta no grafo
            Edge<V, E> edge = graph.edge(u, v);

            double cost = 0;
            if (edge != null) {
                cost = costFunction.applyAsDouble(edge.getWeight());
            }
            totalCost += cost;

            // Formato: 1. Origem -> Destino [Cost: -10.0]
            sb.append(String.format(" %d. %s -> %s [Cost: %.2f]\n", (i + 1), u, v, cost));
        }

        sb.append(String.format("\nTotal Cycle Cost: %.2f", totalCost));
        return sb.toString();
    }
}
