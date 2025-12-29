package org.example.usei15.controller;

import org.example.usei15.algorithm.BellmanFordShortestPath;
import org.example.usei15.algorithm.NegativeCycleException;
import org.example.domain.Connection;
import org.example.domain.Station;
import org.example.usei15.result.ShortestPathResult;
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
            System.out.println("\n=== USEI15 — Negative Cycle Detected ===");
            System.out.println(e.getDetailedMessage());
            System.out.println("\n⚠ Configuration inconsistency detected.");
        }
    }

    /* ===================== OUTPUT ===================== */

    private void printShortestPath(ShortestPathResult<Station> result) {

        System.out.println("\n=== USEI15 — Risk-Aware Shortest Path ===");

        if (!result.hasPath()) {
            System.out.println("No path exists between the selected stations.");
            return;
        }

        System.out.println("Path:");

        List<Station> path = result.getPath();

        for (Station s : path) {
            System.out.printf(" - %s (%s) [cost: %.2f]%n",
                    s.getId(),
                    s.getName(),
                    result.getCostTo(s));
        }

        System.out.printf("Total cost to target: %.2f%n",
                result.getTotalCost());
    }
}


