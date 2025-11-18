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

    public List<Station> queryArea(double minLat, double maxLat,  double minLon, double maxLon,  Boolean isCity, Boolean isMain, String country) {

        BoundingBoxQuery query = new BoundingBoxQuery(minLat, maxLat, minLon, maxLon, isCity, isMain, country);

        List<Station> results = new ArrayList<>();
        Node2D root = indexes.getSpatialIndex().getRoot();
        rangeSearch(root, query, results, minLat, maxLat, minLon, maxLon);
        return results;
    }

    // Travessia recursiva da 2D-Tree com pruning por eixo
    private void rangeSearch(Node2D node,
                             BoundingBoxQuery query,
                             List<Station> out,
                             double minLat, double maxLat,
                             double minLon, double maxLon) {
        if (node == null) return;

        // Coordenadas do bucket (todas as estações do nó partilham estas coords)
        Station pivot = node.getStations().get(0);
        double lat = pivot.getLatitude();
        double lon = pivot.getLongitude();

        // Se o pivot está dentro da caixa, testar todas as estações do bucket
        boolean inLat = (lat >= minLat && lat <= maxLat);
        boolean inLon = (lon >= minLon && lon <= maxLon);
        if (inLat && inLon) {
            for (Station s : node.getStations()) {
                if (query.matches(s)) out.add(s);
            }
        }

        int axis = node.getAxis(); // 0 -> latitude, 1 -> longitude

        if (axis == 0) {
            // Divisão por latitude
            if (minLat <= lat) rangeSearch(node.getLeft(),  query, out, minLat, maxLat, minLon, maxLon);
            if (maxLat >= lat) rangeSearch(node.getRight(), query, out, minLat, maxLat, minLon, maxLon);
        } else {
            // Divisão por longitude
            if (minLon <= lon) rangeSearch(node.getLeft(),  query, out, minLat, maxLat, minLon, maxLon);
            if (maxLon >= lon) rangeSearch(node.getRight(), query, out, minLat, maxLat, minLon, maxLon);
        }
    }
}