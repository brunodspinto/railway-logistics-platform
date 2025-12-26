package org.example.usei14.ui;

import org.example.controller.ComputeMaxFlowController;

public class USEI14Main {

    public static void main(String[] args) {

        System.out.println("=========================================");
        System.out.println(" USEI14 - Maximum Throughput (Max Flow) ");
        System.out.println("=========================================\n");

        ComputeMaxFlowController controller = new ComputeMaxFlowController();
        USEI14Menu menu = new USEI14Menu(controller);

        menu.start();
    }
}