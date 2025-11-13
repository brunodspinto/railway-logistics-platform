package org.example.ui.usei07;

import org.example.service.StationService;

import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * UI (Menu) para as funcionalidades que usam a 2D-Tree.
 */
public class USEI07Menu {
    private final StationService service;
    private final Scanner scanner;
    private boolean running;

    public USEI07Menu(StationService service) {
        this.service = service;
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        if (!service.isReady()) {
            System.err.println("Service not ready");
            return;
        }

        running = true;
        // Mostra o total de estações carregadas
        System.out.printf("\nStations indexed: %d\n", service.getIndexes().getTotalStations());

        while (running) {
            showMainMenu();
            handleMainMenu();
        }
    }

    private void showMainMenu() {
        System.out.println("\n[SPATIAL QUERIES (2D-Tree) MENU]");
        System.out.println("1. Show 2D-Tree Build Stats (USEI07)");
        System.out.println("0. Exit");
        System.out.print("\nOption: ");
    }

    private void handleMainMenu() {
        try {
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1: showBuildStats(); break;
                case 0: running = false; break;
                default: System.out.println("Invalid option");
            }
        } catch (InputMismatchException e) {
            System.out.println("Invalid input");
            scanner.nextLine();
        }
    }

    private void showBuildStats() {
        System.out.println("\n[USEI07: Index Build Report]");
        System.out.println(service.getPerformanceReport());
        pause();
    }

    private void pause() {
        System.out.print("\nPress ENTER...");
        scanner.nextLine();
    }
}