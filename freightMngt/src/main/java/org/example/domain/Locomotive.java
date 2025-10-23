package org.example.domain;

public class Locomotive {
    private final int number;
    private final String name;
    private final String make;
    private final String model;
    private final int serviceYear;
    private final int power; // kW
    private final double length; // metros
    private final double weight; // toneladas
    private final int maxSpeed; // km/h
    private final int operationalSpeed; // km/h
    private final String type; // "Electric" ou "Diesel"
    private final int gauge; // bitola (1668 mm)
    private final Integer fuelCapacity; // litros (null se elétrica)

    public Locomotive(int number, String name, String make, String model,
                      int serviceYear, int power, double length, double weight,
                      int maxSpeed, int operationalSpeed, String type,
                      int gauge, Integer fuelCapacity) {

        if (maxSpeed <= 0) {
            throw new IllegalArgumentException("Max speed must be positive");
        }
        if (type == null || (!type.equals("Electric") && !type.equals("Diesel"))) {
            throw new IllegalArgumentException("Type must be 'Electric' or 'Diesel'");
        }

        this.number = number;
        this.name = name;
        this.make = make;
        this.model = model;
        this.serviceYear = serviceYear;
        this.power = power;
        this.length = length;
        this.weight = weight;
        this.maxSpeed = maxSpeed;
        this.operationalSpeed = operationalSpeed;
        this.type = type;
        this.gauge = gauge;
        this.fuelCapacity = fuelCapacity;
    }

    public boolean isElectric() {
        return "Electric".equals(type);
    }

    public boolean canRunOnElectrifiedTrack() {
        return isElectric();
    }

    public boolean isCompatibleWithGauge(int trackGauge) {
        return this.gauge == trackGauge;
    }

    // Getters
    public int getNumber() { return number; }
    public String getName() { return name; }
    public int getMaxSpeed() { return maxSpeed; }
    public int getOperationalSpeed() { return operationalSpeed; }
    public String getType() { return type; }
    public int getGauge() { return gauge; }
    public String getMake() { return make; }
    public String getModel() { return model; }
    public int getServiceYear() { return serviceYear; }
    public int getPower() { return power; }
    public double getWeight() { return weight; }

    @Override
    public String toString() {
        return String.format("Locomotive{%d '%s', %s %s, %d km/h, %s}",
                number, name, make, model, maxSpeed, type);
    }
}

