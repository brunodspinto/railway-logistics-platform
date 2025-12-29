package org.example.domain;

/**
 * Representa uma conexão (linha) entre duas estações.
 * Agora inclui capacidade e custo para a USEI14.
 */
public class Connection {

    private final Station source;
    private final Station target;
    private final double distance;
    private double capacity; // Capacidade (comboios/dia)
    private double cost;

    public Connection(Station source, Station target, double distance, double capacity, double cost) {
        this.source = source;
        this.target = target;
        this.distance = distance;
        this.capacity = capacity;
        this.cost = cost;
    }

    // Construtor simples (retrocompatibilidade, assume defaults)
    public Connection(Station source, Station target, double distance) {
        this(source, target, distance, 50.0, 0.0);
    }

    public Station getSource() {
        return source;
    }

    public Station getTarget() {
        return target;
    }

    public double getDistance() {
        return distance;
    }

    public double getCapacity() {
        return capacity;
    }

    public void setCapacity(double capacity) {
        this.capacity = capacity;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    @Override
    public String toString() {
        return "Connection{" +
                "from=" + source.getName() +
                ", to=" + target.getName() +
                ", dist=" + distance +
                ", cap=" + capacity +
                '}';
    }
}