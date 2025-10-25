package org.example.service;

import org.example.domain.*;
import org.example.results.UnloadingResult;
import org.example.results.ValidationResult;
import org.example.exception.ValidationException;
import org.example.CsvReaders.*;
import org.example.repository.*;

import java.util.List;

public class DataImportService {

    private final ItemRepository itemRepository;
    private final WarehouseRepository warehouseRepository;
    private final WagonUnloadingService wagonUnloadingService;

    private List<Wagon> importedWagons;
    private UnloadingResult unloadingResult;

    public DataImportService(ItemRepository itemRepository,
                             WarehouseRepository warehouseRepository,
                             WagonUnloadingService wagonUnloadingService) {
        this.itemRepository = itemRepository;
        this.warehouseRepository = warehouseRepository;
        this.wagonUnloadingService = wagonUnloadingService;
    }

    /**
     * Imports all data files and unloads wagons into the warehouse.
     * This completes USEI01 acceptance criteria #1.
     */
    public ValidationResult importAllData(String itemsPath,
                                          String baysPath,
                                          String wagonsPath) {
        ValidationResult result = new ValidationResult();

        try {
            System.out.println("=== Starting Data Import ===\n");

            // 1. Import items FIRST (needed for wagon validation)
            System.out.println("Step 1: Importing items...");
            ItemCsvReader itemParser = new ItemCsvReader();
            List<Item> items = itemParser.parse(itemsPath);

            for (Item item : items) {
                itemRepository.save(item);
            }
            result.setItemsImported(items.size());
            System.out.println("Imported " + items.size() + " items\n");

            // 2. Import bays and create warehouse
            System.out.println("Step 2: Importing bays...");
            BayCsvReader bayParser = new BayCsvReader();
            List<Bay> bays = bayParser.parse(baysPath);

            if (bays.isEmpty()) {
                throw new ValidationException("No bays found in CSV");
            }

            // Validate all bays have same warehouseId
            String warehouseId = bays.get(0).getWarehouseId();
            for (Bay bay : bays) {
                if (!bay.getWarehouseId().equals(warehouseId)) {
                    throw new ValidationException(
                            "Multiple warehouse IDs found in bays.csv. Expected: " +
                                    warehouseId + ", found: " + bay.getWarehouseId()
                    );
                }
            }

            Warehouse warehouse = new Warehouse(warehouseId);
            for (Bay bay : bays) {
                warehouse.addBay(bay);
            }

            warehouseRepository.save(warehouse);
            result.setBaysImported(bays.size());
            System.out.println("Imported " + bays.size() + " bays");
            System.out.println("Created warehouse: " + warehouse.getSummary() + "\n");

            // 3. Import wagons
            System.out.println("Step 3: Importing wagons...");
            WagonCsvReader wagonParser = new WagonCsvReader();
            this.importedWagons = wagonParser.parse(wagonsPath, itemRepository, result);

            result.setWagonsImported(importedWagons.size());
            System.out.println("Imported " + importedWagons.size() + " wagons");

            // Count total boxes
            int totalBoxes = importedWagons.stream()
                    .mapToInt(w -> w.getBoxes().size())
                    .sum();
            System.out.println("Total boxes: " + totalBoxes + "\n");

            // 4. CRITICAL: Unload wagons into warehouse (USEI01 requirement)
            System.out.println("Step 4: Unloading wagons into warehouse...");
            this.unloadingResult = wagonUnloadingService.unloadWagons(importedWagons);

            if (!unloadingResult.isFullSuccess()) {
                System.err.println("⚠️  Some wagons failed to unload:");
                unloadingResult.getErrors().forEach((wagonId, error) ->
                        System.err.println("  - " + wagonId + ": " + error)
                );
            }

            result.setBoxesUnloaded(unloadingResult.getTotalBoxesUnloaded());
            result.setUnloadingResult(unloadingResult);

            System.out.println("Successfully unloaded " +
                    unloadingResult.getSuccessfulWagons() + "/" +
                    importedWagons.size() + " wagons");
            System.out.println("Total boxes placed in warehouse: " +
                    unloadingResult.getTotalBoxesUnloaded() + "\n");

            result.setSuccess(true);
            System.out.println("=== Import Complete ===\n");

            // Print warehouse summary
            printWarehouseSummary(warehouse);

        } catch (Exception e) {
            result.setSuccess(false);
            result.addError("Import failed: " + e.getMessage());
            System.err.println("Import error: " + e.getMessage());
            e.printStackTrace();
        }

        return result;
    }

    private void printWarehouseSummary(Warehouse warehouse) {
        System.out.println("=== Warehouse Summary ===");
        System.out.println("Total bays: " + warehouse.getBays().size());

        int totalBoxes = 0;
        int occupiedBays = 0;

        for (Bay bay : warehouse.getBays().values()) {
            int bayBoxCount = bay.getCurrentBoxCount();
            totalBoxes += bayBoxCount;
            if (bayBoxCount > 0) {
                occupiedBays++;
            }
        }

        System.out.println("Occupied bays: " + occupiedBays);
        System.out.println("Total boxes stored: " + totalBoxes);
        System.out.println("========================\n");
    }

    public List<Wagon> getImportedWagons() {
        return importedWagons;
    }

    public UnloadingResult getUnloadingResult() {
        return unloadingResult;
    }
}
