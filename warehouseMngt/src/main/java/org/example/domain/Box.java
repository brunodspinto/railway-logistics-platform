package org.example.domain;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Represents a box of products to be stored in warehouse bays.
 * Implements FEFO/FIFO ordering for perishable/non-perishable goods.
 */
public class Box implements Comparable<Box> {

    private final String boxId;
    private final String sku;
    private int quantity;
    private final LocalDate expiryDate;
    private final Instant receivedAt;
    private final String wagonId;
    private Location location;

    public Box(String boxId, String sku, int quantity, LocalDate expiryDate,
               Instant receivedAt, String wagonId) {
        // Validações mantidas (são essenciais)
        if (boxId == null || boxId.trim().isEmpty()) {
            throw new IllegalArgumentException("BoxId cannot be null or empty");
        }
        if (sku == null || sku.trim().isEmpty()) {
            throw new IllegalArgumentException("SKU cannot be null or empty");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        if (receivedAt == null) {
            throw new IllegalArgumentException("ReceivedAt cannot be null");
        }
        if (wagonId == null || wagonId.trim().isEmpty()) {
            throw new IllegalArgumentException("WagonId cannot be null or empty");
        }

        this.boxId = boxId.trim();
        this.sku = sku.trim();
        this.quantity = quantity;
        this.expiryDate = expiryDate;
        this.receivedAt = receivedAt;
        this.wagonId = wagonId.trim();
        this.location = null;
    }

    // ==================== GETTERS (mantemos todos) ====================

    public String getBoxId() { return boxId; }
    public String getSku() { return sku; }
    public int getQuantity() { return quantity; }
    public LocalDate getExpiryDate() { return expiryDate; }
    public Instant getReceivedAt() { return receivedAt; }
    public String getWagonId() { return wagonId; }
    public Location getLocation() { return location; }

    // ==================== BUSINESS METHODS (essenciais) ====================

    public boolean isPerishable() {
        return expiryDate != null;
    }

    public void setLocation(Location location) {
        if (location == null) {
            throw new IllegalArgumentException("Location cannot be null");
        }
        this.location = location;
    }

    public void setQuantity(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        this.quantity = quantity;
    }

    /**
     * Simplified reduceQuantity - just does the reduction
     */
    public void reduceQuantity(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Reduction amount must be positive");
        }
        if (amount > quantity) {
            throw new IllegalArgumentException(
                    String.format("Cannot reduce %s by %d - only %d available", boxId, amount, quantity)
            );
        }
        this.quantity -= amount;
    }

    // ==================== FEFO/FIFO COMPARISON (CRÍTICO - mantemos igual) ====================

    @Override
    public int compareTo(Box other) {
        // 1. Compare by expiryDate (earliest first, null last)
        if (this.expiryDate == null && other.expiryDate == null) {
            // Both non-perishable, move to next criterion
        } else if (this.expiryDate == null) {
            return 1;  // this goes after (non-perishable after perishable)
        } else if (other.expiryDate == null) {
            return -1; // this goes before (perishable before non-perishable)
        } else {
            int dateComparison = this.expiryDate.compareTo(other.expiryDate);
            if (dateComparison != 0) {
                return dateComparison;
            }
        }

        // 2. Compare by receivedAt (oldest first)
        int timeComparison = this.receivedAt.compareTo(other.receivedAt);
        if (timeComparison != 0) {
            return timeComparison;
        }

        // 3. Tie-break by boxId
        return this.boxId.compareTo(other.boxId);
    }

    // ==================== BASIC OBJECT METHODS ====================

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Box box = (Box) o;
        return Objects.equals(boxId, box.boxId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(boxId);
    }

    @Override
    public String toString() {
        return String.format("Box[%s, SKU=%s, qty=%d, exp=%s, wagon=%s, loc=%s]",
                boxId, sku, quantity, expiryDate, wagonId,
                location != null ? location.toFormattedString() : "unassigned");
    }
}
