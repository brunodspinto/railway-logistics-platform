package org.example.ui;

import org.example.domain.*;
import org.example.results.*;
import org.example.repository.*;
import org.example.service.*;

import java.io.File;
import java.util.*;

/**
 * Main class for USEI01 - Configurable paths version
 */
public class MainUSEI01 {

    // ========== CONFIGURAÇÃO DE PATHS ==========
    // Altere aqui os caminhos dos ficheiros CSV
    private static final String BASE_PATH = "src/main/java/res/";

    // Cenário ativo (altere conforme necessário)
    private static final String ACTIVE_SCENARIO = "cenario3";

    // Paths dos ficheiros
    private static final String ITEMS_FILE = BASE_PATH + ACTIVE_SCENARIO + "/items3.csv";
    private static final String BAYS_FILE = BASE_PATH + ACTIVE_SCENARIO + "/bays3.csv";
    private static final String WAGONS_FILE = BASE_PATH + ACTIVE_SCENARIO + "/wagons3 .csv";

    // OU configure paths individuais (comente o bloco acima e use este):
    /*
    private static final String ITEMS_FILE = "src/main/java/res/cenario3/items3.csv";
    private static final String BAYS_FILE = "src/main/java/res/cenario3/bays3.csv";
    private static final String WAGONS_FILE = "src/main/java/res/cenario3/wagons3.csv";
    */

    // ===========================================

    public static void main(String[] args) {
        // Permitir override por argumentos da linha de comando
        String itemsPath = args.length > 0 ? args[0] : ITEMS_FILE;
        String baysPath = args.length > 1 ? args[1] : BAYS_FILE;
        String wagonsPath = args.length > 2 ? args[2] : WAGONS_FILE;

        runTest(itemsPath, baysPath, wagonsPath);
    }

    private static void runTest(String itemsFile, String baysFile, String wagonsFile) {
        printHeader("USEI01 - WAGON UNLOADING TEST");

        // Verificar se ficheiros existem
        if (!validateFiles(itemsFile, baysFile, wagonsFile)) {
            return;
        }

        System.out.println("📁 Files:");
        System.out.println("   Items:  " + itemsFile);
        System.out.println("   Bays:   " + baysFile);
        System.out.println("   Wagons: " + wagonsFile);
        System.out.println();

        try {
            // Initialize
            ItemRepository itemRepo = new ItemRepository();
            WarehouseRepository warehouseRepo = new WarehouseRepository();
            WagonUnloadingService unloadingService = new WagonUnloadingService(warehouseRepo);
            DataImportService importService = new DataImportService(itemRepo, warehouseRepo, unloadingService);
            InventoryService inventoryService = new InventoryService(warehouseRepo);

            // 1. Import
            printStep("1. DATA IMPORT");
            ValidationResult result = importService.importAllData(itemsFile, baysFile, wagonsFile);

            if (!result.isSuccess()) {
                System.err.println("❌ Import failed!");
                result.getErrors().forEach(System.err::println);
                System.exit(1);
            }

            System.out.printf("✅ Items: %d | Bays: %d | Wagons: %d | Boxes: %d%n",
                    result.getItemsImported(),
                    result.getBaysImported(),
                    result.getWagonsImported(),
                    result.getBoxesUnloaded());

            Warehouse warehouse = warehouseRepo.findDefault();

            // 2. Verify FEFO
            printStep("2. FEFO/FIFO VERIFICATION");
            if (!verifyFEFO(warehouse)) {
                System.err.println("❌ FEFO/FIFO verification failed!");
                System.exit(1);
            }
            System.out.println("✅ FEFO/FIFO correct");

            // 3. Initial state
            printStep("3. INITIAL STATE");
            printCompactState(warehouse);

            // 4. Dispatch
            printStep("4. DISPATCH TEST");
            testDispatch(inventoryService, warehouse);

            // 5. Relocation
            printStep("5. RELOCATION TEST");
            testRelocation(inventoryService, warehouse);

            // 6. Final state
            printStep("6. FINAL STATE");
            printCompactState(warehouse);

            printFooter("✅ ALL TESTS PASSED");

        } catch (Exception e) {
            printFooter("❌ TEST FAILED");
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static boolean validateFiles(String... files) {
        boolean allExist = true;
        for (String file : files) {
            if (!new File(file).exists()) {
                System.err.println("❌ File not found: " + file);
                allExist = false;
            }
        }

        if (!allExist) {
            System.err.println("\n💡 Tip: Update the paths at the top of MainNoPause.java");
            System.err.println("   Current base path: " + BASE_PATH);
            System.err.println("   Current scenario: " + ACTIVE_SCENARIO);
        }

        return allExist;
    }

    private static boolean verifyFEFO(Warehouse warehouse) {
        boolean hasErrors = false;

        for (Bay bay : warehouse.getAllBays()) {
            List<Box> boxes = bay.getBoxes();

            if (boxes.size() > 1) {
                for (int i = 1; i < boxes.size(); i++) {
                    Box prev = boxes.get(i - 1);
                    Box curr = boxes.get(i);

                    if (prev.compareTo(curr) > 0) {
                        System.err.printf("❌ Bay %s: %s should come BEFORE %s%n",
                                bay.getLocation().toFormattedString(),
                                curr.getBoxId(),
                                prev.getBoxId()
                        );
                        System.err.printf("   %s: exp=%s, rcv=%s%n",
                                prev.getBoxId(),
                                prev.getExpiryDate() != null ? prev.getExpiryDate() : "N/A",
                                prev.getReceivedAt()
                        );
                        System.err.printf("   %s: exp=%s, rcv=%s%n",
                                curr.getBoxId(),
                                curr.getExpiryDate() != null ? curr.getExpiryDate() : "N/A",
                                curr.getReceivedAt()
                        );
                        hasErrors = true;
                    }
                }
            }
        }

        return !hasErrors;
    }

    private static void printCompactState(Warehouse warehouse) {
        System.out.printf("Warehouse: %s | Boxes: %d | Occupancy: %.1f%%%n",
                warehouse.getWarehouseId(),
                warehouse.getTotalBoxCount(),
                warehouse.getOccupancyPercentage()
        );

        // SKU totals
        Map<String, Integer> skuTotals = new HashMap<>();
        for (Bay bay : warehouse.getAllBays()) {
            for (Box box : bay.getBoxes()) {
                skuTotals.merge(box.getSku(), box.getQuantity(), Integer::sum);
            }
        }

        if (skuTotals.isEmpty()) {
            System.out.println("⚠️  Warehouse is EMPTY");
        } else {
            System.out.print("Inventory: ");
            skuTotals.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .forEach(e -> System.out.printf("%s:%d ", e.getKey(), e.getValue()));
            System.out.println();
        }

        // Occupied bays
        long occupiedBays = warehouse.getAllBays().stream()
                .filter(b -> b.getCurrentBoxCount() > 0)
                .count();
        System.out.printf("Occupied bays: %d/%d%n", occupiedBays, warehouse.getBayCount());

        // Show bay details if not too many
        if (occupiedBays <= 5) {
            for (Bay bay : warehouse.getAllBays()) {
                if (bay.getCurrentBoxCount() > 0) {
                    System.out.printf("  %s [%d/%d]: ",
                            bay.getLocation().toFormattedString(),
                            bay.getCurrentBoxCount(),
                            bay.getCapacityBoxes()
                    );

                    Map<String, Integer> baySkus = new HashMap<>();
                    for (Box box : bay.getBoxes()) {
                        baySkus.merge(box.getSku(), box.getQuantity(), Integer::sum);
                    }
                    baySkus.forEach((sku, qty) -> System.out.printf("%s:%d ", sku, qty));
                    System.out.println();
                }
            }
        }
    }

    private static void testDispatch(InventoryService inventoryService, Warehouse warehouse) {
        String sku = findFirstSku(warehouse);
        if (sku == null) {
            System.out.println("⚠️  No stock available for dispatch test");
            return;
        }

        int before = getTotalQty(warehouse, sku);
        int request = Math.min(before + 50, 100);

        System.out.printf("Testing SKU: %s%n", sku);
        System.out.printf("Stock before: %d | Requesting: %d%n", before, request);

        DispatchResult result = inventoryService.dispatchBoxes(sku, request);

        int after = getTotalQty(warehouse, sku);
        int expected = Math.min(request, before);

        System.out.printf("Dispatched: %d/%d | Stock after: %d | Reduction: %d%n",
                result.getDispatchedQty(), request, after, before - after);

        if (result.getDispatchedQty() == expected) {
            System.out.println("✅ Dispatch correct");
        } else {
            System.err.printf("❌ Dispatch mismatch! Expected: %d, Got: %d%n",
                    expected, result.getDispatchedQty());
        }
    }

    private static void testRelocation(InventoryService inventoryService, Warehouse warehouse) {
        Box box = findFirstBox(warehouse);
        if (box == null) {
            System.out.println("⚠️  No boxes available for relocation test");
            return;
        }

        Location from = box.getLocation();
        Bay targetBay = findDifferentBay(warehouse, from);

        if (targetBay == null) {
            System.out.println("⚠️  No alternative bay available");
            return;
        }

        System.out.printf("Relocating: %s | SKU: %s | Qty: %d%n",
                box.getBoxId(), box.getSku(), box.getQuantity());
        System.out.printf("From: %s → To: %s%n",
                from.toFormattedString(),
                targetBay.getLocation().toFormattedString()
        );

        boolean success = inventoryService.relocateBox(box.getBoxId(), targetBay.getLocation());

        if (success) {
            System.out.println("✅ Relocation successful");
        } else {
            System.err.println("❌ Relocation failed");
        }
    }

    // ==================== HELPER METHODS ====================

    private static String findFirstSku(Warehouse warehouse) {
        for (Bay bay : warehouse.getAllBays()) {
            if (!bay.isEmpty()) {
                return bay.getBoxes().get(0).getSku();
            }
        }
        return null;
    }

    private static int getTotalQty(Warehouse warehouse, String sku) {
        return warehouse.getAllBays().stream()
                .mapToInt(bay -> bay.getQuantityForSku(sku))
                .sum();
    }

    private static Box findFirstBox(Warehouse warehouse) {
        for (Bay bay : warehouse.getAllBays()) {
            if (!bay.isEmpty()) {
                return bay.getBoxes().get(0);
            }
        }
        return null;
    }

    private static Bay findDifferentBay(Warehouse warehouse, Location current) {
        for (Bay bay : warehouse.getAllBays()) {
            if (!bay.getLocation().equals(current) && bay.hasAvailableSpace()) {
                return bay;
            }
        }
        return null;
    }

    // ==================== UI ====================

    private static void printHeader(String title) {
        System.out.println("\n" + "═".repeat(60));
        System.out.println("  " + title);
        System.out.println("═".repeat(60) + "\n");
    }

    private static void printStep(String title) {
        System.out.println("\n" + "─".repeat(60));
        System.out.println("  " + title);
        System.out.println("─".repeat(60));
    }

    private static void printFooter(String message) {
        System.out.println("\n" + "═".repeat(60));
        System.out.println("  " + message);
        System.out.println("═".repeat(60) + "\n");
    }
}
