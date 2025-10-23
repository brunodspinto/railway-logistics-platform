package org.example.service;

import org.example.domain.*;
import org.example.results.*;
import org.example.exception.ValidationException;
import org.example.CsvReaders.ReturnsCsvParser;
import org.example.repository.ItemRepository;
import org.example.repository.WarehouseRepository;
import org.example.service.AuditLogService;
import java.time.Instant;
import java.util.List;

/**
 * Central orchestrator for processing product returns (USEI05).
 */
public class ReturnsProcessingService {

    private final ItemRepository itemRepository;
    private final WarehouseRepository warehouseRepository;
    private final InventoryService inventoryService;
    private final InspectionService inspectionService;
    private final AuditLogService auditLogService;

    public ReturnsProcessingService(ItemRepository itemRepository,
                                    WarehouseRepository warehouseRepository) {
        this.itemRepository = itemRepository;
        this.warehouseRepository = warehouseRepository;
        this.inventoryService = new InventoryService(warehouseRepository);
        this.inspectionService = new InspectionService(warehouseRepository);

        this.auditLogService = new AuditLogService("logs/audit-log.txt");
    }

    /**
     * Main entry point for USEI05.
     */
    public ProcessingResult processReturns(String filePath) {
        int restocked = 0, discarded = 0, partial = 0, errors = 0;

        auditLogService.initializeLog();

        ReturnsCsvParser parser = new ReturnsCsvParser(itemRepository);
        List<ReturnRecord> records;
        try {
            records = parser.parse(filePath);
        } catch (ValidationException e) {
            System.err.println("CSV validation failed:\n" + e.getMessage());
            return new ProcessingResult(0, 0, 0, 0, 1);
        }


        Quarantine quarantine = new Quarantine(records);

        while (!quarantine.isEmpty()) {
            ReturnRecord record = quarantine.poll();
            try {
                InspectionResult result = inspectionService.inspect(record);

                switch (result.getAction().toUpperCase()) {
                    case "RESTOCK" -> {
                        addToInventory(result);
                        restocked++;
                    }
                    case "DISCARD" -> discarded++;
                    case "PARTIAL_RESTOCK" -> {
                        addToInventory(result);
                        partial++;
                    }
                }

                auditLogService.log(result);

            } catch (Exception e) {
                System.err.println("Error processing " + record.getReturnId() + ": " + e.getMessage());
                errors++;
            }
        }

        System.out.println("\nAudit log saved to: " + auditLogService.getLogPath());

        int total = restocked + discarded + partial + errors;
        return new ProcessingResult(total, restocked, discarded, partial, errors);
    }

    /**
     * Adds a restocked return to warehouse inventory.
     */
    private void addToInventory(InspectionResult result) {
        try {
            Box restockBox = new Box(
                    result.getReturnId(),
                    result.getSku(),
                    result.getQtyRestocked(),
                    null,
                    Instant.now(),
                    "RETURNS"
            );

            Warehouse warehouse = warehouseRepository.findDefault();
            Bay targetBay = warehouse.findBestAvailableBay(result.getSku());

            if (targetBay == null) {
                throw new IllegalStateException("No available bay to restock SKU " + result.getSku());
            }

            targetBay.addBox(restockBox);
            warehouseRepository.save(warehouse);

        } catch (Exception e) {
            System.err.println("Failed to restock SKU " + result.getSku() + ": " + e.getMessage());
        }
    }
}
