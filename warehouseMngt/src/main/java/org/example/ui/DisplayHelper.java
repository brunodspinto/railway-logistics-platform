package org.example.ui;

import org.example.domain.*;
import org.example.results.*;
import java.util.*;

public class DisplayHelper {

    // ========== HEADERS E FORMATAÇÃO ==========

    public static void printHeader(String title) {
        System.out.println("\n" + "═".repeat(60));
        System.out.println("  " + title);
        System.out.println("═".repeat(60));
    }

    public static void printSuccess(String message) {
        System.out.println("✅ " + message);
    }

    public static void printError(String message) {
        System.err.println("❌ " + message);
    }

    // ========== MÉTODOS ESPECÍFICOS USEI01 ==========

    public static void showImportSummary(ValidationResult result) {
        System.out.println("\n📦 Import Summary");
        System.out.println("────────────────────────────────────────────");
        System.out.printf(" Items imported: %d%n", result.getItemsImported());
        System.out.printf(" Bays created: %d%n", result.getBaysImported());
        System.out.printf(" Wagons processed: %d%n", result.getWagonsImported());
        System.out.printf(" Boxes unloaded: %d%n", result.getBoxesUnloaded());
    }

    public static void showFEFOValidation(Warehouse warehouse) {
        boolean fefoOK = verifyFEFO(warehouse);
        System.out.println("\n📊 FEFO/FIFO Check: " + (fefoOK ? "✅ PASSED" : "❌ FAILED"));
    }

    public static void showWarehouseOverview(Warehouse warehouse) {
        System.out.println("\n🏗 Warehouse Overview");
        System.out.println("────────────────────────────────────────────");
        System.out.printf(" Warehouse ID: %s%n", warehouse.getWarehouseId());
        System.out.printf(" Total bays: %d%n", warehouse.getBayCount());
        System.out.printf(" Occupied bays: %d%n", warehouse.getAllBays().stream()
                .filter(b -> b.getCurrentBoxCount() > 0).count());
        System.out.printf(" Total boxes: %d%n", warehouse.getTotalBoxCount());
        System.out.printf(" Occupancy: %.1f%%%n", warehouse.getOccupancyPercentage());
    }

    public static void showInventoryBySKU(Warehouse warehouse) {
        System.out.println("\n📦 Inventory Totals by SKU");
        System.out.println("────────────────────────────────────────────");
        Map<String, Integer> skuTotals = new TreeMap<>();

        for (Bay bay : warehouse.getAllBays()) {
            for (Box box : bay.getBoxes()) {
                skuTotals.merge(box.getSku(), box.getQuantity(), Integer::sum);
            }
        }

        if (skuTotals.isEmpty()) {
            System.out.println(" (Warehouse is empty)");
        } else {
            skuTotals.forEach((sku, qty) -> System.out.printf(" %s → %d units%n", sku, qty));
        }
    }


    public static void showFEFOExample(Warehouse warehouse) {
        System.out.println("\n🔍 FEFO/FIFO Order Example");
        System.out.println("────────────────────────────────────────────");

        String exampleSku = findSKUWithMultipleBoxes(warehouse);
        if (exampleSku == null) {
            System.out.println("No SKU with multiple boxes found for demonstration.");
            return;
        }

        List<Box> orderedBoxes = getAllBoxesForSKU(warehouse, exampleSku);
        if (orderedBoxes.size() < 2) {
            System.out.println("SKU " + exampleSku + " has only " + orderedBoxes.size() + " box");
            return;
        }

        System.out.println("SKU: " + exampleSku);
        System.out.println("Order: " + getOrderingExplanation(orderedBoxes));
        System.out.println("\nBox sequence (FEFO/FIFO):");

        for (int i = 0; i < orderedBoxes.size(); i++) {
            Box box = orderedBoxes.get(i);
            System.out.printf("  %d. %s", i + 1, formatBoxForDisplay(box));

            if (i > 0) {
                Box prevBox = orderedBoxes.get(i - 1);
                System.out.printf("   ← %s", getOrderingReason(prevBox, box));
            }
            System.out.println();
        }
    }

    // ========== MÉTODOS AUXILIARES PRIVADOS ==========

    private static boolean verifyFEFO(Warehouse warehouse) {
        for (Bay bay : warehouse.getAllBays()) {
            List<Box> boxes = bay.getBoxes();
            for (int i = 1; i < boxes.size(); i++) {
                if (boxes.get(i - 1).compareTo(boxes.get(i)) > 0) return false;
            }
        }
        return true;
    }

    private static String findSKUWithMultipleBoxes(Warehouse warehouse) {
        Map<String, Integer> skuCounts = new HashMap<>();
        for (Bay bay : warehouse.getAllBays()) {
            for (Box box : bay.getBoxes()) {
                skuCounts.merge(box.getSku(), 1, Integer::sum);
            }
        }
        return skuCounts.entrySet().stream()
                .filter(entry -> entry.getValue() > 1)
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);
    }

    private static List<Box> getAllBoxesForSKU(Warehouse warehouse, String sku) {
        List<Box> boxes = new ArrayList<>();
        for (Bay bay : warehouse.getAllBays()) {
            for (Box box : bay.getBoxes()) {
                if (box.getSku().equals(sku)) {
                    boxes.add(box);
                }
            }
        }
        boxes.sort(Box::compareTo);
        return boxes;
    }

    private static String getOrderingExplanation(List<Box> boxes) {
        boolean hasExpiry = boxes.stream().anyMatch(b -> b.getExpiryDate() != null);
        boolean hasMultipleDates = boxes.stream()
                .map(b -> b.getExpiryDate() != null ? b.getExpiryDate() : b.getReceivedAt())
                .distinct()
                .count() > 1;

        if (hasExpiry && hasMultipleDates) return "FEFO (First Expired First Out)";
        else if (hasMultipleDates) return "FIFO (First In First Out)";
        else return "Natural order (same dates)";
    }

    private static String formatBoxForDisplay(Box box) {
        String expiryStr = box.getExpiryDate() != null ?
                box.getExpiryDate().toString() : "NO_EXP";
        String receivedStr = box.getReceivedAt().toString().substring(0, 10);
        return String.format("%s [Exp: %s, Rec: %s, Qty: %d]",
                box.getBoxId(), expiryStr, receivedStr, box.getQuantity());
    }

    private static String getOrderingReason(Box first, Box second) {
        if (first.getExpiryDate() != null && second.getExpiryDate() != null) {
            int expiryCompare = first.getExpiryDate().compareTo(second.getExpiryDate());
            if (expiryCompare < 0) return "earlier expiry";
            if (expiryCompare > 0) return "later expiry";
        } else if (first.getExpiryDate() != null) return "has expiry vs no expiry";
        else if (second.getExpiryDate() != null) return "no expiry vs has expiry";

        int receivedCompare = first.getReceivedAt().compareTo(second.getReceivedAt());
        if (receivedCompare < 0) return "earlier received";
        if (receivedCompare > 0) return "later received";

        return "boxId order";
    }
}
