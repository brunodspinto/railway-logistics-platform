package org.example.usei05.CsvReaders;

import org.example.CsvReaders.ItemCsvReader;
import org.example.CsvReaders.ReturnsCsvParser;
import org.example.domain.Item;
import org.example.domain.ReturnRecord;
import org.example.domain.ReturnReason;
import org.example.exception.ValidationException;
import org.example.repository.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ReturnsCsvParser - Essential 6 tests
 */
class ReturnsCsvParserTest {

    @TempDir
    Path tempDir;

    private ItemRepository itemRepository;
    private ReturnsCsvParser parser;

    @BeforeEach
    void setUp() {
        itemRepository = new ItemRepository();
        parser = new ReturnsCsvParser(itemRepository);
    }

    private void addItemsToRepository(String... skus) throws IOException {
        File itemsFile = createItemsCsvFile(skus);
        ItemCsvReader itemsParser = new ItemCsvReader();
        List<Item> items = itemsParser.parse(itemsFile.getAbsolutePath());
        itemRepository.saveAll(items);
    }

    private File createItemsCsvFile(String... skus) throws IOException {
        File csvFile = tempDir.resolve("items.csv").toFile();
        try (FileWriter writer = new FileWriter(csvFile)) {
            writer.write("SKU,name,category,unit,volume,unitWeight\n");
            for (String sku : skus) {
                writer.write(String.format("%s,Product %s,Category,kg,1.0,1.0\n", sku, sku));
            }
        }
        return csvFile;
    }

    private File createCsvFile(String... lines) throws IOException {
        File csvFile = tempDir.resolve("test_returns.csv").toFile();
        try (FileWriter writer = new FileWriter(csvFile)) {
            for (String line : lines) {
                writer.write(line + "\n");
            }
        }
        return csvFile;
    }

    @Test
    void testParse_ValidFile_ReturnsRecords() throws IOException {
        addItemsToRepository("SKU123", "SKU456");

        File csvFile = createCsvFile(
                "returnId,SKU,qty,reason,timestamp,expiryDate",
                "R001,SKU123,10,customer_remorse,2025-10-20T10:30:00,2026-01-15",
                "R002,SKU456,5,damaged,2025-10-21T14:45:00,"
        );

        List<ReturnRecord> records = parser.parse(csvFile.getAbsolutePath());

        assertEquals(2, records.size());
        assertEquals("R001", records.get(0).getReturnId());
        assertEquals("SKU123", records.get(0).getSku());
        assertEquals(10, records.get(0).getQty());
        assertEquals(ReturnReason.CUSTOMER_REMORSE, records.get(0).getReason());
    }

    @Test
    void testParse_UnknownSku_ReportsError() throws IOException {
        File csvFile = createCsvFile(
                "returnId,SKU,qty,reason,timestamp,expiryDate",
                "R001,UNKNOWN_SKU,10,customer_remorse,2025-10-20T10:30:00,"
        );

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> parser.parse(csvFile.getAbsolutePath())
        );

        assertTrue(exception.getMessage().contains("Unknown SKU: UNKNOWN_SKU"));
    }

    @Test
    void testParse_InvalidQuantity_ReportsError() throws IOException {
        addItemsToRepository("SKU123");

        File csvFile = createCsvFile(
                "returnId,SKU,qty,reason,timestamp,expiryDate",
                "R001,SKU123,-5,customer_remorse,2025-10-20T10:30:00,"
        );

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> parser.parse(csvFile.getAbsolutePath())
        );

        assertTrue(exception.getMessage().contains("Quantity must be positive"));
    }

    @Test
    void testParse_DuplicateReturnId_ReportsError() throws IOException {
        addItemsToRepository("SKU123", "SKU456");

        File csvFile = createCsvFile(
                "returnId,SKU,qty,reason,timestamp,expiryDate",
                "R001,SKU123,10,customer_remorse,2025-10-20T10:30:00,",
                "R001,SKU456,5,damaged,2025-10-21T14:45:00,"
        );

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> parser.parse(csvFile.getAbsolutePath())
        );

        assertTrue(exception.getMessage().contains("Duplicate return ID: R001"));
        assertTrue(exception.getMessage().contains("Successfully loaded 1 valid return"));
    }

    @Test
    void testParse_EmptyTimestamp_ReportsError() throws IOException {
        addItemsToRepository("SKU123");

        File csvFile = createCsvFile(
                "returnId,SKU,qty,reason,timestamp,expiryDate",
                "R001,SKU123,10,customer_remorse,,"
        );

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> parser.parse(csvFile.getAbsolutePath())
        );

        assertTrue(exception.getMessage().contains("Timestamp cannot be empty"));
    }

    @Test
    void testParse_InvalidReason_ReportsError() throws IOException {
        addItemsToRepository("SKU123");

        File csvFile = createCsvFile(
                "returnId,SKU,qty,reason,timestamp,expiryDate",
                "R001,SKU123,10,invalid_reason,2025-10-20T10:30:00,"
        );

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> parser.parse(csvFile.getAbsolutePath())
        );

        assertTrue(exception.getMessage().contains("Invalid return reason: invalid_reason"));
    }
}