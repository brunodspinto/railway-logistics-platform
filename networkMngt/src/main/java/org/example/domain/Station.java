package org.example.domain;

import java.util.Objects;

/**
 * Represents a European railway station.
 */
public class Station implements Comparable<Station> {
    private final String name;
    private final double latitude;
    private final double longitude;
    private final String country;
    private final String timeZone;
    private final String timeZoneGroup;
    private final boolean isCity;
    private final boolean isMainStation;
    private final boolean isAirport;

    // Constructor
    public Station(String name, double latitude, double longitude,
                   String country, String timeZone, String timeZoneGroup,
                   boolean isCity, boolean isMainStation, boolean isAirport) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.country = country;
        this.timeZone = timeZone;
        this.timeZoneGroup = timeZoneGroup;
        this.isCity = isCity;
        this.isMainStation = isMainStation;
        this.isAirport = isAirport;
    }

    // Getters
    public String getName() { return name; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public String getCountry() { return country; }
    public String getTimeZone() { return timeZone; }
    public String getTimeZoneGroup() { return timeZoneGroup; }
    public boolean isCity() { return isCity; }
    public boolean isMainStation() { return isMainStation; }
    public boolean isAirport() { return isAirport; }

    /**
     * Natural ordering: by name (ASC)
     */
    @Override
    public int compareTo(Station other) {
        return this.name.compareTo(other.name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Station)) return false;
        Station station = (Station) o;
        return Double.compare(station.latitude, latitude) == 0 &&
                Double.compare(station.longitude, longitude) == 0 &&
                Objects.equals(name, station.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, latitude, longitude);
    }

    @Override
    public String toString() {
        return String.format("%s (%s) [%.5f, %.5f] - %s",
                name, country, latitude, longitude, timeZoneGroup);
    }

    public String getValidationError() {
        if (name == null || name.trim().isEmpty()) {
            return "Station name cannot be empty";
        }
        if (latitude < -90 || latitude > 90) {
            return "Latitude must be in range [-90, 90], got: " + latitude;
        }
        if (longitude < -180 || longitude > 180) {
            return "Longitude must be in range [-180, 180], got: " + longitude;
        }
        if (country == null || country.trim().isEmpty()) {
            return "Country cannot be empty";
        }
        if (timeZoneGroup == null || timeZoneGroup.trim().isEmpty()) {
            return "TimeZoneGroup cannot be empty";
        }
        return null;
    }
}

