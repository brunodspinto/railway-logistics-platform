package org.example.usei06.trees;

import org.example.trees.CompositeKey;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários para CompositeKey - USEI06
 */
class CompositeKeyTest {

    @Test
    @DisplayName("Create composite key")
    void testCreateCompositeKey() {
        CompositeKey key = new CompositeKey("CET", "PT");

        assertEquals("CET", key.getTimeZoneGroup());
        assertEquals("PT", key.getCountry());
    }

    @Test
    @DisplayName("Compare by timezone first")
    void testCompareByTimeZone() {
        CompositeKey key1 = new CompositeKey("CET", "PT");
        CompositeKey key2 = new CompositeKey("WET/GMT", "PT");

        assertTrue(key1.compareTo(key2) < 0);
    }

    @Test
    @DisplayName("Compare by country when timezone equal")
    void testCompareByCountry() {
        CompositeKey key1 = new CompositeKey("CET", "ES");
        CompositeKey key2 = new CompositeKey("CET", "FR");

        assertTrue(key1.compareTo(key2) < 0);
    }

    @Test
    @DisplayName("Equal keys")
    void testEquality() {
        CompositeKey key1 = new CompositeKey("CET", "PT");
        CompositeKey key2 = new CompositeKey("CET", "PT");

        assertEquals(key1, key2);
        assertEquals(key1.hashCode(), key2.hashCode());
        assertEquals(0, key1.compareTo(key2));
    }

    @Test
    @DisplayName("Not equal keys - different timezone")
    void testInequalityTimezone() {
        CompositeKey key1 = new CompositeKey("CET", "PT");
        CompositeKey key2 = new CompositeKey("EET", "PT");

        assertNotEquals(key1, key2);
        assertNotEquals(0, key1.compareTo(key2));
    }

    @Test
    @DisplayName("ToString format")
    void testToString() {
        CompositeKey key = new CompositeKey("WET/GMT", "PT");

        assertEquals("WET/GMT/PT", key.toString());
    }
}
