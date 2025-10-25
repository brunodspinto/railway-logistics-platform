package org.example.ui.executors;

import org.example.domain.PickingItem;
import org.example.domain.Record;
import org.example.domain.Trolley;
import org.example.service.PathSequencingService;
import org.example.ui.menu.DisplayHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class USEI04Executor {

    public static void execute(List<Trolley> pickingPlan) {
        DisplayHelper.printHeader("USEI04 - Pick Path Sequencing");

        if (pickingPlan == null || pickingPlan.isEmpty()) {
            DisplayHelper.printError("Picking plan is empty. Cannot execute USEI04.");
            return;
        }

        PathSequencingService pathSequencingService = new PathSequencingService();

        for (int i = 0; i < pickingPlan.size(); i++) {
            Trolley trolley = pickingPlan.get(i);
            System.out.printf("\n### Processing Trolley #%d ###\n", i + 1);

            List<Record> baysToVisit = new ArrayList<>();
            for (PickingItem item : trolley.getItems()) {
                baysToVisit.add(new Record(item.getAisle(), item.getBay()));
            }

            if (baysToVisit.isEmpty()) {
                System.out.println("This trolley has no items to pick. Skipping.");
                continue;
            }

            List<Record> uniqueBays = baysToVisit.stream().distinct().collect(Collectors.toList());
            System.out.println("Unique bays to visit: " + uniqueBays);
            System.out.println("--------------------------------------------------");

            PathSequencingService.PickPathResult resultA = pathSequencingService.sequenceByStrategyA(baysToVisit);
            printResult(resultA);

            PathSequencingService.PickPathResult resultB = pathSequencingService.sequenceByStrategyB(baysToVisit);
            printResult(resultB);

            System.out.println("Comparison for Trolley #" + (i + 1) + ":");
            if (resultA.totalDistance < resultB.totalDistance) {
                System.out.printf("  -> Strategy A is shorter by %.2f units.%n", (resultB.totalDistance - resultA.totalDistance));
            } else if (resultB.totalDistance < resultA.totalDistance) {
                System.out.printf("  -> Strategy B is shorter by %.2f units.%n", (resultA.totalDistance - resultB.totalDistance));
            } else {
                System.out.println("  -> Both strategies have the same total distance.");
            }
        }

        System.out.println();
        DisplayHelper.printSuccess("USEI04 completed successfully!");
    }

    private static void printResult(PathSequencingService.PickPathResult result) {
        System.out.println("Strategy: " + result.strategyName);
        System.out.println("  -> Path: " + result.path);
        System.out.println("  -> Total Distance: " + result.totalDistance);
        System.out.println("--------------------------------------------------");
    }
}
