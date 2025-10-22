package org.example.ui.menu;

import org.example.repository.ItemRepository;
import org.example.repository.WarehouseRepository;
import org.example.ui.executors.USEI05Executor;

public class USEI05UI implements Runnable {

    private static final String RETURNS_FILE = "res/Data/returns.csv";

    @Override
    public void run() {
        try {
            System.out.println("\n=== Running USEI05 - Returns & Quarantine ===\n");

            ItemRepository itemRepo = SharedContext.getItemRepository();
            WarehouseRepository warehouseRepo = SharedContext.getWarehouseRepository();

            USEI05Executor.execute(itemRepo, warehouseRepo, RETURNS_FILE);
        } catch (Exception e) {
            System.err.println("❌ USEI05 failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
