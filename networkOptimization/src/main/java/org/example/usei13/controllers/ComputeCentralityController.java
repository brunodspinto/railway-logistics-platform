package org.example.usei13.controllers;

import org.example.usei12.domain.RailGraph;
import org.example.usei12.service.CsvGraphLoader;
import org.example.usei13.service.CentralityService;

import java.nio.file.Path;

/**
 * Controller da USEI13 responsável por coordenar o cálculo
 * das métricas de centralidade da rede ferroviária.
 */
public class ComputeCentralityController {

    /**
     * Carrega o grafo a partir dos ficheiros stations.csv e lines.csv
     * e delega o cálculo das métricas ao CentralityService.
     */
    public void compute(Path stationsCsvPath, Path linesCsvPath) {

        try {
            // 1. Criar grafo vazio
            RailGraph graph = new RailGraph();

            // 2. Carregar vértices (estações)
            CsvGraphLoader.loadStations(stationsCsvPath, graph);

            // 3. Carregar arestas (linhas)
            CsvGraphLoader.loadLines(linesCsvPath, graph);

            // 4. Calcular centralidades
            CentralityService service = new CentralityService(graph);
            service.computeAndPrint();

        } catch (Exception e) {
            System.err.println("Erro ao carregar o grafo: " + e.getMessage());
        }
    }
}