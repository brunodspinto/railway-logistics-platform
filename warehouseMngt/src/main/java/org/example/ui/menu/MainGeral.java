package org.example.ui.menu;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MainGeral implements Runnable {

    private final List<MenuItem> menuItems = new ArrayList<>();
    private final Scanner scanner = new Scanner(System.in);

    @Override
    public void run() {
        buildMenu();

        boolean exit = false;
        while (!exit) {
            printMenu();
            int choice = readChoice();

            if (choice == 0) {
                System.out.println("\nReturning to Main Menu...\n");
                exit = true;
            } else if (choice > 0 && choice <= menuItems.size()) {
                menuItems.get(choice - 1).run();
            } else {
                System.out.println("Invalid option. Try again.\n");
            }
        }
    }

    private void buildMenu() {
        menuItems.add(new MenuItem("USEI01 - Wagons Unloading", new USEI01UI()));
        menuItems.add(new MenuItem("USEI02 - Order Eligibility & Allocation", new USEI02UI()));
        menuItems.add(new MenuItem("USEI03 - Picking Plans", new USEI03UI()));
        menuItems.add(new MenuItem("USEI04 - Pick Path Sequencing", new USEI04UI()));
        menuItems.add(new MenuItem("USEI05 - Returns & Quarantine", new USEI05UI()));
    }

    private void printMenu() {
        System.out.println("\n--- RUN PROGRAM (USEI01–USEI05) ---");
        for (int i = 0; i < menuItems.size(); i++) {
            System.out.printf("%d. %s%n", i + 1, menuItems.get(i).getLabel());
        }
        System.out.println("0. Back");
    }

    private int readChoice() {
        System.out.print("\nSelect an option: ");
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
