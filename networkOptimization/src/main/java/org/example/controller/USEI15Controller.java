package org.example.controller;

import org.example.algorithms.BellmanFordShortestPath;
import org.example.algorithms.NegativeCycleException;
import org.example.domain.Connection;
import org.example.domain.Station;
import org.example.domain.ShortestPathResult;
import org.example.graph.Graph;

import java.util.List;

/**
 * Controller da USEI15 — Risk-Aware Shortest Paths
 *
 * Responsabilidade:
 *  - Executar o algoritmo de caminhos mínimos com custos (Bellman-Ford)
 *  - Detetar ciclos negativos
 *  - Apresentar os resultados
 */
public class USEI15Controller {

    private final Graph<Station, Connection> graph;

    public USEI15Controller(Graph<Station, Connection> graph) {
        this.graph = graph;
    }

    /**
     * Executa a USEI15 entre duas estações
     */
    public void execute(Station source, Station target) {

        BellmanFordShortestPath<Station, Connection> algorithm = new BellmanFordShortestPath<>();

        try {
            ShortestPathResult<Station> result = algorithm.shortestPath(graph, source, target, Connection::getCost);

            printShortestPath(result);

        } catch (NegativeCycleException e) {
            printNegativeCycle(e.getCycle());
        }
    }

    /* ===================== OUTPUT ===================== */

    private void printShortestPath(ShortestPathResult<Station> result) {

        System.out.println("\n=== USEI15 — Risk-Aware Shortest Path ===");
        System.out.println("Path:");

        List<Station> path = result.getPath();
        for (Station s : path) {
            System.out.printf(" - %s (%s)%n",
                    s.getId(),
                    s.getName());
        }

        System.out.printf("Total cost to target: %.2f%n",
                result.getTotalCost());
    }

    private void printNegativeCycle(List<?> cycle) {

        System.out.println("\n=== USEI15 — Negative Cycle Detected ===");
        System.out.println("Stations involved:");

        for (Object obj : cycle) {
            Station s = (Station) obj;
            System.out.printf(" - %s (%s)%n",
                    s.getId(),
                    s.getName());
        }

        System.out.println("⚠ Configuration inconsistency detected.");
    }
}


