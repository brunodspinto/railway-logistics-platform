package org.example.ui.menu;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MainMenuUI {

    private final List<MenuItem> menuItems = new ArrayList<>();
    private final Scanner scanner = new Scanner(System.in);

    public void run() {
        printHeader();
        buildMenu();

        boolean exit = false;
        while (!exit) {
            printMenu();
            int choice = readChoice();

            if (choice == 0) {
                System.out.println("\nExiting system... See you soon!");
                exit = true;
            } else if (choice > 0 && choice <= menuItems.size()) {
                menuItems.get(choice - 1).run();
            } else {
                System.out.println("Invalid option. Try again.\n");
            }
        }
    }

    private void printHeader() {
        System.out.println("""
                 _-====-__-======-__-========-_____-============-__
               _(                                                 _)
            OO(             Logistics on Rails 🚂                 )_
           0  (_                                               _)
         o0     (_                                           _)
        o         '=-___-===-_____-========-___________-===-dwb-='
      .o                                _________
     . ______          ______________  |         |      _____
   _()_||__|| ________ |            |  |_________|   __||___||__
  (G054 ISEP| |      | |            | __Y______00_| |_         _|
 /-OO----OO\"\"=\"OO--OO\"=\"OO--------OO\"=\"OO-------OO\"=\"OO-------OO\"=P
        """);
        System.out.println("Welcome to the Logistics on Rails System (Sprint 1)\n");
    }

    private void buildMenu() {
        menuItems.add(new MenuItem("Know the Development Team", new DevTeamUI()));
        menuItems.add(new MenuItem("Run Program (USEI01–USEI05)", new MainGeral()));
        menuItems.add(new MenuItem("About the Project", () -> {
            System.out.println("""
                Project: Logistics on Rails (Integrative Project 2025/26)
                Sprint 1 – Cargo Handling at a Railway Terminal
                Involves modules: ESINF, BDDAD, ARQCP, LAPR3, FSIAP.
            """);
        }));
    }

    private void printMenu() {
        System.out.println("\n--- MAIN MENU ---");
        for (int i = 0; i < menuItems.size(); i++) {
            System.out.printf("%d. %s%n", i + 1, menuItems.get(i).getLabel());
        }
        System.out.println("0. Exit");
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

