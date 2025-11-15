package org.example.domain;

public class WagonModel {
    private final int id;
    private final String model;
    private final String maker;
    private final int numberBogies;
    private final String bogies; // "Duplo" ou "Simples"
    private final int lengthMm;
    private final int widthMm;
    private final int heightMm;
    private final double weightTons;      // Tare (peso vazio)
    private final int maxSpeed;           // km/h
    private final double payloadTons;     // Carga útil máxima
    private final double volumeM3;
    private final String type;            // "Cereal wagon", etc.
    private final int bitola;             // 1668 ou 1435

    public WagonModel(int id, String model, String maker, int numberBogies,
                      String bogies, int lengthMm, int widthMm, int heightMm,
                      double weightTons, int maxSpeed, double payloadTons,
                      double volumeM3, String type, int bitola) {

        if (model == null || model.isBlank()) {
            throw new IllegalArgumentException("Model cannot be empty");
        }
        if (weightTons <= 0 || payloadTons <= 0) {
            throw new IllegalArgumentException("Weight and payload must be positive");
        }
        if (maxSpeed <= 0) {
            throw new IllegalArgumentException("Max speed must be positive");
        }

        this.id = id;
        this.model = model;
        this.maker = maker;
        this.numberBogies = numberBogies;
        this.bogies = bogies;
        this.lengthMm = lengthMm;
        this.widthMm = widthMm;
        this.heightMm = heightMm;
        this.weightTons = weightTons;
        this.maxSpeed = maxSpeed;
        this.payloadTons = payloadTons;
        this.volumeM3 = volumeM3;
        this.type = type;
        this.bitola = bitola;
    }

    // Getters
    public int getId() { return id; }
    public String getModel() { return model; }
    public String getMaker() { return maker; }
    public int getNumberBogies() { return numberBogies; }
    public String getBogies() { return bogies; }
    public int getLengthMm() { return lengthMm; }
    public double getLengthM() { return lengthMm / 1000.0; }
    public int getWidthMm() { return widthMm; }
    public int getHeightMm() { return heightMm; }
    public double getWeightTons() { return weightTons; }
    public int getMaxSpeed() { return maxSpeed; }
    public double getPayloadTons() { return payloadTons; }
    public double getVolumeM3() { return volumeM3; }
    public String getType() { return type; }
    public int getBitola() { return bitola; }

    public boolean isCompatibleWithGauge(int trackGauge) {
        return this.bitola == trackGauge;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WagonModel)) return false;
        WagonModel that = (WagonModel) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }

    @Override
    public String toString() {
        return String.format("WagonModel{id=%d, model='%s', type='%s', %.1ft, %.1ft payload}",
                id, model, type, weightTons, payloadTons);
    }
}

