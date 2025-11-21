package org.example.usei08.service;

import org.example.domain.Station;
import org.example.service.SpatialQueryService;
import org.example.trees.StationIndexes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class SpatialQueryServiceTest {

    private SpatialQueryService service;
    private Station lisbonOriente;
    private Station lisbonApolonia;
    private Station porto;
    private Station madrid;
    private Station evora;

    @BeforeEach
    void setUp() {
        lisbonOriente = new Station("Lisbon Oriente", 38.7680, -9.1230, "PT", "Europe/Lisbon", "WET/GMT", true, true, false);
        lisbonApolonia = new Station("Lisbon Santa Apolonia", 38.7138, -9.1230, "PT", "Europe/Lisbon", "WET/GMT", true, false, false);
        porto = new Station("Porto Campanha", 41.1495, -8.6100, "PT", "Europe/Lisbon", "WET/GMT", true, true, false);
        madrid = new Station("Madrid Chamartin", 40.4720, -3.6820, "ES", "Europe/Madrid", "CET", true, true, false);
        evora = new Station("Evora", 38.5667, -7.9000, "PT", "Europe/Lisbon", "WET/GMT", false, false, false);

        List<Station> stations = List.of(lisbonOriente, lisbonApolonia, porto, madrid, evora);

        StationIndexes indexes = new StationIndexes();
        Map<Station, String> rejected = indexes.buildIndexes(stations);
        assertTrue(rejected.isEmpty());
        service = new SpatialQueryService(indexes);
    }

    @Test
    @DisplayName("Query: todas as estações dentro da bounding box sem filtros")
    void queryAreaReturnsAllStationsInsideBoxWithoutFilters() {
        List<Station> results = service.queryArea(37.0, 42.0, -10.0, -3.0, null, null, null);

        assertEquals(5, results.size());
        assertTrue(results.containsAll(List.of(lisbonOriente, lisbonApolonia, porto, madrid, evora)));
    }

    @Test
    @DisplayName("Query: Filtrar por país")
    void queryAreaAppliesCountryFilter() {
        List<Station> results = service.queryArea(37.0, 42.0, -10.0, -3.0, null, null, "PT");

        assertEquals(4, results.size());
        assertTrue(results.stream().allMatch(s -> s.getCountry().equalsIgnoreCase("PT")));
    }

    @Test
    @DisplayName("Query: Filtros combinados isCity e isMain")
    void queryAreaAppliesIsCityAndIsMainFiltersTogether() {
        List<Station> results = service.queryArea(37.0, 42.0, -10.0, -3.0, true, true, "PT");

        assertEquals(2, results.size());
        assertTrue(results.contains(lisbonOriente));
        assertTrue(results.contains(porto));
        assertTrue(results.stream().allMatch(Station::isCity));
        assertTrue(results.stream().allMatch(Station::isMainStation));
    }

    @Test
    @DisplayName("Query: Bounding Box exclui corretamente estações fora dos limites")
    void queryAreaRestrictsByBoundingBoxLeavingOutStationsOutsideRange() {
        List<Station> results = service.queryArea(38.0, 39.0, -10.0, -8.5, null, null, "PT");

        assertTrue(results.contains(lisbonOriente));
        assertTrue(results.contains(lisbonApolonia));
        assertEquals(2, results.size());
    }

    @Test
    @DisplayName("Query: bounding box sem resultados retorna lista vazia")
    void queryAreaReturnsEmptyListWhenNoStationsInBox() {
        List<Station> results = service.queryArea(50.0, 60.0, 0.0, 10.0, null, null, null);
        assertTrue(results.isEmpty());
    }
}