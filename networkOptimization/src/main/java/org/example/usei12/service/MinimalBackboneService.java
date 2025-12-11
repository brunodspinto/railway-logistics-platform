package org.example.usei12.service;

import org.example.usei12.domain.Edge;
import org.example.usei12.domain.RailGraph;
import org.example.usei12.domain.Station;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MinimalBackboneService {

    /**
     * Calcula o backbone mínimo usando Kruskal:
     * ordena as arestas e liga componentes com Union-Find.
     */
    public static List<Edge> computeMinimalBackbone(RailGraph graph) {
        List<Edge> mstEdges = new ArrayList<>();
        UnionFind<Station> uf = new UnionFind<>();

        for (Station s : graph.getStations()) {
            uf.makeSet(s);
        }

        List<Edge> edges = new ArrayList<>(graph.getEdges());
        Collections.sort(edges);

        // adiciona a aresta apenas se unir duas componentes distintas
        for (Edge e : edges) {
            Station u = e.getFrom();
            Station v = e.getTo();
            if (uf.union(u, v)) {
                mstEdges.add(e);
            }
        }

        return mstEdges;
    }
}