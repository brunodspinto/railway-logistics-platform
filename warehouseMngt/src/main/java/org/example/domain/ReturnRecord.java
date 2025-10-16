package org.example.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Objects;

/**
 * Represents a product return record in the warehouse system.
 * This class implements Comparable to support ordering in the quarantine queue.
 * Natural ordering: timestamp descending (most recent first), then returnId ascending for ties.
 */
public class ReturnRecord implements Comparable<ReturnRecord> {
    private final String returnId;
    private final String sku;
    private final int qty;
    private final ReturnReason reason;
    private final LocalDateTime timestamp;
    private final LocalDate expiryDate; // Can be null for non-perishable items

    /**
     * Constructor for ReturnRecord with all fields.
     *
     * @param returnId unique identifier for the return
     * @param sku product SKU code
     * @param qty quantity being returned
     * @param reason the reason for return
     * @param timestamp when the return was received
     * @param expiryDate expiry date of the product (can be null)
     * @throws IllegalArgumentException if validation fails
     */
    public ReturnRecord(String returnId, String sku, int qty, ReturnReason reason,
                        LocalDateTime timestamp, LocalDate expiryDate) {
        validateReturnId(returnId);
        validateSku(sku);
        validateQty(qty);
        validateReason(reason);
        validateTimestamp(timestamp);

        this.returnId = returnId;
        this.sku = sku;
        this.qty = qty;
        this.reason = reason;
        this.timestamp = timestamp;
        this.expiryDate = expiryDate;
    }

    /**
     * Constructor for ReturnRecord with string values (useful for CSV parsing).
     *
     * @param returnId unique identifier for the return
     * @param sku product SKU code
     * @param qty quantity being returned (as string)
     * @param reasonStr the reason for return (as string)
     * @param timestampStr when the return was received (ISO format)
     * @param expiryDateStr expiry date of the product (can be null or empty)
     * @throws IllegalArgumentException if validation fails
     */
    public ReturnRecord(String returnId, String sku, String qty, String reasonStr,
                        String timestampStr, String expiryDateStr) {
        this(returnId,
                sku,
                parseQty(qty),
                ReturnReason.fromString(reasonStr),
                parseTimestamp(timestampStr),
                parseExpiryDate(expiryDateStr));
    }

    // Validation methods
    private void validateReturnId(String returnId) {
        if (returnId == null || returnId.trim().isEmpty()) {
            throw new IllegalArgumentException("Return ID cannot be null or empty");
        }
    }

    private void validateSku(String sku) {
        if (sku == null || sku.trim().isEmpty()) {
            throw new IllegalArgumentException("SKU cannot be null or empty");
        }
    }

    private void validateQty(int qty) {
        if (qty <= 0) {
            throw new IllegalArgumentException("Quantity must be positive, got: " + qty);
        }
    }

    private void validateReason(ReturnReason reason) {
        if (reason == null) {
            throw new IllegalArgumentException("Return reason cannot be null");
        }
    }

    private void validateTimestamp(LocalDateTime timestamp) {
        if (timestamp == null) {
            throw new IllegalArgumentException("Timestamp cannot be null");
        }
    }

    // Parsing helper methods
    private static int parseQty(String qtyStr) {
        if (qtyStr == null || qtyStr.trim().isEmpty()) {
            throw new IllegalArgumentException("Quantity cannot be null or empty");
        }

        try {
            return Integer.parseInt(qtyStr.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid quantity format: " + qtyStr);
        }
    }

    private static LocalDateTime parseTimestamp(String timestampStr) {
        if (timestampStr == null || timestampStr.trim().isEmpty()) {
            throw new IllegalArgumentException("Timestamp cannot be null or empty");
        }

        try {
            // Support both ISO formats with 'T' separator and space separator
            String normalized = timestampStr.trim().replace(" ", "T");
            if (!normalized.contains("T")) {
                throw new IllegalArgumentException("Invalid timestamp format: " + timestampStr);
            }
            return LocalDateTime.parse(normalized);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid timestamp format: " + timestampStr +
                    ". Expected ISO format (e.g., 2025-09-22T10:00:00)");
        }
    }

    private static LocalDate parseExpiryDate(String expiryDateStr) {
        if (expiryDateStr == null || expiryDateStr.trim().isEmpty()) {
            return null; // Expiry date is optional
        }

        try {
            return LocalDate.parse(expiryDateStr.trim());
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid expiry date format: " + expiryDateStr +
                    ". Expected format: YYYY-MM-DD");
        }
    }

    /**
     * Checks if the product has expired based on a given reference date.
     *
     * @param referenceDate the date to check against (typically current date)
     * @return true if product has expiry date and it's before or equal to reference date
     */
    public boolean isExpired(LocalDate referenceDate) {
        if (expiryDate == null) {
            return false; // Non-perishable items never expire
        }
        return expiryDate.isBefore(referenceDate) || expiryDate.isEqual(referenceDate);
    }

    /**
     * Determines if this return can potentially be restocked based on its reason.
     * Note: Actual restocking decision may depend on additional inspection.
     *
     * @return true if the reason allows for potential restocking
     */
    public boolean isRestockable() {
        return reason.isRestockable();
    }

    /**
     * Creates a Box ID for restocking this return.
     * Format: "RET-{returnId}"
     *
     * @return the formatted box ID for restocking
     */
    public String createRestockBoxId() {
        return "RET-" + returnId;
    }

    /**
     * Compares this return with another for ordering in quarantine queue.
     * Order: timestamp descending (most recent first), then returnId ascending for ties.
     *
     * @param other the other ReturnRecord to compare to
     * @return negative if this should come before other, positive if after, 0 if equal
     */
    @Override
    public int compareTo(ReturnRecord other) {
        if (other == null) {
            return -1; // This object comes before null
        }

        // First compare by timestamp (descending - most recent first)
        int timestampComparison = other.timestamp.compareTo(this.timestamp);

        if (timestampComparison != 0) {
            return timestampComparison;
        }

        // If timestamps are equal, compare by returnId (ascending)
        return this.returnId.compareTo(other.returnId);
    }

    // Getters
    public String getReturnId() {
        return returnId;
    }

    public String getSku() {
        return sku;
    }

    public int getQty() {
        return qty;
    }

    public ReturnReason getReason() {
        return reason;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public boolean hasExpiryDate() {
        return expiryDate != null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ReturnRecord that = (ReturnRecord) o;
        return returnId.equals(that.returnId); // returnId should be unique
    }

    @Override
    public int hashCode() {
        return Objects.hash(returnId);
    }

    @Override
    public String toString() {
        return String.format("ReturnRecord{id='%s', sku='%s', qty=%d, reason=%s, timestamp=%s, expiryDate=%s}",
                returnId, sku, qty, reason, timestamp,
                expiryDate != null ? expiryDate : "N/A");
    }

    /**
     * Creates a formatted string for display purposes.
     *
     * @return a user-friendly formatted string
     */
    public String toDisplayString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return String.format("Return %s: %d units of %s (%s) at %s",
                returnId, qty, sku, reason.toString(), timestamp.format(formatter));
    }
}

