package org.example.ui.executors;

import org.example.domain.*;
import org.example.repository.*;
import org.example.results.*;
import org.example.ui.menu.DisplayHelper;
import org.example.service.PickingPlannerService;

import java.util.*;

public class USEI03Executor {

    public static List<Trolley> execute(ItemRepository itemRepo, List<OrderAllocationResult> allocationResults) {
        DisplayHelper.printHeader("USEI03 - Picking Plans");

        Scanner sc = new Scanner(System.in);

        try {
            // 1. Extrair todas as AllocationRows das OrderAllocationResults
            List<AllocationRow> allAllocations = new ArrayList<>();
            for (OrderAllocationResult result : allocationResults) {
                allAllocations.addAll(result.getAllocations());
            }

            if (allAllocations.isEmpty()) {
                DisplayHelper.printError("Nenhuma AllocationRow disponível para picking!");
                return Collections.emptyList();
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

            // 🔹 Calcular o peso máximo de um item
            double maxItemWeight = pickingItems.stream()
                    .mapToDouble(PickingItem::getTotalWeight)
                    .max()
                    .orElse(0.0);

            // 4. Pedir ao utilizador a capacidade do trolley, garantindo que é suficiente
            double capacity;
            while (true) {
                System.out.print("\n➡️  Introduza a capacidade do trolley (kg): ");
                capacity = sc.nextDouble();

                if (capacity < maxItemWeight) {
                    DisplayHelper.printError(String.format(
                            "⚠️  A caixa mais pesada (%.2f kg) não cabe no trolley! Introduza uma capacidade maior.",
                            maxItemWeight));
                } else {
                    break; // capacidade válida
                }
            }

            Trolley.setCapacity(capacity);

            // 5. Gerar planos
            PickingPlannerService planner = new PickingPlannerService();
            List<Trolley> planFF  = planner.generatePickingPlan(pickingItems, PickingPlannerService.Heuristic.FF);
            List<Trolley> planFFD = planner.generatePickingPlan(pickingItems, PickingPlannerService.Heuristic.FFD);
            List<Trolley> planBFD = planner.generatePickingPlan(pickingItems, PickingPlannerService.Heuristic.BFD);

            // 6. Escolher o plano a visualizar
            System.out.println("\nSelecione o plano de picking a visualizar:");
            System.out.println("1 - FIRST FIT (FF)");
            System.out.println("2 - FIRST FIT DECREASING (FFD)");
            System.out.println("3 - BEST FIT DECREASING (BFD)");
            System.out.print("Opção: ");
            int opcao = sc.nextInt();

            List<Trolley> chosenPlan;
            String planName;

            switch (opcao) {
                case 1 -> { chosenPlan = planFF; planName = "FIRST FIT"; }
                case 2 -> { chosenPlan = planFFD; planName = "FIRST FIT DECREASING"; }
                case 3 -> { chosenPlan = planBFD; planName = "BEST FIT DECREASING"; }
                default -> {
                    DisplayHelper.printError("Opção inválida! A mostrar plano FFD por defeito.");
                    chosenPlan = planFFD;
                    planName = "FIRST FIT DECREASING";
                }
            }

            // 7. Mostrar plano escolhido
            System.out.printf("\n=== PICKING PLAN ===%n");
            PickingResult.printSummary(planName, chosenPlan);

            // 8. Resumo do plano escolhido
            System.out.println("\n📊 Summary:");

            switch (opcao) {
                case 1 -> System.out.printf(" FF  → %d trolleys%n", planFF.size());
                case 2 -> System.out.printf(" FFD → %d trolleys%n", planFFD.size());
                case 3 -> System.out.printf(" BFD → %d trolleys%n", planBFD.size());
                default -> System.out.println("Nenhum resumo disponível (opção inválida).");
            }

            System.out.println();

            DisplayHelper.printSuccess("USEI03 completed successfully!");

            // Devolver o plano escolhido para a USEI04
            return chosenPlan;

        } catch (Exception e) {
            DisplayHelper.printError("USEI03 execution failed: " + e.getMessage());
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}