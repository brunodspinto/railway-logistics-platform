package org.example.domain;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Represents a warehouse that contains multiple bays for storing boxes.
 */
public class Warehouse {

    private final String warehouseId;
    private final Map<Location, Bay> bays;  // Location -> Bay

    /**
     * Creates a new Warehouse with the specified ID.
     *
     * @param warehouseId the warehouse identifier (must not be null or empty)
     */
    public Warehouse(String warehouseId) {
        if (warehouseId == null || warehouseId.trim().isEmpty()) {
            throw new IllegalArgumentException("WarehouseId cannot be null or empty");
        }

        this.warehouseId = warehouseId.trim();
        this.bays = new HashMap<>();
    }

    // ==================== GETTERS ====================

    public String getWarehouseId() {
        return warehouseId;
    }

    /**
     * Returns an unmodifiable view of all bays in this warehouse.
     *
     * @return map of locations to bays
     */
    public Map<Location, Bay> getBays() {
        return Collections.unmodifiableMap(bays);
    }

    /**
     * Returns a collection of all bays in this warehouse.
     * Useful for iterating over all bays.
     *
     * @return collection of all bays
     */
    public Collection<Bay> getAllBays() {
        return bays.values();
    }

    /**
     * Returns the total number of bays in this warehouse.
     *
     * @return number of bays
     */
    public int getBayCount() {
        return bays.size();
    }

    // ==================== BAY MANAGEMENT ====================

    /**
     * Adds a bay to this warehouse.
     * The bay must belong to this warehouse.
     *
     * @param bay the bay to add (must not be null)
     * @throws IllegalArgumentException if bay is null or doesn't belong to this warehouse
     * @throws IllegalStateException if a bay at this location already exists
     */
    public void addBay(Bay bay) {
        if (bay == null) {
            throw new IllegalArgumentException("Cannot add null bay to warehouse");
        }

        // Validate bay belongs to this warehouse
        if (!bay.getWarehouseId().equals(this.warehouseId)) {
            throw new IllegalArgumentException(
                    String.format("Bay warehouse (%s) doesn't match this warehouse (%s)",
                            bay.getWarehouseId(), this.warehouseId)
            );
        }

        Location location = bay.getLocation();

        // Check if bay already exists at this location
        if (bays.containsKey(location)) {
            throw new IllegalStateException(
                    String.format("Bay already exists at location %s",
                            location.toFormattedString())
            );
        }

        bays.put(location, bay);
    }

    /**
     * Gets a bay at the specified location.
     *
     * @param location the location to search
     * @return the bay at that location, or null if not found
     */
    public Bay getBay(Location location) {
        return bays.get(location);
    }

    /**
     * Gets a bay at the specified coordinates.
     *
     * @param aisle the aisle number
     * @param bay the bay number
     * @return the bay at that location, or null if not found
     */
    public Bay getBay(int aisle, int bay) {
        Location location = new Location(warehouseId, aisle, bay);
        return bays.get(location);
    }

    // ==================== ESSENTIAL FOR USEI01 ====================

    /**
     * Finds the best available bay for storing a box with the specified SKU.
     *
     * Strategy (USEI01 requirement):
     * 1. Prefer bay that already contains the same SKU and has space
     * 2. If not found, use first available bay (lowest aisle, then bay number)
     *
     * @param sku the SKU to store
     * @return a bay with available space, or null if all bays are full
     */
    public Bay findBestAvailableBay(String sku) {
        // Strategy 1: Bay that already has this SKU and has space
        Optional<Bay> bayWithSku = bays.values().stream()
                .filter(bay -> bay.containsSku(sku) && bay.hasAvailableSpace())
                .min(Comparator.comparing(Bay::getAisleNumber)
                        .thenComparing(Bay::getBayNumber));

        if (bayWithSku.isPresent()) {
            return bayWithSku.get();
        }

        // Strategy 2: Any available bay (first by location)
        return bays.values().stream()
                .filter(Bay::hasAvailableSpace)
                .min(Comparator.comparing(Bay::getAisleNumber)
                        .thenComparing(Bay::getBayNumber))
                .orElse(null);
    }

    /**
     * Finds all bays that contain boxes with the specified SKU.
     * Bays are returned sorted by aisle and bay number (ascending).
     * This ensures FEFO/FIFO order is maintained during dispatch operations.
     *
     * @param sku the SKU to search for
     * @return list of bays containing this SKU, sorted by location
     */
    public List<Bay> getBaysWithSku(String sku) {
        return bays.values().stream()
                .filter(bay -> bay.containsSku(sku))
                .sorted(Comparator.comparing(Bay::getAisleNumber)
                        .thenComparing(Bay::getBayNumber))
                .collect(Collectors.toList());
    }

    /**
     * Finds all bays with SKU, sorted by FEFO GLOBAL + ascending location.
     * CRITICAL for dispatch operations to maintain FEFO across multiple bays.
     *
     * Sorting priority:
     * 1. First box in bay (FEFO/FIFO comparison)
     * 2. Aisle ascending (tie-break)
     * 3. Bay ascending (tie-break)
     *
     * @param sku the SKU to search for
     * @return list of bays sorted by FEFO + location
     */
    public List<Bay> getBaysWithSkuSorted(String sku) {
        return bays.values().stream()
                .filter(bay -> bay.containsSku(sku))
                .sorted((bay1, bay2) -> {
                    // Get first box of SKU in each bay
                    Box box1 = bay1.peekFirstBox(sku);
                    Box box2 = bay2.peekFirstBox(sku);

                    // Safety checks
                    if (box1 == null && box2 == null) return 0;
                    if (box1 == null) return 1;
                    if (box2 == null) return -1;

                    // Primary sort: FEFO/FIFO (using Box.compareTo)
                    int fefoCompare = box1.compareTo(box2);
                    if (fefoCompare != 0) return fefoCompare;

                    // Tie-break: ascending location (aisle → bay)
                    int aisleCompare = Integer.compare(
                            bay1.getAisleNumber(),
                            bay2.getAisleNumber()
                    );
                    if (aisleCompare != 0) return aisleCompare;

                    return Integer.compare(
                            bay1.getBayNumber(),
                            bay2.getBayNumber()
                    );
                })
                .collect(Collectors.toList());
    }

    public void addBox(Box box) {
        if (box == null) {
            throw new IllegalArgumentException("Cannot add null box to warehouse");
        }

        // Find the best bay for this SKU (reuses your own logic)
        Bay targetBay = findBestAvailableBay(box.getSku());

        if (targetBay == null) {
            throw new IllegalStateException(
                    String.format("No available bay found for SKU %s in warehouse %s",
                            box.getSku(), warehouseId)
            );
        }

        // Delegate to Bay (handles FEFO/FIFO order and duplicate validation)
        targetBay.addBox(box);
    }

    /**
     * Returns the total capacity (maximum boxes) across all bays.
     *
     * @return sum of all bay capacities
     */
    public int getTotalCapacity() {
        return bays.values().stream()
                .mapToInt(Bay::getCapacityBoxes)
                .sum();
    }

    /**
     * Returns the current number of boxes stored in the warehouse.
     *
     * @return total number of boxes
     */
    public int getTotalBoxCount() {
        return bays.values().stream()
                .mapToInt(Bay::getCurrentBoxCount)
                .sum();
    }

    /**
     * Returns the total available capacity across all bays.
     *
     * @return sum of available spaces in all bays
     */
    public int getTotalAvailableCapacity() {
        return bays.values().stream()
                .mapToInt(Bay::getAvailableCapacity)
                .sum();
    }

    /**
     * Returns the percentage of occupied capacity in this warehouse.
     *
     * @return occupancy percentage (0.0 to 100.0)
     */
    public double getOccupancyPercentage() {
        int totalCapacity = getTotalCapacity();
        if (totalCapacity == 0) {
            return 0.0;
        }
        return (getTotalBoxCount() * 100.0) / totalCapacity;
    }

    /**
     * Groups bays by aisle number.
     * Useful for Round-Robin allocation strategies.
     *
     * @return map of aisle number to list of bays in that aisle
     */
    public Map<Integer, List<Bay>> getBaysByAisle() {
        return bays.values().stream()
                .collect(Collectors.groupingBy(
                        Bay::getAisleNumber,
                        Collectors.toList()
                ));
    }

    /**
     * Gets all unique aisle numbers in this warehouse, sorted.
     *
     * @return sorted list of aisle numbers
     */
    public List<Integer> getAisleNumbers() {
        return bays.values().stream()
                .map(Bay::getAisleNumber)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    /**
     * Returns a summary of the warehouse's current state.
     *
     * @return summary string
     */
    public String getSummary() {
        return String.format("Warehouse %s: %d bays, %d/%d boxes (%.1f%% full)",
                warehouseId,
                getBayCount(),
                getTotalBoxCount(),
                getTotalCapacity(),
                getOccupancyPercentage()
        );
    }

    @Override
    public String toString() {
        return "Warehouse{" +
                "warehouseId='" + warehouseId + '\'' +
                ", bayCount=" + getBayCount() +
                ", totalCapacity=" + getTotalCapacity() +
                ", currentBoxes=" + getTotalBoxCount() +
                ", occupancy=" + String.format("%.1f%%", getOccupancyPercentage()) +
                '}';
    }
}

