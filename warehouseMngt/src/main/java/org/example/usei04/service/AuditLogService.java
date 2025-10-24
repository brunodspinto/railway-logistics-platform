package org.example.usei04.service;

import org.example.results.InspectionResult;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class AuditLogService {

    private static final String DEFAULT_LOG_DIR = "logs";
    private static final String DEFAULT_LOG_FILE = DEFAULT_LOG_DIR + "/audit-log.txt";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final Path logPath;

    public AuditLogService() {
        this(DEFAULT_LOG_FILE);
    }

    public AuditLogService(String filePath) {
        if (filePath == null || filePath.trim().isEmpty()) {
            filePath = DEFAULT_LOG_FILE;
        }
        this.logPath = Path.of(filePath);
    }

    /** Just ensure the directory exists — DO NOT clear the file */
    public void initializeLog() {
        try {
            Files.createDirectories(logPath.getParent());
            System.out.println("Audit log ready at: " + logPath);
        } catch (IOException e) {
            System.err.println("Failed to prepare audit log: " + e.getMessage());
        }
    }

    /** Append-only logging (preserves history) */
    public void log(InspectionResult result) {
        if (result == null)
            throw new IllegalArgumentException("InspectionResult cannot be null");

        try {
            Files.createDirectories(logPath.getParent());
            String timestamp = ZonedDateTime.now(ZoneId.systemDefault()).format(FORMATTER);

            StringBuilder sb = new StringBuilder();
            sb.append(timestamp)
                    .append(" | returnId=").append(result.getReturnId())
                    .append(" | sku=").append(result.getSku())
                    .append(" | action=").append(result.getAction())
                    .append(" | qty=").append(result.getQty())
                    .append(" | qtyRestocked=").append(result.getQtyRestocked())
                    .append(" | qtyDiscarded=").append(result.getQtyDiscarded())
                    .append(" | reason=").append(result.getReason());

            if (result.getExpiryDate() != null) {
                sb.append(" | expiryDate=").append(result.getExpiryDate());
            }

            try (BufferedWriter w = new BufferedWriter(new FileWriter(logPath.toFile(), true))) {
                w.write(sb.toString());
                w.newLine();
            }

        } catch (IOException e) {
            System.err.println("⚠️ Failed to write audit log: " + e.getMessage());
        }
    }

    public String getLogPath() {
        return logPath.toString();
    }
}

