package org.example.usei01.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.example.domain.Bay;
import org.example.domain.Box;
import org.example.domain.Warehouse;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * CRITICAL TESTS for Warehouse - Focus on bay management and FEFO/FIFO dispatch
 * Estimated: 15 tests (~2-3h implementation)
 */
class WarehouseTest {

    private Warehouse warehouse;
    private static final String WAREHOUSE_ID = "WH-001";

    @BeforeEach
    void setUp() {
        warehouse = new Warehouse(WAREHOUSE_ID);
    }

    // ==================== CONSTRUCTION & VALIDATION (3 tests) ====================

    @Test
    void testWarehouseCreation_ValidId_Success() {
        Warehouse wh = new Warehouse("WH-TEST");

        assertEquals("WH-TEST", wh.getWarehouseId());
        assertEquals(0, wh.getBayCount());
        assertTrue(wh.getAllBays().isEmpty());
    }

    @Test
    void testWarehouseCreation_NullId_ThrowsException() {
        assertThrows(IllegalArgumentException.class,
                () -> new Warehouse(null));
    }

    @Test
    void testWarehouseCreation_EmptyId_ThrowsException() {
        assertThrows(IllegalArgumentException.class,
                () -> new Warehouse("   "));
    }

    // ==================== BAY MANAGEMENT (5 tests) ====================

    @Test
    void testAddBay_ValidBay_Success() {
        // Given: New bay for this warehouse
        Bay bay = new Bay(WAREHOUSE_ID, 1, 1, 10);

        // When: Add bay
        warehouse.addBay(bay);

        // Then: Bay should be in warehouse
        assertEquals(1, warehouse.getBayCount());
        assertEquals(bay, warehouse.getBay(1, 1));
    }

    @Test
    void testAddBay_DifferentWarehouseId_ThrowsException() {
        // Given: Bay from different warehouse
        Bay bay = new Bay("WH-OTHER", 1, 1, 10);

        // Then: Adding should fail
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> warehouse.addBay(bay));
        assertTrue(ex.getMessage().contains("doesn't match"));
    }

    @Test
    void testAddBay_DuplicateLocation_ThrowsException() {
        // Given: Two bays at same location
        Bay bay1 = new Bay(WAREHOUSE_ID, 1, 1, 10);
        Bay bay2 = new Bay(WAREHOUSE_ID, 1, 1, 20); // Same location!

        // When: Add first bay
        warehouse.addBay(bay1);

        // Then: Adding second should fail
        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> warehouse.addBay(bay2));
        assertTrue(ex.getMessage().contains("already exists"));
    }

    @Test
    void testAddBay_MultipleBays_AllAdded() {
        // Given: Multiple bays
        warehouse.addBay(new Bay(WAREHOUSE_ID, 1, 1, 10));
        warehouse.addBay(new Bay(WAREHOUSE_ID, 1, 2, 10));
        warehouse.addBay(new Bay(WAREHOUSE_ID, 2, 1, 10));

        // Then: All should be accessible
        assertEquals(3, warehouse.getBayCount());
        assertNotNull(warehouse.getBay(1, 1));
        assertNotNull(warehouse.getBay(1, 2));
        assertNotNull(warehouse.getBay(2, 1));
    }

    @Test
    void testGetBay_NonExistent_ReturnsNull() {
        // Given: Warehouse with one bay
        warehouse.addBay(new Bay(WAREHOUSE_ID, 1, 1, 10));

        // Then: Getting non-existent bay returns null
        assertNull(warehouse.getBay(99, 99));
    }

    // ==================== BOX PLACEMENT & FEFO (4 tests) ====================

    @Test
    void testAddBox_AutoSelectsBay_Success() {
        // Given: Warehouse with one empty bay
        warehouse.addBay(new Bay(WAREHOUSE_ID, 1, 1, 10));
        Box box = createBox("BOX-001", "MILK", LocalDate.of(2025, 11, 1));

        // When: Add box (auto-select bay)
        warehouse.addBox(box);

        // Then: Box should be in the bay
        Bay bay = warehouse.getBay(1, 1);
        assertEquals(1, bay.getCurrentBoxCount());
        assertTrue(bay.containsSku("MILK"));
    }

    @Test
    void testAddBox_PrefersSameSKU_Consolidation() {
        // Given: Two bays, one already has MILK
        Bay bay1 = new Bay(WAREHOUSE_ID, 1, 1, 10);
        Bay bay2 = new Bay(WAREHOUSE_ID, 1, 2, 10);
        warehouse.addBay(bay1);
        warehouse.addBay(bay2);

        // Add MILK to bay1
        Box firstMilk = createBox("BOX-001", "MILK", LocalDate.of(2025, 11, 1));
        warehouse.addBox(firstMilk);

        // When: Add another MILK box
        Box secondMilk = createBox("BOX-002", "MILK", LocalDate.of(2025, 11, 5));
        warehouse.addBox(secondMilk);

        // Then: Both should be in bay1 (consolidation)
        assertEquals(2, bay1.getCurrentBoxCount());
        assertEquals(0, bay2.getCurrentBoxCount());
    }

    @Test
    void testAddBox_NoAvailableBay_ThrowsException() {
        // Given: Warehouse with one bay at full capacity
        Bay bay = new Bay(WAREHOUSE_ID, 1, 1, 2);
        warehouse.addBay(bay);
        warehouse.addBox(createBox("BOX-001", "MILK", null));
        warehouse.addBox(createBox("BOX-002", "MILK", null));

        // Then: Adding third box should fail
        Box overflow = createBox("BOX-003", "MILK", null);
        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> warehouse.addBox(overflow));
        assertTrue(ex.getMessage().contains("No available bay"));
    }

    @Test
    void testAddBox_MaintainsFEFOAcrossBays() {
        // Given: Three bays in different aisles
        warehouse.addBay(new Bay(WAREHOUSE_ID, 1, 1, 5));
        warehouse.addBay(new Bay(WAREHOUSE_ID, 2, 1, 5));
        warehouse.addBay(new Bay(WAREHOUSE_ID, 3, 1, 5));

        // When: Add boxes with different expiry dates
        warehouse.addBox(createBox("BOX-001", "MILK", LocalDate.of(2025, 12, 1)));
        warehouse.addBox(createBox("BOX-002", "MILK", LocalDate.of(2025, 11, 1))); // Earlier!
        warehouse.addBox(createBox("BOX-003", "MILK", LocalDate.of(2025, 11, 15)));

        // Then: All should be in bay (1,1) due to SKU consolidation
        Bay bay = warehouse.getBay(1, 1);
        assertEquals(3, bay.getCurrentBoxCount());

        // Verify FEFO order
        List<Box> boxes = bay.getBoxes();
        assertEquals("BOX-002", boxes.get(0).getBoxId()); // Nov 1
        assertEquals("BOX-003", boxes.get(1).getBoxId()); // Nov 15
        assertEquals("BOX-001", boxes.get(2).getBoxId()); // Dec 1
    }

    // ==================== QUERY OPERATIONS (3 tests) ====================

    @Test
    void testGetBaysWithSku_MultipleMatches_SortedByLocation() {
        // Given: Three bays with MILK in different locations
        Bay bay13 = new Bay(WAREHOUSE_ID, 1, 3, 5);
        Bay bay11 = new Bay(WAREHOUSE_ID, 1, 1, 5);
        Bay bay21 = new Bay(WAREHOUSE_ID, 2, 1, 5);

        warehouse.addBay(bay13);
        warehouse.addBay(bay11);
        warehouse.addBay(bay21);

        // Add MILK to all three (in random order)
        bay13.addBox(createBox("BOX-A", "MILK", null));
        bay21.addBox(createBox("BOX-B", "MILK", null));
        bay11.addBox(createBox("BOX-C", "MILK", null));

        // When: Get bays with MILK
        List<Bay> baysWithMilk = warehouse.getBaysWithSku("MILK");

        // Then: Should be sorted (aisle, bay ascending)
        assertEquals(3, baysWithMilk.size());
        assertEquals(bay11, baysWithMilk.get(0)); // (1,1)
        assertEquals(bay13, baysWithMilk.get(1)); // (1,3)
        assertEquals(bay21, baysWithMilk.get(2)); // (2,1)
    }

    @Test
    void testGetBaysWithSkuSorted_SortsByFEFO_ThenLocation() {
        // Given: Two bays with MILK at different expiry dates
        Bay bay1 = new Bay(WAREHOUSE_ID, 1, 1, 5);
        Bay bay2 = new Bay(WAREHOUSE_ID, 1, 2, 5);
        warehouse.addBay(bay1);
        warehouse.addBay(bay2);

        // bay2 has earlier expiry (should come first despite higher bay number)
        bay1.addBox(createBox("BOX-LATE", "MILK", LocalDate.of(2025, 12, 1)));
        bay2.addBox(createBox("BOX-EARLY", "MILK", LocalDate.of(2025, 11, 1)));

        // When: Get sorted bays
        List<Bay> sorted = warehouse.getBaysWithSkuSorted("MILK");

        // Then: bay2 should come first (earlier expiry wins)
        assertEquals(2, sorted.size());
        assertEquals(bay2, sorted.get(0)); // Earlier expiry
        assertEquals(bay1, sorted.get(1)); // Later expiry
    }

    @Test
    void testGetBaysWithSku_NoMatches_ReturnsEmptyList() {
        // Given: Bays with only MILK
        warehouse.addBay(new Bay(WAREHOUSE_ID, 1, 1, 5));
        warehouse.addBox(createBox("BOX-001", "MILK", null));

        // When: Search for RICE
        List<Bay> baysWithRice = warehouse.getBaysWithSku("RICE");

        // Then: Should be empty
        assertTrue(baysWithRice.isEmpty());
    }

    // ==================== HELPER METHODS ====================

    private Box createBox(String boxId, String sku, LocalDate expiryDate) {
        return new Box(boxId, sku, 10, expiryDate, Instant.now(), "WAGON-1");
    }
}

