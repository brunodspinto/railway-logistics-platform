package org.example.ui.executors;

import org.example.domain.Record;
import org.example.service.PathSequencingService;

import java.util.ArrayList;
import java.util.List;

public class USEI04Executor implements Runnable {

    private final PathSequencingService pathSequencingService;

    public USEI04Executor() {
        this.pathSequencingService = new PathSequencingService();
    }

    @Override
    public void run() {
        System.out.println("\n### Executing USEI04: Pick Path Sequencing ###");

        // Lista fictícia usando a nova classe 'Record'
        List<Record> mockPickingList = new ArrayList<>();
        mockPickingList.add(new Record(1, 8));
        mockPickingList.add(new Record(2, 2));
        mockPickingList.add(new Record(3, 4));
        mockPickingList.add(new Record(1, 8)); // Duplicado para teste

        System.out.println("\nBays to visit (with duplicates): " + mockPickingList);
        System.out.println("--------------------------------------------------");

        PathSequencingService.PickPathResult resultA = pathSequencingService.sequenceByStrategyA(mockPickingList);
        printResult(resultA);

        PathSequencingService.PickPathResult resultB = pathSequencingService.sequenceByStrategyB(mockPickingList);
        printResult(resultB);
    }

    private void printResult(PathSequencingService.PickPathResult result) {
        System.out.println("Strategy: " + result.strategyName);
        System.out.println("  -> Path: " + result.path);
        System.out.println("  -> Total Distance: " + result.totalDistance);
        System.out.println("--------------------------------------------------");
    }
}
