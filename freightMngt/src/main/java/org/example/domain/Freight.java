package org.example.domain;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Freight {
    private final int id;
    private final LocalDate date;
    private final int originId;
    private final String originName;
    private final int destinationId;
    private final String destinationName;
    private final List<String> wagonNumbers;

    // Lazy-loaded
    private Station origin;
    private Station destination;
    private List<Wagon> wagons;

    public Freight(int id, LocalDate date, int originId, String originName,
                   int destinationId, String destinationName, List<String> wagonNumbers) {

        if (wagonNumbers == null || wagonNumbers.isEmpty()) {
            throw new IllegalArgumentException("Freight must have at least one wagon");
        }

        this.id = id;
        this.date = date;
        this.originId = originId;
        this.originName = originName;
        this.destinationId = destinationId;
        this.destinationName = destinationName;
        this.wagonNumbers = new ArrayList<>(wagonNumbers);
        this.wagons = new ArrayList<>();
    }

    // Setters para lazy loading
    public void setOrigin(Station origin) {
        if (origin != null && origin.getId() != this.originId) {
            throw new IllegalArgumentException("Origin station ID mismatch");
        }
        this.origin = origin;
    }

    public void setDestination(Station destination) {
        if (destination != null && destination.getId() != this.destinationId) {
            throw new IllegalArgumentException("Destination station ID mismatch");
        }
        this.destination = destination;
    }

    public void setWagons(List<Wagon> wagons) {
        this.wagons = new ArrayList<>(wagons);
    }

    public void addWagon(Wagon wagon) {
        if (!wagonNumbers.contains(wagon.getNumber())) {
            throw new IllegalArgumentException(
                    "Wagon " + wagon.getNumber() + " not in freight manifest");
        }
        this.wagons.add(wagon);
    }

    // Métodos de cálculo
    public double getTotalWeightTons() {
        return wagons.stream()
                .mapToDouble(w -> w.getTareWeightTons())
                .sum();
    }

    public double getTotalPayloadCapacityTons() {
        return wagons.stream()
                .mapToDouble(w -> w.getMaxPayloadTons())
                .sum();
    }

    public int getWagonCount() {
        return wagonNumbers.size();
    }

    // Getters
    public int getId() { return id; }
    public LocalDate getDate() { return date; }
    public int getOriginId() { return originId; }
    public String getOriginName() { return originName; }
    public int getDestinationId() { return destinationId; }
    public String getDestinationName() { return destinationName; }
    public List<String> getWagonNumbers() { return Collections.unmodifiableList(wagonNumbers); }
    public Station getOrigin() { return origin; }
    public Station getDestination() { return destination; }
    public List<Wagon> getWagons() { return Collections.unmodifiableList(wagons); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Freight)) return false;
        Freight freight = (Freight) o;
        return id == freight.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }

    @Override
    public String toString() {
        return String.format("Freight{id=%d, %s→%s, %d wagons, date=%s}",
                id, originName, destinationName, wagonNumbers.size(),
                date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
    }
}
