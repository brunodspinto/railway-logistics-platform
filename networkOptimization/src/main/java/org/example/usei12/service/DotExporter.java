package org.example.usei12.service;

import org.example.usei12.domain.Edge;
import org.example.usei12.domain.RailGraph;
import org.example.usei12.domain.Station;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DotExporter {

    // Se o Graphviz estiver noutro sítio, altera este caminho
    private static final String NEATO_PATH = "C:\\Program Files\\Graphviz\\bin\\neato.exe";

    /**
     * Exporta o backbone para um ficheiro DOT e gera automaticamente o SVG.
     */
    public static void exportBackboneToDot(RailGraph graph, List<Edge> backbone, Path dotPath) throws Exception {

        // recolhe apenas as estações que participam no backbone
        Set<Station> usedStations = new HashSet<>();
        for (Edge e : backbone) {
            usedStations.add(e.getFrom());
            usedStations.add(e.getTo());
        }

        // escreve o ficheiro DOT com posições e arestas
        try (PrintWriter out = new PrintWriter(Files.newBufferedWriter(dotPath))) {

            out.println("graph Backbone {");
            out.println("  graph [splines=true, overlap=false];");
            out.println("  node [shape=circle, fontsize=8];\n");

            for (Station s : usedStations) {
                String id = "s" + s.getId();
                out.printf("  %s [label=\"%s\", pos=\"%f,%f!\"];%n",
                        id, s.getName(), s.getLon(), s.getLat());
            }

            for (Edge e : backbone) {
                String u = "s" + e.getFrom().getId();
                String v = "s" + e.getTo().getId();
                out.printf("  %s -- %s [label=\"%.2f\"];%n",
                        u, v, e.getLength());
            }

            out.println("}");
        }

        // gera o ficheiro SVG usando o Graphviz (neato)
        String dotName = dotPath.toString();
        String svgName = dotName.toLowerCase().endsWith(".dot")
                ? dotName.substring(0, dotName.length() - 4) + ".svg"
                : dotName + ".svg";

        Path svgPath = Path.of(svgName);

        try {
            ProcessBuilder pb = new ProcessBuilder(
                    NEATO_PATH, "-Tsvg",
                    dotPath.toString(),
                    "-o", svgPath.toString()
            );

            Process process = pb.start();
            int exitCode = process.waitFor();

            if (exitCode != 0) {
                System.out.println("Falha ao gerar o SVG com o Graphviz (neato). Código de saída: " + exitCode);
            }

        } catch (IOException e) {
            System.out.println("Não foi possível encontrar/correr o Graphviz em: " + NEATO_PATH);
        }
    }
}