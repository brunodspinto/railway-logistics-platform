package org.example;

import org.example.demo.USEI11Demo;
import org.example.usei12.ui.USEI12Main;
import org.example.usei13.ui.USEI13Main;
import org.example.usei14.ui.USEI14Main;
import org.example.usei15.ui.USEI15Main;

import java.util.Scanner;

/**
 * Menu principal do projeto - Acesso a todas as User Stories
 * Sprint 3 - Railway Network Optimization
 */
public class MainMenu {

    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        boolean exit = false;

        while (!exit) {
            displayMenu();
            int choice = getChoice();

            switch (choice) {
                case 1:
                    runUSEI11();
                    break;
                case 2:
                    runUSEI12();
                    break;
                case 3:
                    runUSEI13();
                    break;
                case 4:
                    runUSEI14();
                    break;
                case 5:
                    runUSEI15();
                    break;
                case 0:
                    exit = true;
                    System.out.println("\nA sair do programa...");
                    System.out.println("Obrigado por usar o sistema!");
                    break;
                default:
                    System.out.println("\nOpcao invalida! Tente novamente.");
            }

            if (!exit) {
                waitForEnter();
            }
        }

        scanner.close();
    }

    private static void displayMenu() {
        System.out.println("\n" + "=".repeat(70));
        System.out.println("            LOGISTICS ON RAILS - SPRINT 3");
        System.out.println("         Railway Network Optimization System");
        System.out.println("=".repeat(70));
        System.out.println();
        System.out.println("  1. USEI11 - Directed Line Upgrade Plan");
        System.out.println("  2. USEI12 - Minimal Backbone Network");
        System.out.println("  3. USEI13 - Rail Hub Centrality Analysis");
        System.out.println("  4. USEI14 - Maximum Throughput Between Hubs");
        System.out.println("  5. USEI15 - Risk-Aware Shortest Paths");
        System.out.println();
        System.out.println("  0. Sair");
        System.out.println();
        System.out.println("=".repeat(70));
    }

    private static int getChoice() {
        System.out.print("Escolha uma opcao: ");
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private static void runUSEI11() {
        System.out.println("\n" + "=".repeat(70));
        System.out.println("Executando USEI11 - Directed Line Upgrade Plan");
        System.out.println("=".repeat(70));

        try {
            USEI11Demo.main(new String[]{});
        } catch (Exception e) {
            System.err.println("Erro ao executar USEI11: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void runUSEI12() {
        System.out.println("\n" + "=".repeat(70));
        System.out.println("Executando USEI12 - Minimal Backbone Network");
        System.out.println("=".repeat(70));

        try {
            USEI12Main.main(new String[]{});
        } catch (Exception e) {
            System.err.println("Erro ao executar USEI12: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void runUSEI13() {
        System.out.println("\n" + "=".repeat(70));
        System.out.println("Executando USEI13 - Rail Hub Centrality Analysis");
        System.out.println("=".repeat(70));

        try {
            USEI13Main.main(new String[]{});
        } catch (Exception e) {
            System.err.println("Erro ao executar USEI13: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void runUSEI14() {
        System.out.println("\n" + "=".repeat(70));
        System.out.println("Executando USEI14 - Maximum Throughput Between Hubs");
        System.out.println("=".repeat(70));

        try {
            USEI14Main.main(new String[]{});
        } catch (Exception e) {
            System.err.println("Erro ao executar USEI14: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void runUSEI15() {
        System.out.println("\n" + "=".repeat(70));
        System.out.println("Executando USEI15 - Risk-Aware Shortest Paths");
        System.out.println("=".repeat(70));

        try {
            USEI15Main.main(new String[]{});
        } catch (Exception e) {
            System.err.println("Erro ao executar USEI15: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void waitForEnter() {
        System.out.println("\n" + "-".repeat(70));
        System.out.print("Pressione ENTER para voltar ao menu principal...");
        scanner.nextLine();
    }
}
