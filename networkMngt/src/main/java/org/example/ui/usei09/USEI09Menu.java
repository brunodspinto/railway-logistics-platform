package org.example.ui.usei09;

import org.example.queries.NearestNQuery;
import org.example.service.StationService;

import java.util.List;
import java.util.Scanner;

public class USEI09Menu {

    private final StationService service;
    private final Scanner scanner;

    public USEI09Menu(StationService service) {
        this.service = service;
        this.scanner = new Scanner(System.in);
    }

    public void run() {
        System.out.println("\n===== USEI09 – Nearest-N Search =====\n");

        try {
            System.out.println("Enter latitude:");
            double latitude = Double.parseDouble(scanner.nextLine());

            System.out.println("Enter longitude:");
            double longitude = Double.parseDouble(scanner.nextLine());

            System.out.println("Enter number of nearest stations to find (N):");
            int n = Integer.parseInt(scanner.nextLine());

            System.out.println("Enter Time Zone Group (optional, press Enter to skip):");
            String tzGroup = scanner.nextLine().trim();
            if (tzGroup.isEmpty()) {
                tzGroup = null;
            }

            System.out.println("Enter Country (optional, press Enter to skip):");
            String country = scanner.nextLine().trim();
            if (country.isEmpty()) {
                country = null;
            }

            System.out.println("\nSearching for nearest stations...\n");

            List<NearestNQuery.StationDistance> results = service.queryNearestN(latitude, longitude, n, tzGroup, country);

            if (results.isEmpty()) {
                System.out.println("No stations found matching the given filters.");
                return;
            }

            System.out.println("===== Nearest Stations =====");
            int i = 1;

            for (NearestNQuery.StationDistance sd : results) {
                System.out.printf("%d. %s - Distance: %.2f km%n", i++, sd.station, sd.distanceKm);
            }
        } catch (Exception e) {
            System.out.println("Error: invalid input." + e.getMessage());
        }
    }
}