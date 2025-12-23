package org.example.ui;

import org.example.domain.RollingStockStatus;
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

    public void printRollingStockList(List<RollingStockItem> items, String title) {
        System.out.println(BOLD + "\n=== " + title + " ===" + RESET);
        System.out.printf("%-5s %-40s %-12s %-30s %-10s\n",
                "ID", "Description", "Status", "Location", "Distance");
        System.out.println("─".repeat(100));

        for (RollingStockItem item : items) {
            String statusColor = item.isParked() ? GREEN : YELLOW;
            String statusText = item.isParked() ? "PARKED" : "IN_TRANSIT";

            System.out.printf("%-5d %-40s %s%-12s%s %-30s %-10s\n",
                    item.getId(),
                    truncate(item.getDescription(), 40),
                    statusColor, statusText, RESET,
                    truncate(item.getLocation(), 30),
                    item.isParked() ? item.getDistanceFromStart() + " km" : "N/A");
        }

        System.out.println("─".repeat(100));
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
