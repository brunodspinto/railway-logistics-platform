package org.example.domain;

/**
 * Representa uma conexão dirigida entre duas estações
 */
public class Connection {
    private final Station from;
    private final Station to;
    private final double distance;

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

    @Override
    public String toString() {
        return String.format("%s → %s (%.2f km)",
                from.getName(),
                to.getName(),
                distance);
    }
}
