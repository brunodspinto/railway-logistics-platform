package org.example.usei09.trees;

import org.example.trees.TwoDTree;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TwoDTreeDistanceTest {

    @Test
    void testHaversineKnownValue() {
        // Lisboa -> Porto
        double lat1 = 38.7223, lon1 = -9.1393;
        double lat2 = 41.1579, lon2 = -8.6291;

        double d = TwoDTree.haversineKm(lat1, lon1, lat2, lon2);

        // Distância aproximada válida
        assertTrue(d > 270 && d < 320);
    }

    @Test
    void testZeroDistance() {
        double d = TwoDTree.haversineKm(10, 20, 10, 20);
        assertEquals(0.0, d, 1e-9);
    }
}

