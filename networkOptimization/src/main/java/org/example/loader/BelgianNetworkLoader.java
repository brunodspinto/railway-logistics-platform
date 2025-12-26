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
 * Loader for Belgian Railway Network using simple CSV format (comma-separated)
 * Uses lines.csv and stations.csv
 */
public class BelgianNetworkLoader {

    public static Graph<Station, Connection> loadNetwork(String stationsPath, String linesPath)
            throws IOException {

        System.out.println("Loading Belgian railway network...");

        // 1. Load stations first
        Map<String, Station> stationMap = loadStations(stationsPath);
        System.out.println("Loaded " + stationMap.size() + " stations");

        // 2. Create graph
        Graph<Station, Connection> graph = new MapGraph<>(true); // directed

        // 3. Add all stations to graph
        for (Station station : stationMap.values()) {
            graph.addVertex(station);
        }

        // 4. Load lines (connections)
        int validLines = loadLines(linesPath, stationMap, graph);

        System.out.println("\nLoaded network:");
        System.out.println("  Stations: " + graph.numVertices());
        System.out.println("  Connections: " + graph.numEdges());
        System.out.println("  Valid lines: " + validLines);

        return graph;
    }

    /**
     * Load stations from stations.csv
     * Format: Station id,Station,Lat,Lon,CoordX,CoordY
     */
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

    /**
     * Load lines from lines.csv
     * Format: departure_stid,arrival_stid,dist,capacity,cost
     */
    private static int loadLines(String filePath, Map<String, Station> stationMap,
                                 Graph<Station, Connection> graph) throws IOException {
        int validLines = 0;
        int errorLines = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line = br.readLine();

            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                try {
                    String[] parts = line.split(",");
                    if (parts.length >= 5) {
                        String fromId = parts[0].trim();
                        String toId = parts[1].trim();
                        double distance = Double.parseDouble(parts[2].trim());
                        int capacity = Integer.parseInt(parts[3].trim());
                        double cost = Double.parseDouble(parts[4].trim());

                        // Get stations
                        Station from = stationMap.get(fromId);
                        Station to = stationMap.get(toId);

                        if (from != null && to != null) {
                            Connection conn = new Connection(from, to, distance, capacity, cost);
                            graph.addEdge(from, to, conn);
                            validLines++;
                        } else {
                            errorLines++;
                            System.err.println("Warning: Unknown station(s) - from: " + fromId + ", to: " + toId);
                        }
                    }
                } catch (NumberFormatException e) {
                    errorLines++;
                    System.err.println("Warning: Invalid number format in line: " + line);
                }
            }
        }

        if (errorLines > 0) {
            System.out.println("  Error lines: " + errorLines);
        }

        return validLines;
    }
}