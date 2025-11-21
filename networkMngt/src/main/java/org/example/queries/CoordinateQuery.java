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

        sortByLatitudeThenName(results);

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

        sortByLongitudeThenName(results);

        long time = (System.nanoTime() - start) / 1_000_000;

        QueryResult r = new QueryResult("LONGITUDE_RANGE", results, time, results.size());
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

        sortByName(results);

        long time = (System.nanoTime() - start) / 1_000_000;

        QueryResult r = new QueryResult("EXACT_COORDINATES", results, time, 1);
        r.addMeta("latitude", lat);
        r.addMeta("longitude", lon);
        r.addMeta("complexity", "O(log n)");
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

    /**
     * Sort stations by name (ascending) using Insertion Sort.
     */
    private void sortByName(List<Station> stations) {
        int n = stations.size();

        for (int i = 1; i < n; i++) {
            Station key = stations.get(i);
            int j = i - 1;

            while (j >= 0 && stations.get(j).getName().compareTo(key.getName()) > 0) {
                stations.set(j + 1, stations.get(j));
                j--;
            }

            stations.set(j + 1, key);
        }
    }

    /**
     * Sort stations by latitude (ascending), then by name.
     */
    private void sortByLatitudeThenName(List<Station> stations) {
        int n = stations.size();

        for (int i = 1; i < n; i++) {
            Station key = stations.get(i);
            int j = i - 1;

            while (j >= 0) {
                Station current = stations.get(j);
                int latCompare = Double.compare(current.getLatitude(), key.getLatitude());

                boolean shouldMove = false;
                if (latCompare > 0) {
                    shouldMove = true;
                } else if (latCompare == 0) {
                    if (current.getName().compareTo(key.getName()) > 0) {
                        shouldMove = true;
                    }
                }

                if (!shouldMove) break;

                stations.set(j + 1, current);
                j--;
            }

            stations.set(j + 1, key);
        }
    }

    /**
     * Sort stations by longitude (ascending), then by name.
     */
    private void sortByLongitudeThenName(List<Station> stations) {
        int n = stations.size();

        for (int i = 1; i < n; i++) {
            Station key = stations.get(i);
            int j = i - 1;

            while (j >= 0) {
                Station current = stations.get(j);
                int lonCompare = Double.compare(current.getLongitude(), key.getLongitude());

                boolean shouldMove = false;
                if (lonCompare > 0) {
                    shouldMove = true;
                } else if (lonCompare == 0) {
                    if (current.getName().compareTo(key.getName()) > 0) {
                        shouldMove = true;
                    }
                }

                if (!shouldMove) break;

                stations.set(j + 1, current);
                j--;
            }

            stations.set(j + 1, key);
        }
    }
}