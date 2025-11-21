package org.example.queries;

import org.example.trees.StationIndexes;
import java.util.*;

/**
 * USEI06 - Sample queries demonstrating functionality.
 * Provides 4 example queries.
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
     */
    public QueryResult sample1_AllCETStations() {
        System.out.println("\n=== SAMPLE QUERY 1: All CET Stations ===");
        System.out.println("Description: Find all stations in Central European Time zone");

        QueryResult result = timeZoneQuery.queryByTimeZoneGroup("CET");
        System.out.println(result.toString());

        return result;
    }

    /**
     * Sample Query 2: Portuguese stations in WET/GMT time zone.
     */
    public QueryResult sample2_PortugueseWETStations() {
        System.out.println("\n=== SAMPLE QUERY 2: Portuguese WET/GMT Stations ===");
        System.out.println("Description: Find all Portuguese stations in Western European Time");

        QueryResult result = timeZoneQuery.queryByTimeZoneGroupAndCountry("WET/GMT", "PT");
        System.out.println(result.toString());

        return result;
    }

    /**
     * Sample Query 3: Time zone window query - CET and EET.
     */
    public QueryResult sample3_TimeZoneWindow() {
        System.out.println("\n=== SAMPLE QUERY 3: Time Zone Window (CET + EET) ===");
        System.out.println("Description: Find stations in both CET and EET time zones");

        List<String> window = Arrays.asList("CET", "EET");
        QueryResult result = timeZoneQuery.queryByTimeZoneWindow(window);
        System.out.println(result.toString());

        return result;
    }

    /**
     * Sample Query 4: Duplicate coordinates test - Lisboa stations.
     */
    public QueryResult sample4_LisbonDuplicateCoordinates() {
        System.out.println("\n=== SAMPLE QUERY 4: Lisboa Duplicate Coordinates ===");
        System.out.println("Description: Find all stations at Lisboa coordinates");
        System.out.println("Coordinates: (38.71387, -9.122271)");

        QueryResult result = coordinateQuery.queryByExactCoordinates(38.71387, -9.122271);
        System.out.println(result.toString());

        return result;
    }

    /**
     * Run all sample queries.
     */
    public String runAllSamples() {
        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║          USEI06 - SAMPLE QUERIES DEMONSTRATION               ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");

        sample1_AllCETStations();
        sample2_PortugueseWETStations();
        sample3_TimeZoneWindow();
        sample4_LisbonDuplicateCoordinates();

        return "All sample queries executed successfully.";
    }
}