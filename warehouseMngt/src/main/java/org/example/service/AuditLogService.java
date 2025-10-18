package org.example.service; // ajusta package conforme o teu projeto

import org.example.results.InspectionResult; // ajusta o import conforme o teu package real
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

    // Construtor por defeito usa logs/audit-log.txt
    public AuditLogService() {
        this(DEFAULT_LOG_FILE);
    }

    // Construtor que permite customizar o ficheiro (opcional)
    public AuditLogService(String filePath) {
        if (filePath == null || filePath.trim().isEmpty()) {
            filePath = DEFAULT_LOG_FILE;
        }
        this.logPath = Path.of(filePath);
    }

    public void log(InspectionResult result) {
        if (result == null) throw new IllegalArgumentException("InspectionResult cannot be null");

        try {
            // garante a diretoria existe
            Files.createDirectories(logPath.getParent());

            String timestamp = ZonedDateTime.now(ZoneId.systemDefault()).format(FORMATTER);

            StringBuilder sb = new StringBuilder();
            sb.append(timestamp)
                    .append(" | returnId=").append(result.getReturnId())
                    .append(" | sku=").append(result.getSku())
                    .append(" | action=").append(result.getAction())
                    .append(" | qty=").append(result.getQty());

            if (result.getQtyRestocked() > 0) {
                sb.append(" | qtyRestocked=").append(result.getQtyRestocked());
            }
            if (result.getQtyDiscarded() > 0) {
                sb.append(" | qtyDiscarded=").append(result.getQtyDiscarded());
            }
            if (result.getReason() != null && !result.getReason().isEmpty()) {
                sb.append(" | reason=").append(result.getReason());
            }
            if (result.getExpiryDate() != null) {
                sb.append(" | expiryDate=").append(result.getExpiryDate().toString());
            }

            // escreve em append
            try (BufferedWriter w = new BufferedWriter(new FileWriter(logPath.toFile(), true))) {
                w.write(sb.toString());
                w.newLine();
            }

        } catch (IOException e) {
            System.err.println("⚠️ Failed to write audit log: " + e.getMessage());
        }
    }
}
