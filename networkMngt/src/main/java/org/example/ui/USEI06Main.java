package org.example.ui;

import org.example.service.StationService;
import org.example.ui.USEI06Menu;

public class USEI06Main {
    private static final String DEFAULT_CSV = "res/train_stations_europe.csv";

    public static void main(String[] args) {
        System.out.println("════════════════════════════════════════════");
        System.out.println("USEI06 - Time-Zone Index & Windowed Queries");
        System.out.println("════════════════════════════════════════════");

        String path = args.length > 0 ? args[0] : DEFAULT_CSV;

        StationService service = new StationService();
        if (!service.initialize(path)) {
            System.err.println("Initialization failed");
            System.exit(1);
        }

        USEI06Menu menu = new USEI06Menu(service);
        menu.start();
    }
}