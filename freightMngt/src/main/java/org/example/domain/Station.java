package org.example.domain;


public class Station {
    private final int id;
    private final String name;

    public Station(int id, String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Station name cannot be empty");
        }
        this.id = id;
        this.name = name;
    }

    public int getId() { return id; }
    public String getName() { return name; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Station)) return false;
        Station station = (Station) o;
        return id == station.id;
    }

    @Override
    public int hashCode() { return Integer.hashCode(id); }

    @Override
    public String toString() {
        return String.format("Station{id=%d, name='%s'}", id, name);
    }
}

