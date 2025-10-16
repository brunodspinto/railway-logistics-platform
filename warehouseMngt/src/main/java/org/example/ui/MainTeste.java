package org.example.ui;

import org.example.repository.*;
import org.example.service.*;

/**
 * Main Simplificada - Coordena a execução das User Stories
 */
public class MainTeste {
    private static final String ITEMS_FILE = "res/Data/items.csv";
    private static final String BAYS_FILE = "res/Data/bays.csv";
    private static final String WAGONS_FILE = "res/Data/wagons.csv";
    private static final String ORDER_LINES_FILE = "res/Data/order_lines.csv";

    public static void main(String[] args) {
        try {
            // Setup compartilhado
            ItemRepository itemRepo = new ItemRepository();
            WarehouseRepository warehouseRepo = new WarehouseRepository();

            // Executar USEI01
            USEI01Executor.execute(itemRepo, warehouseRepo, ITEMS_FILE, BAYS_FILE, WAGONS_FILE);

            // Executar USEI02
            USEI02Executor.execute(warehouseRepo, ORDER_LINES_FILE);


        } catch (Exception e) {
            System.err.println("❌ Execution failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
