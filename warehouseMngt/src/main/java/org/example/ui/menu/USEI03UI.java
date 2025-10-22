package org.example.ui.menu;

import org.example.domain.Trolley;
import org.example.repository.ItemRepository;
import org.example.repository.WarehouseRepository;
import org.example.results.OrderAllocationResult;
import org.example.ui.executors.USEI02Executor;
import org.example.ui.executors.USEI03Executor;

import java.util.List;

public class USEI03UI implements Runnable {

    @Override
    public void run() {
        try {
            System.out.println("\n=== Running USEI03 - Picking Plans ===\n");

            ItemRepository itemRepo = SharedContext.getItemRepository();
            WarehouseRepository warehouseRepo = SharedContext.getWarehouseRepository();

            List<OrderAllocationResult> allocations = SharedContext.getLastAllocations();

            if (allocations == null || allocations.isEmpty()) {
                System.out.println("⚠️ No allocations found. Running USEI02 to generate them...");
                allocations = USEI02Executor.execute(warehouseRepo, "res/Data/order_lines.csv");
                SharedContext.setLastAllocations(allocations);
            }

            List<Trolley> pickingPlan = USEI03Executor.execute(itemRepo, allocations);

            SharedContext.setLastPickingPlan(pickingPlan);

            if (pickingPlan == null && pickingPlan.isEmpty()) {
                System.out.println("\n⚠️ No picking plan generated.");
            }

        } catch (Exception e) {
            System.err.println("❌ USEI03 failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

