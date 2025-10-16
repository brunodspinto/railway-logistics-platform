package org.example.domain;

import java.util.Map;

public class OrderLine {
    private final String orderId;
    private final int lineNo;
    private final String sku;
    private final int requestedQty;


    public OrderLine(String orderId, int lineNo, String sku, int requestedQty) {
        this.orderId = orderId;
        this.lineNo = lineNo;
        this.sku = sku;
        this.requestedQty = requestedQty;
    }

    public String getOrderId() { return orderId; }
    public int getLineNo() { return lineNo; }
    public String getSku() { return sku; }
    public int getRequestedQty() { return requestedQty; }

    // Compute weight when you have the Item instance
    public double getOrderLineWeight(Item item) {
        if (item == null) throw new IllegalArgumentException("Item cannot be null");
        return requestedQty * item.getUnitWeight();
    }

    // Convenience: compute weight from a map of SKU -> Item
    public double getOrderLineWeight(Map<String, Item> itemMap) {
        if (itemMap == null) throw new IllegalArgumentException("itemMap cannot be null");
        Item item = itemMap.get(sku);
        if (item == null) {
            throw new IllegalArgumentException("Item not found for SKU: " + sku);
        }
        return getOrderLineWeight(item);
    }

    @Override
    public String toString() {
        return String.format("OrderLine[orderId=%s, lineNo=%d, sku=%s, requestedQty=%d]",
                orderId, lineNo, sku, requestedQty);
    }
}