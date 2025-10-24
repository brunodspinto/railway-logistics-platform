package org.example.ui.executors;

import org.example.domain.*;
import org.example.repository.*;
import org.example.results.*;
import org.example.ui.menu.DisplayHelper;
import org.example.usei04.service.PickingPlannerService;

import java.util.*;

public class USEI03Executor {

    // Alterado para devolver a lista de Trolleys (plano de picking)
    public static List<Trolley> execute(ItemRepository itemRepo, List<OrderAllocationResult> allocationResults) {
        DisplayHelper.printHeader("USEI03 - Picking Plans");

        try {
            // 1. Extrair todas as AllocationRows das OrderAllocationResults
            List<AllocationRow> allAllocations = new ArrayList<>();
            for (OrderAllocationResult result : allocationResults) {
                allAllocations.addAll(result.getAllocations());
            }

            if (allAllocations.isEmpty()) {
                DisplayHelper.printError("Nenhuma AllocationRow disponível para picking!");
                return Collections.emptyList(); // Devolve lista vazia se não houver nada a fazer
            }

            // 2. Criar mapa de pesos unitários
            Map<String, Double> unitWeights = new HashMap<>();
            for (Item item : itemRepo.findAll()) {
                unitWeights.put(item.getSku(), item.getUnitWeight());
            }

            // 3. Converter AllocationRow -> PickingItem
            List<PickingItem> pickingItems = new ArrayList<>();
            for (AllocationRow row : allAllocations) {
                double unitWeight = unitWeights.getOrDefault(row.getSku(), 1.0);
                pickingItems.add(new PickingItem(
                        row.getOrderId(),
                        row.getLineNo(),
                        row.getSku(),
                        row.getBoxId(),
                        row.getAisle(),
                        row.getBay(),
                        row.getQty(),
                        unitWeight
                ));
            }

            // 4. Definir capacidade do trolley
            Trolley.setCapacity(638.96);

            // 5. Gerar planos de picking
            PickingPlannerService planner = new PickingPlannerService();
            List<Trolley> planFF  = planner.generatePickingPlan(pickingItems, PickingPlannerService.Heuristic.FF);
            List<Trolley> planFFD = planner.generatePickingPlan(pickingItems, PickingPlannerService.Heuristic.FFD);
            List<Trolley> planBFD = planner.generatePickingPlan(pickingItems, PickingPlannerService.Heuristic.BFD);

            // 6. Mostrar resultados
            System.out.println("\n=== PICKING PLAN - FIRST FIT ===");
            PickingResult.printSummary("FIRST FIT", planFF);

            System.out.println("\n=== PICKING PLAN - FIRST FIT DECREASING ===");
            PickingResult.printSummary("FIRST FIT DECREASING", planFFD);

            System.out.println("\n=== PICKING PLAN - BEST FIT DECREASING ===");
            PickingResult.printSummary("BEST FIT DECREASING", planBFD);

            // 7. Comparação final
            System.out.println("\n📊 Summary Comparison:");
            System.out.printf(" FF  → %d trolleys%n", planFF.size());
            System.out.printf(" FFD → %d trolleys%n", planFFD.size());
            System.out.printf(" BFD → %d trolleys%n", planBFD.size());

            System.out.println();
            DisplayHelper.printSuccess("USEI03 completed successfully!");

            // Devolve o plano FFD para ser usado na USEI04
            return planFFD;

        } catch (Exception e) {
            DisplayHelper.printError("USEI03 execution failed: " + e.getMessage());
            e.printStackTrace();
            return Collections.emptyList(); // Devolve lista vazia em caso de erro
        }
    }
}