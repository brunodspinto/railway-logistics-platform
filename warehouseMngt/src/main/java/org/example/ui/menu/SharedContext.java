package org.example.ui.menu;

import org.example.domain.Trolley;
import org.example.repository.ItemRepository;
import org.example.repository.WarehouseRepository;
import org.example.results.OrderAllocationResult;

import java.util.List;

public class SharedContext {

    private static List<Trolley> lastPickingPlan;
    private static List<OrderAllocationResult> lastAllocations;

    private static ItemRepository itemRepository;
    private static WarehouseRepository warehouseRepository;

    // Getters e setters para picking plan
    public static List<Trolley> getLastPickingPlan() {
        return lastPickingPlan;
    }

    public static void setLastPickingPlan(List<Trolley> pickingPlan) {
        lastPickingPlan = pickingPlan;
    }

    // Getters e setters para allocations
    public static List<OrderAllocationResult> getLastAllocations() {
        return lastAllocations;
    }

    public static void setLastAllocations(List<OrderAllocationResult> allocations) {
        lastAllocations = allocations;
    }

    // Getters e setters para os repositórios
    public static ItemRepository getItemRepository() {
        return itemRepository;
    }

    public static void setItemRepository(ItemRepository repo) {
        itemRepository = repo;
    }

    public static WarehouseRepository getWarehouseRepository() {
        return warehouseRepository;
    }

    public static void setWarehouseRepository(WarehouseRepository repo) {
        warehouseRepository = repo;
    }
}

