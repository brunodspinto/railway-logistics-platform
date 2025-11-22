package org.example.ui;

import org.example.domain.*;
import org.example.repository.IRouteRepository;
import org.example.service.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

/**
 * USLP07 - Interface para o Freight Manager criar agendamentos com path manual
 */
public class ManualSchedulerUI {
    private final Scanner scanner;
    private final IRouteRepository repository;
    private final SchedulerService schedulerService;

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    public ManualSchedulerUI(IRouteRepository repository) {
        this.scanner = new Scanner(System.in);
        this.repository = repository;
        this.schedulerService = new SchedulerService(repository);
    }

    public void start() {
        System.out.println("\n" + "═".repeat(80));
        System.out.println("  FREIGHT MANAGER - MANUAL TRAIN SCHEDULER (USLP07)");
        System.out.println("═".repeat(80));

        try {
            // 1. Input básico do train
            Train train = createManualTrain();

            // 2. Calcular schedule
            System.out.println("\n⏳ Calculating schedule...\n");
            TrainSchedule schedule = schedulerService.calculateSchedule(train);

            // 3. Detectar crossings (se múltiplos trains)
            ScheduleResult result = schedulerService.calculateSchedulesWithConflicts(
                    Collections.singletonList(train)
            );

            // 4. Mostrar resultados
            displayResults(schedule, result);

        } catch (Exception e) {
            System.err.println("\n✗ Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private Train createManualTrain() {
        System.out.println("\n--- TRAIN INFORMATION ---\n");

        // 1. Train ID
        System.out.print("Train ID (number): ");
        int trainId = Integer.parseInt(scanner.nextLine().trim());

        // 2. Operator
        System.out.print("Operator (VAT): ");
        String operator = scanner.nextLine().trim();

        // 3. Date
        LocalDate date = readDate();

        // 4. Time
        LocalTime time = readTime();

        // 5. Path manual (estações em ordem)
        List<Integer> pathStationIds = readManualPath();

        if (pathStationIds.size() < 2) {
            throw new IllegalArgumentException("Path must have at least 2 stations");
        }

        int startId = pathStationIds.get(0);
        int endId = pathStationIds.get(pathStationIds.size() - 1);

        // 6. Locomotivas
        List<Integer> locomotiveNumbers = readLocomotives();

        // 7. Freights (simplificado: criar freight dummy ou usar existentes)
        List<Integer> freightIds = readFreights();

        // 8. Criar Train
        Train train = new Train(
                trainId,
                operator,
                date,
                time,
                startId,
                endId,
                freightIds,
                locomotiveNumbers,
                pathStationIds
        );

        // 9. Lazy load dados completos
        loadTrainData(train);

        return train;
    }

    private LocalDate readDate() {
        while (true) {
            System.out.print("Departure date (dd/MM/yyyy): ");
            String input = scanner.nextLine().trim();

            try {
                return LocalDate.parse(input, DATE_FORMAT);
            } catch (DateTimeParseException e) {
                System.out.println("✗ Invalid format. Use: dd/MM/yyyy (e.g., 25/11/2025)");
            }
        }
    }

    private LocalTime readTime() {
        while (true) {
            System.out.print("Departure time (HH:mm): ");
            String input = scanner.nextLine().trim();

            try {
                return LocalTime.parse(input, TIME_FORMAT);
            } catch (DateTimeParseException e) {
                System.out.println("✗ Invalid format. Use: HH:mm (e.g., 09:45)");
            }
        }
    }

    private List<Integer> readManualPath() {
        System.out.println("\n--- MANUAL PATH DEFINITION ---");
        System.out.println("⚠ IMPORTANT: You must define ALL stations in the path,");
        System.out.println("   including intermediate stops (direct connections only)");
        System.out.println("\nEnter station IDs in order (one per line)");
        System.out.println("Type 'DONE' when finished, 'LIST' to see all stations\n");

        // Mostrar estações disponíveis
        System.out.println("Available stations:");
        Collection<Station> stations = repository.getAllStations();
        stations.stream()
                .limit(10)
                .forEach(s -> System.out.printf("  %d - %s\n", s.getId(), s.getName()));
        System.out.println("  ... (type 'LIST' to see all)\n");

        List<Integer> path = new ArrayList<>();
        int step = 1;

        while (true) {
            System.out.printf("Station %d (or DONE/LIST/LINES): ", step);
            String input = scanner.nextLine().trim().toUpperCase();

            if (input.equals("DONE")) {
                if (path.size() < 2) {
                    System.out.println("✗ Path must have at least 2 stations!");
                    continue;
                }
                break;
            }

            if (input.equals("LIST")) {
                listAllStations();
                continue;
            }

            if (input.equals("LINES")) {
                showAvailableLines();
                continue;
            }

            try {
                int stationId = Integer.parseInt(input);
                Station station = repository.getStation(stationId);

                if (station == null) {
                    System.out.println("✗ Station not found: " + stationId);
                    continue;
                }

                // Validar conexão com estação anterior (se não for a primeira)
                if (!path.isEmpty()) {
                    int previousId = path.get(path.size() - 1);
                    Line line = repository.findDirectLine(previousId, stationId);

                    if (line == null) {
                        Station prevStation = repository.getStation(previousId);
                        System.out.println("\n✗ NO DIRECT CONNECTION!");
                        System.out.printf("   From: %s (ID: %d)\n", prevStation.getName(), previousId);
                        System.out.printf("   To:   %s (ID: %d)\n", station.getName(), stationId);
                        System.out.println("\n💡 You need to add intermediate stations.");
                        System.out.println("   Type 'LINES' to see available direct connections.");
                        continue;
                    }

                    System.out.printf("  ✓ Connection found: %s (%.1f km)\n",
                            line.getName(), line.getTotalLengthKm());
                }

                path.add(stationId);
                System.out.printf("  ✓ Added: %s (ID: %d)\n", station.getName(), stationId);
                step++;

            } catch (NumberFormatException e) {
                System.out.println("✗ Invalid input. Enter station ID, DONE, LIST, or LINES");
            }
        }

        System.out.println("\n✓ Path defined with " + path.size() + " stations");
        displayPathSummary(path);
        return path;
    }

    private void showAvailableLines() {
        System.out.println("\n" + "─".repeat(80));
        System.out.println("AVAILABLE DIRECT LINE CONNECTIONS:");
        System.out.println("─".repeat(80));

        Collection<Line> lines = repository.getAllLines();

        if (lines.isEmpty()) {
            System.out.println("No lines found in database.");
        } else {
            for (Line line : lines) {
                System.out.printf("  %-35s: %s → %s (%.1f km)\n",
                        line.getName(),
                        line.getStartStation().getName(),
                        line.getEndStation().getName(),
                        line.getTotalLengthKm());
            }
        }

        System.out.println("─".repeat(80) + "\n");
    }

    private void displayPathSummary(List<Integer> path) {
        System.out.println("\n" + "─".repeat(60));
        System.out.println("PATH SUMMARY:");
        System.out.println("─".repeat(60));

        double totalDistance = 0;

        for (int i = 0; i < path.size(); i++) {
            Station station = repository.getStation(path.get(i));
            System.out.printf("  %d. %s (ID: %d)\n", i + 1, station.getName(), station.getId());

            // Mostrar linha para próxima estação
            if (i < path.size() - 1) {
                Line line = repository.findDirectLine(path.get(i), path.get(i + 1));
                if (line != null) {
                    System.out.printf("     └─ %s (%.1f km)\n", line.getName(), line.getTotalLengthKm());
                    totalDistance += line.getTotalLengthKm();
                }
            }
        }

        System.out.println("─".repeat(60));
        System.out.printf("Total distance: %.1f km\n", totalDistance);
        System.out.println("─".repeat(60) + "\n");
    }

    private void listAllStations() {
        System.out.println("\n" + "─".repeat(60));
        System.out.println("ALL STATIONS:");
        System.out.println("─".repeat(60));

        Collection<Station> stations = repository.getAllStations();
        stations.forEach(s -> System.out.printf("  %3d - %s\n", s.getId(), s.getName()));

        System.out.println("─".repeat(60) + "\n");
    }

    private List<Integer> readLocomotives() {
        System.out.println("\n--- LOCOMOTIVES ---");
        System.out.print("Number of locomotives: ");
        int count = Integer.parseInt(scanner.nextLine().trim());

        List<Integer> numbers = new ArrayList<>();

        for (int i = 1; i <= count; i++) {
            while (true) {
                System.out.printf("Locomotive %d (number or 'LIST'): ", i);
                String input = scanner.nextLine().trim().toUpperCase();

                if (input.equals("LIST")) {
                    listAvailableLocomotives();
                    continue;
                }

                try {
                    int locoNum = Integer.parseInt(input);
                    Locomotive loco = repository.getLocomotive(locoNum);

                    if (loco == null) {
                        System.out.println("✗ Locomotive not found: " + locoNum);
                        continue;
                    }

                    numbers.add(locoNum);
                    System.out.printf("  ✓ Added: %s (%s, %d kW)\n",
                            loco.getName(), loco.getType(), loco.getPower());
                    break;

                } catch (NumberFormatException e) {
                    System.out.println("✗ Invalid input");
                }
            }
        }

        return numbers;
    }

    private void listAvailableLocomotives() {
        System.out.println("\n" + "─".repeat(70));
        System.out.println("AVAILABLE LOCOMOTIVES:");
        System.out.println("─".repeat(70));

        Collection<Locomotive> locos = repository.getAllLocomotives();
        locos.forEach(l -> System.out.printf("  %5d - %-15s (%s, %4d kW, %3d km/h)\n",
                l.getNumber(), l.getName(), l.getType(), l.getPower(), l.getMaxSpeed()));

        System.out.println("─".repeat(70) + "\n");
    }

    private List<Integer> readFreights() {
        System.out.println("\n--- FREIGHTS ---");
        System.out.println("Options:");
        System.out.println("  0 - Empty train (no freight)");
        System.out.println("  1+ - Add existing freights from database");
        System.out.print("\nNumber of freights: ");
        int count = Integer.parseInt(scanner.nextLine().trim());

        List<Integer> freightIds = new ArrayList<>();

        if (count == 0) {
            System.out.println("⚠ Creating empty train (no freight)");
            freightIds.add(9999); // ID dummy
            return freightIds;
        }

        System.out.println("\nType 'LIST' to see available freights");

        for (int i = 1; i <= count; i++) {
            while (true) {
                System.out.printf("Freight %d (ID or 'LIST'): ", i);
                String input = scanner.nextLine().trim().toUpperCase();

                if (input.equals("LIST")) {
                    listAvailableFreights();
                    continue;
                }

                try {
                    int freightId = Integer.parseInt(input);
                    Freight freight = repository.getFreight(freightId);

                    if (freight == null) {
                        System.out.println("✗ Freight not found: " + freightId);
                        continue;
                    }

                    freightIds.add(freightId);
                    System.out.printf("  ✓ Added: Freight %d (%s → %s, %d wagons)\n",
                            freightId,
                            freight.getOriginName(),
                            freight.getDestinationName(),
                            freight.getWagonCount());
                    break;

                } catch (NumberFormatException e) {
                    System.out.println("✗ Invalid input");
                }
            }
        }

        return freightIds;
    }

    private void listAvailableFreights() {
        System.out.println("\n" + "─".repeat(80));
        System.out.println("AVAILABLE FREIGHTS:");
        System.out.println("─".repeat(80));

        Collection<Freight> freights = repository.getAllFreights();

        if (freights.isEmpty()) {
            System.out.println("  No freights found in database");
        } else {
            System.out.printf("%-8s %-20s %-20s %-10s %-10s\n",
                    "ID", "Origin", "Destination", "Wagons", "Date");
            System.out.println("─".repeat(80));

            for (Freight f : freights) {
                System.out.printf("%-8d %-20s %-20s %-10d %-10s\n",
                        f.getId(),
                        truncate(f.getOriginName(), 20),
                        truncate(f.getDestinationName(), 20),
                        f.getWagonCount(),
                        f.getDate());
            }
        }

        System.out.println("─".repeat(80) + "\n");
    }

    private String truncate(String str, int maxLength) {
        if (str.length() <= maxLength) {
            return str;
        }
        return str.substring(0, maxLength - 3) + "...";
    }

    private void loadTrainData(Train train) {
        // Lazy load stations
        train.setStartStation(repository.getStation(train.getStartId()));
        train.setEndStation(repository.getStation(train.getEndId()));

        List<Station> pathStations = new ArrayList<>();
        for (int stationId : train.getPathStationIds()) {
            Station station = repository.getStation(stationId);
            if (station != null) {
                pathStations.add(station);
            }
        }
        train.setPathStations(pathStations);

        // Lazy load locomotives
        List<Locomotive> locomotives = new ArrayList<>();
        for (int locoNum : train.getLocomotiveNumbers()) {
            Locomotive loco = repository.getLocomotive(locoNum);
            if (loco != null) {
                locomotives.add(loco);
            }
        }
        train.setLocomotives(locomotives);

        // Lazy load freights
        List<Freight> freights = new ArrayList<>();
        for (int freightId : train.getFreightIds()) {
            if (freightId == 9999) continue; // Skip dummy

            Freight freight = repository.getFreight(freightId);
            if (freight != null) {
                freights.add(freight);
            }
        }
        train.setFreights(freights);
    }

    private void displayResults(TrainSchedule schedule, ScheduleResult result) {
        System.out.println("\n" + "═".repeat(100));
        System.out.println("  SCHEDULE CALCULATION COMPLETE");
        System.out.println("═".repeat(100));

        // 1. Mostrar schedule detalhado
        System.out.println(schedule.format());

        displayFreightOperations(schedule.getTrain());

        // 2. Mostrar crossings (se houver)
        if (result.hasCrossings()) {
            System.out.println("\n⚠ CROSSING OPERATIONS REQUIRED:\n");
            System.out.println(result.formatCrossings());
        } else {
            System.out.println("\n✓ No crossing operations required - clear path!\n");
        }

        // 3. Summary
        System.out.println("═".repeat(100));
        System.out.println("SUMMARY:");
        System.out.println("─".repeat(100));
        System.out.printf("  Total distance:    %.1f km\n", schedule.getTotalDistanceKm());
        System.out.printf("  Total duration:    %dh %02dm\n",
                schedule.getTotalTravelMinutes() / 60,
                schedule.getTotalTravelMinutes() % 60);
        System.out.printf("  Average speed:     %.1f km/h\n", schedule.getAverageSpeedKmh());
        System.out.printf("  Total weight:      %.1f tons\n", schedule.getTrain().getTotalWeightTons());
        System.out.printf("  Total power:       %d kW\n", schedule.getTrain().getTotalPowerKw());
        System.out.println("═".repeat(100));
    }

    private void displayFreightOperations(Train train) {
        if (train.getFreights().isEmpty()) {
            System.out.println("\n═".repeat(100));
            System.out.println("FREIGHT OPERATIONS:");
            System.out.println("═".repeat(100));
            System.out.println("  ⚠ No freight operations (empty train)");
            System.out.println("═".repeat(100));
            return;
        }

        System.out.println("\n═".repeat(100));
        System.out.println("FREIGHT OPERATIONS:");
        System.out.println("═".repeat(100));

        // Agrupar operações por estação
        Map<Integer, List<String>> loadOps = new HashMap<>();
        Map<Integer, List<String>> unloadOps = new HashMap<>();

        for (Freight freight : train.getFreights()) {
            int originId = freight.getOriginId();
            int destId = freight.getDestinationId();

            // Operação de carga
            loadOps.computeIfAbsent(originId, k -> new ArrayList<>())
                    .add(String.format("LOAD Freight #%d (%d wagons, %.1f tons) → %s",
                            freight.getId(),
                            freight.getWagonCount(),
                            freight.getTotalWeightTons(),
                            freight.getDestinationName()));

            // Operação de descarga
            unloadOps.computeIfAbsent(destId, k -> new ArrayList<>())
                    .add(String.format("UNLOAD Freight #%d (%d wagons, %.1f tons) from %s",
                            freight.getId(),
                            freight.getWagonCount(),
                            freight.getTotalWeightTons(),
                            freight.getOriginName()));
        }

        // Mostrar operações em ordem do path
        boolean hasOperations = false;

        for (Station station : train.getPathStations()) {
            List<String> loads = loadOps.get(station.getId());
            List<String> unloads = unloadOps.get(station.getId());

            if ((loads != null && !loads.isEmpty()) || (unloads != null && !unloads.isEmpty())) {
                hasOperations = true;
                System.out.printf("\n📍 %s (ID: %d):\n", station.getName(), station.getId());

                if (loads != null) {
                    loads.forEach(op -> System.out.println("   🔵 " + op));
                }

                if (unloads != null) {
                    unloads.forEach(op -> System.out.println("   🔴 " + op));
                }
            }
        }

        if (!hasOperations) {
            System.out.println("  ℹ️ No freight operations at intermediate stations");
            System.out.println("     (All freight travels full route)");
        }
    }

    // ✨ NOVO: Calcular tempo de movimento real
    private double calculateMovementTime(TrainSchedule schedule) {
        double totalHours = 0;

        for (ScheduleEntry entry : schedule.getEntries()) {
            if (entry.getSpeedKmh() > 0 && entry.getSegmentDistanceKm() > 0) {
                totalHours += entry.getSegmentDistanceKm() / entry.getSpeedKmh();
            }
        }

        return totalHours * 60; // Retornar em minutos
    }
}
