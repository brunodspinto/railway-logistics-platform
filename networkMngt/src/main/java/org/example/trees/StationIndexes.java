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

        for (Station station : stations) {
            String error = station.getValidationError();
            if (error != null) {
                rejected.put(station, error);
                continue;
            }
            validStations.add(station);
        }

        for (Station station : validStations) {
            latIndex.insert(station.getLatitude(), station);
            lonIndex.insert(station.getLongitude(), station);

            CompositeKey key = new CompositeKey(station.getTimeZoneGroup(), station.getCountry());
            tzIndex.insert(key, station);
        }

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

        sb.append("\n══════════════════════════════════════════════════════════\n");
        sb.append(String.format("              INDEX REPORT (N=%d)              \n", total));
        sb.append("══════════════════════════════════════════════════════════\n");
        sb.append(String.format(" Total Build Time    : %d ms\n", buildTime));
        sb.append("──────────────────────────────────────────────────────────\n");

        sb.append(String.format(" %-15s | %-10s | %-8s | %-10s\n", "AVL Index", "Nodes", "Height", "Target Height"));
        sb.append("──────────────────────────────────────────────────────────\n");

        appendIndexRow(sb, "Latitude", latIndex.size(), latIndex.height());
        appendIndexRow(sb, "Longitude", lonIndex.size(), lonIndex.height());
        appendIndexRow(sb, "TimeZone", tzIndex.size(), tzIndex.height());

        sb.append("══════════════════════════════════════════════════════════\n");

        sb.append(" SPATIAL INDEX (2D-Tree)\n");
        sb.append("──────────────────────────────────────────────────────────\n");
        sb.append(String.format(" Size (Nodes)        : %d\n", spatialIndex.size()));

        double optH = log2(spatialIndex.size());
        sb.append(String.format(" Height              : %d (Target ~%.1f)\n", spatialIndex.height(), optH));

        List<Integer> buckets = new ArrayList<>(spatialIndex.getDistinctBucketSizes());
        Collections.sort(buckets);
        sb.append(String.format(" Buckets (Distinct)  : %s\n", buckets.toString()));
        sb.append("══════════════════════════════════════════════════════════\n");

        return sb.toString();
    }

    private void appendIndexRow(StringBuilder sb, String name, int size, int height) {
        double opt = log2(size);
        sb.append(String.format(" %-15s | %-10d | %-8d | ~%-10.1f\n", name, size, height, opt));
    }


    private double log2(int n) {
        return n > 0 ? Math.log(n) / Math.log(2) : 0;
    }

    public int getTotalStations() {
        return total;
    }

    public long getBuildTimeMs() {
        return buildTime;
    }
}