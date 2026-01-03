package org.example;

import org.example.repository.DatabaseRepositoryFacade;
import org.example.repository.IRouteRepository;
import org.example.ui.*;
import org.example.utils.DatabaseConnection;

import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * Main Geral - Railway Logistics Management System
 * Integrates Sprint 1 (USLP03), Sprint 2 (USLP07) and Sprint 3 (USLP08, USLP09)
 * Database Version
 */
public class MainGeral {

    private final IRouteRepository repository;
    private final DatabaseRepositoryFacade databaseRepository;
    private final Scanner scanner;

    // UIs
    private TravelTimeUI travelTimeUI;
    private SchedulerUI schedulerUI;
    private ManualSchedulerUI manualSchedulerUI;
    private RoutePlannerUI routePlannerUI;
    private TrainAssemblyUI trainAssemblyUI; // <--- NOVA UI USLP09
    private TrafficSchedulerUI trafficSchedulerUI;

    public MainGeral(IRouteRepository repository, DatabaseRepositoryFacade databaseRepository) {
        this.repository = repository;
        this.databaseRepository = databaseRepository;
        this.scanner = new Scanner(System.in);

        // Inicialização das UIs
        this.travelTimeUI = new TravelTimeUI(repository);
        this.schedulerUI = new SchedulerUI(repository);
        this.manualSchedulerUI = new ManualSchedulerUI(databaseRepository);
        this.routePlannerUI = new RoutePlannerUI(repository);
        this.trainAssemblyUI = new TrainAssemblyUI(repository); // <--- INICIALIZAR USLP09
        this.trafficSchedulerUI = new TrafficSchedulerUI(repository);
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
                case 4:
                    runUSLP08();
                    break;
                case 5:
                    runUSLP09();
                    break;
                case 6:
                    runUSLP10();
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
        System.out.println("  4. Run USLP08 - Route Planner Manifest (Sprint 3)");
        System.out.println("  5. Run USLP09 - Train Assembly (Sprint 3)");
        System.out.println("  6. Run USLP10 - Traffic Scheduler (Sprint 3)");
        System.out.println("  0. Exit");
        System.out.println("═".repeat(80));
        System.out.print("  Option: ");
    }

    private void showDevelopmentTeam() {
        System.out.println("\n" + "═".repeat(80));
        System.out.println("                           DEVELOPMENT TEAM");
        System.out.println("═".repeat(80));
        System.out.println("  Sprint 1 (USLP03): Travel Time Calculator");
        System.out.println("  Sprint 2 (USLP07): Train Scheduler - [Diogo]");
        System.out.println("  Sprint 3 (USLP08): Route Planner - [Bruno]");
        System.out.println("  Sprint 3 (USLP09): Train Assembly - [Diogo]");
        System.out.println("  Sprint 3 (USLP10): Traffic Scheduler - [David]");
        System.out.println("═".repeat(80));
    }

    private void runUSLP03() {
        System.out.println("\n" + "═".repeat(80));
        System.out.println("              LAUNCHING USLP03 - TRAVEL TIME CALCULATOR");
        System.out.println("═".repeat(80));
        System.out.println("  Sprint: 1");
        System.out.println("  User Story: USLP03");
        System.out.println("  Goal: Calculate travel time between stations");
        System.out.println("─".repeat(80) + "\n");

        try {
            travelTimeUI.run();
        } catch (Exception e) {
            System.err.println("\n✗ Error running USLP03: " + e.getMessage());
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
        }
    }

    private void runUSLP08() {
        System.out.println("\n" + "═".repeat(80));
        System.out.println("                 LAUNCHING USLP08 - ROUTE PLANNER");
        System.out.println("═".repeat(80));
        System.out.println("  Sprint: 3");
        System.out.println("  User Story: USLP08");
        System.out.println("  Goal: Generate Logistics Manifest (Load/Unload operations)");
        System.out.println("─".repeat(80) + "\n");

        try {
            routePlannerUI.run();
        } catch (Exception e) {
            System.err.println("\n✗ Error running USLP08: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void runUSLP09() {
        System.out.println("\n" + "═".repeat(80));
        System.out.println("                 LAUNCHING USLP09 - TRAIN ASSEMBLY");
        System.out.println("═".repeat(80));
        System.out.println("  Sprint: 3");
        System.out.println("  User Story: USLP09");
        System.out.println("  Goal: Assemble and assign rolling stock to trains");
        System.out.println("─".repeat(80) + "\n");

        try {
            trainAssemblyUI.run();
        } catch (Exception e) {
            System.err.println("\n✗ Error running USLP09: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void runUSLP10() {
        System.out.println("\n" + "═".repeat(80));
        System.out.println("              LAUNCHING USLP10 - TRAFFIC SCHEDULER (AUTO)");
        System.out.println("═".repeat(80));
        System.out.println("  Sprint: 3");
        System.out.println("  User Story: USLP10");
        System.out.println("  Goal: Automatic Dispatch & Conflict Resolution for Trains");
        System.out.println("─".repeat(80) + "\n");

        try {
            trafficSchedulerUI.run();
        } catch (Exception e) {
            System.err.println("\n✗ Error running USLP10: " + e.getMessage());
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

        if (!DatabaseConnection.testConnection()) {
            System.err.println("\n✗ FATAL ERROR: Cannot connect to database!");
            System.exit(1);
        }

        System.out.println("✓ Database connection successful!");

        try {
            System.out.println("Loading system data from database...");
            DatabaseRepositoryFacade databaseRepository = new DatabaseRepositoryFacade();
            IRouteRepository routeRepository = databaseRepository;

            System.out.println("✓ System ready!\n");

            MainGeral mainUI = new MainGeral(routeRepository, databaseRepository);
            mainUI.run();

        } catch (Exception e) {
            System.err.println("\n✗ FATAL ERROR: Could not initialize system!");
            e.printStackTrace();
            System.exit(1);
        } finally {
            DatabaseConnection.closeConnection();
        }
    }
}