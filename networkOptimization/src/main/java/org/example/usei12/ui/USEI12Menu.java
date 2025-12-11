package org.example.usei12.ui;

import org.example.usei12.controllers.ComputeBackboneController;
import org.example.usei12.domain.Edge;
import org.example.usei12.domain.RailGraph;

import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;

public class USEI12Menu {

    private static final String DEFAULT_CSV = "res/station_to_station.csv";
    private static final String DEFAULT_DOT = "docs/backbone.dot";

    private final ComputeBackboneController controller;

    public USEI12Menu(ComputeBackboneController controller) {
        this.controller = controller;
    }

    /**
     * Interage com o utilizador e executa todo o processo:
     * ler ficheiros, carregar grafo, calcular backbone e gerar DOT/SVG.
     */
    public void start() {
        try {
            Scanner in = new Scanner(System.in);

            System.out.print("Caminho do ficheiro CSV [Default: " + DEFAULT_CSV + "]: ");
            String csvInput = in.nextLine().trim();
            if (csvInput.isEmpty()) {
                csvInput = DEFAULT_CSV;
            }

            System.out.print("Caminho do ficheiro DOT de saída [Default: " + DEFAULT_DOT + "]: ");
            String dotInput = in.nextLine().trim();
            if (dotInput.isEmpty()) {
                dotInput = DEFAULT_DOT;
            }

            Path csvPath = Path.of(csvInput);
            Path dotPath = Path.of(dotInput);

            // carregar, calcular e exportar resultados
            RailGraph graph = controller.loadGraph(csvPath);
            List<Edge> backbone = controller.computeBackbone(graph);
            controller.exportToDot(graph, backbone, dotPath);

            double total = backbone.stream()
                    .mapToDouble(Edge::getLength)
                    .sum();

            System.out.println();
            System.out.println("Backbone calculado com sucesso.");
            System.out.println("\nNúmero de arestas no backbone: " + backbone.size());
            System.out.printf("Comprimento total: %.2f km%n", total);

            System.out.println("DOT gerado em: " + dotPath.toAbsolutePath());

            String dotName = dotPath.toString();
            String svgName;
            if (dotName.toLowerCase().endsWith(".dot")) {
                svgName = dotName.substring(0, dotName.length() - 4) + ".svg";
            } else {
                svgName = dotName + ".svg";
            }
            Path svgPath = Path.of(svgName);
            System.out.println("SVG gerado em: " + svgPath.toAbsolutePath());

        } catch (Exception e) {
            System.out.println("Erro ao calcular o backbone: " + e.getMessage());
            e.printStackTrace();
        }
    }
}