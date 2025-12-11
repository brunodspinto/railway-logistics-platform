package org.example.usei12.domain;

import java.util.*;

public class RailGraph {

    private final Map<String, Station> stations = new HashMap<>();
    private final List<Edge> edges = new ArrayList<>();

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
     * Se não existir, cria uma nova e adiciona-a ao grafo.
     */
    public Station getOrCreateStation(String id, String name, double lat, double lon) {
        Station s = stations.get(id);
        if (s == null) {
            s = new Station(id, name, lat, lon);
            stations.put(id, s);
        }
        return s;
    }

    /**
     * Cria uma nova aresta entre duas estações e regista-a no grafo.
     */
    public Edge addEdge(Station from, Station to, double length) {
        Edge e = new Edge(from, to, length);
        edges.add(e);
        return e;
    }
}