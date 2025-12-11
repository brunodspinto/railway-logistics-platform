package org.example.usei12.ui;

import org.example.usei12.controllers.ComputeBackboneController;

public class USEI12Main {

    public static void main(String[] args) {

        System.out.println("=====================================");
        System.out.println(" USEI12 - Minimal Backbone Network  ");
        System.out.println("=====================================\n");

        ComputeBackboneController controller = new ComputeBackboneController();

        USEI12Menu menu = new USEI12Menu(controller);
        menu.start();
    }
}