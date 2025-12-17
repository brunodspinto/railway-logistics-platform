package org.example.ui;

import org.example.domain.Freight;
import org.example.domain.Station;
import org.example.repository.IRouteRepository;
import org.example.service.RoutePlan;
import org.example.service.RoutePlannerService;

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
        System.out.println("\n--- ROUTE PLANNER (USLP08 - DATABASE MODE) ---");

        List<Station> path = selectPathStrategy();
        if (path == null || path.isEmpty()) return;

        System.out.println("\nRota Selecionada: " + path.get(0).getName() + " -> " + path.get(path.size()-1).getName());

        System.out.println("\n[BD] A consultar cargas pendentes na tabela FREIGHTS...");
        List<Freight> allFreights = repository.getAllPendingFreights();
        List<Freight> filterFreights = new ArrayList<>();

        if (allFreights != null) {
            for (Freight f : allFreights) {
                int originIndex = -1, destIndex = -1;
                for (int i = 0; i < path.size(); i++) {
                    if (path.get(i).getId() == f.getOriginId()) originIndex = i;
                    if (path.get(i).getId() == f.getDestinationId()) destIndex = i;
                }
                // Validar direção: Origem antes do Destino
                if (originIndex != -1 && destIndex != -1 && originIndex < destIndex) {
                    filterFreights.add(f);
                }
            }
        }

        if (filterFreights.isEmpty()) {
            System.out.println("⚠ Nenhuma carga encontrada para esta rota (Verifique se o Repositório está a ler a BD corretamente).");
            return;
        }

        System.out.println("[Sistema] SUCESSO! Encontradas " + filterFreights.size() + " cargas compatíveis.");
        System.out.println("\nA gerar manifesto...");

        try {
            RoutePlan plan = plannerService.planRoute(path, filterFreights);
            printer.print(plan);
        } catch (Exception e) {
            System.err.println("Erro ao calcular: " + e.getMessage());
        }
    }

    private List<Station> selectPathStrategy() {
        System.out.println("\nSelecione uma Rota Válida:");

        // Carga 2001 e 2003 (Leixões -> Darque/Valença)
        System.out.println("1. Rota Longa Norte (Leixões -> Contumil -> Nine -> Darque -> Valença)");

        // Carga 2005 (Nine -> Valença)
        System.out.println("2. Rota Curta Minho (Nine -> Darque -> Valença)");

        // Carga 2007 (Leixões -> Campanhã)
        System.out.println("3. Conexão Porto (Leixões -> Contumil -> Porto Campanhã)");

        System.out.println("4. Inserir IDs Manualmente");
        System.out.println("0. Sair");
        System.out.print("Opção: ");

        int option = readInt();

        switch (option) {
            case 1:
                // Leixões(50) -> Contumil(13) -> Nine(20) -> Darque(12) -> Valença(11)
                return fetchStations(50, 13, 20, 12, 11);
            case 2:
                // Nine(20) -> Darque(12) -> Valença(11)
                return fetchStations(20, 12, 11);
            case 3:
                // Leixões(50) -> Contumil(13) -> Campanhã(5)
                return fetchStations(50, 13, 5);
            case 4:
                return readUserPathManual();
            case 0: default: return null;
        }
    }

    private List<Station> fetchStations(int... ids) {
        List<Station> path = new ArrayList<>();
        for (int id : ids) {
            Station s = repository.getStation(id);
            if (s != null) path.add(s);
            else System.out.println("Erro: Estação ID " + id + " não encontrada na BD.");
        }
        return path;
    }

    private List<Station> readUserPathManual() {
        // ... (igual ao anterior)
        return new ArrayList<>();
    }

    private int readInt() {
        try { int i = scanner.nextInt(); scanner.nextLine(); return i; }
        catch (Exception e) { scanner.nextLine(); return -1; }
    }
}