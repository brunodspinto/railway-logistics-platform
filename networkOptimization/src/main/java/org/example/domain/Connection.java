package org.example.domain;

/**
 * Representa uma conexão dirigida entre duas estações
 */
public class Connection {
    private final Station from;
    private final Station to;
    private final double distance;
    private final double cost;

    public Connection(Station from, Station to, double distance) {
        if (from == null || to == null) {
            throw new IllegalArgumentException("Stations cannot be null");
        }
        if (distance < 0) {
            throw new IllegalArgumentException("Distance cannot be negative");
        }

        this.from = from;
        this.to = to;
        this.distance = distance;
        this.cost = calculateAdjustedCost(distance);
    }

    private double calculateAdjustedCost(double distance) {
        double penalty = 0.0;

        if (distance > 100) {
            penalty = -10.0;
        } else if (distance < 10) {
            penalty = 5.0;
        }

        return distance + penalty;
    }

    public Station getFrom() {
        return from;
    }

    public Station getTo() {
        return to;
    }

    public double getDistance() {
        return distance;
    }

    public double getCost() { return cost; }

    @Override
    public String toString() {
        return String.format("%s → %s (%.2f km)",
                from.getName(),
                to.getName(),
                distance,
                cost);
    }
}
