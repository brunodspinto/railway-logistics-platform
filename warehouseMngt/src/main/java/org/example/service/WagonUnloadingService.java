package org.example.service;

import org.example.domain.*;
import org.example.results.UnloadingResult;
import org.example.exception.CapacityExceededException;
import org.example.repository.WarehouseRepository;

import java.util.*;

public class WagonUnloadingService {

    private final WarehouseRepository warehouseRepository;

    public WagonUnloadingService(WarehouseRepository warehouseRepository) {
        this.warehouseRepository = warehouseRepository;
    }

    /**
     * Unloads wagons into warehouse following GLOBAL FEFO/FIFO order.
     * All boxes from all wagons are sorted BEFORE distribution to ensure
     * correct dispatch order across multiple bays.
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

        // ✅ STEP 2: Sort GLOBALLY by FEFO/FIFO (Box implements Comparable)
        allBoxes.sort(Box::compareTo);

        // ✅ STEP 3: Distribute to bays in sorted order
        Map<String, Integer> successfulBoxesPerWagon = new HashMap<>();
        Map<String, String> errorPerWagon = new HashMap<>();

        for (Box box : allBoxes) {
            try {
                // Find best bay for this SKU
                Bay availableBay = warehouse.findBestAvailableBay(box.getSku());

                if (availableBay == null) {
                    throw new CapacityExceededException(
                            "No available bays for box " + box.getBoxId(),
                            0
                    );
                }

                // Add box to bay (Bay.addBox will insert in correct position)
                availableBay.addBox(box);

                // Track success
                successfulBoxesPerWagon.merge(box.getWagonId(), 1, Integer::sum);

            } catch (Exception e) {
                // Track error (only keep first error per wagon)
                errorPerWagon.putIfAbsent(box.getWagonId(), e.getMessage());
            }
        }

        // ✅ STEP 4: Build result
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
        return result;
    }
}

