package org.example.usei01.service;

import org.example.domain.*;
import org.example.repository.WarehouseRepository;
import org.example.results.DispatchResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.example.service.InventoryService;

import java.time.Instant;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * CRITICAL TESTS for InventoryService
 * Focus: Dispatch (FEFO/FIFO) + Relocation
 */
class InventoryServiceTest {

    private InventoryService inventoryService;
    private WarehouseRepository warehouseRepository;
    private Warehouse warehouse;

    @BeforeEach
    void setUp() {
        // Setup in-memory repository
        warehouseRepository = new WarehouseRepository();
        warehouse = new Warehouse("WH-TEST");

        // Create test bays
        warehouse.addBay(new Bay("WH-TEST", 1, 1, 10));
        warehouse.addBay(new Bay("WH-TEST", 1, 2, 10));
        warehouse.addBay(new Bay("WH-TEST", 2, 1, 10));

        warehouseRepository.save(warehouse);

        inventoryService = new InventoryService(warehouseRepository);
    }

    // ==================== DISPATCH - FEFO/FIFO (6 tests) ====================

    @Test
    void testDispatch_SingleBay_FullQuantity_Success() {
        // Given: Bay with one box (50 units)
        Bay bay = warehouse.getBay(1, 1);
        bay.addBox(createBox("BOX-001", "MILK", 50, LocalDate.of(2025, 11, 1)));

        // When: Dispatch 50 units
        DispatchResult result = inventoryService.dispatchBoxes("MILK", 50);

        // Then: Should dispatch all
        assertEquals(50, result.getTotalDispatched());
        assertTrue(bay.isEmpty(), "Bay should be empty after full dispatch");
    }

    @Test
    void testDispatch_SingleBay_PartialQuantity_Success() {
        // Given: Bay with one box (100 units)
        Bay bay = warehouse.getBay(1, 1);
        Box box = createBox("BOX-001", "MILK", 100, LocalDate.of(2025, 11, 1));
        bay.addBox(box);

        // When: Dispatch 30 units (partial)
        DispatchResult result = inventoryService.dispatchBoxes("MILK", 30);

        // Then: Box should have 70 remaining
        assertEquals(30, result.getTotalDispatched());
        assertEquals(70, box.getQuantity(), "Box should have 70 units remaining");
        assertFalse(bay.isEmpty(), "Bay should not be empty");
    }

    @Test
    void testDispatch_MultipleBays_FEFOOrder_Maintained() {
        // Given: Two bays with MILK (different expiry dates)
        Bay bay1 = warehouse.getBay(1, 1);
        Bay bay2 = warehouse.getBay(1, 2);

        bay1.addBox(createBox("BOX-LATE", "MILK", 50, LocalDate.of(2025, 12, 1)));
        bay2.addBox(createBox("BOX-EARLY", "MILK", 50, LocalDate.of(2025, 11, 1)));

        // When: Dispatch 60 units
        DispatchResult result = inventoryService.dispatchBoxes("MILK", 60);

        // Then: Should dispatch from EARLY first (50), then LATE (10)
        assertEquals(60, result.getTotalDispatched());

        // Bay2 should be empty (EARLY fully consumed)
        assertTrue(bay2.isEmpty(), "Bay with earlier expiry should be emptied first");

        // Bay1 should have 40 remaining (50 - 10)
        Box remainingBox = bay1.peekFirstBox("MILK");
        assertNotNull(remainingBox);
        assertEquals(40, remainingBox.getQuantity());
    }

    @Test
    void testDispatch_SpansMultipleBays_FEFO_GlobalOrder() {
        // Given: Three bays with MILK at different expiry dates
        Bay bay1 = warehouse.getBay(1, 1);
        Bay bay2 = warehouse.getBay(1, 2);
        Bay bay3 = warehouse.getBay(2, 1);

        bay1.addBox(createBox("BOX-1", "MILK", 30, LocalDate.of(2025, 11, 15)));
        bay2.addBox(createBox("BOX-2", "MILK", 40, LocalDate.of(2025, 11, 1))); // Earliest!
        bay3.addBox(createBox("BOX-3", "MILK", 30, LocalDate.of(2025, 12, 1)));

        // When: Dispatch 80 units
        DispatchResult result = inventoryService.dispatchBoxes("MILK", 80);

        // Then: Order should be BOX-2 (40) → BOX-1 (30) → BOX-3 (10)
        assertEquals(80, result.getTotalDispatched());

        // Verify dispatch order by checking remaining quantities
        assertTrue(bay2.isEmpty(), "Earliest expiry bay should be empty");
        assertTrue(bay1.isEmpty(), "Second expiry bay should be empty");
        assertEquals(20, bay3.peekFirstBox("MILK").getQuantity(), "Last bay should have 20 remaining");
    }

    @Test
    void testDispatch_InsufficientStock_PartialFulfillment() {
        // Given: Only 30 units available
        Bay bay = warehouse.getBay(1, 1);
        bay.addBox(createBox("BOX-001", "MILK", 30, null));

        // When: Request 100 units
        DispatchResult result = inventoryService.dispatchBoxes("MILK", 100);

        // Then: Should dispatch only 30
        assertEquals(30, result.getTotalDispatched());
        assertTrue(bay.isEmpty());
    }

    @Test
    void testDispatch_NoStock_ReturnsZero() {
        // Given: Empty warehouse (no MILK)

        // When: Try to dispatch MILK
        DispatchResult result = inventoryService.dispatchBoxes("MILK", 50);

        // Then: Should dispatch 0
        assertEquals(0, result.getTotalDispatched());
    }

    // ==================== RELOCATION (4 tests) ====================

    @Test
    void testRelocation_ValidMove_Success() {
        // Given: Box in bay (1,1)
        Bay sourceBay = warehouse.getBay(1, 1);
        Bay targetBay = warehouse.getBay(1, 2);

        Box box = createBox("BOX-001", "MILK", 50, LocalDate.of(2025, 11, 1));
        sourceBay.addBox(box);

        // When: Relocate to bay (1,2)
        Location newLocation = new Location("WH-TEST", 1, 2);
        boolean success = inventoryService.relocateBox("BOX-001", newLocation);

        // Then: Box should move
        assertTrue(success);
        assertFalse(sourceBay.containsSku("MILK"), "Source bay should be empty");
        assertTrue(targetBay.containsSku("MILK"), "Target bay should have the box");
        assertEquals(newLocation, box.getLocation(), "Box location should be updated");
    }

    @Test
    void testRelocation_MaintainsFEFO_InDestination() {
        // Given: Target bay already has boxes
        Bay sourceBay = warehouse.getBay(1, 1);
        Bay targetBay = warehouse.getBay(1, 2);

        // Add box to target (late expiry)
        targetBay.addBox(createBox("BOX-LATE", "MILK", 30, LocalDate.of(2025, 12, 1)));

        // Add box to source (early expiry) - to be relocated
        Box boxToMove = createBox("BOX-EARLY", "MILK", 40, LocalDate.of(2025, 11, 1));
        sourceBay.addBox(boxToMove);

        // When: Relocate early expiry box to target bay
        Location newLocation = new Location("WH-TEST", 1, 2);
        inventoryService.relocateBox("BOX-EARLY", newLocation);

        // Then: Early expiry should be first in target bay
        assertEquals("BOX-EARLY", targetBay.getBoxes().get(0).getBoxId());
        assertEquals("BOX-LATE", targetBay.getBoxes().get(1).getBoxId());
    }

    @Test
    void testRelocation_SameLocation_NoOp() {
        // Given: Box in bay (1,1)
        Bay bay = warehouse.getBay(1, 1);
        bay.addBox(createBox("BOX-001", "MILK", 50, null));

        // When: Try to relocate to same bay
        Location sameLocation = new Location("WH-TEST", 1, 1);
        boolean success = inventoryService.relocateBox("BOX-001", sameLocation);

        // Then: Should return false (no move needed)
        assertFalse(success, "Relocating to same bay should be no-op");
    }

    @Test
    void testRelocation_TargetBayFull_ThrowsException() {
        // Given: Source bay with box, target bay at full capacity
        Bay sourceBay = warehouse.getBay(1, 1);
        Bay targetBay = warehouse.getBay(1, 2);

        sourceBay.addBox(createBox("BOX-RELOCATE", "MILK", 50, null));

        // Fill target bay to capacity
        for (int i = 0; i < 10; i++) {
            targetBay.addBox(createBox("BOX-" + i, "RICE", 10, null));
        }

        // Then: Relocation should fail
        Location fullLocation = new Location("WH-TEST", 1, 2);
        assertThrows(IllegalStateException.class,
                () -> inventoryService.relocateBox("BOX-RELOCATE", fullLocation));
    }

    // ==================== EDGE CASES (2 tests) ====================

    @Test
    void testDispatch_NonPerishableBeforePerishable_WhenSorted() {
        // Given: Mix of perishable and non-perishable
        Bay bay = warehouse.getBay(1, 1);

        bay.addBox(createBox("BOX-NP", "MILK", 30, null)); // Non-perishable
        bay.addBox(createBox("BOX-P", "MILK", 40, LocalDate.of(2025, 11, 1))); // Perishable

        // When: Dispatch 50 units
        DispatchResult result = inventoryService.dispatchBoxes("MILK", 50);

        // Then: Should dispatch perishable first (40), then non-perishable (10)
        assertEquals(50, result.getTotalDispatched());

        Box remaining = bay.peekFirstBox("MILK");
        assertEquals("BOX-NP", remaining.getBoxId());
        assertEquals(20, remaining.getQuantity());
    }

    @Test
    void testRelocation_NonExistentBox_ThrowsException() {
        // When: Try to relocate non-existent box
        Location location = new Location("WH-TEST", 1, 1);

        // Then: Should throw BoxNotFoundException
        assertThrows(Exception.class,
                () -> inventoryService.relocateBox("BOX-GHOST", location));
    }

    // ==================== HELPER METHODS ====================

    private Box createBox(String boxId, String sku, int qty, LocalDate expiryDate) {
        return new Box(boxId, sku, qty, expiryDate, Instant.now(), "WAGON-TEST");
    }
}

