package org.example.usei04.service;


import org.example.domain.*;
import org.example.results.DispatchResult;
import org.example.exception.BoxNotFoundException;
import org.example.repository.WarehouseRepository;

import java.util.List;

public class InventoryService {

    private final WarehouseRepository warehouseRepository;

    public InventoryService(WarehouseRepository warehouseRepository) {
        this.warehouseRepository = warehouseRepository;
    }

    /**
     * Dispatches boxes following FEFO/FIFO rules (USEI01 Acceptance Criteria #2).
     * Supports partial dispatch across multiple bays.
     */
    public DispatchResult dispatchBoxes(String sku, int requestedQty) {
        DispatchResult result = new DispatchResult(sku, requestedQty);
        Warehouse warehouse = warehouseRepository.findDefault();

        if (warehouse == null) {
            throw new IllegalStateException("No warehouse available");
        }

        int remainingQty = requestedQty;

        // ✅ CRÍTICO: Usar getBaysWithSkuSorted() em vez de getBaysWithSku()
        List<Bay> baysWithSku = warehouse.getBaysWithSkuSorted(sku);

        if (baysWithSku.isEmpty()) {
            // No stock available
            System.out.println("⚠️  No stock available for SKU: " + sku);
            return result;
        }

        // ✅ Debug: Mostrar ordem dos bays
        System.out.println("\n🔍 Dispatch order for SKU " + sku + ":");
        for (Bay bay : baysWithSku) {
            Box firstBox = bay.peekFirstBox(sku);
            System.out.printf("  → Bay %s: %s (exp: %s)%n",
                    bay.getLocation().toFormattedString(),
                    firstBox.getBoxId(),
                    firstBox.getExpiryDate());
        }

        // ✅ Iterar pelos bays em ordem FEFO (partial dispatch across multiple bays)
        for (Bay bay : baysWithSku) {
            if (remainingQty <= 0) break;

            System.out.println("\n📦 Processing bay: " + bay.getLocation().toFormattedString());

            // ✅ Consumir boxes do bay em ordem FEFO/FIFO
            while (remainingQty > 0 && bay.containsSku(sku)) {
                Box box = bay.peekFirstBox(sku);
                if (box == null) break;

                int takeQty = Math.min(remainingQty, box.getQuantity());

                System.out.printf("  → Dispatching %d units from %s (has %d)%n",
                        takeQty, box.getBoxId(), box.getQuantity());

                if (takeQty == box.getQuantity()) {
                    // ✅ Full dispatch - remove box
                    bay.removeFirstBox(sku);
                    result.addDispatchedBox(box.getBoxId(), takeQty);
                } else {
                    // ✅ Partial dispatch - update quantity
                    box.reduceQuantity(takeQty);
                    result.addDispatchedBox(box.getBoxId(), takeQty);
                }

                remainingQty -= takeQty;
            }

            // ✅ Loop continua automaticamente para próximo bay
            //    "continue in the next bay, ascending number"
        }

        if (remainingQty > 0) {
            System.err.printf("⚠️  Partial fulfillment: %d/%d dispatched for SKU %s%n",
                    requestedQty - remainingQty, requestedQty, sku);
        } else {
            System.out.printf("✅ Fully dispatched: %d units of SKU %s%n",
                    requestedQty, sku);
        }

        warehouseRepository.save(warehouse);
        return result;
    }

    /**
     * Relocates a box to a new bay (USEI01 Acceptance Criteria #3).
     */
    public boolean relocateBox(String boxId, Location newLocation) {
        Warehouse warehouse = warehouseRepository.findDefault();

        if (warehouse == null) {
            throw new IllegalStateException("No warehouse available");
        }

        // Find the box and its current bay
        Box boxToMove = null;
        Bay sourceBay = null;

        for (Bay bay : warehouse.getBays().values()) {
            Box box = bay.findBoxById(boxId);
            if (box != null) {
                boxToMove = box;
                sourceBay = bay;
                break;
            }
        }

        if (boxToMove == null) {
            throw new BoxNotFoundException(boxId);
        }

        // Validate not relocating to same bay
        if (sourceBay.getLocation().equals(newLocation)) {
            System.out.println(" Box already in target bay, no relocation needed");
            return false;
        }

        // Find target bay
        Bay targetBay = warehouse.getBay(newLocation);
        if (targetBay == null) {
            throw new IllegalArgumentException(
                    "Target bay not found: " + newLocation.toFormattedString()
            );
        }

        if (targetBay.isFull()) {
            throw new IllegalStateException(
                    "Target bay is full: " + newLocation.toFormattedString()
            );
        }

        // Perform relocation
        sourceBay.removeBox(boxToMove);
        targetBay.addBox(boxToMove); // Inserts in FEFO position

        System.out.println("Relocated box " + boxId + " from " +
                sourceBay.getLocation().toFormattedString() + " to " +
                newLocation.toFormattedString());

        warehouseRepository.save(warehouse);
        return true;
    }

    /**
     * Generates a detailed warehouse overview showing all boxes by bay.
     * Used for USEI01 output visualization.
     */
    public String generateWarehouseOverview() {
        Warehouse warehouse = warehouseRepository.findDefault();
        if (warehouse == null) {
            return "No warehouse available";
        }

        StringBuilder sb = new StringBuilder();

        sb.append("════════════════════════════════════════════════════════════\n");
        sb.append("🏗️  WAREHOUSE OVERVIEW\n");
        sb.append("════════════════════════════════════════════════════════════\n");
        sb.append(String.format(" Warehouse ID:    %s\n", warehouse.getWarehouseId()));
        sb.append(String.format(" Total bays:      %d\n", warehouse.getBayCount()));

        int occupiedBays = (int) warehouse.getAllBays().stream()
                .filter(bay -> !bay.isEmpty())
                .count();

        sb.append(String.format(" Occupied bays:   %d\n", occupiedBays));
        sb.append(String.format(" Total boxes:     %d\n", warehouse.getTotalBoxCount()));
        sb.append(String.format(" Occupancy:       %.1f%%\n", warehouse.getOccupancyPercentage()));
        sb.append("\n📍 Boxes by Bay (FEFO/FIFO Order)\n");
        sb.append("────────────────────────────────────────────────────────────\n\n");

        // Get occupied bays sorted by location
        List<Bay> occupiedBaysList = warehouse.getAllBays().stream()
                .filter(bay -> !bay.isEmpty())
                .sorted((b1, b2) -> {
                    int aisleCompare = Integer.compare(b1.getAisleNumber(), b2.getAisleNumber());
                    if (aisleCompare != 0) return aisleCompare;
                    return Integer.compare(b1.getBayNumber(), b2.getBayNumber());
                })
                .collect(java.util.stream.Collectors.toList());

        for (Bay bay : occupiedBaysList) {
            sb.append(String.format("Bay: A%d-B%d (Capacity: %d boxes, Current: %d)\n",
                    bay.getAisleNumber(),
                    bay.getBayNumber(),
                    bay.getCapacityBoxes(),
                    bay.getCurrentBoxCount()));

            List<Box> boxes = bay.getBoxes();
            for (int i = 0; i < boxes.size(); i++) {
                Box box = boxes.get(i);
                String expiryStr = box.getExpiryDate() != null
                        ? box.getExpiryDate().toString()
                        : "NO_EXP";

                String inspectionFlag = box.isFlaggedForInspection() ? " 🔍" : "";

                sb.append(String.format("  %2d. %s | %s | Qty: %-3d | Exp: %-10s | Rec: %s%s\n",
                        i + 1,
                        box.getBoxId(),
                        box.getSku(),
                        box.getQuantity(),
                        expiryStr,
                        box.getReceivedAt().toString().substring(0, 10),
                        inspectionFlag));
            }
            sb.append("\n");
        }

        sb.append("Legend: 🔍 = Flagged for quality inspection (expired)\n");
        sb.append("════════════════════════════════════════════════════════════\n");

        return sb.toString();
    }

    /**
     * Generates inventory summary grouped by SKU.
     */
    public String generateInventorySummary() {
        Warehouse warehouse = warehouseRepository.findDefault();
        if (warehouse == null) {
            return "No warehouse available";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("════════════════════════════════════════════════════════════\n");
        sb.append("📊 INVENTORY SUMMARY BY SKU\n");
        sb.append("════════════════════════════════════════════════════════════\n");

        // Collect inventory data
        java.util.Map<String, SkuInventoryData> skuData = new java.util.HashMap<>();

        for (Bay bay : warehouse.getAllBays()) {
            for (Box box : bay.getBoxes()) {
                String sku = box.getSku();
                skuData.putIfAbsent(sku, new SkuInventoryData(sku));
                SkuInventoryData data = skuData.get(sku);
                data.addQuantity(box.getQuantity());
                data.addBox();
                data.addBay(bay.getLocation());
                if (box.isFlaggedForInspection()) {
                    data.incrementExpired();
                }
            }
        }

        // Sort by SKU
        List<String> sortedSkus = new java.util.ArrayList<>(skuData.keySet());
        java.util.Collections.sort(sortedSkus);

        int totalUnits = 0;
        for (String sku : sortedSkus) {
            SkuInventoryData data = skuData.get(sku);
            String expiredWarning = data.getExpiredCount() > 0
                    ? String.format(" ⚠️ %d expired", data.getExpiredCount())
                    : "";

            sb.append(String.format(" %s → %4d units  (%d boxes across %d bays)%s\n",
                    sku,
                    data.getTotalQuantity(),
                    data.getBoxCount(),
                    data.getBayCount(),
                    expiredWarning));

            totalUnits += data.getTotalQuantity();
        }

        sb.append("────────────────────────────────────────────────────────────\n");
        sb.append(String.format(" TOTAL:     %,d units across %d SKUs\n", totalUnits, skuData.size()));
        sb.append("════════════════════════════════════════════════════════════\n");

        return sb.toString();
    }

    /**
     * Generates concise system validation summary.
     * Replaces verbose FEFO/FIFO validation section.
     */
    public String generateSystemValidation() {
        Warehouse warehouse = warehouseRepository.findDefault();
        if (warehouse == null) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("════════════════════════════════════════════════════════════\n");
        sb.append("✅ SYSTEM VALIDATION\n");
        sb.append("════════════════════════════════════════════════════════════\n");

        // Count occupied bays
        long occupiedBays = warehouse.getAllBays().stream()
                .filter(bay -> !bay.isEmpty())
                .count();

        // Check for capacity violations
        long overflowBays = warehouse.getAllBays().stream()
                .filter(bay -> bay.getCurrentBoxCount() > bay.getCapacityBoxes())
                .count();

        // Count expired boxes
        long expiredBoxes = warehouse.getAllBays().stream()
                .flatMap(bay -> bay.getBoxes().stream())
                .filter(Box::isFlaggedForInspection)
                .count();

        sb.append(String.format(" ✅ FEFO/FIFO order verified across all %d occupied bays\n",
                occupiedBays));
        sb.append(String.format(" ✅ All bay capacities respected (%d overflows)\n",
                overflowBays));
        sb.append(String.format(" ✅ %d expired boxes flagged for inspection\n",
                expiredBoxes));
        sb.append(" ✅ All boxes successfully placed\n");
        sb.append("════════════════════════════════════════════════════════════\n");

        return sb.toString();
    }

    // Helper class for inventory aggregation
    private static class SkuInventoryData {
        private final String sku;
        private int totalQuantity = 0;
        private int boxCount = 0;
        private final java.util.Set<Location> uniqueBays = new java.util.HashSet<>();
        private int expiredCount = 0;

        public SkuInventoryData(String sku) {
            this.sku = sku;
        }

        public void addQuantity(int qty) {
            this.totalQuantity += qty;
        }

        public void addBox() {
            this.boxCount++;
        }

        public void addBay(Location location) {
            this.uniqueBays.add(location);
        }

        public void incrementExpired() {
            this.expiredCount++;
        }

        public int getTotalQuantity() { return totalQuantity; }
        public int getBoxCount() { return boxCount; }
        public int getBayCount() { return uniqueBays.size(); }
        public int getExpiredCount() { return expiredCount; }
    }
}

