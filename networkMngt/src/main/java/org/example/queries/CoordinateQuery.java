package org.example.queries;

import org.example.domain.Station;
import org.example.trees.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Executes coordinate-based queries on station indexes.
 * Implements USEI06 requirements for latitude/longitude queries.
 */
public class CoordinateQuery {
    private final StationIndexes indexes;

    public CoordinateQuery(StationIndexes indexes) {
        this.indexes = indexes;
    }

    /**
     * Query all stations within a latitude range [minLat, maxLat].
     *
     * Example: queryByLatitudeRange(38.0, 42.0)
     * Returns all stations between latitudes 38°N and 42°N.
     *
     * Complexity: O(log n + k) where k = number of results
     */
    public QueryResult queryByLatitudeRange(double minLat, double maxLat) {
        long startTime = System.nanoTime();

        // Validate input
        if (minLat < -90 || minLat > 90 || maxLat < -90 || maxLat > 90) {
            throw new IllegalArgumentException("Latitude must be in range [-90, 90]");
        }
        if (minLat > maxLat) {
            throw new IllegalArgumentException("minLat must be <= maxLat");
        }

        AVLTree<Double, Station> latIndex = indexes.getLatitudeIndex();
        List<Station> results = latIndex.rangeSearch(minLat, maxLat);

        // Sort by latitude ASC, then by name ASC
        results.sort(Comparator
                .comparing(Station::getLatitude)
                .thenComparing(Station::getName));

        long executionTime = (System.nanoTime() - startTime) / 1_000_000;

        return new QueryResult.Builder()
                .queryType("LATITUDE_RANGE")
                .stations(results)
                .executionTimeMs(executionTime)
                .nodesVisited(results.size())  // Approximation
                .metadata("minLatitude", minLat)
                .metadata("maxLatitude", maxLat)
                .metadata("complexity", "O(log n + k)")
                .build();
    }

    /**
     * Query all stations within a longitude range [minLon, maxLon].
     *
     * Example: queryByLongitudeRange(-10.0, 0.0)
     * Returns all stations between longitudes 10°W and 0°.
     *
     * Complexity: O(log n + k) where k = number of results
     */
    public QueryResult queryByLongitudeRange(double minLon, double maxLon) {
        long startTime = System.nanoTime();

        // Validate input
        if (minLon < -180 || minLon > 180 || maxLon < -180 || maxLon > 180) {
            throw new IllegalArgumentException("Longitude must be in range [-180, 180]");
        }
        if (minLon > maxLon) {
            throw new IllegalArgumentException("minLon must be <= maxLon");
        }

        AVLTree<Double, Station> lonIndex = indexes.getLongitudeIndex();
        List<Station> results = lonIndex.rangeSearch(minLon, maxLon);

        // Sort by longitude ASC, then by name ASC
        results.sort(Comparator
                .comparing(Station::getLongitude)
                .thenComparing(Station::getName));

        long executionTime = (System.nanoTime() - startTime) / 1_000_000;

        return new QueryResult.Builder()
                .queryType("LONGITUDE_RANGE")
                .stations(results)
                .executionTimeMs(executionTime)
                .nodesVisited(results.size())  // Approximation
                .metadata("minLongitude", minLon)
                .metadata("maxLongitude", maxLon)
                .metadata("complexity", "O(log n + k)")
                .build();
    }

    /**
     * Query all stations within a bounding box (rectangular geographic area).
     *
     * Example: queryByBoundingBox(38.0, 42.0, -10.0, -5.0)
     * Returns all stations in the rectangle defined by these coordinates.
     *
     * Complexity: O(log n + k) where k = number of results
     */
    public QueryResult queryByBoundingBox(double minLat, double maxLat,
                                          double minLon, double maxLon) {
        long startTime = System.nanoTime();

        // Get stations in latitude range
        List<Station> latResults = indexes.getLatitudeIndex()
                .rangeSearch(minLat, maxLat);

        // Filter by longitude range
        List<Station> results = latResults.stream()
                .filter(s -> s.getLongitude() >= minLon && s.getLongitude() <= maxLon)
                .sorted(Comparator
                        .comparing(Station::getLatitude)
                        .thenComparing(Station::getLongitude)
                        .thenComparing(Station::getName))
                .collect(Collectors.toList());

        long executionTime = (System.nanoTime() - startTime) / 1_000_000;

        return new QueryResult.Builder()
                .queryType("BOUNDING_BOX")
                .stations(results)
                .executionTimeMs(executionTime)
                .nodesVisited(latResults.size())
                .metadata("minLatitude", minLat)
                .metadata("maxLatitude", maxLat)
                .metadata("minLongitude", minLon)
                .metadata("maxLongitude", maxLon)
                .metadata("complexity", "O(log n + k)")
                .build();
    }

    /**
     * Query stations at exact coordinates.
     * Handles duplicate coordinates (e.g., Lisboa Santa Apolónia & Oriente).
     *
     * Example: queryByExactCoordinates(38.71387, -9.122271)
     * Returns all stations at these exact coordinates (sorted by name).
     *
     * Complexity: O(log n)
     */
    public QueryResult queryByExactCoordinates(double latitude, double longitude) {
        long startTime = System.nanoTime();

        // Search by latitude first
        AVLTree<Double, Station> latIndex = indexes.getLatitudeIndex();
        List<Station> latResults = latIndex.search(latitude);

        // Filter by exact longitude
        List<Station> results = latResults.stream()
                .filter(s -> Math.abs(s.getLongitude() - longitude) < 0.000001)
                .sorted(Comparator.comparing(Station::getName))
                .collect(Collectors.toList());

        long executionTime = (System.nanoTime() - startTime) / 1_000_000;

        return new QueryResult.Builder()
                .queryType("EXACT_COORDINATES")
                .stations(results)
                .executionTimeMs(executionTime)
                .nodesVisited(1)
                .metadata("latitude", latitude)
                .metadata("longitude", longitude)
                .metadata("complexity", "O(log n)")
                .build();
    }

    /**
     * Query stations by country within a coordinate range.
     * Combines geographic and country filters.
     *
     * Complexity: O(log n + k) where k = number of results
     */
    public QueryResult queryByBoundingBoxAndCountry(double minLat, double maxLat,
                                                    double minLon, double maxLon,
                                                    String country) {
        long startTime = System.nanoTime();

        // Get bounding box results first
        QueryResult boxResult = queryByBoundingBox(minLat, maxLat, minLon, maxLon);

        // Filter by country
        List<Station> results = boxResult.getStations().stream()
                .filter(s -> s.getCountry().equalsIgnoreCase(country))
                .collect(Collectors.toList());

        long executionTime = (System.nanoTime() - startTime) / 1_000_000;

        return new QueryResult.Builder()
                .queryType("BOUNDING_BOX_AND_COUNTRY")
                .stations(results)
                .executionTimeMs(executionTime)
                .nodesVisited(boxResult.getNodesVisited())
                .metadata("minLatitude", minLat)
                .metadata("maxLatitude", maxLat)
                .metadata("minLongitude", minLon)
                .metadata("maxLongitude", maxLon)
                .metadata("country", country)
                .metadata("complexity", "O(log n + k)")
                .build();
    }

    /**
     * Get latitude distribution summary.
     */
    public Map<String, Object> getLatitudeDistributionSummary() {
        List<Station> allStations = indexes.getLatitudeIndex().inOrder();

        DoubleSummaryStatistics stats = allStations.stream()
                .mapToDouble(Station::getLatitude)
                .summaryStatistics();

        Map<String, Object> summary = new HashMap<>();
        summary.put("count", stats.getCount());
        summary.put("min", stats.getMin());
        summary.put("max", stats.getMax());
        summary.put("average", stats.getAverage());

        return summary;
    }

    /**
     * Get longitude distribution summary.
     */
    public Map<String, Object> getLongitudeDistributionSummary() {
        List<Station> allStations = indexes.getLongitudeIndex().inOrder();

        DoubleSummaryStatistics stats = allStations.stream()
                .mapToDouble(Station::getLongitude)
                .summaryStatistics();

        Map<String, Object> summary = new HashMap<>();
        summary.put("count", stats.getCount());
        summary.put("min", stats.getMin());
        summary.put("max", stats.getMax());
        summary.put("average", stats.getAverage());

        return summary;
    }
}

