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

    private void printResults(String queryName, String description, String queryCode, List<Station> list) {
        System.out.println("\n=== " + queryName + " ===");
        System.out.println("Description: " + description);
        System.out.println("Query: " + queryCode);
        System.out.printf("Results: %d stations%n%n", list.size());

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

    // 1. Hubs Urbanos Ibéricos
    public void sample1_IberianUrbanHubs() {
        List<Station> result = spatialQueryService.queryArea(37.0, 43.0, -10.0, 4.0, true, true, "all");

        printResults(
                "SAMPLE QUERY 1: Iberian Cities",
                "Main city stations in PT and ES.",
                "USEI08_IBERIAN_URBAN",
                result
        );
    }

    // 2. Faixa Atlântica PT–ES
    public void sample2_AtlanticBand() {
        List<Station> result = spatialQueryService.queryArea(36.0, 44.0, -10.0, -5.0, null, null, "all");

        printResults(
                "SAMPLE QUERY 2: Atlantic Strip",
                "All stations from PT and North ES coastline.",
                "USEI08_ATLANTIC",
                result
        );
    }

    // 3. Grandes Estações de França
    public void sample3_FranceMainStations() {
        List<Station> result = spatialQueryService.queryArea(41.0, 51.5, -5.5, 10.0, null, true, "FR");

        printResults(
                "SAMPLE QUERY 3: Major French Stations",
                "All main stations in France.",
                "USEI08_FR_MAIN",
                result
        );
    }

    // 4. Região de Paris
    public void sample4_ParisDenseArea() {
        List<Station> result = spatialQueryService.queryArea(48.7, 48.95, 2.2, 2.45, null, null, "FR");

        printResults(
                "SAMPLE QUERY 4: Paris Zone",
                "Stations in a dense region around Paris.",
                "USEI08_PARIS",
                result
        );
    }

    // 5. Fronteira PT–ES
    public void sample5_IberianBorder() {
        List<Station> result = spatialQueryService.queryArea(38.0, 43.0, -7.5, -6.0, null, null, "all");

        printResults(
                "SAMPLE QUERY 5: Iberian Border",
                "Stations along the border between PT and ES.",
                "USEI08_BORDER",
                result
        );
    }

    public void runAllSamples() {
        sample1_IberianUrbanHubs();
        sample2_AtlanticBand();
        sample3_FranceMainStations();
        sample4_ParisDenseArea();
        sample5_IberianBorder();

        System.out.println("\nAll Sample Queries were executed successfully.\n");
    }
}