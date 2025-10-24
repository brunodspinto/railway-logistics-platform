package org.example.usei04.service;

import org.example.domain.*;

public class TravelTimeResult {
    private final Station origin;
    private final Station destination;
    private final Line line;
    private final Locomotive locomotive;
    private final double distanceKm;
    private final int effectiveSpeedKmh;
    private final double timeHours;

    public TravelTimeResult(Station origin, Station destination, Line line,
                            Locomotive locomotive, double distanceKm,
                            int effectiveSpeedKmh, double timeHours) {
        this.origin = origin;
        this.destination = destination;
        this.line = line;
        this.locomotive = locomotive;
        this.distanceKm = distanceKm;
        this.effectiveSpeedKmh = effectiveSpeedKmh;
        this.timeHours = timeHours;
    }

    public void printReport() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("           TRAVEL TIME CALCULATION REPORT");
        System.out.println("=".repeat(60));
        System.out.printf("Route:        %s → %s%n", origin.getName(), destination.getName());
        System.out.printf("Line:         %s%n", line.getName());
        System.out.printf("Locomotive:   %s (%s %s)%n",
                locomotive.getName(), locomotive.getMake(), locomotive.getModel());
        System.out.println("-".repeat(60));
        System.out.printf("Distance:     %.2f km%n", distanceKm);
        System.out.printf("Line Speed:   %d km/h%n", line.getMinMaxSpeed());
        System.out.printf("Loco Speed:   %d km/h%n", locomotive.getMaxSpeed());
        System.out.printf("Effective:    %d km/h (minimum)%n", effectiveSpeedKmh);
        System.out.println("-".repeat(60));
        System.out.printf(" ESTIMATED TIME: %.2f hours (%.0f minutes)%n",
                timeHours, timeHours * 60);
        System.out.println("=".repeat(60));

        System.out.println("\nSegments:");
        for (LineSegment seg : line.getSegments()) {
            System.out.printf("  • Order %d: %.1f km, %s, %d tracks%n",
                    seg.getOrder(), seg.getLengthKm(),
                    seg.isElectrified() ? "electrified" : "non-electrified",
                    seg.getNumberTracks());
        }
    }

    // Getters
    public double getTimeHours() { return timeHours; }
    public double getDistanceKm() { return distanceKm; }
    public int getEffectiveSpeedKmh() { return effectiveSpeedKmh; }

    public Station getOrigin() { return origin; }
    public Station getDestination() { return destination; }
    public Line getLine() { return line; }
    public Locomotive getLocomotive() { return locomotive; }
}

