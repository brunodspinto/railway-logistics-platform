package org.example.usei15;

import org.example.usei15.result.ShortestPathResult;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ShortestPathResultTest {

    @Test
    void testSuccessfulPath() {
        // Arrange
        List<String> path = List.of("A", "B", "C");
        Map<String, Double> costs = Map.of("A", 0.0, "B", 10.0, "C", 20.0);
        double totalCost = 20.0;

        // Act
        ShortestPathResult<String> result = new ShortestPathResult<>(path, costs, totalCost, true);

        // Assert
        assertTrue(result.hasPath());
        assertEquals(path, result.getPath());
        assertEquals(totalCost, result.getTotalCost());
        assertEquals(10.0, result.getCostTo("B"));
        assertEquals(0.0, result.getCostTo("A"));
    }

    @Test
    void testNoPath() {
        // Act
        ShortestPathResult<String> result = ShortestPathResult.noPath();

        // Assert
        assertFalse(result.hasPath());
        assertTrue(result.getPath().isEmpty());
        assertEquals(Double.POSITIVE_INFINITY, result.getTotalCost());
    }

    @Test
    void testGetCostToUnknownVertex() {
        List<String> path = List.of("A");
        Map<String, Double> costs = Map.of("A", 0.0);
        ShortestPathResult<String> result = new ShortestPathResult<>(path, costs, 0.0, true);

        Double cost = result.getCostTo("Z");


        assertEquals(Double.POSITIVE_INFINITY, cost, "Deveria ser null ou Infinity para nós inalcançáveis/desconhecidos");
    }
}