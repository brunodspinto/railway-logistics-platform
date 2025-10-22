package org.example.ui.menu;


import org.example.repository.ItemRepository;
import org.example.repository.WarehouseRepository;
import org.example.ui.executors.USEI01Executor;

public class USEI01UI implements Runnable {

    private static final String ITEMS_FILE = "res/Data/items.csv";
    private static final String BAYS_FILE = "res/Data/bays.csv";
    private static final String WAGONS_FILE = "res/Data/wagons.csv";

    @Override
    public void run() {
        try {
            System.out.println("\n=== Running USEI01 - Wagons Unloading ===\n");

            ItemRepository itemRepo = new ItemRepository();
            WarehouseRepository warehouseRepo = new WarehouseRepository();

            USEI01Executor.execute(itemRepo, warehouseRepo, ITEMS_FILE, BAYS_FILE, WAGONS_FILE);

            SharedContext.setItemRepository(itemRepo);
            SharedContext.setWarehouseRepository(warehouseRepo);

        } catch (Exception e) {
            System.err.println("❌ USEI01 failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

