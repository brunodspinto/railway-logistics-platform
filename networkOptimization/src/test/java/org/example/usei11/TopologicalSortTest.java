package org.example.usei11;

import org.example.algorithms.TopologicalSort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.example.domain.Connection;
import org.example.domain.Station;
import org.example.graph.Graph;
import org.example.graph.map.MapGraph;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários para TopologicalSort
 * Baseado em Kahn's Algorithm (ESINF06-Graph.pdf, slide 116)
 */
class TopologicalSortTest {

    private TopologicalSort<Station, Connection> sorter;

    @BeforeEach
    void setUp() {
        sorter = new TopologicalSort<>();
    }

    /**
     * Teste 1: DAG simples linear
     * A → B → C
     * Esperado: [A, B, C]
     */
    @Test
    void testSimpleDAG_LinearChain() {
        // Arrange
        Graph<Station, Connection> graph = new MapGraph<>(true);
        Station a = new Station("1", "Station A");
        Station b = new Station("2", "Station B");
        Station c = new Station("3", "Station C");

        graph.addVertex(a);
        graph.addVertex(b);
        graph.addVertex(c);
        graph.addEdge(a, b, new Connection(a, b, 10.0, 5, 10.0));
        graph.addEdge(b, c, new Connection(b, c, 10.0, 5, 10.0));

        // Act
        List<Station> order = sorter.kahn(graph);

        // Assert
        assertNotNull(order);
        assertEquals(3, order.size());
        assertEquals(a, order.get(0), "A should be first");
        assertEquals(b, order.get(1), "B should be second");
        assertEquals(c, order.get(2), "C should be last");
    }

    /**
     * Teste 2: DAG com fork (diamante)
     * A → B → D
     * A → C → D
     * Esperado: A primeiro, D último, B e C em qualquer ordem no meio
     */
    @Test
    void testDAG_DiamondShape() {
        // Arrange
        Graph<Station, Connection> graph = new MapGraph<>(true);
        Station a = new Station("1", "A");
        Station b = new Station("2", "B");
        Station c = new Station("3", "C");
        Station d = new Station("4", "D");

        graph.addVertex(a); graph.addVertex(b);
        graph.addVertex(c); graph.addVertex(d);

        graph.addEdge(a, b, new Connection(a, b, 10.0, 5, 10.0));
        graph.addEdge(a, c, new Connection(a, c, 10.0, 5, 10.0));
        graph.addEdge(b, d, new Connection(b, d, 10.0, 5, 10.0));
        graph.addEdge(c, d, new Connection(c, d, 10.0, 5, 10.0));

        // Act
        List<Station> order = sorter.kahn(graph);

        // Assert
        assertNotNull(order);
        assertEquals(4, order.size());
        assertEquals(a, order.get(0), "A must be first (no predecessors)");
        assertEquals(d, order.get(3), "D must be last (depends on B and C)");

        // B e C podem estar em qualquer ordem, mas antes de D
        assertTrue(order.indexOf(b) < order.indexOf(d),
                "B must come before D");
        assertTrue(order.indexOf(c) < order.indexOf(d),
                "C must come before D");
    }

    /**
     * Teste 3: Grafo com ciclo deve lançar exceção
     * A → B → A
     */
    @Test
    void testGraphWithCycle_ThrowsException() {
        // Arrange
        Graph<Station, Connection> graph = new MapGraph<>(true);
        Station a = new Station("1", "A");
        Station b = new Station("2", "B");

        graph.addVertex(a);
        graph.addVertex(b);
        graph.addEdge(a, b, new Connection(a, b, 10.0, 5, 10.0));
        graph.addEdge(b, a, new Connection(b, a, 10.0, 5, 10.0));

        // Act & Assert
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> sorter.kahn(graph),
                "Should throw exception for cyclic graph"
        );

        assertTrue(exception.getMessage().contains("cycles"),
                "Exception message should mention cycles");
    }

    /**
     * Teste 4: Grafo com vértice único
     * A (sozinha)
     * Esperado: [A]
     */
    @Test
    void testSingleVertex() {
        // Arrange
        Graph<Station, Connection> graph = new MapGraph<>(true);
        Station a = new Station("1", "A");
        graph.addVertex(a);

        // Act
        List<Station> order = sorter.kahn(graph);

        // Assert
        assertNotNull(order);
        assertEquals(1, order.size());
        assertEquals(a, order.get(0));
    }

    /**
     * Teste 5: Grafo desconectado (2 componentes sem ligação)
     * A → B, C → D
     * Esperado: Ordem válida com 4 vértices
     */
    @Test
    void testDisconnectedGraph() {
        // Arrange
        Graph<Station, Connection> graph = new MapGraph<>(true);
        Station a = new Station("1", "A");
        Station b = new Station("2", "B");
        Station c = new Station("3", "C");
        Station d = new Station("4", "D");

        graph.addVertex(a); graph.addVertex(b);
        graph.addVertex(c); graph.addVertex(d);

        graph.addEdge(a, b, new Connection(a, b, 10.0, 5, 10.0));
        graph.addEdge(c, d, new Connection(c, d, 10.0, 5, 10.0));

        // Act
        List<Station> order = sorter.kahn(graph);

        // Assert
        assertNotNull(order);
        assertEquals(4, order.size());

        // Verificar que A vem antes de B
        assertTrue(order.indexOf(a) < order.indexOf(b));

        // Verificar que C vem antes de D
        assertTrue(order.indexOf(c) < order.indexOf(d));
    }

    /**
     * Teste 6: Grafo complexo sem ciclos
     * A → B → D → F
     * A → C → E → F
     * B → E
     */
    @Test
    void testComplexDAG() {
        // Arrange
        Graph<Station, Connection> graph = new MapGraph<>(true);
        Station a = new Station("1", "A");
        Station b = new Station("2", "B");
        Station c = new Station("3", "C");
        Station d = new Station("4", "D");
        Station e = new Station("5", "E");
        Station f = new Station("6", "F");

        graph.addVertex(a); graph.addVertex(b); graph.addVertex(c);
        graph.addVertex(d); graph.addVertex(e); graph.addVertex(f);

        graph.addEdge(a, b, new Connection(a, b, 10.0, 5, 10.0));
        graph.addEdge(a, c, new Connection(a, c, 10.0, 5, 10.0));
        graph.addEdge(b, d, new Connection(b, d, 10.0, 5, 10.0));
        graph.addEdge(b, e, new Connection(b, e, 10.0, 5, 10.0));
        graph.addEdge(c, e, new Connection(c, e, 10.0, 5, 10.0));
        graph.addEdge(d, f, new Connection(d, f, 10.0, 5, 10.0));
        graph.addEdge(e, f, new Connection(e, f, 10.0, 5, 10.0));

        // Act
        List<Station> order = sorter.kahn(graph);

        // Assert
        assertNotNull(order);
        assertEquals(6, order.size());

        // A deve ser primeiro (sem predecessores)
        assertEquals(a, order.get(0));

        // F deve ser último (todos dependem dele)
        assertEquals(f, order.get(5));

        // Verificar dependências
        assertTrue(order.indexOf(a) < order.indexOf(b));
        assertTrue(order.indexOf(a) < order.indexOf(c));
        assertTrue(order.indexOf(b) < order.indexOf(d));
        assertTrue(order.indexOf(b) < order.indexOf(e));
        assertTrue(order.indexOf(c) < order.indexOf(e));
        assertTrue(order.indexOf(d) < order.indexOf(f));
        assertTrue(order.indexOf(e) < order.indexOf(f));
    }

    /**
     * Teste 7: Grafo vazio
     * Esperado: Lista vazia
     */
    @Test
    void testEmptyGraph() {
        // Arrange
        Graph<Station, Connection> graph = new MapGraph<>(true);

        // Act
        List<Station> order = sorter.kahn(graph);

        // Assert
        assertNotNull(order);
        assertTrue(order.isEmpty(), "Empty graph should return empty list");
    }
}
