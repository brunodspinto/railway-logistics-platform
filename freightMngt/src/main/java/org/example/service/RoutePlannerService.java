package org.example.service;

import org.example.domain.Freight;
import org.example.domain.Station;

import java.util.List;

/**
 * Serviço responsável por planear rotas e operações de carga/descarga (USLP08).
 */
public class RoutePlannerService {

    /**
     * Cria um plano de rota detalhado com base num caminho e numa lista de cargas.
     *
     * @param path     A sequência de estações (rota definida manual ou automaticamente).
     * @param freights A lista de cargas (Freights) que se pretende transportar.
     * @return Um RoutePlan contendo as operações a realizar em cada estação.
     */
    public RoutePlan planRoute(List<Station> path, List<Freight> freights) {
        if (path == null || path.isEmpty()) {
            throw new IllegalArgumentException("The path cannot be empty.");
        }
        if (freights == null || freights.isEmpty()) {
            throw new IllegalArgumentException("The freight list cannot be empty.");
        }

        RoutePlan plan = new RoutePlan();

        // Iterar por cada estação do caminho para determinar as operações
        for (Station currentStation : path) {
            RouteStop stop = new RouteStop(currentStation);

            for (Freight freight : freights) {
                // Verificar se esta estação é a origem da carga (Carregar)
                if (freight.getOriginId() == currentStation.getId()) {
                    stop.addFreightToLoad(freight);
                }

                // Verificar se esta estação é o destino da carga (Descarregar)
                if (freight.getDestinationId() == currentStation.getId()) {
                    stop.addFreightToUnload(freight);
                }
            }

            // Adiciona a paragem ao plano (mesmo que não tenha operações, faz parte da rota)
            // Opcionalmente, podes filtrar paragens sem operações se o objetivo for apenas o manifesto de carga
            plan.addStop(stop);
        }

        validatePlan(plan, freights);

        return plan;
    }

    /**
     * Validação simples para garantir que todas as cargas são entregues.
     * Lança aviso no console se algo parecer errado (ex: carga carregada mas nunca descarregada na rota atual).
     */
    private void validatePlan(RoutePlan plan, List<Freight> freights) {
        for (Freight f : freights) {
            boolean loaded = false;
            boolean unloaded = false;

            for (RouteStop stop : plan.getStops()) {
                if (stop.getFreightsToLoad().contains(f)) loaded = true;
                if (stop.getFreightsToUnload().contains(f)) unloaded = true;
            }

            if (!loaded) {
                System.out.printf("[WARNING] Freight #%d (Origin: %s) is not being LOADED in this route path.\n",
                        f.getId(), f.getOriginName());
            }
            if (!unloaded) {
                System.out.printf("[WARNING] Freight #%d (Dest: %s) is not being UNLOADED in this route path.\n",
                        f.getId(), f.getDestinationName());
            }
        }
    }
}