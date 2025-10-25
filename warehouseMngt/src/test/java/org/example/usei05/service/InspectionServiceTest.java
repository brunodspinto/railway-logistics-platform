package org.example.usei05.service;

import org.example.domain.ReturnRecord;
import org.example.domain.ReturnReason;
import org.example.domain.Warehouse;
import org.example.repository.WarehouseRepository;
import org.example.results.InspectionResult;
import org.example.service.InspectionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class InspectionServiceTest {

    private InspectionService service;

    @BeforeEach
    void setUp() {
        // Stub simples do repository
        WarehouseRepository repo = new WarehouseRepository() {
            @Override
            public Warehouse findDefault() {
                return new Warehouse("TEST");
            }

            @Override
            public void save(Warehouse warehouse) {
                // no-op
            }
        };

        service = new InspectionService(repo);
    }

    @Test
    void testInspect_Damaged_AlwaysDiscard() {
        ReturnRecord record = createReturn("RET001", ReturnReason.DAMAGED, 10, null);
        InspectionResult result = service.inspect(record);

        assertEquals("DISCARD", result.getAction());
        assertEquals(0, result.getQtyRestocked());
        assertEquals(10, result.getQtyDiscarded());
    }

    @Test
    void testInspect_Damaged_WithExpiryDate_StillDiscard() {
        ReturnRecord record = createReturn("RET002", ReturnReason.DAMAGED, 20,
                LocalDate.now().plusMonths(1));
        InspectionResult result = service.inspect(record);

        assertEquals("DISCARD", result.getAction());
        assertEquals(20, result.getQtyDiscarded());
    }

    @Test
    void testInspect_ExpiredReason_AlwaysDiscard() {
        ReturnRecord record = createReturn("RET003", ReturnReason.EXPIRED, 15, null);
        InspectionResult result = service.inspect(record);

        assertEquals("DISCARD", result.getAction());
        assertEquals(15, result.getQtyDiscarded());
    }

    @Test
    void testInspect_CustomerRemorse_ButProductExpired_DiscardsAnyway() {
        ReturnRecord record = createReturn("RET004", ReturnReason.CUSTOMER_REMORSE, 10,
                LocalDate.now().minusDays(1));
        InspectionResult result = service.inspect(record);

        assertEquals("DISCARD", result.getAction());
        assertEquals(10, result.getQtyDiscarded());
        assertEquals(0, result.getQtyRestocked());
    }

    @Test
    void testInspect_CustomerRemorse_NotExpired_PartialRestock() {
        ReturnRecord record = createReturn("RET005", ReturnReason.CUSTOMER_REMORSE, 10,
                LocalDate.now().plusMonths(1));
        InspectionResult result = service.inspect(record);

        // 80% rule: 10 * 0.8 = 8 restocked, 2 discarded
        assertTrue(result.getAction().contains("RESTOCK"));
        assertEquals(8, result.getQtyRestocked());
        assertEquals(2, result.getQtyDiscarded());
    }

    @Test
    void testInspect_CustomerRemorse_LargeQuantity() {
        ReturnRecord record = createReturn("RET006", ReturnReason.CUSTOMER_REMORSE, 100, null);
        InspectionResult result = service.inspect(record);

        assertEquals("PARTIAL_RESTOCK", result.getAction());
        assertEquals(80, result.getQtyRestocked());
        assertEquals(20, result.getQtyDiscarded());
    }

    @Test
    void testInspect_CycleCount_NotExpired_PartialRestock() {
        ReturnRecord record = createReturn("RET008", ReturnReason.CYCLE_COUNT, 20,
                LocalDate.now().plusMonths(1));
        InspectionResult result = service.inspect(record);

        assertEquals(16, result.getQtyRestocked());
        assertEquals(4, result.getQtyDiscarded());
    }

    @Test
    void testInspect_CycleCount_Expired_Discard() {
        ReturnRecord record = createReturn("RET009", ReturnReason.CYCLE_COUNT, 10,
                LocalDate.now().minusDays(1));
        InspectionResult result = service.inspect(record);

        assertEquals("DISCARD", result.getAction());
        assertEquals(10, result.getQtyDiscarded());
    }

    @Test
    void testInspect_QtyRestockedPlusDiscarded_EqualsTotal() {
        ReturnRecord record = createReturn("RET011", ReturnReason.CUSTOMER_REMORSE, 37, null);
        InspectionResult result = service.inspect(record);

        int total = result.getQtyRestocked() + result.getQtyDiscarded();
        assertEquals(37, total, "Restocked + Discarded must equal original qty");
    }

    @Test
    void testInspect_NullRecord_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> service.inspect(null));
    }

    private ReturnRecord createReturn(String id, ReturnReason reason, int qty, LocalDate expiry) {
        return new ReturnRecord(id, "SKU001", qty, reason, LocalDateTime.now(), expiry);
    }
}