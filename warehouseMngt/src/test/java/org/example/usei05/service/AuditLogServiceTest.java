package org.example.usei05.service;

import org.example.results.InspectionResult;
import org.example.service.AuditLogService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Essential tests for AuditLogService.
 * Focus: file I/O, format validation, append behavior.
 */
class AuditLogServiceTest {

    @TempDir
    Path tempDir;

    private AuditLogService service;
    private Path logFile;

    @BeforeEach
    void setUp() {
        logFile = tempDir.resolve("test-audit.txt");
        service = new AuditLogService(logFile.toString());
    }

    // ==================== INITIALIZATION (~2 testes) ====================

    @Test
    void testInitializeLog_CreatesDirectory() {
        service.initializeLog();
        assertTrue(Files.exists(logFile.getParent()));
    }

    @Test
    void testGetLogPath_ReturnsCorrectPath() {
        assertEquals(logFile.toString(), service.getLogPath());
    }

    // ==================== LOGGING (~5 testes) ====================

    @Test
    void testLog_WritesEntry() throws IOException {
        InspectionResult result = createResult("RET001", "RESTOCK", 10, 10, 0);

        service.log(result);

        assertTrue(Files.exists(logFile));
        List<String> lines = Files.readAllLines(logFile);
        assertEquals(1, lines.size());
    }

    @Test
    void testLog_FormatCorrect() throws IOException {
        InspectionResult result = createResult("RET001", "RESTOCK", 10, 10, 0);

        service.log(result);

        String line = Files.readAllLines(logFile).get(0);

        // Expected format: "2025-10-22 15:04:17 | returnId=RET001 | sku=SKU001 | ..."
        assertTrue(line.contains("returnId=RET001"));
        assertTrue(line.contains("sku=SKU001"));
        assertTrue(line.contains("action=RESTOCK"));
        assertTrue(line.contains("qty=10"));
        assertTrue(line.contains("qtyRestocked=10"));
    }

    @Test
    void testLog_MultipleEntries_Appends() throws IOException {
        service.log(createResult("RET001", "RESTOCK", 10, 10, 0));
        service.log(createResult("RET002", "DISCARD", 5, 0, 5));
        service.log(createResult("RET003", "PARTIAL_RESTOCK", 20, 12, 8));

        List<String> lines = Files.readAllLines(logFile);
        assertEquals(3, lines.size());

        assertTrue(lines.get(0).contains("RET001"));
        assertTrue(lines.get(1).contains("RET002"));
        assertTrue(lines.get(2).contains("RET003"));
    }

    @Test
    void testLog_WithExpiryDate_Included() throws IOException {
        InspectionResult result = createResult(
                "RET004",
                "DISCARD",
                10,
                0,
                10,
                LocalDate.of(2025, 10, 15)
        );

        service.log(result);

        String line = Files.readAllLines(logFile).get(0);
        assertTrue(line.contains("expiryDate=2025-10-15"));
    }

    @Test
    void testLog_WithoutExpiryDate_NotIncluded() throws IOException {
        InspectionResult result = createResult("RET005", "RESTOCK", 10, 10, 0);

        service.log(result);

        String line = Files.readAllLines(logFile).get(0);
        assertFalse(line.contains("expiryDate="));
    }

    // ==================== EDGE CASES (~3 testes) ====================

    @Test
    void testLog_NullResult_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> service.log(null));
    }

    @Test
    void testLog_PartialRestock_BothQtyPresent() throws IOException {
        InspectionResult result = createResult("RET006", "PARTIAL_RESTOCK", 100, 60, 40);

        service.log(result);

        String line = Files.readAllLines(logFile).get(0);
        assertTrue(line.contains("qtyRestocked=60"));
        assertTrue(line.contains("qtyDiscarded=40"));
    }

    @Test
    void testLog_ManyEntries_Performance() throws IOException {
        // Write 100 entries
        for (int i = 1; i <= 100; i++) {
            service.log(createResult("RET" + String.format("%03d", i), "RESTOCK", 10, 10, 0));
        }

        List<String> lines = Files.readAllLines(logFile);
        assertEquals(100, lines.size());
    }

    // ==================== HELPER METHODS ====================

    private InspectionResult createResult(String returnId, String action,
                                          int qty, int restocked, int discarded) {
        return createResult(returnId, action, qty, restocked, discarded, null);
    }

    private InspectionResult createResult(String returnId, String action,
                                          int qty, int restocked, int discarded,
                                          LocalDate expiryDate) {
        return new InspectionResult(
                returnId,
                "SKU001",
                qty,
                action,
                restocked,
                discarded,
                "customer remorse",
                LocalDateTime.now(),
                expiryDate
        );
    }
}
