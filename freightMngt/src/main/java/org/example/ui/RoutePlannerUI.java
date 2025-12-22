package org.example.ui;

import org.example.domain.Freight;
import org.example.domain.Station;
import org.example.repository.IRouteRepository;
import org.example.service.RoutePlan;
import org.example.service.RoutePlannerService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class RoutePlannerUI {

    private final IRouteRepository repository;
    private final RoutePlannerService plannerService;
    private final RouteManifestPrinter printer;
    private final Scanner scanner;

    public RoutePlannerUI(IRouteRepository repository) {
        this.repository = repository;
        this.plannerService = new RoutePlannerService();
        this.printer = new RouteManifestPrinter();
        this.scanner = new Scanner(System.in);
    }

    public void run() {
        System.out.println("\n--- ROUTE PLANNER (USLP08) ---");
        System.out.println("Planeamento Logístico de Cargas");

        // 1. Data do Planeamento (Automática)
        LocalDate planDate = LocalDate.now();

        // 2. O Utilizador escolhe o Path
        List<Station> path = selectPathStrategy();

        if (path == null || path.isEmpty()) {
            return; // Cancelado ou inválido
        }

        // Mostrar resumo da rota
        System.out.println("\nRota Selecionada: " + path.get(0).getName() + " -> " + path.get(path.size()-1).getName());
        System.out.println("(Passando por " + (path.size()-2) + " estações intermédias)");

        // 3. Buscar Cargas
        List<Freight> allFreights = repository.getAllPendingFreights();
        List<Freight> filterFreights = new ArrayList<>();

        if (allFreights != null) {
            for (Freight f : allFreights) {
                // Filtro de Data
                if (f.getDate().isAfter(planDate)) {
                    continue;
                }

                int originIndex = -1;
                int destIndex = -1;

                // Encontrar índices na rota
                for (int i = 0; i < path.size(); i++) {
                    int currentStationId = path.get(i).getId();
                    if (currentStationId == f.getOriginId()) originIndex = i;
                    if (currentStationId == f.getDestinationId()) destIndex = i;
                }

                // Regras de Negócio
                if (originIndex != -1 && destIndex != -1 && originIndex < destIndex) {
                    filterFreights.add(f);
                }
            }
        }

        if (filterFreights.isEmpty()) {
            System.out.println("(!) Nenhuma carga compatível encontrada para esta rota/data.");
            return;
        }

        System.out.println("\n[Sistema] Encontradas " + filterFreights.size() + " cargas para transporte.");
        System.out.println("A gerar manifesto...");

        try {
            // 4. Calcular Plano
            RoutePlan plan = plannerService.planRoute(path, filterFreights);

            // 5. Imprimir
            printer.print(plan);

        } catch (Exception e) {
            // Substituído System.err por System.out para não ser vermelho
            System.out.println("Erro ao calcular o plano: " + e.getMessage());
        }
    }

    private List<Station> selectPathStrategy() {
        System.out.println("\nSelecione a Rota do Comboio:");

        System.out.println("1. Linha do Minho Norte (Porto Campanhã -> Valença)");
        System.out.println("2. Linha do Minho Sul (Valença -> Porto Campanhã)");
        System.out.println("3. Corredor de Exportação (Leixões -> Valença)");
        System.out.println("4. Definir Rota Manualmente");
        System.out.println("0. Cancelar");
        System.out.print("Opção: ");

        int option = readInt();

        switch (option) {
            case 1:
                return fetchStations(5, 13, 20, 8, 12, 17, 11);
            case 2:
                return fetchStations(11, 17, 12, 8, 20, 13, 5);
            case 3:
                return fetchStations(50, 48, 45, 43, 13, 20, 8, 12, 17, 11);
            case 4:
                return readUserPathManual();
            case 0:
            default:
                return null;
        }
    }

    private List<Station> fetchStations(int... ids) {
        List<Station> path = new ArrayList<>();
        for (int id : ids) {
            try {
                Station s = repository.getStation(id);
                if (s != null) {
                    path.add(s);
                } else {
                    System.out.println("(!) Estação ID " + id + " não encontrada na BD.");
                    return null;
                }
            } catch (Exception e) {
                System.out.println("(!) Erro de BD: " + e.getMessage());
                return null;
            }
        }
        return path;
    }

    private List<Station> readUserPathManual() {
        List<Station> path = new ArrayList<>();
        System.out.println("\n--- Definição Manual ---");
        System.out.println("Insira os IDs das estações (ex: 5 20 11). Digite '0' para terminar.");

        while (true) {
            System.out.print("Estação #" + (path.size() + 1) + ": ");
            String input = scanner.next();

            if (input.equals("0") || input.equalsIgnoreCase("fim")) {
                if (path.size() < 2) {
                    System.out.println("(!) Rota incompleta.");
                    return null;
                }
                break;
            }

            try {
                int id = Integer.parseInt(input);
                Station s = repository.getStation(id);

                if (s != null) {
                    System.out.println("   -> " + s.getName());
                    path.add(s);
                } else {
                    System.out.println("   (!) ID inválido.");
                }
            } catch (NumberFormatException e) {
                System.out.println("   (!) Digite um número.");
            }
        }
        scanner.nextLine();
        return path;
    }

    private int readInt() {
        try {
            int i = scanner.nextInt();
            scanner.nextLine();
            return i;
        } catch (Exception e) {
            scanner.nextLine();
            return -1;
        }
    }
}