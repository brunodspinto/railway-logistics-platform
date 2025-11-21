package org.example.usei06.queries;

import org.example.domain.Station;
import org.example.queries.QueryResult;
import org.example.queries.TimeZoneQuery;
import org.example.trees.StationIndexes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Testes unitários para TimeZoneQuery - USEI06
 */
class TimeZoneQueryTest {

    private StationIndexes indexes;
    private TimeZoneQuery query;

    @BeforeEach
    void setUp() {
        indexes = new StationIndexes();

        indexes.buildIndexes(List.of(
                createStation("Lisboa Oriente", 38.71387, -9.12227, "PT", "WET/GMT"),
                createStation("Porto Campanhã", 41.14961, -8.58519, "PT", "WET/GMT"),
                createStation("Madrid Chamartín", 40.47259, -3.68273, "ES", "CET"),
                createStation("Barcelona Sants", 41.37926, 2.14034, "ES", "CET"),
                createStation("Paris Gare de Lyon", 48.84410, 2.37345, "FR", "CET"),
                createStation("Athens", 37.98344, 23.72806, "GR", "EET")
        ));

        query = new TimeZoneQuery(indexes);
    }

    private Station createStation(String name, double lat, double lon, String country, String tzGroup) {
        return new Station(name, lat, lon, country, "Europe/" + country, tzGroup, true, true, false);
    }

    @Test
    @DisplayName("Query by timezone group returns all stations")
    void testQueryByTimeZoneGroup() {
        QueryResult result = query.queryByTimeZoneGroup("WET/GMT");

        assertEquals("TIME_ZONE_GROUP", result.getType());
        assertEquals(2, result.getStations().size());

        assertTrue(result.getStations().stream()
                .allMatch(s -> s.getTimeZoneGroup().equals("WET/GMT")));
    }

    @Test
    @DisplayName("Query by timezone returns sorted by country then name")
    void testQueryByTimeZoneGroupSorting() {
        QueryResult result = query.queryByTimeZoneGroup("CET");
        List<Station> stations = result.getStations();

        for (int i = 0; i < stations.size() - 1; i++) {
            Station current = stations.get(i);
            Station next = stations.get(i + 1);

            int countryCompare = current.getCountry().compareTo(next.getCountry());
            if (countryCompare == 0) {
                assertTrue(current.getName().compareTo(next.getName()) <= 0);
            }
        }
    }

    @Test
    @DisplayName("Query by timezone and country")
    void testQueryByTimeZoneGroupAndCountry() {
        QueryResult result = query.queryByTimeZoneGroupAndCountry("CET", "ES");

        assertEquals("TIME_ZONE_COUNTRY", result.getType());
        assertEquals(2, result.getStations().size());

        assertTrue(result.getStations().stream()
                .allMatch(s -> s.getTimeZoneGroup().equals("CET") && s.getCountry().equals("ES")));
    }

    @Test
    @DisplayName("Query by timezone window")
    void testQueryByTimeZoneWindow() {
        List<String> window = Arrays.asList("WET/GMT", "CET");
        QueryResult result = query.queryByTimeZoneWindow(window);

        assertEquals("TIME_ZONE_WINDOW", result.getType());
        assertEquals(5, result.getStations().size());
    }

    @Test
    @DisplayName("Query by timezone window removes duplicates")
    void testQueryByTimeZoneWindowDuplicates() {
        List<String> window = Arrays.asList("WET/GMT", "WET/GMT");
        QueryResult result = query.queryByTimeZoneWindow(window);

        assertEquals(2, result.getStations().size());
    }

    @Test
    @DisplayName("Get country distribution for timezone")
    void testGetCountryDistribution() {
        Map<String, Long> distribution = query.getCountryDistribution("CET");

        assertEquals(2, distribution.size());
        assertEquals(2L, distribution.get("ES"));
        assertEquals(1L, distribution.get("FR"));
    }

    @Test
    @DisplayName("Get timezone distribution")
    void testGetTimeZoneDistribution() {
        Map<String, Long> distribution = query.getTimeZoneDistribution();

        assertEquals(3, distribution.size());
        assertEquals(2L, distribution.get("WET/GMT"));
        assertEquals(3L, distribution.get("CET"));
        assertEquals(1L, distribution.get("EET"));
    }

    @Test
    @DisplayName("Query with no results")
    void testQueryNoResults() {
        QueryResult result = query.queryByTimeZoneGroup("PST");
        assertTrue(result.getStations().isEmpty());
    }

    @Test
    @DisplayName("Query includes metadata")
    void testQueryMetadata() {
        QueryResult result = query.queryByTimeZoneGroup("WET/GMT");

        assertEquals("WET/GMT", result.getMeta("timeZoneGroup"));
        assertEquals("O(k log n)", result.getMeta("complexity"));
        assertTrue(result.getTime() >= 0);
    }

    @Test
    @DisplayName("Window query with empty list")
    void testWindowQueryEmpty() {
        List<String> window = Arrays.asList();
        QueryResult result = query.queryByTimeZoneWindow(window);

        assertTrue(result.getStations().isEmpty());
    }
}
