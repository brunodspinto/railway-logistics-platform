package org.example.repository;


import org.example.domain.Bay;
import org.example.domain.Box;
import org.example.domain.Warehouse;
import org.example.exception.ValidationException;

import java.util.HashMap;
import java.util.List;
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
            return null;  // ✅ Retorna null
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

    // Adicionar no final da classe (antes do último })

// ==================== STATISTICS METHODS ====================

    public Map<String, Integer> getTotalUnitsBySku() {
        Warehouse warehouse = findDefault();
        Map<String, Integer> totals = new HashMap<>();

        for (Bay bay : warehouse.getAllBays()) {
            for (Box box : bay.getBoxes()) {
                totals.merge(box.getSku(), box.getQuantity(), Integer::sum);
            }
        }
        return totals;
    }

    public Map<String, Integer> getExpiredUnitsBySku() {
        Warehouse warehouse = findDefault();
        Map<String, Integer> expiredTotals = new HashMap<>();

        for (Bay bay : warehouse.getAllBays()) {
            for (Box box : bay.getBoxes()) {
                if (box.isExpired()) {
                    expiredTotals.merge(box.getSku(), box.getQuantity(), Integer::sum);
                }
            }
        }
        return expiredTotals;
    }

    public int getTotalUnits() {
        Map<String, Integer> totals = getTotalUnitsBySku();
        return totals.values().stream().mapToInt(Integer::intValue).sum();
    }

    public double getExpiredPercentage() {
        int total = getTotalUnits();
        if (total == 0) return 0.0;

        Map<String, Integer> expired = getExpiredUnitsBySku();
        int expiredTotal = expired.values().stream().mapToInt(Integer::intValue).sum();
        return (expiredTotal * 100.0) / total;
    }

    public List<Map.Entry<String, Integer>> getSkusByTotalUnits() {
        Map<String, Integer> totals = getTotalUnitsBySku();
        return totals.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .collect(java.util.stream.Collectors.toList());
    }
}
