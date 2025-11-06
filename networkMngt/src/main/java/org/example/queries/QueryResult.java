package org.example.queries;

import org.example.domain.Station;
import java.util.*;

/**
 * Data Transfer Object for query results.
 * Contains stations found, execution metrics, and complexity analysis.
 */
public class QueryResult {
    private final List<Station> stations;
    private final long executionTimeMs;
    private final int nodesVisited;
    private final String queryType;
    private final Map<String, Object> metadata;

    private QueryResult(Builder builder) {
        this.stations = Collections.unmodifiableList(new ArrayList<>(builder.stations));
        this.executionTimeMs = builder.executionTimeMs;
        this.nodesVisited = builder.nodesVisited;
        this.queryType = builder.queryType;
        this.metadata = Collections.unmodifiableMap(new HashMap<>(builder.metadata));
    }

    // Getters
    public List<Station> getStations() {
        return stations;
    }

    public int getStationCount() {
        return stations.size();
    }

    public long getExecutionTimeMs() {
        return executionTimeMs;
    }

    public int getNodesVisited() {
        return nodesVisited;
    }

    public String getQueryType() {
        return queryType;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public Object getMetadata(String key) {
        return metadata.get(key);
    }

    /**
     * Format results as a detailed report.
     */
    public String toReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== QUERY RESULT: ").append(queryType).append(" ===\n");
        sb.append(String.format("Stations found: %d\n", stations.size()));
        sb.append(String.format("Execution time: %d ms\n", executionTimeMs));
        sb.append(String.format("Nodes visited: %d\n", nodesVisited));

        if (!metadata.isEmpty()) {
            sb.append("\n--- Metadata ---\n");
            metadata.forEach((key, value) ->
                    sb.append(String.format("%s: %s\n", key, value)));
        }

        if (!stations.isEmpty()) {
            sb.append("\n--- Stations (first 10) ---\n");
            stations.stream()
                    .limit(10)
                    .forEach(s -> sb.append(s).append("\n"));

            if (stations.size() > 10) {
                sb.append(String.format("... and %d more stations\n", stations.size() - 10));
            }
        }

        return sb.toString();
    }

    /**
     * Get temporal complexity analysis.
     */
    public String getComplexityAnalysis() {
        String baseComplexity = metadata.getOrDefault("complexity", "O(?)").toString();
        return String.format("Temporal Complexity: %s | Nodes visited: %d | Time: %d ms",
                baseComplexity, nodesVisited, executionTimeMs);
    }

    @Override
    public String toString() {
        return String.format("QueryResult[type=%s, count=%d, time=%dms]",
                queryType, stations.size(), executionTimeMs);
    }

    // Builder pattern
    public static class Builder {
        private List<Station> stations = new ArrayList<>();
        private long executionTimeMs;
        private int nodesVisited;
        private String queryType;
        private Map<String, Object> metadata = new HashMap<>();

        public Builder queryType(String queryType) {
            this.queryType = queryType;
            return this;
        }

        public Builder stations(List<Station> stations) {
            this.stations = stations;
            return this;
        }

        public Builder addStation(Station station) {
            this.stations.add(station);
            return this;
        }

        public Builder executionTimeMs(long executionTimeMs) {
            this.executionTimeMs = executionTimeMs;
            return this;
        }

        public Builder nodesVisited(int nodesVisited) {
            this.nodesVisited = nodesVisited;
            return this;
        }

        public Builder metadata(String key, Object value) {
            this.metadata.put(key, value);
            return this;
        }

        public Builder metadata(Map<String, Object> metadata) {
            this.metadata.putAll(metadata);
            return this;
        }

        public QueryResult build() {
            if (queryType == null) {
                queryType = "UNKNOWN";
            }
            return new QueryResult(this);
        }
    }
}

