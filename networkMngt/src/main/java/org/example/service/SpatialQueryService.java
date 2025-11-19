package org.example.service;

import org.example.domain.Station;
import org.example.queries.BoundingBoxQuery;
import org.example.trees.Node2D;
import org.example.trees.StationIndexes;

import java.util.ArrayList;
import java.util.List;

public class SpatialQueryService {

    private final StationIndexes indexes;

    public SpatialQueryService(StationIndexes indexes) {
        this.indexes = indexes;
    }

    public List<Station> queryArea(double minLat, double maxLat, double minLon, double maxLon, Boolean isCity, Boolean isMain, String country) {

        BoundingBoxQuery query = new BoundingBoxQuery(minLat, maxLat, minLon, maxLon, isCity, isMain, country);

        List<Station> results = new ArrayList<>();
        Node2D root = indexes.getSpatialIndex().getRoot();

        rangeSearch(root, query, results);
        return results;
    }

    private void rangeSearch(Node2D node, BoundingBoxQuery query, List<Station> out) {
        if (node == null) return;

        Station pivot = node.getStations().get(0);
        double lat = pivot.getLatitude();
        double lon = pivot.getLongitude();

        boolean insideLat = (lat >= query.getMinLat() && lat <= query.getMaxLat());
        boolean insideLon = (lon >= query.getMinLon() && lon <= query.getMaxLon());

        if (insideLat && insideLon) {
            for (Station s : node.getStations()) {
                if (query.matches(s)) out.add(s);
            }
        }

        int axis = node.getAxis();

        if (axis == 0) { // latitude
            if (query.getMinLat() <= lat) {
                rangeSearch(node.getLeft(), query, out);
            }
            if (query.getMaxLat() >= lat) {
                rangeSearch(node.getRight(), query, out);
            }
        } else { // longitude
            if (query.getMinLon() <= lon) {
                rangeSearch(node.getLeft(), query, out);
            }
            if (query.getMaxLon() >= lon) {
                rangeSearch(node.getRight(), query, out);
            }
        }
    }
}