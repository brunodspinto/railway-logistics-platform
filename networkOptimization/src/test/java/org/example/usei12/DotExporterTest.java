package org.example.usei12;

import org.example.usei12.domain.Edge;
import org.example.usei12.domain.RailGraph;
import org.example.usei12.domain.Station;
import org.example.usei12.service.DotExporter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DotExporterTest {

    /**
     * Verifica que o DOT é gerado e contém os nós e arestas esperados.
     */
    @Test
    @DisplayName("Exportação deve criar um ficheiro DOT válido contendo nós e arestas")
    void exportBackboneToDotCreatesValidDotFile() throws Exception {
        RailGraph graph = new RailGraph();

        Station a = graph.getOrCreateStation("1", "A", 50.0, 4.0);
        Station b = graph.getOrCreateStation("2", "B", 51.0, 5.0);

        Edge e = graph.addEdge(a, b, 10.0);
        List<Edge> backbone = List.of(e);

        Path tempDir = Files.createTempDirectory("dot-test");
        Path dotPath = tempDir.resolve("backbone.dot");

        DotExporter.exportBackboneToDot(graph, backbone, dotPath);

        assertTrue(Files.exists(dotPath));

        String content = Files.readString(dotPath);

        assertTrue(content.contains("graph Backbone"));
        assertTrue(content.contains("s1"));
        assertTrue(content.contains("s2"));
        assertTrue(content.contains("s1 -- s2"));
    }
}