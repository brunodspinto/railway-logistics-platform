package org.example.usei14.ui;

import org.example.usei14.controllers.ComputeMaxFlowController;

/**
 * Ponto de entrada principal para a execução da USEI14 (Cálculo do Fluxo Máximo).
 * Esta classe é responsável por instanciar as dependências necessárias (controlador)
 * e iniciar o menu de interação com o utilizador.
 */
public class USEI14Main {

    /**
     * Metodo principal que arranca a aplicação para este user storie específico.
     *
     * @param args Argumentos da linha de comandos (não utilizados nesta implementação).
     */
    public static void main(String[] args) {

        System.out.println("=========================================");
        System.out.println(" USEI14 - Maximum Throughput (Max Flow) ");
        System.out.println("=========================================\n");

        // Inicializar o controlador responsável pela lógica de negócio
        ComputeMaxFlowController controller = new ComputeMaxFlowController();

        // Inicializar o menu injetando o controlador e iniciar a interação
        USEI14Menu menu = new USEI14Menu(controller);

        menu.start();
    }
}