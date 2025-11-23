package org.example.usei09.trees;

import org.example.domain.Station;
import org.example.trees.TwoDTree;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

public class TwoDTreeBuildTest {

    @Test
    void testBuildSmallTree() {

        List<Station> stations = List.of(
                new Station("A", 0.0, 0.0, "PT", "WET", "WET", true, false, false),
                new Station("B", 1.0, 1.0, "PT", "WET", "WET", false, false, false),
                new Station("C", -1.0, -1.0, "ES", "CET", "CET", true, false, false)
        );

        List<Station> byLat = new ArrayList<>(stations);
        byLat.sort(Comparator.comparingDouble(Station::getLatitude));

        List<Station> byLon = new ArrayList<>(stations);
        byLon.sort(Comparator.comparingDouble(Station::getLongitude));

        TwoDTree tree = new TwoDTree();
        tree.build(byLat, byLon);

        assertEquals(3, tree.size());
        assertTrue(tree.height() >= 2);
    }
}