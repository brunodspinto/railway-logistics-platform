package org.example.service;

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
        List<Bay> baysWithSku = warehouse.getBaysWithSku(sku);

        for (Bay bay : baysWithSku) {
            if (remainingQty <= 0) break;

            // Process boxes in this bay
            while (remainingQty > 0 && bay.containsSku(sku)) {
                Box box = bay.peekFirstBox(sku);
                if (box == null) break;

                int takeQty = Math.min(remainingQty, box.getQuantity());

                if (takeQty == box.getQuantity()) {
                    // Full dispatch - remove completely
                    bay.removeFirstBox(sku);
                    result.addDispatchedBox(box.getBoxId(), takeQty);

                } else {
                    // Partial dispatch - create new box with remaining quantity
                    Box originalBox = bay.removeFirstBox(sku);

                    // Create new box with remaining quantity
                    Box remainingBox = new Box(
                            originalBox.getBoxId(),
                            originalBox.getSku(),
                            originalBox.getQuantity() - takeQty, // Remaining
                            originalBox.getExpiryDate(),
                            originalBox.getReceivedAt(),
                            originalBox.getWagonId()
                    );

                    // Re-add with updated quantity (will re-sort correctly)
                    bay.addBox(remainingBox);

                    result.addDispatchedBox(originalBox.getBoxId(), takeQty);
                }

                remainingQty -= takeQty;
            }
        }

        // Log if request couldn't be fully satisfied
        if (remainingQty > 0) {
            System.err.println(" Partial fulfillment: " +
                    (requestedQty - remainingQty) + "/" + requestedQty +
                    " dispatched for SKU " + sku);
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
}
