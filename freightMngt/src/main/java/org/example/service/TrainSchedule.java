package org.example.service;

import org.example.domain.Station;
import org.example.domain.Train;
import org.example.service.ScheduleEntry;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Horário completo de um comboio
 */
public class TrainSchedule {
    private final Train train;
    private final LocalDateTime departureTime;
    private final List<ScheduleEntry> entries;

    public TrainSchedule(Train train, LocalDateTime departureTime) {
        this.train = train;
        this.departureTime = departureTime;
        this.entries = new ArrayList<>();
    }

    public void addEntry(ScheduleEntry entry) {
        entries.add(entry);
    }

    public Train getTrain() {
        return train;
    }

    public LocalDateTime getDepartureTime() {
        return departureTime;
    }

    public List<ScheduleEntry> getEntries() {
        return Collections.unmodifiableList(entries);
    }

    public LocalDateTime getArrivalTime() {
        if (entries.isEmpty()) {
            return departureTime;
        }
        return entries.get(entries.size() - 1).getArrivalTime();
    }

    public long getTotalTravelMinutes() {
        return java.time.Duration.between(departureTime, getArrivalTime()).toMinutes();
    }

    public double getTotalDistanceKm() {
        return entries.stream()
                .mapToDouble(ScheduleEntry::getSegmentDistanceKm)
                .sum();
    }

    public double getAverageSpeedKmh() {
        if (getTotalTravelMinutes() == 0) return 0;
        double hours = getTotalTravelMinutes() / 60.0;
        return getTotalDistanceKm() / hours;
    }

    /**
     * Adiciona um delay a partir de uma determinada estação
     * (atualiza todos os horários subsequentes)
     */
    public void addDelay(Station station, long delayMinutes) {
        boolean foundStation = false;

        for (int i = 0; i < entries.size(); i++) {
            ScheduleEntry entry = entries.get(i);

            if (entry.getStation().equals(station)) {
                foundStation = true;
            }

            if (foundStation) {
                // Criar nova entry com horários ajustados
                LocalDateTime newArrival = entry.getArrivalTime().plusMinutes(delayMinutes);
                LocalDateTime newDeparture = entry.getDepartureTime().plusMinutes(delayMinutes);

                ScheduleEntry updatedEntry = new ScheduleEntry(
                        entry.getStation(),
                        newArrival,
                        newDeparture,
                        entry.getSpeedKmh(),
                        entry.getSegmentDistanceKm(),
                        entry.stops()
                );

                entries.set(i, updatedEntry);
            }
        }
    }

    /**
     * Obtém o tempo de chegada a uma estação específica
     */
    public LocalDateTime getArrivalTimeAt(Station station) {
        for (ScheduleEntry entry : entries) {
            if (entry.getStation().equals(station)) {
                return entry.getArrivalTime();
            }
        }
        return null;
    }

    /**
     * Obtém o tempo de saída de uma estação específica
     */
    public LocalDateTime getDepartureTimeAt(Station station) {
        for (ScheduleEntry entry : entries) {
            if (entry.getStation().equals(station)) {
                return entry.getDepartureTime();
            }
        }
        return null;
    }

    /**
     * Formata horário completo para display
     */
    public String format() {
        StringBuilder sb = new StringBuilder();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        sb.append("═".repeat(100)).append("\n");
        sb.append(String.format("  TRAIN %d - %s\n", train.getId(), train.getOperator()));
        sb.append("═".repeat(100)).append("\n");
        sb.append(String.format("  Departure:    %s\n", departureTime.format(dateTimeFormatter)));
        sb.append(String.format("  Arrival:      %s\n", getArrivalTime().format(dateTimeFormatter)));
        sb.append(String.format("  Duration:     %dh %02dm\n",
                getTotalTravelMinutes() / 60, getTotalTravelMinutes() % 60));
        sb.append(String.format("  Distance:     %.1f km\n", getTotalDistanceKm()));
        sb.append(String.format("  Avg Speed:    %.1f km/h\n", getAverageSpeedKmh()));
        sb.append(String.format("  Locomotives:  %d (Total power: %d kW)\n",
                train.getLocomotives().size(), train.getTotalPowerKw()));
        sb.append(String.format("  Freights:     %d (Total weight: %.1f tons)\n",
                train.getFreights().size(), train.getTotalWeightTons()));
        sb.append("─".repeat(100)).append("\n");
        sb.append(String.format("%-20s  %-5s  %-5s  %-12s  %-10s  %-15s\n",
                "Station", "Arr.", "Dep.", "Speed", "Distance", "Operation"));
        sb.append("─".repeat(100)).append("\n");

        for (ScheduleEntry entry : entries) {
            sb.append(entry.format()).append("\n");
        }

        sb.append("═".repeat(100)).append("\n");

        return sb.toString();
    }

    @Override
    public String toString() {
        return String.format("TrainSchedule{train=%d, departure=%s, entries=%d}",
                train.getId(), departureTime, entries.size());
    }
}

