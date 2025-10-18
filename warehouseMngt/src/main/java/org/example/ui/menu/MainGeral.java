package org.example.ui.menu;

import org.example.repository.*;
import org.example.ui.executors.USEI01Executor;
import org.example.ui.executors.USEI02Executor;
import org.example.ui.executors.USEI05Executor;

/**
 * Main Simplificada - Coordena a execução das User Stories
 */
public class MainGeral implements Runnable {

    private static final String ITEMS_FILE = "res/Data/items.csv";
    private static final String BAYS_FILE = "res/Data/bays.csv";
    private static final String WAGONS_FILE = "res/Data/wagons.csv";
    private static final String ORDER_LINES_FILE = "res/Data/order_lines.csv";
    private static final String RETURNS_FILE = "res/Data/returns.csv";

    @Override
    public void run() {
        System.out.println("\n=== Running MainGeral (USEI01, USEI02, USEI05) ===\n");
        executeAll();
    }

    public static void main(String[] args) {
        new MainGeral().executeAll();
    }

    private void executeAll() {
        try {
            // Setup compartilhado
            ItemRepository itemRepo = new ItemRepository();
            WarehouseRepository warehouseRepo = new WarehouseRepository();

            // Executar USEI01
            USEI01Executor.execute(itemRepo, warehouseRepo, ITEMS_FILE, BAYS_FILE, WAGONS_FILE);

            // Executar USEI02
            USEI02Executor.execute(warehouseRepo, ORDER_LINES_FILE);

            // Executar USEI05
            USEI05Executor.execute(itemRepo, warehouseRepo, RETURNS_FILE);

            System.out.println("\n✅ All user stories executed successfully!\n");

        } catch (Exception e) {
            System.err.println("❌ Execution failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
