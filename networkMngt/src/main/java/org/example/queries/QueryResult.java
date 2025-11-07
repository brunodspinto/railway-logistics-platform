package org.example.queries;

import org.example.domain.Station;
import java.util.*;

public class QueryResult {
    private List<Station> stations;
    private long timeMs;
    private int nodesVisited;
    private String type;
    private Map<String, Object> meta;

    public QueryResult(String type, List<Station> stations, long timeMs, int nodes) {
        this.type = type;
        this.stations = new ArrayList<>(stations);
        this.timeMs = timeMs;
        this.nodesVisited = nodes;
        this.meta = new HashMap<>();
    }

    public void addMeta(String key, Object value) {
        meta.put(key, value);
    }

    public List<Station> getStations() {
        return stations;
    }

    public int count() {
        return stations.size();
    }

    public long getTime() {
        return timeMs;
    }

    public int getNodesVisited() {
        return nodesVisited;
    }

    public String getType() {
        return type;
    }

    public Object getMeta(String key) {
        return meta.get(key);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Query: ").append(type).append("\n");
        sb.append("Results: ").append(stations.size()).append(" stations\n");

        if (!stations.isEmpty()) {
            sb.append("\nFirst 10:\n");
            stations.stream().limit(10)
                    .forEach(s -> sb.append("  ").append(s).append("\n"));

            if (stations.size() > 10) {
                sb.append(String.format("  ... %d more\n", stations.size() - 10));
            }
        }

        return sb.toString();
    }

    public String complexityInfo() {
        String comp = meta.getOrDefault("complexity", "O(?)").toString();
        return String.format("Complexity: %s | Nodes: %d | Time: %d ms",
                comp, nodesVisited, timeMs);
    }
}