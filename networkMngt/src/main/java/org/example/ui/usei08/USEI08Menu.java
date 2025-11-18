package org.example.ui.usei08;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

import org.example.domain.Station;
import org.example.queries.USEI08SampleQueries;
import org.example.service.SpatialQueryService;
import org.example.service.StationService;

public class USEI08Menu {

    private final Scanner input;
    private final StationService service;
    private final SpatialQueryService spatialService;

    public USEI08Menu(StationService service) {
        this.input = new Scanner(System.in);
        this.service = service;
        this.spatialService = new SpatialQueryService(service.getIndexes());
    }

    // --------- MAIN MENU ----------
    public void run() {
        while (true) {
            System.out.println("\n[MAIN MENU]");
            System.out.println("1. Search by Geographical Area");
            System.out.println("2. Sample Queries");
            System.out.println("0. Exit");
            System.out.print("\nOption: ");

            String opt = input.nextLine().trim();
            switch (opt) {
                case "1":
                    runSearchFlow();
                    break;
                case "2":
                    runSamplesMenu();
                    break;
                case "0":
                    return;
                default:
                    System.out.println("Invalid option. Try again.");
            }
        }
    }

    // --------- SUBMENU: SAMPLE QUERIES ----------
    private void runSamplesMenu() {
        USEI08SampleQueries samples = new USEI08SampleQueries(service.getIndexes());

        while (true) {
            System.out.println("\n[SAMPLE QUERIES]");
            System.out.println("1. Iberian Cities");
            System.out.println("2. Atlantic Strip");
            System.out.println("3. Major French Stations");
            System.out.println("4. Paris Zone");
            System.out.println("5. Iberian Border");
            System.out.println("6. Run All");
            System.out.println("0. Back");
            System.out.print("\nOption: ");

            String opt = input.nextLine().trim();
            switch (opt) {
                case "1":
                    samples.sample1_IberianUrbanHubs();
                    break;
                case "2":
                    samples.sample2_AtlanticBand();
                    break;
                case "3":
                    samples.sample3_FranceMainStations();
                    break;
                case "4":
                    samples.sample4_ParisDenseArea();
                    break;
                case "5":
                    samples.sample5_IberianBorder();
                    break;
                case "6":
                    samples.runAllSamples();
                    break;
                case "0":
                    return;
                default:
                    System.out.println("Invalid option. Try again.");
            }
        }
    }

    // --------- PESQUISA NORMAL ----------
    private void runSearchFlow() {
        try {
            double minLat, maxLat, minLon, maxLon;

            // Validação de latitudes
            while (true) {
                minLat = readValidatedDouble("Minimum latitude (-90 a 90): ", -90, 90);
                maxLat = readValidatedDouble("Maximum latitude (-90 a 90): ", -90, 90);
                if (maxLat < minLat) {
                    System.out.println("Maximum latitude cannot be less than minimum latitude. Try again.\n");
                } else break;
            }

            // Validação de longitudes
            while (true) {
                minLon = readValidatedDouble("Minimum longitude (-180 a 180): ", -180, 180);
                maxLon = readValidatedDouble("Maximum longitude (-180 a 180): ", -180, 180);
                if (maxLon < minLon) {
                    System.out.println("Maximum longitude cannot be less than minimum longitude. Try again.\n");
                } else break;
            }

            Boolean isCity = readValidatedBoolean("\nCities only (true/false/no filters): ");
            Boolean isMain = readValidatedBoolean("Main stations only (true/false/no filters): ");

            System.out.print("Filter by country (ex: PT, ES, FR) or 'all': ");
            String country = input.nextLine().trim();
            if (country.length() == 0) country = "all";

            long t0 = System.nanoTime();
            List<Station> list = spatialService.queryArea(
                    minLat, maxLat, minLon, maxLon,
                    isCity, isMain, country
            );
            long elapsedNs = System.nanoTime() - t0;
            long durationMs = Math.max(1, (elapsedNs + 999_999) / 1_000_000); // arredonda e evita 0 ms

            // Ordenação por longitude crescente
            Collections.sort(list, Comparator.comparingDouble(Station::getLongitude));

            // Cabeçalho e contagem
            System.out.println();
            System.out.println("Query: USEI08 (Search by Geographical Area)");
            System.out.printf("Results: %d stations%n%n", list.size());

            // Listagem (<=20 mostra todas; >20 mostra primeiras 10 e últimas 10)
            if (list.isEmpty()) {
                System.out.println("No results found.");
            } else if (list.size() <= 20) {
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

            // Tempo no fim
            System.out.printf("%nSearch Time: %d ms%n", durationMs);

        } catch (NumberFormatException e) {
            System.out.println("Invalid numeric input. Try again.");
        }
    }

    // --------- Utilitários de validação ----------
    private Boolean readValidatedBoolean(String prompt) {
        while (true) {
            System.out.print(prompt);
            String s = input.nextLine().trim().toLowerCase();

            if (s.equals("true")) return true;
            if (s.equals("false")) return false;
            if (s.equals("no filters")) return null;

            System.out.println("Invalid option. Type 'true', 'false' or 'no filters'.");
        }
    }

    private double readValidatedDouble(String prompt, double min, double max) {
        while (true) {
            try {
                System.out.print(prompt);
                double value = Double.parseDouble(input.nextLine());
                if (value < min || value > max) {
                    System.out.printf("Value outside the allowed range [%.0f, %.0f]. Try again.%n", min, max);
                } else {
                    return value;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a decimal number.");
            }
        }
    }
}