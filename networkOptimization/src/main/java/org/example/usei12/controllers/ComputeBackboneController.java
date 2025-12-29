package org.example.usei12.controllers;

import org.example.usei12.domain.Edge;
import org.example.usei12.domain.RailGraph;
import org.example.usei12.service.CsvGraphLoader;
import org.example.usei12.service.MinimalBackboneService;
import org.example.usei12.service.DotExporter;

import java.nio.file.Path;
import java.util.List;

public class ComputeBackboneController {

    /**
     * Carrega o grafo completo a partir dos ficheiros stations.csv e lines.csv.
     * Primeiro carrega os vértices (estações) e só depois as arestas (linhas).
     */
    public RailGraph loadGraph(Path stationsCsvPath, Path linesCsvPath) throws Exception {
        RailGraph graph = new RailGraph();

        CsvGraphLoader.loadStations(stationsCsvPath, graph);
        CsvGraphLoader.loadLines(linesCsvPath, graph);

        return graph;
    }

    /**
     * Calcula a Minimal Backbone Network (MST) usando Kruskal.
     */
    public List<Edge> computeBackbone(RailGraph graph) {
        return MinimalBackboneService.computeMinimalBackbone(graph);
    }

    /**
     * Exporta o backbone para ficheiro DOT, usando as coordenadas reais das estações.
     */
    public void exportToDot(RailGraph graph, List<Edge> backbone, Path dotPath) throws Exception {
        DotExporter.exportBackboneToDot(graph, backbone, dotPath);
    }
}