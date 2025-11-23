package org.example.usei07.trees;

import org.example.domain.Station;
import org.example.trees.StationIndexes;
import org.example.trees.TwoDTree;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class StationIndexesTest {

    private StationIndexes indexes;
    private Station lisbonOriente, lisbonSantaApolonia, porto, faro, invalidStation;

    @BeforeEach
    void setUp() {
        indexes = new StationIndexes();

        // Estações Válidas
        lisbonOriente = new Station("Lisbon Oriente", 38.71387, -9.122271, "PT", "WET", "WET/GMT", true, true, false);
        lisbonSantaApolonia = new Station("Lisbon Santa Apolonia", 38.71387, -9.122271, "PT", "WET", "WET/GMT", true, true, false);
        porto = new Station("Porto Campanha", 41.14961, -8.58397, "PT", "WET", "WET/GMT", true, true, false);
        faro = new Station("Faro", 37.01757, -7.93041, "PT", "WET", "WET/GMT", true, true, false);

        // Estação Inválida
        invalidStation = new Station("", -200, 0, "PT", "WET", "WET/GMT", true, true, false);
    }

    @Test
    void testBuildIndexesCreatesAllTrees() {
        List<Station> allStations = List.of(lisbonOriente, lisbonSantaApolonia, porto, faro, invalidStation);

        Map<Station, String> rejected = indexes.buildIndexes(allStations);

        // Verifica rejeitadas
        assertEquals(1, rejected.size());
        assertTrue(rejected.containsKey(invalidStation));

        // Verifica estações válidas
        assertEquals(4, indexes.getTotalStations());

        // Verifica se todas as árvores (índices) foram criadas
        assertNotNull(indexes.getLatitudeIndex());
        assertNotNull(indexes.getLongitudeIndex());
        assertNotNull(indexes.getTimeZoneIndex());
        assertNotNull(indexes.getSpatialIndex());

        // Verifica se a 2D-Tree foi construída corretamente
        TwoDTree spatial = indexes.getSpatialIndex();
        assertEquals(3, spatial.size()); // 3 nós
        assertEquals(2, spatial.height()); // Equilibrada
        assertEquals(Set.of(1, 2), spatial.getDistinctBucketSizes());
    }

    @Test
    void testGetReportIncludesAllIndexes() {
        List<Station> allStations = List.of(lisbonOriente, lisbonSantaApolonia, porto, faro);
        indexes.buildIndexes(allStations);

        String report = indexes.getReport();

        assertTrue(report.contains("INDEX REPORT"), "Deve conter o título principal");
        assertTrue(report.contains("AVL Index"), "Deve conter o cabeçalho da tabela AVL");
        assertTrue(report.contains("Target Height"), "Deve conter a coluna de altura ideal/alvo");

        assertTrue(report.contains("Latitude"));
        assertTrue(report.contains("Longitude"));
        assertTrue(report.contains("TimeZone"));

        assertTrue(report.contains("SPATIAL INDEX (2D-Tree)"), "Deve conter o título da secção 2D");

        assertTrue(report.contains("Size (Nodes)"));
        assertTrue(report.contains("3"));

        assertTrue(report.contains("Height"));
        assertTrue(report.contains("2"));

        assertTrue(report.contains("Buckets (Distinct)"));
        assertTrue(report.contains("[1, 2]"));
    }
}