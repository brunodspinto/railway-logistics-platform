package org.example.domain;

import java.util.Objects;

/**
 * Represents a physical location within a warehouse storage system.
 * A location is uniquely identified by a combination of warehouse ID, aisle number, and bay number.

 */
public class Location {

    private final String warehouseId;
    private final int aisle;
    private final int bay;

    /**
     * Creates a new Location with the specified coordinates.
     *
     * @param warehouseId the warehouse identifier (must not be null or empty)
     * @param aisle the aisle number within the warehouse (must be positive)
     * @param bay the bay number within the aisle (must be positive)
     * @throws IllegalArgumentException if any parameter is invalid
     */
    public Location(String warehouseId, int aisle, int bay) {
        if (warehouseId == null || warehouseId.trim().isEmpty()) {
            throw new IllegalArgumentException("WarehouseId cannot be null or empty");
        }
        if (aisle <= 0) {
            throw new IllegalArgumentException("Aisle must be positive. Received: " + aisle);
        }
        if (bay <= 0) {
            throw new IllegalArgumentException("Bay must be positive. Received: " + bay);
        }

        this.warehouseId = warehouseId.trim();
        this.aisle = aisle;
        this.bay = bay;
    }

    /**
     * Gets the warehouse identifier.
     *
     * @return the warehouse ID
     */
    public String getWarehouseId() {
        return warehouseId;
    }

    /**
     * Gets the aisle number.
     *
     * @return the aisle number
     */
    public int getAisle() {
        return aisle;
    }

    /**
     * Gets the bay number.
     *
     * @return the bay number
     */
    public int getBay() {
        return bay;
    }

    /**
     * Checks if this location is in the same warehouse as another location.
     *
     * @param other the other location to compare
     * @return true if both locations are in the same warehouse, false otherwise
     */
    public boolean isSameWarehouse(Location other) {
        if (other == null) return false;
        return Objects.equals(this.warehouseId, other.warehouseId);
    }

    /**
     * Checks if this location is in the same aisle as another location.
     *
     * @param other the other location to compare
     * @return true if both locations are in the same warehouse and aisle, false otherwise
     */
    public boolean isSameAisle(Location other) {
        if (other == null) return false;
        return Objects.equals(this.warehouseId, other.warehouseId) &&
                this.aisle == other.aisle;
    }

    /**
     * Returns a formatted string representation of this location.
     * Format: "W{warehouseId}-A{aisle}-B{bay}"
     * Example: "W1-A2-B5"
     *
     * @return a formatted location string
     */
    public String toFormattedString() {
        return String.format("W%s-A%d-B%d", warehouseId, aisle, bay);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Location location = (Location) o;
        return Objects.equals(this.warehouseId, location.warehouseId) &&
                this.aisle == location.aisle &&
                this.bay == location.bay;
    }

    @Override
    public int hashCode() {
        return Objects.hash(warehouseId, aisle, bay);
    }

    @Override
    public String toString() {
        return "Location{" +
                "warehouseId=" + warehouseId +
                ", aisle=" + aisle +
                ", bay=" + bay +
                '}';
    }
}
