package org.example.usei02.results;

import org.example.results.AllocationRow;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AllocationRowTest {

    @Test
    @DisplayName("Criar AllocationRow com todos os valores")
    void testAllocationRowCreation() {
        AllocationRow row = new AllocationRow("O1", 1, "SKU1", 5, "BX1", 2, 7);
        assertEquals("O1", row.getOrderId());
        assertEquals(1, row.getLineNo());
        assertEquals("SKU1", row.getSku());
        assertEquals(5, row.getQty());
        assertEquals("BX1", row.getBoxId());
        assertEquals(2, row.getAisle());
        assertEquals(7, row.getBay());
    }

    @Test
    @DisplayName("Permitir quantidade zero")
    void testZeroQuantity() {
        AllocationRow row = new AllocationRow("O2", 2, "SKU2", 0, "BX2", 3, 5);
        assertEquals(0, row.getQty());
    }

    @Test
    @DisplayName("Guardar BoxId, Aisle e Bay corretamente")
    void testDifferentBoxAndLocation() {
        AllocationRow row = new AllocationRow("O3", 3, "SKU3", 7, "BX3", 10, 15);
        assertEquals("BX3", row.getBoxId());
        assertEquals(10, row.getAisle());
        assertEquals(15, row.getBay());
    }
}