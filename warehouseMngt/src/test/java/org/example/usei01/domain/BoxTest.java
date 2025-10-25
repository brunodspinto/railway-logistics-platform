package org.example.usei01.domain;

import org.junit.jupiter.api.Test;
import org.example.domain.Box;

import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * CRITICAL TESTS for Box - Focus on FEFO/FIFO ordering
 * Estimated: 15 tests (~3h implementation)
 */
class BoxTest {

    // ==================== FEFO/FIFO ORDERING (8 tests) - PRIORITY 1 ====================

    @Test
    void testCompareTo_ExpiryDate_EarlierFirst() {
        // Given: Two perishable boxes with different expiry dates
        Instant now = Instant.now();
        Box earlyExpiry = new Box("BOX-001", "SKU-A", 10,
                LocalDate.of(2025, 11, 1), now, "WAGON-1");
        Box lateExpiry = new Box("BOX-002", "SKU-A", 10,
                LocalDate.of(2025, 12, 1), now, "WAGON-1");

        // Then: Earlier expiry should come first
        assertTrue(earlyExpiry.compareTo(lateExpiry) < 0,
                "Box with earlier expiry should come first");
        assertTrue(lateExpiry.compareTo(earlyExpiry) > 0,
                "Box with later expiry should come after");
    }

    @Test
    void testCompareTo_NullExpiry_ComesLast() {
        // Given: One perishable and one non-perishable box
        Instant now = Instant.now();
        Box perishable = new Box("BOX-001", "SKU-A", 10,
                LocalDate.of(2025, 11, 1), now, "WAGON-1");
        Box nonPerishable = new Box("BOX-002", "SKU-A", 10,
                null, now, "WAGON-1");

        // Then: Perishable should come before non-perishable
        assertTrue(perishable.compareTo(nonPerishable) < 0,
                "Perishable box should come before non-perishable");
        assertTrue(nonPerishable.compareTo(perishable) > 0,
                "Non-perishable box should come after perishable");
    }

    @Test
    void testCompareTo_SameExpiry_ReceivedAtOlderFirst() {
        // Given: Two boxes with same expiry, different receivedAt
        LocalDate expiry = LocalDate.of(2025, 11, 1);
        Instant older = Instant.parse("2025-10-01T10:00:00Z");
        Instant newer = Instant.parse("2025-10-02T10:00:00Z");

        Box olderBox = new Box("BOX-001", "SKU-A", 10, expiry, older, "WAGON-1");
        Box newerBox = new Box("BOX-002", "SKU-A", 10, expiry, newer, "WAGON-1");

        // Then: Older receivedAt should come first
        assertTrue(olderBox.compareTo(newerBox) < 0,
                "Box received earlier should come first");
        assertTrue(newerBox.compareTo(olderBox) > 0,
                "Box received later should come after");
    }

    @Test
    void testCompareTo_AllSame_BoxIdAscending() {
        // Given: Two boxes identical except for boxId
        LocalDate expiry = LocalDate.of(2025, 11, 1);
        Instant received = Instant.now();

        Box boxA = new Box("BOX-A", "SKU-A", 10, expiry, received, "WAGON-1");
        Box boxB = new Box("BOX-B", "SKU-A", 10, expiry, received, "WAGON-1");

        // Then: BoxId should be tie-breaker (ascending)
        assertTrue(boxA.compareTo(boxB) < 0, "BOX-A should come before BOX-B");
        assertTrue(boxB.compareTo(boxA) > 0, "BOX-B should come after BOX-A");
    }

    @Test
    void testCompareTo_BothNullExpiry_ReceivedAtDecides() {
        // Given: Two non-perishable boxes with different receivedAt
        Instant older = Instant.parse("2025-10-01T10:00:00Z");
        Instant newer = Instant.parse("2025-10-02T10:00:00Z");

        Box olderBox = new Box("BOX-001", "SKU-A", 10, null, older, "WAGON-1");
        Box newerBox = new Box("BOX-002", "SKU-A", 10, null, newer, "WAGON-1");

        // Then: FIFO for non-perishable (older first)
        assertTrue(olderBox.compareTo(newerBox) < 0,
                "Non-perishable: older receivedAt should come first");
    }

    @Test
    void testCompareTo_Equal_ReturnsZero() {
        // Given: Same box compared to itself
        Instant now = Instant.now();
        Box box = new Box("BOX-001", "SKU-A", 10,
                LocalDate.of(2025, 11, 1), now, "WAGON-1");

        // Then: Should return 0
        assertEquals(0, box.compareTo(box), "Box compared to itself should be equal");
    }

    @Test
    void testCompareTo_ComplexMixedScenario() {
        // Given: Mix of perishable and non-perishable with various dates
        Instant baseTime = Instant.parse("2025-10-01T10:00:00Z");

        Box p1 = new Box("P1", "SKU-A", 10,
                LocalDate.of(2025, 11, 1), baseTime, "WAGON-1");
        Box p2 = new Box("P2", "SKU-A", 10,
                LocalDate.of(2025, 12, 1), baseTime, "WAGON-1");
        Box np1 = new Box("NP1", "SKU-A", 10,
                null, baseTime, "WAGON-1");
        Box np2 = new Box("NP2", "SKU-A", 10,
                null, baseTime.plus(1, ChronoUnit.DAYS), "WAGON-1");

        // Then: Expected order: p1 < p2 < np1 < np2
        assertTrue(p1.compareTo(p2) < 0, "Earlier expiry comes first");
        assertTrue(p2.compareTo(np1) < 0, "Perishable before non-perishable");
        assertTrue(np1.compareTo(np2) < 0, "Older non-perishable first");
    }

    @Test
    void testCompareTo_TransitivityProperty() {
        // Given: Three boxes A < B < C
        Instant now = Instant.now();
        Box a = new Box("A", "SKU", 10, LocalDate.of(2025, 11, 1), now, "W1");
        Box b = new Box("B", "SKU", 10, LocalDate.of(2025, 12, 1), now, "W1");
        Box c = new Box("C", "SKU", 10, LocalDate.of(2026, 1, 1), now, "W1");

        // Then: Transitivity should hold (A<B, B<C => A<C)
        assertTrue(a.compareTo(b) < 0);
        assertTrue(b.compareTo(c) < 0);
        assertTrue(a.compareTo(c) < 0, "Transitivity: A<B, B<C => A<C must hold");
    }

    // ==================== QTY MANAGEMENT (4 tests) - PRIORITY 2 ====================

    @Test
    void testReduceQuantity_PartialReduction_Success() {
        // Given: Box with 100 units
        Box box = new Box("BOX-001", "SKU-A", 100,
                null, Instant.now(), "WAGON-1");

        // When: Reduce by 30
        box.reduceQuantity(30);

        // Then: Should have 70 remaining
        assertEquals(70, box.getQuantity(), "Quantity should be reduced correctly");
    }

    @Test
    void testReduceQuantity_FullReduction_BecomesZero() {
        // Given: Box with 50 units
        Box box = new Box("BOX-001", "SKU-A", 50,
                null, Instant.now(), "WAGON-1");

        // When: Reduce by 50 (all)
        box.reduceQuantity(50);

        // Then: Should have 0 remaining
        assertEquals(0, box.getQuantity(), "Quantity should be zero after full reduction");
    }

    @Test
    void testReduceQuantity_ExceedsAvailable_ThrowsException() {
        // Given: Box with only 10 units
        Box box = new Box("BOX-001", "SKU-A", 10,
                null, Instant.now(), "WAGON-1");

        // Then: Trying to reduce by 20 should throw
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> box.reduceQuantity(20));
        assertTrue(ex.getMessage().contains("Cannot reduce"),
                "Exception message should mention 'Cannot reduce'");
    }

    @Test
    void testReduceQuantity_NegativeAmount_ThrowsException() {
        // Given: Any box
        Box box = new Box("BOX-001", "SKU-A", 10,
                null, Instant.now(), "WAGON-1");

        // Then: Negative reduction should throw
        assertThrows(IllegalArgumentException.class,
                () -> box.reduceQuantity(-5));
    }

    // ==================== VALIDATION (3 tests) - PRIORITY 3 ====================

    @Test
    void testBoxCreation_NullBoxId_ThrowsException() {
        assertThrows(IllegalArgumentException.class,
                () -> new Box(null, "SKU-A", 10, null, Instant.now(), "WAGON-1"));
    }

    @Test
    void testBoxCreation_NegativeQty_ThrowsException() {
        assertThrows(IllegalArgumentException.class,
                () -> new Box("BOX-001", "SKU-A", -10, null, Instant.now(), "WAGON-1"));
    }

    @Test
    void testBoxCreation_NullReceivedAt_ThrowsException() {
        assertThrows(IllegalArgumentException.class,
                () -> new Box("BOX-001", "SKU-A", 10, null, null, "WAGON-1"));
    }
}

