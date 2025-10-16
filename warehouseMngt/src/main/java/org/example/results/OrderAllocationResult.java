package org.example.results;

import org.example.domain.OrderLine;
import java.util.List;

public class OrderAllocationResult {

    public enum Status { ELIGIBLE, PARTIAL, UNDISPATCHABLE }

    private final OrderLine line;
    private final List<AllocationRow> allocations;
    private final Status status;

    public OrderAllocationResult(OrderLine line, List<AllocationRow> allocations, Status status) {
        this.line = line;
        this.allocations = allocations;
        this.status = status;
    }

    public OrderLine getLine() { return line; }
    public List<AllocationRow> getAllocations() { return allocations; }
    public Status getStatus() { return status; }

    @Override
    public String toString() {
        return String.format("%s -> %s, allocated=%d",
                line, status,
                allocations.stream().mapToInt(AllocationRow::getQty).sum());
    }
}