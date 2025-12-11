package org.example.demo;

import org.example.controller.UpgradePlanController;
import org.example.controller.UpgradePlanResult;
import org.example.domain.Station;

import java.io.IOException;
import java.util.List;

/**
 * Demonstração da USEI11 - Directed Line Upgrade Plan
 */
public class USEI11Demo {

    public static void main(String[] args) {
        try {
            // 1. Criar controller
            UpgradePlanController controller = new UpgradePlanController();

            // 2. Carregar rede belga
            String filePath = "src/main/java/pt/ipp/isep/dei/data/station_to_station.csv";
            controller.loadNetwork(filePath);

            // 3. Calcular ordem de upgrades
            System.out.println("\n" + "=".repeat(70));
            System.out.println("USEI11 - DIRECTED LINE UPGRADE PLAN");
            System.out.println("=".repeat(70));

            UpgradePlanResult result = controller.calculateUpgradeOrder();

            // 4. Apresentar resultados
            System.out.println("\n" + "=".repeat(70));
            System.out.println("RESULTS");
            System.out.println("=".repeat(70));

            System.out.printf("Network size: %d stations, %d connections%n",
                    result.getNumStations(),
                    result.getNumConnections());

            if (result.hasCycles()) {
                // CASO 1: Grafo tem ciclos
                System.out.println("\n❌ GRAPH HAS CYCLES - Cannot determine upgrade order!");
                System.out.println("\nCycles detected: " + result.getCycles().size());

                for (int i = 0; i < result.getCycles().size(); i++) {
                    List<Station> cycle = result.getCycles().get(i);
                    System.out.println("\nCycle #" + (i + 1) + ":");
                    System.out.print("  ");
                    for (int j = 0; j < cycle.size(); j++) {
                        System.out.print(cycle.get(j).getName());
                        if (j < cycle.size() - 1) {
                            System.out.print(" → ");
                        }
                    }
                    System.out.println();
                }

            } else {
                // CASO 2: Ordem válida encontrada
                System.out.println("\n✅ VALID UPGRADE ORDER FOUND!");
                System.out.println("\nUpgrade sequence (first 20 stations):");

                List<Station> order = result.getUpgradeOrder();
                int showLimit = Math.min(20, order.size());

                for (int i = 0; i < showLimit; i++) {
                    System.out.printf("%3d. %s%n", i + 1, order.get(i));
                }

                if (order.size() > showLimit) {
                    System.out.printf("\n... and %d more stations%n",
                            order.size() - showLimit);
                }
            }

            System.out.println("\n" + "-".repeat(70));
            System.out.printf("Execution time: %d ms%n", result.getExecutionTimeMs());
            System.out.printf("Complexity: %s%n", result.getComplexity());
            System.out.println("=".repeat(70));

        } catch (IOException e) {
            System.err.println("Error loading network: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

