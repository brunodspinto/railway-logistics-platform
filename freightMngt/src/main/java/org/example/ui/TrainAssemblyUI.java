package org.example.ui;

import org.example.domain.Station;
import org.example.domain.Train;
import org.example.repository.IRouteRepository;
import org.example.service.RollingStockItem;
import org.example.service.TrainAssemblyService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;

public class TrainAssemblyUI {

    private final IRouteRepository repository;
    private final TrainAssemblyService service;
    private final AssemblyManifestPrinter printer;
    private final Scanner scanner;

    public TrainAssemblyUI(IRouteRepository repository) {
        this.repository = repository;
        this.service = new TrainAssemblyService(repository);
        this.printer = new AssemblyManifestPrinter();
        this.scanner = new Scanner(System.in);
    }

    public void run() {
        System.out.println("\n--- TRAIN ASSEMBLY (USLP09) ---");
        System.out.println("Montagem e Atribuição de Comboios a Rotas");

        // 1. LISTAR TRAINS DISPONÍVEIS
        System.out.println("\n--- TRAINS DISPONÍVEIS ---");
        Collection<Train> allTrains = repository.getAllTrains();

        if (allTrains.isEmpty()) {
            System.out.println("(!) Nenhum train disponível na base de dados.");
            return;
        }

        List<Train> trainList = new ArrayList<>(allTrains);
        printer.printTrainList(trainList);

        // 2. SELECIONAR TRAIN POR ÍNDICE
        System.out.print("\nSelecione Train [índice 1-" + trainList.size() + "]: ");
        int trainIndex = readInt();

        if (trainIndex < 1 || trainIndex > trainList.size()) {
            System.out.println("(!) Índice inválido.");
            return;
        }

        Train selectedTrain = trainList.get(trainIndex - 1);

        // 3. MOSTRAR DETALHES DO TRAIN (INCLUINDO ROUTE)
        System.out.println("\n" + "═".repeat(70));
        System.out.println("  TRAIN SELECIONADO");
        System.out.println("═".repeat(70));
        System.out.println("  Train ID:   " + selectedTrain.getId());
        System.out.println("  Operator:   " + selectedTrain.getOperator());
        System.out.println("  Date:       " + selectedTrain.getDate());
        System.out.println("  Time:       " + selectedTrain.getTime());

        // DETERMINAR START STATION
        int startStationId;

        // MOSTRAR ROUTE (se existir)
        if (selectedTrain.getPath() != null && !selectedTrain.getPath().isEmpty()) {
            System.out.println("\n  Route:");
            List<Integer> path = selectedTrain.getPath();

            System.out.print("    ");
            for (int i = 0; i < path.size(); i++) {
                int stationId = path.get(i);

                try {
                    Station station = repository.getStation(stationId);
                    System.out.print(station.getName());

                    if (i < path.size() - 1) {
                        System.out.print(" → ");
                    }
                } catch (Exception e) {
                    System.out.print("Station #" + stationId);
                    if (i < path.size() - 1) {
                        System.out.print(" → ");
                    }
                }
            }
            System.out.println();

            // Usar primeiro station da route
            startStationId = path.get(0);

            try {
                Station startStation = repository.getStation(startStationId);
                System.out.println("\n  Start Station: " + startStation.getName() + " (ID: " + startStationId + ")");
            } catch (Exception e) {
                System.out.println("\n  Start Station ID: " + startStationId);
            }

        } else {
            System.out.println("\n  Route: Not defined yet");
            System.out.print("\n  Enter Start Station ID manually: ");
            startStationId = readInt();

            if (startStationId <= 0) {
                System.out.println("(!) Station ID inválido.");
                return;
            }

            try {
                Station station = repository.getStation(startStationId);
                System.out.println("  Start Station: " + station.getName());
            } catch (Exception e) {
                System.out.println("  Start Station ID: " + startStationId);
            }
        }

        System.out.println("═".repeat(70));

        // 4. LISTAR E SELECIONAR LOCOMOTIVES
        System.out.println("\n--- LOCOMOTIVES DISPONÍVEIS ---");
        System.out.println("(Ordenadas por distância da estação de partida)");

        List<RollingStockItem> locos = service.getAvailableLocomotives(startStationId);

        if (locos.isEmpty()) {
            System.out.println("(!) Nenhuma locomotive disponível.");
            return;
        }

        printer.printRollingStockList(locos, "LOCOMOTIVES");

        List<Integer> selectedLocoIds = selectItemsByIndex(locos, "locomotives");

        if (selectedLocoIds.isEmpty()) {
            System.out.println("(!) Pelo menos 1 locomotive é necessária.");
            return;
        }

        // 5. LISTAR E SELECIONAR WAGONS
        System.out.println("\n--- WAGONS DISPONÍVEIS ---");
        System.out.println("(Ordenados por distância da estação de partida)");

        List<RollingStockItem> wagons = service.getAvailableWagons(startStationId);

        if (wagons.isEmpty()) {
            System.out.println("(!) Nenhum wagon disponível.");
            return;
        }

        printer.printRollingStockList(wagons, "WAGONS");

        List<Integer> selectedWagonIds = selectItemsByIndex(wagons, "wagons");

        if (selectedWagonIds.isEmpty()) {
            System.out.println("(!) Pelo menos 1 wagon é necessário.");
            return;
        }

        // 6. RESUMO E CONFIRMAÇÃO
        System.out.println("\n" + "═".repeat(60));
        System.out.println("  RESUMO DA MONTAGEM");
        System.out.println("═".repeat(60));
        System.out.println("  Train ID:      " + selectedTrain.getId());
        System.out.println("  Operator:      " + selectedTrain.getOperator());
        System.out.println("  Date:          " + selectedTrain.getDate());
        System.out.println("  Time:          " + selectedTrain.getTime());
        System.out.println("  Locomotives:   " + selectedLocoIds.size() + " units");
        System.out.println("  Wagons:        " + selectedWagonIds.size() + " units");
        System.out.println("═".repeat(60));

        System.out.print("\nConfirmar associação? (S/N): ");
        String confirm = scanner.next();

        if (!confirm.equalsIgnoreCase("S")) {
            System.out.println("(!) Operação cancelada.");
            return;
        }

        // 7. EXECUTAR ASSOCIAÇÃO
        boolean success = service.assignRollingStock(
                selectedTrain.getId(),
                selectedLocoIds,
                selectedWagonIds
        );

        if (success) {
            System.out.println("\n✓ Train montado com sucesso!");
            printer.printAssemblyConfirmation(
                    selectedTrain.getId(),
                    selectedLocoIds.size(),
                    selectedWagonIds.size()
            );
        } else {
            System.out.println("\n(!) Erro ao montar train.");
        }
    }

    /**
     * Seleciona items por ÍNDICE [1, 2, 3...] em vez de ID direto
     */
    private List<Integer> selectItemsByIndex(List<RollingStockItem> items, String itemType) {
        List<Integer> selectedIds = new ArrayList<>();

        System.out.println("\nSelecione " + itemType + " pelos ÍNDICES mostrados [1-" + items.size() + "]");
        System.out.println("Digite os índices separados por espaço (ex: 1 3 5):");
        System.out.print("> ");

        String input = scanner.nextLine().trim();

        if (input.isEmpty()) {
            System.out.println("(!) Nenhum input fornecido.");
            return selectedIds;
        }

        String[] parts = input.split("\\s+");
        for (String part : parts) {
            try {
                int index = Integer.parseInt(part);

                if (index == 0) break;

                if (index >= 1 && index <= items.size()) {
                    int realId = items.get(index - 1).getId();
                    selectedIds.add(realId);
                    System.out.println("  ✓ [" + index + "] " + items.get(index - 1).getDescription());
                } else {
                    System.out.println("  (!) Índice " + index + " fora do intervalo.");
                }
            } catch (NumberFormatException e) {
                System.out.println("  (!) Input inválido: '" + part + "'");
            }
        }

        return selectedIds;
    }

    private int readInt() {
        try {
            int i = scanner.nextInt();
            scanner.nextLine(); // ← Consumir newline pendente
            return i;
        } catch (Exception e) {
            scanner.nextLine();
            return -1;
        }
    }
}