package org.example.usei14;

import org.example.domain.Connection;
import org.example.domain.Station;
import org.example.graph.Edge;
import org.example.graph.Graph;
import org.example.usei14.controllers.ComputeMaxFlowController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ComputeMaxFlowControllerTest {

    private ComputeMaxFlowController controller;

    @Mock
    private Graph<Station, Connection> mockGraph;

    @BeforeEach
    void setUp() throws Exception {
        controller = new ComputeMaxFlowController();

        // Injeta o mockGraph no campo privado "network"
        Field networkField = ComputeMaxFlowController.class.getDeclaredField("network");
        networkField.setAccessible(true);
        networkField.set(controller, mockGraph);
    }

    @Test
    void testGetStations_ReturnsSortedList() {
        Station s1 = new Station("S1", "Porto");
        Station s2 = new Station("S2", "Lisboa");
        Station s3 = new Station("S3", "Aveiro");

        // CORREÇÃO: Converter para ArrayList genuíno
        ArrayList<Station> unorderedStations = new ArrayList<>();
        unorderedStations.add(s2);
        unorderedStations.add(s1);
        unorderedStations.add(s3);

        doReturn(unorderedStations).when(mockGraph).vertices();

        List<Station> result = controller.getStations();

        assertNotNull(result, "A lista não deve ser nula");
        assertEquals(3, result.size(), "A lista deve conter 3 estações");

        assertEquals("Aveiro", result.get(0).getName());
        assertEquals("Lisboa", result.get(1).getName());
        assertEquals("Porto", result.get(2).getName());
    }

    @Test
    void testGetStations_EmptyGraph() throws Exception {
        Field networkField = ComputeMaxFlowController.class.getDeclaredField("network");
        networkField.setAccessible(true);
        networkField.set(controller, null);

        List<Station> result = controller.getStations();
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testCalculateMaxFlow_Success() {
        Station source = new Station("S1", "A");
        Station sink = new Station("S2", "B");

        // CORREÇÃO: Usar ArrayList explícito para vertices
        ArrayList<Station> vertices = new ArrayList<>();
        vertices.add(source);
        vertices.add(sink);

        // CORREÇÃO: Usar ArrayList explícito para edges
        ArrayList<Edge<Station, Connection>> edges = new ArrayList<>();

        lenient().doReturn(true).when(mockGraph).validVertex(any());
        // Força o retorno do tipo correto (ArrayList)
        lenient().doReturn(vertices).when(mockGraph).vertices();
        lenient().doReturn(edges).when(mockGraph).edges();

        Double result = controller.calculateMaxFlow(source, sink);

        assertNotNull(result);
        assertEquals(0.0, result);

        verify(mockGraph, atLeastOnce()).vertices();
    }

    @Test
    void testCalculateMaxFlow_NetworkNotLoaded() throws Exception {
        Field networkField = ComputeMaxFlowController.class.getDeclaredField("network");
        networkField.setAccessible(true);
        networkField.set(controller, null);

        Station s1 = new Station("S1", "A");
        Station s2 = new Station("S2", "B");

        assertThrows(IllegalStateException.class, () -> controller.calculateMaxFlow(s1, s2));
    }
}