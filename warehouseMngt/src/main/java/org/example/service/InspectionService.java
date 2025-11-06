package org.example.service;

import org.example.domain.ReturnRecord;
import org.example.results.InspectionResult;
import org.example.repository.WarehouseRepository;

import java.time.LocalDate;

/**
 * Simplified InspectionService: only RESTOCK or DISCARD.
 * - Damaged / Expired → DISCARD
 * - Customer remorse / Cycle count → RESTOCK
 * - Everything else → DISCARD
 */
public class InspectionService {

    private final WarehouseRepository warehouseRepository;

    public InspectionService(WarehouseRepository warehouseRepository) {
        this.warehouseRepository = warehouseRepository;
    }

    public InspectionResult inspect(ReturnRecord record) {
        if (record == null)
            throw new IllegalArgumentException("Return record cannot be null");

        LocalDate today = LocalDate.now();
        String action;
        int qtyRestocked = 0;
        int qtyDiscarded = 0;

        // ============ DECISION LOGIC ============
        switch (record.getReason()) {
            case DAMAGED, EXPIRED -> {
                action = "DISCARD";
                qtyRestocked = 0;
                qtyDiscarded = record.getQty();
            }
            case CUSTOMER_REMORSE, CYCLE_COUNT -> {
                if (record.isExpired(today)) {
                    action = "DISCARD";
                    qtyRestocked = 0;
                    qtyDiscarded = record.getQty();
                } else {
                    action = "RESTOCK";
                    qtyRestocked = record.getQty();
                    qtyDiscarded = 0;
                }
            }
            default -> {
                action = "DISCARD";
                qtyRestocked = 0;
                qtyDiscarded = record.getQty();
            }
        }

        return new InspectionResult(
                record.getReturnId(),
                record.getSku(),
                record.getQty(),
                action,
                qtyRestocked,
                qtyDiscarded,
                record.getReason().toString(),
                record.getTimestamp(),
                record.getExpiryDate()
        );
    }
}
