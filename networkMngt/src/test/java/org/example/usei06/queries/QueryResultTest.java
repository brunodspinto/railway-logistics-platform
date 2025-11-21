package org.example.usei06.queries;

import org.example.domain.Station;
import org.example.queries.QueryResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

/**
 * Testes unitários para QueryResult - USEI06
 */
class QueryResultTest {

    private Station station1;
    private Station station2;

    @BeforeEach
    void setUp() {
        station1 = new Station("Lisboa", 38.7, -9.1, "PT", "Europe/Lisbon",
                "WET/GMT", true, true, false);
        station2 = new Station("Porto", 41.1, -8.6, "PT", "Europe/Lisbon",
                "WET/GMT", true, true, false);
    }

    @Test
    @DisplayName("Create query result")
    void testCreateQueryResult() {
        List<Station> stations = Arrays.asList(station1, station2);
        QueryResult result = new QueryResult("TEST", stations, 100L, 5);

        assertEquals("TEST", result.getType());
        assertEquals(2, result.count());
        assertEquals(100L, result.getTime());
        assertEquals(5, result.getNodesVisited());
    }

    @Test
    @DisplayName("Add and retrieve metadata")
    void testMetadata() {
        QueryResult result = new QueryResult("TEST", Arrays.asList(), 0L, 0);
        result.addMeta("key", "value");
        result.addMeta("number", 42);

        assertEquals("value", result.getMeta("key"));
        assertEquals(42, result.getMeta("number"));
    }

    @Test
    @DisplayName("Get stations returns copy")
    void testGetStations() {
        List<Station> original = Arrays.asList(station1);
        QueryResult result = new QueryResult("TEST", original, 0L, 0);

        List<Station> retrieved = result.getStations();
        assertEquals(1, retrieved.size());

        // Verify it's a defensive copy
        assertNotSame(original, retrieved);
    }

    @Test
    @DisplayName("Empty result")
    void testEmptyResult() {
        QueryResult result = new QueryResult("EMPTY", Arrays.asList(), 50L, 0);

        assertEquals(0, result.count());
        assertTrue(result.getStations().isEmpty());
    }

    @Test
    @DisplayName("ToString contains basic info")
    void testToString() {
        List<Station> stations = Arrays.asList(station1, station2);
        QueryResult result = new QueryResult("LAT_RANGE", stations, 100L, 5);

        String str = result.toString();

        assertTrue(str.contains("LAT_RANGE"));
        assertTrue(str.contains("2 stations"));
    }

    @Test
    @DisplayName("Complexity info format")
    void testComplexityInfo() {
        QueryResult result = new QueryResult("TEST", Arrays.asList(), 50L, 10);
        result.addMeta("complexity", "O(log n)");

        String info = result.complexityInfo();

        assertTrue(info.contains("O(log n)"));
        assertTrue(info.contains("10"));
        assertTrue(info.contains("50 ms"));
    }
}
