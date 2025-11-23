package org.example.domain;

public class LineSegment {
    private final int id;
    private final int lineId;
    private final int order;
    private final boolean electrified;
    private final int maxWeightKgPerM;
    private final int lengthMeters;
    private final int numberTracks;
    private final int lineSegmentsTypeid;  // ✅ NOVO
    private final Integer sidingPosition;
    private final Integer sidingLength;

    public LineSegment(int id, int lineId, int order, boolean electrified,
                       int maxWeightKgPerM, int lengthMeters, int numberTracks,
                       int lineSegmentsTypeid) {
        this(id, lineId, order, electrified, maxWeightKgPerM, lengthMeters,
                numberTracks, lineSegmentsTypeid, null, null);
    }

    public LineSegment(int id, int lineId, int order, boolean electrified,
                       int maxWeightKgPerM, int lengthMeters, int numberTracks,
                       int lineSegmentsTypeid,
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
        this.lineSegmentsTypeid = lineSegmentsTypeid;  // ✅ GUARDAR
        this.sidingPosition = sidingPosition;
        this.sidingLength = sidingLength;
    }

    // Construtor para CSV (sem typeId)
    public LineSegment(int id, int lineId, int order, boolean electrified,
                       int maxWeightKgPerM, int lengthMeters, int numberTracks,
                       Integer sidingPosition, Integer sidingLength) {
        this(id, lineId, order, electrified, maxWeightKgPerM, lengthMeters,
                numberTracks, 2, sidingPosition, sidingLength);
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
    public int getLineSegmentsTypeid() { return lineSegmentsTypeid; }  // ✅ GETTER

    public Integer getSidingPosition() { return sidingPosition; }
    public Integer getSidingLength() { return sidingLength; }

    public boolean hasSiding() {
        return sidingPosition != null && sidingLength != null;
    }

    public int getMaxSpeedKmh() {
        return maxWeightKgPerM >= 8000 ? 120 : 100;
    }

    public boolean isSingleTrack() {
        return this.lineSegmentsTypeid == 1;  // ✅ USAR lineSegmentsTypeid!
    }

    @Override
    public String toString() {
        String sidingInfo = hasSiding() ?
                String.format(", siding@%dm(%dm)", sidingPosition, sidingLength) : "";

        return String.format("Segment{id=%d, line=%d, order=%d, length=%.1fkm, " +
                        "electrified=%b, tracks=%d, type=%d%s}",
                id, lineId, order, getLengthKm(), electrified, numberTracks,
                lineSegmentsTypeid, sidingInfo);
    }
}