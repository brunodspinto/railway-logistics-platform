package org.example.usei02.domain;

import org.example.domain.OrderLine;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class OrderLineTest {

    @Test
    @DisplayName("Criar OrderLine com valores corretos")
    void testOrderLineCreation() {
        OrderLine line = new OrderLine("O1", 1, "SKU1", 10);
        assertEquals("O1", line.getOrderId());
        assertEquals(1, line.getLineNo());
        assertEquals("SKU1", line.getSku());
        assertEquals(10, line.getRequestedQty());
    }

    @Test
    @DisplayName("Permitir quantidade zero")
    void testZeroQuantity() {
        OrderLine line = new OrderLine("O2", 2, "SKU2", 0);
        assertEquals(0, line.getRequestedQty());
    }

    @Test
    @DisplayName("Guardar SKU corretamente")
    void testDifferentSku() {
        OrderLine line = new OrderLine("O3", 3, "SKU999", 5);
        assertEquals("SKU999", line.getSku());
    }
}