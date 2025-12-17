package org.example.ui;

import org.example.domain.Freight;
import org.example.service.RoutePlan;
import org.example.service.RouteStop;

import java.util.List;

/**
 * Responsável por imprimir o manifesto da rota na consola.
 * Versão simplificada e limpa.
 */
public class RouteManifestPrinter {

    // Definição de cores básicas ANSI para não depender de ficheiros externos
    private static final String RESET = "\033[0m";
    private static final String BOLD = "\033[1m";
    private static final String GREEN = "\033[1;32m"; // Verde claro para cargas
    private static final String RED = "\033[1;31m";   // Vermelho claro para descargas
    private static final String BLUE = "\033[1;34m";  // Azul para estações
    private static final String GREY = "\033[0;90m";  // Cinzento para estrutura

    public void print(RoutePlan plan) {
        if (plan == null || plan.getStops().isEmpty()) {
            System.out.println("⚠  O plano de rota está vazio.");
            return;
        }

        System.out.println("\n" + BOLD + "=== MANIFESTO DE TRANSPORTE ===" + RESET);

        List<RouteStop> stops = plan.getStops();
        for (int i = 0; i < stops.size(); i++) {
            RouteStop stop = stops.get(i);
            boolean isLast = (i == stops.size() - 1);

            printStop(stop, isLast);
        }

        System.out.println(BOLD + "=== FIM DA ROTA ===\n" + RESET);
    }

    private void printStop(RouteStop stop, boolean isLast) {
        // Linha vertical que conecta as estações (timeline)
        String vLine = isLast ? " " : "│";

        // 1. Nome da Estação
        System.out.printf(GREY + "%s\n" + RESET, isLast ? "" : "│"); // Pequeno espaço antes
        System.out.printf(BLUE + "●  %s" + RESET + " (Station ID: %d)\n",
                stop.getStation().getName().toUpperCase(),
                stop.getStation().getId());

        boolean hasAction = false;

        // 2. Cargas a CARREGAR (Indented with simple '+')
        for (Freight f : stop.getFreightsToLoad()) {
            hasAction = true;
            System.out.printf(GREY + "%s" + RESET + "   " + GREEN + "+ CARREGAR:" + RESET + " Carga #%d (%d vagões) -> %s\n",
                    vLine, f.getId(), f.getWagonCount(), f.getDestinationName());
        }

        // 3. Cargas a DESCARREGAR (Indented with simple '-')
        for (Freight f : stop.getFreightsToUnload()) {
            hasAction = true;
            System.out.printf(GREY + "%s" + RESET + "   " + RED + "- DESCARREGAR:" + RESET + " Carga #%d (Vinda de %s)\n",
                    vLine, f.getId(), f.getOriginName());
        }

        // Se não houver ações, mostrar apenas a linha de passagem
        if (!hasAction && !isLast) {
            // Opcional: Podes comentar a linha abaixo se preferires que não diga nada
            // System.out.printf(GREY + "%s" + RESET + "   (Passagem)\n", vLine);
        }
    }
}