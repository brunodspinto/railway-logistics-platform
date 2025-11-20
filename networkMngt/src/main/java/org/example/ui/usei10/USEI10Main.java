package org.example.ui.usei10;

import org.example.service.StationService;

public class USEI10Main {
    private static final String DEFAULT_CSV = "res/train_stations_europe.csv";

    public static void main(String[] args) {
        System.out.println("============================================");
        System.out.println("USEI10 - Radius Search and Density Summary");
        System.out.println("============================================");

        String path = args.length > 0 ? args[0] : DEFAULT_CSV;

        StationService service = new StationService();
        if (!service.initialize(path)) {
            System.err.println("Initialization failed");
            System.exit(1);
        }

        USEI10Menu menu = new USEI10Menu(service);
        menu.start();
    }
}
