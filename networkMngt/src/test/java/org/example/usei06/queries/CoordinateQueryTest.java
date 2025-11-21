package org.example.usei06.queries;

import org.example.domain.Station;
import org.example.queries.CoordinateQuery;
import org.example.queries.QueryResult;
import org.example.trees.StationIndexes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;

/**
 * Testes unitários para CoordinateQuery - USEI06
 */
class CoordinateQueryTest {

    private StationIndexes indexes;
    private CoordinateQuery query;

    @BeforeEach
    void setUp() {
        indexes = new StationIndexes();

        // Estações portuguesas
        indexes.buildIndexes(List.of(
                createStation("Lisboa Santa Apolónia", 38.71387, -9.12227, "PT", "WET/GMT"),
                createStation("Lisboa Oriente", 38.71387, -9.12227, "PT", "WET/GMT"),
                createStation("Porto Campanhã", 41.14961, -8.58519, "PT", "WET/GMT"),
                createStation("Faro", 37.01937, -7.93188, "PT", "WET/GMT"),
                createStation("Madrid Chamartín", 40.47259, -3.68273, "ES", "CET")
        ));

        query = new CoordinateQuery(indexes);
    }

    private Station createStation(String name, double lat, double lon, String country, String tzGroup) {
        return new Station(name, lat, lon, country, "Europe/" + country, tzGroup, true, true, false);
    }

    @Test
    @DisplayName("Query by latitude range returns stations in range")
    void testQueryByLatitudeRange() {
        QueryResult result = query.queryByLatitudeRange(38.0, 42.0);

        assertEquals("LATITUDE_RANGE", result.getType());
        assertEquals(4, result.getStations().size());

        for (Station station : result.getStations()) {
            assertTrue(station.getLatitude() >= 38.0);
            assertTrue(station.getLatitude() <= 42.0);
        }
    }

    @Test
    @DisplayName("Query by latitude validates bounds")
    void testQueryByLatitudeValidation() {
        assertThrows(IllegalArgumentException.class, () ->
                query.queryByLatitudeRange(-100, 0));

        assertThrows(IllegalArgumentException.class, () ->
                query.queryByLatitudeRange(50.0, 40.0));
    }

    @Test
    @DisplayName("Query by latitude handles duplicate coordinates")
    void testQueryByLatitudeDuplicates() {
        QueryResult result = query.queryByLatitudeRange(38.7, 38.8);
        List<Station> stations = result.getStations();

        long lisboaCount = stations.stream()
                .filter(s -> s.getName().startsWith("Lisboa"))
                .count();

        assertEquals(2, lisboaCount);
    }

    @Test
    @DisplayName("Query by longitude range")
    void testQueryByLongitudeRange() {
        QueryResult result = query.queryByLongitudeRange(-10.0, 0.0);

        assertTrue(result.getStations().size() >= 4);

        for (Station station : result.getStations()) {
            assertTrue(station.getLongitude() >= -10.0);
            assertTrue(station.getLongitude() <= 0.0);
        }
    }

    @Test
    @DisplayName("Query by exact coordinates")
    void testQueryByExactCoordinates() {
        QueryResult result = query.queryByExactCoordinates(38.71387, -9.12227);

        assertEquals("EXACT_COORDINATES", result.getType());
        assertEquals(2, result.getStations().size());

        // Ordenadas por nome
        assertEquals("Lisboa Oriente", result.getStations().get(0).getName());
        assertEquals("Lisboa Santa Apolónia", result.getStations().get(1).getName());
    }

    @Test
    @DisplayName("Get latitude distribution summary")
    void testLatitudeDistribution() {
        Map<String, Object> summary = query.getLatitudeDistributionSummary();

        assertEquals(5, summary.get("count"));

        double min = (double) summary.get("min");
        double max = (double) summary.get("max");
        double avg = (double) summary.get("average");

        assertTrue(min <= avg && avg <= max);
    }

    @Test
    @DisplayName("Query with no results")
    void testQueryNoResults() {
        QueryResult result = query.queryByLatitudeRange(50.0, 55.0);
        assertTrue(result.getStations().isEmpty());
    }

    @Test
    @DisplayName("Query includes metadata")
    void testQueryMetadata() {
        QueryResult result = query.queryByLatitudeRange(38.0, 42.0);

        assertEquals(38.0, result.getMeta("minLatitude"));
        assertEquals(42.0, result.getMeta("maxLatitude"));
        assertEquals("O(log n + k)", result.getMeta("complexity"));
    }

    @Test
    @DisplayName("Query with min equals max")
    void testQuerySinglePoint() {
        QueryResult result = query.queryByLatitudeRange(38.71387, 38.71387);
        assertEquals(2, result.getStations().size());
    }

    @Test
    @DisplayName("Longitude validates bounds")
    void testLongitudeValidation() {
        assertThrows(IllegalArgumentException.class, () ->
                query.queryByLongitudeRange(-200, 0));

        assertThrows(IllegalArgumentException.class, () ->
                query.queryByLongitudeRange(10.0, -10.0));
    }
}
