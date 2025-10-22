package org.example.ui.menu;

import org.example.exception.ValidationException;
import org.example.repository.WarehouseRepository;
import org.example.results.OrderAllocationResult;
import org.example.ui.executors.USEI02Executor;

import java.util.List;

public class USEI02UI implements Runnable {

    private static final String ORDER_LINES_FILE = "res/Data/order_lines.csv";

    @Override
    public void run() {
        try {
            System.out.println("\n=== Running USEI02 - Order Eligibility & Allocation ===\n");

            WarehouseRepository warehouseRepo = SharedContext.getWarehouseRepository();
            if (warehouseRepo == null) {
                throw new ValidationException("No warehouse available. Please run USEI01 first.");
            }

            List<OrderAllocationResult> results = USEI02Executor.execute(warehouseRepo, ORDER_LINES_FILE);

            SharedContext.setLastAllocations(results);

            if (results == null && results.isEmpty()) {
                System.out.println("\n⚠️ No allocation results generated.");
            }

        } catch (Exception e) {
            System.err.println("❌ USEI02 execution failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

