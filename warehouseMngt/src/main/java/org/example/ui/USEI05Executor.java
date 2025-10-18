package org.example.ui;

import org.example.repository.*;
import org.example.results.*;
import org.example.service.*;
import org.example.domain.*;

public class USEI05Executor {

    public static void execute(ItemRepository itemRepo, WarehouseRepository warehouseRepo, String returnsPath) {

        DisplayHelper.printHeader("USEI05 - Returns Processing");

        try {
            ReturnsProcessingService returnsService = new ReturnsProcessingService(itemRepo, warehouseRepo);

            DisplayHelper.printHeader("Loading returns file");
            System.out.println("File: " + returnsPath);

            ProcessingResult result = returnsService.processReturns(returnsPath);

            DisplayHelper.printHeader("Processing Summary");
            System.out.printf("✅ Restocked: %d | 🗑 Discarded: %d | ♻ Partial Restocks: %d | ⚠ Errors: %d%n",
                    result.getRestockedCount(),
                    result.getDiscardedCount(),
                    result.getPartialRestockCount(),
                    result.getErrorsCount());

            System.out.println("\n🧾 Audit log written to: logs/audit-log.txt");
            DisplayHelper.printSuccess("USEI05 completed successfully");

        } catch (Exception e) {
            DisplayHelper.printError("USEI05 execution failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

