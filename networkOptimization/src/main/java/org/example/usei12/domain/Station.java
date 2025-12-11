package org.example.usei12.domain;

public class Station {

    private final String id;
    private final String name;
    private final double lat;
    private final double lon;

    public Station(String id, String name, double lat, double lon) {
        this.id = id;
        this.name = name;
        this.lat = lat;
        this.lon = lon;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }
}