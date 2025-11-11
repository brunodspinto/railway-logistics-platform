package org.example.usei07.trees;

import org.example.domain.Station;
import org.example.trees.Node2D;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class Node2DTest {

    private Station sA, sB, sC;

    @BeforeEach
    void setUp() {
        sA = new Station("A-Station", 40, -8, "PT", "WET", "WET/GMT", true, true, false);
        sB = new Station("B-Station", 40, -8, "PT", "WET", "WET/GMT", true, true, false);
        sC = new Station("C-Station", 40, -8, "PT", "WET", "WET/GMT", true, true, false);
    }

    @Test
    void testConstructor() {
        Node2D node = new Node2D(sB, 0); // Axis 0 (latitude)
        assertEquals(1, node.getBucketSize());
        assertEquals(sB, node.getStations().get(0));
        assertEquals(40.0, node.getSplitCoordinate());
        assertEquals(0, node.getAxis());
    }

    @Test
    void testAddStationMaintainsNameSortOrder() {
        // Teste crucial para o requisito [687]
        Node2D node = new Node2D(sB, 0); // Começa com "B"

        // Adiciona "C" (depois) e "A" (antes)
        node.addStation(sC);
        node.addStation(sA);

        List<Station> stations = node.getStations();

        // Verifica o tamanho e a ordem
        assertEquals(3, node.getBucketSize());
        assertEquals("A-Station", stations.get(0).getName());
        assertEquals("B-Station", stations.get(1).getName());
        assertEquals("C-Station", stations.get(2).getName());
    }

    @Test
    void testAddStationIgnoresTrueDuplicates() {
        // Se a estação (mesmo nome, mesmas coords) for adicionada, deve ser ignorada
        Node2D node = new Node2D(sA, 0);
        node.addStation(sA); // Adiciona o mesmo objeto
        node.addStation(new Station("A-Station", 40, -8, "PT", "WET", "WET/GMT", true, true, false)); // Adiciona um "igual"

        assertEquals(1, node.getBucketSize());
    }

    @Test
    void testGetCoordinateHelper() {
        assertEquals(40.0, Node2D.getCoordinate(sA, 0)); // Axis 0 = Latitude
        assertEquals(-8.0, Node2D.getCoordinate(sA, 1)); // Axis 1 = Longitude
    }
}