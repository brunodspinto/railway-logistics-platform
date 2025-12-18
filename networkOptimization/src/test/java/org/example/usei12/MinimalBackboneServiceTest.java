package org.example.usei12;

import org.example.usei12.domain.Edge;
import org.example.usei12.domain.RailGraph;
import org.example.usei12.domain.Station;
import org.example.usei12.service.MinimalBackboneService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MinimalBackboneServiceTest {

    /**
     * Testa um triângulo simples:
     * o MST deve escolher as duas arestas mais baratas (1.0 e 2.0).
     */
    @Test
    @DisplayName("MST num triângulo simples: deve escolher as duas arestas de menor custo")
    void mstOnSimpleTriangle() {
        RailGraph graph = new RailGraph();

        Station a = graph.getOrCreateStation("A", "A", 0.0, 0.0);
        Station b = graph.getOrCreateStation("B", "B", 0.0, 1.0);
        Station c = graph.getOrCreateStation("C", "C", 1.0, 0.0);

        graph.addEdge(a, b, 1.0);
        graph.addEdge(b, c, 2.0);
        graph.addEdge(a, c, 10.0);

        List<Edge> backbone = MinimalBackboneService.computeMinimalBackbone(graph);

        assertEquals(2, backbone.size());

        double total = backbone.stream().mapToDouble(Edge::getLength).sum();
        assertEquals(3.0, total, 1e-6);
    }

    /**
     * Testa um grafo com vértices desligados:
     * o MST é apenas a floresta na componente ligada (arestas de custo 1.0 + 1.0).
     */
    @Test
    @DisplayName("Grafo desconectado: MST deve gerar uma floresta apenas na componente ligada")
    void mstOnDisconnectedGraphProducesForest() {
        RailGraph graph = new RailGraph();

        Station a = graph.getOrCreateStation("A", "A", 0, 0);
        Station b = graph.getOrCreateStation("B", "B", 0, 0);
        Station c = graph.getOrCreateStation("C", "C", 0, 0);
        Station d = graph.getOrCreateStation("D", "D", 0, 0);

        graph.addEdge(a, b, 1.0);
        graph.addEdge(b, c, 1.0);
        graph.addEdge(a, c, 5.0);

        List<Edge> backbone = MinimalBackboneService.computeMinimalBackbone(graph);

        assertEquals(2, backbone.size());
        double total = backbone.stream().mapToDouble(Edge::getLength).sum();
        assertEquals(2.0, total, 1e-6);
    }
}