package org.example.queries;

import org.example.domain.Station;

public class BoundingBoxQuery {

    private final double minLat, maxLat, minLon, maxLon;
    private final Boolean isCity, isMain;
    private final String country;

    public BoundingBoxQuery(double minLat, double maxLat, double minLon, double maxLon,
                            Boolean isCity, Boolean isMain, String country) {
        this.minLat = minLat;
        this.maxLat = maxLat;
        this.minLon = minLon;
        this.maxLon = maxLon;
        this.isCity = isCity;
        this.isMain = isMain;
        this.country = (country == null || country.isEmpty()) ? "all" : country;
    }

    public boolean matches(Station s) {
        if (s == null) return false;

        if (s.getLatitude() < minLat || s.getLatitude() > maxLat) return false;
        if (s.getLongitude() < minLon || s.getLongitude() > maxLon) return false;

        if (isCity != null && s.isCity() != isCity) return false;
        if (isMain != null && s.isMainStation() != isMain) return false;

        if (!country.equalsIgnoreCase("all") &&
                !s.getCountry().equalsIgnoreCase(country))
            return false;

        return true;
    }
}