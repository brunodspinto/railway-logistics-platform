package org.example.domain;

public class Wagon {
    private final String number;        // "3330001"
    private final int modelId;
    private final String operator;
    private final int serviceYear;

    // Lazy-loaded (será preenchido pelo repository)
    private WagonModel model;

    public Wagon(String number, int modelId, String operator, int serviceYear) {
        if (number == null || number.isBlank()) {
            throw new IllegalArgumentException("Wagon number cannot be empty");
        }
        if (operator == null || operator.isBlank()) {
            throw new IllegalArgumentException("Operator cannot be empty");
        }

        this.number = number;
        this.modelId = modelId;
        this.operator = operator;
        this.serviceYear = serviceYear;
    }

    // Setters para lazy loading
    public void setModel(WagonModel model) {
        if (model != null && model.getId() != this.modelId) {
            throw new IllegalArgumentException("Model ID mismatch");
        }
        this.model = model;
    }

    // Getters
    public String getNumber() { return number; }
    public int getModelId() { return modelId; }
    public String getOperator() { return operator; }
    public int getServiceYear() { return serviceYear; }
    public WagonModel getModel() { return model; }

    // Métodos de conveniência (delegam para model)
    public double getTareWeightTons() {
        if (model == null) {
            throw new IllegalStateException("Model not loaded for wagon " + number);
        }
        return model.getWeightTons();
    }

    public double getMaxPayloadTons() {
        if (model == null) {
            throw new IllegalStateException("Model not loaded for wagon " + number);
        }
        return model.getPayloadTons();
    }

    public int getMaxSpeed() {
        if (model == null) {
            throw new IllegalStateException("Model not loaded for wagon " + number);
        }
        return model.getMaxSpeed();
    }

    public int getBitola() {
        if (model == null) {
            throw new IllegalStateException("Model not loaded for wagon " + number);
        }
        return model.getBitola();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Wagon)) return false;
        Wagon wagon = (Wagon) o;
        return number.equals(wagon.number);
    }

    @Override
    public int hashCode() {
        return number.hashCode();
    }

    @Override
    public String toString() {
        return String.format("Wagon{number='%s', model=%s, operator='%s'}",
                number, model != null ? model.getModel() : "ID:" + modelId, operator);
    }
}

