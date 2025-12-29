package org.example.ui;

import org.example.domain.Train;
import org.example.service.RollingStockItem;

import java.util.List;

/**
 * Printer para USLP09 (similar ao RouteManifestPrinter).
 */
public class AssemblyManifestPrinter {

    private static final String RESET = "\033[0m";
    private static final String BOLD = "\033[1m";
    private static final String GREEN = "\033[1;32m";
    private static final String YELLOW = "\033[1;33m";
    private static final String BLUE = "\033[1;34m";
    private static final String GREY = "\033[0;90m";

    /**
     * Lista trains disponíveis com índices para seleção
     */
    public void printTrainList(List<Train> trains) {
        System.out.println(BOLD + "\n=== TRAINS DISPONÍVEIS ===" + RESET);
        System.out.printf("%-6s %-10s %-15s %-12s %-8s\n",
                "[Idx]", "Train ID", "Operator", "Date", "Time");
        System.out.println("─".repeat(60));

        for (int i = 0; i < trains.size(); i++) {
            Train t = trains.get(i);

            System.out.printf("%s[%d]%s   %-10d %-15s %-12s %-8s\n",
                    GREEN, (i + 1), RESET,
                    t.getId(),
                    truncate(t.getOperator(), 15),
                    t.getDate().toString(),
                    t.getTime().toString());
        }

        System.out.println("─".repeat(60));
    }

    /**
     * Lista rolling stock com ÍNDICES para seleção
     */
    public void printRollingStockList(List<RollingStockItem> items, String title) {
        System.out.println(BOLD + "\n=== " + title + " ===" + RESET);
        System.out.printf("%-6s %-8s %-40s %-12s %-30s %-10s\n",
                "[Idx]", "ID", "Description", "Status", "Location", "Distance");
        System.out.println("─".repeat(110));

        for (int i = 0; i < items.size(); i++) {
            RollingStockItem item = items.get(i);
            String statusColor = item.isParked() ? GREEN : YELLOW;
            String statusText = item.isParked() ? "PARKED" : "IN_TRANSIT";

            System.out.printf("%s[%d]%s   %-8d %-40s %s%-12s%s %-30s %-10s\n",
                    BLUE, (i + 1), RESET,
                    item.getId(),
                    truncate(item.getDescription(), 40),
                    statusColor, statusText, RESET,
                    truncate(item.getLocation(), 30),
                    item.isParked() ? item.getDistanceFromStart() + " km" : "N/A");
        }

        System.out.println("─".repeat(110));
    }

    public void printAssemblyConfirmation(int trainId, int locoCount, int wagonCount) {
        System.out.println(BOLD + "\n╔════════════════════════════════════════╗" + RESET);
        System.out.println(BOLD + "║    TRAIN ASSEMBLY CONFIRMATION        ║" + RESET);
        System.out.println(BOLD + "╚════════════════════════════════════════╝" + RESET);
        System.out.printf("\n%sTrain ID:%s       %d\n", BLUE, RESET, trainId);
        System.out.printf("%sLocomotives:%s   %d\n", GREEN, RESET, locoCount);
        System.out.printf("%sWagons:%s        %d\n", GREEN, RESET, wagonCount);
        System.out.println("\n" + GREY + "Rolling stock successfully assigned to train." + RESET);
    }

    private String truncate(String str, int maxLength) {
        if (str == null) return "";
        return str.length() > maxLength ? str.substring(0, maxLength - 3) + "..." : str;
    }
}