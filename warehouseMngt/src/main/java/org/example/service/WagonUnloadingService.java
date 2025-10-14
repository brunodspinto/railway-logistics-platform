package org.example.service;

import org.example.domain.*;
import org.example.results.UnloadingResult;
import org.example.exception.CapacityExceededException;
import org.example.repository.WarehouseRepository;

import java.util.List;

public class WagonUnloadingService {

    private final WarehouseRepository warehouseRepository;

    public WagonUnloadingService(WarehouseRepository warehouseRepository) {
        this.warehouseRepository = warehouseRepository;
    }

    public UnloadingResult unloadWagons(List<Wagon> wagons) {
        UnloadingResult result = new UnloadingResult();
        Warehouse warehouse = warehouseRepository.findDefault();

        if (warehouse == null) {
            result.addError("SYSTEM", "No warehouse available");
            return result;
        }

        for (Wagon wagon : wagons) {
            try {
                int boxesUnloaded = unloadSingleWagon(wagon, warehouse);
                result.addSuccess(wagon.getWagonId(), boxesUnloaded);
            } catch (Exception e) {
                result.addError(wagon.getWagonId(), e.getMessage());
            }
        }

        warehouseRepository.save(warehouse);
        return result;
    }

    private int unloadSingleWagon(Wagon wagon, Warehouse warehouse) {
        int boxesUnloaded = 0;

        for (Box box : wagon.getBoxes()) {
            Bay availableBay = warehouse.findBestAvailableBay(box.getSku());

            if (availableBay == null) {
                throw new CapacityExceededException(
                        "No available bays for box " + box.getBoxId(),
                        0
                );
            }

            availableBay.addBox(box);
            boxesUnloaded++;
        }

        return boxesUnloaded;
    }
}
