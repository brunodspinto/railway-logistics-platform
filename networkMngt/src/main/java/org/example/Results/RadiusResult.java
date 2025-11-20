package org.example.Results;

import org.example.domain.Station;
import org.example.queries.RadiusKey;
import org.example.trees.AVLTree;

import java.util.Map;

public class RadiusResult {

    private final AVLTree<RadiusKey, Station> tree;
    private final Map<String, Integer> byCountry;
    private final Map<Boolean, Integer> byIsCity;

    public RadiusResult(AVLTree<RadiusKey, Station> tree, Map<String, Integer> byCountry, Map<Boolean, Integer> byIsCity) {
        this.tree = tree;
        this.byCountry = byCountry;
        this.byIsCity = byIsCity;
    }

    public AVLTree<RadiusKey, Station> getTree() {
        return tree;
    }

    public Map<String, Integer> getByCountry() {
        return byCountry;
    }

    public Map<Boolean, Integer> getByIsCity() {
        return byIsCity;
    }
}
