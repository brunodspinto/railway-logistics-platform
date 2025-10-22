package org.example.ui.menu;

import org.example.domain.Trolley;
import org.example.repository.*;
import org.example.results.OrderAllocationResult;
import org.example.ui.executors.*;

import java.util.List;
import java.util.Scanner;

/**
 * Main Simplificada - Coordena a execução das User Stories
 */
public class MainGeral implements Runnable {

    // As constantes dos ficheiros mantêm-se
    private static final String ITEMS_FILE = "res/Data/items.csv";
    private static final String BAYS_FILE = "res/Data/bays.csv";
    private static final String WAGONS_FILE = "res/Data/wagons.csv";
    private static final String ORDER_LINES_FILE = "res/Data/order_lines.csv";
    private static final String RETURNS_FILE = "res/Data/returns.csv";

    @Override
    public void run() {
        // Mensagem atualizada para incluir todas as USEIs
        System.out.println("\n=== Running Full Program Flow (USEI01–USEI05) ===\n");
        executeAll();

        // Pausa para o utilizador poder ler o output antes de voltar ao menu
        System.out.println("\n--- FIM DO PROGRAMA ---");
        System.out.println("Pressione Enter para voltar ao Menu Principal...");
        new Scanner(System.in).nextLine();
    }

    // O método main pode ser mantido para testes diretos
    public static void main(String[] args) {
        new MainGeral().executeAll();
    }

    private void executeAll() {
        try {
            // Setup compartilhado
            ItemRepository itemRepo = new ItemRepository();
            WarehouseRepository warehouseRepo = new WarehouseRepository();

            // =============================================================
            // ▶ USEI01 - Warehouse Loading
            // =============================================================
            USEI01Executor.execute(itemRepo, warehouseRepo, ITEMS_FILE, BAYS_FILE, WAGONS_FILE);

            // =============================================================
            // ▶ USEI02 - Order Allocation
            // =============================================================
            List<OrderAllocationResult> allocationResults = USEI02Executor.execute(warehouseRepo, ORDER_LINES_FILE);

            // =============================================================
            // ▶ FLUXO DEPENDENTE (USEI03 -> USEI04)
            // =============================================================
            if (allocationResults != null && !allocationResults.isEmpty()) {
                // ▶ USEI03 - Picking Plans (agora retorna o plano)
                List<Trolley> pickingPlan = USEI03Executor.execute(itemRepo, allocationResults);

                // ▶ USEI04 - Pick Path Sequencing (usa o resultado da USEI03)
                if (pickingPlan != null && !pickingPlan.isEmpty()) {
                    USEI04Executor.execute(pickingPlan);
                } else {
                    System.out.println("⚠️ USEI04 skipped — no picking plan generated from USEI03.");
                }

            } else {
                System.out.println("⚠️ USEI03 & USEI04 skipped — no allocation results from USEI02.");
            }

            // =============================================================
            // ▶ USEI05 - Returns Processing
            // =============================================================
            USEI05Executor.execute(itemRepo, warehouseRepo, RETURNS_FILE);

            System.out.println("\n✅ All user stories executed successfully!");

        } catch (Exception e) {
            System.err.println("❌ Execution failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}