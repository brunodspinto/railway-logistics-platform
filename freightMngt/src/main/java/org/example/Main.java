package org.example;

import org.example.repository.CsvRouteRepository;
import org.example.repository.IRouteRepository;
import org.example.ui.SchedulerUI;
import org.example.ui.TravelTimeUI;

import java.io.IOException;
import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * Main Menu - Railway Logistics Management System
 * Integrates Sprint 1 (USLP03) and Sprint 2 (USLP07)
 */
public class Main {

    private final IRouteRepository repository;
    private final Scanner scanner;
    private TravelTimeUI travelTimeUI;
    private SchedulerUI schedulerUI;

    public Main(IRouteRepository repository) {
        this.repository = repository;
        this.scanner = new Scanner(System.in);
        this.travelTimeUI = new TravelTimeUI(repository);
        this.schedulerUI = new SchedulerUI(repository);
    }

    public void run() {
        printWelcomeBanner();

        boolean exit = false;

        while (!exit) {
            printMainMenu();
            int option = readInt();

            switch (option) {
                case 1:
                    showDevelopmentTeam();
                    break;
                case 2:
                    runUSLP03();
                    break;
                case 3:
                    runUSLP07();
                    break;
                case 0:
                    exit = true;
                    printGoodbye();
                    break;
                default:
                    System.out.println("\nInvalid option! Please try again.");
            }

            if (!exit) {
                System.out.println("\nPress ENTER to return to main menu...");
                scanner.nextLine();
            }
        }
    }

    private void printWelcomeBanner() {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("         RAILWAY LOGISTICS MANAGEMENT SYSTEM - LOGISTICS ON RAILS");
        System.out.println("                    ISEP - DEI - 3rd Semester 2025/2026");
        System.out.println("=".repeat(80));
    }

    private void printMainMenu() {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("                              MAIN MENU");
        System.out.println("=".repeat(80));
        System.out.println("  1. Know the Development Team");
        System.out.println("  2. Run USLP03 - Travel Time Calculator (Sprint 1)");
        System.out.println("  3. Run USLP07 - Train Scheduler (Sprint 2)");
        System.out.println("  0. Exit");
        System.out.println("=".repeat(80));
        System.out.print("  Option: ");
    }

    private void showDevelopmentTeam() {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("                           DEVELOPMENT TEAM");
        System.out.println("=".repeat(80));
        System.out.println("\n  Course:      Engenharia Informatica");
        System.out.println("  Institution: Instituto Superior de Engenharia do Porto (ISEP)");
        System.out.println("  Academic Year: 2025/2026");
        System.out.println("  Project:     sem3pi-2025-26 - Integrative Project");

        System.out.println("\n" + "-".repeat(80));
        System.out.println("  TEAM MEMBERS:");
        System.out.println("-".repeat(80));
        System.out.println("  Student 1: David Ribeiro  -  [removed]  -  [removed]");
        System.out.println("  Student 2: Eduardo Oliveira  -  [removed]  -  [removed]");
        System.out.println("  Student 3: Diogo Azevedo  -  [removed]  -  [removed]");
        System.out.println("  Student 4: Bruno Pinto  -  [removed]  -  [removed]");
        System.out.println("  Student 5: Rafael Santos  -  [removed]   -  [removed]");

    }

    private void runUSLP03() {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("              LAUNCHING USLP03 - TRAVEL TIME CALCULATOR");
        System.out.println("=".repeat(80));
        System.out.println("  Sprint: 1");
        System.out.println("  User Story: USLP03");
        System.out.println("  Goal: Calculate estimated travel time between two stations");
        System.out.println("-".repeat(80) + "\n");

        try {
            travelTimeUI.run();
        } catch (Exception e) {
            System.err.println("\nError running USLP03: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void runUSLP07() {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("                 LAUNCHING USLP07 - TRAIN SCHEDULER");
        System.out.println("=".repeat(80));
        System.out.println("  Sprint: 2");
        System.out.println("  User Story: USLP07");
        System.out.println("  Goal: Schedule multiple trains with conflict detection");
        System.out.println("-".repeat(80) + "\n");

        try {
            schedulerUI.run();
        } catch (Exception e) {
            System.err.println("\nError running USLP07: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void printGoodbye() {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("       Thank you for using Railway Logistics Management System!");
        System.out.println("                           Goodbye!");
        System.out.println("=".repeat(80) + "\n");
    }

    private int readInt() {
        while (true) {
            try {
                int value = scanner.nextInt();
                scanner.nextLine();
                return value;
            } catch (InputMismatchException e) {
                System.out.print("Invalid input! Please enter a number: ");
                scanner.nextLine();
            }
        }
    }

    public static void main(String[] args) {
        try {
            System.out.println("Loading system data...\n");

            IRouteRepository repository = new CsvRouteRepository("freightMngt/data");

            System.out.println("\nSystem ready!");

            Main mainUI = new Main(repository);
            mainUI.run();

        } catch (IOException e) {
            System.err.println("\nFATAL ERROR: Could not load system data!");
            System.err.println("Error: " + e.getMessage());
            System.err.println("\nPlease ensure the 'data' folder exists and contains all required CSV files:");
            System.err.println("  - facilities.csv, lines.csv, segments.csv, locomotives.csv");
            System.err.println("  - wagon_models.csv, wagons.csv, freights.csv, trains.csv");
            e.printStackTrace();
            System.exit(1);
        }
    }
}
