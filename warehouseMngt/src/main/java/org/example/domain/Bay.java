package org.example.domain;

import java.util.*;

/**
 * Represents a storage bay that maintains boxes in FEFO/FIFO order.
 */
public class Bay {

    private final Location location;
    private final int capacityBoxes;
    private final List<Box> boxes;  // Maintained in FEFO/FIFO order

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

    public Location getLocation() { return location; }
    public String getWarehouseId() { return location.getWarehouseId(); }
    public int getAisleNumber() { return location.getAisle(); }
    public int getBayNumber() { return location.getBay(); }
    public int getCapacityBoxes() { return capacityBoxes; }
    public List<Box> getBoxes() { return Collections.unmodifiableList(boxes); }
    public int getCurrentBoxCount() { return boxes.size(); }
    public int getAvailableCapacity() { return capacityBoxes - boxes.size(); }
    public boolean isFull() { return boxes.size() >= capacityBoxes; }
    public boolean isEmpty() { return boxes.isEmpty(); }
    public boolean hasAvailableSpace() { return boxes.size() < capacityBoxes; }

    // ==================== ESSENTIAL SKU OPERATIONS ====================

    public boolean containsSku(String sku) {
        for (Box box : boxes) {
            if (box.getSku().equals(sku)) {
                return true;
            }
        }
        return false;
    }

    public int getQuantityForSku(String sku) {
        int total = 0;
        for (Box box : boxes) {
            if (box.getSku().equals(sku)) {
                total += box.getQuantity();
            }
        }
        return total;
    }

    // ==================== CORE BOX OPERATIONS (USEI01) ====================

    /**
     * Adds a box while maintaining FEFO/FIFO order.
     * Uses binary search for efficient insertion.
     */
    public void addBox(Box box) {
        if (box == null) {
            throw new IllegalArgumentException("Cannot add null box");
        }
        if (isFull()) {
            throw new IllegalStateException(
                    String.format("Bay %s is full (capacity: %d)", location.toFormattedString(), capacityBoxes)
            );
        }

        // Check for duplicate boxId in this bay
        for (Box existing : boxes) {
            if (existing.getBoxId().equals(box.getBoxId())) {
                throw new IllegalArgumentException(
                        String.format("Box %s already exists in this bay", box.getBoxId())
                );
            }
        }

        // Efficient ordered insertion
        int index = Collections.binarySearch(boxes, box);
        if (index < 0) {
            index = -index - 1;  // Insertion point
        }
        boxes.add(index, box);

        // Update box location
        box.setLocation(location);
    }

    /**
     * Removes and returns the first box (FEFO/FIFO) with the specified SKU.
     * This is the primary method for dispatch operations.
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
     * Removes a specific box by reference (for relocation operations).
     * Returns true if the box was found and removed.
     */
    public boolean removeBox(Box box) {
        return boxes.remove(box);
    }

    /**
     * Finds a box by ID - simple linear search is acceptable for MVP.
     */
    public Box findBoxById(String boxId) {
        for (Box box : boxes) {
            if (box.getBoxId().equals(boxId)) {
                return box;
            }
        }
        return null;
    }

    // ==================== BASIC OBJECT METHODS ====================

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
}
