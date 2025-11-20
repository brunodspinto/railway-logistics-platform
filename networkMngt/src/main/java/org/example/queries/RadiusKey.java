package org.example.queries;

public class RadiusKey implements Comparable<RadiusKey>{

    private final double distanceKm;
    private final String stationName;

    public RadiusKey(double distanceKm, String stationName) {
        this.distanceKm = distanceKm;
        this.stationName = stationName;
    }

    public double getDistanceKm() {
        return distanceKm;
    }

    public String getStationName() {
        return stationName;
    }

    @Override
    public int compareTo(RadiusKey other) {
        int cmp = Double.compare(this.distanceKm, other.distanceKm);
        if (cmp != 0) {
            return cmp;
        }
        return other.stationName.compareTo(this.stationName);
    }

    @Override
    public String toString() {
        return String.format("%.3f km | %s", distanceKm, stationName);
    }
}
