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

        System.out.printf("\nStations indexed: %d\n", service.getIndexes().getTotalStations());

        while (running) {
            showMainMenu();
            handleMainMenu();
        }
    }

    private void showMainMenu() {
        System.out.println("\n[MENU]");
        System.out.println("1. Show 2D-Tree Build Report");
        System.out.println("0. Exit");
        System.out.print("\nSelect option: ");
    }

    private void handleMainMenu() {
        try {
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    showBuildStats();
                    break;
                case 0:
                    running = false;
                    break;
                default: System.out.println("Invalid option. Please try again.");
            }
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter a number.");
            scanner.nextLine();
        }
    }

    private void showBuildStats() {
        System.out.println(service.getPerformanceReport());
        pause();
    }

    private void pause() {
        System.out.print("\nPress ENTER to continue...");
        scanner.nextLine();
    }
}