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
        System.out.println("(Definida com " + path.size() + " paragens/hubs principais)");

        // 2. Detetar Cargas na Base de Dados
        System.out.println("\n[BD] A consultar cargas pendentes na tabela FREIGHTS...");

        // Vai buscar todas as cargas que não estão associadas a comboios (lógica do repositório)
        // Nota: O teu repositório deve filtrar corretamente.
        List<Freight> allFreights = repository.getAllPendingFreights();

        // --- FILTRAGEM DE CARGAS ---
        List<Freight> filterFreights = new ArrayList<>();

        if (allFreights != null) {
            for (Freight f : allFreights) {
                int originIndex = -1;
                int destIndex = -1;

                // Validar se a carga faz sentido nesta rota (Direção correta)
                // A rota tem de conter a estação de origem E a de destino da carga
                for (int i = 0; i < path.size(); i++) {
                    int currentStationId = path.get(i).getId();

                    if (currentStationId == f.getOriginId()) {
                        originIndex = i;
                    }
                    if (currentStationId == f.getDestinationId()) {
                        destIndex = i;
                    }
                }

                // A origem tem de aparecer ANTES do destino na lista da rota
                if (originIndex != -1 && destIndex != -1 && originIndex < destIndex) {
                    filterFreights.add(f);
                }
            }
        }

        if (filterFreights.isEmpty()) {
            System.out.println("⚠ Nenhuma carga na BD é compatível com a direção/paragens desta rota.");
            System.out.println("   Sugestão: Verifique se a rota passa nos Hubs intermédios (ex: Contumil, Nine).");
            return;
        }

        System.out.println("[Sistema] Encontradas " + filterFreights.size() + " cargas compatíveis.");
        System.out.println("   (Ex: Carga #" + filterFreights.get(0).getId() + " de " + filterFreights.get(0).getOriginName() + ")");
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

        // ROTA 1: Baseada nas cargas 2001, 2002, 2003, 2004, 2005.
        // Origens variadas (Leixões, Contumil, Nine) -> Destinos Norte (Darque, Valença, Caminha)
        System.out.println("1. Linha do Minho Norte (Leixões -> Contumil -> Nine -> Valença)");

        // ROTA 2: Baseada nas cargas 2006, 2050, 2051.
        // Sentido inverso: Valença/Darque -> Leixões
        System.out.println("2. Retorno Minho Sul (Valença -> Nine -> Contumil -> Leixões)");

        // ROTA 3: Baseada na carga 2007 (Leixões -> Campanhã)
        System.out.println("3. Conexão Porto (Leixões -> Contumil -> Campanhã)");

        System.out.println("4. Listar TODAS as Estações (Consultar BD)");
        System.out.println("5. Definir Rota Manualmente (Inserir IDs)");
        System.out.println("0. Cancelar");
        System.out.print("Opção: ");

        int option = readInt();

        switch (option) {
            case 1:
                // IDs SQL: 50(Leixões), 13(Contumil), 20(Nine), 12(Darque), 11(Valença)
                // Incluir hubs intermédios é crucial para apanhar cargas como a 2005 (Nine->Valença) ou 2002 (Contumil->Valença)
                return fetchStations(50, 13, 20, 12, 11);

            case 2:
                // IDs SQL: 11(Valença), 12(Darque), 20(Nine), 13(Contumil), 50(Leixões)
                // Apanha Carga 2051 (Valença->Leixões) e 2006 (Darque->Leixões)
                return fetchStations(11, 12, 20, 13, 50);

            case 3:
                // IDs SQL: 50(Leixões), 13(Contumil), 5(Campanhã)
                // Apanha Carga 2007 (Leixões -> Campanhã)
                return fetchStations(50, 13, 5);

            case 4:
                listAllStationsFromDB();
                return selectPathStrategy(); // Recomeça

            case 5:
                return readUserPathManual();

            case 0:
            default:
                return null;
        }
    }

    private void listAllStationsFromDB() {
        System.out.println("\n--- ESTAÇÕES REGISTADAS NA BD ---");
        try {
            Collection<Station> stations = repository.getAllStations();
            if (stations == null || stations.isEmpty()) {
                System.out.println("⚠ A tabela STATION está vazia!");
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

    private List<Station> fetchStations(int... ids) {
        List<Station> path = new ArrayList<>();
        for (int id : ids) {
            try {
                Station s = repository.getStation(id);
                if (s != null) {
                    path.add(s);
                } else {
                    System.out.println("Erro: Estação ID " + id + " não encontrada na tabela STATION.");
                    return new ArrayList<>();
                }
            } catch (Exception e) {
                System.out.println("Erro Crítico de BD: " + e.getMessage());
                return new ArrayList<>();
            }
        }
        return path;
    }

    private List<Station> readUserPathManual() {
        List<Station> path = new ArrayList<>();
        System.out.println("\n--- Definição Manual de Rota ---");
        System.out.println("IDs Comuns: 50(Leixões), 13(Contumil), 20(Nine), 11(Valença), 5(Campanhã)");
        System.out.println("Digite '0' para terminar.");

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
                Station s = repository.getStation(id);

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