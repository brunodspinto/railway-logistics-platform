package org.example.service;

import org.example.results.InspectionResult;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Generic AuditLogService for all use cases (USEI01, USEI05, etc.)
 * Supports structured inspection logs (USEI05) and generic operation logs (USEI01).
 */
public class AuditLogService {

    private final String logFilePath;
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Creates an AuditLogService for a specific file.
     *
     * @param logFilePath path to log file (e.g., "logs/usei01-log.txt")
     */
    public AuditLogService(String logFilePath) {
        this.logFilePath = logFilePath;
        ensureLogDirectory();
    }

    /**
     * Logs a generic operation with timestamp.
     *
     * @param operation short name of the operation (e.g., "IMPORT", "DISPATCH")
     * @param details detailed message describing the action
     */
    public void log(String operation, String details) {
        String timestamp = LocalDateTime.now().format(FORMATTER);
        String line = String.format("%s | %s | %s", timestamp, operation, details);
        write(line);
    }

    /**
     * Logs a structured inspection (specific to USEI05).
     */
    public void log(InspectionResult result) {
        if (result == null) {
            throw new IllegalArgumentException("Cannot log null inspection result");
        }

        StringBuilder line = new StringBuilder();
        line.append(result.getTimestamp().format(FORMATTER))
                .append(" | returnId=").append(result.getReturnId())
                .append(" | sku=").append(result.getSku())
                .append(" | action=").append(result.getAction())
                .append(" | qty=").append(result.getQty());

        if (result.getQtyRestocked() > 0 && result.getQtyDiscarded() > 0) {
            line.append(" | qtyRestocked=").append(result.getQtyRestocked())
                    .append(" | qtyDiscarded=").append(result.getQtyDiscarded())
                    .append(" | action=PartialRestock");
        } else if ("DISCARD".equalsIgnoreCase(result.getAction())) {
            line.append(" | reason=").append(result.getReason());
        }

        if (result.getExpiryDate() != null) {
            line.append(" | expiryDate=").append(result.getExpiryDate());
        }

        write(line.toString());
    }

    /**
     * Ensures the log directory exists.
     */
    private void ensureLogDirectory() {
        try {
            Files.createDirectories(Paths.get("logs"));
        } catch (IOException e) {
            System.err.println("⚠️ Failed to create log directory: " + e.getMessage());
        }
    }

    /**
     * Writes a single line to the configured log file.
     */
    private void write(String line) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFilePath, true))) {
            writer.write(line);
            writer.newLine();
        } catch (IOException e) {
            System.err.println("⚠️ Failed to write to log " + logFilePath + ": " + e.getMessage());
        }
    }
}
