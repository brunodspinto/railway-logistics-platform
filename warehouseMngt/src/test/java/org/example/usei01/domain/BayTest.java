package org.example.usei01.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.example.domain.Bay;
import org.example.domain.Box;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * CRITICAL TESTS for Bay - Focus on FEFO/FIFO insertion and dispatch
 * Estimated: 15 tests (~3h implementation)
 */
class BayTest {

    private Bay bay;
    private static final String WAREHOUSE_ID = "WH-001";
    private static final int AISLE = 1;
    private static final int BAY_NUMBER = 5;
    private static final int CAPACITY = 10;

    @BeforeEach
    void setUp() {
        bay = new Bay(WAREHOUSE_ID, AISLE, BAY_NUMBER, CAPACITY);
    }

    // ==================== CONSTRUCTION & VALIDATION (3 tests) ====================

    @Test
    void testBayCreation_ValidData_Success() {
        Bay newBay = new Bay("WH-TEST", 2, 3, 20);

        assertEquals("WH-TEST", newBay.getWarehouseId());
        assertEquals(2, newBay.getAisleNumber());
        assertEquals(3, newBay.getBayNumber());
        assertEquals(20, newBay.getCapacityBoxes());
        assertTrue(newBay.isEmpty());
    }

    @Test
    void testBayCreation_ZeroCapacity_ThrowsException() {
        assertThrows(IllegalArgumentException.class,
                () -> new Bay("WH-001", 1, 1, 0));
    }

    @Test
    void testBayCreation_NegativeCapacity_ThrowsException() {
        assertThrows(IllegalArgumentException.class,
                () -> new Bay("WH-001", 1, 1, -5));
    }

    // ==================== BOX INSERTION - FEFO/FIFO (6 tests) ====================

    @Test
    void testAddBox_EmptyBay_InsertsSuccessfully() {
        // Given: Empty bay
        Box box = createBox("BOX-001", LocalDate.of(2025, 11, 1));

        // When: Add box
        bay.addBox(box);

        // Then: Box should be in bay
        assertEquals(1, bay.getCurrentBoxCount());
        assertEquals(box, bay.getBoxes().get(0));
        assertNotNull(box.getLocation(), "Box location should be set");
    }

    @Test
    void testAddBox_PerishableBeforeNonPerishable() {
        // Given: Add non-perishable first
        Box nonPerishable = createBox("BOX-NP", null);
        bay.addBox(nonPerishable);

        // When: Add perishable
        Box perishable = createBox("BOX-P", LocalDate.of(2025, 11, 1));
        bay.addBox(perishable);

        // Then: Perishable should be first
        List<Box> boxes = bay.getBoxes();
        assertEquals(2, boxes.size());
        assertEquals("BOX-P", boxes.get(0).getBoxId());
        assertEquals("BOX-NP", boxes.get(1).getBoxId());
    }

    @Test
    void testAddBox_EarlierExpiryFirst() {
        // Given: Add late expiry first
        Box lateExpiry = createBox("BOX-LATE", LocalDate.of(2025, 12, 1));
        bay.addBox(lateExpiry);

        // When: Add earlier expiry
        Box earlyExpiry = createBox("BOX-EARLY", LocalDate.of(2025, 11, 1));
        bay.addBox(earlyExpiry);

        // Then: Earlier expiry should be first
        assertEquals("BOX-EARLY", bay.getBoxes().get(0).getBoxId());
        assertEquals("BOX-LATE", bay.getBoxes().get(1).getBoxId());
    }

    @Test
    void testAddBox_MultipleBoxes_MaintainsFEFOOrder() {
        // Given: Add 5 boxes in random order
        bay.addBox(createBox("BOX-3", LocalDate.of(2025, 12, 1)));
        bay.addBox(createBox("BOX-1", LocalDate.of(2025, 11, 1)));
        bay.addBox(createBox("BOX-5", null)); // Non-perishable
        bay.addBox(createBox("BOX-2", LocalDate.of(2025, 11, 15)));
        bay.addBox(createBox("BOX-4", LocalDate.of(2026, 1, 1)));

        // Then: Should be sorted by FEFO (11/1, 11/15, 12/1, 1/1, null)
        List<Box> boxes = bay.getBoxes();
        assertEquals("BOX-1", boxes.get(0).getBoxId());
        assertEquals("BOX-2", boxes.get(1).getBoxId());
        assertEquals("BOX-3", boxes.get(2).getBoxId());
        assertEquals("BOX-4", boxes.get(3).getBoxId());
        assertEquals("BOX-5", boxes.get(4).getBoxId());
    }

    @Test
    void testAddBox_ExceedsCapacity_ThrowsException() {
        // Given: Bay with capacity 10, fill it completely
        for (int i = 0; i < CAPACITY; i++) {
            bay.addBox(createBox("BOX-" + i, null));
        }

        // Then: Adding one more should fail
        Box overflow = createBox("BOX-OVERFLOW", null);
        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> bay.addBox(overflow));
        assertTrue(ex.getMessage().contains("full"));
    }

    @Test
    void testAddBox_DuplicateBoxId_ThrowsException() {
        // Given: Bay with one box
        bay.addBox(createBox("BOX-001", null));

        // Then: Adding box with same ID should fail
        Box duplicate = createBox("BOX-001", null);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> bay.addBox(duplicate));
        assertTrue(ex.getMessage().contains("Duplicate"));
    }

    // ==================== DISPATCH OPERATIONS (4 tests) ====================

    @Test
    void testRemoveFirstBox_FromFront_Success() {
        // Given: Bay with 3 boxes
        bay.addBox(createBox("BOX-1", LocalDate.of(2025, 11, 1)));
        bay.addBox(createBox("BOX-2", LocalDate.of(2025, 12, 1)));
        bay.addBox(createBox("BOX-3", null));

        // When: Remove first box of SKU-A
        Box removed = bay.removeFirstBox("SKU-A");

        // Then: Should remove BOX-1 (earliest expiry)
        assertNotNull(removed);
        assertEquals("BOX-1", removed.getBoxId());
        assertEquals(2, bay.getCurrentBoxCount());
        assertEquals("BOX-2", bay.getBoxes().get(0).getBoxId());
    }

    @Test
    void testRemoveFirstBox_EmptyBay_ReturnsNull() {
        // When: Try to remove from empty bay
        Box removed = bay.removeFirstBox("SKU-A");

        // Then: Should return null
        assertNull(removed);
    }

    @Test
    void testRemoveFirstBox_NoMatchingSKU_ReturnsNull() {
        // Given: Bay with SKU-A boxes only
        bay.addBox(createBox("BOX-001", null));

        // When: Try to remove SKU-B
        Box removed = bay.removeFirstBox("SKU-B");

        // Then: Should return null
        assertNull(removed);
        assertEquals(1, bay.getCurrentBoxCount()); // Box still there
    }

    @Test
    void testRemoveFirstBox_MultipleDispatches_MaintainsFEFO() {
        // Given: Bay with 4 boxes
        bay.addBox(createBox("BOX-1", LocalDate.of(2025, 11, 1)));
        bay.addBox(createBox("BOX-2", LocalDate.of(2025, 11, 15)));
        bay.addBox(createBox("BOX-3", LocalDate.of(2025, 12, 1)));
        bay.addBox(createBox("BOX-4", null));

        // When: Remove 3 times
        assertEquals("BOX-1", bay.removeFirstBox("SKU-A").getBoxId());
        assertEquals("BOX-2", bay.removeFirstBox("SKU-A").getBoxId());
        assertEquals("BOX-3", bay.removeFirstBox("SKU-A").getBoxId());

        // Then: Only non-perishable remains
        assertEquals(1, bay.getCurrentBoxCount());
        assertEquals("BOX-4", bay.getBoxes().get(0).getBoxId());
    }

    // ==================== CAPACITY MANAGEMENT (2 tests) ====================

    @Test
    void testGetAvailableCapacity_PartiallyFilled_CorrectValue() {
        // Given: Bay with capacity 10
        bay.addBox(createBox("BOX-1", null));
        bay.addBox(createBox("BOX-2", null));
        bay.addBox(createBox("BOX-3", null));

        // Then: Available capacity should be 7
        assertEquals(7, bay.getAvailableCapacity());
        assertFalse(bay.isFull());
        assertTrue(bay.hasAvailableSpace());
    }

    @Test
    void testIsFull_WhenAtCapacity_ReturnsTrue() {
        // Given: Fill bay to capacity
        for (int i = 0; i < CAPACITY; i++) {
            bay.addBox(createBox("BOX-" + i, null));
        }

        // Then: Should be full
        assertTrue(bay.isFull());
        assertFalse(bay.hasAvailableSpace());
        assertEquals(0, bay.getAvailableCapacity());
    }

    // ==================== HELPER METHODS ====================

    private Box createBox(String boxId, LocalDate expiryDate) {
        return new Box(boxId, "SKU-A", 10, expiryDate, Instant.now(), "WAGON-1");
    }
}

