package org.example.usei03.domain;

import org.example.domain.PickingItem;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class PickingItemTest {

    private PickingItem item;

    @BeforeEach
    void setUp() {
        item = new PickingItem(
                "ORD1",     // orderId
                10,         // lineNo
                "SKU123",   // sku
                "BOX9",     // boxId
                3,          // aisle
                5,          // bay
                4,          // qty
                12.5        // unitWeight
        );
    }

    @Test
    @DisplayName("getTotalWeight deve calcular corretamente qty * unitWeight")
    void testGetTotalWeight() {
        double expected = 4 * 12.5;
        assertEquals(expected, item.getTotalWeight(), 0.001, "Peso total deve ser qty * unitWeight");
    }

    @Test
    @DisplayName("Getters devem devolver os valores corretos")
    void testGetters() {
        assertEquals("ORD1", item.getOrderId());
        assertEquals(10, item.getLineNo());
        assertEquals("SKU123", item.getSku());
        assertEquals("BOX9", item.getBoxId());
        assertEquals(3, item.getAisle());
        assertEquals(5, item.getBay());
        assertEquals(4, item.getQty());
    }

    @Test
    @DisplayName("toString deve conter todos os campos principais")
    void testToString() {
        String text = item.toString();

        assertTrue(text.contains("ORD1"));
        assertTrue(text.contains("SKU123"));
        assertTrue(text.contains("BOX9"));
        assertTrue(text.contains("aisle=3"));
        assertTrue(text.contains("bay=5"));
        assertTrue(text.contains("quantity=4"));
        assertTrue(text.contains("unitWeight=12.50"));
    }

    @Test
    @DisplayName("Deve permitir criar PickingItem com valores positivos válidos")
    void testValidConstruction() {
        assertDoesNotThrow(() -> new PickingItem("O2", 1, "SKUX", "B5", 2, 3, 10, 5.0));
    }

    @Test
    @DisplayName("getTotalWeight deve funcionar com unitWeight decimal")
    void testDecimalWeightCalculation() {
        PickingItem decimalItem = new PickingItem("O2", 1, "SKUX", "B2", 2, 3, 3, 2.75);
        assertEquals(8.25, decimalItem.getTotalWeight(), 0.001);
    }
}
