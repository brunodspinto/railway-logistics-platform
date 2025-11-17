package org.example.ui;

import org.example.domain.*;
import org.example.repository.*;
import org.example.service.*;

import java.io.FileWriter;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Interface console para o Scheduler (USLP07)
 */
public class SchedulerUI {

    private final IRouteRepository repository;
    private final SchedulerService schedulerService;
    private final Scanner scanner;

    public SchedulerUI(IRouteRepository repository) {
        this.repository = repository;
        this.schedulerService = new SchedulerService(repository);
        this.scanner = new Scanner(System.in);
    }

    public void run() {
        boolean exit = false;

        while (!exit) {
            printMenu();
            int option = readInt();

            switch (option) {
                case 1:
                    calculateSingleTrainSchedule();
                    break;
                case 2:
                    calculateAllTrainSchedules();
                    break;
                case 3:
                    listAvailableTrains();
                    break;
                case 4:
                    exportSchedulesToFile();
                    break;
                case 0:
                    exit = true;
                    System.out.println("\n👋 Exiting Scheduler. Goodbye!");
                    break;
                default:
                    System.out.println("\n❌ Invalid option! Please try again.");
            }

            if (!exit) {
                System.out.println("\n[Press ENTER to continue]");
                scanner.nextLine();
            }
        }
    }

    private void printMenu() {
        System.out.println("\n" + "═".repeat(80));
        System.out.println("                    TRAIN DISPATCH SCHEDULER - USLP07");
        System.out.println("═".repeat(80));
        System.out.println("  1. Calculate schedule for a specific train");
        System.out.println("  2. Calculate schedules for all trains");
        System.out.println("  3. List available trains");
        System.out.println("  4. Export schedules to file");
        System.out.println("  0. Exit");
        System.out.println("═".repeat(80));
        System.out.print("  Option: ");
    }

    private void calculateSingleTrainSchedule() {
        System.out.println("\n" + "─".repeat(80));
        System.out.println("CALCULATE SCHEDULE FOR SPECIFIC TRAIN");
        System.out.println("─".repeat(80));

        System.out.print("Enter Train ID: ");
        int trainId = readInt();

        Train train = repository.getTrain(trainId);
        if (train == null) {
            System.out.println("\n❌ Train not found with ID: " + trainId);
            return;
        }

        try {
            System.out.println("\n⏳ Calculating schedule...\n");

            TrainSchedule schedule = schedulerService.calculateSchedule(train);

            System.out.println(schedule.format());

        } catch (Exception e) {
            System.out.println("\n❌ Error calculating schedule: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void calculateAllTrainSchedules() {
        Collection<Train> trains = repository.getAllTrains();

        if (trains.isEmpty()) {
            System.out.println("\n❌ No trains found in the system.");
            return;
        }

        System.out.println("\n" + "-".repeat(80));
        System.out.println("CALCULATE SCHEDULES WITH CONFLICT DETECTION");
        System.out.println("-".repeat(80));
        System.out.println("Found " + trains.size() + " trains");
        System.out.println("\n⏳ Calculating schedules...");

        ScheduleResult result = schedulerService.calculateSchedulesWithConflicts(trains);

        List<TrainSchedule> schedules = result.getSchedules();

        System.out.println("\n✅ Successfully calculated " + schedules.size() + " schedules\n");

        for (TrainSchedule schedule : schedules) {
            System.out.println(schedule.format());
            System.out.println();
        }

        // Mostrar crossings
        if (result.hasCrossings()) {
            System.out.println(result.formatCrossings());
        } else {
            System.out.println("=".repeat(100));  // ⬅️ FIX AQUI
            System.out.println("✅ NO CROSSINGS REQUIRED");
            System.out.println("All trains can proceed without conflicts!");
            System.out.println("=".repeat(100));  // ⬅️ FIX AQUI
        }

        // Summary
        System.out.println("\n" + "=".repeat(80));  // ⬅️ FIX AQUI
        System.out.println("SUMMARY");
        System.out.println("=".repeat(80));  // ⬅️ FIX AQUI

        double totalDistance = schedules.stream()
                .mapToDouble(TrainSchedule::getTotalDistanceKm)
                .sum();

        long totalMinutes = schedules.stream()
                .mapToLong(TrainSchedule::getTotalTravelMinutes)
                .sum();

        System.out.printf("Total trains:    %d\n", schedules.size());
        System.out.printf("Total crossings: %d\n", result.getCrossings().size());
        System.out.printf("Total distance:  %.1f km\n", totalDistance);
        System.out.printf("Total time:      %dh %02dm\n",
                totalMinutes / 60, totalMinutes % 60);
        System.out.println("=".repeat(80));  // ⬅️ FIX AQUI
    }

    private void listAvailableTrains() {
        Collection<Train> trains = repository.getAllTrains();

        if (trains.isEmpty()) {
            System.out.println("\n❌ No trains found in the system.");
            return;
        }

        System.out.println("\n" + "═".repeat(80));
        System.out.println("AVAILABLE TRAINS");
        System.out.println("═".repeat(80));

        System.out.printf("%-8s %-12s %-18s %-20s %-10s %-10s\n",
                "ID", "Operator", "Departure", "Route", "Locos", "Freights");
        System.out.println("─".repeat(80));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        for (Train train : trains) {
            String route = String.format("%s → %s",
                    train.getStartStation() != null ? train.getStartStation().getName() : "?",
                    train.getEndStation() != null ? train.getEndStation().getName() : "?");

            System.out.printf("%-8d %-12s %-18s %-20s %-10d %-10d\n",
                    train.getId(),
                    train.getOperator(),
                    train.getDepartureDateTime().format(formatter),
                    truncate(route, 20),
                    train.getLocomotives().size(),
                    train.getFreights().size());
        }

        System.out.println("═".repeat(80));
        System.out.printf("Total: %d trains\n", trains.size());
    }

    private void exportSchedulesToFile() {
        System.out.println("\n" + "─".repeat(80));
        System.out.println("EXPORT SCHEDULES TO FILE");
        System.out.println("─".repeat(80));

        System.out.print("Enter filename (without extension): ");
        scanner.nextLine(); // clear buffer
        String filename = scanner.nextLine().trim();

        if (filename.isEmpty()) {
            System.out.println("\n❌ Filename cannot be empty!");
            return;
        }

        filename = filename + ".txt";

        try {
            Collection<Train> trains = repository.getAllTrains();
            List<TrainSchedule> schedules = schedulerService.calculateSchedules(trains);

            try (FileWriter writer = new FileWriter(filename)) {
                writer.write("=" .repeat(100) + "\n");
                writer.write("TRAIN DISPATCH SCHEDULES - USLP07\n");
                writer.write("Generated: " + java.time.LocalDateTime.now()
                        .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")) + "\n");
                writer.write("=" .repeat(100) + "\n\n");

                for (TrainSchedule schedule : schedules) {
                    writer.write(schedule.format());
                    writer.write("\n\n");
                }

                writer.write("=" .repeat(100) + "\n");
                writer.write("END OF REPORT\n");
                writer.write("=" .repeat(100) + "\n");
            }

            System.out.println("\n✅ Schedules exported successfully to: " + filename);

        } catch (IOException e) {
            System.out.println("\n❌ Error exporting to file: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("\n❌ Error calculating schedules: " + e.getMessage());
        }
    }

    private int readInt() {
        while (true) {
            try {
                int value = scanner.nextInt();
                scanner.nextLine(); // clear buffer
                return value;
            } catch (InputMismatchException e) {
                System.out.print("❌ Invalid input! Please enter a number: ");
                scanner.nextLine(); // clear buffer
            }
        }
    }

    private String truncate(String str, int maxLength) {
        if (str.length() <= maxLength) {
            return str;
        }
        return str.substring(0, maxLength - 3) + "...";
    }

    // Main para testar
    public static void main(String[] args) {
        try {
            System.out.println("Loading data...\n");
            IRouteRepository repository = new CsvRouteRepository("freightMngt/data");

            SchedulerUI ui = new SchedulerUI(repository);
            ui.run();

        } catch (IOException e) {
            System.err.println("❌ Error loading data: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

