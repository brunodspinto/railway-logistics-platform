package org.example.trees;

import org.example.domain.Station;
import java.util.*;

public class StationIndexes {
    private final AVLTree<Double, Station> latIndex;
    private final AVLTree<Double, Station> lonIndex;
    private final AVLTree<CompositeKey, Station> tzIndex;

    private final TwoDTree spatialIndex;

    private long buildTime;
    private int total;

    public StationIndexes() {
        this.latIndex = new AVLTree<>();
        this.lonIndex = new AVLTree<>();
        this.tzIndex = new AVLTree<>();
        this.spatialIndex = new TwoDTree();
    }

    /**
     * Build all indexes (AVL trees + 2D-tree).
     *
     * @param stations list of stations to index
     * @return map of rejected stations with error messages
     */
    public Map<Station, String> buildIndexes(List<Station> stations) {
        long start = System.currentTimeMillis();
        Map<Station, String> rejected = new HashMap<>();

        List<Station> validStations = new ArrayList<>();

        // Separate valid from invalid stations
        for (Station s : stations) {
            String err = s.getValidationError();
            if (err != null) {
                rejected.put(s, err);
                continue;
            }
            validStations.add(s);
        }

        // Build USEI06 indexes (AVL trees)
        for (Station s : validStations) {
            latIndex.insert(s.getLatitude(), s);
            lonIndex.insert(s.getLongitude(), s);

            CompositeKey key = new CompositeKey(s.getTimeZoneGroup(), s.getCountry());
            tzIndex.insert(key, s);
        }

        // Build 2D-tree silently (for USEI07)
        // Using AVL in-order for efficient pre-sorted construction
        List<Station> stationsSortedByLat = latIndex.inOrder();
        List<Station> stationsSortedByLon = lonIndex.inOrder();
        spatialIndex.build(stationsSortedByLat, stationsSortedByLon);

        this.total = validStations.size();
        this.buildTime = System.currentTimeMillis() - start;

        return rejected;
    }

    public AVLTree<Double, Station> getLatitudeIndex() {
        return latIndex;
    }

    public AVLTree<Double, Station> getLongitudeIndex() {
        return lonIndex;
    }

    public AVLTree<CompositeKey, Station> getTimeZoneIndex() {
        return tzIndex;
    }

    public TwoDTree getSpatialIndex() {
        return spatialIndex;
    }

    public String getReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("Index Statistics\n");
        sb.append(String.format("Stations: %d | Build time: %d ms\n\n", total, buildTime));

        sb.append("Latitude Index:\n");
        sb.append("  Size: ").append(latIndex.size()).append(" nodes\n");
        sb.append("  Height: ").append(latIndex.height()).append("\n\n");

        sb.append("Longitude Index:\n");
        sb.append("  Size: ").append(lonIndex.size()).append(" nodes\n");
        sb.append("  Height: ").append(lonIndex.height()).append("\n\n");

        sb.append("TimeZone Index:\n");
        sb.append("  Size: ").append(tzIndex.size()).append(" nodes\n");
        sb.append("  Height: ").append(tzIndex.height()).append("\n\n");

        sb.append("Spatial Index (2D-Tree):\n");
        sb.append("  Size (Nodes): ").append(spatialIndex.size()).append("\n");
        sb.append("  Height: ").append(spatialIndex.height()).append("\n");
        sb.append("  Distinct Bucket Sizes: ").append(spatialIndex.getDistinctBucketSizes().toString()).append("\n");

        return sb.toString();
    }

    public int getTotalStations() {
        return total;
    }

    public long getBuildTimeMs() {
        return buildTime;
    }
}