package org.example.usei14.ui;

import org.example.domain.Station;
import org.example.usei14.controllers.ComputeMaxFlowController;

import java.util.List;
import java.util.Scanner;

/**
 * Interface de utilizador (UI) para a USEI14 - Cálculo de Fluxo Máximo.
 * Responsável por interagir com o utilizador, solicitar dados e apresentar os resultados.
 */
public class USEI14Menu {

    // Caminhos predefinidos para os ficheiros de dados
    private static final String DEFAULT_STATIONS_CSV = "res/stations.csv";
    private static final String DEFAULT_LINES_CSV    = "res/lines.csv";

    private final ComputeMaxFlowController controller;
    private final Scanner in;

    /**
     * Construtor do menu.
     *
     * @param controller O controlador responsável pela lógica de negócio do fluxo máximo.
     */
    public USEI14Menu(ComputeMaxFlowController controller) {
        this.controller = controller;
        this.in = new Scanner(System.in);
    }

    /**
     * Inicia o ciclo de vida do menu.
     * Carrega a rede ferroviária automaticamente e apresenta as opções ao utilizador.
     */
    public void start() {
        try {
            // Carrega a rede imediatamente sem perguntar ao utilizador
            System.out.println("Loading railway network...");
            controller.loadNetwork(DEFAULT_STATIONS_CSV, DEFAULT_LINES_CSV);
            System.out.println("Data loaded successfully!");

            // Ciclo do Menu Principal
            while (true) {
                System.out.println("\n========================================");
                System.out.println("       MAIN MENU - MAXIMUM FLOW         ");
                System.out.println("========================================");
                System.out.println("1. Select Stations and Calculate Flow");
                System.out.println("0. Exit");
                System.out.println("========================================");
                System.out.print("Option: ");

                String option = in.nextLine().trim();

                if (option.equals("0")) {
                    break;
                } else if (option.equals("1")) {
                    processFlowCalculation();
                } else {
                    System.out.println("Invalid option.");
                }
            }

        } catch (Exception e) {
            System.err.println("Fatal error starting application: " + e.getMessage());
            System.err.println("Please check if files exist in the 'res/' folder.");
        }
    }

    /**
     * Gere o fluxo de interação para o cálculo do fluxo máximo:
     * 1. Lista as estações.
     * 2. Pede a origem.
     * 3. Pede o destino.
     * 4. Executa o cálculo e mostra o resultado.
     */
    private void processFlowCalculation() {
        // 1. Obter a lista de estações do controlador
        List<Station> stations = controller.getStations();
        if (stations.isEmpty()) {
            System.out.println("Error: No stations loaded.");
            return;
        }

        // 2. Apresentar a lista numerada para facilitar a escolha
        printStationsNumerically(stations);

        // 3. Escolher a estação de Origem
        System.out.println("\n--- SOURCE Selection ---");
        Station source = pickStationByNumber(stations);
        if (source == null) return; // O utilizador cancelou a operação

        // 4. Escolher a estação de Destino
        System.out.println("\n--- DESTINATION Selection ---");
        Station sink = pickStationByNumber(stations);
        if (sink == null) return;

        // Validação básica: Origem não pode ser igual ao Destino
        if (source.equals(sink)) {
            System.out.println("Error: Source and Destination are the same station.");
            return;
        }

        // 5. Executar o cálculo
        try {
            System.out.printf("\nCalculating flow from [%s] to [%s]...%n", source.getName(), sink.getName());
            Double maxFlow = controller.calculateMaxFlow(source, sink);

            System.out.println("****************************************");
            System.out.printf("maxflow.summary: Source: %s, Target: %s, MaxFlow: %.0f%n", source.getId(),
                    sink.getId(), maxFlow);
            System.out.println("****************************************");

            System.out.println("\nPress Enter to return to menu...");
            in.nextLine();

        } catch (Exception e) {
            System.err.println("Calculation error: " + e.getMessage());
        }
    }

    /**
     * Imprime a lista de estações na consola com um índice numérico.
     * Formata a saída em duas colunas para melhor leitura.
     *
     * @param stations A lista de estações a imprimir.
     */
    private void printStationsNumerically(List<Station> stations) {
        System.out.println("\n--- Station List ---");
        int count = 0;
        for (int i = 0; i < stations.size(); i++) {
            // Formato: [1] Nome (ID)
            System.out.printf("[%3d] %-25s (ID: %s)  ", i + 1, stations.get(i).getName(), stations.get(i).getId());

            count++;
            if (count % 2 == 0) {
                System.out.println();
            }
        }
        if (count % 2 != 0) System.out.println();
        System.out.println("-------------------------");
    }

    /**
     * Solicita ao utilizador que insira o número correspondente a uma estação da lista.
     * Inclui validação de entrada (apenas números dentro do intervalo).
     *
     * @param stations A lista de estações disponíveis.
     * @return A estação selecionada ou {@code null} se o utilizador escolher cancelar (opção 0).
     */
    private Station pickStationByNumber(List<Station> stations) {
        while (true) {
            System.out.print("Enter station NUMBER (or '0' to cancel): ");
            String input = in.nextLine().trim();

            try {
                int index = Integer.parseInt(input);

                if (index == 0) return null; // Cancelar

                if (index > 0 && index <= stations.size()) {
                    return stations.get(index - 1);
                } else {
                    System.out.println("Invalid number. Choose between 1 and " + stations.size());
                }

            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter numbers only.");
            }
        }
    }
}