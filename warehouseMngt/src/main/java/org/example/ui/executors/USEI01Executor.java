package org.example.ui.executors;

import org.example.domain.*;
import org.example.repository.*;
import org.example.service.*;
import org.example.results.*;
import org.example.ui.menu.DisplayHelper;

import java.util.Scanner;

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
                System.out.println(" Warehouse saved to repository");
            } else {
                DisplayHelper.printError("No warehouse available after import!");
                return;
            }

            // 1. Show Boxes by Bay (simplified output)
            showBoxesByBay(inventoryService);

            // 2. Interactive Menu
            runInteractiveMenu(inventoryService, warehouseRepo);

            System.out.println("\n" + "═".repeat(60) + "\n");
            DisplayHelper.printSuccess("USEI01 - Wagon Unloading completed successfully");

        } catch (Exception e) {
            DisplayHelper.printError("USEI01 execution failed: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Shows only the Boxes by Bay section (simplified output)
     */
    private static void showBoxesByBay(InventoryService inventoryService) {
        Warehouse warehouse = inventoryService.getWarehouse();
        if (warehouse == null) {
            System.out.println("No warehouse available");
            return;
        }

        System.out.println("\n════════════════════════════════════════════════════════════");
        System.out.println(" Boxes by Bay (FEFO/FIFO Order)");
        System.out.println("════════════════════════════════════════════════════════════\n");

        // Get occupied bays sorted by location
        var occupiedBays = warehouse.getAllBays().stream()
                .filter(bay -> !bay.isEmpty())
                .sorted((b1, b2) -> {
                    int aisleCompare = Integer.compare(b1.getAisleNumber(), b2.getAisleNumber());
                    if (aisleCompare != 0) return aisleCompare;
                    return Integer.compare(b1.getBayNumber(), b2.getBayNumber());
                })
                .toList();

        for (Bay bay : occupiedBays) {
            System.out.printf("Bay: A%d-B%d (Capacity: %d boxes, Current: %d)\n",
                    bay.getAisleNumber(),
                    bay.getBayNumber(),
                    bay.getCapacityBoxes(),
                    bay.getCurrentBoxCount());

            var boxes = bay.getBoxes();
            for (int i = 0; i < boxes.size(); i++) {
                Box box = boxes.get(i);
                String expiryStr = box.getExpiryDate() != null
                        ? box.getExpiryDate().toString()
                        : "NO_EXP";

                System.out.printf("  %2d. %s | %s | Qty: %-3d | Exp: %-10s | Rec: %s\n",
                        i + 1,
                        box.getBoxId(),
                        box.getSku(),
                        box.getQuantity(),
                        expiryStr,
                        box.getReceivedAt().toString().substring(0, 10));
            }
            System.out.println();
        }

        System.out.println("════════════════════════════════════════════════════════════\n");
    }

    /**
     * Interactive menu for warehouse operations
     */
    private static void runInteractiveMenu(InventoryService inventoryService,
                                           WarehouseRepository warehouseRepo) {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("════════════════════════════════════════════════════════════");
            System.out.println("OPERATIONS MENU");
            System.out.println("════════════════════════════════════════════════════════════");
            System.out.println("1. Dispatch boxes (FEFO/FIFO)");
            System.out.println("2. Relocate box");
            System.out.println("3. View warehouse overview");
            System.out.println("4. Exit");
            System.out.println("════════════════════════════════════════════════════════════");
            System.out.print("Select operation: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> handleDispatch(scanner, inventoryService);
                case "2" -> handleRelocate(scanner, inventoryService);
                case "3" -> handleWarehouseOverview(inventoryService);
                case "4" -> {
                    System.out.println("\n Exiting operations menu...\n");
                    running = false;
                }
                default -> System.out.println(" Invalid option. Please try again.\n");
            }
        }
    }

    /**
     * Handles dispatch operation
     */
    private static void handleDispatch(Scanner scanner, InventoryService inventoryService) {
        System.out.println("\n─── DISPATCH OPERATION ───");

        System.out.print("Enter SKU: ");
        String sku = scanner.nextLine().trim().toUpperCase();

        System.out.print("Enter quantity to dispatch: ");
        String qtyInput = scanner.nextLine().trim();

        try {
            int quantity = Integer.parseInt(qtyInput);

            if (quantity <= 0) {
                System.out.println(" Quantity must be positive\n");
                return;
            }

            System.out.println("\n Dispatching " + quantity + " units of " + sku + "...\n");

            DispatchResult result = inventoryService.dispatchBoxes(sku, quantity);

            // Show result
            System.out.println(result.toString());

            if (result.isFullyDispatched()) {
                System.out.println(" Dispatch completed successfully!");
            } else if (result.isPartiallyDispatched()) {
                System.out.println("  Partial dispatch - insufficient stock");
            } else {
                System.out.println(" No stock available for " + sku);
            }

        } catch (NumberFormatException e) {
            System.out.println(" Invalid quantity format\n");
        } catch (Exception e) {
            System.out.println(" Dispatch failed: " + e.getMessage() + "\n");
        }

        System.out.println();
    }

    /**
     * Handles relocate operation
     */
    private static void handleRelocate(Scanner scanner, InventoryService inventoryService) {
        System.out.println("\n─── RELOCATE OPERATION ───");

        System.out.print("Enter Box ID: ");
        String boxId = scanner.nextLine().trim();

        System.out.print("Enter target Aisle number: ");
        String aisleInput = scanner.nextLine().trim();

        System.out.print("Enter target Bay number: ");
        String bayInput = scanner.nextLine().trim();

        try {
            int aisle = Integer.parseInt(aisleInput);
            int bay = Integer.parseInt(bayInput);

            Warehouse warehouse = inventoryService.getWarehouse();
            if (warehouse == null) {
                System.out.println(" No warehouse available\n");
                return;
            }

            Location newLocation = new Location(warehouse.getWarehouseId(), aisle, bay);

            System.out.println("\n Relocating " + boxId + " to " +
                    newLocation.toFormattedString() + "...\n");

            boolean success = inventoryService.relocateBox(boxId, newLocation);

            if (success) {
                System.out.println(" Box relocated successfully!");
            } else {
                System.out.println(" Relocation skipped (box already in target bay)");
            }

        } catch (NumberFormatException e) {
            System.out.println(" Invalid aisle/bay number format\n");
        } catch (Exception e) {
            System.out.println(" Relocation failed: " + e.getMessage() + "\n");
        }

        System.out.println();
    }

    /**
     * Shows complete warehouse overview
     */
    private static void handleWarehouseOverview(InventoryService inventoryService) {
        System.out.println("\n" + inventoryService.generateWarehouseOverview());
        System.out.println(inventoryService.generateInventorySummary());
    }
}