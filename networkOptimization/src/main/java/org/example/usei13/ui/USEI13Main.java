package org.example.usei13.ui;

import org.example.usei13.controllers.ComputeCentralityController;

public class USEI13Main {

    public static void main(String[] args) {

        System.out.println("=========================================");
        System.out.println(" USEI13 - Rail Hub Centrality Analysis ");
        System.out.println("=========================================\n");

        ComputeCentralityController controller = new ComputeCentralityController();

        USEI13Menu menu = new USEI13Menu(controller);
        menu.start();
    }
}