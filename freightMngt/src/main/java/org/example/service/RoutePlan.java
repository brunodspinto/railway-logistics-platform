package org.example.service;

import org.example.domain.Station; // Certifica-te que tens este import

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Representa o plano completo da rota.
 * Compatível com USLP08 (Logística) e USLP10 (Tráfego/Automático).
 */
public class RoutePlan {

    // Mantemos RouteStop para não quebrar a lógica de Carga/Descarga da USLP08
    private final List<RouteStop> stops;

    // NOVOS CAMPOS para a USLP10 (Métricas de Planeamento)
    private double totalDistance = 0.0;
    private double totalCost = 0.0;

    // Construtor Vazio (Mantém compatibilidade com código antigo que cria planos manuais)
    public RoutePlan() {
        this.stops = new ArrayList<>();
    }

    // NOVO CONSTRUTOR COMPLETO (Para ser usado pelo RoutePlannerService na USLP10)
    public RoutePlan(List<RouteStop> stops, double totalDistance, double totalCost) {
        this.stops = new ArrayList<>(stops);
        this.totalDistance = totalDistance;
        this.totalCost = totalCost;
    }

    // NOVO CONSTRUTOR DE CONVENIÊNCIA (Converte List<Station> em RoutePlan)
    // O RoutePlannerService (Dijkstra) geralmente devolve List<Station>.
    // Este construtor converte isso automaticamente em RouteStop.
    public RoutePlan(List<Station> stations, double totalDistance) {
        this.stops = new ArrayList<>();
        this.totalDistance = totalDistance;
        this.totalCost = 0.0; // Custo zero ou calculado à parte

        if (stations != null) {
            for (Station s : stations) {
                // Cria um RouteStop simples (apenas passagem, sem operações logísticas)
                this.stops.add(new RouteStop(s));
            }
        }
    }

    public void addStop(RouteStop stop) {
        this.stops.add(stop);
    }

    public List<RouteStop> getStops() {
        return Collections.unmodifiableList(stops);
    }

    // NOVO MÉTODO AUXILIAR (Vital para o SchedulerService)
    // O Scheduler precisa da lista de Estações para calcular a física/tempos.
    // Este método extrai as estações de dentro dos RouteStops.
    public List<Station> getStations() {
        return stops.stream()
                .map(RouteStop::getStation)
                .collect(Collectors.toList());
    }

    // NOVO MÉTODO (Se precisares de IDs para persistência na BD/Segmentos)
    public List<Integer> getSegmentIds() {
        // Implementação simplificada para o MVP.
        // Num cenário real, mapearia as estações para os segmentos que as ligam.
        return new ArrayList<>();
    }

    // Getters para as novas métricas
    public double getTotalDistance() {
        return totalDistance;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public void setTotalDistance(double totalDistance) {
        this.totalDistance = totalDistance;
    }

    public void printManifest() {
        System.out.println("=== ROUTE LOGISTICS MANIFEST ===");
        System.out.printf("Total Distance: %.2f km | Total Cost: %.2f\n", totalDistance, totalCost);
        System.out.println("--------------------------------");
        for (RouteStop stop : stops) {
            System.out.println(stop.toString());
            // System.out.println("--------------------------------"); // Opcional: reduzir ruído visual
        }
        System.out.println("================================");
    }
}