package org.example.usei01.CsvReaders;

import org.example.CsvReaders.WagonCsvReader;
import org.example.domain.Item;
import org.example.domain.Wagon;
import org.example.exception.ValidationException;
import org.example.repository.ItemRepository;
import org.example.results.ValidationResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * CRITICAL TESTS for WagonCsvReader
 * Focus: Validation rules from USEI01 specification
 */
class WagonCsvReaderTest {

    private WagonCsvReader reader;
    private ItemRepository itemRepository;
    private ValidationResult validationResult;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        reader = new WagonCsvReader();
        itemRepository = new ItemRepository();
        validationResult = new ValidationResult();

        // Setup test items
        itemRepository.save(new Item("MILK", "Fresh Milk", "Dairy", "L", 1.0, 1.03));
        itemRepository.save(new Item("RICE", "White Rice", "Grains", "kg", 0.8, 0.8));
    }

    // ==================== CRITICAL VALIDATION TESTS ====================

    @Test
    void testParse_ValidCsv_Success() throws IOException {
        // Given: Valid CSV
        String csv = """
                wagonId,boxId,SKU,qty,expiryDate,receivedAt
                W-001,BOX-001,MILK,50,2025-12-01,2025-10-01T10:00:00
                W-001,BOX-002,RICE,100,,2025-10-01T11:00:00
                """;
        Path file = createTempFile(csv);

        // When: Parse
        List<Wagon> wagons = reader.parse(file.toString(), itemRepository, validationResult);

        // Then: Should succeed
        assertEquals(1, wagons.size());
        assertEquals("W-001", wagons.get(0).getWagonId());
        assertEquals(2, wagons.get(0).getBoxes().size());
    }

    @Test
    void testParse_UnknownSKU_ThrowsException() throws IOException {
        // Given: CSV with unknown SKU
        String csv = """
                wagonId,boxId,SKU,qty,expiryDate,receivedAt
                W-001,BOX-001,UNKNOWN-SKU,50,,2025-10-01T10:00:00
                """;
        Path file = createTempFile(csv);

        // Then: Should throw ValidationException
        ValidationException ex = assertThrows(ValidationException.class,
                () -> reader.parse(file.toString(), itemRepository, validationResult));

        assertTrue(ex.getMessage().contains("Unknown SKU"));
        assertTrue(ex.getMessage().contains("UNKNOWN-SKU"));
    }

    @Test
    void testParse_NegativeQuantity_ThrowsException() throws IOException {
        // Given: CSV with negative quantity
        String csv = """
                wagonId,boxId,SKU,qty,expiryDate,receivedAt
                W-001,BOX-001,MILK,-10,,2025-10-01T10:00:00
                """;
        Path file = createTempFile(csv);

        // Then: Should reject
        ValidationException ex = assertThrows(ValidationException.class,
                () -> reader.parse(file.toString(), itemRepository, validationResult));

        assertTrue(ex.getMessage().contains("Quantity must be positive"));
    }

    @Test
    void testParse_MissingReceivedAt_ThrowsException() throws IOException {
        // Given: CSV without receivedAt (mandatory field)
        String csv = """
                wagonId,boxId,SKU,qty,expiryDate,receivedAt
                W-001,BOX-001,MILK,50,2025-12-01,
                """;
        Path file = createTempFile(csv);

        // Then: Should reject
        ValidationException ex = assertThrows(ValidationException.class,
                () -> reader.parse(file.toString(), itemRepository, validationResult));

        assertTrue(ex.getMessage().contains("receivedAt cannot be empty"));
    }

    @Test
    void testParse_DuplicateBoxId_ThrowsException() throws IOException {
        // Given: CSV with duplicate boxId (CRITICAL: global uniqueness)
        String csv = """
                wagonId,boxId,SKU,qty,expiryDate,receivedAt
                W-001,BOX-001,MILK,50,,2025-10-01T10:00:00
                W-002,BOX-001,RICE,30,,2025-10-01T11:00:00
                """;
        Path file = createTempFile(csv);

        // Then: Should reject second occurrence
        ValidationException ex = assertThrows(ValidationException.class,
                () -> reader.parse(file.toString(), itemRepository, validationResult));

        assertTrue(ex.getMessage().contains("Duplicate boxId"));
        assertTrue(ex.getMessage().contains("BOX-001"));
    }

    @Test
    void testParse_InvalidDateFormat_ThrowsException() throws IOException {
        // Given: CSV with invalid date format
        String csv = """
                wagonId,boxId,SKU,qty,expiryDate,receivedAt
                W-001,BOX-001,MILK,50,01/12/2025,2025-10-01T10:00:00
                """;
        Path file = createTempFile(csv);

        // Then: Should reject
        ValidationException ex = assertThrows(ValidationException.class,
                () -> reader.parse(file.toString(), itemRepository, validationResult));

        assertTrue(ex.getMessage().contains("Invalid expiry date format"));
    }

    @Test
    void testParse_FutureReceivedAt_ThrowsException() throws IOException {
        // Given: CSV with future receivedAt
        String csv = """
                wagonId,boxId,SKU,qty,expiryDate,receivedAt
                W-001,BOX-001,MILK,50,,2099-10-01T10:00:00
                """;
        Path file = createTempFile(csv);

        // Then: Should reject
        ValidationException ex = assertThrows(ValidationException.class,
                () -> reader.parse(file.toString(), itemRepository, validationResult));

        assertTrue(ex.getMessage().contains("receivedAt cannot be in the future"));
    }

    @Test
    void testParse_ExpiredProduct_FlagsForInspection() throws IOException {
        // Given: CSV with expired product
        String csv = """
                wagonId,boxId,SKU,qty,expiryDate,receivedAt
                W-001,BOX-001,MILK,50,2020-01-01,2025-10-01T10:00:00
                """;
        Path file = createTempFile(csv);

        // When: Parse
        List<Wagon> wagons = reader.parse(file.toString(), itemRepository, validationResult);

        // Then: Should load but flag for inspection
        assertEquals(1, wagons.size());
        assertTrue(wagons.get(0).getBoxes().get(0).isFlaggedForInspection());
        assertTrue(validationResult.getBoxesFlaggedForInspection() > 0);
    }

    // ==================== HELPER ====================

    private Path createTempFile(String content) throws IOException {
        Path file = tempDir.resolve("test.csv");
        Files.writeString(file, content);
        return file;
    }
}

