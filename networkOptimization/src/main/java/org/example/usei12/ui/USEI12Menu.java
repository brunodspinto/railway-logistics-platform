package org.example.usei12.ui;

import org.example.usei12.controllers.ComputeBackboneController;
import org.example.usei12.domain.Edge;
import org.example.usei12.domain.RailGraph;

import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;

public class USEI12Menu {

    private static final String DEFAULT_STATIONS_CSV = "res/stations.csv";
    private static final String DEFAULT_LINES_CSV    = "res/lines.csv";
    private static final String DEFAULT_DOT          = "docs/backbone.dot";

    private final ComputeBackboneController controller;

    public USEI12Menu(ComputeBackboneController controller) {
        this.controller = controller;
    }

    /**
     * Interage com o utilizador e executa todo o processo:
     * carregar estações + linhas, calcular backbone e gerar DOT/SVG.
     */
    public void start() {
        try {
            Scanner in = new Scanner(System.in);

            System.out.print("Caminho do ficheiro stations.csv [Default: " + DEFAULT_STATIONS_CSV + "]: ");
            String stationsInput = in.nextLine().trim();
            if (stationsInput.isEmpty()) {
                stationsInput = DEFAULT_STATIONS_CSV;
            }

            System.out.print("Caminho do ficheiro lines.csv [Default: " + DEFAULT_LINES_CSV + "]: ");
            String linesInput = in.nextLine().trim();
            if (linesInput.isEmpty()) {
                linesInput = DEFAULT_LINES_CSV;
            }

            System.out.print("Caminho do ficheiro DOT de saída [Default: " + DEFAULT_DOT + "]: ");
            String dotInput = in.nextLine().trim();
            if (dotInput.isEmpty()) {
                dotInput = DEFAULT_DOT;
            }

            Path stationsPath = Path.of(stationsInput);
            Path linesPath    = Path.of(linesInput);
            Path dotPath      = Path.of(dotInput);

            // carregar grafo
            RailGraph graph = controller.loadGraph(stationsPath, linesPath);

            // calcular backbone (MST)
            List<Edge> backbone = controller.computeBackbone(graph);

            // exportar DOT
            controller.exportToDot(graph, backbone, dotPath);

            double total = backbone.stream().mapToDouble(Edge::getLength).sum();

            System.out.println();
            System.out.println("Backbone calculado com sucesso.\n");
            System.out.println("Número de arestas no backbone: " + backbone.size());
            System.out.printf("Comprimento total: %.2f km%n", total);

            System.out.println("DOT gerado em: " + dotPath.toAbsolutePath());

            String dotName = dotPath.toString();
            String svgName = dotName.toLowerCase().endsWith(".dot")
                    ? dotName.substring(0, dotName.length() - 4) + ".svg"
                    : dotName + ".svg";

            Path svgPath = Path.of(svgName);
            System.out.println("SVG gerado em: " + svgPath.toAbsolutePath());

        } catch (Exception e) {
            System.out.println("Erro ao calcular o backbone: " + e.getMessage());
            e.printStackTrace();
        }
    }
}