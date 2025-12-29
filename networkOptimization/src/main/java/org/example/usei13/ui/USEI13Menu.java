package org.example.usei13.ui;

import org.example.usei13.controllers.ComputeCentralityController;

import java.nio.file.Path;
import java.util.Scanner;

public class USEI13Menu {

    private static final String DEFAULT_STATIONS_CSV = "res/stations.csv";
    private static final String DEFAULT_LINES_CSV    = "res/lines.csv";

    private final ComputeCentralityController controller;

    public USEI13Menu(ComputeCentralityController controller) {
        this.controller = controller;
    }

    public void start() {

        try (Scanner in = new Scanner(System.in)) {

            System.out.print("Caminho do ficheiro stations.csv [Default: " + DEFAULT_STATIONS_CSV + "]: ");
            String stationsInput = in.nextLine().trim();
            if (stationsInput.isEmpty()) {
                stationsInput = DEFAULT_STATIONS_CSV;
            }

            System.out.print("Caminho do ficheiro lines.csv [Default: " + DEFAULT_LINES_CSV + "]:\n");
            String linesInput = in.nextLine().trim();
            if (linesInput.isEmpty()) {
                linesInput = DEFAULT_LINES_CSV;
            }

            Path stationsPath = Path.of(stationsInput);
            Path linesPath    = Path.of(linesInput);

            controller.compute(stationsPath, linesPath);
        }
    }
}