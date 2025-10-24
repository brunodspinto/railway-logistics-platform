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
                qtyDiscarded = record.getQty();
            }
            case CUSTOMER_REMORSE, CYCLE_COUNT -> {
                if (record.isExpired(today)) {
                    action = "DISCARD";
                    qtyDiscarded = record.getQty();
                } else {
                    // Exemplo de avaliação unitária: 80% aceites
                    qtyRestocked = (int) Math.ceil(record.getQty() * 0.8);
                    qtyDiscarded = record.getQty() - qtyRestocked;
                    action = (qtyRestocked > 0 && qtyDiscarded > 0)
                            ? "PARTIAL_RESTOCK" : "RESTOCK";
                }
            }
            default -> {
                action = "DISCARD";
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
