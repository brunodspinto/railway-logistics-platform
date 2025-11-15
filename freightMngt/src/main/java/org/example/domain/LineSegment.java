package org.example.domain;

public class LineSegment {
    private final int id;
    private final int lineId;
    private final int order;
    private final boolean electrified;
    private final int maxWeightKgPerM;
    private final int lengthMeters;
    private final int numberTracks;
    private final Integer sidingPosition;
    private final Integer sidingLength;


    public LineSegment(int id, int lineId, int order, boolean electrified,
                       int maxWeightKgPerM, int lengthMeters, int numberTracks) {
        this(id, lineId, order, electrified, maxWeightKgPerM, lengthMeters,
                numberTracks, null, null);
    }

    public LineSegment(int id, int lineId, int order, boolean electrified,
                       int maxWeightKgPerM, int lengthMeters, int numberTracks,
                       Integer sidingPosition, Integer sidingLength) {
        if (lengthMeters <= 0) {
            throw new IllegalArgumentException("Length must be positive");
        }
        if (maxWeightKgPerM <= 0) {
            throw new IllegalArgumentException("Max weight must be positive");
        }

        this.id = id;
        this.lineId = lineId;
        this.order = order;
        this.electrified = electrified;
        this.maxWeightKgPerM = maxWeightKgPerM;
        this.lengthMeters = lengthMeters;
        this.numberTracks = numberTracks;
        this.sidingPosition = sidingPosition;    // ⭐ NOVO
        this.sidingLength = sidingLength;        // ⭐ NOVO
    }

    // Getters
    public int getId() { return id; }
    public int getLineId() { return lineId; }
    public int getOrder() { return order; }
    public boolean isElectrified() { return electrified; }
    public int getMaxWeightKgPerM() { return maxWeightKgPerM; }
    public int getLengthMeters() { return lengthMeters; }
    public double getLengthKm() { return lengthMeters / 1000.0; }
    public int getNumberTracks() { return numberTracks; }

    public Integer getSidingPosition() { return sidingPosition; }
    public Integer getSidingLength() { return sidingLength; }

    public boolean hasSiding() {
        return sidingPosition != null && sidingLength != null;
    }

    public int getMaxSpeedKmh() {
        // Assumindo: 8000 kg/m → 120 km/h, 6400 kg/m → 100 km/h
        return maxWeightKgPerM >= 8000 ? 120 : 100;
    }

    public boolean isSingleTrack() {
        return numberTracks == 1;
    }

    @Override
    public String toString() {
        String sidingInfo = hasSiding() ?
                String.format(", siding@%dm(%dm)", sidingPosition, sidingLength) : "";

        return String.format("Segment{id=%d, line=%d, order=%d, length=%.1fkm, " +
                        "electrified=%b, tracks=%d%s}",
                id, lineId, order, getLengthKm(), electrified, numberTracks, sidingInfo);
    }
}