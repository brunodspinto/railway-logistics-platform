package org.example.trees;

import java.util.Objects;

/**
 * Composite key for TimeZone + Country indexing.
 * Used in AVL tree for efficient time zone queries.
 */
public class CompositeKey implements Comparable<CompositeKey> {
    private final String timeZoneGroup;
    private final String country;

    public CompositeKey(String timeZoneGroup, String country) {
        this.timeZoneGroup = timeZoneGroup;
        this.country = country;
    }

    public String getTimeZoneGroup() {
        return timeZoneGroup;
    }

    public String getCountry() {
        return country;
    }

    /**
     * Compare: first by timeZoneGroup, then by country (both ASC).
     */
    @Override
    public int compareTo(CompositeKey other) {
        int tzCompare = this.timeZoneGroup.compareTo(other.timeZoneGroup);
        if (tzCompare != 0) {
            return tzCompare;
        }
        return this.country.compareTo(other.country);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CompositeKey)) return false;
        CompositeKey that = (CompositeKey) o;
        return Objects.equals(timeZoneGroup, that.timeZoneGroup) &&
                Objects.equals(country, that.country);
    }

    @Override
    public int hashCode() {
        return Objects.hash(timeZoneGroup, country);
    }

    @Override
    public String toString() {
        return timeZoneGroup + "/" + country;
    }
}

