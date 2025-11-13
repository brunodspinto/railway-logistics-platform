package org.example.ui.usei08;

import org.example.service.StationService;

public class USEI08Main {

    private static final String DEFAULT_CSV = "res/train_stations_europe.csv";

    public static void main(String[] args) {

        String path = args.length > 0 ? args[0] : DEFAULT_CSV;

        StationService service = new StationService();

        if (!service.initialize(path)) {
            System.err.println("Initialization failed");
            System.exit(1);
        }

        System.out.println("=======================================");
        System.out.println(" USEI08 - Search by Geographical Area");
        System.out.println("=======================================\n");

        USEI08Menu menu = new USEI08Menu(service);
        menu.run();
    }
}