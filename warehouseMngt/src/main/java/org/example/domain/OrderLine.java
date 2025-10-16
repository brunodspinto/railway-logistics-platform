package org.example.domain;

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

    @Override
    public String toString() {
        return String.format("OrderLine[orderId=%s, lineNo=%d, sku=%s, requestedQty=%d]",
                orderId, lineNo, sku, requestedQty);
    }
}