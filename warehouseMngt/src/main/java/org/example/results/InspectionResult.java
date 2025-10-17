package org.example.results;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class InspectionResult {
    private final String returnId;
    private final String sku;
    private final int qty;
    private final String action;
    private final int qtyRestocked;
    private final int qtyDiscarded;
    private final String reason;
    private final LocalDateTime timestamp;
    private final LocalDate expiryDate;

    public InspectionResult(String returnId, String sku, int qty, String action,
                            int qtyRestocked, int qtyDiscarded, String reason,
                            LocalDateTime timestamp, LocalDate expiryDate) {
        this.returnId = returnId;
        this.sku = sku;
        this.qty = qty;
        this.action = action;
        this.qtyRestocked = qtyRestocked;
        this.qtyDiscarded = qtyDiscarded;
        this.reason = reason;
        this.timestamp = timestamp;
        this.expiryDate = expiryDate;
    }

    public String getReturnId() { return returnId; }
    public String getSku() { return sku; }
    public int getQty() { return qty; }
    public String getAction() { return action; }
    public int getQtyRestocked() { return qtyRestocked; }
    public int getQtyDiscarded() { return qtyDiscarded; }
    public String getReason() { return reason; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public LocalDate getExpiryDate() { return expiryDate; }

    @Override
    public String toString() {
        return String.format(
                "InspectionResult[%s | sku=%s | action=%s | qty=%d | restocked=%d | discarded=%d]",
                returnId, sku, action, qty, qtyRestocked, qtyDiscarded);
    }
}
