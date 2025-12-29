package org.example.usei15.result;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ShortestPathResult<V> {

    private final List<V> path;
    private final Map<V, Double> costToVertex;
    private final double totalCost;
    private final boolean hasPath;

    public ShortestPathResult(List<V> path, Map<V, Double> costToVertex, double totalCost, boolean hasPath) {
        this.path = path;
        this.costToVertex = costToVertex;
        this.totalCost = totalCost;
        this.hasPath = hasPath;
    }

    public List<V> getPath() {
        return path;
    }

    public double getCostTo(V vertex) {
        return costToVertex.getOrDefault(vertex, Double.POSITIVE_INFINITY);
    }

    public double getTotalCost() {
        return totalCost;
    }

    public static <V> ShortestPathResult<V> noPath() {
        return new ShortestPathResult<>(Collections.emptyList(), Collections.emptyMap(), Double.POSITIVE_INFINITY, false);
    }

    public boolean hasPath() {
        return hasPath;
    }
}
