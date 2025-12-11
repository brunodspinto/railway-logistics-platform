package org.example.loader;

import org.example.domain.Station;
import org.example.domain.Connection;
import org.example.graph.Graph;
import org.example.graph.map.MapGraph;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Carrega a rede ferroviária belga do dataset Infrabel
 */
public class BelgianNetworkLoader {

    public static Graph<Station, Connection> loadNetwork(String filePath)
            throws IOException {

        Graph<Station, Connection> graph = new MapGraph<>(true);
        Map<String, Station> stationCache = new HashMap<>();

        int validLines = 0;
        int errorLines = 0;

        try (BufferedReader br = new BufferedReader(
                new FileReader(filePath, StandardCharsets.UTF_8))) {

            // Ler header
            String header = br.readLine();
            if (header == null) {
                throw new IOException("Empty file");
            }

            // Remover BOM se existir
            if (header.startsWith("\uFEFF")) {
                header = header.substring(1);
            }

            System.out.println("Loading Belgian railway network...");

            // Detectar separador do header
            char delimiter = detectDelimiter(header);
            System.out.printf("Detected delimiter: '%s'%n",
                    delimiter == ';' ? "semicolon (;)" :
                            delimiter == '\t' ? "tab" :
                                    "comma (,)");

            String line;
            int lineNum = 0;
            while ((line = br.readLine()) != null) {
                lineNum++;

                try {
                    if (processLine(line, delimiter, graph, stationCache, validLines)) {
                        validLines++;
                    }
                } catch (Exception e) {
                    errorLines++;
                    if (errorLines <= 10) {
                        System.err.printf("Line %d error: %s%n", lineNum + 1, e.getMessage());
                    }
                }
            }
        }

        if (errorLines > 10) {
            System.err.printf("... and %d more errors%n", errorLines - 10);
        }

        System.out.printf("%nLoaded network:%n");
        System.out.printf("  Stations: %d%n", graph.numVertices());
        System.out.printf("  Connections: %d%n", graph.numEdges());
        System.out.printf("  Valid lines: %d%n", validLines);
        System.out.printf("  Error lines: %d%n", errorLines);

        return graph;
    }

    /**
     * Detecta o separador do CSV
     */
    private static char detectDelimiter(String header) {
        int semicolons = countChar(header, ';');
        int tabs = countChar(header, '\t');
        int commas = countChar(header, ',');

        // O maior vence
        if (semicolons > tabs && semicolons > commas) {
            return ';';
        } else if (tabs > commas) {
            return '\t';
        } else {
            return ',';
        }
    }

    private static int countChar(String str, char ch) {
        int count = 0;
        for (char c : str.toCharArray()) {
            if (c == ch) count++;
        }
        return count;
    }

    /**
     * Processa uma linha do CSV
     */
    private static boolean processLine(String line,
                                       char delimiter,
                                       Graph<Station, Connection> graph,
                                       Map<String, Station> cache,
                                       int validLines) {

        List<String> fields = parseLine(line, delimiter);

        // Debug primeiras 3 linhas válidas
        if (validLines < 3) {
            System.out.printf("%n=== DEBUG Line %d ===%n", validLines + 1);
            System.out.printf("Raw line length: %d chars%n", line.length());
            System.out.printf("Fields parsed: %d%n", fields.size());
            for (int i = 0; i < Math.min(fields.size(), 7); i++) {
                String preview = fields.get(i).trim();
                if (preview.length() > 60) {
                    preview = preview.substring(0, 60) + "...";
                }
                System.out.printf("  [%d]: %s%n", i, preview);
            }
        }

        if (fields.size() < 7) {
            throw new IllegalArgumentException(
                    String.format("Expected 7 fields, got %d", fields.size()));
        }

        // Índices conforme header:
        // 0: Geo Shape (ignorar)
        // 1: Station van vertrek (id)
        String fromId = cleanField(fields.get(1));
        // 2: Station van vertrek (name)
        String fromName = cleanField(fields.get(2));
        // 3: Aankomstation (id)
        String toId = cleanField(fields.get(3));
        // 4: Aankomststation (name)
        String toName = cleanField(fields.get(4));
        // 5: Lengte (distance)
        double distance = parseDouble(cleanField(fields.get(5)));
        // 6: geo_point_2d
        String geoPoint = cleanField(fields.get(6));
        double[] coords = parseGeoPoint(geoPoint);

        // Validações
        if (fromId.isEmpty() || toId.isEmpty()) {
            return false;
        }
        if (distance < 0) {
            return false;
        }

        // Criar/obter estações
        Station from = cache.computeIfAbsent(fromId, k -> {
            Station s = new Station(fromId, fromName, coords[0], coords[1]);
            graph.addVertex(s);
            return s;
        });

        Station to = cache.computeIfAbsent(toId, k -> {
            Station s = new Station(toId, toName, coords[0], coords[1]);
            graph.addVertex(s);
            return s;
        });

        // Adicionar conexão
        Connection conn = new Connection(from, to, distance);
        graph.addEdge(from, to, conn);

        return true;
    }

    /**
     * Parse de linha CSV com suporte a JSON nested
     */
    private static List<String> parseLine(String line, char delimiter) {
        List<String> fields = new ArrayList<>();
        StringBuilder current = new StringBuilder();

        boolean inQuotes = false;
        int braceDepth = 0;    // {}
        int bracketDepth = 0;  // []

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            // Tracking JSON structures (quando não está em quotes)
            if (!inQuotes) {
                if (c == '{') {
                    braceDepth++;
                } else if (c == '}') {
                    braceDepth--;
                } else if (c == '[') {
                    bracketDepth++;
                } else if (c == ']') {
                    bracketDepth--;
                }
            }

            // Tracking quotes
            if (c == '"' && (i == 0 || line.charAt(i-1) != '\\')) {
                inQuotes = !inQuotes;
            }

            // É um separador se:
            // - é o delimiter
            // - NÃO está em quotes
            // - NÃO está dentro de JSON (depth = 0)
            boolean isSeparator = (c == delimiter) &&
                    !inQuotes &&
                    braceDepth == 0 &&
                    bracketDepth == 0;

            if (isSeparator) {
                fields.add(current.toString());
                current = new StringBuilder();
            } else {
                current.append(c);
            }
        }

        // Último campo
        fields.add(current.toString());

        return fields;
    }

    /**
     * Limpa campo: remove quotes externas e espaços
     */
    private static String cleanField(String field) {
        field = field.trim();

        // Remove quotes externas se existirem
        if (field.startsWith("\"") && field.endsWith("\"")) {
            field = field.substring(1, field.length() - 1);
        }

        // Substitui "" por " (escape de quotes em CSV)
        field = field.replace("\"\"", "\"");

        return field.trim();
    }

    /**
     * Parse de geo_point_2d: "lat, lon"
     */
    private static double[] parseGeoPoint(String geoPoint) {
        try {
            String[] parts = geoPoint.split(",");
            if (parts.length >= 2) {
                double lat = Double.parseDouble(parts[0].trim());
                double lon = Double.parseDouble(parts[1].trim());

                // Validar ranges
                if (lat < -90 || lat > 90 || lon < -180 || lon > 180) {
                    return new double[]{0.0, 0.0};
                }

                return new double[]{lat, lon};
            }
        } catch (NumberFormatException e) {
            // Ignore
        }
        return new double[]{0.0, 0.0};
    }

    /**
     * Parse de double
     */
    private static double parseDouble(String value) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
}