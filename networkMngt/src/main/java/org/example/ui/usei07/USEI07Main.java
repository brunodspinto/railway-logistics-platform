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

        // Show 2D-Tree build progress (USEI07 only)
        System.out.println("\nBuilding 2D-Tree index (using AVL pre-sort)...");
        long start2D = System.currentTimeMillis();

        if (!service.initialize(path)) {
            System.err.println("Initialization failed");
            System.exit(1);
        }

        long time2D = System.currentTimeMillis() - start2D;
        System.out.println("2D-Tree build complete in " + time2D + " ms.\n");

        USEI07Menu menu = new USEI07Menu(service);
        menu.start();
    }
}