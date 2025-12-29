package org.example.usei15.ui;

import org.example.usei15.controller.USEI15Controller;
import org.example.domain.Connection;
import org.example.domain.Station;
import org.example.graph.Graph;

import java.util.Scanner;

public class USEI15Menu {

    private final Graph<Station, Connection> graph;
    private final USEI15Controller controller;
    private final Scanner scanner;

    public USEI15Menu(Graph<Station, Connection> graph, USEI15Controller controller) {
        this.graph = graph;
        this.controller = controller;
        this.scanner = new Scanner(System.in);
    }

    public void start() {

        while (true) {
            System.out.println("\n--- USEI15 Menu ---");
            System.out.println("1 - Compute risk-aware shortest path");
            System.out.println("0 - Exit");
            System.out.print("Option: ");

            String option = scanner.nextLine().trim();

            switch (option) {
                case "1":
                    handleShortestPath();
                    break;
                case "0":
                    System.out.println("Exiting USEI15.");
                    return;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    private void handleShortestPath() {

        System.out.print("Enter source station ID: ");
        String sourceId = scanner.nextLine().trim();

        System.out.print("Enter target station ID: ");
        String targetId = scanner.nextLine().trim();

        Station source = graph.vertex(s -> s.getId().equals(sourceId));
        Station target = graph.vertex(s -> s.getId().equals(targetId));

        if (source == null) {
            System.out.println("Source station not found.");
            return;
        }

        if (target == null) {
            System.out.println("Target station not found.");
            return;
        }

        controller.execute(source, target);
    }
}
