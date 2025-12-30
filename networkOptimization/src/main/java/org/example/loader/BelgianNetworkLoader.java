package org.example.loader;

import org.example.domain.Connection;
import org.example.domain.Station;
import org.example.graph.Graph;
import org.example.graph.map.MapGraph;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Loader for Belgian Railway Network using simple CSV format.
 * Suporta leitura robusta de lines.csv (com ou sem capacidade explícita).
 */
public class BelgianNetworkLoader {

    private static final int DEFAULT_CAPACITY = 50;
    private static final double DEFAULT_COST = 0.0;

    /**
     * Carrega a rede.
     * @param isBidirectional Se true, cria arestas de ida e volta (para MaxFlow/Backbone).
     * Se false, carrega apenas o sentido do CSV (para UpgradePlan/TopologicalSort).
     */
    public static Graph<Station, Connection> loadNetwork(String stationsPath, String linesPath, boolean isBidirectional)
            throws IOException {

        System.out.println("Loading Belgian railway network (Bidirectional: " + isBidirectional + ")...");

        // 1. Load stations first
        Map<String, Station> stationMap = loadStations(stationsPath);
        System.out.println("Loaded " + stationMap.size() + " stations");

        // 2. Create graph (Directed = true).
        // Mesmo sendo bidirecional físico, representamo-lo como directed com arestas opostas.
        Graph<Station, Connection> graph = new MapGraph<>(true);

        // 3. Add all stations to graph
        for (Station station : stationMap.values()) {
            graph.addVertex(station);
        }

        // 4. Load lines (connections)
        int validLines = loadLines(linesPath, stationMap, graph, isBidirectional);

        System.out.println("\nLoaded network:");
        System.out.println("  Stations: " + graph.numVertices());
        System.out.println("  Connections (Edges): " + graph.numEdges());
        System.out.println("  Valid physical lines from CSV: " + validLines);

        return graph;
    }

    private static Map<String, Station> loadStations(String filePath) throws IOException {
        Map<String, Station> stations = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line = br.readLine();
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] parts = line.split(",");
                if (parts.length >= 2) {
                    String id = parts[0].trim();
                    String name = parts[1].trim();
                    Station station = new Station(id, name);
                    stations.put(id, station);
                }
            }
        }
        return stations;
    }

    private static int loadLines(String filePath, Map<String, Station> stationMap,
                                 Graph<Station, Connection> graph, boolean isBidirectional) throws IOException {
        int validLines = 0;
        int errorLines = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line = br.readLine(); // Skip header

            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                try {
                    String[] parts = line.split(",");

                    if (parts.length >= 3) {
                        String fromId = parts[0].trim();
                        String toId = parts[1].trim();
                        double distance = Double.parseDouble(parts[2].trim());

                        int capacity = DEFAULT_CAPACITY;
                        double cost = DEFAULT_COST;

                        if (parts.length >= 4 && !parts[3].trim().isEmpty()) {
                            try {
                                capacity = Integer.parseInt(parts[3].trim());
                            } catch (NumberFormatException e) {
                                // ignore
                            }
                        }
                        if (parts.length >= 5 && !parts[4].trim().isEmpty()) {
                            try {
                                cost = Double.parseDouble(parts[4].trim());
                            } catch (NumberFormatException e) {
                                // ignore
                            }
                        }

                        Station from = stationMap.get(fromId);
                        Station to = stationMap.get(toId);

                        if (from != null && to != null) {
                            // 1. Sentido de Ida (Sempre adicionado, conforme o CSV)
                            Connection connForward = new Connection(from, to, distance, capacity, cost);
                            graph.addEdge(from, to, connForward);

                            // 2. Sentido de Volta (Só se for bidirecional)
                            if (isBidirectional) {
                                Connection connBackward = new Connection(to, from, distance, capacity, cost);
                                graph.addEdge(to, from, connBackward);
                            }

                            validLines++;
                        } else {
                            errorLines++;
                        }
                    }
                } catch (NumberFormatException e) {
                    errorLines++;
                }
            }
        }
        return validLines;
    }
}