package org.example.domain;

import java.util.Objects;

/**
 * Esta classe representa uma localização no armazém (corredor e baía).
 */
public final class Record {

    private final int aisle;
    private final int bay;

    // Constante para representar a entrada do armazém
    public static final Record ENTRANCE = new Record(0, 0);

    public Record(int aisle, int bay) {
        this.aisle = aisle;
        this.bay = bay;
    }

    public int getAisle() {
        return aisle;
    }

    public int getBay() {
        return bay;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Record that = (Record) o;
        return aisle == that.aisle && bay == that.bay;
    }

    @Override
    public int hashCode() {
        return Objects.hash(aisle, bay);
    }

    @Override
    public String toString() {
        return "Record[" +
                "aisle=" + aisle + ", " +
                "bay=" + bay + ']';
    }
}
