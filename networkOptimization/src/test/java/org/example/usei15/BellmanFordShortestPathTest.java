package org.example.usei15;

import org.example.graph.Graph;
import org.example.graph.map.MapGraph;
import org.example.usei15.algorithm.BellmanFordShortestPath;
import org.example.usei15.algorithm.NegativeCycleException;
import org.example.usei15.result.ShortestPathResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BellmanFordShortestPathTest {

    private BellmanFordShortestPath<String, Double> algorithm;
    private Graph<String, Double> graph;

    @BeforeEach
    void setUp() {
        algorithm = new BellmanFordShortestPath<>();
        // Cria um grafo direcionado simples para os testes
        graph = new MapGraph<>(true);
    }

    /**
     * Teste 1: Caminho Simples com pesos positivos
     * A -> B (cost 10)
     * B -> C (cost 5)
     * Esperado: Custo 15
     */
    @Test
    void testShortestPathSimplePositive() {
        graph.addVertex("A");
        graph.addVertex("B");
        graph.addVertex("C");

        graph.addEdge("A", "B", 10.0);
        graph.addEdge("B", "C", 5.0);

        // Função de custo é simplesmente o valor do Double da aresta
        ShortestPathResult<String> result = algorithm.shortestPath(graph, "A", "C", Double::doubleValue);

        assertTrue(result.hasPath());
        assertEquals(15.0, result.getTotalCost(), 0.001);
        assertEquals(List.of("A", "B", "C"), result.getPath());
    }

    /**
     * Teste 2: Caminho com pesos negativos (Bónus), mas SEM ciclo negativo.
     * A -> B (10)
     * A -> C (20)
     * B -> C (-5)  <-- Atalho com bónus
     *
     * Caminho A->C direto custa 20.
     * Caminho A->B->C custa 10 + (-5) = 5.
     * O algoritmo deve escolher o caminho com bónus.
     */
    @Test
    void testShortestPathWithNegativeWeightsNoCycle() {
        graph.addVertex("A");
        graph.addVertex("B");
        graph.addVertex("C");

        graph.addEdge("A", "B", 10.0);
        graph.addEdge("A", "C", 20.0);
        graph.addEdge("B", "C", -5.0);

        ShortestPathResult<String> result = algorithm.shortestPath(graph, "A", "C", Double::doubleValue);

        assertTrue(result.hasPath());
        assertEquals(5.0, result.getTotalCost(), 0.001);
        assertEquals(List.of("A", "B", "C"), result.getPath());
    }

    /**
     * Teste 3: Deteção de Ciclo Negativo
     * A -> B (1)
     * B -> C (-5)
     * C -> A (2)
     *
     * Total do ciclo: 1 - 5 + 2 = -2.
     * Deve lançar NegativeCycleException.
     */
    @Test
    void testNegativeCycleDetection() {
        graph.addVertex("A");
        graph.addVertex("B");
        graph.addVertex("C");

        graph.addEdge("A", "B", 1.0);
        graph.addEdge("B", "C", -5.0);
        graph.addEdge("C", "A", 2.0);

        // Verifica se lança a exceção específica
        NegativeCycleException exception = assertThrows(NegativeCycleException.class, () -> {
            algorithm.shortestPath(graph, "A", "C", Double::doubleValue);
        });

        // Verificações extra opcionais para garantir que o report está a ser gerado
        String report = exception.getDetailedMessage();
        System.out.println("Output de Teste (Report Gerado): " + report);

        assertNotNull(report);
        assertTrue(report.contains("Cost: -5,00") || report.contains("Cost: -5.00"), "O report deve mencionar o custo negativo");
        assertTrue(report.contains("Total Cycle Cost"), "O report deve ter o custo total");
    }

    /**
     * Teste 4: Sem Caminho (Desconexo)
     * A -> B
     * C -> D
     * Pedido: A -> D
     */
    @Test
    void testNoPathAvailable() {
        graph.addVertex("A");
        graph.addVertex("B");
        graph.addVertex("C");
        graph.addVertex("D");

        graph.addEdge("A", "B", 10.0);
        graph.addEdge("C", "D", 5.0);

        ShortestPathResult<String> result = algorithm.shortestPath(graph, "A", "D", Double::doubleValue);

        assertFalse(result.hasPath());
        assertEquals(Double.POSITIVE_INFINITY, result.getTotalCost());
        assertTrue(result.getPath().isEmpty());
    }
}