package org.example.usei13.ui;

import org.example.usei13.controllers.ComputeCentralityController;

import java.nio.file.Path;
import java.util.Scanner;

public class USEI13Menu {

    private static final String DEFAULT_CSV = "res/station_to_station.csv";

    private final ComputeCentralityController controller;

    public USEI13Menu(ComputeCentralityController controller) {
        this.controller = controller;
    }

    public void start() {

        try (Scanner in = new Scanner(System.in)) {

            System.out.print("Caminho do ficheiro CSV [Default: " + DEFAULT_CSV + "]: \n");

            String csvInput = in.nextLine().trim();
            if (csvInput.isEmpty()) {
                csvInput = DEFAULT_CSV;
            }

            Path csvPath = Path.of(csvInput);
            controller.compute(csvPath);
        }
    }
}