package org.example.repository;


import org.example.domain.Warehouse;
import org.example.exception.ValidationException;

import java.util.HashMap;
import java.util.Map;

public class WarehouseRepository {
    private final Map<String, Warehouse> warehouses = new HashMap<>();
    private String defaultWarehouseId;

    public void save(Warehouse warehouse) {
        if (warehouse == null) {
            throw new ValidationException("Cannot save null warehouse");
        }

        String warehouseId = warehouse.getWarehouseId();
        warehouses.put(warehouseId, warehouse);

        // Set as default if it's the first one
        if (defaultWarehouseId == null) {
            defaultWarehouseId = warehouseId;
        }
    }

    public Warehouse findById(String warehouseId) {
        return warehouses.get(warehouseId);
    }

    public Warehouse findDefault() {
        if (defaultWarehouseId == null) {
            throw new ValidationException("No warehouse available");
        }
        return warehouses.get(defaultWarehouseId);
    }

    public void setDefaultWarehouse(String warehouseId) {
        if (!warehouses.containsKey(warehouseId)) {
            throw new ValidationException("Warehouse not found: " + warehouseId);
        }
        defaultWarehouseId = warehouseId;
    }

    public boolean exists(String warehouseId) {
        return warehouses.containsKey(warehouseId);
    }

    public int count() {
        return warehouses.size();
    }

    public void clear() {
        warehouses.clear();
        defaultWarehouseId = null;
    }
}
