package org.example.domain;

public class LineSegment {
    private final int id;
    private final int lineId;
    private final int order;
    private final boolean electrified;
    private final int maxWeightKgPerM;
    private final int lengthMeters;
    private final int numberTracks;

    public LineSegment(int id, int lineId, int order, boolean electrified,
                       int maxWeightKgPerM, int lengthMeters, int numberTracks) {
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
    }

    public int getId() { return id; }
    public int getLineId() { return lineId; }
    public int getOrder() { return order; }
    public boolean isElectrified() { return electrified; }
    public int getMaxWeightKgPerM() { return maxWeightKgPerM; }
    public int getLengthMeters() { return lengthMeters; }
    public double getLengthKm() { return lengthMeters / 1000.0; }
    public int getNumberTracks() { return numberTracks; }

    // Calcula velocidade máxima baseada no peso/metro (simplificado)
    public int getMaxSpeedKmh() {
        // Assumindo: 8000 kg/m → 120 km/h, 6400 kg/m → 100 km/h
        return maxWeightKgPerM >= 8000 ? 120 : 100;
    }

    @Override
    public String toString() {
        return String.format("Segment{id=%d, line=%d, order=%d, length=%.1fkm, electrified=%b}",
                id, lineId, order, getLengthKm(), electrified);
    }
}

