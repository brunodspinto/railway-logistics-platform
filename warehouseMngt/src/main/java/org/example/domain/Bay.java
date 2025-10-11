package org.example.domain;


import org.example.domain.Box;
import org.example.domain.Location;

import java.util.*;

/**
 * Represents a storage bay that maintains boxes in FEFO/FIFO order.
 * Boxes are automatically sorted upon insertion according to:
 * 1. Expiry date (earliest first, null last)
 * 2. ReceivedAt timestamp (oldest first)
 * 3. BoxId (ascending, tie-breaker)
 */
public class Bay {

    private final Location location;
    private final int capacityBoxes;
    private final List<Box> boxes;  // Maintained in FEFO/FIFO order

    // ==================== CONSTRUCTORS ====================

    public Bay(String warehouseId, int aisleNumber, int bayNumber, int capacityBoxes) {
        if (capacityBoxes <= 0) {
            throw new IllegalArgumentException("Capacity must be positive");
        }

        this.location = new Location(warehouseId, aisleNumber, bayNumber);
        this.capacityBoxes = capacityBoxes;
        this.boxes = new ArrayList<>();
    }

    public Bay(Location location, int capacityBoxes) {
        if (location == null) {
            throw new IllegalArgumentException("Location cannot be null");
        }
        if (capacityBoxes <= 0) {
            throw new IllegalArgumentException("Capacity must be positive");
        }

        this.location = location;
        this.capacityBoxes = capacityBoxes;
        this.boxes = new ArrayList<>();
    }

    // ==================== BASIC GETTERS ====================

    public Location getLocation() {
        return location;
    }

    public String getWarehouseId() {
        return location.getWarehouseId();
    }

    public int getAisleNumber() {
        return location.getAisle();
    }

    public int getBayNumber() {
        return location.getBay();
    }

    public int getCapacityBoxes() {
        return capacityBoxes;
    }

    /**
     * Returns an unmodifiable view of the boxes.
     * Boxes are in FEFO/FIFO order.
     */
    public List<Box> getBoxes() {
        return Collections.unmodifiableList(boxes);
    }

    public int getCurrentBoxCount() {
        return boxes.size();
    }

    public int getAvailableCapacity() {
        return capacityBoxes - boxes.size();
    }

    public boolean isFull() {
        return boxes.size() >= capacityBoxes;
    }

    public boolean isEmpty() {
        return boxes.isEmpty();
    }

    public boolean hasAvailableSpace() {
        return boxes.size() < capacityBoxes;
    }

    // ==================== SKU QUERY OPERATIONS ====================

    /**
     * Checks if this bay contains any box with the specified SKU.
     */
    public boolean containsSku(String sku) {
        for (Box box : boxes) {
            if (box.getSku().equals(sku)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the total quantity of a specific SKU in this bay.
     */
    public int getQuantityForSku(String sku) {
        int total = 0;
        for (Box box : boxes) {
            if (box.getSku().equals(sku)) {
                total += box.getQuantity();
            }
        }
        return total;
    }

    /**
     * Returns all boxes containing the specified SKU, in FEFO/FIFO order.
     */
    public List<Box> getBoxesForSku(String sku) {
        List<Box> result = new ArrayList<>();
        for (Box box : boxes) {
            if (box.getSku().equals(sku)) {
                result.add(box);
            }
        }
        return result;
    }

    // ==================== CORE BOX OPERATIONS (USEI01) ====================

    /**
     * Adds a box while maintaining FEFO/FIFO order.
     * Uses binary search for O(log n) insertion point finding.
     *
     * IMPORTANT: Box class MUST implement Comparable<Box> with FEFO/FIFO rules.
     *
     * @param box the box to add
     * @throws IllegalArgumentException if box is null or duplicate boxId
     * @throws IllegalStateException if bay is full
     */
    public void addBox(Box box) {
        if (box == null) {
            throw new IllegalArgumentException("Cannot add null box");
        }
        if (isFull()) {
            throw new IllegalStateException(
                    String.format("Bay %s is full (capacity: %d)",
                            location.toFormattedString(), capacityBoxes)
            );
        }

        // ✅ Check for duplicate boxId in this bay
        for (Box existing : boxes) {
            if (existing.getBoxId().equals(box.getBoxId())) {
                throw new IllegalArgumentException(
                        String.format("Duplicate boxId %s in bay %s",
                                box.getBoxId(), location.toFormattedString())
                );
            }
        }

        // ✅ Efficient ordered insertion using binary search
        int index = Collections.binarySearch(boxes, box);
        if (index < 0) {
            index = -index - 1;  // Convert to insertion point
        }
        boxes.add(index, box);

        // ✅ Update box location
        box.setLocation(location);
    }

    /**
     * Returns (but does NOT remove) the first box with the specified SKU.
     * Used for inspection before dispatch decisions.
     *
     * @param sku the SKU to search for
     * @return the first box in FEFO/FIFO order, or null if not found
     */
    public Box peekFirstBox(String sku) {
        for (Box box : boxes) {
            if (box.getSku().equals(sku)) {
                return box;
            }
        }
        return null;
    }

    /**
     * Removes and returns the first box (FEFO/FIFO) with the specified SKU.
     * This is the primary method for dispatch operations (USEI01 Criteria #2).
     *
     * @param sku the SKU to dispatch
     * @return the removed box, or null if no box with this SKU exists
     */
    public Box removeFirstBox(String sku) {
        for (int i = 0; i < boxes.size(); i++) {
            Box box = boxes.get(i);
            if (box.getSku().equals(sku)) {
                return boxes.remove(i);  // Remove and return
            }
        }
        return null;  // No box with this SKU found
    }

    /**
     * Removes a specific box by reference.
     * Used for relocation operations (USEI01 Criteria #3).
     *
     * @param box the box to remove
     * @return true if the box was found and removed
     */
    public boolean removeBox(Box box) {
        return boxes.remove(box);
    }

    /**
     * Finds a box by ID without removing it.
     * Linear search is acceptable for MVP scale.
     *
     * @param boxId the box ID to search for
     * @return the box if found, null otherwise
     */
    public Box findBoxById(String boxId) {
        for (Box box : boxes) {
            if (box.getBoxId().equals(boxId)) {
                return box;
            }
        }
        return null;
    }

    // ==================== WAREHOUSE OPERATIONS ====================

    /**
     * Calculates a suitability score for storing a specific SKU.
     * Higher score = better fit.
     *
     * Logic:
     * - Prefer bays that already contain this SKU (consolidation)
     * - Prefer bays with more available space
     *
     * @param sku the SKU to score for
     * @return suitability score (higher is better)
     */
    public int getSuitabilityScore(String sku) {
        if (isFull()) return -1;  // Cannot accept

        int score = getAvailableCapacity();  // Base score

        // Bonus for SKU consolidation
        if (containsSku(sku)) {
            score += 100;  // Strong preference for same SKU
        }

        return score;
    }

    // ==================== OBJECT METHODS ====================

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Bay bay = (Bay) o;
        return Objects.equals(location, bay.location);
    }

    @Override
    public int hashCode() {
        return Objects.hash(location);
    }

    @Override
    public String toString() {
        return String.format("Bay[%s, %d/%d boxes]",
                location.toFormattedString(), boxes.size(), capacityBoxes);
    }

    /**
     * Returns detailed summary of bay contents.
     * Useful for debugging and reporting.
     */
    public String getDetailedSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append(toString()).append("\n");

        if (boxes.isEmpty()) {
            sb.append("  (empty)");
        } else {
            Map<String, Integer> skuCounts = new HashMap<>();
            for (Box box : boxes) {
                skuCounts.merge(box.getSku(), box.getQuantity(), Integer::sum);
            }

            for (Map.Entry<String, Integer> entry : skuCounts.entrySet()) {
                sb.append(String.format("  - %s: %d units\n",
                        entry.getKey(), entry.getValue()));
            }
        }

        return sb.toString();
    }
}