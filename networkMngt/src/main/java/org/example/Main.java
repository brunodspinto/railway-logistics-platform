package org.example;

import org.example.service.StationService;
import org.example.ui.usei06.USEI06Menu;
import org.example.ui.usei07.USEI07Menu;
import org.example.ui.usei08.USEI08Menu;

import java.util.Scanner;

/**
 * Aplicação principal que permite executar diferentes User Stories.
 */
public class Main {
    private static final String DEFAULT_CSV = "res/train_stations_europe.csv";
    private static final Scanner scanner = new Scanner(System.in);
    private static StationService service = null;

    public static void main(String[] args) {
        String csvPath = args.length > 0 ? args[0] : DEFAULT_CSV;

        // Carregar dados uma única vez
        System.out.println("Loading data...");
        service = new StationService();
        if (!service.initialize(csvPath)) {
            System.err.println("Failed to initialize data");
            System.exit(1);
        }

        boolean running = true;
        while (running) {
            printMainMenu();

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    runUSEI06();
                    break;
                case "2":
                    runUSEI07();
                    break;
                case "3":
                    runUSEI08();
                    break;
                case "0":
                    running = false;
                    System.out.println("\nA encerrar aplicação...");
                    break;
                default:
                    System.out.println("\nOpção inválida! Tenta novamente.");
            }
        }

        scanner.close();
    }

    private static void printMainMenu() {
        System.out.println("\n════════════════════════════════════════════");
        System.out.println("               MENU PRINCIPAL");
        System.out.println("════════════════════════════════════════════");
        System.out.println("1 - Run USEI06");
        System.out.println("2 - Run USEI07");
        System.out.println("3 - Run USEI08");
        System.out.println("0 - Sair");
        System.out.println("════════════════════════════════════════════");
        System.out.print("Escolha uma opção: ");
    }

    private static void runUSEI06() {
        System.out.println("\n════════════════════════════════════════════");
        System.out.println("USEI06 - Time-Zone Index & Windowed Queries");
        System.out.println("════════════════════════════════════════════");

        USEI06Menu menu = new USEI06Menu(service);
        menu.start();
    }

    private static void runUSEI07() {
        System.out.println("\n════════════════════════════════════════════");
        System.out.println("USEI07 - Spatial Queries (2D-Tree)");
        System.out.println("════════════════════════════════════════════");

        USEI07Menu menu = new USEI07Menu(service);
        menu.start();
    }

    private static void runUSEI08() {
        System.out.println("\n=======================================");
        System.out.println(" USEI08 - Search by Geographical Area");
        System.out.println("=======================================\n");

        USEI08Menu menu = new USEI08Menu(service);
        menu.run();
    }
}