package org.example.domain;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Representa uma operação de cruzamento resolvida
 */
public class CrossingOperation {
    private final Train waitingTrain;
    private final Train passingTrain;
    private final Station waitingStation;
    private final LineSegment conflictSegment;
    private final LocalDateTime originalArrival;
    private final LocalDateTime delayedDeparture;
    private final long waitDurationMinutes;

    public CrossingOperation(Train waitingTrain, Train passingTrain,
                             Station waitingStation, LineSegment conflictSegment,
                             LocalDateTime originalArrival,
                             LocalDateTime delayedDeparture) {
        this.waitingTrain = waitingTrain;
        this.passingTrain = passingTrain;
        this.waitingStation = waitingStation;
        this.conflictSegment = conflictSegment;
        this.originalArrival = originalArrival;
        this.delayedDeparture = delayedDeparture;
        this.waitDurationMinutes = java.time.Duration.between(
                originalArrival, delayedDeparture).toMinutes();
    }

    public String format() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

        return String.format(
                "⚠️  CROSSING: Train %d waits at %s for %d min (until %s) while Train %d passes",
                waitingTrain.getId(),
                waitingStation.getName(),
                waitDurationMinutes,
                delayedDeparture.format(formatter),
                passingTrain.getId()
        );
    }

    // Getters
    public Train getWaitingTrain() { return waitingTrain; }
    public Train getPassingTrain() { return passingTrain; }
    public Station getWaitingStation() { return waitingStation; }
    public LineSegment getConflictSegment() { return conflictSegment; }
    public long getWaitDurationMinutes() { return waitDurationMinutes; }
    public LocalDateTime getDelayedDeparture() { return delayedDeparture; }

    @Override
    public String toString() {
        return String.format("CrossingOperation{waiting=%d, passing=%d, at=%s, wait=%dmin}",
                waitingTrain.getId(), passingTrain.getId(),
                waitingStation.getName(), waitDurationMinutes);
    }
}

