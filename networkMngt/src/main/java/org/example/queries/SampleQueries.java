package org.example.queries;

import org.example.trees.StationIndexes;
import java.util.*;

/**
 * Sample queries demonstrating USEI06 functionality.
 * These are the 3-5 example queries required in the deliverable.
 */
public class SampleQueries {
    private final StationIndexes indexes;
    private final TimeZoneQuery timeZoneQuery;
    private final CoordinateQuery coordinateQuery;

    public SampleQueries(StationIndexes indexes) {
        this.indexes = indexes;
        this.timeZoneQuery = new TimeZoneQuery(indexes);
        this.coordinateQuery = new CoordinateQuery(indexes);
    }

    /**
     * Sample Query 1: All CET time zone stations, ordered by country.
     *
     * Purpose: Demonstrates time zone group query with country ordering.
     * Expected: Stations from Germany, France, Italy, etc. in CET zone.
     */
    public QueryResult sample1_AllCETStations() {
        System.out.println("\n=== SAMPLE QUERY 1: All CET Stations ===");
        System.out.println("Description: Find all stations in Central European Time zone");
        System.out.println("Expected: Stations from DE, FR, IT, ES, etc. ordered by country\n");

        QueryResult result = timeZoneQuery.queryByTimeZoneGroup("CET");
        System.out.println(result.toReport());
        System.out.println(result.getComplexityAnalysis());

        return result;
    }

    /**
     * Sample Query 2: Portuguese stations in WET/GMT time zone.
     *
     * Purpose: Demonstrates combined time zone + country filter.
     * Expected: Stations from Portugal only (Lisboa, Porto, Funchal, Faro, etc.).
     */
    public QueryResult sample2_PortugueseWETStations() {
        System.out.println("\n=== SAMPLE QUERY 2: Portuguese WET/GMT Stations ===");
        System.out.println("Description: Find all Portuguese stations in Western European Time");
        System.out.println("Expected: Lisboa, Porto, Funchal, Faro, etc.\n");

        QueryResult result = timeZoneQuery.queryByTimeZoneGroupAndCountry("WET/GMT", "PT");
        System.out.println(result.toReport());
        System.out.println(result.getComplexityAnalysis());

        return result;
    }

    /**
     * Sample Query 3: Time zone window query - CET and EET.
     *
     * Purpose: Demonstrates window query across multiple time zones.
     * Expected: Stations from Central and Eastern European time zones.
     */
    public QueryResult sample3_TimeZoneWindow() {
        System.out.println("\n=== SAMPLE QUERY 3: Time Zone Window (CET + EET) ===");
        System.out.println("Description: Find stations in both CET and EET time zones");
        System.out.println("Expected: Combined results from both zones, ordered by country\n");

        List<String> window = Arrays.asList("CET", "EET");
        QueryResult result = timeZoneQuery.queryByTimeZoneWindow(window);
        System.out.println(result.toReport());
        System.out.println(result.getComplexityAnalysis());

        return result;
    }

    /**
     * Sample Query 4: Iberian Peninsula stations (geographic bounding box).
     *
     * Purpose: Demonstrates coordinate range query.
     * Expected: Stations from Spain and Portugal (lat: 36-44, lon: -10 to 3).
     */
    public QueryResult sample4_IberianPeninsula() {
        System.out.println("\n=== SAMPLE QUERY 4: Iberian Peninsula Stations ===");
        System.out.println("Description: Find all stations in Iberian Peninsula bounding box");
        System.out.println("Coordinates: Lat [36.0, 44.0], Lon [-10.0, 3.0]");
        System.out.println("Expected: Stations from Spain and Portugal\n");

        QueryResult result = coordinateQuery.queryByBoundingBox(
                36.0, 44.0,   // latitude range
                -10.0, 3.0    // longitude range
        );
        System.out.println(result.toReport());
        System.out.println(result.getComplexityAnalysis());

        return result;
    }

    /**
     * Sample Query 5: Duplicate coordinates test - Lisboa stations.
     *
     * Purpose: Demonstrates handling of multiple stations at same coordinates.
     * Expected: Lisboa Santa Apolónia and Lisboa Oriente (both at same coords).
     */
    public QueryResult sample5_LisbonDuplicateCoordinates() {
        System.out.println("\n=== SAMPLE QUERY 5: Lisboa Duplicate Coordinates ===");
        System.out.println("Description: Find all stations at Lisboa coordinates");
        System.out.println("Coordinates: (38.71387, -9.122271)");
        System.out.println("Expected: Multiple Lisboa stations, sorted by name\n");

        // Lisboa approximate coordinates
        QueryResult result = coordinateQuery.queryByExactCoordinates(
                38.71387,
                -9.122271
        );
        System.out.println(result.toReport());
        System.out.println(result.getComplexityAnalysis());

        return result;
    }

    /**
     * Run all sample queries and generate comprehensive report.
     */
    public String runAllSamples() {
        StringBuilder report = new StringBuilder();
        report.append("╔══════════════════════════════════════════════════════════════╗\n");
        report.append("║          USEI06 - SAMPLE QUERIES DEMONSTRATION              ║\n");
        report.append("╚══════════════════════════════════════════════════════════════╝\n");

        List<QueryResult> results = new ArrayList<>();

        // Run all queries
        results.add(sample1_AllCETStations());
        results.add(sample2_PortugueseWETStations());
        results.add(sample3_TimeZoneWindow());
        results.add(sample4_IberianPeninsula());
        results.add(sample5_LisbonDuplicateCoordinates());

        // Summary statistics
        report.append("\n\n╔══════════════════════════════════════════════════════════════╗\n");
        report.append("║                    SUMMARY STATISTICS                        ║\n");
        report.append("╚══════════════════════════════════════════════════════════════╝\n\n");

        int totalResults = results.stream().mapToInt(QueryResult::getStationCount).sum();
        long totalTime = results.stream().mapToLong(QueryResult::getExecutionTimeMs).sum();
        double avgTime = totalTime / (double) results.size();

        report.append(String.format("Total queries executed: %d\n", results.size()));
        report.append(String.format("Total stations returned: %d\n", totalResults));
        report.append(String.format("Total execution time: %d ms\n", totalTime));
        report.append(String.format("Average execution time: %.2f ms\n", avgTime));

        // Index performance
        report.append("\n").append(indexes.getPerformanceReport());

        return report.toString();
    }

    /**
     * Additional demo: Country distribution analysis.
     */
    public void demonstrateCountryDistribution() {
        System.out.println("\n=== BONUS: Country Distribution in CET ===");
        Map<String, Long> distribution = timeZoneQuery.getCountryDistribution("CET");

        distribution.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .forEach(e -> System.out.printf("%s: %d stations\n", e.getKey(), e.getValue()));
    }

    /**
     * Additional demo: Time zone distribution analysis.
     */
    public void demonstrateTimeZoneDistribution() {
        System.out.println("\n=== BONUS: Time Zone Distribution ===");
        Map<String, Long> distribution = timeZoneQuery.getTimeZoneDistribution();

        distribution.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .forEach(e -> System.out.printf("%s: %d stations\n", e.getKey(), e.getValue()));
    }

    /**
     * Additional demo: Geographic distribution summary.
     */
    public void demonstrateGeographicDistribution() {
        System.out.println("\n=== BONUS: Geographic Distribution ===");

        System.out.println("\nLatitude Statistics:");
        Map<String, Object> latStats = coordinateQuery.getLatitudeDistributionSummary();
        latStats.forEach((k, v) -> System.out.printf("  %s: %s\n", k, v));

        System.out.println("\nLongitude Statistics:");
        Map<String, Object> lonStats = coordinateQuery.getLongitudeDistributionSummary();
        lonStats.forEach((k, v) -> System.out.printf("  %s: %s\n", k, v));
    }
}
