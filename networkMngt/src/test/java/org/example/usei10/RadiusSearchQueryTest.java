package org.example.usei10;

import org.example.queries.RadiusSearchQuery;
import org.example.results.RadiusResult;
import org.example.domain.Station;
import org.example.trees.TwoDTree;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class RadiusSearchQueryTest {

    private TwoDTree buildTree(List<Station> stations) {
        List<Station> byLat = new ArrayList<>(stations);
        List<Station> byLon = new ArrayList<>(stations);

        byLat.sort(Comparator.comparingDouble(Station::getLatitude));
        byLon.sort(Comparator.comparingDouble(Station::getLongitude));

        TwoDTree tree = new TwoDTree();
        tree.build(byLat, byLon);
        return tree;
    }

    @Test
    void testSimpleRadiusSearch() {
        List<Station> stations = List.of(
                new Station("A", 0, 0, "PT", "WET", "WET", true, false, false),
                new Station("B", 0.1, 0.1, "PT", "WET", "WET", false, false, false),
                new Station("C", 5, 5, "ES", "CET", "CET", false, false, false)
        );

        TwoDTree tree = buildTree(stations);
        RadiusSearchQuery query = new RadiusSearchQuery(tree);

        RadiusResult res = query.search(0, 0, 20.0);

        // A e B devem cair dentro dos ~15 km
        assertEquals(2, res.getTree().size());
    }

    @Test
    void testDistanceOrder() {
        List<Station> stations = List.of(
                new Station("Alpha", 0, 0, "PT", "WET", "WET", true, false, false),
                new Station("Bravo", 0.2, 0, "PT", "WET", "WET", true, false, false)
        );

        TwoDTree tree = buildTree(stations);
        RadiusSearchQuery query = new RadiusSearchQuery(tree);

        RadiusResult res = query.search(0, 0, 40);

        var ordered = res.getTree().inOrder();
        assertEquals("Alpha", ordered.get(0).getName());
        assertEquals("Bravo", ordered.get(1).getName());
    }

    @Test
    void testSummaryByCountry() {
        List<Station> stations = List.of(
                new Station("A", 0, 0, "PT", "WET", "WET", true, false, false),
                new Station("B", 0.1, 0.1, "PT", "WET", "WET", false, false, false),
                new Station("C", 0.2, 0.2, "ES", "CET", "CET", false, false, false)
        );

        TwoDTree tree = buildTree(stations);
        RadiusSearchQuery query = new RadiusSearchQuery(tree);

        RadiusResult res = query.search(0, 0, 35);

        Map<String, Integer> byCountry = res.getByCountry();
        assertEquals(2, byCountry.get("PT"));
        assertEquals(1, byCountry.get("ES"));
    }

    @Test
    void testSummaryByIsCity() {
        List<Station> stations = List.of(
                new Station("A", 0, 0, "PT", "WET", "WET", true, false, false),
                new Station("B", 0.1, 0.1, "PT", "WET", "WET", false, false, false),
                new Station("C", 0.2, 0.2, "PT", "WET", "WET", true, false, false)
        );

        TwoDTree tree = buildTree(stations);
        RadiusSearchQuery query = new RadiusSearchQuery(tree);

        RadiusResult res = query.search(0, 0, 35);

        Map<Boolean, Integer> byCity = res.getByIsCity();

        assertEquals(2, byCity.get(true));   // A e C
        assertEquals(1, byCity.get(false));  // B
    }

    @Test
    void testTieBreakNameDescending() {
        List<Station> stations = List.of(
                new Station("Alpha", 0, 0, "PT", "WET", "WET", false, false, false),
                new Station("Zulu", 0, 0, "PT", "WET", "WET", false, false, false)
        );

        TwoDTree tree = buildTree(stations);
        RadiusSearchQuery query = new RadiusSearchQuery(tree);

        RadiusResult res = query.search(0, 0, 1);

        var ordered = res.getTree().inOrder();

        // DEVE vir Zulu primeiro (DESC name em empate de distância)
        assertEquals("Zulu", ordered.get(0).getName());
        assertEquals("Alpha", ordered.get(1).getName());
    }
}
