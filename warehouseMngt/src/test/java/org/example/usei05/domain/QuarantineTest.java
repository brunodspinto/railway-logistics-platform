package org.example.usei05.domain;

import org.example.domain.Quarantine;
import org.example.domain.ReturnRecord;
import org.example.domain.ReturnReason;
import org.example.exception.QuarantineEmptyException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Essential tests for Quarantine priority queue.
 * Focus: ordering (CRITICAL), poll behavior, state tracking.
 */
class QuarantineTest {

    private Quarantine quarantine;

    @BeforeEach
    void setUp() {
        quarantine = new Quarantine();
    }

    // ==================== BASIC OPERATIONS (~4 testes) ====================

    @Test
    void testEmptyQuarantine() {
        assertTrue(quarantine.isEmpty());
        assertEquals(0, quarantine.size());
        assertEquals(0, quarantine.getProcessedCount());
    }

    @Test
    void testAdd_SingleReturn() {
        ReturnRecord record = createReturn("RET00001", LocalDateTime.now());
        quarantine.add(record);

        assertEquals(1, quarantine.size());
        assertFalse(quarantine.isEmpty());
    }

    @Test
    void testAdd_NullReturn_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> quarantine.add(null));
    }

    @Test
    void testAddAll() {
        List<ReturnRecord> returns = List.of(
                createReturn("RET00001", LocalDateTime.now()),
                createReturn("RET00002", LocalDateTime.now()),
                createReturn("RET00003", LocalDateTime.now())
        );

        quarantine.addAll(returns);
        assertEquals(3, quarantine.size());
    }

    // ==================== ORDERING (CRITICAL!) (~5 testes) ====================

    @Test
    void testPoll_ReturnsLatestFirst() {
        LocalDateTime earlier = LocalDateTime.of(2025, 10, 22, 14, 0);
        LocalDateTime later = LocalDateTime.of(2025, 10, 22, 15, 0);

        quarantine.add(createReturn("RET00001", earlier));
        quarantine.add(createReturn("RET00002", later));

        // Should return RET00002 first (latest)
        ReturnRecord first = quarantine.poll();
        assertEquals("RET00002", first.getReturnId());

        ReturnRecord second = quarantine.poll();
        assertEquals("RET00001", second.getReturnId());
    }

    @Test
    void testPoll_SameTimestamp_AscendingReturnId() {
        LocalDateTime sameTime = LocalDateTime.of(2025, 10, 22, 15, 0);

        quarantine.add(createReturn("RET00003", sameTime));
        quarantine.add(createReturn("RET00001", sameTime));
        quarantine.add(createReturn("RET00002", sameTime));

        // Should poll in order: RET00001, RET00002, RET00003
        assertEquals("RET00001", quarantine.poll().getReturnId());
        assertEquals("RET00002", quarantine.poll().getReturnId());
        assertEquals("RET00003", quarantine.poll().getReturnId());
    }

    @Test
    void testPoll_MultipleReturns_CorrectOrder() {
        quarantine.add(createReturn("RET00001", LocalDateTime.of(2025, 10, 22, 14, 0)));
        quarantine.add(createReturn("RET00003", LocalDateTime.of(2025, 10, 22, 15, 0)));
        quarantine.add(createReturn("RET00002", LocalDateTime.of(2025, 10, 22, 15, 0)));

        // Expected: RET00002 (15:00), RET00003 (15:00), RET00001 (14:00)
        assertEquals("RET00002", quarantine.poll().getReturnId());
        assertEquals("RET00003", quarantine.poll().getReturnId());
        assertEquals("RET00001", quarantine.poll().getReturnId());
    }

    @Test
    void testPeek_DoesNotRemove() {
        ReturnRecord record = createReturn("RET00001", LocalDateTime.now());
        quarantine.add(record);

        ReturnRecord peeked1 = quarantine.peek();
        ReturnRecord peeked2 = quarantine.peek();

        assertEquals(peeked1, peeked2);
        assertEquals(1, quarantine.size(), "Size should not change after peek");
    }

    @Test
    void testPoll_EmptyQueue_ThrowsException() {
        assertThrows(QuarantineEmptyException.class, () -> quarantine.poll());
    }

    // ==================== STATE TRACKING (~3 testes) ====================

    @Test
    void testProcessedCount_IncreasesAfterPoll() {
        quarantine.add(createReturn("RET00001", LocalDateTime.now()));
        quarantine.add(createReturn("RET00002", LocalDateTime.now()));

        assertEquals(0, quarantine.getProcessedCount());

        quarantine.poll();
        assertEquals(1, quarantine.getProcessedCount());
        assertEquals(1, quarantine.size());

        quarantine.poll();
        assertEquals(2, quarantine.getProcessedCount());
        assertEquals(0, quarantine.size());
    }

    @Test
    void testWasProcessed() {
        ReturnRecord record = createReturn("RET00001", LocalDateTime.now());
        quarantine.add(record);

        assertFalse(quarantine.wasProcessed("RET00001"));

        quarantine.poll();

        assertTrue(quarantine.wasProcessed("RET00001"));
    }

    @Test
    void testClearAll() {
        quarantine.add(createReturn("RET00001", LocalDateTime.now()));
        quarantine.poll();

        assertEquals(0, quarantine.size());
        assertEquals(1, quarantine.getProcessedCount());

        quarantine.clearAll();

        assertEquals(0, quarantine.size());
        assertEquals(0, quarantine.getProcessedCount());
    }

    // ==================== HELPER METHOD ====================

    private ReturnRecord createReturn(String returnId, LocalDateTime timestamp) {
        return new ReturnRecord(returnId, "SKU001", 10,
                ReturnReason.CUSTOMER_REMORSE, timestamp, null);
    }
}
