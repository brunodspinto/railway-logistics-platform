package org.example.results;

import java.util.ArrayList;
import java.util.List;

/**
 * Result of a dispatch operation (USEI01 Acceptance Criteria #2).
 */
public class DispatchResult {
    private final String sku;
    private final int requestedQty;
    private int dispatchedQty = 0;
    private final List<DispatchedBox> dispatchedBoxes = new ArrayList<>();

    public DispatchResult(String sku, int requestedQty) {
        this.sku = sku;
        this.requestedQty = requestedQty;
    }

    public void addDispatchedBox(String boxId, int qty) {
        dispatchedBoxes.add(new DispatchedBox(boxId, qty));
        dispatchedQty += qty;
    }

    // Getters
    public String getSku() { return sku; }
    public int getRequestedQty() { return requestedQty; }
    public int getDispatchedQty() { return dispatchedQty; }

    /**
     * Returns the total quantity dispatched.
     * Alias for getDispatchedQty() to match test expectations.
     */
    public int getTotalDispatched() {
        return getDispatchedQty();
    }

    public int getRemainingQty() { return requestedQty - dispatchedQty; }
    public List<DispatchedBox> getDispatchedBoxes() { return dispatchedBoxes; }

    // ✅ Status checkers
    public boolean isFullyDispatched() {
        return dispatchedQty >= requestedQty;
    }

    public boolean isPartiallyDispatched() {
        return dispatchedQty > 0 && dispatchedQty < requestedQty;
    }

    public boolean hasNoStock() {
        return dispatchedQty == 0;
    }

    public double getFulfillmentRate() {
        return requestedQty > 0 ? (double) dispatchedQty / requestedQty : 0.0;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("╔═══════════════════════════════════════════════╗\n"));
        sb.append(String.format("║ Dispatch Result - SKU: %-21s ║\n", sku));
        sb.append(String.format("╠═══════════════════════════════════════════════╣\n"));
        sb.append(String.format("║ Requested  : %5d units                      ║\n", requestedQty));
        sb.append(String.format("║ Dispatched : %5d units (%.1f%%)              ║\n",
                dispatchedQty, getFulfillmentRate() * 100));
        sb.append(String.format("║ Remaining  : %5d units                      ║\n", getRemainingQty()));
        sb.append(String.format("╠═══════════════════════════════════════════════╣\n"));

        if (!dispatchedBoxes.isEmpty()) {
            sb.append(String.format("║ Dispatched Boxes: %-27s ║\n",
                    dispatchedBoxes.size() + " box(es)"));
            for (DispatchedBox box : dispatchedBoxes) {
                sb.append(String.format("║   - %-41s ║\n", box.toString()));
            }
        } else {
            sb.append(String.format("║ ⚠️  NO STOCK AVAILABLE                       ║\n"));
        }

        sb.append(String.format("╚═══════════════════════════════════════════════╝"));
        return sb.toString();
    }

    // ✅ Inner class for type safety
    public static class DispatchedBox {
        private final String boxId;
        private final int quantity;

        public DispatchedBox(String boxId, int quantity) {
            this.boxId = boxId;
            this.quantity = quantity;
        }

        public String getBoxId() { return boxId; }
        public int getQuantity() { return quantity; }

        @Override
        public String toString() {
            return String.format("%s (%d units)", boxId, quantity);
        }
    }
}
