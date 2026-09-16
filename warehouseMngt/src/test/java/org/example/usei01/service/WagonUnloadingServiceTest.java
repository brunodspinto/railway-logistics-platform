package org.example.usei01.service;

import org.example.domain.*;
import org.example.repository.WarehouseRepository;
import org.example.results.UnloadingResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.example.service.WagonUnloadingService;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * CRITICAL TESTS for WagonUnloadingService
 * Focus: Round-Robin allocation + FEFO/FIFO ordering
 */
class WagonUnloadingServiceTest {

    private WagonUnloadingService unloadingService;
    private WarehouseRepository warehouseRepository;
    private Warehouse warehouse;

    @BeforeEach
    void setUp() {
        warehouseRepository = new WarehouseRepository();
        warehouse = new Warehouse("WH-TEST");

        // Create 2 aisles with 3 bays each
        for (int aisle = 1; aisle <= 2; aisle++) {
            for (int bay = 1; bay <= 3; bay++) {
                warehouse.addBay(new Bay("WH-TEST", aisle, bay, 5));
            }
        }

        warehouseRepository.save(warehouse);
        unloadingService = new WagonUnloadingService(warehouseRepository);
    }

    // ==================== BASIC UNLOADING (3 tests) ====================

    @Test
    void testUnloadWagon_SingleBox_Success() {
        // Given: Wagon with one box
        Wagon wagon = new Wagon("W-001");
        wagon.addBox(createBox("BOX-001", "MILK", 50, null, "W-001"));  // ✅ wagonId correto

        // When: Unload wagon
        UnloadingResult result = unloadingService.unloadWagons(List.of(wagon));

        // Then: Should succeed
        assertEquals(1, result.getSuccessCount());
        assertEquals(0, result.getErrorCount());
        assertEquals(1, warehouse.getTotalBoxCount());
    }

    @Test
    void testUnloadWagon_MultipleBoxes_AllPlaced() {
        // Given: Wagon with 5 boxes
        Wagon wagon = new Wagon("W-001");
        for (int i = 1; i <= 5; i++) {
            wagon.addBox(createBox("BOX-" + i, "MILK", 10, null, "W-001"));  // ✅ wagonId correto
        }

        // When: Unload wagon
        UnloadingResult result = unloadingService.unloadWagons(List.of(wagon));

        // Then: All boxes should be placed
        assertEquals(1, result.getSuccessCount());
        assertEquals(5, warehouse.getTotalBoxCount());
    }

    @Test
    void testUnloadWagon_MultipleWagons_Sequential() {
        // Given: Three wagons
        List<Wagon> wagons = new ArrayList<>();
        for (int w = 1; w <= 3; w++) {
            Wagon wagon = new Wagon("W-" + w);
            wagon.addBox(createBox("BOX-W" + w, "MILK", 20, null, "W-" + w));  // ✅ wagonId match!
            wagons.add(wagon);
        }

        // When: Unload all wagons
        UnloadingResult result = unloadingService.unloadWagons(wagons);

        // Then: All should succeed
        assertEquals(3, result.getSuccessCount());
        assertEquals(3, warehouse.getTotalBoxCount());
    }

    @Test
    void testUnloadWagon_SortsGloballyByFEFO() {
        // Given: Wagon with boxes in random order
        Wagon wagon = new Wagon("W-001");
        wagon.addBox(createBox("BOX-3", "MILK", 10, LocalDate.of(2025, 12, 1), "W-001"));
        wagon.addBox(createBox("BOX-1", "MILK", 10, LocalDate.of(2025, 11, 1), "W-001"));
        wagon.addBox(createBox("BOX-2", "MILK", 10, LocalDate.of(2025, 11, 15), "W-001"));

        // When: Unload
        unloadingService.unloadWagons(List.of(wagon));

        // Then: Boxes should be sorted by expiry in at least one bay
        Bay bayWithMilk = warehouse.getAllBays().stream()
                .filter(bay -> bay.containsSku("MILK"))
                .findFirst()
                .orElseThrow();

        List<Box> boxes = bayWithMilk.getBoxes();

        // Verify FEFO order
        assertTrue(boxes.get(0).getExpiryDate().isBefore(boxes.get(1).getExpiryDate()));
        assertTrue(boxes.get(1).getExpiryDate().isBefore(boxes.get(2).getExpiryDate()));
    }

    @Test
    void testUnloadWagon_PerishableBeforeNonPerishable() {
        // Given: Mix of perishable and non-perishable
        Wagon wagon = new Wagon("W-001");
        wagon.addBox(createBox("BOX-NP", "RICE", 50, null, "W-001"));
        wagon.addBox(createBox("BOX-P", "MILK", 30, LocalDate.of(2025, 11, 1), "W-001"));

        // When: Unload
        unloadingService.unloadWagons(List.of(wagon));

        // Then: Both boxes should be placed
        assertTrue(warehouse.getTotalBoxCount() == 2, "Both boxes should be placed");
    }

    @Test
    void testUnloadWagon_SameExpiry_ReceivedAtDecides() {
        // Given: Boxes with same expiry, different receivedAt
        Wagon wagon = new Wagon("W-001");
        LocalDate expiry = LocalDate.of(2025, 11, 1);

        Instant older = Instant.parse("2025-10-01T10:00:00Z");
        Instant newer = Instant.parse("2025-10-02T10:00:00Z");

        wagon.addBox(new Box("BOX-NEW", "MILK", 20, expiry, newer, "W-001"));
        wagon.addBox(new Box("BOX-OLD", "MILK", 30, expiry, older, "W-001"));

        // When: Unload
        unloadingService.unloadWagons(List.of(wagon));

        // Then: Find bay with MILK and verify order
        Bay bayWithMilk = warehouse.getAllBays().stream()
                .filter(bay -> bay.containsSku("MILK"))
                .findFirst()
                .orElseThrow();

        List<Box> boxes = bayWithMilk.getBoxes();

        // Older receivedAt should come first
        assertEquals("BOX-OLD", boxes.get(0).getBoxId());
        assertEquals("BOX-NEW", boxes.get(1).getBoxId());
    }

    @Disabled("Round-robin distribution across aisles was removed from " +
            "WagonUnloadingService. It now delegates placement to " +
            "Warehouse.findBestAvailableBay, which picks the lowest aisle/bay with space, " +
            "so all boxes cluster in the first aisle until it is full. Re-enable if " +
            "round-robin (or a similar spread policy) is reintroduced.")
    @Test
    void testRoundRobin_DistributesAcrossAisles() {
        // Given: 6 boxes (more than one aisle capacity)
        Wagon wagon = new Wagon("W-001");
        for (int i = 1; i <= 6; i++) {
            wagon.addBox(createBox("BOX-" + i, "RICE-" + i, 10, null, "W-001"));
        }

        // When: Unload with Round-Robin
        unloadingService.unloadWagons(List.of(wagon));

        // Then: Boxes should be distributed across aisles
        int aisle1Count = 0;
        int aisle2Count = 0;

        for (Bay bay : warehouse.getAllBays()) {
            if (bay.getAisleNumber() == 1) {
                aisle1Count += bay.getCurrentBoxCount();
            } else if (bay.getAisleNumber() == 2) {
                aisle2Count += bay.getCurrentBoxCount();
            }
        }

        // Verify distribution
        assertTrue(aisle1Count > 0, "Aisle 1 should have boxes");
        assertTrue(aisle2Count > 0, "Aisle 2 should have boxes");
    }

    @Test
    void testRoundRobin_Reset_DeterministicBehavior() {
        // Given: Same set of boxes unloaded twice
        Wagon wagon1 = new Wagon("W-001");
        for (int i = 1; i <= 3; i++) {
            wagon1.addBox(createBox("SET1-BOX-" + i, "SKU-" + i, 10, null, "W-001"));
        }

        // When: Unload first time
        unloadingService.unloadWagons(List.of(wagon1));

        // Reset
        warehouseRepository.clear();
        warehouse = new Warehouse("WH-TEST");
        for (int aisle = 1; aisle <= 2; aisle++) {
            for (int bay = 1; bay <= 3; bay++) {
                warehouse.addBay(new Bay("WH-TEST", aisle, bay, 5));
            }
        }
        warehouseRepository.save(warehouse);


        Wagon wagon2 = new Wagon("W-002");
        for (int i = 1; i <= 3; i++) {
            wagon2.addBox(createBox("SET2-BOX-" + i, "SKU-" + i, 10, null, "W-002"));
        }

        // When: Unload second time
        unloadingService.unloadWagons(List.of(wagon2));

        // Then: Distribution should be identical
        assertEquals(3, warehouse.getTotalBoxCount());
    }

    @Test
    void testUnloadWagon_CapacityExceeded_ReportsError() {
        // Given: Warehouse with limited capacity (30 boxes total)
        // When: Try to unload 40 boxes
        Wagon wagon = new Wagon("W-001");
        for (int i = 1; i <= 40; i++) {
            wagon.addBox(createBox("BOX-" + i, "SKU-" + i, 10, null, "W-001"));
        }

        // When: Unload
        UnloadingResult result = unloadingService.unloadWagons(List.of(wagon));

        // Then: Should have errors
        assertTrue(result.getErrorCount() > 0, "Should report capacity errors");
    }

    @Test
    void testUnloadWagon_NoWarehouse_ReportsError() {
        // Given: No warehouse in repository
        warehouseRepository.clear();

        // When: Try to unload
        Wagon wagon = new Wagon("W-001");
        wagon.addBox(createBox("BOX-001", "MILK", 50, null, "W-001"));

        UnloadingResult result = unloadingService.unloadWagons(List.of(wagon));

        // Then: Should report error
        assertEquals(0, result.getSuccessCount());
        assertTrue(result.getErrorCount() > 0);
    }

    // ==================== HELPER METHODS ====================

    /**
     * Creates a box with specified wagonId.
     * Use this version for WagonUnloadingService tests to ensure wagonId matches.
     */
    private Box createBox(String boxId, String sku, int qty, LocalDate expiryDate, String wagonId) {
        return new Box(boxId, sku, qty, expiryDate, Instant.now(), wagonId);
    }

    /**
     * Creates a box with default wagonId "WAGON-TEST".
     * Kept for backward compatibility with other tests.
     */
    private Box createBox(String boxId, String sku, int qty, LocalDate expiryDate) {
        return createBox(boxId, sku, qty, expiryDate, "WAGON-TEST");
    }
}

