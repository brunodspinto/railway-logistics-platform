package org.example.usei02.order;

import org.example.domain.Box;
import org.example.domain.Bay;
import org.example.domain.Warehouse;
import org.example.domain.OrderLine;
import org.example.results.OrderAllocationResult;
import org.example.results.AllocationRow;
import org.example.usei04.service.OrderAllocationService;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.Instant;
import java.util.List;

public class OrderTest {

    private int allocatedQty(OrderAllocationResult result) {
        return result.getAllocations().stream()
                .mapToInt(AllocationRow::getQty)
                .sum();
    }

    @Test
    void testEligible() {
        Warehouse warehouse = new Warehouse("W1");
        Bay bay = new Bay("W1", 1, 1, 10);
        warehouse.addBay(bay);

        // Stock suficiente: 10 unidades do SKU1
        bay.addBox(new Box("BX1", "SKU1", 10, LocalDate.parse("2025-11-01"), Instant.now(), "WAGON1"));

        OrderAllocationService service = new OrderAllocationService(warehouse);

        OrderLine line = new OrderLine("O1", 1, "SKU1", 10);
        List<OrderLine> lines = List.of(line);

        List<OrderAllocationResult> results = service.allocateOrders(lines, false);
        OrderAllocationResult result = results.get(0);

        assertEquals(10, allocatedQty(result));
        assertEquals(OrderAllocationResult.Status.ELIGIBLE, result.getStatus());
    }

    @Test
    void testUndispatchable_Strict() {
        Warehouse warehouse = new Warehouse("W1");
        Bay bay = new Bay("W1", 1, 1, 10);
        warehouse.addBay(bay);

        // Só há 5 unidades, pedido pede 10 (modo estrito -> UNDISPATCHABLE, sem alocações)
        bay.addBox(new Box("BX2", "SKU1", 5, LocalDate.parse("2025-11-01"), Instant.now(), "WAGON1"));

        OrderAllocationService service = new OrderAllocationService(warehouse);

        OrderLine line = new OrderLine("O2", 1, "SKU1", 10);
        List<OrderLine> lines = List.of(line);

        List<OrderAllocationResult> results = service.allocateOrders(lines, false);
        OrderAllocationResult result = results.get(0);

        assertEquals(0, allocatedQty(result));
        assertEquals(OrderAllocationResult.Status.UNDISPATCHABLE, result.getStatus());
    }

    @Test
    void testPartial_PartialMode() {
        Warehouse warehouse = new Warehouse("W1");
        Bay bay = new Bay("W1", 1, 1, 10);
        warehouse.addBay(bay);

        // Só há 5 unidades, pedido pede 10 (modo parcial -> PARTIAL, 5 alocadas)
        bay.addBox(new Box("BX3", "SKU1", 5, LocalDate.parse("2025-11-01"), Instant.now(), "WAGON1"));

        OrderAllocationService service = new OrderAllocationService(warehouse);

        OrderLine line = new OrderLine("O3", 1, "SKU1", 10);
        List<OrderLine> lines = List.of(line);

        List<OrderAllocationResult> results = service.allocateOrders(lines, true);
        OrderAllocationResult result = results.get(0);

        assertEquals(5, allocatedQty(result));
        assertEquals(OrderAllocationResult.Status.PARTIAL, result.getStatus());
    }

    @Test
    void testDifferentSku_NoMisallocation() {
        Warehouse warehouse = new Warehouse("W1");
        Bay bay = new Bay("W1", 1, 1, 10);
        warehouse.addBay(bay);

        // Stock de dois SKUs, pedido apenas do SKU1
        bay.addBox(new Box("BX4", "SKU1", 5, LocalDate.parse("2025-11-01"), Instant.now(), "WAGON1"));
        bay.addBox(new Box("BX5", "SKU2", 8, LocalDate.parse("2025-11-02"), Instant.now(), "WAGON1"));

        OrderAllocationService service = new OrderAllocationService(warehouse);

        OrderLine line = new OrderLine("O4", 1, "SKU1", 5);
        List<OrderLine> lines = List.of(line);

        List<OrderAllocationResult> results = service.allocateOrders(lines, false);
        OrderAllocationResult result = results.get(0);

        assertEquals(5, allocatedQty(result));
        assertEquals(OrderAllocationResult.Status.ELIGIBLE, result.getStatus());
    }

    @Test
    void testFefoOrdering() {
        Warehouse warehouse = new Warehouse("W1");
        Bay bay = new Bay("W1", 1, 1, 10);
        warehouse.addBay(bay);

        // Duas caixas do mesmo SKU, FEFO (mais cedo primeiro)
        bay.addBox(new Box("BX6", "SKU1", 5, LocalDate.parse("2025-01-01"), Instant.now(), "WAGON1"));
        bay.addBox(new Box("BX7", "SKU1", 5, LocalDate.parse("2025-12-31"), Instant.now(), "WAGON1"));

        OrderAllocationService service = new OrderAllocationService(warehouse);

        OrderLine line = new OrderLine("O5", 1, "SKU1", 6);
        List<OrderLine> lines = List.of(line);

        List<OrderAllocationResult> results = service.allocateOrders(lines, true);
        OrderAllocationResult result = results.get(0);

        assertEquals(6, allocatedQty(result));
        assertEquals(OrderAllocationResult.Status.ELIGIBLE, result.getStatus());

        // A primeira box (expira mais cedo) deve ser usada primeiro
        AllocationRow firstAlloc = result.getAllocations().get(0);
        assertEquals("BX6", firstAlloc.getBoxId());
    }
}