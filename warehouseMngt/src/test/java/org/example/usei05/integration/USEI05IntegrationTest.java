package org.example.usei05.integration;

import org.example.domain.*;
import org.example.repository.ItemRepository;
import org.example.repository.WarehouseRepository;
import org.example.results.ProcessingResult;
import org.example.service.ReturnsProcessingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for USEI05 - Returns & Quarantine
 */
class USEI05IntegrationTest {

    @TempDir
    Path tempDir;

    private ItemRepository itemRepository;
    private WarehouseRepository warehouseRepository;
    private ReturnsProcessingService processingService;
    private Warehouse warehouse;
    private Path auditLogPath;

    @BeforeEach
    void setUp() throws IOException {
        // Setup repositories
        itemRepository = new ItemRepository();
        warehouseRepository = new WarehouseRepository();

        // Setup test items
        itemRepository.save(new Item("SKU001", "Product A", "Electronics", "kg", 1.0, 2.0));
        itemRepository.save(new Item("SKU002", "Product B", "Food", "kg", 0.5, 1.5));

        // Setup warehouse (must be named for findDefault() to work)
        warehouse = new Warehouse("WH-DEFAULT");

        for (int aisle = 1; aisle <= 2; aisle++) {
            for (int bay = 1; bay <= 3; bay++) {
                warehouse.addBay(new Bay("WH-DEFAULT", aisle, bay, 100));
            }
        }
        warehouseRepository.save(warehouse);

        // Clear audit log before each test
        auditLogPath = Paths.get("logs/audit-log.txt");
        Files.createDirectories(auditLogPath.getParent());
        if (Files.exists(auditLogPath)) {
            Files.delete(auditLogPath);
        }

        // Setup processing service
        processingService = new ReturnsProcessingService(itemRepository, warehouseRepository);
    }

    @AfterEach
    void tearDown() throws IOException {
        // Clean up audit log after each test
        if (Files.exists(auditLogPath)) {
            Files.delete(auditLogPath);
        }
    }

    @Test
    void testCompleteFlow_ProcessReturnsFromCSV() throws IOException {
        System.out.println("\n=== TEST 1: Complete Flow - CSV → Process → Audit ===");

        // Create returns CSV file
        File returnsFile = createReturnsFile(
                "returnId,sku,qty,reason,timestamp,expiryDate",
                "R001,SKU001,5,customer_remorse,2025-10-22T09:00:00,2025-12-31",
                "R002,SKU002,3,damaged,2025-10-22T10:00:00,",
                "R003,SKU001,2,cycle_count,2025-10-22T11:00:00,2026-01-15"
        );

        System.out.println("✅ Created CSV with 3 returns");

        // Process returns
        ProcessingResult result = processingService.processReturns(returnsFile.getAbsolutePath());

        System.out.println("\n📊 Processing Results:");
        System.out.println("   - Total: " + result.getTotalProcessed());
        System.out.println("   - Restocked: " + result.getRestockedCount());
        System.out.println("   - Discarded: " + result.getDiscardedCount());
        System.out.println("   - Partial: " + result.getPartialRestockCount());
        System.out.println("   - Errors: " + result.getErrorsCount());

        // Verify results
        assertEquals(3, result.getTotalProcessed(), "Should process 3 returns");

        // Verify audit log was created
        assertTrue(Files.exists(auditLogPath), "Audit log should exist");

        List<String> logLines = Files.readAllLines(auditLogPath);
        System.out.println("\n📝 Audit Log Content:");
        logLines.forEach(line -> System.out.println("   " + line));

        assertEquals(3, logLines.size(), "Audit log should have 3 entries");

        System.out.println("✅ Test completed");
    }

    @Test
    void testLIFOOrdering_QuarantineProcessesLatestFirst() throws IOException {
        System.out.println("\n=== TEST 2: LIFO Ordering - Latest Processed First ===");

        // Create returns with explicit timestamps
        File returnsFile = createReturnsFile(
                "returnId,sku,qty,reason,timestamp,expiryDate",
                "R001,SKU001,5,cycle_count,2025-10-22T09:00:00,",
                "R002,SKU001,3,cycle_count,2025-10-22T10:00:00,",
                "R003,SKU001,2,cycle_count,2025-10-22T11:00:00,"
        );

        System.out.println("✅ Created CSV with timestamps:");
        System.out.println("   - R001: 09:00 (oldest)");
        System.out.println("   - R002: 10:00");
        System.out.println("   - R003: 11:00 (latest)");

        // Process returns
        ProcessingResult result = processingService.processReturns(returnsFile.getAbsolutePath());

        assertEquals(3, result.getTotalProcessed());
        System.out.println("✅ All returns processed");

        // Verify audit log exists and read it
        assertTrue(Files.exists(auditLogPath), "Audit log should exist");
        List<String> logLines = Files.readAllLines(auditLogPath);

        System.out.println("\n📝 Audit Log Order:");
        for (int i = 0; i < logLines.size(); i++) {
            System.out.println("   " + (i + 1) + ". " + logLines.get(i));
        }

        // Check if order is LIFO
        boolean isLIFO = logLines.get(0).contains("R003") &&
                logLines.get(1).contains("R002") &&
                logLines.get(2).contains("R001");

        if (isLIFO) {
            System.out.println("✅ LIFO order verified: R003 → R002 → R001");
        } else {
            System.out.println("⚠️  Order is: " +
                    extractReturnId(logLines.get(0)) + " → " +
                    extractReturnId(logLines.get(1)) + " → " +
                    extractReturnId(logLines.get(2)));
        }

        assertTrue(isLIFO, "Processing order should be LIFO (latest first)");
    }

    @Test
    void testProcessingResults_VerifyCounters() throws IOException {
        System.out.println("\n=== TEST 3: Verify Processing Counters ===");

        // Create returns with known outcomes
        File returnsFile = createReturnsFile(
                "returnId,sku,qty,reason,timestamp,expiryDate",
                "R-CYCLE,SKU001,10,cycle_count,2025-10-22T10:00:00,2025-12-31",
                "R-DAMAGED,SKU001,5,damaged,2025-10-22T11:00:00,"
        );

        System.out.println("✅ Created CSV with 2 returns:");
        System.out.println("   - R-CYCLE: cycle_count");
        System.out.println("   - R-DAMAGED: damaged");

        // Process returns
        ProcessingResult result = processingService.processReturns(returnsFile.getAbsolutePath());

        System.out.println("\n📊 Results:");
        System.out.println("   - Total: " + result.getTotalProcessed());
        System.out.println("   - Restocked: " + result.getRestockedCount());
        System.out.println("   - Discarded: " + result.getDiscardedCount());
        System.out.println("   - Errors: " + result.getErrorsCount());

        // Verify
        assertEquals(2, result.getTotalProcessed(), "Should process 2 returns");
        assertEquals(0, result.getErrorsCount(), "Should have no errors");

        // Verify audit log
        assertTrue(Files.exists(auditLogPath), "Audit log should exist");
        List<String> logLines = Files.readAllLines(auditLogPath);

        System.out.println("\n📝 Audit Log:");
        logLines.forEach(line -> System.out.println("   " + line));

        assertEquals(2, logLines.size(), "Audit log should have 2 entries");

        System.out.println("✅ Test completed");
    }

    // ========== Helper Methods ==========

    private File createReturnsFile(String... lines) throws IOException {
        File csvFile = tempDir.resolve("returns.csv").toFile();
        try (FileWriter writer = new FileWriter(csvFile)) {
            for (String line : lines) {
                writer.write(line + "\n");
            }
        }
        return csvFile;
    }

    private String extractReturnId(String logLine) {
        // Extract returnId from log line
        if (logLine.contains("returnId=")) {
            int start = logLine.indexOf("returnId=") + 9;
            int end = logLine.indexOf(" ", start);
            if (end == -1) end = logLine.indexOf("|", start);
            if (end == -1) end = logLine.length();
            return logLine.substring(start, end).trim();
        }
        return "UNKNOWN";
    }
}