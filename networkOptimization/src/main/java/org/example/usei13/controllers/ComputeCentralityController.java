package org.example.usei13.controllers;

import org.example.usei12.domain.RailGraph;
import org.example.usei12.service.CsvGraphLoader;
import org.example.usei13.service.CentralityService;

import java.nio.file.Path;

/**
 * Controller da USEI13 responsável por coordenar o cálculo das métricas
 * de centralidade da rede ferroviária.
 */
public class ComputeCentralityController {

    /**
     * Carrega o grafo a partir de um ficheiro CSV e delega ao serviço
     * o cálculo e apresentação das métricas de centralidade.
     */
    public void compute(Path csvPath) {

        try {
            RailGraph graph = CsvGraphLoader.loadFromCsv(csvPath);

            CentralityService service = new CentralityService(graph);

            service.computeAndPrint();

        } catch (Exception e) {
            System.err.println(
                    "Erro ao carregar o grafo: " + e.getMessage()
            );
        }
    }
}