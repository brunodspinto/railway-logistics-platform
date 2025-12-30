package org.example.usei11;

import org.example.controller.UpgradePlanController;
import org.example.controller.UpgradePlanResult;
import org.example.domain.Connection;
import org.example.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.example.graph.Graph;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários para UpgradePlanController
 * Testa a orquestração completa da USEI11
 */
class UpgradePlanControllerTest {

    private static final String STATIONS_PATH = "../res/stations.csv";
    private static final String LINES_PATH = "../res/lines.csv";

    private UpgradePlanController controller;

    @BeforeEach
    void setUp() {
        controller = new UpgradePlanController();
    }

    /**
     * Teste 1: Carregamento da rede belga
     */
    @Test
    void testLoadNetwork_BelgianDataset() throws IOException {
        // Act
        controller.loadNetwork(STATIONS_PATH, LINES_PATH);

        // Assert
        Graph<Station, Connection> network = controller.getNetwork();
        assertNotNull(network, "Network should be loaded");
        assertEquals(559, network.numVertices(), "Should have 559 stations");
        // Espera 691 porque o Controller USEI11 deve usar loadNetwork(..., false)
        assertEquals(691, network.numEdges(), "Should have 691 connections (Directed Graph)");
    }

    /**
     * Teste 2: Calcular ordem de upgrade COM ciclos (dataset real)
     */
    @Test
    void testCalculateUpgradeOrder_WithCycles() throws IOException {
        // Arrange
        controller.loadNetwork(STATIONS_PATH, LINES_PATH);

        // Act
        UpgradePlanResult result = controller.calculateUpgradeOrder();

        // Assert
        assertNotNull(result, "Result should not be null");
        assertTrue(result.hasCycles(),
                "Belgian network is expected to have cycles in Directed Mode");

        // Se o grafo fosse bidirecional, seriam 559 estações em ciclo.
        // Como é direcionado, esperamos apenas os ciclos lógicos reais (125).
        assertEquals(125, result.getStationsInCycles().size(),
                "Should detect exactly 125 stations in logical cycles");

        assertNull(result.getUpgradeOrder(),
                "Order should be null when cycles exist");
        assertEquals(559, result.getNumStations());
        assertEquals(691, result.getNumConnections());
        assertTrue(result.getExecutionTimeMs() >= 0,
                "Execution time should be non-negative");
        assertEquals("O(V + E)", result.getComplexity());
    }

    /**
     * Teste 3: Exceção quando rede não foi carregada
     */
    @Test
    void testCalculateUpgradeOrder_NetworkNotLoaded() {
        // Act & Assert
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> controller.calculateUpgradeOrder(),
                "Should throw exception when network not loaded"
        );

        assertTrue(exception.getMessage().contains("not loaded"),
                "Exception message should mention network not loaded");
    }

    /**
     * Teste 4: GetNetwork antes de carregar retorna null
     */
    @Test
    void testGetNetwork_BeforeLoad() {
        // Act
        Graph<Station, Connection> network = controller.getNetwork();

        // Assert
        assertNull(network, "Network should be null before loading");
    }

    /**
     * Teste 5: GetNetwork depois de carregar retorna grafo válido
     */
    @Test
    void testGetNetwork_AfterLoad() throws IOException {
        // Arrange
        controller.loadNetwork(STATIONS_PATH, LINES_PATH);

        // Act
        Graph<Station, Connection> network = controller.getNetwork();

        // Assert
        assertNotNull(network);
        assertTrue(network.numVertices() > 0);
    }

    /**
     * Teste 6: Resultado contém estações específicas em ciclos
     * (Baseado no debug anterior: MONS, GHLIN, ERBISOEUL, etc.)
     */
    @Test
    void testCalculateUpgradeOrder_SpecificStationsInCycles() throws IOException {
        // Arrange
        controller.loadNetwork(STATIONS_PATH, LINES_PATH);

        // Act
        UpgradePlanResult result = controller.calculateUpgradeOrder();

        // Assert
        boolean foundMons = result.getStationsInCycles().stream()
                .anyMatch(s -> s.getId().equals("848"));
        boolean foundGhlin = result.getStationsInCycles().stream()
                .anyMatch(s -> s.getId().equals("462"));

        assertTrue(foundMons, "MONS (848) should be in cycles");
        assertTrue(foundGhlin, "GHLIN (462) should be in cycles");
    }

    /**
     * Teste 7: Tempo de execução é razoável (< 5 segundos)
     */
    @Test
    void testCalculateUpgradeOrder_PerformanceCheck() throws IOException {
        // Arrange
        controller.loadNetwork(STATIONS_PATH, LINES_PATH);

        // Act
        UpgradePlanResult result = controller.calculateUpgradeOrder();

        // Assert
        assertTrue(result.getExecutionTimeMs() < 5000,
                "Execution should complete in less than 5 seconds (was: "
                        + result.getExecutionTimeMs() + "ms)");
    }
}