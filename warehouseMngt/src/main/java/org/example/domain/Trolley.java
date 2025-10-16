package org.example.domain;

import java.util.*;

public class Trolley {
    private static final double DEFAULT_CAPACITY = 100.0;

    public double capacity;
    public double usedCapacity = 0;
    public List<OrderLine> lines = new ArrayList<>();

    public Trolley() {
        this(DEFAULT_CAPACITY);
    }

    public Trolley(double capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be positive");
        }
        this.capacity = capacity;
    }

    public boolean canFit(OrderLine orderLine, Map<String, Item> itemMap) {
        return usedCapacity + orderLine.getOrderLineWeight(itemMap) <= capacity;
    }

    public void add(OrderLine orderLine, Map<String, Item> itemMap) {
        lines.add(orderLine);
        usedCapacity += orderLine.getOrderLineWeight(itemMap);
    }

    public double remainingCapacity() {
        return capacity - usedCapacity;
    }

    public int utilization() {
        return (int) ((usedCapacity / capacity) * 100);
    }

    public OrderLine addPartial(OrderLine line, Map<String, Item> itemMap) {
        if (line.getRequestedQty() <= 0) return null;
        Item item = itemMap.get(line.getSku());
        if (item == null) throw new IllegalArgumentException("Item not found for SKU: " + line.getSku());
        double unitWeight = item.getUnitWeight();
        if (unitWeight <= 0)
            throw new IllegalArgumentException("Unit weight must be positive for SKU: " + line.getSku());

        int canFitQty = (int) Math.floor(remainingCapacity() / unitWeight);
        if (canFitQty <= 0) {
            // nothing fits
            return line;
        }

        int toAdd = Math.min(canFitQty, line.getRequestedQty());
        OrderLine part = new OrderLine(line.getOrderId(), line.getLineNo(), line.getSku(), toAdd);
        double addedWeight = toAdd * unitWeight;
        lines.add(part);
        usedCapacity += addedWeight;

        int remainderQty = line.getRequestedQty() - toAdd;
        if (remainderQty <= 0) {
            return null;
        } else {
            return new OrderLine(line.getOrderId(), line.getLineNo(), line.getSku(), remainderQty);
        }
    }
}

