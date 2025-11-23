package org.example.usei06.trees;

import org.example.domain.Station;
import org.example.trees.AVLTree;
import org.example.trees.CompositeKey;
import org.example.trees.StationIndexes;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Testes unitários para StationIndexes - USEI06
 */
class StationIndexesTest {

    private StationIndexes indexes;
    private Station validStation;
    private Station invalidStation;

    @BeforeEach
    void setUp() {
        indexes = new StationIndexes();

        validStation = new Station("Lisboa", 38.7, -9.1, "PT",
                "Europe/Lisbon", "WET/GMT", true, true, false);

        // Invalid: latitude out of bounds
        invalidStation = new Station("Invalid", 100.0, -9.1, "PT",
                "Europe/Lisbon", "WET/GMT", true, true, false);
    }

    @Test
    @DisplayName("Build indexes with valid stations")
    void testBuildIndexes() {
        List<Station> stations = Arrays.asList(validStation);
        Map<Station, String> rejected = indexes.buildIndexes(stations);

        assertTrue(rejected.isEmpty());
        assertEquals(1, indexes.getTotalStations());
        assertTrue(indexes.getBuildTimeMs() >= 0);
    }

    @Test
    @DisplayName("Build indexes rejects invalid stations")
    void testBuildIndexesRejectsInvalid() {
        List<Station> stations = Arrays.asList(validStation, invalidStation);
        Map<Station, String> rejected = indexes.buildIndexes(stations);

        assertEquals(1, rejected.size());
        assertTrue(rejected.containsKey(invalidStation));
        assertEquals(1, indexes.getTotalStations());
    }

    @Test
    @DisplayName("Get latitude index")
    void testGetLatitudeIndex() {
        List<Station> stations = Arrays.asList(validStation);
        indexes.buildIndexes(stations);

        AVLTree<Double, Station> latIndex = indexes.getLatitudeIndex();
        assertNotNull(latIndex);
        assertEquals(1, latIndex.size());
    }

    @Test
    @DisplayName("Get longitude index")
    void testGetLongitudeIndex() {
        List<Station> stations = Arrays.asList(validStation);
        indexes.buildIndexes(stations);

        AVLTree<Double, Station> lonIndex = indexes.getLongitudeIndex();
        assertNotNull(lonIndex);
        assertEquals(1, lonIndex.size());
    }

    @Test
    @DisplayName("Get timezone index")
    void testGetTimeZoneIndex() {
        List<Station> stations = Arrays.asList(validStation);
        indexes.buildIndexes(stations);

        AVLTree<CompositeKey, Station> tzIndex = indexes.getTimeZoneIndex();
        assertNotNull(tzIndex);
        assertEquals(1, tzIndex.size());
    }

    @Test
    @DisplayName("Get report contains statistics")
    void testGetReport() {
        List<Station> stations = Arrays.asList(validStation);
        indexes.buildIndexes(stations);

        String report = indexes.getReport();

        assertTrue(report.contains("INDEX REPORT"), "Report must contain title 'INDEX REPORT'");

        assertTrue(report.contains("AVL Index"), "Must contain 'AVL Index' header");
        assertTrue(report.contains("Latitude"), "Must list Latitude index");
        assertTrue(report.contains("Longitude"), "Must list Longitude index");
        assertTrue(report.contains("TimeZone"), "Must list TimeZone index");

        assertTrue(report.contains("Target Height"), "Must show the Target Height column");

        assertTrue(report.contains("SPATIAL INDEX"), "Must contain the 2D-Tree section");
    }
}
