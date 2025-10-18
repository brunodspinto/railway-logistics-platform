package org.example.CsvReaders;

import org.example.domain.ReturnRecord;
import org.example.domain.ReturnReason;
import org.example.exception.ValidationException;
import org.example.repository.ItemRepository;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Parser for returns.csv file.
 * Format: returnId, SKU, qty, reason, timestamp, expiryDate
 *
 * Validates each record and collects errors. Invalid records are skipped
 * but do not stop the parsing process. All validation errors are reported
 * at the end.
 */
public class ReturnsCsvParser {
    private static final String DELIMITER = ",";
    private static final int EXPECTED_COLUMNS = 6;

    private final ItemRepository itemRepository;
    private final List<String> validationErrors;
    private final Set<String> seenReturnIds; // To detect duplicates

    /**
     * Creates a parser with a reference to the item repository for SKU validation.
     *
     * @param itemRepository repository to validate SKUs against
     */
    public ReturnsCsvParser(ItemRepository itemRepository) {
        if (itemRepository == null) {
            throw new IllegalArgumentException("ItemRepository cannot be null");
        }
        this.itemRepository = itemRepository;
        this.validationErrors = new ArrayList<>();
        this.seenReturnIds = new HashSet<>();
    }

    /**
     * Parses the returns CSV file and returns valid ReturnRecords.
     * Invalid records are skipped and errors are collected.
     * After parsing, if there were critical errors, a ValidationException is thrown.
     *
     * @param filePath path to the returns.csv file
     * @return list of valid ReturnRecords
     * @throws ValidationException if file cannot be read or if validation errors occurred
     */
    public List<ReturnRecord> parse(String filePath) {
        validationErrors.clear();
        seenReturnIds.clear();
        List<ReturnRecord> validRecords = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            int lineNumber = 0;
            boolean isFirstLine = true;

            while ((line = br.readLine()) != null) {
                lineNumber++;

                // Skip header line
                if (isFirstLine) {
                    isFirstLine = false;
                    if (line.trim().toLowerCase().startsWith("returnid")) {
                        continue; // Skip header
                    }
                }

                // Skip empty lines
                if (line.trim().isEmpty()) {
                    continue;
                }

                try {
                    ReturnRecord record = parseLine(line, lineNumber);
                    if (record != null) {
                        validRecords.add(record);
                    }
                } catch (Exception e) {
                    // Error already added in parseLine or by parseLine itself
                    // Continue processing remaining lines
                }
            }

        } catch (IOException e) {
            throw new ValidationException("Error reading returns file: " + e.getMessage());
        }

        // If there were validation errors, report them
        if (!validationErrors.isEmpty()) {
            StringBuilder errorMsg = new StringBuilder();
            errorMsg.append(String.format("Found %d validation error(s) in returns.csv:\n",
                    validationErrors.size()));
            for (String error : validationErrors) {
                errorMsg.append("  - ").append(error).append("\n");
            }
            errorMsg.append(String.format("\nSuccessfully loaded %d valid return(s)",
                    validRecords.size()));

            throw new ValidationException(errorMsg.toString());
        }

        return validRecords;
    }

    /**
     * Parses a single line from the CSV file.
     *
     * @param line the CSV line to parse
     * @param lineNumber the line number (for error reporting)
     * @return ReturnRecord if valid, null if invalid
     */
    private ReturnRecord parseLine(String line, int lineNumber) {
        String[] fields = line.split(DELIMITER, -1); // -1 to keep empty trailing fields

        // Validate column count
        if (fields.length != EXPECTED_COLUMNS) {
            addError(lineNumber, null,
                    String.format("Expected %d columns, found %d", EXPECTED_COLUMNS, fields.length));
            return null;
        }

        // Trim all fields
        for (int i = 0; i < fields.length; i++) {
            fields[i] = fields[i].trim();
        }

        String returnId = fields[0];
        String sku = fields[1];
        String qtyStr = fields[2];
        String reasonStr = fields[3];
        String timestampStr = fields[4];
        String expiryDateStr = fields[5];

        if (lineNumber == 1 && fields[0].contains("returnId")) return null;

        // Validate returnId is not empty
        if (returnId.isEmpty()) {
            addError(lineNumber, returnId, "Return ID cannot be empty");
            return null;
        }

        // Check for duplicate returnId
        if (seenReturnIds.contains(returnId)) {
            addError(lineNumber, returnId, "Duplicate return ID: " + returnId);
            return null;
        }

        // Validate SKU exists in item repository
        if (sku.isEmpty()) {
            addError(lineNumber, returnId, "SKU cannot be empty");
            return null;
        }

        if (!itemRepository.existsBySku(sku)) {
            addError(lineNumber, returnId, "Unknown SKU: " + sku);
            return null;
        }

        // Validate quantity
        if (qtyStr.isEmpty()) {
            addError(lineNumber, returnId, "Quantity cannot be empty");
            return null;
        }

        int qty;
        try {
            qty = Integer.parseInt(qtyStr);
            if (qty <= 0) {
                addError(lineNumber, returnId, "Quantity must be positive, got: " + qty);
                return null;
            }
        } catch (NumberFormatException e) {
            addError(lineNumber, returnId, "Invalid quantity format: " + qtyStr);
            return null;
        }

        // Validate return reason
        if (reasonStr.isEmpty()) {
            addError(lineNumber, returnId, "Return reason cannot be empty");
            return null;
        }

        ReturnReason reason;
        try {
            reason = ReturnReason.fromString(reasonStr);
        } catch (IllegalArgumentException e) {
            addError(lineNumber, returnId, "Invalid return reason: " + reasonStr);
            return null;
        }

        // Validate timestamp (required)
        if (timestampStr.isEmpty()) {
            addError(lineNumber, returnId, "Timestamp cannot be empty");
            return null;
        }

        // Create ReturnRecord (this will validate timestamp and expiryDate formats)
        try {
            ReturnRecord record = new ReturnRecord(
                    returnId,
                    sku,
                    qtyStr,
                    reasonStr,
                    timestampStr,
                    expiryDateStr.isEmpty() ? null : expiryDateStr
            );

            seenReturnIds.add(returnId);
            return record;

        } catch (IllegalArgumentException e) {
            addError(lineNumber, returnId, e.getMessage());
            return null;
        }
    }

    /**
     * Adds an error message to the validation errors list.
     *
     * @param lineNumber the line number where the error occurred
     * @param returnId the return ID (can be null if not parsed yet)
     * @param message the error message
     */
    private void addError(int lineNumber, String returnId, String message) {
        String errorMsg = String.format("Line %d", lineNumber);
        if (returnId != null && !returnId.isEmpty()) {
            errorMsg += String.format(" [returnId=%s]", returnId);
        }
        errorMsg += ": " + message;
        validationErrors.add(errorMsg);
    }

    /**
     * Gets all validation errors encountered during the last parse.
     *
     * @return list of error messages
     */
    public List<String> getValidationErrors() {
        return new ArrayList<>(validationErrors);
    }

    /**
     * Checks if there were any validation errors during the last parse.
     *
     * @return true if errors were encountered
     */
    public boolean hasValidationErrors() {
        return !validationErrors.isEmpty();
    }
}
