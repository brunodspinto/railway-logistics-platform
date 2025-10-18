package org.example.domain;

import java.util.*;

public class Trolley {
    private static double DEFAULT_CAPACITY = 500.0;

    private final int id;
    private double usedWeight;
    private final List<PickingItem> items = new ArrayList<>();

    public Trolley(int id) {
        this.id = id;
        this.usedWeight = 0.0;
    }

    public boolean canFit(PickingItem item) {
        return (usedWeight + item.getTotalWeight()) <= DEFAULT_CAPACITY;
    }

    public void addItem(PickingItem item) {
        if (!canFit(item)) {
            throw new IllegalArgumentException("Item cannot fit in trolley");
        }
        items.add(item);
        usedWeight += item.getTotalWeight();
    }

    //getters

    public double getUsedWeight() {return usedWeight; }
    public double getUtilization() { return (usedWeight / DEFAULT_CAPACITY) * 100.0; }
    public List<PickingItem> getItems() {return items; }
    public int getId() { return id; }

    //Métodos estáticos para configurar a capacidade global
    public static double getCapacity() {
        return DEFAULT_CAPACITY;
    }

    public static void setCapacity(double capacity) {
        if (capacity <= 0)
            throw new IllegalArgumentException("Trolley's capacity must be positive.");
        DEFAULT_CAPACITY = capacity;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Trolley #%d -> Utilization: %.2f%% (%.2f/%.2f kg)\n",
                id, getUtilization(), usedWeight, DEFAULT_CAPACITY));
        for (PickingItem item : items) {
            sb.append("  ").append(item).append("\n");
        }
        return sb.toString();
    }
}

