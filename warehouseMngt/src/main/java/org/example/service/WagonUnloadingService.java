package org.example.service;

import org.example.domain.*;
import org.example.results.UnloadingResult;
import org.example.exception.CapacityExceededException;
import org.example.repository.WarehouseRepository;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for unloading wagons into warehouse.
 *
 * ALLOCATION STRATEGY: Round-Robin (USEI01)
 * Distributes boxes across multiple aisles to support parallel picking operations.
 * This strategy balances load between aisles, enabling multiple pickers to work
 * simultaneously in different areas (critical for USEI04 pick path optimization).
 *
 * @author [Your Name]
 * @version 2.0 (Round-Robin)
 */
public class WagonUnloadingService {

    private final WarehouseRepository warehouseRepository;

    // ✅ Round-Robin state: tracks which aisle to use next
    private int currentAisleIndex = 0;

    public WagonUnloadingService(WarehouseRepository warehouseRepository) {
        this.warehouseRepository = warehouseRepository;
    }

    /**
     * Unloads wagons into warehouse following GLOBAL FEFO/FIFO order
     * with Round-Robin distribution across aisles.
     *
     * Algorithm:
     * 1. Collect all boxes from all wagons
     * 2. Sort globally by FEFO/FIFO (ensures correct dispatch order)
     * 3. Distribute using Round-Robin across aisles (balances load)
     *
     * @param wagons list of wagons to unload
     * @return result containing success/error counts per wagon
     */
    public UnloadingResult unloadWagons(List<Wagon> wagons) {
        UnloadingResult result = new UnloadingResult();
        Warehouse warehouse = warehouseRepository.findDefault();

        if (warehouse == null) {
            result.addError("SYSTEM", "No warehouse available");
            return result;
        }

        // ✅ STEP 1: Collect ALL boxes from ALL wagons
        List<Box> allBoxes = new ArrayList<>();
        for (Wagon wagon : wagons) {
            allBoxes.addAll(wagon.getBoxes());
        }

        System.out.println("📦 Total boxes to unload: " + allBoxes.size());

        // ✅ STEP 2: Sort GLOBALLY by FEFO/FIFO (Box implements Comparable)
        allBoxes.sort(Box::compareTo);
        System.out.println("✅ Boxes sorted by FEFO/FIFO order");

        // ✅ STEP 3: Reset Round-Robin counter for this unload operation
        currentAisleIndex = 0;

        // ✅ STEP 4: Distribute to bays using Round-Robin
        Map<String, Integer> successfulBoxesPerWagon = new HashMap<>();
        Map<String, String> errorPerWagon = new HashMap<>();

        for (Box box : allBoxes) {
            try {
                // ✅ Select bay using Round-Robin strategy
                Bay targetBay = selectBayRoundRobin(warehouse, box);

                if (targetBay == null) {
                    throw new CapacityExceededException(
                            "No available bays for box " + box.getBoxId(),
                            0
                    );
                }

                // Add box to bay (Bay.addBox will insert in correct FEFO/FIFO position)
                targetBay.addBox(box);

                // Track success
                successfulBoxesPerWagon.merge(box.getWagonId(), 1, Integer::sum);

            } catch (Exception e) {
                // Track error (only keep first error per wagon)
                errorPerWagon.putIfAbsent(box.getWagonId(), e.getMessage());
            }
        }

        // ✅ STEP 5: Build result
        for (Map.Entry<String, Integer> entry : successfulBoxesPerWagon.entrySet()) {
            String wagonId = entry.getKey();
            int boxCount = entry.getValue();

            // Only mark as success if NO errors for this wagon
            if (!errorPerWagon.containsKey(wagonId)) {
                result.addSuccess(wagonId, boxCount);
            }
        }

        // Add errors
        for (Map.Entry<String, String> entry : errorPerWagon.entrySet()) {
            result.addError(entry.getKey(), entry.getValue());
        }

        warehouseRepository.save(warehouse);

        // ✅ Print distribution summary
        printDistributionSummary(warehouse);

        return result;
    }

    /**
     * Round-Robin bay selection strategy.
     *
     * Algorithm:
     * 1. Try to co-locate boxes with same SKU (if bay has space)
     * 2. Otherwise, rotate between aisles using Round-Robin
     * 3. Within each aisle, select first available bay
     *
     * This ensures:
     * - SKU consolidation (easier picking)
     * - Load balancing across aisles (parallel operations)
     * - Deterministic allocation (same input = same output)
     *
     * @param warehouse the warehouse to allocate in
     * @param box the box to allocate
     * @return selected bay, or null if no space available
     */
    private Bay selectBayRoundRobin(Warehouse warehouse, Box box) {
        // ✅ PHASE 1: Try to co-locate with same SKU (SKU grouping)
        // This improves picking efficiency (USEI04)
        List<Bay> baysWithSku = warehouse.getBaysWithSku(box.getSku()).stream()
                .filter(Bay::hasAvailableSpace)
                .toList();

        if (!baysWithSku.isEmpty()) {
            // Prefer bay with most available space (reduces fragmentation)
            return baysWithSku.stream()
                    .max(Comparator.comparingInt(Bay::getAvailableCapacity))
                    .orElse(null);
        }

        // ✅ PHASE 2: Round-Robin across aisles
        // Group bays by aisle number
        Map<Integer, List<Bay>> baysByAisle = warehouse.getAllBays().stream()
                .filter(Bay::hasAvailableSpace)
                .collect(Collectors.groupingBy(Bay::getAisleNumber));

        if (baysByAisle.isEmpty()) {
            return null; // No space available anywhere
        }

        // Get sorted list of aisle numbers
        List<Integer> aisles = new ArrayList<>(baysByAisle.keySet());
        Collections.sort(aisles);

        // ✅ Select aisle using Round-Robin
        int targetAisle = aisles.get(currentAisleIndex % aisles.size());
        currentAisleIndex++; // Increment for next box

        // ✅ Within selected aisle, get first available bay (by bay number)
        return baysByAisle.get(targetAisle).stream()
                .sorted(Comparator.comparingInt(Bay::getBayNumber))
                .filter(Bay::hasAvailableSpace)
                .findFirst()
                .orElse(null);
    }

    /**
     * Prints a summary of how boxes were distributed across aisles.
     * Useful for debugging and verifying Round-Robin behavior.
     */
    private void printDistributionSummary(Warehouse warehouse) {
        Map<Integer, Integer> boxesPerAisle = new HashMap<>();
        Map<Integer, Integer> baysPerAisle = new HashMap<>();

        for (Bay bay : warehouse.getAllBays()) {
            int aisle = bay.getAisleNumber();
            int boxCount = bay.getCurrentBoxCount();

            if (boxCount > 0) {
                boxesPerAisle.merge(aisle, boxCount, Integer::sum);
                baysPerAisle.merge(aisle, 1, Integer::sum);
            }
        }

        System.out.println("\n📊 Distribution Summary (Round-Robin):");
        System.out.println("─".repeat(50));

        List<Integer> sortedAisles = new ArrayList<>(boxesPerAisle.keySet());
        Collections.sort(sortedAisles);

        for (int aisle : sortedAisles) {
            int boxes = boxesPerAisle.get(aisle);
            int bays = baysPerAisle.get(aisle);
            System.out.printf("  Aisle %d: %d boxes in %d bay(s)%n", aisle, boxes, bays);
        }

        System.out.println("─".repeat(50));
    }

    /**
     * Resets the Round-Robin counter.
     * Useful for testing to ensure deterministic behavior.
     */
    public void resetRoundRobin() {
        currentAisleIndex = 0;
    }
}