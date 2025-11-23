package org.example;

import org.example.service.StationService;
import org.example.ui.usei06.USEI06Menu;
import org.example.ui.usei07.USEI07Menu;
import org.example.ui.usei08.USEI08Menu;
import org.example.ui.usei09.USEI09Menu;
import org.example.ui.usei10.USEI10Menu;

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

        printWelcomeBanner();

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
                    showDevelopmentTeam();
                    break;
                case "2":
                    runUSEI06();
                    break;
                case "3":
                    runUSEI07();
                    break;
                case "4":
                    runUSEI08();
                    break;
                case "5":
                    runUSEI09();
                    break;
                case "6":
                    runUSEI10();
                    break;
                case "0":
                    running = false;
                    printGoodbye();
                    break;
                default:
                    System.out.println("\n✗ Opção inválida! Tenta novamente.");
            }

            if (running && !choice.equals("1")) {
                pauseForUser();
            }
        }

        scanner.close();
    }

    private static void printWelcomeBanner() {
        System.out.println("\n" + "═".repeat(80));
        System.out.println("           ESINF - SPATIAL & INDEXED QUERIES - SPRINT 2");
        System.out.println("                  ISEP - DEI - 3rd Semester 2025/2026");
        System.out.println("═".repeat(80));
    }

    private static void printMainMenu() {
        System.out.println("\n" + "═".repeat(80));
        System.out.println("                            MENU PRINCIPAL");
        System.out.println("═".repeat(80));
        System.out.println("  1. Know the Development Team");
        System.out.println("  2. Run USEI06 - Time-Zone Index & Windowed Queries");
        System.out.println("  3. Run USEI07 - Build Balanced 2D-Tree");
        System.out.println("  4. Run USEI08 - Search by Geographical Area");
        System.out.println("  5. Run USEI09 - Proximity Search (Nearest-N)");
        System.out.println("  6. Run USEI10 - Radius Search & Density Summary");
        System.out.println("  0. Sair");
        System.out.println("═".repeat(80));
        System.out.print("  Escolha uma opção: ");
    }

    private static void showDevelopmentTeam() {
        System.out.println("\n" + "═".repeat(80));
        System.out.println("                           DEVELOPMENT TEAM");
        System.out.println("═".repeat(80));
        System.out.println("\n  Course:        Engenharia Informática");
        System.out.println("  Institution:   Instituto Superior de Engenharia do Porto (ISEP)");
        System.out.println("  Academic Year: 2025/2026");
        System.out.println("  Project:       sem3pi-2025-26 - Integrative Project");
        System.out.println("  Component:     ESINF - Information Structures");

        System.out.println("\n" + "─".repeat(80));
        System.out.println("  TEAM MEMBERS:");
        System.out.println("─".repeat(80));
        System.out.println("  Student 1: David Ribeiro     -  [removed]  -  [removed]");
        System.out.println("  Student 2: Eduardo Oliveira  -  [removed]  -  [removed]");
        System.out.println("  Student 3: Diogo Azevedo     -  [removed]  -  [removed]");
        System.out.println("  Student 4: Bruno Pinto       -  [removed]  -  [removed]");
        System.out.println("  Student 5: Rafael Santos     -  [removed]  -  [removed]");
        System.out.println("═".repeat(80));

        pauseForUser();
    }

    private static void runUSEI06() {
        System.out.println("\n" + "═".repeat(80));
        System.out.println("          USEI06 - Time-Zone Index & Windowed Queries");
        System.out.println("═".repeat(80));

        USEI06Menu menu = new USEI06Menu(service);
        menu.start();
    }

    private static void runUSEI07() {
        System.out.println("\n" + "═".repeat(80));
        System.out.println("          USEI07 - Build Balanced 2D-Tree on Lat/Lon");
        System.out.println("═".repeat(80));

        USEI07Menu menu = new USEI07Menu(service);
        menu.start();
    }

    private static void runUSEI08() {
        System.out.println("\n" + "═".repeat(80));
        System.out.println("          USEI08 - Search by Geographical Area");
        System.out.println("═".repeat(80));

        USEI08Menu menu = new USEI08Menu(service);
        menu.start();
    }

    private static void runUSEI09() {
        System.out.println("\n" + "═".repeat(80));
        System.out.println("          USEI09 - Proximity Search (Nearest-N)");
        System.out.println("═".repeat(80));

        USEI09Menu menu = new USEI09Menu(service);
        menu.start();
    }

    private static void runUSEI10() {
        System.out.println("\n" + "═".repeat(80));
        System.out.println("          USEI10 - Radius Search & Density Summary");
        System.out.println("═".repeat(80));

        USEI10Menu menu = new USEI10Menu(service);
        menu.start();
    }

    private static void printGoodbye() {
        System.out.println("\n" + "═".repeat(80));
        System.out.println("         Thank you for using ESINF Query System!");
        System.out.println("                        Goodbye!");
        System.out.println("═".repeat(80) + "\n");
    }

    private static void pauseForUser() {
        System.out.print("\nPress ENTER to continue...");
        scanner.nextLine();
    }
}