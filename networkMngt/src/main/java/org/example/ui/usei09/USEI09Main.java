package org.example.ui.usei09;

import org.example.service.StationService;

public class USEI09Main {
    private static final String DEFAULT_CSV = "res/train_stations_europe.csv";

    public static void main(String[] args) {
        System.out.println("════════════════════════════════════════════");
        System.out.println("USEI09 - Nearest-N Spatial Search");
        System.out.println("════════════════════════════════════════════");

        String path = args.length > 0 ? args[0] : DEFAULT_CSV;

        StationService service = new StationService();
        if (!service.initialize(path)) {
            System.err.println("Initialization failed");
            System.exit(1);
        }

        USEI09Menu menu = new USEI09Menu(service);
        menu.start();
    }
}