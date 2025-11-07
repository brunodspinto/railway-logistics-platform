package org.example.trees;

import org.example.domain.Station;
import java.util.*;

public class StationIndexes {
    private final AVLTree<Double, Station> latIndex;
    private final AVLTree<Double, Station> lonIndex;
    private final AVLTree<CompositeKey, Station> tzIndex;

    private long buildTime;
    private int total;

    public StationIndexes() {
        this.latIndex = new AVLTree<>();
        this.lonIndex = new AVLTree<>();
        this.tzIndex = new AVLTree<>();
    }

    public Map<Station, String> buildIndexes(List<Station> stations) {
        long start = System.currentTimeMillis();
        Map<Station, String> rejected = new HashMap<>();
        int valid = 0;

        for (Station s : stations) {
            String err = s.getValidationError();
            if (err != null) {
                rejected.put(s, err);
                continue;
            }

            latIndex.insert(s.getLatitude(), s);
            lonIndex.insert(s.getLongitude(), s);

            CompositeKey key = new CompositeKey(s.getTimeZoneGroup(), s.getCountry());
            tzIndex.insert(key, s);

            valid++;
        }

        this.total = valid;
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
        sb.append("  Height: ").append(tzIndex.height()).append("\n");

        return sb.toString();
    }

    public int getTotalStations() {
        return total;
    }

    public long getBuildTimeMs() {
        return buildTime;
    }
}