package org.example.usei05.domain;

import org.example.domain.ReturnReason;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Minimal but effective tests for ReturnReason enum.
 * Focuses on parsing logic and restockability.
 */
class ReturnReasonTest {

    // ==================== PARSING TESTS (~5 testes) ====================

    @Test
    void testFromString_ValidReasons_AllVariants() {
        // Test all 4 reasons work
        assertEquals(ReturnReason.CUSTOMER_REMORSE, ReturnReason.fromString("customer remorse"));
        assertEquals(ReturnReason.DAMAGED, ReturnReason.fromString("damaged"));
        assertEquals(ReturnReason.EXPIRED, ReturnReason.fromString("expired"));
        assertEquals(ReturnReason.CYCLE_COUNT, ReturnReason.fromString("cycle count"));
    }

    @Test
    void testFromString_CaseInsensitive() {
        assertEquals(ReturnReason.CUSTOMER_REMORSE, ReturnReason.fromString("CUSTOMER REMORSE"));
        assertEquals(ReturnReason.CUSTOMER_REMORSE, ReturnReason.fromString("Customer Remorse"));
        assertEquals(ReturnReason.CUSTOMER_REMORSE, ReturnReason.fromString("customer remorse"));
    }

    @Test
    void testFromString_WithHyphensAndUnderscores() {
        assertEquals(ReturnReason.CUSTOMER_REMORSE, ReturnReason.fromString("customer-remorse"));
        assertEquals(ReturnReason.CUSTOMER_REMORSE, ReturnReason.fromString("customer_remorse"));
        assertEquals(ReturnReason.CYCLE_COUNT, ReturnReason.fromString("cycle-count"));
        assertEquals(ReturnReason.CYCLE_COUNT, ReturnReason.fromString("cycle_count"));
    }

    @Test
    void testFromString_InvalidReason_ThrowsException() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> ReturnReason.fromString("invalid reason")
        );
        assertTrue(ex.getMessage().contains("Invalid return reason"));
    }

    @Test
    void testFromString_NullReason_ThrowsException() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> ReturnReason.fromString(null)
        );
        assertEquals("Return reason cannot be null", ex.getMessage());
    }

    // ==================== RESTOCKABILITY TESTS (~3 testes) ====================

    @Test
    void testIsRestockable_RestockableReasons() {
        assertTrue(ReturnReason.CUSTOMER_REMORSE.isRestockable());
        assertTrue(ReturnReason.CYCLE_COUNT.isRestockable());
    }

    @Test
    void testIsRestockable_NonRestockableReasons() {
        assertFalse(ReturnReason.DAMAGED.isRestockable());
        assertFalse(ReturnReason.EXPIRED.isRestockable());
    }

    @Test
    void testToString_FormatsCorrectly() {
        assertEquals("customer remorse", ReturnReason.CUSTOMER_REMORSE.toString());
        assertEquals("cycle count", ReturnReason.CYCLE_COUNT.toString());
        assertEquals("damaged", ReturnReason.DAMAGED.toString());
        assertEquals("expired", ReturnReason.EXPIRED.toString());
    }
}
