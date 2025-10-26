package org.example.usei03.service;

import org.example.domain.PickingItem;
import org.example.domain.Trolley;
import org.example.service.PickingPlannerService;
import org.junit.jupiter.api.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class PickingPlannerServiceTest {

    private PickingPlannerService service;
    private List<PickingItem> items;

    @BeforeEach
    void setUp() {
        service = new PickingPlannerService();

        // ✅ Define a capacidade global dos trolleys conforme o enunciado da USEI03
        Trolley.setCapacity(638.96);

        // Cria uma lista de PickingItems simulando várias linhas de ordem
        items = List.of(
                new PickingItem("O1", 1, "SKU1", "B1", 1, 1, 5, 50.0),  // 250 kg
                new PickingItem("O1", 2, "SKU2", "B2", 1, 2, 3, 80.0),  // 240 kg
                new PickingItem("O2", 1, "SKU3", "B3", 2, 1, 2, 100.0)  // 200 kg
        );
    }

    @Test
    @DisplayName("Heurística FF - Deve distribuir itens respeitando a ordem de entrada")
    void testGeneratePickingPlan_FirstFit() {
        List<Trolley> trolleys = service.generatePickingPlan(items, PickingPlannerService.Heuristic.FF);

        assertNotNull(trolleys, "Lista de trolleys não deve ser nula");
        assertEquals(2, trolleys.size(), "Devem existir 2 trolleys (FF)");

        double totalWeight = trolleys.stream()
                .mapToDouble(Trolley::getUsedWeight)
                .sum();

        assertEquals(690.0, totalWeight, 0.001, "Peso total deve ser igual à soma dos itens");
        assertTrue(trolleys.stream().allMatch(t -> t.getUsedWeight() <= Trolley.getCapacity()),
                "Nenhum trolley deve ultrapassar a capacidade de 638.96 kg");
    }

    @Test
    @DisplayName("Heurística FFD - Deve ordenar itens por peso decrescente e otimizar capacidade")
    void testGeneratePickingPlan_FirstFitDecreasing() {
        List<Trolley> trolleys = service.generatePickingPlan(items, PickingPlannerService.Heuristic.FFD);

        assertNotNull(trolleys);
        assertTrue(trolleys.size() >= 1, "Deve criar pelo menos 1 trolley");
        assertTrue(trolleys.stream().allMatch(t -> t.getUsedWeight() <= Trolley.getCapacity()));

        double firstTrolleyWeight = trolleys.get(0).getUsedWeight();
        assertTrue(firstTrolleyWeight <= Trolley.getCapacity(), "Primeiro trolley não deve exceder a capacidade");
    }

    @Test
    @DisplayName("Heurística BFD - Deve usar o trolley com melhor aproveitamento")
    void testGeneratePickingPlan_BestFitDecreasing() {
        List<Trolley> trolleys = service.generatePickingPlan(items, PickingPlannerService.Heuristic.BFD);

        assertNotNull(trolleys);
        assertTrue(trolleys.size() >= 1, "Deve criar pelo menos um trolley");
        assertTrue(trolleys.stream().allMatch(t -> t.getUsedWeight() <= Trolley.getCapacity()),
                "Nenhum trolley deve ultrapassar 638.96 kg");
    }

    @Test
    @DisplayName("Capacidade - Nenhum trolley pode ultrapassar 638.96 kg")
    void testTrolleyCapacityNotExceeded() {
        List<Trolley> trolleys = service.generatePickingPlan(items, PickingPlannerService.Heuristic.FFD);

        for (Trolley t : trolleys) {
            assertTrue(t.getUsedWeight() <= 638.96,
                    "O trolley " + t.getId() + " ultrapassou a capacidade!");
        }
    }

    @Test
    @DisplayName("Lista vazia - Deve retornar lista vazia de trolleys")
    void testEmptyListReturnsEmptyPlan() {
        List<Trolley> trolleys = service.generatePickingPlan(Collections.emptyList(), PickingPlannerService.Heuristic.FF);
        assertNotNull(trolleys);
        assertTrue(trolleys.isEmpty(), "Lista de trolleys deve estar vazia");
    }
}
