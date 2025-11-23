package org.example.queries;

import org.example.domain.Station;

/**
 * Representa uma consulta retangular (bounding box).
 * Guarda os limites de latitude e longitude definidos pelo utilizador,
 * e os filtros opcionais como isCity, isMainStation e country.
 *
 * A KD-tree usa este objeto para verificar se uma estação respeita a área e os filtros.
 */
public class BoundingBoxQuery {

    // Limites da área geográfica
    private final double minLat, maxLat, minLon, maxLon;

    // Filtros opcionais
    private final Boolean isCity;      // null = ignorar filtro
    private final Boolean isMain;      // null = ignorar filtro
    private final String country;      // "pt", "es", etc.
    private final boolean ignoreCountry;

    // Getters simples
    public double getMinLat() { return minLat; }
    public double getMaxLat() { return maxLat; }
    public double getMinLon() { return minLon; }
    public double getMaxLon() { return maxLon; }

    /**
     * Construtor da bounding box.
     * Guarda a área e prepara os filtros opcionais.
     *
     * Se o filtro 'country' for "all" (ou vazio), marca ignoreCountry=true
     * para não aplicar esse filtro mais tarde na pesquisa.
     */
    public BoundingBoxQuery(double minLatitude, double maxLatitude, double minLongitude, double maxLongitude, Boolean isCityFilter, Boolean isMainFilter, String countryFilter) {

        this.minLat = minLatitude;
        this.maxLat = maxLatitude;
        this.minLon = minLongitude;
        this.maxLon = maxLongitude;

        this.isCity = isCityFilter;
        this.isMain = isMainFilter;

        // Processamento do filtro 'country'
        if (countryFilter == null || countryFilter.isBlank() ||
                countryFilter.equalsIgnoreCase("all")) {

            this.ignoreCountry = true;
            this.country = "all";

        } else {
            this.ignoreCountry = false;
            this.country = countryFilter.trim().toLowerCase();
        }
    }

    /**
     * Verifica se uma estação está dentro da bounding box, e se passa todos os filtros opcionais.
     *
     * Este metodo NÃO altera a pesquisa na KD-tree: apenas decide se uma estação encontrada é válida.
     */
    public boolean matches(Station s) {
        if (s == null) return false;

        // Verificar latitude
        double lat = s.getLatitude();
        if (lat < minLat || lat > maxLat) return false;

        // Verificar longitude
        double lon = s.getLongitude();
        if (lon < minLon || lon > maxLon) return false;

        // Filtro opcional: cidade
        if (isCity != null && s.isCity() != isCity) return false;

        // Filtro opcional: estação principal
        if (isMain != null && s.isMainStation() != isMain) return false;

        // Filtro opcional: país
        if (!ignoreCountry) {
            if (!s.getCountry().equalsIgnoreCase(country)) return false;
        }

        return true;
    }
}