package org.example.usei02.results;

import org.example.domain.OrderLine;
import org.example.results.AllocationRow;
import org.example.results.OrderAllocationResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class OrderAllocationResultTest {

    @Test
    @DisplayName("Estar ELIGIBLE quando quantidade é totalmente alocada")
    void testEligibleStatus() {
        OrderLine line = new OrderLine("O1", 1, "SKU1", 10);
        AllocationRow row = new AllocationRow("O1", 1, "SKU1", 10, "BX1", 1, 1);
        OrderAllocationResult result = new OrderAllocationResult(line, List.of(row), OrderAllocationResult.Status.ELIGIBLE);

        assertEquals(OrderAllocationResult.Status.ELIGIBLE, result.getStatus());
        assertEquals(1, result.getAllocations().size());
    }

    @Test
    @DisplayName("Estar PARTIAL quando só parte é alocada")
    void testPartialStatus() {
        OrderLine line = new OrderLine("O2", 1, "SKU2", 10);
        AllocationRow row = new AllocationRow("O2", 1, "SKU2", 5, "BX2", 2, 2);
        OrderAllocationResult result = new OrderAllocationResult(line, List.of(row), OrderAllocationResult.Status.PARTIAL);

        assertEquals(OrderAllocationResult.Status.PARTIAL, result.getStatus());
        assertEquals(5, result.getAllocations().get(0).getQty());
    }

    @Test
    @DisplayName("Estar UNDISPATCHABLE quando nada é alocado")
    void testUndispatchableStatus() {
        OrderLine line = new OrderLine("O3", 1, "SKU3", 10);
        OrderAllocationResult result = new OrderAllocationResult(line, List.of(), OrderAllocationResult.Status.UNDISPATCHABLE);

        assertEquals(OrderAllocationResult.Status.UNDISPATCHABLE, result.getStatus());
        assertTrue(result.getAllocations().isEmpty());
    }
}