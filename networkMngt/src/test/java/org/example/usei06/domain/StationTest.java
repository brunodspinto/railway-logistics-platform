package org.example.usei06.domain;

import org.example.domain.Station;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários para Station - USEI06
 */
class StationTest {

    @Test
    @DisplayName("Create valid station")
    void testCreateValidStation() {
        Station station = new Station("Lisboa Santa Apolónia", 38.71387, -9.12227,
                "PT", "Europe/Lisbon", "WET/GMT",
                true, true, false);

        assertEquals("Lisboa Santa Apolónia", station.getName());
        assertEquals(38.71387, station.getLatitude(), 0.00001);
        assertEquals(-9.12227, station.getLongitude(), 0.00001);
        assertEquals("PT", station.getCountry());
        assertEquals("WET/GMT", station.getTimeZoneGroup());
        assertTrue(station.isValid());
        assertNull(station.getValidationError());
    }

    @Test
    @DisplayName("Validation - invalid latitude")
    void testInvalidLatitude() {
        Station station = new Station("Test", 100.0, -9.0,
                "PT", "Europe/Lisbon", "WET/GMT",
                true, true, false);

        assertFalse(station.isValid());
        assertNotNull(station.getValidationError());
        assertTrue(station.getValidationError().contains("Latitude"));
    }

    @Test
    @DisplayName("Validation - invalid longitude")
    void testInvalidLongitude() {
        Station station = new Station("Test", 38.0, -200.0,
                "PT", "Europe/Lisbon", "WET/GMT",
                true, true, false);

        assertFalse(station.isValid());
        assertNotNull(station.getValidationError());
        assertTrue(station.getValidationError().contains("Longitude"));
    }

    @Test
    @DisplayName("Validation - empty name")
    void testEmptyName() {
        Station station = new Station("", 38.0, -9.0,
                "PT", "Europe/Lisbon", "WET/GMT",
                true, true, false);

        assertFalse(station.isValid());
        assertTrue(station.getValidationError().contains("name"));
    }

    @Test
    @DisplayName("Compare stations by name")
    void testCompareTo() {
        Station lisboa = new Station("Lisboa", 38.7, -9.1, "PT",
                "Europe/Lisbon", "WET/GMT", true, true, false);
        Station porto = new Station("Porto", 41.1, -8.6, "PT",
                "Europe/Lisbon", "WET/GMT", true, true, false);

        assertTrue(lisboa.compareTo(porto) < 0);
        assertTrue(porto.compareTo(lisboa) > 0);
        assertEquals(0, lisboa.compareTo(lisboa));
    }

    @Test
    @DisplayName("Stations with same coordinates are equal")
    void testEquality() {
        Station s1 = new Station("Lisboa Oriente", 38.71387, -9.12227, "PT",
                "Europe/Lisbon", "WET/GMT", true, true, false);
        Station s2 = new Station("Lisboa Oriente", 38.71387, -9.12227, "PT",
                "Europe/Lisbon", "WET/GMT", true, true, false);

        assertEquals(s1, s2);
        assertEquals(s1.hashCode(), s2.hashCode());
    }
}
