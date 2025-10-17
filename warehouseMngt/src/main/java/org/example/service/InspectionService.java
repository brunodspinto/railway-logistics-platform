package org.example.service;

import org.example.domain.ReturnRecord;
import org.example.results.InspectionResult;
import org.example.repository.WarehouseRepository;

import java.time.LocalDate;

/**
 * Service responsible for inspecting returned products and deciding
 * whether they should be restocked, partially restocked, or discarded.
 *
 * Rules:
 *  - reason = DAMAGED → DISCARD
 *  - reason = EXPIRED → DISCARD
 *  - expiryDate < hoje → DISCARD
 *  - reason = CUSTOMER_REMORSE → RESTOCK
 *  - reason = WRONG_ITEM → RESTOCK
 *  - reason = PACKAGE_OPENED → DISCARD
 *
 *  Future improvement: Partial restock for mixed conditions.
 */
public class InspectionService {

    private final WarehouseRepository warehouseRepository;

    public InspectionService(WarehouseRepository warehouseRepository) {
        this.warehouseRepository = warehouseRepository;
    }

    /**
     * Inspects a single return and decides the action to take.
     *
     * @param record the return record to inspect
     * @return InspectionResult describing the action taken
     */
    public InspectionResult inspect(ReturnRecord record) {
        if (record == null) {
            throw new IllegalArgumentException("Return record cannot be null");
        }

        LocalDate today = LocalDate.now();
        String action;
        int qtyRestocked = 0;
        int qtyDiscarded = 0;

        // ============ DECISION LOGIC ============
        if (record.getReason() == null) {
            action = "DISCARD";
            qtyDiscarded = record.getQty();
        } else if (!record.getReason().isRestockable()) {
            // Reason not eligible
            action = "DISCARD";
            qtyDiscarded = record.getQty();
        } else if (record.isExpired(today)) {
            // Expired product
            action = "DISCARD";
            qtyDiscarded = record.getQty();
        } else {
            // Valid for restock
            action = "RESTOCK";
            qtyRestocked = record.getQty();
        }

        // Build and return result
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

