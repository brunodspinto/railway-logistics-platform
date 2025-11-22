package org.example.service;

import org.example.domain.Station;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Representa uma entrada no horário de um comboio
 * (passagem ou paragem numa estação)
 */
public class ScheduleEntry {
    private final Station station;
    private final LocalDateTime arrivalTime;
    private final LocalDateTime departureTime;
    private final double speedKmh;
    private final double segmentDistanceKm;
    private final boolean stops;

    public ScheduleEntry(Station station, LocalDateTime arrivalTime,
                         LocalDateTime departureTime, double speedKmh,
                         double segmentDistanceKm, boolean stops) {
        this.station = station;
        this.arrivalTime = arrivalTime;
        this.departureTime = departureTime;
        this.speedKmh = speedKmh;
        this.segmentDistanceKm = segmentDistanceKm;
        this.stops = stops;
    }

    // Getters
    public Station getStation() { return station; }
    public LocalDateTime getArrivalTime() { return arrivalTime; }
    public LocalDateTime getDepartureTime() { return departureTime; }
    public double getSpeedKmh() { return speedKmh; }
    public double getSegmentDistanceKm() { return segmentDistanceKm; }
    public boolean stops() { return stops; }

    public long getStopDurationMinutes() {
        if (!stops || arrivalTime.equals(departureTime)) {
            return 0;
        }
        return java.time.Duration.between(arrivalTime, departureTime).toMinutes();
    }

    /**
     * Formata entrada para display
     */
    public String format() {
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        String stopInfo;
        if (!stops) {
            stopInfo = "PASS";
        } else {
            long duration = getStopDurationMinutes();

            // Identificar operação (se tiver train disponível)
            stopInfo = String.format("STOP (%d min)", duration);

            // Se quisermos detalhar a operação:
            // stopInfo = String.format("STOP (%d min) - Loading/Unloading", duration);
        }

        return String.format("%-20s  %s  %s  %6.1f km/h  %6.1f km  %s",
                station.getName(),
                arrivalTime.format(timeFormatter),
                departureTime.format(timeFormatter),
                speedKmh,
                segmentDistanceKm,
                stopInfo);
    }

    @Override
    public String toString() {
        return String.format("ScheduleEntry{station=%s, arrival=%s, speed=%.1f km/h}",
                station.getName(), arrivalTime, speedKmh);
    }
}

