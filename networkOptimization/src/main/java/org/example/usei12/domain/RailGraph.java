package org.example.usei12.domain;

import java.util.*;

/**
 * Representação de um grafo ferroviário.
 * Utilizado na USEI12 e reutilizado na USEI13.
 */
public class RailGraph {

    /**
     * Estações do grafo, indexadas pelo seu identificador.
     */
    private final Map<String, Station> stations = new HashMap<>();

    /**
     * Lista global de arestas (não dirigidas).
     * Necessária para USEI12 (MST, exportação DOT).
     */
    private final List<Edge> edges = new ArrayList<>();

    /**
     * Lista de adjacência (USEI13).
     * Cada estação mantém todas as arestas incidentes.
     */
    private final Map<String, List<Edge>> adj = new HashMap<>();

    public Collection<Station> getStations() {
        return stations.values();
    }

    public List<Edge> getEdges() {
        return edges;
    }

    public Station getStationById(String id) {
        return stations.get(id);
    }

    /**
     * Cria explicitamente uma estação (vértice).
     * Usado ao carregar stations.csv.
     */
    public void addStation(Station station) {
        stations.putIfAbsent(station.getId(), station);
        adj.putIfAbsent(station.getId(), new ArrayList<>());
    }

    /**
     * Cria uma aresta NÃO DIRIGIDA entre duas estações.
     * Se já existir ligação entre o mesmo par, mantém a de menor distância.
     */
    public void addEdge(Station a, Station b, double length) {

        // verificar se já existe ligação entre a e b
        Edge existing = findEdgeBetween(a, b);

        if (existing != null) {
            if (length < existing.getLength()) {
                removeEdge(existing);
            } else {
                return; // mantém a existente
            }
        }

        Edge e = new Edge(a, b, length);
        edges.add(e);

        // registo nos dois sentidos (grafo não dirigido)
        adj.get(a.getId()).add(e);
        adj.get(b.getId()).add(e);
    }

    /**
     * Procura uma aresta existente entre duas estações (ordem irrelevante).
     */
    private Edge findEdgeBetween(Station a, Station b) {
        for (Edge e : edges) {
            boolean same = (e.getFrom().equals(a) && e.getTo().equals(b)) || (e.getFrom().equals(b) && e.getTo().equals(a));
            if (same) {
                return e;
            }
        }
        return null;
    }

    /**
     * Remove uma aresta do grafo (lista global e adjacências).
     */
    private void removeEdge(Edge e) {
        edges.remove(e);
        adj.get(e.getFrom().getId()).remove(e);
        adj.get(e.getTo().getId()).remove(e);
    }

    /**
     * Devolve as arestas adjacentes a uma estação.
     */
    public List<Edge> getAdjEdges(String stationId) {
        return adj.getOrDefault(stationId, Collections.emptyList());
    }
}