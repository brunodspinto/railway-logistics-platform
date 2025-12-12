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
     * Lista global de arestas.
     * Necessária para USEI12 (MST, exportação DOT).
     */
    private final List<Edge> edges = new ArrayList<>();

    /**
     * Lista de adjacência (USEI13).
     * Associa cada estação às suas arestas de saída,
     * permitindo acesso eficiente aos vizinhos.
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
     * Obtém a estação com o ID dado.
     * Se não existir, cria-a e inicializa a sua lista de adjacência.
     */
    public Station getOrCreateStation(String id, String name, double lat, double lon) {

        Station s = stations.get(id);

        if (s == null) {
            s = new Station(id, name, lat, lon);
            stations.put(id, s);

            // inicialização da lista de adjacência (USEI13)
            adj.put(id, new ArrayList<>());
        }

        return s;
    }

    /**
     * Cria uma nova aresta entre duas estações.
     * A aresta é registada na lista global e na lista de adjacência.
     */
    public Edge addEdge(Station from, Station to, double length) {

        Edge e = new Edge(from, to, length);
        edges.add(e);

        // registo da aresta como vizinha da estação de origem
        adj.get(from.getId()).add(e);

        return e;
    }

    /**
     * Devolve as arestas adjacentes (de saída) de uma estação.
     * Se não existirem, devolve uma lista vazia.
     */
    public List<Edge> getAdjEdges(String stationId) {
        return adj.getOrDefault(stationId, Collections.emptyList());
    }
}