package org.example.domain;

import java.util.*;

public class Line {
    private final int id;
    private final String name;
    private final String owner;
    private final Station startStation;
    private final Station endStation;
    private final int gauge; // 1668 mm
    private final List<LineSegment> segments;

    public Line(int id, String name, String owner, Station startStation,
                Station endStation, int gauge) {
        this.id = id;
        this.name = name;
        this.owner = owner;
        this.startStation = startStation;
        this.endStation = endStation;
        this.gauge = gauge;
        this.segments = new ArrayList<>();
    }

    public void addSegment(LineSegment segment) {
        segments.add(segment);
        segments.sort(Comparator.comparingInt(LineSegment::getOrder));
    }

    public double getTotalLengthKm() {
        return segments.stream()
                .mapToDouble(LineSegment::getLengthKm)
                .sum();
    }

    public boolean isFullyElectrified() {
        return segments.stream().allMatch(LineSegment::isElectrified);
    }

    public int getMinMaxSpeed() {
        return segments.stream()
                .mapToInt(LineSegment::getMaxSpeedKmh)
                .min()
                .orElse(0);
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public Station getStartStation() { return startStation; }
    public Station getEndStation() { return endStation; }
    public int getGauge() { return gauge; }
    public List<LineSegment> getSegments() { return Collections.unmodifiableList(segments); }
    public String getOwner() { return owner; }

    @Override
    public String toString() {
        return String.format("Line{id=%d, name='%s', %s→%s, %.1fkm}",
                id, name, startStation.getName(), endStation.getName(), getTotalLengthKm());
    }
}


