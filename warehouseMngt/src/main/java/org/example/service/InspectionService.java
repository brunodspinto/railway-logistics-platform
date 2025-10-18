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
 *  - reason = CUSTOMER_REMORSE → RESTOCK (or PARTIAL if large qty)
 *  - reason = CYCLE_COUNT → RESTOCK (may also be PARTIAL)
 *  - reason = WRONG_ITEM → RESTOCK
 *  - reason = PACKAGE_OPENED → DISCARD
 */
public class InspectionService {

    private final WarehouseRepository warehouseRepository;

    public InspectionService(WarehouseRepository warehouseRepository) {
        this.warehouseRepository = warehouseRepository;
    }

    public InspectionResult inspect(ReturnRecord record) {
        if (record == null) {
            throw new IllegalArgumentException("Return record cannot be null");
        }

        LocalDate today = LocalDate.now();
        String action;
        int qtyRestocked = 0;
        int qtyDiscarded = 0;

        // ============ DECISION LOGIC ============

        // invalid or non-restockable reason
        if (record.getReason() == null || !record.getReason().isRestockable()) {
            action = "DISCARD";
            qtyDiscarded = record.getQty();

            // expired product
        } else if (record.isExpired(today)) {
            action = "DISCARD";
            qtyDiscarded = record.getQty();

            // partial restock (for large quantities of customer remorse or cycle count)
        } else if ((record.getReason().name().equalsIgnoreCase("CUSTOMER_REMORSE")
                || record.getReason().name().equalsIgnoreCase("CYCLE_COUNT"))
                && record.getQty() >= 10) {

            int restocked = (int) Math.ceil(record.getQty() * 0.6);
            int discarded = record.getQty() - restocked;

            action = "PARTIAL_RESTOCK";
            qtyRestocked = restocked;
            qtyDiscarded = discarded;

            // full restock
        } else {
            action = "RESTOCK";
            qtyRestocked = record.getQty();
        }

        // ============ RESULT CONSTRUCTION ============
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
