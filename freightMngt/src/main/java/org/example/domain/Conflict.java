package org.example.domain;

import java.time.LocalDateTime;

/**
 * Representa um conflito detectado entre dois trains
 */
public class Conflict {
    private final Train train1;
    private final Train train2;
    private final LineSegment segment;
    private final LocalDateTime train1EntryTime;
    private final LocalDateTime train1ExitTime;
    private final LocalDateTime train2EntryTime;
    private final LocalDateTime train2ExitTime;

    public Conflict(Train train1, Train train2, LineSegment segment,
                    LocalDateTime t1Entry, LocalDateTime t1Exit,
                    LocalDateTime t2Entry, LocalDateTime t2Exit) {
        this.train1 = train1;
        this.train2 = train2;
        this.segment = segment;
        this.train1EntryTime = t1Entry;
        this.train1ExitTime = t1Exit;
        this.train2EntryTime = t2Entry;
        this.train2ExitTime = t2Exit;
    }

    /**
     * Verifica se há sobreposição temporal
     */
    public boolean hasTemporalOverlap() {
        return !(train1ExitTime.isBefore(train2EntryTime) ||
                train2ExitTime.isBefore(train1EntryTime));
    }

    /**
     * Determina qual train deve esperar (baseado em quem chega primeiro)
     */
    public Train getWaitingTrain() {
        // O que chega depois é quem espera
        return train2EntryTime.isAfter(train1EntryTime) ? train2 : train1;
    }

    public Train getPassingTrain() {
        return getWaitingTrain() == train1 ? train2 : train1;
    }

    // Getters
    public Train getTrain1() { return train1; }
    public Train getTrain2() { return train2; }
    public LineSegment getSegment() { return segment; }
    public LocalDateTime getTrain1EntryTime() { return train1EntryTime; }
    public LocalDateTime getTrain1ExitTime() { return train1ExitTime; }
    public LocalDateTime getTrain2EntryTime() { return train2EntryTime; }
    public LocalDateTime getTrain2ExitTime() { return train2ExitTime; }

    @Override
    public String toString() {
        return String.format("Conflict{trains=%d vs %d, segment=%d, overlap=%b}",
                train1.getId(), train2.getId(), segment.getId(), hasTemporalOverlap());
    }
}

