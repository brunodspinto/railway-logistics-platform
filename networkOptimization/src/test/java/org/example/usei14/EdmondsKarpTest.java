package org.example.usei14;

import org.example.algorithms.EdmondsKarp;
import org.example.domain.Connection;
import org.example.graph.Edge;
import org.example.graph.Graph;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EdmondsKarpTest {

    private EdmondsKarp<String, Connection> algorithm;

    @Mock
    private Graph<String, Connection> graph;

    @BeforeEach
    void setUp() {
        algorithm = new EdmondsKarp<>();
    }

    @Test
    void testComputeMaxFlow_NullInputs() {
        assertThrows(IllegalArgumentException.class, () -> algorithm.computeMaxFlow(null, "A", "B"));
        assertThrows(IllegalArgumentException.class, () -> algorithm.computeMaxFlow(graph, null, "B"));
        assertThrows(IllegalArgumentException.class, () -> algorithm.computeMaxFlow(graph, "A", null));
    }

    @Test
    void testComputeMaxFlow_SourceEqualsSink() {
        lenient().when(graph.validVertex("A")).thenReturn(true);
        double result = algorithm.computeMaxFlow(graph, "A", "A");
        assertEquals(0.0, result);
    }

    @Test
    void testComputeMaxFlow_InvalidVertices() {
        lenient().when(graph.validVertex("A")).thenReturn(true);
        lenient().when(graph.validVertex("B")).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> algorithm.computeMaxFlow(graph, "A", "B"));
    }

    @Test
    void testComputeMaxFlow_SimplePath() {
        String source = "A";
        String sink = "B";

        setupGraphMock(Arrays.asList(source, sink));
        mockEdge(source, sink, 10.0);

        double result = algorithm.computeMaxFlow(graph, source, sink);
        assertEquals(10.0, result);
    }

    @Test
    void testComputeMaxFlow_TwoPaths() {
        String source = "A";
        String sink = "B";
        String mid = "C";

        setupGraphMock(Arrays.asList(source, sink, mid));

        Edge<String, Connection> e1 = createMockEdge("A", "B", 10.0);
        Edge<String, Connection> e2 = createMockEdge("A", "C", 5.0);
        Edge<String, Connection> e3 = createMockEdge("C", "B", 5.0);

        // CORREÇÃO: Criar explicitamente um ArrayList
        ArrayList<Edge<String, Connection>> edges = new ArrayList<>();
        edges.add(e1);
        edges.add(e2);
        edges.add(e3);

        lenient().doReturn(edges).when(graph).edges();

        double result = algorithm.computeMaxFlow(graph, source, sink);
        assertEquals(15.0, result);
    }

    @Test
    void testComputeMaxFlow_Disconnected() {
        setupGraphMock(Arrays.asList("A", "B", "C", "D"));

        Edge<String, Connection> e1 = createMockEdge("A", "B", 10.0);
        Edge<String, Connection> e2 = createMockEdge("C", "D", 10.0);

        ArrayList<Edge<String, Connection>> edges = new ArrayList<>();
        edges.add(e1);
        edges.add(e2);

        lenient().doReturn(edges).when(graph).edges();

        double result = algorithm.computeMaxFlow(graph, "A", "D");
        assertEquals(0.0, result);
    }

    // --- Helpers ---

    private void setupGraphMock(List<String> vertices) {
        // CORREÇÃO: Envolver a lista num new ArrayList<>()
        // O Mockito precisa que o tipo de retorno seja exatamente ArrayList, não apenas List
        ArrayList<String> verticesList = new ArrayList<>(vertices);

        lenient().doReturn(verticesList).when(graph).vertices();

        for (String v : vertices) {
            lenient().when(graph.validVertex(v)).thenReturn(true);
        }
    }

    private void mockEdge(String u, String v, double capacity) {
        Edge<String, Connection> edge = createMockEdge(u, v, capacity);

        // CORREÇÃO: Criar explicitamente um ArrayList
        ArrayList<Edge<String, Connection>> edgeList = new ArrayList<>();
        edgeList.add(edge);

        lenient().doReturn(edgeList).when(graph).edges();
    }

    private Edge<String, Connection> createMockEdge(String u, String v, double capacity) {
        Edge<String, Connection> edge = mock(Edge.class);
        Connection conn = mock(Connection.class);

        lenient().when(edge.getVOrig()).thenReturn(u);
        lenient().when(edge.getVDest()).thenReturn(v);
        lenient().when(edge.getWeight()).thenReturn(conn);
        lenient().when(conn.getCapacity()).thenReturn(capacity);

        return edge;
    }
}