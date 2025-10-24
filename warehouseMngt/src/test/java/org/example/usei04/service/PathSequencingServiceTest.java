package org.example.usei04.service;

import org.example.domain.Record;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários para PathSequencingService (USEI04).
 */
class PathSequencingServiceTest {

    private PathSequencingService service;

    @BeforeEach
    void setUp() {
        service = new PathSequencingService();
    }

    // ----------------------------------------------------------
    // TESTES DO CÁLCULO DE DISTÂNCIAS
    // ----------------------------------------------------------

    @Test
    void testDistanceSameAisle() {
        Record r1 = new Record(1, 2);
        Record r2 = new Record(1, 8);

        double distance = service.calculateDistance(r1, r2);
        assertEquals(6.0, distance, 0.0001,
                "A distância no mesmo corredor deve ser a diferença vertical |b1 - b2|");
    }

    @Test
    void testDistanceDifferentAisles() {
        Record r1 = new Record(1, 8);
        Record r2 = new Record(3, 4);

        // b1 + |a1 - a2| * 3 + b2 = 8 + |1-3|*3 + 4 = 18
        double distance = service.calculateDistance(r1, r2);
        assertEquals(18.0, distance, 0.0001,
                "A distância entre corredores deve seguir a fórmula b1 + |a1 - a2|*3 + b2");
    }

    // ----------------------------------------------------------
    // TESTES DAS ESTRATÉGIAS
    // ----------------------------------------------------------

    @Test
    void testStrategyAExampleFromSpecification() {
        // Exemplo do enunciado USEI04 (pág. 14)
        List<Record> bays = List.of(
                new Record(1, 8),
                new Record(2, 2),
                new Record(3, 4)
        );

        PathSequencingService.PickPathResult result = service.sequenceByStrategyA(bays);

        // Ordem esperada: (1,8) → (2,2) → (3,4)
        List<Record> expectedPath = List.of(
                new Record(1, 8),
                new Record(2, 2),
                new Record(3, 4)
        );

        assertEquals(expectedPath, result.path, "O percurso da Estratégia A deve ser ordenado por corredor e baía.");
        assertEquals(33.0, result.totalDistance, 0.0001,
                "A distância total da Estratégia A deve ser 33 (segundo o exemplo do enunciado).");
    }

    @Test
    void testStrategyBExampleFromSpecification() {
        // Mesmo conjunto de bays do exemplo
        List<Record> bays = List.of(
                new Record(1, 8),
                new Record(2, 2),
                new Record(3, 4)
        );

        PathSequencingService.PickPathResult result = service.sequenceByStrategyB(bays);

        // Ordem esperada: (2,2) → (3,4) → (1,8)
        List<Record> expectedPath = List.of(
                new Record(2, 2),
                new Record(3, 4),
                new Record(1, 8)
        );

        assertEquals(expectedPath, result.path, "O percurso da Estratégia B deve seguir o algoritmo do vizinho mais próximo.");
        assertEquals(35.0, result.totalDistance, 0.0001,
                "A distância total da Estratégia B deve ser 35 (segundo o exemplo do enunciado).");
    }

    // ----------------------------------------------------------
    // TESTES DE CENÁRIOS SIMPLES
    // ----------------------------------------------------------

    @Test
    void testSingleBay() {
        List<Record> bays = List.of(new Record(1, 5));

        PathSequencingService.PickPathResult resultA = service.sequenceByStrategyA(bays);
        PathSequencingService.PickPathResult resultB = service.sequenceByStrategyB(bays);

        assertEquals(resultA.totalDistance, resultB.totalDistance, 0.0001,
                "Com apenas uma baía, ambas as estratégias devem ter a mesma distância.");
        assertEquals(8.0, resultA.totalDistance, 0.0001,
                "Distância esperada (0,0)->(1,5) = 0 + |0-1|*3 + 5 = 8");
    }

    @Test
    void testAllBaysInSameAisle() {
        List<Record> bays = List.of(
                new Record(1, 2),
                new Record(1, 5),
                new Record(1, 8)
        );

        PathSequencingService.PickPathResult resultA = service.sequenceByStrategyA(bays);
        PathSequencingService.PickPathResult resultB = service.sequenceByStrategyB(bays);

        // O percurso será igual porque todas as bays estão no mesmo corredor.
        assertEquals(resultA.path, resultB.path,
                "Se todas as bays estiverem no mesmo corredor, ambas as estratégias devem produzir o mesmo percurso.");
        assertEquals(resultA.totalDistance, resultB.totalDistance, 0.0001,
                "As distâncias devem coincidir no mesmo corredor.");
    }

    @Test
    void testDuplicateBaysAreMerged() {
        List<Record> bays = List.of(
                new Record(1, 2),
                new Record(1, 2),
                new Record(2, 3)
        );

        PathSequencingService.PickPathResult result = service.sequenceByStrategyA(bays);
        long uniqueCount = result.path.stream().distinct().count();

        assertEquals(2, uniqueCount, "As bays duplicadas devem ser fundidas num único ponto.");
    }

    @Test
    void testEmptyList() {
        List<Record> bays = List.of();

        double total = service.calculateTotalDistance(bays);
        assertEquals(0.0, total, "A distância total de uma lista vazia deve ser 0.");
    }
}
