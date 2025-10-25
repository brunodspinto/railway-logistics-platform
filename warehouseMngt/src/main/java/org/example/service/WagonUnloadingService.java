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

        // ✅ Track which wagon each box belongs to (for result tracking)
        Map<String, String> boxToWagon = new HashMap<>();  // boxId -> wagonId

        for (Wagon wagon : wagons) {
            for (Box box : wagon.getBoxes()) {
                allBoxes.add(box);
                boxToWagon.put(box.getBoxId(), wagon.getWagonId());
            }
        }

        System.out.println("📦 Total boxes to unload: " + allBoxes.size());

        // ✅ STEP 2: Sort GLOBALLY by FEFO/FIFO
        allBoxes.sort(Box::compareTo);
        System.out.println("✅ Boxes sorted by FEFO/FIFO order");

        // ✅ STEP 3: Reset Round-Robin counter
        currentAisleIndex = 0;

        // ✅ STEP 4: Distribute to bays using Round-Robin
        Map<String, Integer> successfulBoxesPerWagon = new HashMap<>();
        Map<String, Integer> totalBoxesPerWagon = new HashMap<>();  // Track expected count
        Map<String, String> errorPerWagon = new HashMap<>();

        // Initialize counters for each wagon
        for (Wagon wagon : wagons) {
            totalBoxesPerWagon.put(wagon.getWagonId(), wagon.getBoxes().size());
        }

        for (Box box : allBoxes) {
            try {
                Bay targetBay = selectBayRoundRobin(warehouse, box);

                if (targetBay == null) {
                    throw new CapacityExceededException(
                            "No available bays for box " + box.getBoxId(), 0
                    );
                }

                targetBay.addBox(box);

                // Track success by wagon
                String wagonId = box.getWagonId();
                successfulBoxesPerWagon.merge(wagonId, 1, Integer::sum);

            } catch (Exception e) {
                String wagonId = box.getWagonId();
                errorPerWagon.putIfAbsent(wagonId, e.getMessage());
            }
        }

        // ✅ STEP 5: Build result
        System.out.println("\n🔍 DEBUG: Building result...");
        System.out.println("  totalBoxesPerWagon: " + totalBoxesPerWagon);
        System.out.println("  successfulBoxesPerWagon: " + successfulBoxesPerWagon);
        System.out.println("  errorPerWagon: " + errorPerWagon);

        for (String wagonId : totalBoxesPerWagon.keySet()) {
            int expectedBoxes = totalBoxesPerWagon.get(wagonId);
            int successfulBoxes = successfulBoxesPerWagon.getOrDefault(wagonId, 0);

            System.out.println(String.format("\n  Wagon %s: %d/%d boxes placed",
                    wagonId, successfulBoxes, expectedBoxes));

            if (successfulBoxes == expectedBoxes && !errorPerWagon.containsKey(wagonId)) {
                System.out.println("    → SUCCESS");
                result.addSuccess(wagonId, successfulBoxes);
            } else {
                String error = errorPerWagon.getOrDefault(wagonId,
                        String.format("Partial unload: %d/%d boxes placed",
                                successfulBoxes, expectedBoxes));
                System.out.println("    → ERROR: " + error);
                result.addError(wagonId, error);
            }
        }

        warehouseRepository.save(warehouse);
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