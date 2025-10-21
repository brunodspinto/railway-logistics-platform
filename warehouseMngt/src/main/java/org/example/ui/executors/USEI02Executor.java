package org.example.ui.executors;

import org.example.repository.*;
import org.example.domain.*;
import org.example.results.OrderAllocationResult;
import org.example.service.*;
import org.example.ui.menu.DisplayHelper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;

public class USEI02Executor {

    public static List<OrderAllocationResult> execute(WarehouseRepository warehouseRepo, String orderLinesPath) {
        DisplayHelper.printHeader("USEI02 - Order Allocation");

        try {
            Warehouse warehouse = warehouseRepo.findDefault();
            if (warehouse == null) {
                DisplayHelper.printError("❌ No warehouse available for USEI02 - Run USEI01 first!");
                return Collections.emptyList();
            }

            System.out.println("✅ Warehouse loaded: " + warehouse.getWarehouseId());

            OrderAllocationService service = new OrderAllocationService(warehouse);
            List<OrderLine> orders = loadOrderLinesFromCsv(orderLinesPath);

            if (orders.isEmpty()) {
                DisplayHelper.printError("No orders could be loaded - check file format and path");
                return Collections.emptyList();
            }

            System.out.println("✅ Loaded " + orders.size() + " order lines");
            System.out.println("\n🚀 Starting Order Allocation...");

            List<OrderAllocationResult> results = service.allocateOrders(orders, false);

            if (results == null || results.isEmpty()) {
                DisplayHelper.printError("❌ Order allocation produced no results");
                return Collections.emptyList();
            }

            System.out.println("✅ Generated " + results.size() + " allocation results");

            int eligible = 0, partial = 0, undispatchable = 0;

            for (OrderAllocationResult result : results) {
                System.out.println("\n" + result.toString());

                if (!result.getAllocations().isEmpty()) {
                    System.out.println("  Allocations:");
                    result.getAllocations().forEach(allocation ->
                            System.out.println("    - " + allocation.toString()));
                }

                // Contar por status
                switch (result.getStatus()) {
                    case ELIGIBLE -> eligible++;
                    case PARTIAL -> partial++;
                    case UNDISPATCHABLE -> undispatchable++;
                }
            }

            // ✅ RESUMO FINAL
            System.out.println("\n📊 ORDER ALLOCATION SUMMARY");
            System.out.println("────────────────────────────────────────────");
            System.out.printf(" Total orders processed: %d%n", results.size());
            System.out.printf(" ✅ Eligible: %d%n", eligible);
            System.out.printf(" ⚠️  Partial: %d%n", partial);
            System.out.printf(" ❌ Undispatchable: %d%n", undispatchable);
            System.out.printf(" 📦 Success rate: %.1f%%%n",
                    (eligible + partial) * 100.0 / results.size());

            DisplayHelper.printSuccess("USEI02 completed successfully");

            return results; // 🔹 devolve a lista para ser usada na USEI03

        } catch (Exception e) {
            DisplayHelper.printError("USEI02 execution failed: " + e.getMessage());
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    // ==============================================================
    private static List<OrderLine> loadOrderLinesFromCsv(String csvPath) {
        List<OrderLine> lines = new ArrayList<>();
        File f = new File(csvPath);

        if (!f.exists()) {
            System.out.println("❌ File not found: " + csvPath);
            return lines;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String header = br.readLine(); // Skip header

            if (header == null) {
                System.out.println("⚠️ File is empty");
                return lines;
            }

            String row;
            while ((row = br.readLine()) != null) {
                if (row.isBlank()) continue;

                String[] parts = row.split(",", -1);
                if (parts.length < 4) continue;

                try {
                    OrderLine orderLine = new OrderLine(
                            parts[0].trim(),
                            Integer.parseInt(parts[1].trim()),
                            parts[2].trim(),
                            Integer.parseInt(parts[3].trim())
                    );
                    lines.add(orderLine);

                } catch (NumberFormatException e) {
                    // Silently skip invalid lines
                }
            }

        } catch (Exception e) {
            System.out.println("❌ Failed to read file: " + e.getMessage());
        }
        return lines;
    }
}
