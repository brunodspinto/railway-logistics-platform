package org.example.domain;

import java.util.List;
import java.util.Map;

public class PackingResult {
    private final List<Trolley> trolleys;
    private final List<String> logs;

    public PackingResult(List<Trolley> trolleys, List<String> logs) {
        this.trolleys = trolleys;
        this.logs = logs;
    }

    public List<Trolley> getTrolleys() { return trolleys; }
    public List<String> getLogs() { return logs; }

    public void printSummary(Map<String, Item> itemMap) {
        System.out.printf("Total trolleys: %d%n", trolleys.size());
        System.out.println();

        for (int i = 0; i < trolleys.size(); i++) {
            Trolley t = trolleys.get(i);
            double used = t.usedCapacity;
            double cap = t.capacity;
            int utilPct = t.utilization();
            System.out.printf("Trolley %d: Utilisation: %d%% (%.2f/%.2f kg)%n", i + 1, utilPct, used, cap);

            System.out.println(" Picking plan:");
            System.out.println("  orderId | lineNo | aisle | bay | boxId | sku | qty");
            for (OrderLine ol : t.lines) {
                // location details are not available in OrderLine; show N\/A
                System.out.printf("  %s | %d | %s | %s | %s | %s | %d%n",
                        ol.getOrderId(),
                        ol.getLineNo(),
                        "N/A",
                        "N/A",
                        "N/A",
                        ol.getSku(),
                        ol.getRequestedQty()
                );
            }
            System.out.println();
        }

        if (!logs.isEmpty()) {
            System.out.println("Events:");
            logs.forEach(System.out::println);
        }
    }

    @Override
    public String toString() {
        return String.format("PackingResult[trolleys=%d, logs=%d]", trolleys.size(), logs.size());
    }
}
