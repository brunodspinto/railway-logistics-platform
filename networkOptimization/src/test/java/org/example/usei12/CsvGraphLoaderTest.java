package org.example.usei12;

import org.example.usei12.domain.RailGraph;
import org.example.usei12.domain.Station;
import org.example.usei12.service.CsvGraphLoader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.BufferedWriter;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class CsvGraphLoaderTest {

    /**
     * Garante que um CSV bem formado é corretamente convertido
     * num grafo com as estações e arestas esperadas.
     */
    @Test
    @DisplayName("CSV válido deve gerar o grafo correto com estações e arestas")
    void loadFromCsvBuildsCorrectGraph() throws Exception {
        Path tempCsv = Files.createTempFile("stations-test", ".csv");

        try (BufferedWriter w = Files.newBufferedWriter(tempCsv)) {
            w.write("geo;fromId;fromName;toId;toName;length;geo_point_2d");
            w.newLine();
            w.write("shape;1;A;2;B;10.5;50.0,4.0");
            w.newLine();
            w.write("shape;2;B;3;C;20.0;51.0,5.0");
            w.newLine();
        }

        RailGraph graph = CsvGraphLoader.loadFromCsv(tempCsv);

        assertEquals(3, graph.getStations().size());
        assertEquals(2, graph.getEdges().size());

        Station s1 = graph.getStationById("1");
        Station s2 = graph.getStationById("2");
        Station s3 = graph.getStationById("3");

        assertNotNull(s1);
        assertNotNull(s2);
        assertNotNull(s3);
    }
}