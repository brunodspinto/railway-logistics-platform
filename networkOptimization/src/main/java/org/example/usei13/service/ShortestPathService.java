package org.example.usei13.service;

import org.example.usei12.domain.Edge;
import org.example.usei12.domain.RailGraph;

import java.util.*;

/**
 * Serviço responsável pelo cálculo de caminhos mínimos
 * entre estações usando o algoritmo de Dijkstra.
 */
public class ShortestPathService {

    /**
     * Calcula as distâncias mínimas desde uma estação origem
     * para todas as outras estações do grafo.
     *
     * Na USEI13, o algoritmo utiliza a lista de adjacência
     * para aceder diretamente aos vizinhos.
     */
    public static Map<String, Double> dijkstra(RailGraph graph, String sourceId) {

        Map<String, Double> dist = new HashMap<>();
        Set<String> visited = new HashSet<>();

        PriorityQueue<String> pq = new PriorityQueue<>(Comparator.comparingDouble(dist::get));

        // inicialização das distâncias
        graph.getStations().forEach(s -> dist.put(s.getId(), Double.POSITIVE_INFINITY));

        dist.put(sourceId, 0.0);
        pq.add(sourceId);

        // ciclo principal do algoritmo de Dijkstra
        while (!pq.isEmpty()) {

            String u = pq.poll();

            if (visited.contains(u)) continue;
            visited.add(u);

            // percorre apenas as arestas adjacentes ao nó atual
            for (Edge e : graph.getAdjEdges(u)) {

                String v = e.getTo().getId();
                double nd = dist.get(u) + e.getLength();

                if (nd < dist.get(v)) {
                    dist.put(v, nd);
                    pq.add(v);
                }
            }
        }

        return dist;
    }
}