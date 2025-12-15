package org.example.ui.usei15;

import org.example.controller.USEI15Controller;
import org.example.domain.Connection;
import org.example.domain.Station;
import org.example.graph.Graph;
import org.example.loader.BelgianNetworkLoader;

public class USEI15Main {

    public static void main(String[] args) {

        try {
            System.out.println("=== USEI15 — Risk-Aware Shortest Paths ===");
            String filepath = "src/main/java/org.example/data/station_to_station.csv";
            Graph<Station, Connection> graph = BelgianNetworkLoader.loadNetwork(filepath);

            USEI15Controller controller = new USEI15Controller(graph);

            USEI15Menu menu = new USEI15Menu(graph, controller);

            menu.start();

        } catch (Exception e) {
            System.err.println("Failed to start USEI15:");
            e.printStackTrace();
        }
    }
}
