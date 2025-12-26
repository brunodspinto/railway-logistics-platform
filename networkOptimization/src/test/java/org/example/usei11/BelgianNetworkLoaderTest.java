package org.example.usei11;

import org.example.domain.Connection;
import org.example.domain.Station;
import org.junit.jupiter.api.Test;
import org.example.graph.Graph;
import org.example.loader.BelgianNetworkLoader;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class BelgianNetworkLoaderTest {

    // ⭐⭐⭐ CAMINHO RELATIVO SAINDO DO MÓDULO ⭐⭐⭐
    private static final String STATIONS_PATH = "../res/stations.csv";
    private static final String LINES_PATH = "../res/lines.csv";

    @Test
    void testLoadNetwork_Success() throws IOException {
        // Act
        Graph<Station, Connection> graph =
                BelgianNetworkLoader.loadNetwork(STATIONS_PATH, LINES_PATH);

        // Assert
        assertNotNull(graph, "Graph should not be null");
        assertEquals(559, graph.numVertices(), "Should load 559 stations");
        assertEquals(691, graph.numEdges(), "Should load 691 connections");
        assertTrue(graph.isDirected(), "Graph should be directed");
    }

    @Test
    void testLoadNetwork_SpecificStations() throws IOException {
        // Act
        Graph<Station, Connection> graph =
                BelgianNetworkLoader.loadNetwork(STATIONS_PATH, LINES_PATH);

        // Assert
        boolean foundMons = false;
        boolean foundGhlin = false;
        boolean foundBrussels = false;

        for (Station station : graph.vertices()) {
            if (station.getName().equals("MONS")) foundMons = true;
            if (station.getName().equals("GHLIN")) foundGhlin = true;
            if (station.getName().contains("BRUXELLES")) foundBrussels = true;
        }

        assertTrue(foundMons, "Should find MONS station");
        assertTrue(foundGhlin, "Should find GHLIN station");
        assertTrue(foundBrussels, "Should find Brussels station");
    }

    @Test
    void testLoadNetwork_StationsFileNotFound() {
        // Act & Assert
        assertThrows(IOException.class,
                () -> BelgianNetworkLoader.loadNetwork("invalid/path.csv", LINES_PATH),
                "Should throw IOException when stations file not found");
    }

    @Test
    void testLoadNetwork_LinesFileNotFound() {
        // Act & Assert
        assertThrows(IOException.class,
                () -> BelgianNetworkLoader.loadNetwork(STATIONS_PATH, "invalid/path.csv"),
                "Should throw IOException when lines file not found");
    }

    @Test
    void testLoadNetwork_SpecificConnection() throws IOException {
        // Act
        Graph<Station, Connection> graph =
                BelgianNetworkLoader.loadNetwork(STATIONS_PATH, LINES_PATH);

        // Assert - Procurar MONS
        Station mons = null;
        for (Station s : graph.vertices()) {
            if (s.getId().equals("848")) { // ID de MONS
                mons = s;
                break;
            }
        }

        assertNotNull(mons, "MONS station should exist");
        assertTrue(graph.outDegree(mons) > 0,
                "MONS should have outgoing connections");
    }

    @Test
    void testLoadNetwork_IsDirected() throws IOException {
        // Act
        Graph<Station, Connection> graph =
                BelgianNetworkLoader.loadNetwork(STATIONS_PATH, LINES_PATH);

        // Assert
        assertTrue(graph.isDirected(),
                "Loaded graph must be directed for USEI11");
    }
}