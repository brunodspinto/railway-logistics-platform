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

    @Test
    @DisplayName("CSV válidos devem gerar o grafo correto com estações e arestas")
    void loadStationsAndLinesBuildCorrectGraph() throws Exception {

        Path stationsCsv = Files.createTempFile("stations-test", ".csv");
        Path linesCsv    = Files.createTempFile("lines-test", ".csv");

        // stations.csv — loader exige >= 5 colunas
        try (BufferedWriter w = Files.newBufferedWriter(stationsCsv)) {
            w.write("id;name,lat,lon,dummy");
            w.newLine();
            w.write("1,A,50.0,4.0,x");
            w.newLine();
            w.write("2,B,51.0,5.0,x");
            w.newLine();
            w.write("3,C,52.0,6.0,x");
            w.newLine();
        }

        // lines.csv — loader exige >= 4 colunas
        try (BufferedWriter w = Files.newBufferedWriter(linesCsv)) {
            w.write("fromId;toId;length;dummy");
            w.newLine();
            w.write("1,2,10.5,x");
            w.newLine();
            w.write("2,3,20.0,x");
            w.newLine();
        }

        RailGraph graph = new RailGraph();
        CsvGraphLoader.loadStations(stationsCsv, graph);
        CsvGraphLoader.loadLines(linesCsv, graph);

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