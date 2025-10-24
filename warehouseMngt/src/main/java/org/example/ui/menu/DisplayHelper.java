package org.example.ui.menu;

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
        System.out.println("\n════════════════════════════════════════════════════════════");
        System.out.println("📦 IMPORT SUMMARY");
        System.out.println("════════════════════════════════════════════════════════════");
        System.out.printf(" Items imported:         %d%n", result.getItemsImported());
        System.out.printf(" Bays created:           %d%n", result.getBaysImported());
        System.out.printf(" Wagons processed:       %d%n", result.getWagonsImported());
        System.out.printf(" Boxes unloaded:         %d%n", result.getBoxesUnloaded());

        if (result.getBoxesFlaggedForInspection() > 0) {
            System.out.printf(" Flagged for inspection: %d%n", result.getBoxesFlaggedForInspection());
        }

        System.out.printf(" Validation errors:      %d%n", result.getErrors().size());

        if (result.hasWarnings()) {
            System.out.println("────────────────────────────────────────────────────────────");
            System.out.println("⚠️  Warnings:");
            for (String warning : result.getWarnings()) {
                System.out.println("  • " + warning);
            }
        }

        System.out.println("════════════════════════════════════════════════════════════");
    }

    public static void showFEFOValidation(Warehouse warehouse) {
        System.out.println("\n════════════════════════════════════════════════════════════");
        System.out.println("🔍 FEFO/FIFO VALIDATION");
        System.out.println("════════════════════════════════════════════════════════════");

        String exampleSku = findSKUWithMultipleBoxes(warehouse);
        if (exampleSku == null) {
            System.out.println("No SKU with multiple boxes found for validation.");
            System.out.println("════════════════════════════════════════════════════════════");
            return;
        }

        List<Box> orderedBoxes = getAllBoxesForSKU(warehouse, exampleSku);
        if (orderedBoxes.size() < 2) {
            System.out.println("Not enough boxes for validation example.");
            System.out.println("════════════════════════════════════════════════════════════");
            return;
        }

        System.out.println("Sample verification for " + exampleSku + ":");
        System.out.println();
        System.out.println("Expected order (FEFO/FIFO rules):");
        System.out.println("  1. Earliest expiry date first");
        System.out.println("  2. Oldest received date for same expiry");
        System.out.println("  3. BoxId ascending for ties");
        System.out.println();
        System.out.println("Actual order in warehouse:");

        for (int i = 0; i < Math.min(3, orderedBoxes.size()); i++) {
            Box box = orderedBoxes.get(i);
            System.out.printf("  %d. %s%n", i + 1, formatBoxForDisplay(box));
        }

        System.out.println();
        System.out.println("Validation checks:");

        boolean fefoOK = verifyFEFO(warehouse);
        System.out.println(" " + (fefoOK ? "✅" : "❌") + " Expiry dates in ascending order (nulls last)");
        System.out.println(" " + (fefoOK ? "✅" : "❌") + " Received dates ordered correctly within same expiry");
        System.out.println(" " + (fefoOK ? "✅" : "❌") + " BoxIds ordered correctly for ties");
        System.out.println();
        System.out.println("Result: " + (fefoOK ? "✅ FEFO/FIFO ORDER VERIFIED" : "❌ FEFO/FIFO ORDER FAILED"));

        System.out.println("════════════════════════════════════════════════════════════");
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
