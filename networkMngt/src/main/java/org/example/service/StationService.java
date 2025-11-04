package org.example.service;

import org.example.data.StationLoader;
import org.example.domain.Station;
import org.example.queries.*;
import org.example.trees.StationIndexes;

import java.io.IOException;
import java.util.*;

/**
 * Main service orchestrator for USEI06.
 * Coordinates data loading, index building, and query execution.
 *
 * This is the single entry point for all USEI06 functionality.
 */
public class StationService {
    private StationLoader loader;
    private StationIndexes indexes;
    private TimeZoneQuery timeZoneQuery;
    private CoordinateQuery coordinateQuery;
    private SampleQueries sampleQueries;

    private boolean isInitialized;
    private String currentDataSource;

    public StationService() {
        this.loader = new StationLoader();
        this.indexes = new StationIndexes();
        this.isInitialized = false;
    }

    /**
     * Initialize the service by loading data from CSV and building indexes.
     * This must be called before any queries can be executed.
     *
     * @param csvFilePath Path to the stations CSV file
     * @return true if initialization succeeded, false otherwise
     */
    public boolean initialize(String csvFilePath) {
        try {
            System.out.println("╔══════════════════════════════════════════════════════════════╗");
            System.out.println("║          USEI06 - Station Index Initialization              ║");
            System.out.println("╚══════════════════════════════════════════════════════════════╝\n");

            // Step 1: Load CSV data
            System.out.println("Step 1: Loading stations from CSV...");
            long startTime = System.currentTimeMillis();
            List<Station> stations = loader.loadFromCSV(csvFilePath);
            long loadTime = System.currentTimeMillis() - startTime;

            System.out.println(loader.getImportReport());
            System.out.println(String.format("Load time: %d ms\n", loadTime));

            if (stations.isEmpty()) {
                System.err.println("ERROR: No valid stations loaded. Cannot continue.");
                return false;
            }

            // Step 2: Build indexes
            System.out.println("Step 2: Building AVL tree indexes...");
            startTime = System.currentTimeMillis();
            Map<Station, String> rejected = indexes.buildIndexes(stations);
            long indexTime = System.currentTimeMillis() - startTime;

            System.out.println(String.format("Indexed %d stations in %d ms",
                    indexes.getTotalStations(), indexTime));

            if (!rejected.isEmpty()) {
                System.out.println(String.format("Warning: %d stations rejected during indexing",
                        rejected.size()));
            }

            // Step 3: Initialize query engines
            System.out.println("\nStep 3: Initializing query engines...");
            this.timeZoneQuery = new TimeZoneQuery(indexes);
            this.coordinateQuery = new CoordinateQuery(indexes);
            this.sampleQueries = new SampleQueries(indexes);

            // Step 4: Display performance metrics
            System.out.println("\n" + indexes.getPerformanceReport());

            this.isInitialized = true;
            this.currentDataSource = csvFilePath;

            System.out.println("✓ Initialization complete!\n");
            return true;

        } catch (IOException e) {
            System.err.println("ERROR: Failed to load CSV file: " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.err.println("ERROR: Initialization failed: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Check if service is ready for queries.
     */
    public boolean isReady() {
        return isInitialized;
    }

    /**
     * Ensure service is initialized before executing queries.
     */
    private void ensureInitialized() {
        if (!isInitialized) {
            throw new IllegalStateException(
                    "Service not initialized. Call initialize() first.");
        }
    }

    // ========== TIME ZONE QUERIES ==========

    /**
     * Query all stations in a time zone group.
     */
    public QueryResult queryByTimeZoneGroup(String timeZoneGroup) {
        ensureInitialized();
        return timeZoneQuery.queryByTimeZoneGroup(timeZoneGroup);
    }

    /**
     * Query all stations in a time zone group and country.
     */
    public QueryResult queryByTimeZoneGroupAndCountry(String timeZoneGroup, String country) {
        ensureInitialized();
        return timeZoneQuery.queryByTimeZoneGroupAndCountry(timeZoneGroup, country);
    }

    /**
     * Query stations in multiple time zone groups (window query).
     */
    public QueryResult queryByTimeZoneWindow(List<String> timeZoneGroups) {
        ensureInitialized();
        return timeZoneQuery.queryByTimeZoneWindow(timeZoneGroups);
    }

    /**
     * Get country distribution for a time zone group.
     */
    public Map<String, Long> getCountryDistribution(String timeZoneGroup) {
        ensureInitialized();
        return timeZoneQuery.getCountryDistribution(timeZoneGroup);
    }

    /**
     * Get time zone distribution across all stations.
     */
    public Map<String, Long> getTimeZoneDistribution() {
        ensureInitialized();
        return timeZoneQuery.getTimeZoneDistribution();
    }

    // ========== COORDINATE QUERIES ==========

    /**
     * Query stations within a latitude range.
     */
    public QueryResult queryByLatitudeRange(double minLat, double maxLat) {
        ensureInitialized();
        return coordinateQuery.queryByLatitudeRange(minLat, maxLat);
    }

    /**
     * Query stations within a longitude range.
     */
    public QueryResult queryByLongitudeRange(double minLon, double maxLon) {
        ensureInitialized();
        return coordinateQuery.queryByLongitudeRange(minLon, maxLon);
    }

    /**
     * Query stations within a geographic bounding box.
     */
    public QueryResult queryByBoundingBox(double minLat, double maxLat,
                                          double minLon, double maxLon) {
        ensureInitialized();
        return coordinateQuery.queryByBoundingBox(minLat, maxLat, minLon, maxLon);
    }

    /**
     * Query stations at exact coordinates (handles duplicates).
     */
    public QueryResult queryByExactCoordinates(double latitude, double longitude) {
        ensureInitialized();
        return coordinateQuery.queryByExactCoordinates(latitude, longitude);
    }

    /**
     * Query stations in bounding box filtered by country.
     */
    public QueryResult queryByBoundingBoxAndCountry(double minLat, double maxLat,
                                                    double minLon, double maxLon,
                                                    String country) {
        ensureInitialized();
        return coordinateQuery.queryByBoundingBoxAndCountry(
                minLat, maxLat, minLon, maxLon, country);
    }

    /**
     * Get latitude distribution summary.
     */
    public Map<String, Object> getLatitudeDistributionSummary() {
        ensureInitialized();
        return coordinateQuery.getLatitudeDistributionSummary();
    }

    /**
     * Get longitude distribution summary.
     */
    public Map<String, Object> getLongitudeDistributionSummary() {
        ensureInitialized();
        return coordinateQuery.getLongitudeDistributionSummary();
    }

    // ========== SAMPLE QUERIES ==========

    /**
     * Run all 5 sample queries for USEI06 demonstration.
     */
    public String runAllSampleQueries() {
        ensureInitialized();
        return sampleQueries.runAllSamples();
    }

    /**
     * Run individual sample queries.
     */
    public QueryResult runSampleQuery1() {
        ensureInitialized();
        return sampleQueries.sample1_AllCETStations();
    }

    public QueryResult runSampleQuery2() {
        ensureInitialized();
        return sampleQueries.sample2_PortugueseWETStations();
    }

    public QueryResult runSampleQuery3() {
        ensureInitialized();
        return sampleQueries.sample3_TimeZoneWindow();
    }

    public QueryResult runSampleQuery4() {
        ensureInitialized();
        return sampleQueries.sample4_IberianPeninsula();
    }

    public QueryResult runSampleQuery5() {
        ensureInitialized();
        return sampleQueries.sample5_LisbonDuplicateCoordinates();
    }

    // ========== PERFORMANCE & STATISTICS ==========

    /**
     * Get comprehensive performance report.
     */
    public String getPerformanceReport() {
        ensureInitialized();
        return indexes.getPerformanceReport();
    }

    /**
     * Get CSV import report.
     */
    public String getImportReport() {
        return loader.getImportReport();
    }

    /**
     * Get system status.
     */
    public String getSystemStatus() {
        StringBuilder sb = new StringBuilder();
        sb.append("╔══════════════════════════════════════════════════════════════╗\n");
        sb.append("║                    SYSTEM STATUS                            ║\n");
        sb.append("╚══════════════════════════════════════════════════════════════╝\n");
        sb.append(String.format("Initialized: %s\n", isInitialized ? "YES" : "NO"));

        if (isInitialized) {
            sb.append(String.format("Data source: %s\n", currentDataSource));
            sb.append(String.format("Total stations: %d\n", indexes.getTotalStations()));
            sb.append(String.format("Index build time: %d ms\n", indexes.getBuildTimeMs()));
        }

        return sb.toString();
    }

    // ========== GETTERS ==========

    public StationIndexes getIndexes() {
        return indexes;
    }

    public TimeZoneQuery getTimeZoneQuery() {
        ensureInitialized();
        return timeZoneQuery;
    }

    public CoordinateQuery getCoordinateQuery() {
        ensureInitialized();
        return coordinateQuery;
    }

    public SampleQueries getSampleQueries() {
        ensureInitialized();
        return sampleQueries;
    }
}
