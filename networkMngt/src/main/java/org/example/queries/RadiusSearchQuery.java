package org.example.queries;

import org.example.results.RadiusResult;
import org.example.domain.Station;
import org.example.trees.AVLTree;
import org.example.trees.Node2D;
import org.example.trees.TwoDTree;

import java.util.HashMap;
import java.util.Map;

public class RadiusSearchQuery {

    private final TwoDTree tree;

    public RadiusSearchQuery(TwoDTree spatialTree) {
        this.tree = spatialTree;
    }

    public RadiusResult search(double lat, double lon, double radiusKm) {

        AVLTree<RadiusKey, Station> resultTree = new AVLTree<>();
        Map<String, Integer> byCountry = new HashMap<>();
        Map<Boolean, Integer> byIsCity = new HashMap<>();
        byIsCity.put(true, 0);
        byIsCity.put(false, 0);

        searchRecursive(tree.getRoot(), lat, lon, radiusKm, resultTree, byCountry, byIsCity);

        return new RadiusResult(resultTree, byCountry, byIsCity);
    }

    private void searchRecursive(Node2D node,
                                 double lat, double lon,
                                 double radiusKm,
                                 AVLTree<RadiusKey, Station> results,
                                 Map<String, Integer> byCountry,
                                 Map<Boolean, Integer> byIsCity) {

        if (node == null) return;

        // process bucket
        for (Station s : node.getStations()) {
            double d = TwoDTree.haversineKm(lat, lon, s.getLatitude(), s.getLongitude());

            if (d <= radiusKm) {

                // Key uses .3 rounding distance
                double dRounded = Math.round(d * 1000.0) / 1000.0;
                RadiusKey key = new RadiusKey(dRounded, s.getName());
                results.insert(key, s);

                // FIX: count by country
                byCountry.merge(s.getCountry(), 1, Integer::sum);

                byIsCity.merge(s.isCity(), 1, Integer::sum);
            }
        }

        // compute distance to splitting plane
        double split = node.getSplitCoordinate();
        int axis = node.getAxis();
        double targetCoord = (axis == 0 ? lat : lon);

        double degDiff = Math.abs(targetCoord - split);

        double kmApprox = (axis == 0 ?
                degDiff * 111.32 :
                degDiff * 111.32 * Math.cos(Math.toRadians(lat)));

        Node2D first = (targetCoord < split) ? node.getLeft() : node.getRight();
        Node2D second = (first == node.getLeft()) ? node.getRight() : node.getLeft();

        searchRecursive(first, lat, lon, radiusKm, results, byCountry, byIsCity);

        if (kmApprox <= radiusKm) {
            searchRecursive(second, lat, lon, radiusKm, results, byCountry, byIsCity);
        }
    }
}

