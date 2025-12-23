package org.example.service;

import org.example.domain.RollingStockStatus;

/**
 * Representa um item de rolling stock (loco ou wagon)
 * com informação de disponibilidade.
 */
public class RollingStockItem {
    private final int id;
    private final String type;           // "LOCOMOTIVE" ou "WAGON"
    private final String description;    // Ex: "Siemens Vectron MS (5600 kW)"
    private final RollingStockStatus status;
    private final String location;       // Station name ou Train ID
    private final int distanceFromStart; // km (se parked)

    public RollingStockItem(int id, String type, String description,
                            RollingStockStatus status, String location,
                            int distanceFromStart) {
        this.id = id;
        this.type = type;
        this.description = description;
        this.status = status;
        this.location = location;
        this.distanceFromStart = distanceFromStart;
    }

    // Getters
    public int getId() { return id; }
    public String getType() { return type; }
    public String getDescription() { return description; }
    public RollingStockStatus getStatus() { return status; }
    public String getLocation() { return location; }
    public int getDistanceFromStart() { return distanceFromStart; }

    public boolean isParked() {
        return status == RollingStockStatus.PARKED;
    }

    public boolean isInTransit() {
        return status == RollingStockStatus.IN_TRANSIT;
    }
}