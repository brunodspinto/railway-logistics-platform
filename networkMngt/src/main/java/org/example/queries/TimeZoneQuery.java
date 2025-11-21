package org.example.queries;

import org.example.domain.Station;
import org.example.trees.*;
import java.util.*;

public class TimeZoneQuery {
    private final StationIndexes indexes;

    public TimeZoneQuery(StationIndexes indexes) {
        this.indexes = indexes;
    }

    public QueryResult queryByTimeZoneGroup(String tzGroup) {
        long start = System.nanoTime();
        List<Station> results = new ArrayList<>();
        int visited = 0;

        AVLTree<CompositeKey, Station> tzIndex = indexes.getTimeZoneIndex();
        List<Station> all = tzIndex.inOrder();

        for (Station s : all) {
            visited++;
            if (s.getTimeZoneGroup().equals(tzGroup)) {
                results.add(s);
            }
        }

        sortByCountryThenName(results);

        long time = (System.nanoTime() - start) / 1_000_000;

        QueryResult r = new QueryResult("TIME_ZONE_GROUP", results, time, visited);
        r.addMeta("timeZoneGroup", tzGroup);
        r.addMeta("complexity", "O(k log n)");
        return r;
    }

    public QueryResult queryByTimeZoneGroupAndCountry(String tzGroup, String country) {
        long start = System.nanoTime();

        CompositeKey key = new CompositeKey(tzGroup, country);
        AVLTree<CompositeKey, Station> tzIndex = indexes.getTimeZoneIndex();

        List<Station> results = tzIndex.search(key);

        long time = (System.nanoTime() - start) / 1_000_000;

        QueryResult r = new QueryResult("TIME_ZONE_COUNTRY", results, time, 1);
        r.addMeta("timeZoneGroup", tzGroup);
        r.addMeta("country", country);
        r.addMeta("complexity", "O(log n + k)");
        return r;
    }

    public QueryResult queryByTimeZoneWindow(List<String> tzGroups) {
        long start = System.nanoTime();
        List<Station> results = new ArrayList<>();
        int visited = 0;

        AVLTree<CompositeKey, Station> tzIndex = indexes.getTimeZoneIndex();

        for (String tzGroup : tzGroups) {
            List<Station> all = tzIndex.inOrder();

            for (Station s : all) {
                visited++;
                if (s.getTimeZoneGroup().equals(tzGroup)) {
                    results.add(s);
                }
            }
        }

        results = removeDuplicates(results);

        sortByCountryThenName(results);

        long time = (System.nanoTime() - start) / 1_000_000;

        QueryResult r = new QueryResult("TIME_ZONE_WINDOW", results, time, visited);
        r.addMeta("timeZoneWindow", tzGroups.toString());
        r.addMeta("windowSize", tzGroups.size());
        r.addMeta("complexity", "O(m * log n + k)");
        return r;
    }

    public Map<String, Long> getCountryDistribution(String tzGroup) {
        QueryResult result = queryByTimeZoneGroup(tzGroup);
        List<Station> stations = result.getStations();

        Map<String, Long> distribution = new HashMap<>();
        for (Station station : stations) {
            String country = station.getCountry();
            Long count = distribution.get(country);
            if (count == null) {
                distribution.put(country, 1L);
            } else {
                distribution.put(country, count + 1);
            }
        }

        return distribution;
    }

    public Map<String, Long> getTimeZoneDistribution() {
        List<Station> all = indexes.getTimeZoneIndex().inOrder();

        Map<String, Long> distribution = new HashMap<>();
        for (Station station : all) {
            String timeZone = station.getTimeZoneGroup();
            Long count = distribution.get(timeZone);
            if (count == null) {
                distribution.put(timeZone, 1L);
            } else {
                distribution.put(timeZone, count + 1);
            }
        }

        return distribution;
    }


    /**
     * Sort stations by country (ascending), then by name.
     */
    private void sortByCountryThenName(List<Station> stations) {
        int n = stations.size();

        for (int i = 1; i < n; i++) {
            Station key = stations.get(i);
            int j = i - 1;

            while (j >= 0) {
                Station current = stations.get(j);
                int countryCompare = current.getCountry().compareTo(key.getCountry());

                boolean shouldMove = false;
                if (countryCompare > 0) {
                    shouldMove = true;
                } else if (countryCompare == 0) {
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
     * Remove duplicate stations from list.
     */
    private List<Station> removeDuplicates(List<Station> stations) {
        List<Station> unique = new ArrayList<>();

        for (Station station : stations) {
            boolean found = false;

            for (Station existing : unique) {
                if (existing.equals(station)) {
                    found = true;
                    break;
                }
            }

            if (!found) {
                unique.add(station);
            }
        }

        return unique;
    }
}