package org.example;

import org.example.repository.DatabaseRepositoryFacade;
import org.example.repository.IRouteRepository;
import org.example.ui.SchedulerUI;
import org.example.ui.TravelTimeUI;
import org.example.ui.ManualSchedulerUI;
import org.example.utils.DatabaseConnection;

import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * Main Geral - Railway Logistics Management System
 * Integrates Sprint 1 (USLP03) and Sprint 2 (USLP07)
 * Database Version
 */
public class MainGeral {

    private final IRouteRepository repository;
    private final DatabaseRepositoryFacade databaseRepository;
    private final Scanner scanner;
    private TravelTimeUI travelTimeUI;
    private SchedulerUI schedulerUI;
    private ManualSchedulerUI manualSchedulerUI;

    public MainGeral(IRouteRepository repository, DatabaseRepositoryFacade databaseRepository) {
        this.repository = repository;
        this.databaseRepository = databaseRepository;
        this.scanner = new Scanner(System.in);
        this.travelTimeUI = new TravelTimeUI(repository);
        this.schedulerUI = new SchedulerUI(repository);
        this.manualSchedulerUI = new ManualSchedulerUI(databaseRepository);
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
                    System.out.println("\n✗ Invalid option! Please try again.");
            }

            if (!exit) {
                System.out.println("\nPress ENTER to return to main menu...");
                scanner.nextLine();
            }
        }
    }

    private void printWelcomeBanner() {
        System.out.println("\n" + "═".repeat(80));
        System.out.println("         RAILWAY LOGISTICS MANAGEMENT SYSTEM - LOGISTICS ON RAILS");
        System.out.println("                    ISEP - DEI - 3rd Semester 2025/2026");
        System.out.println("═".repeat(80));
    }

    private void printMainMenu() {
        System.out.println("\n" + "═".repeat(80));
        System.out.println("                              MAIN MENU");
        System.out.println("═".repeat(80));
        System.out.println("  1. Know the Development Team");
        System.out.println("  2. Run USLP03 - Travel Time Calculator (Sprint 1)");
        System.out.println("  3. Run USLP07 - Train Scheduler (Sprint 2)");
        System.out.println("  0. Exit");
        System.out.println("═".repeat(80));
        System.out.print("  Option: ");
    }

    private void showDevelopmentTeam() {
        System.out.println("\n" + "═".repeat(80));
        System.out.println("                           DEVELOPMENT TEAM");
        System.out.println("═".repeat(80));
        System.out.println("\n  Course:      Engenharia Informatica");
        System.out.println("  Institution: Instituto Superior de Engenharia do Porto (ISEP)");
        System.out.println("  Academic Year: 2025/2026");
        System.out.println("  Project:     sem3pi-2025-26 - Integrative Project");

        System.out.println("\n" + "─".repeat(80));
        System.out.println("  TEAM MEMBERS:");
        System.out.println("─".repeat(80));
        System.out.println("  Student 1: David Ribeiro     -  [removed]  -  [removed]");
        System.out.println("  Student 2: Eduardo Oliveira  -  [removed]  -  [removed]");
        System.out.println("  Student 3: Diogo Azevedo     -  [removed]  -  [removed]");
        System.out.println("  Student 4: Bruno Pinto       -  [removed]  -  [removed]");
        System.out.println("  Student 5: Rafael Santos     -  [removed]  -  [removed]");
    }

    private void runUSLP03() {
        System.out.println("\n" + "═".repeat(80));
        System.out.println("              LAUNCHING USLP03 - TRAVEL TIME CALCULATOR");
        System.out.println("═".repeat(80));
        System.out.println("  Sprint: 1");
        System.out.println("  User Story: USLP03");
        System.out.println("  Goal: Calculate estimated travel time between two stations");
        System.out.println("─".repeat(80) + "\n");

        try {
            travelTimeUI.run();
        } catch (Exception e) {
            System.err.println("\n✗ Error running USLP03: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void runUSLP07() {
        System.out.println("\n" + "═".repeat(80));
        System.out.println("                 LAUNCHING USLP07 - TRAIN SCHEDULER");
        System.out.println("═".repeat(80));
        System.out.println("  Sprint: 2");
        System.out.println("  User Story: USLP07");
        System.out.println("  Goal: Schedule multiple trains with conflict detection");
        System.out.println("─".repeat(80) + "\n");

        try {
            manualSchedulerUI.start();
        } catch (Exception e) {
            System.err.println("\n✗ Error running USLP07: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void printGoodbye() {
        System.out.println("\n" + "═".repeat(80));
        System.out.println("       Thank you for using Railway Logistics Management System!");
        System.out.println("                           Goodbye!");
        System.out.println("═".repeat(80) + "\n");
    }

    private int readInt() {
        while (true) {
            try {
                int value = scanner.nextInt();
                scanner.nextLine();
                return value;
            } catch (InputMismatchException e) {
                System.out.print("✗ Invalid input! Please enter a number: ");
                scanner.nextLine();
            }
        }
    }

    public static void main(String[] args) {
        System.out.println("\n" + "═".repeat(80));
        System.out.println("  MAIN GERAL - Database Version");
        System.out.println("═".repeat(80));
        System.out.println("\nInitializing system...");

        // Test database connection
        if (!DatabaseConnection.testConnection()) {
            System.err.println("\n✗ FATAL ERROR: Cannot connect to database!");
            System.err.println("Please check database.properties configuration");
            System.err.println("\nRequired properties:");
            System.err.println("  - db.url");
            System.err.println("  - db.username");
            System.err.println("  - db.password");
            System.exit(1);
        }

        System.out.println("✓ Database connection successful!");

        try {
            System.out.println("Loading system data from database...");

            // Create repository facade for database access
            DatabaseRepositoryFacade databaseRepository = new DatabaseRepositoryFacade();

            // For USLP03, if it still uses IRouteRepository interface
            // you might need an adapter or keep using CSV for that specific feature
            // Here I'm assuming you have a way to use the database repository
            IRouteRepository routeRepository = databaseRepository; // or create an adapter

            System.out.println("✓ System ready!\n");

            MainGeral mainUI = new MainGeral(routeRepository, databaseRepository);
            mainUI.run();

        } catch (Exception e) {
            System.err.println("\n✗ FATAL ERROR: Could not initialize system!");
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        } finally {
            DatabaseConnection.closeConnection();
            System.out.println("\n✓ Database connection closed.");
        }
    }
}