package org.example.ui;

import org.example.domain.*;
import org.example.repository.*;
import org.example.results.*;
import org.example.service.*;

import java.io.File;
import java.util.*;


public class Main {

    // Paths dos ficheiros
    private static final String ITEMS_FILE = "res/Data/items.csv";
    private static final String BAYS_FILE = "res/Data/bays.csv";
    private static final String WAGONS_FILE = "res/Data/wagons.csv";

    public static void main(String[] args) {
        // Permitir override por argumentos da linha de comando
        String itemsPath = args.length > 0 ? args[0] : ITEMS_FILE;
        String baysPath = args.length > 1 ? args[1] : BAYS_FILE;
        String wagonsPath = args.length > 2 ? args[2] : WAGONS_FILE;

        // Criar repositórios e serviços
        ItemRepository itemRepo = new ItemRepository();
        WarehouseRepository warehouseRepo = new WarehouseRepository();
        WagonUnloadingService unloadingService = new WagonUnloadingService(warehouseRepo);
        DataImportService importService = new DataImportService(itemRepo, warehouseRepo, unloadingService);
        InventoryService inventoryService = new InventoryService(warehouseRepo);

        // Validar ficheiros
        if (!validateFiles(itemsPath, baysPath, wagonsPath)) {
            return;
        }

        printHeader("USEI01 - WAGON UNLOADING TEST");

        System.out.println("   Files:");
        System.out.println("   Items:  " + itemsPath);
        System.out.println("   Bays:   " + baysPath);
        System.out.println("   Wagons: " + wagonsPath);
        System.out.println();

        try {
            // 1. Importar dados
            printStep("1. DATA IMPORT");
            ValidationResult result = importService.importAllData(itemsPath, baysPath, wagonsPath);

            if (!result.isSuccess()) {
                System.err.println("Import failed!");
                result.getErrors().forEach(System.err::println);
                System.exit(1);
            }

            System.out.printf("Items: %d | Bays: %d | Wagons: %d | Boxes: %d%n",
                    result.getItemsImported(),
                    result.getBaysImported(),
                    result.getWagonsImported(),
                    result.getBoxesUnloaded());

            Warehouse warehouse = warehouseRepo.findDefault();

            // 2. Verificar FEFO
            printStep("2. FEFO/FIFO VERIFICATION");
            if (!verifyFEFO(warehouse)) {
                System.err.println("FEFO/FIFO verification failed!");
                System.exit(1);
            }
            System.out.println(" FEFO/FIFO correct");

            // 3. Estado inicial
            printStep("3. INITIAL STATE");
            printCompactState(warehouse);

            // 4. Teste de despacho
            printStep("4. DISPATCH TEST");
            testDispatch(inventoryService, warehouse);

            // 5. Teste de realocação
            printStep("5. RELOCATION TEST");
            testRelocation(inventoryService, warehouse);

            // 6. Estado final
            printStep("6. FINAL STATE");
            printCompactState(warehouse);

            printFooter("ALL TESTS PASSED");

            // 7. Executar alocação de encomendas (USEI02)
            printStep("7. ORDER ALLOCATION (USEI02)");
            OrderAllocationService service = new OrderAllocationService(warehouse);

            List<OrderLine> orders = Arrays.asList(
                    new OrderLine("O1", 1, "SKU123", 5),
                    new OrderLine("O1", 2, "SKU200", 10),
                    new OrderLine("O2", 1, "SKU123", 15)
            );

            List<OrderAllocationResult> results = service.allocateOrders(orders, false);

            // Output
            for (OrderAllocationResult r : results) {
                System.out.println(r);
                r.getAllocations().forEach(System.out::println);
            }

        } catch (Exception e) {
            printFooter("TEST FAILED");
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    // Métodos auxiliares do MainUSEI01

    private static boolean validateFiles(String... files) {
        boolean allExist = true;
        for (String file : files) {
            if (!new File(file).exists()) {
                System.err.println("File not found: " + file);
                allExist = false;
            }
        }

        if (!allExist) {
            System.err.println("\n Tip: Update the paths at the top of Main.java");
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
                        System.err.printf("Bay %s: %s should come BEFORE %s%n",
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

        // Totais por SKU
        Map<String, Integer> skuTotals = new HashMap<>();
        for (Bay bay : warehouse.getAllBays()) {
            for (Box box : bay.getBoxes()) {
                skuTotals.merge(box.getSku(), box.getQuantity(), Integer::sum);
            }
        }

        if (skuTotals.isEmpty()) {
            System.out.println("Warehouse is EMPTY");
        } else {
            System.out.print("Inventory: ");
            skuTotals.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .forEach(e -> System.out.printf("%s:%d ", e.getKey(), e.getValue()));
            System.out.println();
        }

        // Bays ocupadas
        long occupiedBays = warehouse.getAllBays().stream()
                .filter(b -> b.getCurrentBoxCount() > 0)
                .count();
        System.out.printf("Occupied bays: %d/%d%n", occupiedBays, warehouse.getBayCount());

        // Detalhes das bays se não houver muitas
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
            System.out.println(" No stock available for dispatch test");
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
            System.out.println("Dispatch correct");
        } else {
            System.err.printf("Dispatch mismatch! Expected: %d, Got: %d%n",
                    expected, result.getDispatchedQty());
        }
    }

    private static void testRelocation(InventoryService inventoryService, Warehouse warehouse) {
        Box box = findFirstBox(warehouse);
        if (box == null) {
            System.out.println(" No boxes available for relocation test");
            return;
        }

        Location from = box.getLocation();
        Bay targetBay = findDifferentBay(warehouse, from);

        if (targetBay == null) {
            System.out.println("No alternative bay available");
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
            System.out.println("Relocation successful");
        } else {
            System.err.println("Relocation failed");
        }
    }

    // Métodos auxiliares

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

    // Métodos de interface

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