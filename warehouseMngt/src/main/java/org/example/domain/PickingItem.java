package org.example.domain;

public class PickingItem {
    private final String orderId;
    private final int lineNo;
    private final String sku;
    private final String boxId;
    private final int aisle;
    private final int bay;
    private final int qty;
    private final double unitWeight;

    public PickingItem(String orderId, int lineNo, String sku, String boxId, int aisle, int bay, int qty, double unitWeight) {
        this.orderId = orderId;
        this.lineNo = lineNo;
        this.sku = sku;
        this.boxId = boxId;
        this.aisle = aisle;
        this.bay = bay;
        this.qty = qty;
        this.unitWeight = unitWeight;
    }

    public double getTotalWeight() {
        return qty * unitWeight;
    }

    //getters

    public String getOrderId() { return orderId; }
    public int getLineNo() { return lineNo; }
    public String getSku() { return sku; }
    public String getBoxId() { return boxId; }
    public int getAisle() { return aisle; }
    public int getBay() { return bay; }
    public int getQty() { return qty; }

    @Override
    public String toString() {
        return String.format("PickingItem[orderId=%s, lineNo=%d, sku=%s, boxId=%s, aisle=%d, bay=%d, quantity=%d, unitWeight=%.2f]",
                orderId, lineNo, sku, boxId, aisle, bay, qty, unitWeight);
    }
}
