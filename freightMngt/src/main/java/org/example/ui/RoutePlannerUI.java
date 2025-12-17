package org.example.ui;

import org.example.domain.Freight;
import org.example.domain.Station;
import org.example.repository.IRouteRepository;
import org.example.service.RoutePlan;
import org.example.service.RoutePlannerService;

import java.util.ArrayList;
import java.util.Collection;
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
        System.out.println("\n--- ROUTE PLANNER (USLP08 - DATABASE MODE) ---");
        System.out.println("Planeamento Logístico de Cargas");

        // 1. O Utilizador escolhe o Path
        List<Station> path = selectPathStrategy();

        if (path == null || path.isEmpty()) {
            return; // Cancelado ou inválido
        }

        System.out.println("\nRota Selecionada: " + path.get(0).getName() + " -> " + path.get(path.size()-1).getName());
        System.out.println("(Passando por " + (path.size()-2) + " estações intermédias)");

        // 2. Detetar Cargas na Base de Dados
        System.out.println("\n[BD] A consultar cargas pendentes...");

        // Esta chamada vai ao DatabaseRepositoryFacade -> FreightRepository (SELECT * FROM Freights...)
        List<Freight> allFreights = repository.getAllPendingFreights();

        // --- FILTRAGEM DE CARGAS ---
        List<Freight> filterFreights = new ArrayList<>();

        if (allFreights != null) {
            for (Freight f : allFreights) {
                int originIndex = -1;
                int destIndex = -1;

                // Validar se a carga faz sentido nesta rota (Direção correta)
                for (int i = 0; i < path.size(); i++) {
                    int currentStationId = path.get(i).getId();

                    if (currentStationId == f.getOriginId()) {
                        originIndex = i;
                    }
                    if (currentStationId == f.getDestinationId()) {
                        destIndex = i;
                    }
                }

                // A origem tem de aparecer ANTES do destino
                if (originIndex != -1 && destIndex != -1 && originIndex < destIndex) {
                    filterFreights.add(f);
                }
            }
        }

        if (filterFreights.isEmpty()) {
            System.out.println("⚠ Nenhuma carga na BD é compatível com esta rota.");
            return;
        }

        System.out.println("[Sistema] Encontradas " + filterFreights.size() + " cargas compatíveis.");
        System.out.println("\nA gerar manifesto...");

        try {
            RoutePlan plan = plannerService.planRoute(path, filterFreights);
            printer.print(plan);

        } catch (Exception e) {
            System.err.println("Erro ao calcular o plano: " + e.getMessage());
        }
    }

    private List<Station> selectPathStrategy() {
        System.out.println("\nSelecione a Rota do Comboio:");
        System.out.println("1. Rota Sugerida A: Leixões -> Darque -> Valença (IDs: 50, 12, 11)");
        System.out.println("2. Rota Sugerida B: Nine -> Valença (IDs: 20, 11)");
        System.out.println("3. Rota Sugerida C: Leixões -> Porto Campanhã (IDs: 50, 5)");
        System.out.println("4. Listar TODAS as Estações (Consultar BD)");
        System.out.println("5. Definir Rota Manualmente (Inserir IDs)");
        System.out.println("0. Cancelar");
        System.out.print("Opção: ");

        int option = readInt();

        switch (option) {
            case 1:
                return fetchStations(50, 12, 11);
            case 2:
                return fetchStations(20, 11);
            case 3:
                return fetchStations(50, 5);
            case 4:
                listAllStationsFromDB();
                return selectPathStrategy(); // Volta ao menu após listar
            case 5:
                return readUserPathManual();
            case 0:
            default:
                return null;
        }
    }

    /**
     * Imprime todas as estações presentes na BD para o utilizador saber que IDs usar.
     */
    private void listAllStationsFromDB() {
        System.out.println("\n--- ESTAÇÕES REGISTADAS NA BD ---");
        try {
            Collection<Station> stations = repository.getAllStations();
            if (stations.isEmpty()) {
                System.out.println("⚠ A tabela de estações está vazia!");
            } else {
                for (Station s : stations) {
                    System.out.printf("ID: %-4d | Nome: %s%n", s.getId(), s.getName());
                }
            }
        } catch (Exception e) {
            System.out.println("Erro ao ler da BD: " + e.getMessage());
        }
        System.out.println("---------------------------------");
    }

    /**
     * Busca estações à BD pelos IDs fornecidos.
     */
    private List<Station> fetchStations(int... ids) {
        List<Station> path = new ArrayList<>();
        for (int id : ids) {
            try {
                Station s = repository.getStation(id); // Chamada SQL via Repository
                if (s != null) {
                    path.add(s);
                } else {
                    System.out.println("Erro: Estação ID " + id + " não encontrada na BD.");
                    return new ArrayList<>();
                }
            } catch (Exception e) {
                System.out.println("Erro de BD: " + e.getMessage());
                return new ArrayList<>();
            }
        }
        return path;
    }

    private List<Station> readUserPathManual() {
        List<Station> path = new ArrayList<>();
        System.out.println("\n--- Definição Manual de Rota ---");
        System.out.println("Insira os IDs sequencialmente. Digite '0' para terminar.");

        while (true) {
            System.out.print("ID da Estação #" + (path.size() + 1) + ": ");
            String input = scanner.next();

            if (input.equals("0") || input.equalsIgnoreCase("fim")) {
                if (path.size() < 2) {
                    System.out.println("⚠ Rota incompleta. Mínimo 2 estações.");
                    return null;
                }
                break;
            }

            try {
                int id = Integer.parseInt(input);
                Station s = repository.getStation(id); // Validação na BD

                if (s != null) {
                    System.out.println("   -> Adicionada: " + s.getName());
                    path.add(s);
                } else {
                    System.out.println("    ERRO: ID " + id + " não existe na BD.");
                }
            } catch (NumberFormatException e) {
                System.out.println("   ⚠ ID inválido.");
            } catch (Exception e) {
                System.out.println("   ⚠ Erro de BD: " + e.getMessage());
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