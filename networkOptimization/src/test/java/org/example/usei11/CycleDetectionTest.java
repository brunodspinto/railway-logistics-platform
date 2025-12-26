package org.example.usei11;

import org.example.algorithms.CycleDetection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.example.domain.Connection;
import org.example.domain.Station;
import org.example.graph.Graph;
import org.example.graph.map.MapGraph;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários para CycleDetection
 * Baseado em Colored DFS (ESINF06-Graph.pdf, slides 90-92)
 */
class CycleDetectionTest {

    private CycleDetection<Station, Connection> detector;

    @BeforeEach
    void setUp() {
        detector = new CycleDetection<>();
    }

    /**
     * Teste 1: Grafo linear sem ciclos
     * A → B → C
     * Esperado: Set vazio (sem ciclos)
     */
    @Test
    void testNoCycles_LinearGraph() {
        // Arrange
        Graph<Station, Connection> graph = new MapGraph<>(true);
        Station a = new Station("1", "Station A");
        Station b = new Station("2", "Station B");
        Station c = new Station("3", "Station C");

        graph.addVertex(a);
        graph.addVertex(b);
        graph.addVertex(c);
        graph.addEdge(a, b, new Connection(a, b, 10.0, 5, 10.0));
        graph.addEdge(b, c, new Connection(b, c, 15.0, 5, 15.0));

        // Act
        Set<Station> cycleStations = detector.findStationsInCycles(graph);

        // Assert
        assertTrue(cycleStations.isEmpty(),
                "Linear graph should have no cycles");
    }

    /**
     * Teste 2: Ciclo simples com 3 vértices
     * A → B → C → A
     * Esperado: {A, B, C}
     */
    @Test
    void testSimpleCycle_ThreeNodes() {
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
        graph.addEdge(c, a, new Connection(c, a, 10.0, 5, 10.0));

        // Act
        Set<Station> cycleStations = detector.findStationsInCycles(graph);

        // Assert
        assertEquals(3, cycleStations.size(),
                "Should detect all 3 stations in cycle");
        assertTrue(cycleStations.contains(a));
        assertTrue(cycleStations.contains(b));
        assertTrue(cycleStations.contains(c));
    }

    /**
     * Teste 3: Ciclo bidireccional (A ↔ B)
     * Esperado: {A, B}
     */
    @Test
    void testBidirectionalCycle() {
        // Arrange
        Graph<Station, Connection> graph = new MapGraph<>(true);
        Station a = new Station("1", "Station A");
        Station b = new Station("2", "Station B");

        graph.addVertex(a);
        graph.addVertex(b);
        graph.addEdge(a, b, new Connection(a, b, 10.0, 5, 10.0));
        graph.addEdge(b, a, new Connection(b, a, 10.0, 5, 10.0));

        // Act
        Set<Station> cycleStations = detector.findStationsInCycles(graph);

        // Assert
        assertEquals(2, cycleStations.size());
        assertTrue(cycleStations.contains(a));
        assertTrue(cycleStations.contains(b));
    }

    /**
     * Teste 4: Self-loop (A → A)
     * Esperado: {A}
     */
    @Test
    void testSelfLoop() {
        // Arrange
        Graph<Station, Connection> graph = new MapGraph<>(true);
        Station a = new Station("1", "Station A");

        graph.addVertex(a);
        graph.addEdge(a, a, new Connection(a, a, 5.0, 5, 5.0));

        // Act
        Set<Station> cycleStations = detector.findStationsInCycles(graph);

        // Assert
        assertEquals(1, cycleStations.size());
        assertTrue(cycleStations.contains(a));
    }

    /**
     * Teste 5: Múltiplos ciclos separados
     * A ↔ B (ciclo 1), C ↔ D (ciclo 2), E → F (sem ciclo)
     * Esperado: {A, B, C, D} (E e F não devem estar)
     */
    @Test
    void testMultipleSeparateCycles() {
        // Arrange
        Graph<Station, Connection> graph = new MapGraph<>(true);
        Station a = new Station("1", "Station A");
        Station b = new Station("2", "Station B");
        Station c = new Station("3", "Station C");
        Station d = new Station("4", "Station D");
        Station e = new Station("5", "Station E");
        Station f = new Station("6", "Station F");

        graph.addVertex(a); graph.addVertex(b);
        graph.addVertex(c); graph.addVertex(d);
        graph.addVertex(e); graph.addVertex(f);

        // Ciclo 1: A ↔ B
        graph.addEdge(a, b, new Connection(a, b, 10.0, 5, 10.0));
        graph.addEdge(b, a, new Connection(b, a, 10.0, 5, 10.0));

        // Ciclo 2: C ↔ D
        graph.addEdge(c, d, new Connection(c, d, 10.0, 5, 10.0));
        graph.addEdge(d, c, new Connection(d, c, 10.0, 5, 10.0));

        // Sem ciclo: E → F
        graph.addEdge(e, f, new Connection(e, f, 10.0, 5, 10.0));

        // Act
        Set<Station> cycleStations = detector.findStationsInCycles(graph);

        // Assert
        assertEquals(4, cycleStations.size(),
                "Should detect 4 stations in cycles");
        assertTrue(cycleStations.contains(a));
        assertTrue(cycleStations.contains(b));
        assertTrue(cycleStations.contains(c));
        assertTrue(cycleStations.contains(d));
        assertFalse(cycleStations.contains(e),
                "Station E should NOT be in cycles");
        assertFalse(cycleStations.contains(f),
                "Station F should NOT be in cycles");
    }

    /**
     * Teste 6: Ciclo com cadeia adicional
     * A → B → C → A, B → D → E (sem ciclo)
     * Esperado: {A, B, C} (D e E não devem estar)
     */
    @Test
    void testCycleWithAdditionalChain() {
        // Arrange
        Graph<Station, Connection> graph = new MapGraph<>(true);
        Station a = new Station("1", "A");
        Station b = new Station("2", "B");
        Station c = new Station("3", "C");
        Station d = new Station("4", "D");
        Station e = new Station("5", "E");

        graph.addVertex(a); graph.addVertex(b); graph.addVertex(c);
        graph.addVertex(d); graph.addVertex(e);

        // Ciclo: A → B → C → A
        graph.addEdge(a, b, new Connection(a, b, 10.0, 5, 10.0));
        graph.addEdge(b, c, new Connection(b, c, 10.0, 5, 10.0));
        graph.addEdge(c, a, new Connection(c, a, 10.0, 5, 10.0));

        // Cadeia adicional: B → D → E
        graph.addEdge(b, d, new Connection(b, d, 10.0, 5, 10.0));
        graph.addEdge(d, e, new Connection(d, e, 10.0, 5, 10.0));

        // Act
        Set<Station> cycleStations = detector.findStationsInCycles(graph);

        // Assert
        assertEquals(3, cycleStations.size());
        assertTrue(cycleStations.contains(a));
        assertTrue(cycleStations.contains(b));
        assertTrue(cycleStations.contains(c));
        assertFalse(cycleStations.contains(d));
        assertFalse(cycleStations.contains(e));
    }

    /**
     * Teste 7: Grafo vazio
     * Esperado: Set vazio
     */
    @Test
    void testEmptyGraph() {
        // Arrange
        Graph<Station, Connection> graph = new MapGraph<>(true);

        // Act
        Set<Station> cycleStations = detector.findStationsInCycles(graph);

        // Assert
        assertTrue(cycleStations.isEmpty(),
                "Empty graph should have no cycles");
    }

    /**
     * Teste 8: Grafo com vértice isolado
     * A (sozinha, sem arestas)
     * Esperado: Set vazio
     */
    @Test
    void testSingleIsolatedVertex() {
        // Arrange
        Graph<Station, Connection> graph = new MapGraph<>(true);
        Station a = new Station("1", "Station A");
        graph.addVertex(a);

        // Act
        Set<Station> cycleStations = detector.findStationsInCycles(graph);

        // Assert
        assertTrue(cycleStations.isEmpty(),
                "Single vertex without edges should have no cycles");
    }
}
