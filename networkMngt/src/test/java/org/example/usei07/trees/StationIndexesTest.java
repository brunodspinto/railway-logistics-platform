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

        // Verifica se a 2D-Tree foi construída corretamente (lógica do teste anterior)
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

        // Verifica se o relatório da USEI07 está presente [689]
        assertTrue(report.contains("Spatial Index (2D-Tree):"));
        assertTrue(report.contains("Size (Nodes): 3"));
        assertTrue(report.contains("Height: 2"));
        assertTrue(report.contains("Distinct Bucket Sizes: [1, 2]"));

        // Verifica se os relatórios da USEI06 também estão
        assertTrue(report.contains("Latitude Index"));
        assertTrue(report.contains("Longitude Index"));
        assertTrue(report.contains("TimeZone Index"));
    }
}