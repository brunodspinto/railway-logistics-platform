package org.example.usei05.domain;

import org.example.domain.ReturnRecord;
import org.example.domain.ReturnReason;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Essential tests for ReturnRecord.
 * Focus: ordering (CRITICAL for USEI05), validation, business logic.
 */
class ReturnRecordTest {

    // ==================== CREATION & VALIDATION (~4 testes) ====================

    @Test
    void testValidCreation() {
        ReturnRecord record = new ReturnRecord(
                "RET00001",
                "SKU001",
                10,
                ReturnReason.CUSTOMER_REMORSE,
                LocalDateTime.of(2025, 10, 22, 15, 0),
                LocalDate.of(2025, 12, 31)
        );

        assertEquals("RET00001", record.getReturnId());
        assertEquals("SKU001", record.getSku());
        assertEquals(10, record.getQty());
        assertEquals(ReturnReason.CUSTOMER_REMORSE, record.getReason());
        assertTrue(record.hasExpiryDate());
    }

    @Test
    void testInvalidCreation_NullsThrowException() {
        assertThrows(IllegalArgumentException.class,
                () -> new ReturnRecord(null, "SKU001", 10, ReturnReason.DAMAGED, LocalDateTime.now(), null));

        assertThrows(IllegalArgumentException.class,
                () -> new ReturnRecord("RET001", null, 10, ReturnReason.DAMAGED, LocalDateTime.now(), null));

        assertThrows(IllegalArgumentException.class,
                () -> new ReturnRecord("RET001", "SKU001", 10, null, LocalDateTime.now(), null));

        assertThrows(IllegalArgumentException.class,
                () -> new ReturnRecord("RET001", "SKU001", 10, ReturnReason.DAMAGED, null, null));
    }

    @Test
    void testInvalidQuantity_ThrowsException() {
        assertThrows(IllegalArgumentException.class,
                () -> new ReturnRecord("RET001", "SKU001", 0, ReturnReason.DAMAGED, LocalDateTime.now(), null));

        assertThrows(IllegalArgumentException.class,
                () -> new ReturnRecord("RET001", "SKU001", -5, ReturnReason.DAMAGED, LocalDateTime.now(), null));
    }

    @Test
    void testStringConstructor_ParsesCorrectly() {
        ReturnRecord record = new ReturnRecord(
                "RET00001",
                "SKU001",
                "10",
                "customer remorse",
                "2025-10-22T15:00:00",
                "2025-12-31"
        );

        assertEquals(10, record.getQty());
        assertEquals(ReturnReason.CUSTOMER_REMORSE, record.getReason());
        assertEquals(LocalDate.of(2025, 12, 31), record.getExpiryDate());
    }

    // ==================== ORDERING (CRITICAL!) (~5 testes) ====================

    @Test
    void testCompareTo_DifferentTimestamps_LatestFirst() {
        ReturnRecord earlier = createReturn("RET00001", LocalDateTime.of(2025, 10, 22, 14, 0));
        ReturnRecord later = createReturn("RET00002", LocalDateTime.of(2025, 10, 22, 15, 0));

        // Later should come BEFORE earlier (descending order)
        assertTrue(later.compareTo(earlier) < 0);
        assertTrue(earlier.compareTo(later) > 0);
    }

    @Test
    void testCompareTo_SameTimestamp_AscendingReturnId() {
        LocalDateTime sameTime = LocalDateTime.of(2025, 10, 22, 15, 0);
        ReturnRecord ret001 = createReturn("RET00001", sameTime);
        ReturnRecord ret002 = createReturn("RET00002", sameTime);

        // When timestamps equal, RET00001 < RET00002
        assertTrue(ret001.compareTo(ret002) < 0);
        assertTrue(ret002.compareTo(ret001) > 0);
    }

    @Test
    void testCompareTo_MultipleReturns_CorrectOrder() {
        ReturnRecord r1 = createReturn("RET00001", LocalDateTime.of(2025, 10, 22, 14, 0));
        ReturnRecord r2 = createReturn("RET00002", LocalDateTime.of(2025, 10, 22, 15, 0));
        ReturnRecord r3 = createReturn("RET00003", LocalDateTime.of(2025, 10, 22, 15, 0)); // same as r2

        // Expected order: r2 < r3 < r1 (latest first, then by ID)
        assertTrue(r2.compareTo(r3) < 0, "r2 should come before r3 (same time, lower ID)");
        assertTrue(r2.compareTo(r1) < 0, "r2 should come before r1 (later time)");
        assertTrue(r3.compareTo(r1) < 0, "r3 should come before r1 (later time)");
    }

    @Test
    void testCompareTo_SameReturn_ReturnsZero() {
        ReturnRecord record = createReturn("RET00001", LocalDateTime.now());
        assertEquals(0, record.compareTo(record));
    }

    @Test
    void testCompareTo_NullOther_ReturnsNegative() {
        ReturnRecord record = createReturn("RET00001", LocalDateTime.now());
        assertTrue(record.compareTo(null) < 0);
    }

    // ==================== BUSINESS LOGIC (~3 testes) ====================

    @Test
    void testIsExpired() {
        LocalDate today = LocalDate.of(2025, 10, 22);

        ReturnRecord expiredYesterday = new ReturnRecord(
                "RET001", "SKU001", 10, ReturnReason.DAMAGED,
                LocalDateTime.now(), LocalDate.of(2025, 10, 21)
        );
        assertTrue(expiredYesterday.isExpired(today));

        ReturnRecord expiresToday = new ReturnRecord(
                "RET002", "SKU001", 10, ReturnReason.DAMAGED,
                LocalDateTime.now(), LocalDate.of(2025, 10, 22)
        );
        assertTrue(expiresToday.isExpired(today), "Should be expired today");

        ReturnRecord expiresTomorrow = new ReturnRecord(
                "RET003", "SKU001", 10, ReturnReason.DAMAGED,
                LocalDateTime.now(), LocalDate.of(2025, 10, 23)
        );
        assertFalse(expiresTomorrow.isExpired(today));

        ReturnRecord noExpiry = new ReturnRecord(
                "RET004", "SKU001", 10, ReturnReason.DAMAGED,
                LocalDateTime.now(), null
        );
        assertFalse(noExpiry.isExpired(today), "Non-perishable never expires");
    }

    @Test
    void testIsRestockable() {
        ReturnRecord restockable = createReturn("RET001", ReturnReason.CUSTOMER_REMORSE);
        assertTrue(restockable.isRestockable());

        ReturnRecord notRestockable = createReturn("RET002", ReturnReason.DAMAGED);
        assertFalse(notRestockable.isRestockable());
    }

    @Test
    void testCreateRestockBoxId() {
        ReturnRecord record = createReturn("RET00123", LocalDateTime.now());
        assertEquals("RET-RET00123", record.createRestockBoxId());
    }

    // ==================== HELPER METHODS ====================

    private ReturnRecord createReturn(String returnId, LocalDateTime timestamp) {
        return new ReturnRecord(returnId, "SKU001", 10,
                ReturnReason.CUSTOMER_REMORSE, timestamp, null);
    }

    private ReturnRecord createReturn(String returnId, ReturnReason reason) {
        return new ReturnRecord(returnId, "SKU001", 10,
                reason, LocalDateTime.now(), null);
    }
}
