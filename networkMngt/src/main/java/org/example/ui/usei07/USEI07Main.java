package org.example.ui.usei07;

import org.example.service.StationService;

/**
 * Ponto de entrada principal para a querie da USEI07.
 * Carrega os dados e inicia o menu de queries espaciais (2D-Tree).
 */
public class USEI07Main {
    private static final String DEFAULT_CSV = "res/train_stations_europe.csv";

    public static void main(String[] args) {
        System.out.println("════════════════════════════════════════════");
        System.out.println("USEI07 - Spatial Queries (2D-Tree)");
        System.out.println("════════════════════════════════════════════");

        String path = args.length > 0 ? args[0] : DEFAULT_CSV;

        StationService service = new StationService();

        if (!service.initialize(path)) {
            System.err.println("Initialization failed");
            System.exit(1);
        }

        USEI07Menu menu = new USEI07Menu(service);
        menu.start();
    }
}