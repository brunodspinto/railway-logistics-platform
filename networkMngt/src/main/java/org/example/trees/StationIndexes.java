package org.example.trees;

import org.example.domain.Station;
import java.util.*;

/**
 * Container for the three AVL indexes required by USEI06:
 * 1. Latitude index
 * 2. Longitude index
 * 3. TimeZone + Country index
 *
 * This class encapsulates all indexes and provides build functionality.
 */
public class StationIndexes {
    private final AVLTree<Double, Station> latitudeIndex;
    private final AVLTree<Double, Station> longitudeIndex;
    private final AVLTree<CompositeKey, Station> timeZoneIndex;

    private long buildTimeMs;
    private int totalStations;

    public StationIndexes() {
        this.latitudeIndex = new AVLTree<>();
        this.longitudeIndex = new AVLTree<>();
        this.timeZoneIndex = new AVLTree<>();
    }

    /**
     * Build all three indexes from list of stations.
     * Validates stations and rejects invalid ones.
     *
     * @param stations List of stations to index
     * @return Map of rejected stations (Station -> error message)
     */
    public Map<Station, String> buildIndexes(List<Station> stations) {
        long startTime = System.currentTimeMillis();
        Map<Station, String> rejected = new HashMap<>();
        int validCount = 0;

        for (Station station : stations) {
            String error = station.getValidationError();
            if (error != null) {
                rejected.put(station, error);
                continue;
            }

            // Insert into all three indexes
            latitudeIndex.insert(station.getLatitude(), station);
            longitudeIndex.insert(station.getLongitude(), station);

            CompositeKey tzKey = new CompositeKey(
                    station.getTimeZoneGroup(),
                    station.getCountry()
            );
            timeZoneIndex.insert(tzKey, station);

            validCount++;
        }

        this.totalStations = validCount;
        this.buildTimeMs = System.currentTimeMillis() - startTime;

        return rejected;
    }

    // Getters for indexes
    public AVLTree<Double, Station> getLatitudeIndex() {
        return latitudeIndex;
    }

    public AVLTree<Double, Station> getLongitudeIndex() {
        return longitudeIndex;
    }

    public AVLTree<CompositeKey, Station> getTimeZoneIndex() {
        return timeZoneIndex;
    }

    /**
     * Get performance metrics for all indexes.
     */
    public String getPerformanceReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== STATION INDEXES PERFORMANCE ===\n");
        sb.append(String.format("Total stations indexed: %d\n", totalStations));
        sb.append(String.format("Build time: %d ms\n\n", buildTimeMs));

        sb.append("Latitude Index:\n");
        sb.append("  ").append(latitudeIndex.getComplexityAnalysis()).append("\n");
        sb.append("  Bucket sizes: ").append(latitudeIndex.getBucketSizeDistribution()).append("\n\n");

        sb.append("Longitude Index:\n");
        sb.append("  ").append(longitudeIndex.getComplexityAnalysis()).append("\n");
        sb.append("  Bucket sizes: ").append(longitudeIndex.getBucketSizeDistribution()).append("\n\n");

        sb.append("TimeZone Index:\n");
        sb.append("  ").append(timeZoneIndex.getComplexityAnalysis()).append("\n");
        sb.append("  Bucket sizes: ").append(timeZoneIndex.getBucketSizeDistribution()).append("\n");

        return sb.toString();
    }

    public int getTotalStations() {
        return totalStations;
    }

    public long getBuildTimeMs() {
        return buildTimeMs;
    }
}

