package org.example.usei12.service;

import org.example.usei12.domain.RailGraph;
import org.example.usei12.domain.Station;

import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Carregador de grafos para a USEI12.
 * Lê separadamente stations.csv (vértices) e lines.csv (arestas).
 */
public class CsvGraphLoader {

    /**
     * Carrega todas as estações a partir de stations.csv.
     * Este metodo cria TODOS os vértices antes de existirem arestas.
     */
    public static void loadStations(Path stationsCsvPath, RailGraph graph) throws Exception {

        try (BufferedReader br = Files.newBufferedReader(stationsCsvPath)) {

            String line = br.readLine(); // ignorar cabeçalho

            while ((line = br.readLine()) != null) {

                if (line.isBlank()) continue;

                String[] parts = line.split(",");
                if (parts.length < 5) continue;

                String id = parts[0].trim();
                String name = parts[1].trim();
                double lat = Double.parseDouble(parts[2].trim());
                double lon = Double.parseDouble(parts[3].trim());

                Station station = new Station(id, name, lat, lon);
                graph.addStation(station);
            }
        }
    }

    /**
     * Carrega as linhas ferroviárias a partir de lines.csv.
     * Cria arestas NÃO DIRIGIDAS com peso = distância.
     * Não cria estações — assume que já existem.
     */
    public static void loadLines(Path linesCsvPath, RailGraph graph) throws Exception {

        try (BufferedReader br = Files.newBufferedReader(linesCsvPath)) {

            String line = br.readLine(); // ignorar cabeçalho

            while ((line = br.readLine()) != null) {

                if (line.isBlank()) continue;

                String[] parts = line.split(",");
                if (parts.length < 4) continue;

                String fromId = parts[0].trim();
                String toId   = parts[1].trim();
                double length = Double.parseDouble(parts[2].trim());

                Station from = graph.getStationById(fromId);
                Station to   = graph.getStationById(toId);

                // só adiciona a aresta se ambas as estações existirem
                if (from != null && to != null) {
                    graph.addEdge(from, to, length);
                }
            }
        }
    }
}