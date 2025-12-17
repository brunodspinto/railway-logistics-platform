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
            return; // Cancelado pelo utilizador
        }

        System.out.println("\nRota Selecionada: " + path.get(0).getName() + " -> " + path.get(path.size()-1).getName());
        System.out.println("(Passando por " + (path.size()-2) + " estações intermédias)");

        // 2. Detetar Cargas (Simulação)
        List<Freight> allFreights = getMockFreights();
        System.out.println("\n[Sistema] A procurar cargas pendentes compatíveis...");

        // --- FILTRAGEM DE CARGAS ---
        // Seleciona apenas as cargas cuja Origem E Destino fazem parte da rota escolhida
        List<Freight> filterFreights = new ArrayList<>();
        for (Freight f : allFreights) {
            boolean hasOrigin = path.stream().anyMatch(s -> s.getId() == f.getOriginId());
            boolean hasDest = path.stream().anyMatch(s -> s.getId() == f.getDestinationId());

            if (hasOrigin && hasDest) {
                filterFreights.add(f);
            }
        }

        if (filterFreights.isEmpty()) {
            System.out.println("⚠  Nenhuma carga pendente é compatível com esta rota.");
            System.out.println("   (O comboio seguirá vazio ou a rota não passa onde as cargas estão)");
            return;
        }

        System.out.println("[Sistema] Encontradas " + filterFreights.size() + " cargas compatíveis (de " + allFreights.size() + " totais).");
        System.out.println("\nA gerar manifesto...");

        try {
            // 3. Calcular (Envia apenas as cargas filtradas para evitar warnings)
            RoutePlan plan = plannerService.planRoute(path, filterFreights);

            // 4. Imprimir
            printer.print(plan);

        } catch (Exception e) {
            System.err.println("Erro ao calcular o plano: " + e.getMessage());
        }
    }

    /**
     * Menu para escolher entre rotas predefinidas ou manual
     */
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
                return fetchStations(10, 25, 40); // Lisboa, Entroncamento, Porto
            case 2:
                return fetchStations(40, 55);     // Porto, Braga
            case 3:
                return fetchStations(10, 25, 30, 40, 55); // Rota completa
            case 4:
                return readUserPathManual();
            case 0:
            default:
                return null;
        }
    }

    /**
     * Auxiliar para converter lista de IDs em lista de objetos Station
     */
    private List<Station> fetchStations(int... ids) {
        List<Station> path = new ArrayList<>();
        for (int id : ids) {
            try {
                Station s = repository.getStation(id);
                if (s != null) {
                    path.add(s);
                } else {
                    // Fallback para Mock se a BD falhar
                    path.add(new Station(id, "Estação " + id));
                }
            } catch (Exception e) {
                path.add(new Station(id, "Estação " + id)); // Fallback erro
            }
        }
        return path;
    }

    private List<Station> readUserPathManual() {
        List<Station> path = new ArrayList<>();
        System.out.println("\n--- Definição Manual ---");
        System.out.println("Insira a sequência de IDs (ex: 10 25 40). Digite '0' para terminar.");

        while (true) {
            System.out.print("ID da Estação #" + (path.size() + 1) + ": ");
            String input = scanner.next();

            if (input.equals("0") || input.equalsIgnoreCase("fim")) {
                if (path.size() < 2) {
                    System.out.println("⚠ Rota inválida. Mínimo 2 estações.");
                    path.clear();
                    continue;
                }
                break;
            }

            try {
                int id = Integer.parseInt(input);
                List<Station> fetched = fetchStations(id); // Reutiliza o método seguro
                if (!fetched.isEmpty()) {
                    System.out.println("   -> Adicionada: " + fetched.get(0).getName());
                    path.add(fetched.get(0));
                }
            } catch (NumberFormatException e) {
                System.out.println("   ⚠ ID inválido.");
            }
        }
        scanner.nextLine(); // Limpar buffer
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

    // --- MOCKS DE CARGAS (Para teste) ---
    private List<Freight> getMockFreights() {
        List<Freight> list = new ArrayList<>();

        // Carga 1: Lisboa -> Porto
        list.add(new Freight(501, LocalDate.now(),
                10, "Lisboa Santa Apolónia",
                40, "Porto Campanhã",
                Arrays.asList("W01", "W02")));

        // Carga 2: Entroncamento -> Porto
        list.add(new Freight(502, LocalDate.now(),
                25, "Entroncamento",
                40, "Porto Campanhã",
                Arrays.asList("W03")));

        // Carga 3: Porto -> Braga
        list.add(new Freight(600, LocalDate.now(),
                40, "Porto Campanhã",
                55, "Braga",
                Arrays.asList("W04", "W05")));

        // Carga 4: Coimbra -> Braga
        list.add(new Freight(700, LocalDate.now(),
                30, "Coimbra B",
                55, "Braga",
                Arrays.asList("W06")));

        return list;
    }
}