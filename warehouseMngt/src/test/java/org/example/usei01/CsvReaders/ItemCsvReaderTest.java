package org.example.usei01.CsvReaders;

import org.example.CsvReaders.ItemCsvReader;
import org.example.domain.Item;
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
 * CRITICAL TESTS for ItemCsvReader
 */
class ItemCsvReaderTest {

    private ItemCsvReader reader;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        reader = new ItemCsvReader();
    }

    @Test
    void testParse_ValidCsv_Success() throws IOException {
        // Given: Valid CSV
        String csv = """
                SKU,name,category,unit,volume,unitWeight
                MILK,Fresh Milk,Dairy,L,1.0,1.03
                RICE,White Rice,Grains,kg,0.8,0.8
                """;
        Path file = createTempFile(csv);

        // When: Parse
        List<Item> items = reader.parse(file.toString());

        // Then: Should load correctly
        assertEquals(2, items.size());
        assertEquals("MILK", items.get(0).getSku());
        assertEquals("Fresh Milk", items.get(0).getName());
        assertEquals(1.0, items.get(0).getVolume(), 0.001);
        assertEquals(1.03, items.get(0).getUnitWeight(), 0.001);
    }

    @Test
    void testParse_InvalidFormat_ThrowsException() throws IOException {
        // Given: CSV with wrong number of fields
        String csv = """
                SKU,name,category,unit,volume,unitWeight
                MILK,Fresh Milk,Dairy,L,1.0
                """;
        Path file = createTempFile(csv);

        // Then: Should reject
        ValidationException ex = assertThrows(ValidationException.class,
                () -> reader.parse(file.toString()));

        assertTrue(ex.getMessage().contains("Invalid item CSV format"));
    }

    @Test
    void testParse_InvalidNumberFormat_ThrowsException() throws IOException {
        // Given: CSV with non-numeric volume/weight
        String csv = """
                SKU,name,category,unit,volume,unitWeight
                MILK,Fresh Milk,Dairy,L,ABC,1.03
                """;
        Path file = createTempFile(csv);

        // Then: Should reject
        ValidationException ex = assertThrows(ValidationException.class,
                () -> reader.parse(file.toString()));

        assertTrue(ex.getMessage().contains("Invalid number format"));
    }

    @Test
    void testParse_NegativeVolume_ThrowsException() throws IOException {
        // Given: CSV with negative volume
        String csv = """
                SKU,name,category,unit,volume,unitWeight
                MILK,Fresh Milk,Dairy,L,-1.0,1.03
                """;
        Path file = createTempFile(csv);

        // Then: Should reject (Item constructor validates)
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> reader.parse(file.toString()));

        assertTrue(ex.getMessage().contains("Volume must be positive"));
    }

    @Test
    void testParse_EmptySKU_ThrowsException() throws IOException {
        // Given: CSV with empty SKU
        String csv = """
                SKU,name,category,unit,volume,unitWeight
                ,Fresh Milk,Dairy,L,1.0,1.03
                """;
        Path file = createTempFile(csv);

        // Then: Should reject (Item constructor validates)
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> reader.parse(file.toString()));

        assertTrue(ex.getMessage().contains("SKU cannot be null or empty"));
    }

    @Test
    void testParse_FileNotFound_ThrowsException() {
        // Given: Non-existent file
        String invalidPath = "/non/existent/file.csv";

        // Then: Should throw ValidationException
        ValidationException ex = assertThrows(ValidationException.class,
                () -> reader.parse(invalidPath));

        assertTrue(ex.getMessage().contains("Error reading items file"));
    }

    // ==================== HELPER ====================

    private Path createTempFile(String content) throws IOException {
        Path file = tempDir.resolve("test.csv");
        Files.writeString(file, content);
        return file;
    }
}
