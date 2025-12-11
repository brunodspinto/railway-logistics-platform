package org.example.usei12.service;

import org.example.usei12.domain.RailGraph;
import org.example.usei12.domain.Station;

import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Path;

public class CsvGraphLoader {

    /**
     * Lê o ficheiro CSV e constrói o RailGraph.
     * Para cada linha válida: cria/obtém as duas estações e adiciona a aresta correspondente.
     */
    public static RailGraph loadFromCsv(Path csvPath) throws Exception {
        RailGraph graph = new RailGraph();

        try (BufferedReader br = Files.newBufferedReader(csvPath)) {

            String line = br.readLine(); // ignorar cabeçalho

            while ((line = br.readLine()) != null) {

                if (line.isBlank()) continue;

                String[] parts = line.split(";");
                if (parts.length < 7) continue;

                String fromId = parts[1].trim();
                String fromName = parts[2].trim();
                String toId = parts[3].trim();
                String toName = parts[4].trim();
                double length = Double.parseDouble(parts[5].trim());

                String[] coords = parts[6].split(",");
                double lat = Double.parseDouble(coords[0].trim());
                double lon = Double.parseDouble(coords[1].trim());

                Station from = graph.getOrCreateStation(fromId, fromName, lat, lon);
                Station to = graph.getOrCreateStation(toId, toName, lat, lon);

                graph.addEdge(from, to, length);
            }
        }

        return graph;
    }
}