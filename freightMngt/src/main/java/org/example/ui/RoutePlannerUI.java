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

        // 1. Simular a escolha de um caminho (Path)
        // Num cenário real, isto viria da BD ou do algoritmo de caminho mais curto
        System.out.println("Generating demo path for testing...");
        List<Station> path = getMockPath();

        if (path.isEmpty()) {
            System.out.println("Error: Could not generate path.");
            return;
        }

        System.out.println("Path defined: " + path.get(0).getName() + " -> " + path.get(path.size()-1).getName());

        // 2. Simular a escolha de Cargas (Freights)
        // Num cenário real, listarias as cargas pendentes da BD
        List<Freight> freights = getMockFreights();
        System.out.println("Selected " + freights.size() + " pending freights for this route.");

        System.out.println("\nCalculating logistics manifest...");

        try {
            // 3. Chamar o Serviço
            RoutePlan plan = plannerService.planRoute(path, freights);

            // 4. Imprimir o resultado bonito
            printer.print(plan);

        } catch (Exception e) {
            System.err.println("Error calculating plan: " + e.getMessage());
        }
    }

    // --- MÉTODOS AUXILIARES PARA DADOS DE TESTE (MOCK) ---
    // Isto serve para conseguires testar já, mesmo que a BD não tenha dados perfeitos

    private List<Station> getMockPath() {
        // Tenta buscar estações reais à BD se existirem, senão cria novas
        try {
            Station s1 = repository.getStation(10); // Lisboa
            Station s2 = repository.getStation(25); // Entroncamento
            Station s3 = repository.getStation(40); // Porto

            if(s1 != null && s2 != null && s3 != null) {
                return Arrays.asList(s1, s2, s3);
            }
        } catch (Exception e) {
            // Fallback se a BD falhar
        }

        return Arrays.asList(
                new Station(10, "Lisboa Santa Apolónia"),
                new Station(25, "Entroncamento"),
                new Station(40, "Porto Campanhã")
        );
    }

    private List<Freight> getMockFreights() {
        List<Freight> list = new ArrayList<>();

        // Carga 1: Lisboa -> Porto
        Freight f1 = new Freight(501, LocalDate.now(),
                10, "Lisboa Santa Apolónia",
                40, "Porto Campanhã",
                Arrays.asList("W01", "W02"));

        // Carga 2: Entroncamento -> Porto
        Freight f2 = new Freight(502, LocalDate.now(),
                25, "Entroncamento",
                40, "Porto Campanhã",
                Arrays.asList("W03"));

        list.add(f1);
        list.add(f2);

        return list;
    }
}