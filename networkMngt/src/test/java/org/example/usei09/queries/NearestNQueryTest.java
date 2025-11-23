package org.example.usei09.queries;

import org.example.domain.Station;
import org.example.queries.NearestNQuery;
import org.example.trees.TwoDTree;
import org.junit.jupiter.api.Test;

import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

public class NearestNQueryTest {

    private TwoDTree buildSimpleTree() {
        List<Station> stations = List.of(
                new Station("Alpha", 0, 0, "PT", "WET", "WET", true, false, false),
                new Station("Bravo", 1, 1, "PT", "WET", "WET", false, false, false),
                new Station("Charlie", -1, -1, "ES", "CET", "CET", false, false, false),
                new Station("Delta", 2, 2, "ES", "CET", "CET", false, false, false)
        );

        List<Station> byLat = new ArrayList<>(stations);
        List<Station> byLon = new ArrayList<>(stations);

        byLat.sort(Comparator.comparingDouble(Station::getLatitude));
        byLon.sort(Comparator.comparingDouble(Station::getLongitude));

        TwoDTree tree = new TwoDTree();
        tree.build(byLat, byLon);
        return tree;
    }

    @Test
    void testNearestNReturnsCorrectOrder() {
        TwoDTree tree = buildSimpleTree();
        NearestNQuery q = new NearestNQuery(tree);

        var res = q.nearestN(0, 0, 3, null, null);

        assertEquals(3, res.size());

        // Closest to (0,0) should be Alpha
        assertEquals("Alpha", res.get(0).station.getName());
    }

    @Test
    void testFilterByCountry() {
        TwoDTree tree = buildSimpleTree();
        NearestNQuery q = new NearestNQuery(tree);

        var res = q.nearestN(0,0,3, null, "PT");

        assertEquals(2, res.size());
        assertTrue(res.stream().allMatch(d -> d.station.getCountry().equals("PT")));
    }

    @Test
    void testFilterByTZ() {
        TwoDTree tree = buildSimpleTree();
        NearestNQuery q = new NearestNQuery(tree);

        var res = q.nearestN(0,0,10, "CET", null);

        assertEquals(2, res.size());
        assertTrue(res.stream().allMatch(d -> d.station.getTimeZoneGroup().equals("CET")));
    }

    @Test
    void testNameTieBreakDescending() {
        // Alpha and another station at same place
        Station s1 = new Station("Alpha", 0, 0, "PT", "WET", "WET", true, false, false);
        Station s2 = new Station("Zulu",  0, 0, "PT", "WET", "WET", false, false, false);

        List<Station> stations = List.of(s1, s2);

        List<Station> byLat = new ArrayList<>(stations);
        byLat.sort(Comparator.comparingDouble(Station::getLatitude));

        List<Station> byLon = new ArrayList<>(stations);
        byLon.sort(Comparator.comparingDouble(Station::getLongitude));

        TwoDTree tree = new TwoDTree();
        tree.build(byLat, byLon);

        NearestNQuery q = new NearestNQuery(tree);

        var res = q.nearestN(0,0,2,null,null);

        assertEquals(2, res.size());

        // Same distance -> must sort DESC by name
        assertEquals("Zulu",  res.get(0).station.getName());
        assertEquals("Alpha", res.get(1).station.getName());
    }
}




