package org.example.ui;

import org.example.domain.*;
import org.example.repository.*;
import org.example.service.*;
import org.example.results.*;

public class USEI01Executor {

    public static void execute(ItemRepository itemRepo, WarehouseRepository warehouseRepo,
                               String itemsPath, String baysPath, String wagonsPath) {

        DisplayHelper.printHeader("USEI01 - Wagon Unloading & Inventory");

        try {
            WagonUnloadingService unloadingService = new WagonUnloadingService(warehouseRepo);
            DataImportService importService = new DataImportService(itemRepo, warehouseRepo, unloadingService);
            InventoryService inventoryService = new InventoryService(warehouseRepo);

            // Importar dados
            ValidationResult result = importService.importAllData(itemsPath, baysPath, wagonsPath);
            if (!result.isSuccess()) {
                DisplayHelper.printError("Data import failed!");
                return;
            }

            Warehouse warehouse = warehouseRepo.findDefault();

            if (warehouse != null) {
                warehouseRepo.save(warehouse);  // ou warehouseRepo.setWarehouse(warehouse);
                System.out.println("✅ Warehouse saved to repository");
            } else {
                DisplayHelper.printError("No warehouse available after import!");
                return;
            }

            // Mostrar resultados
            DisplayHelper.showImportSummary(result);
            DisplayHelper.showFEFOExample(warehouse);
            DisplayHelper.showFEFOValidation(warehouse);
            DisplayHelper.showWarehouseOverview(warehouse);
            DisplayHelper.showInventoryBySKU(warehouse);

            DisplayHelper.showDispatchTest(warehouse, inventoryService);
            DisplayHelper.showRelocationTest(warehouse, inventoryService);

            DisplayHelper.printSuccess("USEI01 completed successfully");

        } catch (Exception e) {
            DisplayHelper.printError("USEI01 execution failed: " + e.getMessage());
            throw e;
        }
    }
}
