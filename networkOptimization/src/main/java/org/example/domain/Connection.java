package org.example.domain;

/**
 * Representa uma conexão dirigida entre duas estações
 */
public class Connection {
    private final Station from;
    private final Station to;
    private final double distance;
    private final int capacity;      // ✅ NOVO
    private final double cost;       // ✅ NOVO

    // Construtor COM capacity e cost (para lines.csv)
    public Connection(Station from, Station to, double distance, int capacity, double cost) {
        if (from == null || to == null) {
            throw new IllegalArgumentException("Stations cannot be null");
        }
        if (distance < 0) {
            throw new IllegalArgumentException("Distance cannot be negative");
        }

        this.from = from;
        this.to = to;
        this.distance = distance;
        this.capacity = capacity;    // ✅ NOVO
        this.cost = cost;            // ✅ NOVO
    }

    // Construtor ORIGINAL (para compatibilidade)
    public Connection(Station from, Station to, double distance) {
        this(from, to, distance, 0, distance); // capacity=0, cost=distance
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

    public int getCapacity() {        // ✅ NOVO
        return capacity;
    }

    public double getCost() {         // ✅ NOVO
        return cost;
    }

    @Override
    public String toString() {
        return String.format("%s → %s (%.2f km, capacity: %d, cost: %.2f)",
                from.getName(),
                to.getName(),
                distance,
                capacity,
                cost);
    }
}