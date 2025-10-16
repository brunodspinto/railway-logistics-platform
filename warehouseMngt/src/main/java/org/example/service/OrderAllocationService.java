package org.example.service;

import org.example.domain.Bay;
import org.example.domain.Box;
import org.example.domain.OrderLine;
import org.example.domain.Warehouse;
import org.example.results.AllocationRow;
import org.example.results.OrderAllocationResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OrderAllocationService {

    private final Warehouse warehouse;

    public OrderAllocationService(Warehouse warehouse) {
        this.warehouse = warehouse;
    }

    public List<OrderAllocationResult> allocateOrders(List<OrderLine> orderLines, boolean partialMode) {
        List<OrderAllocationResult> results = new ArrayList<>();

        for (OrderLine line : orderLines) {
            int remaining = line.getRequestedQty();
            List<AllocationRow> allocations = new ArrayList<>();

            // Recolhe TODAS as boxes do SKU em todas as bays e ordena FEFO/FIFO (Box implements Comparable)
            List<Box> boxes = new ArrayList<>();
            for (Bay bay : warehouse.getAllBays()) {
                for (Box b : bay.getBoxes()) {
                    if (b.getSku().equals(line.getSku())) {
                        boxes.add(b);
                    }
                }
            }
            Collections.sort(boxes);

            for (Box box : boxes) {
                if (remaining <= 0) break;

                int available = box.getQuantity(); // planeamento: não altera stock real
                int allocate = Math.min(remaining, available);
                if (allocate > 0) {
                    allocations.add(new AllocationRow(
                            line.getOrderId(), line.getLineNo(), line.getSku(),
                            allocate,
                            box.getBoxId(),
                            box.getLocation().getAisle(),
                            box.getLocation().getBay()
                    ));
                    remaining -= allocate;
                }
            }

            int totalAllocated = 0;
            for (AllocationRow a : allocations) totalAllocated += a.getQty();

            OrderAllocationResult.Status status;
            if (!partialMode) {
                if (totalAllocated == line.getRequestedQty()) {
                    status = OrderAllocationResult.Status.ELIGIBLE;
                } else {
                    status = OrderAllocationResult.Status.UNDISPATCHABLE;
                    allocations.clear(); // descarta em modo estrito
                }
            } else {
                if (totalAllocated == line.getRequestedQty()) {
                    status = OrderAllocationResult.Status.ELIGIBLE;
                } else if (totalAllocated > 0) {
                    status = OrderAllocationResult.Status.PARTIAL;
                } else {
                    status = OrderAllocationResult.Status.UNDISPATCHABLE;
                }
            }

            results.add(new OrderAllocationResult(line, allocations, status));
        }

        return results;
    }
}