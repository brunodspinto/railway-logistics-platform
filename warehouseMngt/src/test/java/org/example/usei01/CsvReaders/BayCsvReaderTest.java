package org.example.usei01.CsvReaders;

import org.example.CsvReaders.BayCsvReader;
import org.example.domain.Bay;
import org.example.exception.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * CRITICAL TESTS for BayCsvReader
 */
class BayCsvReaderTest {

    private BayCsvReader reader;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        reader = new BayCsvReader();
    }

    @Test
    void testParse_ValidCsv_Success() throws IOException {
        // Given: Valid CSV (note: delimiter is semicolon)
        String csv = """
                warehouseId;aisle;bay;capacityBoxes
                WH-001;1;1;10
                WH-001;1;2;15
                """;
        Path file = createTempFile(csv);

        // When: Parse
        List<Bay> bays = reader.parse(file.toString());

        // Then: Should load correctly
        assertEquals(2, bays.size());
        assertEquals("WH-001", bays.get(0).getWarehouseId());
        assertEquals(1, bays.get(0).getAisleNumber());
        assertEquals(1, bays.get(0).getBayNumber());
        assertEquals(10, bays.get(0).getCapacityBoxes());
    }

    @Test
    void testParse_NegativeCapacity_ThrowsException() throws IOException {
        // Given: CSV with negative capacity
        String csv = """
                warehouseId;aisle;bay;capacityBoxes
                WH-001;1;1;-5
                """;
        Path file = createTempFile(csv);

        // Then: Should reject (Bay constructor validates)
        assertThrows(IllegalArgumentException.class,
                () -> reader.parse(file.toString()));
    }

    @Test
    void testParse_ZeroCapacity_ThrowsException() throws IOException {
        // Given: CSV with zero capacity
        String csv = """
                warehouseId;aisle;bay;capacityBoxes
                WH-001;1;1;0
                """;
        Path file = createTempFile(csv);

        // Then: Should reject
        assertThrows(IllegalArgumentException.class,
                () -> reader.parse(file.toString()));
    }

    @Test
    void testParse_InvalidFormat_ThrowsException() throws IOException {
        // Given: CSV with wrong number of fields
        String csv = """
                warehouseId;aisle;bay;capacityBoxes
                WH-001;1;1
                """;
        Path file = createTempFile(csv);

        // Then: Should reject
        ValidationException ex = assertThrows(ValidationException.class,
                () -> reader.parse(file.toString()));

        assertTrue(ex.getMessage().contains("Invalid bay CSV format"));
    }

    @Test
    void testParse_InvalidNumberFormat_ThrowsException() throws IOException {
        // Given: CSV with non-numeric values
        String csv = """
                warehouseId;aisle;bay;capacityBoxes
                WH-001;ABC;1;10
                """;
        Path file = createTempFile(csv);

        // Then: Should reject
        ValidationException ex = assertThrows(ValidationException.class,
                () -> reader.parse(file.toString()));

        assertTrue(ex.getMessage().contains("Invalid number format"));
    }

    @Test
    void testParse_EmptyWarehouseId_ThrowsException() throws IOException {
        // Given: CSV with empty warehouseId
        String csv = """
                warehouseId;aisle;bay;capacityBoxes
                ;1;1;10
                """;
        Path file = createTempFile(csv);

        // Then: Should reject (Bay constructor validates)
        assertThrows(IllegalArgumentException.class,
                () -> reader.parse(file.toString()));
    }

    // ==================== HELPER ====================

    private Path createTempFile(String content) throws IOException {
        Path file = tempDir.resolve("test.csv");
        Files.writeString(file, content);
        return file;
    }
}

