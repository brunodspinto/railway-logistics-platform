package org.example.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Train {
    private final int id;
    private final String operator;
    private final LocalDate date;
    private final LocalTime time;

    // ✅ REMOVER "final" destes 3:
    private int startId;
    private int endId;
    private List<Integer> pathStationIds;

    private final List<Integer> freightIds;
    private final List<Integer> locomotiveNumbers;

    // Lazy-loaded
    private Station startStation;
    private Station endStation;
    private List<Freight> freights;
    private List<Locomotive> locomotives;
    private List<Station> pathStations;

    public Train(int id, String operator, LocalDate date, LocalTime time,
                 int startId, int endId, List<Integer> freightIds,
                 List<Integer> locomotiveNumbers, List<Integer> pathStationIds) {

        if (operator == null || operator.isBlank()) {
            throw new IllegalArgumentException("Operator cannot be empty");
        }
        if (freightIds == null || freightIds.isEmpty()) {
            throw new IllegalArgumentException("Train must have at least one freight");
        }
        if (locomotiveNumbers == null || locomotiveNumbers.isEmpty()) {
            throw new IllegalArgumentException("Train must have at least one locomotive");
        }
        if (pathStationIds == null || pathStationIds.size() < 2) {
            throw new IllegalArgumentException("Path must have at least origin and destination");
        }

        this.id = id;
        this.operator = operator;
        this.date = date;
        this.time = time;
        this.startId = startId;
        this.endId = endId;
        this.freightIds = new ArrayList<>(freightIds);
        this.locomotiveNumbers = new ArrayList<>(locomotiveNumbers);
        this.pathStationIds = new ArrayList<>(pathStationIds);

        this.freights = new ArrayList<>();
        this.locomotives = new ArrayList<>();
        this.pathStations = new ArrayList<>();
    }

    // ✅ SETTERS (agora funcionam porque não são final)
    public void setPathStationIds(List<Integer> pathStationIds) {
        if (pathStationIds == null || pathStationIds.size() < 2) {
            throw new IllegalArgumentException("Path must have at least 2 stations");
        }
        this.pathStationIds = new ArrayList<>(pathStationIds);
    }

    public void setStartId(int startId) {
        this.startId = startId;
    }

    public void setEndId(int endId) {
        this.endId = endId;
    }

    public void setStartStation(Station station) {
        this.startStation = station;
    }

    public void setEndStation(Station station) {
        this.endStation = station;
    }

    public void setFreights(List<Freight> freights) {
        this.freights = new ArrayList<>(freights);
    }

    public void setLocomotives(List<Locomotive> locomotives) {
        this.locomotives = new ArrayList<>(locomotives);
    }

    public void setPathStations(List<Station> stations) {
        this.pathStations = new ArrayList<>(stations);
    }

    // Métodos de cálculo
    public double getTotalWeightTons() {
        double locoWeight = locomotives.stream()
                .mapToDouble(Locomotive::getWeight)
                .sum();

        double freightWeight = freights.stream()
                .mapToDouble(Freight::getTotalWeightTons)
                .sum();

        return locoWeight + freightWeight;
    }

    public int getMaxSpeed() {
        return locomotives.stream()
                .mapToInt(Locomotive::getMaxSpeed)
                .min()
                .orElse(80);
    }

    public int getTotalPowerKw() {
        return locomotives.stream()
                .mapToInt(Locomotive::getPower)
                .sum();
    }

    public LocalDateTime getDepartureDateTime() {
        return LocalDateTime.of(date, time);
    }

    public boolean isElectricOnly() {
        return locomotives.stream().allMatch(Locomotive::isElectric);
    }

    public boolean hasElectricLocomotive() {
        return locomotives.stream().anyMatch(Locomotive::isElectric);
    }

    // Getters
    public int getId() { return id; }
    public String getOperator() { return operator; }
    public LocalDate getDate() { return date; }
    public LocalTime getTime() { return time; }
    public int getStartId() { return startId; }
    public int getEndId() { return endId; }
    public List<Integer> getFreightIds() { return Collections.unmodifiableList(freightIds); }
    public List<Integer> getLocomotiveNumbers() { return Collections.unmodifiableList(locomotiveNumbers); }
    public List<Integer> getPathStationIds() { return Collections.unmodifiableList(pathStationIds); }

    public Station getStartStation() { return startStation; }
    public Station getEndStation() { return endStation; }
    public List<Freight> getFreights() { return Collections.unmodifiableList(freights); }
    public List<Locomotive> getLocomotives() { return Collections.unmodifiableList(locomotives); }
    public List<Station> getPathStations() { return Collections.unmodifiableList(pathStations); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Train)) return false;
        Train train = (Train) o;
        return id == train.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }

    @Override
    public String toString() {
        return String.format("Train{id=%d, operator='%s', %s %s, %d freights, %d locos}",
                id, operator,
                date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                time.format(DateTimeFormatter.ofPattern("HH:mm")),
                freightIds.size(), locomotiveNumbers.size());
    }
}