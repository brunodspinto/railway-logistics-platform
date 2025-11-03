package org.example.queries;

import org.example.domain.Station;
import org.example.trees.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Executes time zone related queries on station indexes.
 * Implements USEI06 requirements for time zone group queries.
 */
public class TimeZoneQuery {
    private final StationIndexes indexes;

    public TimeZoneQuery(StationIndexes indexes) {
        this.indexes = indexes;
    }

    /**
     * Query all stations of a specific time zone group, ordered by country ASC.
     *
     * Example: queryByTimeZoneGroup("CET")
     * Returns all CET stations ordered by country name.
     *
     * Complexity: O(k log n) where k = number of results, n = total stations
     */
    public QueryResult queryByTimeZoneGroup(String timeZoneGroup) {
        long startTime = System.nanoTime();
        List<Station> results = new ArrayList<>();
        int nodesVisited = 0;

        // Get all entries from timeZone index
        AVLTree<CompositeKey, Station> tzIndex = indexes.getTimeZoneIndex();
        List<Station> allStations = tzIndex.inOrder();

        // Filter by time zone group
        for (Station station : allStations) {
            nodesVisited++;
            if (station.getTimeZoneGroup().equals(timeZoneGroup)) {
                results.add(station);
            }
        }

        // Sort by country ASC, then by name ASC
        results.sort(Comparator
                .comparing(Station::getCountry)
                .thenComparing(Station::getName));

        long executionTime = (System.nanoTime() - startTime) / 1_000_000;

        return new QueryResult.Builder()
                .queryType("TIME_ZONE_GROUP")
                .stations(results)
                .executionTimeMs(executionTime)
                .nodesVisited(nodesVisited)
                .metadata("timeZoneGroup", timeZoneGroup)
                .metadata("complexity", "O(k log n)")
                .build();
    }

    /**
     * Query all stations of a specific time zone group AND country.
     *
     * Example: queryByTimeZoneGroupAndCountry("WET", "PT")
     * Returns all Portuguese WET stations.
     *
     * Complexity: O(log n + k) where k = number of results
     */
    public QueryResult queryByTimeZoneGroupAndCountry(String timeZoneGroup, String country) {
        long startTime = System.nanoTime();

        CompositeKey key = new CompositeKey(timeZoneGroup, country);
        AVLTree<CompositeKey, Station> tzIndex = indexes.getTimeZoneIndex();

        List<Station> results = tzIndex.search(key);

        // Results already sorted by name (AVL node maintains sorted list)

        long executionTime = (System.nanoTime() - startTime) / 1_000_000;

        return new QueryResult.Builder()
                .queryType("TIME_ZONE_GROUP_AND_COUNTRY")
                .stations(results)
                .executionTimeMs(executionTime)
                .nodesVisited(1)  // Direct search in AVL
                .metadata("timeZoneGroup", timeZoneGroup)
                .metadata("country", country)
                .metadata("complexity", "O(log n + k)")
                .build();
    }

    /**
     * Query stations in a time zone window (multiple time zone groups).
     *
     * Example: queryByTimeZoneWindow(["CET", "WET/GMT"])
     * Returns all stations from both time zones, ordered by country ASC.
     *
     * Complexity: O(m * log n + k) where m = window size, k = results
     */
    public QueryResult queryByTimeZoneWindow(List<String> timeZoneGroups) {
        long startTime = System.nanoTime();
        List<Station> results = new ArrayList<>();
        int nodesVisited = 0;

        AVLTree<CompositeKey, Station> tzIndex = indexes.getTimeZoneIndex();

        // For each time zone in the window, get all stations
        for (String tzGroup : timeZoneGroups) {
            List<Station> allStations = tzIndex.inOrder();

            for (Station station : allStations) {
                nodesVisited++;
                if (station.getTimeZoneGroup().equals(tzGroup)) {
                    results.add(station);
                }
            }
        }

        // Remove duplicates (if any)
        results = results.stream()
                .distinct()
                .collect(Collectors.toList());

        // Sort by country ASC, then by name ASC
        results.sort(Comparator
                .comparing(Station::getCountry)
                .thenComparing(Station::getName));

        long executionTime = (System.nanoTime() - startTime) / 1_000_000;

        return new QueryResult.Builder()
                .queryType("TIME_ZONE_WINDOW")
                .stations(results)
                .executionTimeMs(executionTime)
                .nodesVisited(nodesVisited)
                .metadata("timeZoneWindow", timeZoneGroups.toString())
                .metadata("windowSize", timeZoneGroups.size())
                .metadata("complexity", "O(m * log n + k)")
                .build();
    }

    /**
     * Get summary statistics by country for a time zone group.
     */
    public Map<String, Long> getCountryDistribution(String timeZoneGroup) {
        QueryResult result = queryByTimeZoneGroup(timeZoneGroup);

        return result.getStations().stream()
                .collect(Collectors.groupingBy(
                        Station::getCountry,
                        Collectors.counting()
                ));
    }

    /**
     * Get summary statistics by time zone group.
     */
    public Map<String, Long> getTimeZoneDistribution() {
        List<Station> allStations = indexes.getTimeZoneIndex().inOrder();

        return allStations.stream()
                .collect(Collectors.groupingBy(
                        Station::getTimeZoneGroup,
                        Collectors.counting()
                ));
    }
}

