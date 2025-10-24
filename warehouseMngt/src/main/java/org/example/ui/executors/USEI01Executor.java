package org.example.ui.executors;

import org.example.domain.*;
import org.example.repository.*;
import org.example.results.*;
import org.example.ui.menu.DisplayHelper;
import org.example.usei04.service.DataImportService;
import org.example.usei04.service.InventoryService;
import org.example.usei04.service.WagonUnloadingService;

public class USEI01Executor {

    public static void execute(ItemRepository itemRepo, WarehouseRepository warehouseRepo,
                               String itemsPath, String baysPath, String wagonsPath) {

        DisplayHelper.printHeader("USEI01 - Wagon Unloading & Inventory Management");

        try {
            WagonUnloadingService unloadingService = new WagonUnloadingService(warehouseRepo);
            DataImportService importService = new DataImportService(itemRepo, warehouseRepo, unloadingService);
            InventoryService inventoryService = new InventoryService(warehouseRepo);

            // Importar dados
            ValidationResult result = importService.importAllData(itemsPath, baysPath, wagonsPath);
            if (!result.isSuccess()) {
                DisplayHelper.printError("Data import failed!");
                result.getErrors().forEach(error -> System.err.println("  • " + error));
                return;
            }

            Warehouse warehouse = warehouseRepo.findDefault();

            if (warehouse != null) {
                warehouseRepo.save(warehouse);
                System.out.println("✅ Warehouse saved to repository");
            } else {
                DisplayHelper.printError("No warehouse available after import!");
                return;
            }

            // ========== NOVO OUTPUT FORMATADO ==========

            // 1. Import Summary (com warnings se existirem)
            DisplayHelper.showImportSummary(result);

            // 2. Warehouse Overview DETALHADO
            System.out.println(inventoryService.generateWarehouseOverview());

            // 3. Inventory Summary by SKU
            System.out.println(inventoryService.generateInventorySummary());

            // 4. FEFO/FIFO Validation
            DisplayHelper.showFEFOValidation(warehouse);

            System.out.println("\n" + "═".repeat(60) + "\n");
            System.out.println();
            DisplayHelper.printSuccess("USEI01 - Wagon Unloading completed successfully");
            DisplayHelper.printSuccess("Warehouse operational and ready for dispatch operations");

        } catch (Exception e) {
            DisplayHelper.printError("USEI01 execution failed: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
}
