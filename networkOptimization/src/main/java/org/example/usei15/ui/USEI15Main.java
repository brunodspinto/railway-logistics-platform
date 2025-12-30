package org.example.usei15.ui;

import org.example.usei15.algorithm.NegativeCycleException;
import org.example.usei15.controller.USEI15Controller;
import org.example.domain.Connection;
import org.example.domain.Station;
import org.example.graph.Graph;
import org.example.loader.BelgianNetworkLoader;

import java.io.IOException;

public class USEI15Main {

    public static void main(String[] args) {

        try {
            System.out.println("=== USEI15 — Risk-Aware Shortest Paths ===");
            String stationsPath = "res/stations.csv";
            String linesPath = "res/lines.csv";

            Graph<Station, Connection> graph = BelgianNetworkLoader.loadNetwork(stationsPath, linesPath, false);

            USEI15Controller controller = new USEI15Controller(graph);

            USEI15Menu menu = new USEI15Menu(graph, controller);

            menu.start();

        } catch (NegativeCycleException e) {
            System.out.println("\n=== USEI15 — Negative Cycle Detected ===");

            // Imprime o report detalhado que gerámos no algoritmo
            System.out.println(e.getDetailedMessage());

            System.out.println("\n⚠ Configuration inconsistency detected.");

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}