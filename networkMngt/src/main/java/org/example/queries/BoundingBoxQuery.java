package org.example.queries;

import org.example.domain.Station;

public class BoundingBoxQuery {

    private final double minLat, maxLat, minLon, maxLon;
    private final Boolean isCity, isMain;
    private final String country;
    private final boolean ignoreCountry;

    public double getMinLat() { return minLat; }
    public double getMaxLat() { return maxLat; }
    public double getMinLon() { return minLon; }
    public double getMaxLon() { return maxLon; }


    public BoundingBoxQuery(double minLatitude, double maxLatitude, double minLongitude, double maxLongitude, Boolean isCityFilter, Boolean isMainFilter, String countryFilter) {

        this.minLat = minLatitude;
        this.maxLat = maxLatitude;
        this.minLon = minLongitude;
        this.maxLon = maxLongitude;

        this.isCity = isCityFilter;
        this.isMain = isMainFilter;

        if (countryFilter == null || countryFilter.isBlank() || countryFilter.equalsIgnoreCase("all")) {
            this.ignoreCountry = true;
            this.country = "all";
        } else {
            this.ignoreCountry = false;
            this.country = countryFilter.trim().toLowerCase();
        }
    }

    public boolean matches(Station s) {
        if (s == null) return false;

        double lat = s.getLatitude();
        if (lat < minLat || lat > maxLat) return false;

        double lon = s.getLongitude();
        if (lon < minLon || lon > maxLon) return false;

        if (isCity != null && s.isCity() != isCity) return false;
        if (isMain != null && s.isMainStation() != isMain) return false;

        if (!ignoreCountry) {
            if (!s.getCountry().equalsIgnoreCase(country)) return false;
        }

        return true;
    }
}