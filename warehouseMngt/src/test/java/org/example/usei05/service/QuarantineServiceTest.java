package org.example.usei05.service;

import org.example.domain.ReturnRecord;
import org.example.domain.ReturnReason;
import org.example.exception.QuarantineEmptyException;
import org.example.repository.ItemRepository;
import org.example.service.QuarantineService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class QuarantineServiceTest {

    private QuarantineService service;

    @BeforeEach
    void setUp() {
        ItemRepository repo = new ItemRepository() {
            public boolean existsSku(String sku) {
                return true;
            }
        };

        service = new QuarantineService(repo);
    }

    @Test
    void testInitialState_Empty() {
        assertFalse(service.hasReturns());
        assertEquals(0, service.getPendingCount());
        assertEquals(0, service.getProcessedCount());
    }

    @Test
    void testAddReturn() {
        ReturnRecord record = createReturn("RET001");
        service.addReturn(record);

        assertTrue(service.hasReturns());
        assertEquals(1, service.getPendingCount());
    }

    @Test
    void testGetNextReturn() {
        service.addReturn(createReturn("RET001"));
        service.addReturn(createReturn("RET002"));

        ReturnRecord first = service.getNextReturn();
        assertNotNull(first);

        assertEquals(1, service.getPendingCount());
        assertEquals(1, service.getProcessedCount());
    }

    @Test
    void testGetNextReturn_EmptyQueue_ThrowsException() {
        assertThrows(QuarantineEmptyException.class, () -> service.getNextReturn());
    }

    @Test
    void testPeekNextReturn_DoesNotRemove() {
        service.addReturn(createReturn("RET001"));

        ReturnRecord peeked1 = service.peekNextReturn();
        ReturnRecord peeked2 = service.peekNextReturn();

        assertEquals(peeked1, peeked2);
        assertEquals(1, service.getPendingCount());
    }

    @Test
    void testPeekNextReturn_EmptyQueue_ReturnsNull() {
        assertNull(service.peekNextReturn());
    }

    @Test
    void testProcessAllReturns() {
        service.addReturn(createReturn("RET001"));
        service.addReturn(createReturn("RET002"));
        service.addReturn(createReturn("RET003"));

        AtomicInteger processedCount = new AtomicInteger(0);

        int result = service.processAllReturns(record -> {
            assertNotNull(record);
            processedCount.incrementAndGet();
        });

        assertEquals(3, result);
        assertEquals(3, processedCount.get());
        assertFalse(service.hasReturns());
    }

    @Test
    void testProcessAllReturns_EmptyQueue_ReturnsZero() {
        int result = service.processAllReturns(record -> {});
        assertEquals(0, result);
    }

    @Test
    void testProcessAllReturns_NullProcessor_ThrowsException() {
        service.addReturn(createReturn("RET001"));
        assertThrows(IllegalArgumentException.class,
                () -> service.processAllReturns(null));
    }

    @Test
    void testReset_ClearsBothPendingAndProcessed() {
        service.addReturn(createReturn("RET001"));
        service.getNextReturn();

        assertEquals(0, service.getPendingCount());
        assertEquals(1, service.getProcessedCount());

        service.reset();

        assertEquals(0, service.getPendingCount());
        assertEquals(0, service.getProcessedCount());
    }

    private ReturnRecord createReturn(String returnId) {
        return new ReturnRecord(returnId, "SKU001", 10,
                ReturnReason.CUSTOMER_REMORSE, LocalDateTime.now(), null);
    }
}