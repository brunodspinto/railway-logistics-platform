package org.example.ui;

import org.example.repository.IRouteRepository;
import org.example.service.RollingStockItem;
import org.example.service.TrainAssemblyService;

import java.util.ArrayList;
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
        System.out.println("Montagem e Atribuição de Comboios");

        // 1. Selecionar Train (simplificado - podes expandir)
        System.out.print("\nTrain ID: ");
        int trainId = readInt();

        if (trainId <= 0) {
            System.out.println("(!) Train ID inválido.");
            return;
        }

        // 2. Selecionar estação de partida (para calcular distâncias)
        System.out.print("Station ID (route start): ");
        int startStationId = readInt();

        if (startStationId <= 0) {
            System.out.println("(!) Station ID inválido.");
            return;
        }

        // 3. Listar Locomotives disponíveis
        System.out.println("\n--- LOCOMOTIVES DISPONÍVEIS ---");
        List<RollingStockItem> locos = service.getAvailableLocomotives(startStationId);

        if (locos.isEmpty()) {
            System.out.println("(!) Nenhuma locomotive disponível.");
            return;
        }

        printer.printRollingStockList(locos, "LOCOMOTIVES");

        // 4. Selecionar Locomotives
        List<Integer> selectedLocoIds = selectItems("locomotives");

        if (selectedLocoIds.isEmpty()) {
            System.out.println("(!) Pelo menos 1 locomotive é necessária.");
            return;
        }

        // 5. Listar Wagons disponíveis
        System.out.println("\n--- WAGONS DISPONÍVEIS ---");
        List<RollingStockItem> wagons = service.getAvailableWagons(startStationId);

        if (wagons.isEmpty()) {
            System.out.println("(!) Nenhum wagon disponível.");
            return;
        }

        printer.printRollingStockList(wagons, "WAGONS");

        // 6. Selecionar Wagons
        List<Integer> selectedWagonIds = selectItems("wagons");

        if (selectedWagonIds.isEmpty()) {
            System.out.println("(!) Pelo menos 1 wagon é necessário.");
            return;
        }

        // 7. Confirmar e associar
        System.out.println("\n--- RESUMO ---");
        System.out.println("Train ID: " + trainId);
        System.out.println("Locomotives: " + selectedLocoIds.size());
        System.out.println("Wagons: " + selectedWagonIds.size());
        System.out.print("\nConfirmar associação? (S/N): ");

        String confirm = scanner.next();
        if (!confirm.equalsIgnoreCase("S")) {
            System.out.println("(!) Operação cancelada.");
            return;
        }

        // 8. Executar associação
        boolean success = service.assignRollingStock(trainId, selectedLocoIds, selectedWagonIds);

        if (success) {
            System.out.println("\n✓ Train montado com sucesso!");
            printer.printAssemblyConfirmation(trainId, selectedLocoIds.size(),
                    selectedWagonIds.size());
        } else {
            System.out.println("\n(!) Erro ao montar train.");
        }
    }

    private List<Integer> selectItems(String itemType) {
        List<Integer> ids = new ArrayList<>();
        System.out.println("\nSelecione " + itemType + " (IDs separados por espaço, 0 para terminar):");
        System.out.print("> ");

        scanner.nextLine(); // Consumir newline anterior
        String input = scanner.nextLine();

        String[] parts = input.split("\\s+");
        for (String part : parts) {
            try {
                int id = Integer.parseInt(part);
                if (id == 0) break;
                if (id > 0) ids.add(id);
            } catch (NumberFormatException e) {
                System.out.println("(!) ID inválido ignorado: " + part);
            }
        }

        return ids;
    }

    private int readInt() {
        try {
            int i = scanner.nextInt();
            return i;
        } catch (Exception e) {
            scanner.nextLine();
            return -1;
        }
    }
}
