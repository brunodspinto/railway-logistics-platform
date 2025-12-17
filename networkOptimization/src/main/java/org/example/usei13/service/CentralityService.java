package org.example.usei13.service;

import org.example.usei12.domain.Edge;
import org.example.usei12.domain.RailGraph;
import org.example.usei12.domain.Station;

import java.util.*;

/**
 * Serviço da USEI13 responsável pelo cálculo das métricas de centralidade
 * e do HubScore da rede ferroviária.
 */
public class CentralityService {

    private final RailGraph graph;

    public CentralityService(RailGraph graph) {
        this.graph = graph;
    }

    /**
     * Calcula as métricas de centralidade e imprime os resultados ordenados
     * por HubScore (ranking).
     */
    public void computeAndPrint() {

        Map<String, Integer> degree = new HashMap<>();
        Map<String, Double> strength = new HashMap<>();
        Map<String, Double> betweenness = new HashMap<>();
        Map<String, Double> harmonic = new HashMap<>();

        // inicialização
        for (Station s : graph.getStations()) {
            degree.put(s.getId(), 0);
            strength.put(s.getId(), 0.0);
            betweenness.put(s.getId(), 0.0);
            harmonic.put(s.getId(), 0.0);
        }

        // degree e strength (tratado como não-direcionado)
        for (Edge e : graph.getEdges()) {
            String u = e.getFrom().getId();
            String v = e.getTo().getId();

            degree.put(u, degree.get(u) + 1);
            degree.put(v, degree.get(v) + 1);

            strength.put(u, strength.get(u) + e.getLength());
            strength.put(v, strength.get(v) + e.getLength());
        }

        // all-pairs shortest paths
        Map<String, Map<String, Double>> allDist = new HashMap<>();
        for (Station s : graph.getStations()) {
            allDist.put(s.getId(), ShortestPathService.dijkstra(graph, s.getId()));
        }

        // harmonic closeness
        for (Station s : graph.getStations()) {
            double sum = 0.0;
            for (double d : allDist.get(s.getId()).values()) {
                if (d > 0 && d < Double.POSITIVE_INFINITY) {
                    sum += 1.0 / d;
                }
            }
            harmonic.put(s.getId(), sum);
        }

        // betweenness (abordagem simplificada)
        for (Station s : graph.getStations()) {
            for (Station t : graph.getStations()) {

                if (s.getId().equals(t.getId())) continue;

                double dst = allDist.get(s.getId()).get(t.getId());

                for (Station v : graph.getStations()) {

                    if (v.getId().equals(s.getId())
                            || v.getId().equals(t.getId())) continue;

                    double dsv = allDist.get(s.getId()).get(v.getId());
                    double dvt = allDist.get(v.getId()).get(t.getId());

                    if (Math.abs(dsv + dvt - dst) < 1e-6) {
                        betweenness.put(v.getId(), betweenness.get(v.getId()) + 1);
                    }
                }
            }
        }

        printResults(degree, strength, betweenness, harmonic);
    }

    /**
     * Normaliza as métricas, calcula o HubScore e imprime os resultados
     * ordenados por HubScore (descendente).
     */
    private void printResults(
            Map<String, Integer> degree,
            Map<String, Double> strength,
            Map<String, Double> betweenness,
            Map<String, Double> harmonic) {

        double maxStr = Collections.max(strength.values());
        double maxBet = Collections.max(betweenness.values());
        double maxHar = Collections.max(harmonic.values());

        class Result {
            Station s;
            double hub;

            Result(Station s, double hub) {
                this.s = s;
                this.hub = hub;
            }
        }

        List<Result> results = new ArrayList<>();

        for (Station s : graph.getStations()) {

            double strN = maxStr == 0 ? 0 : strength.get(s.getId()) / maxStr;
            double betN = maxBet == 0 ? 0 : betweenness.get(s.getId()) / maxBet;
            double harN = maxHar == 0 ? 0 : harmonic.get(s.getId()) / maxHar;

            double hub = 0.35 * betN + 0.35 * harN + 0.30 * strN;

            results.add(new Result(s, hub));
        }

        // ordenar por HubScore
        results.sort((a, b) -> Double.compare(b.hub, a.hub));

        System.out.printf(
                "%-12s %-30s %6s %10s %10s %10s %10s%n",
                "STID", "STNAME", "DEGREE", "STRENGTH", "BETWEENNESS", "HARMONIC", "HUBSCORE");

        for (Result r : results) {

            Station s = r.s;

            System.out.printf(
                    "%-12s %-30s %6d %10.2f %10.0f %10.2f %10.4f%n",
                    s.getId(),
                    s.getName(),
                    degree.get(s.getId()),
                    strength.get(s.getId()),
                    betweenness.get(s.getId()),
                    harmonic.get(s.getId()),
                    r.hub
            );
        }
    }
}