package org.example.CsvReaders;

import org.example.domain.Box;
import org.example.domain.Item;
import org.example.domain.Wagon;
import org.example.exception.ValidationException;
import org.example.repository.ItemRepository;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeParseException;
import java.util.*;

public class WagonCsvReader {

    // Track unique boxIds across ALL wagons
    private final Set<String> globalBoxIds = new HashSet<>();

    public List<Wagon> parse(String filePath, ItemRepository itemRepository) {
        List<Wagon> wagons = new ArrayList<>();
        globalBoxIds.clear(); // Reset for each parse operation

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean isFirstLine = true;
            int lineNumber = 0;

            while ((line = br.readLine()) != null) {
                lineNumber++;

                if (isFirstLine) {
                    isFirstLine = false;
                    continue; // Skip header
                }

                // Skip empty lines
                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] fields = line.split(",", -1);

                if (fields.length < 6) {
                    System.err.println("❌ Line " + lineNumber + ": Expected 6 fields, got " + fields.length);
                    throw new ValidationException("Invalid wagon CSV format at line " + lineNumber + ": expected 6 fields");
                }

                try {
                    parseAndValidateLine(fields, lineNumber, wagons, itemRepository);
                } catch (ValidationException e) {
                    // Re-throw with line number if not already included
                    if (!e.getMessage().contains("line " + lineNumber)) {
                        throw new ValidationException("Line " + lineNumber + ": " + e.getMessage());
                    }
                    throw e;
                }
            }

        } catch (IOException e) {
            throw new ValidationException("Error reading wagons file: " + e.getMessage());
        }

        return wagons;
    }

    private void parseAndValidateLine(String[] fields, int lineNumber,
                                      List<Wagon> wagons,
                                      ItemRepository itemRepository) {
        String wagonId = getField(fields, 0);
        String boxId = getField(fields, 1);
        String sku = getField(fields, 2);

        // Validate required fields
        if (wagonId.isEmpty()) {
            throw new ValidationException("WagonId cannot be empty");
        }
        if (boxId.isEmpty()) {
            throw new ValidationException("BoxId cannot be empty");
        }
        if (sku.isEmpty()) {
            throw new ValidationException("SKU cannot be empty");
        }

        // Validate boxId uniqueness GLOBALLY
        if (globalBoxIds.contains(boxId)) {
            throw new ValidationException("Duplicate boxId found: " + boxId);
        }
        globalBoxIds.add(boxId);

        // Parse and validate quantity
        int quantity = parseQuantity(getField(fields, 3));

        // Parse expiry date (optional)
        LocalDate expiryDate = parseExpiryDate(getField(fields, 4));

        // Parse and validate receivedAt (mandatory)
        Instant receivedAt = parseReceivedAt(getField(fields, 5));

        // Validate SKU exists in items
        Item item = itemRepository.findBySku(sku);
        if (item == null) {
            throw new ValidationException("Unknown SKU: " + sku);
        }

        // ✅ Additional validations per requirements
        if (expiryDate != null && expiryDate.isBefore(LocalDate.now())) {
            System.err.println("⚠️  Line " + lineNumber + ": Product already expired: " + boxId);
            // Decide: reject or just warn? Requirements unclear
        }

        // Create box
        Box box = new Box(boxId, sku, quantity, expiryDate, receivedAt, wagonId);

        // Find or create wagon
        Wagon wagon = findOrCreateWagon(wagons, wagonId);
        wagon.addBox(box);
    }

    private int parseQuantity(String quantityStr) {
        if (quantityStr.isEmpty()) {
            throw new ValidationException("Quantity cannot be empty");
        }

        try {
            int quantity = Integer.parseInt(quantityStr);
            if (quantity <= 0) {
                throw new ValidationException("Quantity must be positive, got: " + quantity);
            }
            return quantity;
        } catch (NumberFormatException e) {
            throw new ValidationException("Invalid quantity format: " + quantityStr);
        }
    }

    private LocalDate parseExpiryDate(String expiryField) {
        if (expiryField.isEmpty()) {
            return null; // Non-perishable products
        }

        try {
            return LocalDate.parse(expiryField);
        } catch (DateTimeParseException e) {
            throw new ValidationException("Invalid expiry date format: " + expiryField +
                    " (expected: YYYY-MM-DD)");
        }
    }

    private Instant parseReceivedAt(String receivedField) {
        if (receivedField.isEmpty()) {
            throw new ValidationException("receivedAt cannot be empty (mandatory field)");
        }

        try {
            LocalDateTime localDateTime = LocalDateTime.parse(receivedField);
            Instant instant = localDateTime.atZone(ZoneOffset.UTC).toInstant();

            // ✅ Validate not in the future
            if (instant.isAfter(Instant.now())) {
                throw new ValidationException("receivedAt cannot be in the future: " + receivedField);
            }

            return instant;
        } catch (DateTimeParseException e) {
            throw new ValidationException("Invalid receivedAt format: " + receivedField +
                    " (expected: ISO datetime, e.g., 2025-10-10T14:30:00)");
        }
    }

    private String getField(String[] fields, int index) {
        return (index < fields.length) ? fields[index].trim() : "";
    }

    private Wagon findOrCreateWagon(List<Wagon> wagons, String wagonId) {
        return wagons.stream()
                .filter(w -> w.getWagonId().equals(wagonId))
                .findFirst()
                .orElseGet(() -> {
                    Wagon newWagon = new Wagon(wagonId);
                    wagons.add(newWagon);
                    return newWagon;
                });
    }
}