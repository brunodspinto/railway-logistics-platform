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
     * Carrega o grafo completo a partir de um ficheiro CSV.
     * Responsável por transformar o ficheiro de dados na estrutura RailGraph.
     */
    public RailGraph loadGraph(Path csvPath) throws Exception {
        return CsvGraphLoader.loadFromCsv(csvPath);
    }

    public List<Edge> computeBackbone(RailGraph graph) {
        return MinimalBackboneService.computeMinimalBackbone(graph);
    }

    public void exportToDot(RailGraph graph, List<Edge> backbone, Path dotPath) throws Exception {
        DotExporter.exportBackboneToDot(graph, backbone, dotPath);
    }
}