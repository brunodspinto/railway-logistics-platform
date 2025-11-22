package org.example.ui.usei10;

import org.example.domain.Station;
import org.example.service.StationService;
import org.example.results.RadiusResult;
import org.example.trees.TwoDTree;

import java.util.List;
import java.util.Scanner;

public class USEI10Menu {

    private final StationService service;
    private final Scanner read = new Scanner(System.in);

    public USEI10Menu(StationService service) {
        this.service = service;
    }

    public void start() {
        System.out.println("===== USEI10 - Radius Search and Density Summary =====");

        System.out.println("Latitude: ");
        double lat = Double.parseDouble(read.nextLine());

        System.out.println("Longitude: ");
        double lon = Double.parseDouble(read.nextLine());

        System.out.println("Radius (km): ");
        double radius = Double.parseDouble(read.nextLine());

        RadiusResult result = service.queryRadius(lat, lon, radius);


        System.out.println("\n===== Results =====");
        List<Station> stationsInOrder = result.getTree().inOrder();

        for (Station s : stationsInOrder) {
            double d = TwoDTree.haversineKm(lat, lon, s.getLatitude(), s.getLongitude());

            // station name DESC ordering is already represented in the order of the AVL
            System.out.printf("%.3f km -> %s\n", d, s.getName());
        }

        System.out.println("\n--- Summary by Country ---");
        result.getByCountry().forEach((c, count) -> System.out.printf("%s → %d\n", c, count));

        System.out.println("\n--- Summary by isCity ---");
        result.getByIsCity().forEach((flag, count) -> System.out.printf("%s → %d\n", flag, count));
    }
}
