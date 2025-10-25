package org.example.domain;

import java.util.Objects;

/**
 * Represents a product in the warehouse catalog.
 * Simplified for USEI01 - focused on wagon unloading validation.
 */
public class Item {

    private final String sku;
    private final String name;
    private final String category;
    private final String unit;
    private final double volume;
    private final double unitWeight;

    public Item(String sku, String name, String category, String unit,
                double volume, double unitWeight) {

        if (sku == null || sku.trim().isEmpty()) {
            throw new IllegalArgumentException("SKU cannot be null or empty");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        if (category == null || category.trim().isEmpty()) {
            throw new IllegalArgumentException("Category cannot be null or empty");
        }
        if (unit == null || unit.trim().isEmpty()) {
            throw new IllegalArgumentException("Unit cannot be null or empty");
        }
        if (volume <= 0) {
            throw new IllegalArgumentException("Volume must be positive");
        }
        if (unitWeight <= 0) {
            throw new IllegalArgumentException("Unit weight must be positive");
        }

        this.sku = sku.trim();
        this.name = name.trim();
        this.category = category.trim();
        this.unit = unit.trim();
        this.volume = volume;
        this.unitWeight = unitWeight;
    }

    // ==================== GETTERS ESSENCIAIS ====================

    public String getSku() { return sku; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public String getUnit() { return unit; }
    public double getVolume() { return volume; }
    public double getUnitWeight() { return unitWeight; }


    // ==================== OBJECT METHODS ====================

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return Objects.equals(sku, item.sku);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sku);
    }

    @Override
    public String toString() {
        return String.format("Item[%s: %s, %s, %.3f%s]",
                sku, name, category, unitWeight, unit);
    }
}