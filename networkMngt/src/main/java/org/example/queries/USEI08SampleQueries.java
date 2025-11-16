package org.example.queries;

import org.example.domain.Station;
import org.example.service.SpatialQueryService;
import org.example.trees.StationIndexes;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class USEI08SampleQueries {

    private final SpatialQueryService spatialQueryService;

    public USEI08SampleQueries(StationIndexes indexes) {
        this.spatialQueryService = new SpatialQueryService(indexes);
    }

    // Utilitário comum para imprimir os resultados com o mesmo formato
    private void printResults(String queryName, String description, String queryCode, List<Station> list) {
        System.out.println("\n=== " + queryName + " ===");
        System.out.println("Description: " + description);
        System.out.println("Query: " + queryCode);
        System.out.printf("Results: %d stations%n%n", list.size());

        // Ordenação por longitude crescente
        Collections.sort(list, Comparator.comparingDouble(Station::getLongitude));

        if (list.isEmpty()) {
            System.out.println("No results found.");
            return;
        }

        if (list.size() <= 20) {
            System.out.printf("The %d Stations:%n", list.size());
            for (Station s : list) {
                System.out.println("  " + s);
            }
        } else {
            int total = list.size();
            System.out.println("First 10:");
            for (int i = 0; i < 10; i++) {
                System.out.println("  " + list.get(i));
            }
            System.out.printf("  ... %d middle stations omitted ...%n", total - 20);
            System.out.println("\nLast 10:");
            for (int i = total - 10; i < total; i++) {
                System.out.println("  " + list.get(i));
            }
        }
    }

    // SAMPLE QUERY 1
    public void sample1_PortugalAll() {
        List<Station> result = spatialQueryService.queryArea(
                36.5, 42.5, -9.5, -6.0, null, null, "PT"
        );
        printResults(
                "SAMPLE QUERY 1: All Portuguese Stations",
                "Find all stations in Portugal.",
                "USEI08_PT_ALL",
                result
        );
    }

    // SAMPLE QUERY 2
    public void sample2_SpainMainCities() {
        List<Station> result = spatialQueryService.queryArea(
                36.0, 44.0, -10.0, 4.0, true, true, "ES"
        );
        printResults(
                "SAMPLE QUERY 2: Main City Stations in Spain",
                "Find all main city stations in Spain.",
                "USEI08_ES_MAIN",
                result
        );
    }

    // SAMPLE QUERY 3
    public void sample3_WesternEuropeBand() {
        List<Station> result = spatialQueryService.queryArea(
                35.0, 60.0, -10.0, 10.0, null, null, "all"
        );
        printResults(
                "SAMPLE QUERY 3: Western Europe Longitude Band",
                "Find stations between longitudes -10 and 10 across PT, ES, and FR.",
                "USEI08_WEST_BAND",
                result
        );
    }

    // SAMPLE QUERY 4
    public void sample4_LisbonArea() {
        List<Station> result = spatialQueryService.queryArea(
                38.6, 38.8, -9.3, -9.0, null, null, "PT"
        );
        printResults(
                "SAMPLE QUERY 4: Lisbon Area Stations",
                "Find all stations near Lisbon coordinates.",
                "USEI08_LISBON",
                result
        );
    }

    // Run all
    public void runAllSamples() {
        sample1_PortugalAll();
        sample2_SpainMainCities();
        sample3_WesternEuropeBand();
        sample4_LisbonArea();

        System.out.println("\nAll sample queries were executed successfully.\n");
    }
}