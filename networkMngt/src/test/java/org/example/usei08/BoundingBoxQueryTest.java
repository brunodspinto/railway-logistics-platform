package org.example.usei08;

import org.example.domain.Station;
import org.example.queries.BoundingBoxQuery;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BoundingBoxQueryTest {

    private Station station(String name, double lat, double lon, String country, boolean isCity, boolean isMain) {
        return new Station(name, lat, lon, country, "Europe/Lisbon", "WET/GMT", isCity, isMain, false);
    }

    @Test
    @DisplayName("Match: estação dentro da bounding box sem filtros")
    void matchesStationInsideBoundsWithoutFilters() {
        Station s = station("Lisbon", 38.7, -9.1, "PT", true, true);

        BoundingBoxQuery q = new BoundingBoxQuery(38.0, 39.0, -10.0, -8.0, null, null, null);

        assertTrue(q.matches(s));
    }

    @Test
    @DisplayName("Não corresponde: estação null")
    void doesNotMatchWhenStationIsNull() {
        BoundingBoxQuery q = new BoundingBoxQuery(38.0, 39.0, -10.0, -8.0, null, null, null);
        assertFalse(q.matches(null));
    }

    @Test
    @DisplayName("Não corresponde: latitude fora dos limites")
    void doesNotMatchOutsideLatitudeRange() {
        Station s = station("North", 41.0, -9.0, "PT", true, true);

        BoundingBoxQuery q = new BoundingBoxQuery(38.0, 39.0, -10.0, -8.0, null, null, null);
        assertFalse(q.matches(s));
    }

    @Test
    @DisplayName("Não corresponde: longitude fora dos limites")
    void doesNotMatchOutsideLongitudeRange() {

        Station s = station("East", 38.5, -3.5, "ES", true, true);

        BoundingBoxQuery q = new BoundingBoxQuery(38.0, 39.0, -10.0, -8.0, null, null, null);
        assertFalse(q.matches(s));
    }

    @Test
    @DisplayName("Limites inclusivos (min e max)")
    void boundsAreInclusive() {

        Station s1 = station("MinCorner", 38.0, -10.0, "PT", false, false);

        Station s2 = station("MaxCorner", 39.0, -8.0, "PT", false, false);

        BoundingBoxQuery q = new BoundingBoxQuery(38.0, 39.0, -10.0, -8.0, null, null, null);
        assertTrue(q.matches(s1));
        assertTrue(q.matches(s2));
    }

    @Test
    @DisplayName("Filtros combinados: isCity e isMain")
    void filtersByIsCityAndIsMain() {
        Station cityMain = station("CityMain", 38.5, -9.0, "PT", true, true);

        Station cityNotMain = station("CityNotMain", 38.6, -9.0, "PT", true, false);

        Station rural = station("Rural", 38.4, -9.0, "PT", false, false);

        BoundingBoxQuery q = new BoundingBoxQuery(38.0, 39.0, -10.0, -8.0, true, true, "PT");
        assertTrue(q.matches(cityMain));
        assertFalse(q.matches(cityNotMain));
        assertFalse(q.matches(rural));
    }

    @Test
    @DisplayName("Filtro de país é case-insensitive e 'all' desativa filtro")
    void countryFilterIsCaseInsensitiveAndAllDisablesFilter() {
        Station pt = station("Lisbon", 38.7, -9.1, "PT", true, true);
        Station es = station("Madrid", 40.4, -3.7, "ES", true, true);

        BoundingBoxQuery onlyPT = new BoundingBoxQuery(35.0, 45.0, -12.0, -2.0, null, null, "pt");
        assertTrue(onlyPT.matches(pt));
        assertFalse(onlyPT.matches(es));

        BoundingBoxQuery allCountries = new BoundingBoxQuery(35.0, 45.0, -12.0, -2.0, null, null, null);
        assertTrue(allCountries.matches(pt));
        assertTrue(allCountries.matches(es));

        BoundingBoxQuery explicitAll = new BoundingBoxQuery(35.0, 45.0, -12.0, -2.0, null, null, "ALL");
        assertTrue(explicitAll.matches(pt));
        assertTrue(explicitAll.matches(es));
    }
}