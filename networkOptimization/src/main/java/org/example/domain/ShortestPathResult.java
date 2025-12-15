package org.example.domain;

import java.util.List;

public class ShortestPathResult<V> {

    private final List<V> path;
    private final double totalCost;

    public ShortestPathResult(List<V> path, double totalCost) {
        this.path = path;
        this.totalCost = totalCost;
    }

    public List<V> getPath() {
        return path;
    }

    public double getTotalCost() {
        return totalCost;
    }
}
