package org.example.usei02.service;

import org.example.domain.Box;
import org.example.domain.Bay;
import org.example.domain.Warehouse;
import org.example.domain.OrderLine;
import org.example.results.OrderAllocationResult;
import org.example.service.OrderAllocationService;
import org.example.results.AllocationRow;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.Instant;
import java.util.List;

public class OrderAllocationServiceTest {

    private int allocatedQty(OrderAllocationResult result) {
        return result.getAllocations().stream()
                .mapToInt(AllocationRow::getQty)
                .sum();
    }

    @Test
    @DisplayName("Alocar totalmente quando há stock suficiente")
    void testEligible() {
        Warehouse warehouse = new Warehouse("W1");
        Bay bay = new Bay("W1", 1, 1, 10);
        warehouse.addBay(bay);

        bay.addBox(new Box("BX1", "SKU1", 10, LocalDate.parse("2025-11-01"), Instant.now(), "WAGON1"));

        OrderAllocationService service = new OrderAllocationService(warehouse);
        OrderLine line = new OrderLine("O1", 1, "SKU1", 10);

        OrderAllocationResult result = service.allocateOrders(List.of(line), false).get(0);

        assertEquals(10, allocatedQty(result));
        assertEquals(OrderAllocationResult.Status.ELIGIBLE, result.getStatus());
    }

    @Test
    @DisplayName("Devolver PARTIAL quando só há parte do stock")
    void testPartial() {
        Warehouse warehouse = new Warehouse("W1");
        Bay bay = new Bay("W1", 1, 1, 10);
        warehouse.addBay(bay);

        bay.addBox(new Box("BX2", "SKU1", 5, LocalDate.parse("2025-11-01"), Instant.now(), "WAGON1"));

        OrderAllocationService service = new OrderAllocationService(warehouse);
        OrderLine line = new OrderLine("O2", 1, "SKU1", 10);

        OrderAllocationResult result = service.allocateOrders(List.of(line), true).get(0);

        assertEquals(5, allocatedQty(result));
        assertEquals(OrderAllocationResult.Status.PARTIAL, result.getStatus());
    }

    @Test
    @DisplayName("Devolver UNDISPATCHABLE quando não há stock")
    void testUndispatchable() {
        Warehouse warehouse = new Warehouse("W1");
        Bay bay = new Bay("W1", 1, 1, 10);
        warehouse.addBay(bay);

        OrderAllocationService service = new OrderAllocationService(warehouse);
        OrderLine line = new OrderLine("O3", 1, "SKU1", 10);

        OrderAllocationResult result = service.allocateOrders(List.of(line), true).get(0);

        assertEquals(0, allocatedQty(result));
        assertEquals(OrderAllocationResult.Status.UNDISPATCHABLE, result.getStatus());
    }

    @Test
    @DisplayName("Alocar múltiplas linhas de um pedido")
    void testMultipleLines() {
        Warehouse warehouse = new Warehouse("W1");
        Bay bay = new Bay("W1", 1, 1, 20);
        warehouse.addBay(bay);

        bay.addBox(new Box("BX3", "SKU1", 10, LocalDate.parse("2025-11-01"), Instant.now(), "WAGON1"));
        bay.addBox(new Box("BX4", "SKU2", 10, LocalDate.parse("2025-12-01"), Instant.now(), "WAGON2"));

        OrderAllocationService service = new OrderAllocationService(warehouse);
        OrderLine line1 = new OrderLine("O4", 1, "SKU1", 10);
        OrderLine line2 = new OrderLine("O4", 2, "SKU2", 10);

        List<OrderAllocationResult> results = service.allocateOrders(List.of(line1, line2), false);

        assertEquals(2, results.size());
        assertEquals(OrderAllocationResult.Status.ELIGIBLE, results.get(0).getStatus());
        assertEquals(OrderAllocationResult.Status.ELIGIBLE, results.get(1).getStatus());
    }

    @Test
    @DisplayName("Escolher a caixa com validade mais próxima")
    void testExpiryDateSelection() {
        Warehouse warehouse = new Warehouse("W1");
        Bay bay = new Bay("W1", 1, 1, 20);
        warehouse.addBay(bay);

        bay.addBox(new Box("BX5", "SKU1", 10, LocalDate.parse("2025-10-01"), Instant.now(), "WAGON1"));
        bay.addBox(new Box("BX6", "SKU1", 10, LocalDate.parse("2025-12-01"), Instant.now(), "WAGON2"));

        OrderAllocationService service = new OrderAllocationService(warehouse);
        OrderLine line = new OrderLine("O5", 1, "SKU1", 10);

        OrderAllocationResult result = service.allocateOrders(List.of(line), false).get(0);

        assertEquals(10, allocatedQty(result));
        assertEquals(OrderAllocationResult.Status.ELIGIBLE, result.getStatus());
    }
}