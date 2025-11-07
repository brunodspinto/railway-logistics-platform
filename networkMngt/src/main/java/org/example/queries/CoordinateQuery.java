package org.example.queries;

import org.example.domain.Station;
import org.example.trees.*;
import java.util.*;

public class CoordinateQuery {
    private final StationIndexes indexes;

    public CoordinateQuery(StationIndexes indexes) {
        this.indexes = indexes;
    }

    public QueryResult queryByLatitudeRange(double minLat, double maxLat) {
        long start = System.nanoTime();

        if (minLat < -90 || minLat > 90 || maxLat < -90 || maxLat > 90) {
            throw new IllegalArgumentException("Latitude must be in [-90, 90]");
        }
        if (minLat > maxLat) {
            throw new IllegalArgumentException("minLat must be <= maxLat");
        }

        AVLTree<Double, Station> latIndex = indexes.getLatitudeIndex();
        List<Station> results = latIndex.rangeSearch(minLat, maxLat);

        Collections.sort(results, new Comparator<Station>() {
            @Override
            public int compare(Station s1, Station s2) {
                int latCompare = Double.compare(s1.getLatitude(), s2.getLatitude());
                if (latCompare != 0) {
                    return latCompare;
                }
                return s1.getName().compareTo(s2.getName());
            }
        });

        long time = (System.nanoTime() - start) / 1_000_000;

        QueryResult r = new QueryResult("LATITUDE_RANGE", results, time, results.size());
        r.addMeta("minLatitude", minLat);
        r.addMeta("maxLatitude", maxLat);
        r.addMeta("complexity", "O(log n + k)");
        return r;
    }

    public QueryResult queryByLongitudeRange(double minLon, double maxLon) {
        long start = System.nanoTime();

        if (minLon < -180 || minLon > 180 || maxLon < -180 || maxLon > 180) {
            throw new IllegalArgumentException("Longitude must be in [-180, 180]");
        }
        if (minLon > maxLon) {
            throw new IllegalArgumentException("minLon must be <= maxLon");
        }

        AVLTree<Double, Station> lonIndex = indexes.getLongitudeIndex();
        List<Station> results = lonIndex.rangeSearch(minLon, maxLon);

        Collections.sort(results, new Comparator<Station>() {
            @Override
            public int compare(Station s1, Station s2) {
                int lonCompare = Double.compare(s1.getLongitude(), s2.getLongitude());
                if (lonCompare != 0) {
                    return lonCompare;
                }
                return s1.getName().compareTo(s2.getName());
            }
        });

        long time = (System.nanoTime() - start) / 1_000_000;

        QueryResult r = new QueryResult("LONGITUDE_RANGE", results, time, results.size());
        r.addMeta("minLongitude", minLon);
        r.addMeta("maxLongitude", maxLon);
        r.addMeta("complexity", "O(log n + k)");
        return r;
    }

    public QueryResult queryByBoundingBox(double minLat, double maxLat,
                                          double minLon, double maxLon) {
        long start = System.nanoTime();

        List<Station> latResults = indexes.getLatitudeIndex().rangeSearch(minLat, maxLat);

        List<Station> results = new ArrayList<>();
        for (Station station : latResults) {
            if (station.getLongitude() >= minLon && station.getLongitude() <= maxLon) {
                results.add(station);
            }
        }

        Collections.sort(results, new Comparator<Station>() {
            @Override
            public int compare(Station s1, Station s2) {
                int latCompare = Double.compare(s1.getLatitude(), s2.getLatitude());
                if (latCompare != 0) {
                    return latCompare;
                }
                int lonCompare = Double.compare(s1.getLongitude(), s2.getLongitude());
                if (lonCompare != 0) {
                    return lonCompare;
                }
                return s1.getName().compareTo(s2.getName());
            }
        });

        long time = (System.nanoTime() - start) / 1_000_000;

        QueryResult r = new QueryResult("BOUNDING_BOX", results, time, latResults.size());
        r.addMeta("minLatitude", minLat);
        r.addMeta("maxLatitude", maxLat);
        r.addMeta("minLongitude", minLon);
        r.addMeta("maxLongitude", maxLon);
        r.addMeta("complexity", "O(log n + k)");
        return r;
    }

    public QueryResult queryByExactCoordinates(double lat, double lon) {
        long start = System.nanoTime();

        AVLTree<Double, Station> latIndex = indexes.getLatitudeIndex();
        List<Station> latResults = latIndex.search(lat);

        List<Station> results = new ArrayList<>();
        for (Station station : latResults) {
            if (Math.abs(station.getLongitude() - lon) < 0.000001) {
                results.add(station);
            }
        }

        Collections.sort(results, new Comparator<Station>() {
            @Override
            public int compare(Station s1, Station s2) {
                return s1.getName().compareTo(s2.getName());
            }
        });

        long time = (System.nanoTime() - start) / 1_000_000;

        QueryResult r = new QueryResult("EXACT_COORDINATES", results, time, 1);
        r.addMeta("latitude", lat);
        r.addMeta("longitude", lon);
        r.addMeta("complexity", "O(log n)");
        return r;
    }

    public QueryResult queryByBoundingBoxAndCountry(double minLat, double maxLat,
                                                    double minLon, double maxLon,
                                                    String country) {
        long start = System.nanoTime();

        QueryResult boxResult = queryByBoundingBox(minLat, maxLat, minLon, maxLon);

        List<Station> results = new ArrayList<>();
        for (Station station : boxResult.getStations()) {
            if (station.getCountry().equalsIgnoreCase(country)) {
                results.add(station);
            }
        }

        long time = (System.nanoTime() - start) / 1_000_000;

        QueryResult r = new QueryResult("BOUNDING_BOX_COUNTRY", results, time, boxResult.getNodesVisited());
        r.addMeta("minLatitude", minLat);
        r.addMeta("maxLatitude", maxLat);
        r.addMeta("minLongitude", minLon);
        r.addMeta("maxLongitude", maxLon);
        r.addMeta("country", country);
        r.addMeta("complexity", "O(log n + k)");
        return r;
    }

    public Map<String, Object> getLatitudeDistributionSummary() {
        List<Station> all = indexes.getLatitudeIndex().inOrder();

        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;
        double sum = 0;
        int count = 0;

        for (Station station : all) {
            double lat = station.getLatitude();
            if (lat < min) min = lat;
            if (lat > max) max = lat;
            sum += lat;
            count++;
        }

        Map<String, Object> summary = new HashMap<>();
        summary.put("count", count);
        summary.put("min", min);
        summary.put("max", max);
        summary.put("average", count > 0 ? sum / count : 0);

        return summary;
    }

    public Map<String, Object> getLongitudeDistributionSummary() {
        List<Station> all = indexes.getLongitudeIndex().inOrder();

        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;
        double sum = 0;
        int count = 0;

        for (Station station : all) {
            double lon = station.getLongitude();
            if (lon < min) min = lon;
            if (lon > max) max = lon;
            sum += lon;
            count++;
        }

        Map<String, Object> summary = new HashMap<>();
        summary.put("count", count);
        summary.put("min", min);
        summary.put("max", max);
        summary.put("average", count > 0 ? sum / count : 0);

        return summary;
    }
}