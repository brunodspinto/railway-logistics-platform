package org.example.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Representa o plano completo da rota, contendo a sequência de paragens.
 */
public class RoutePlan {
    private final List<RouteStop> stops;

    public RoutePlan() {
        this.stops = new ArrayList<>();
    }

    public void addStop(RouteStop stop) {
        this.stops.add(stop);
    }

    public List<RouteStop> getStops() {
        return Collections.unmodifiableList(stops);
    }

    public void printManifest() {
        System.out.println("=== ROUTE LOGISTICS MANIFEST ===");
        for (RouteStop stop : stops) {
            System.out.println(stop.toString());
            System.out.println("--------------------------------");
        }
        System.out.println("================================");
    }
}