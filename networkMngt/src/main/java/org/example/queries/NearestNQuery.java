package org.example.queries;

import org.example.domain.Station;
import org.example.trees.Node2D;
import org.example.trees.TwoDTree;

import java.util.*;
import java.util.function.Predicate;

public class NearestNQuery {

    private final TwoDTree tree;

    public static class StationDistance {
        public final Station station;
        public final double distanceKm;

        public StationDistance(Station s, double d) {
            this.station = s;
            this.distanceKm = d;
        }
    }

    public NearestNQuery(TwoDTree tree) {
        this.tree = tree;
    }

    public List<StationDistance> nearestN(
            double lat, double lon, int n,
            String tzGroupFilter, String countryFilter
    ) {
        Predicate<Station> filter = s -> true;

        if (tzGroupFilter != null && !tzGroupFilter.isEmpty())
            filter = filter.and(s -> s.getTimeZoneGroup().equalsIgnoreCase(tzGroupFilter));

        if (countryFilter != null && !countryFilter.isEmpty())
            filter = filter.and(s -> s.getCountry().equalsIgnoreCase(countryFilter));

        // MAX-HEAP (biggest distance at top)
        Comparator<StationDistance> heapCmp = (a, b) -> {
            int cmp = Double.compare(b.distanceKm, a.distanceKm); // distance DESC
            if (cmp != 0) return cmp;
            return a.station.getName().compareToIgnoreCase(b.station.getName()); // name ASC for eviction
        };

        PriorityQueue<StationDistance> heap = new PriorityQueue<>(heapCmp);

        search(tree.getRoot(), lat, lon, n, filter, heap);

        // Convert heap → final sorted output
        List<StationDistance> result = new ArrayList<>(heap);
        result.sort((a, b) -> {
            int d = Double.compare(a.distanceKm, b.distanceKm); // distance ASC
            if (d != 0) return d;
            return b.station.getName().compareToIgnoreCase(a.station.getName()); // name DESC
        });

        return result;
    }

    private void search(Node2D node, double lat, double lon, int n,
                        Predicate<Station> filter,
                        PriorityQueue<StationDistance> heap) {

        if (node == null) return;

        // Process bucket stations
        for (Station s : node.getStations()) {
            if (!filter.test(s)) continue;

            double d = TwoDTree.haversineKm(lat, lon, s.getLatitude(), s.getLongitude());

            if (heap.size() < n) {
                heap.offer(new StationDistance(s, d));
            } else if (d < heap.peek().distanceKm) {
                heap.poll();
                heap.offer(new StationDistance(s, d));
            }
        }

        // Pick axis branch
        final int axis = node.getAxis();
        final double splitValue = node.getSplitCoordinate();
        final double targetCoord = (axis == 0 ? lat : lon);

        Node2D first = (targetCoord < splitValue) ? node.getLeft() : node.getRight();
        Node2D second = (first == node.getLeft()) ? node.getRight() : node.getLeft();

        // Search the closer side first
        search(first, lat, lon, n, filter, heap);

        // Check whether other branch could contain closer points
        double minPossibleKm = estimateMinDistanceKm(lat, lon, splitValue, axis);

        if (heap.size() < n || minPossibleKm < heap.peek().distanceKm) {
            search(second, lat, lon, n, filter, heap);
        }
    }

    private double estimateMinDistanceKm(double lat, double lon, double splitCoord, int axis) {
        double degDiff = Math.abs((axis == 0 ? lat : lon) - splitCoord);

        if (axis == 0) {
            return degDiff * 111; // latitude distance
        }

        double factor = Math.cos(Math.toRadians(lat)) * 111;
        if (factor < 1e-6) factor = 1e-6;

        return degDiff * factor;
    }
}
