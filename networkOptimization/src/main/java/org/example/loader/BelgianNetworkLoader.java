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

    // Capacidade por defeito caso não exista no CSV (para evitar fluxo 0 na USEI14)
    private static final int DEFAULT_CAPACITY = 50;
    private static final double DEFAULT_COST = 0.0;

    public static Graph<Station, Connection> loadNetwork(String stationsPath, String linesPath)
            throws IOException {

        System.out.println("Loading Belgian railway network...");

        // 1. Load stations first
        Map<String, Station> stationMap = loadStations(stationsPath);
        System.out.println("Loaded " + stationMap.size() + " stations");

        // 2. Create graph (Directed = true, pois o fluxo é direcional)
        Graph<Station, Connection> graph = new MapGraph<>(true);

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
            String line = br.readLine(); // Skip header if exists, or handle in loop

            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split(",");
                // Formato mínimo esperado: ID, Nome
                if (parts.length >= 2) {
                    String id = parts[0].trim();
                    String name = parts[1].trim();

                    // Podes adicionar coordenadas aqui se o construtor de Station suportar
                    Station station = new Station(id, name);
                    stations.put(id, station);
                }
            }
        }
        return stations;
    }

    /**
     * Load lines from lines.csv
     * Format esperado: departure_stid,arrival_stid,dist,capacity,cost
     * Se faltarem colunas, usa defaults.
     */
    private static int loadLines(String filePath, Map<String, Station> stationMap,
                                 Graph<Station, Connection> graph) throws IOException {
        int validLines = 0;
        int errorLines = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line = br.readLine(); // Skip header

            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                try {
                    String[] parts = line.split(",");

                    // Mínimo aceitável: Origem, Destino, Distância
                    if (parts.length >= 3) {
                        String fromId = parts[0].trim();
                        String toId = parts[1].trim();
                        double distance = Double.parseDouble(parts[2].trim());

                        // Leitura robusta: Se não houver coluna 3 ou 4, usa DEFAULT
                        int capacity = DEFAULT_CAPACITY;
                        double cost = DEFAULT_COST;

                        if (parts.length >= 4 && !parts[3].trim().isEmpty()) {
                            try {
                                capacity = Integer.parseInt(parts[3].trim());
                            } catch (NumberFormatException e) {
                                System.err.println("Aviso: Capacidade inválida na linha, usando default: " + DEFAULT_CAPACITY);
                            }
                        }

                        if (parts.length >= 5 && !parts[4].trim().isEmpty()) {
                            try {
                                cost = Double.parseDouble(parts[4].trim());
                            } catch (NumberFormatException e) {
                            }
                        }

                        // Obter estações do mapa
                        Station from = stationMap.get(fromId);
                        Station to = stationMap.get(toId);

                        if (from != null && to != null) {
                            Connection conn = new Connection(from, to, distance, capacity, cost);

                            graph.addEdge(from, to, conn);
                            validLines++;
                        } else {
                            errorLines++;
                        }
                    }
                } catch (NumberFormatException e) {
                    errorLines++;
                    System.err.println("Warning: Invalid number format in line: " + line);
                }
            }
        }

        if (errorLines > 0) {
            System.out.println("  Ignored lines (errors/unknown stations): " + errorLines);
        }

        return validLines;
    }
}