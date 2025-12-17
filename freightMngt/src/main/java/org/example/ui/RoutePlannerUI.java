package org.example.ui;

import org.example.domain.Freight;
import org.example.domain.Station;
import org.example.repository.IRouteRepository;
import org.example.service.RoutePlan;
import org.example.service.RoutePlannerService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
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

        // 1. O Utilizador escolhe o Path (Via Menu ou Manual)
        List<Station> path = selectPathStrategy();

        if (path == null || path.isEmpty()) {
            return; // Cancelado ou inválido
        }

        System.out.println("\nRota Selecionada: " + path.get(0).getName() + " -> " + path.get(path.size()-1).getName());
        System.out.println("(Passando por " + (path.size()-2) + " estações intermédias)");

        // 2. Detetar Cargas (CORRIGIDO AQUI)
        System.out.println("\n[Sistema] A procurar cargas pendentes compatíveis...");

        // Chama o metodo do repositório e guarda na lista 'allFreights'
        List<Freight> allFreights = repository.getAllPendingFreights();

        // --- FILTRAGEM DE CARGAS ---
        List<Freight> filterFreights = new ArrayList<>();

        if (allFreights != null) { // Proteção contra null pointer
            for (Freight f : allFreights) {
                int originIndex = -1;
                int destIndex = -1;

                // Encontrar os índices das estações na rota atual para validar a direção
                for (int i = 0; i < path.size(); i++) {
                    int currentStationId = path.get(i).getId();

                    if (currentStationId == f.getOriginId()) {
                        originIndex = i;
                    }
                    if (currentStationId == f.getDestinationId()) {
                        destIndex = i;
                    }
                }

                // CRITÉRIO DE ACEITAÇÃO:
                // 1. A rota contém a estação de origem.
                // 2. A rota contém a estação de destino.
                // 3. A origem aparece ANTES do destino (originIndex < destIndex).
                if (originIndex != -1 && destIndex != -1 && originIndex < destIndex) {
                    filterFreights.add(f);
                }
            }
        }

        if (filterFreights.isEmpty()) {
            System.out.println("⚠  Nenhuma carga pendente é compatível com a direção desta rota.");
            System.out.println("   (O comboio seguirá vazio ou a rota é inversa às cargas disponíveis)");
            return;
        }

        System.out.println("[Sistema] Encontradas " + filterFreights.size() + " cargas compatíveis (de " + (allFreights != null ? allFreights.size() : 0) + " totais).");
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
        System.out.println("1. Linha do Norte (Lisboa -> Entroncamento -> Porto)");
        System.out.println("2. Ramal de Braga (Porto -> Braga)");
        System.out.println("3. Longo Curso (Lisboa -> Entroncamento -> Coimbra -> Porto -> Braga)");
        System.out.println("4. Definir Rota Manualmente (Inserir IDs)");
        System.out.println("0. Cancelar");
        System.out.print("Opção: ");

        int option = readInt();

        switch (option) {
            case 1:
                return fetchStations(10, 25, 40);
            case 2:
                return fetchStations(40, 55);
            case 3:
                return fetchStations(10, 25, 30, 40, 55);
            case 4:
                return readUserPathManual();
            case 0:
            default:
                return null;
        }
    }

    /**
     * Busca estações à BD. Se falhar alguma, devolve lista vazia (rota inválida).
     */
    private List<Station> fetchStations(int... ids) {
        List<Station> path = new ArrayList<>();
        for (int id : ids) {
            try {
                Station s = repository.getStation(id);
                if (s != null) {
                    path.add(s);
                } else {
                    System.out.println("Erro de Configuração: Estação ID " + id + " não encontrada na BD.");
                    return new ArrayList<>(); // Retorna vazio para abortar
                }
            } catch (Exception e) {
                System.out.println("Erro de BD ao buscar estação " + id + ": " + e.getMessage());
                return new ArrayList<>();
            }
        }
        return path;
    }

    /**
     * Lê IDs manualmente e valida rigorosamente contra a BD.
     */
    private List<Station> readUserPathManual() {
        List<Station> path = new ArrayList<>();
        System.out.println("\n--- Definição Manual ---");
        System.out.println("Insira a sequência de IDs existentes na BD (ex: 10 25 40).");
        System.out.println("Digite '0' para terminar a inserção.");

        while (true) {
            System.out.print("ID da Estação #" + (path.size() + 1) + ": ");
            String input = scanner.next();

            if (input.equals("0") || input.equalsIgnoreCase("fim")) {
                if (path.size() < 2) {
                    System.out.println("⚠ Rota incompleta. Mínimo 2 estações necessárias.");
                    path.clear(); // Reseta ou pede para continuar? Aqui forçamos reinício ou aborto.
                    // Para simplificar, se sair com <2, aborta:
                    return null;
                }
                break;
            }

            try {
                int id = Integer.parseInt(input);
                Station s = repository.getStation(id); // Validação Real

                if (s != null) {
                    System.out.println("   -> Adicionada: " + s.getName()); // Mostra o nome real da BD
                    path.add(s);
                } else {
                    // MENSAGEM DE ERRO (O que pediste)
                    System.out.println("    ERRO: Estação com ID " + id + " não existe na Base de Dados.");
                    System.out.println("    Por favor, insira um ID válido.");
                }
            } catch (NumberFormatException e) {
                System.out.println("   ⚠ ID inválido. Insira um número.");
            } catch (Exception e) {
                System.out.println("   ⚠ Erro técnico: " + e.getMessage());
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