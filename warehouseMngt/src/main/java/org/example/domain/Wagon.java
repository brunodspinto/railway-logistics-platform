package org.example.domain;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a railway wagon carrying multiple boxes to be unloaded at the terminal.
 * Each wagon has a unique identifier and contains a collection of boxes.

 */
public class Wagon {
    private final String wagonId;
    private final List<Box> boxes;

    public Wagon(String wagonId) {
        if (wagonId == null || wagonId.trim().isEmpty()) {
            throw new IllegalArgumentException("Wagon ID cannot be null or empty");
        }
        this.wagonId = wagonId.trim();
        this.boxes = new ArrayList<>();
    }

    public String getWagonId() {
        return wagonId;
    }

    public List<Box> getBoxes() {
        return new ArrayList<>(boxes); // Defensive copy
    }

    public void addBox(Box box) {
        if (box == null) {
            throw new IllegalArgumentException("Box cannot be null");
        }
        boxes.add(box);
    }

    public int getBoxCount() {
        return boxes.size();
    }

    public boolean isEmpty() {
        return boxes.isEmpty();
    }

    /**
     * Calculates the total quantity of a specific SKU in this wagon.
     */
    public int getTotalQuantityForSku(String sku) {
        return boxes.stream()
                .filter(box -> box.getSku().equals(sku))
                .mapToInt(Box::getQuantity)
                .sum();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Wagon wagon = (Wagon) o;
        return Objects.equals(wagonId, wagon.wagonId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(wagonId);
    }

    @Override
    public String toString() {
        return String.format("Wagon[id=%s, boxes=%d]", wagonId, boxes.size());
    }
}

