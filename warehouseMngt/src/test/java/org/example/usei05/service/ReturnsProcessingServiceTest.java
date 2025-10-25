package org.example.usei05.service;

import org.example.repository.ItemRepository;
import org.example.repository.WarehouseRepository;
import org.example.domain.Warehouse;
import org.example.results.ProcessingResult;
import org.example.service.ReturnsProcessingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Minimal tests for ReturnsProcessingService.
 * Only uses methods that actually exist in ProcessingResult.
 */
class ReturnsProcessingServiceTest {

    private ReturnsProcessingService service;

    @BeforeEach
    void setUp() {
        ItemRepository itemRepo = new ItemRepository() {
            public boolean existsSku(String sku) {
                return true;
            }
        };

        WarehouseRepository warehouseRepo = new WarehouseRepository() {
            public Warehouse findDefault() {
                return new Warehouse("TEST");
            }

            public void save(Warehouse warehouse) {
                // no-op
            }
        };

        service = new ReturnsProcessingService(itemRepo, warehouseRepo);
    }

    @Test
    void testServiceCreation() {
        assertNotNull(service, "Service should be created");
    }

    @Test
    void testProcessReturns_InvalidFile_ReturnsResult() {
        ProcessingResult result = service.processReturns("non-existent-file.csv");

        // ✅ Só verifica que não é null e que total é 0 (erro)
        assertNotNull(result, "Should return ProcessingResult, not null");
        assertEquals(0, result.getTotalProcessed(), "Should have 0 processed for invalid file");
    }

    @Test
    void testProcessReturns_NullFilePath_ReturnsResult() {
        ProcessingResult result = service.processReturns(null);

        assertNotNull(result, "Should return ProcessingResult, not null");
        assertEquals(0, result.getTotalProcessed(), "Should have 0 processed for null path");
    }

    @Test
    void testProcessReturns_EmptyFilePath_ReturnsResult() {
        ProcessingResult result = service.processReturns("");

        assertNotNull(result, "Should return ProcessingResult, not null");
        assertEquals(0, result.getTotalProcessed(), "Should have 0 processed for empty path");
    }

    @Test
    void testProcessingResult_Constructor() {
        // Test that ProcessingResult can be created
        ProcessingResult result = new ProcessingResult(10, 5, 3, 2, 0);

        assertNotNull(result);
        assertEquals(10, result.getTotalProcessed());
    }
}