package org.example.graph.map;

import org.example.graph.Edge;
import org.example.graph.Graph;

import java.util.*;
import java.util.function.Predicate;

/**
 * Implementação de Grafo usando Adjacency Map
 * Baseada em ESINF06-Graph.pdf (slide 24)
 *
 * Complexidade espacial: O(V + E)
 * - getEdge(u,v): O(1) esperado
 * - adjVertices(v): O(degree(v))
 *
 * @param <V> Tipo dos vértices
 * @param <E> Tipo das arestas
 */
public class MapGraph<V, E> implements Graph<V, E> {

    private final boolean isDirected;

    // Map externo: vértice -> Map interno
    // Map interno: vértice adjacente -> aresta
    private final Map<V, Map<V, Edge<V, E>>> mapVertices;

    /**
     * Construtor
     * @param directed true para grafo dirigido, false para não-dirigido
     */
    public MapGraph(boolean directed) {
        this.isDirected = directed;
        this.mapVertices = new LinkedHashMap<>();
    }

    @Override
    public boolean isDirected() {
        return isDirected;
    }

    @Override
    public int numVertices() {
        return mapVertices.size();
    }

    @Override
    public ArrayList<V> vertices() {
        return new ArrayList<>(mapVertices.keySet());
    }

    @Override
    public boolean validVertex(V vert) {
        return vert != null && mapVertices.containsKey(vert);
    }

    @Override
    public int key(V vert) {
        if (!validVertex(vert)) {
            return -1;
        }

        int index = 0;
        for (V v : mapVertices.keySet()) {
            if (v.equals(vert)) {
                return index;
            }
            index++;
        }
        return -1;
    }

    @Override
    public V vertex(int key) {
        if (key < 0 || key >= numVertices()) {
            return null;
        }

        int index = 0;
        for (V v : mapVertices.keySet()) {
            if (index == key) {
                return v;
            }
            index++;
        }
        return null;
    }

    @Override
    public V vertex(Predicate<V> p) {
        for (V v : mapVertices.keySet()) {
            if (p.test(v)) {
                return v;
            }
        }
        return null;
    }

    @Override
    public Collection<V> adjVertices(V vert) {
        if (!validVertex(vert)) {
            return Collections.emptyList();
        }
        return new ArrayList<>(mapVertices.get(vert).keySet());
    }

    @Override
    public int numEdges() {
        int count = 0;
        for (Map<V, Edge<V, E>> edges : mapVertices.values()) {
            count += edges.size();
        }

        // Em grafo não-dirigido, cada aresta é contada duas vezes
        return isDirected ? count : count / 2;
    }

    @Override
    public Collection<Edge<V, E>> edges() {
        List<Edge<V, E>> allEdges = new ArrayList<>();

        if (isDirected) {
            // Grafo dirigido: adicionar todas as arestas
            for (Map<V, Edge<V, E>> edges : mapVertices.values()) {
                allEdges.addAll(edges.values());
            }
        } else {
            // Grafo não-dirigido: evitar duplicados
            Set<Edge<V, E>> seen = new HashSet<>();
            for (Map<V, Edge<V, E>> edges : mapVertices.values()) {
                for (Edge<V, E> edge : edges.values()) {
                    // Criar aresta normalizada para comparação
                    V v1 = edge.getVOrig();
                    V v2 = edge.getVDest();
                    Edge<V, E> normalized = new Edge<>(
                            v1.hashCode() < v2.hashCode() ? v1 : v2,
                            v1.hashCode() < v2.hashCode() ? v2 : v1,
                            edge.getWeight()
                    );

                    if (!seen.contains(normalized)) {
                        allEdges.add(edge);
                        seen.add(normalized);
                    }
                }
            }
        }

        return allEdges;
    }

    @Override
    public Edge<V, E> edge(V vOrig, V vDest) {
        if (!validVertex(vOrig) || !validVertex(vDest)) {
            return null;
        }
        return mapVertices.get(vOrig).get(vDest);
    }

    @Override
    public Edge<V, E> edge(int vOrigKey, int vDestKey) {
        V vOrig = vertex(vOrigKey);
        V vDest = vertex(vDestKey);
        return edge(vOrig, vDest);
    }

    @Override
    public int outDegree(V vert) {
        if (!validVertex(vert)) {
            return -1;
        }
        return mapVertices.get(vert).size();
    }

    @Override
    public int inDegree(V vert) {
        if (!validVertex(vert)) {
            return -1;
        }

        int count = 0;
        for (V v : mapVertices.keySet()) {
            if (mapVertices.get(v).containsKey(vert)) {
                count++;
            }
        }
        return count;
    }

    @Override
    public Collection<Edge<V, E>> outgoingEdges(V vert) {
        if (!validVertex(vert)) {
            return Collections.emptyList();
        }
        return new ArrayList<>(mapVertices.get(vert).values());
    }

    @Override
    public Collection<Edge<V, E>> incomingEdges(V vert) {
        if (!validVertex(vert)) {
            return Collections.emptyList();
        }

        List<Edge<V, E>> incoming = new ArrayList<>();
        for (V v : mapVertices.keySet()) {
            Edge<V, E> edge = mapVertices.get(v).get(vert);
            if (edge != null) {
                incoming.add(edge);
            }
        }
        return incoming;
    }

    @Override
    public boolean addVertex(V vert) {
        if (vert == null || validVertex(vert)) {
            return false;
        }
        mapVertices.put(vert, new LinkedHashMap<>());
        return true;
    }

    @Override
    public boolean addEdge(V vOrig, V vDest, E weight) {
        if (vOrig == null || vDest == null) {
            return false;
        }

        // Adicionar vértices se não existirem
        if (!validVertex(vOrig)) {
            addVertex(vOrig);
        }
        if (!validVertex(vDest)) {
            addVertex(vDest);
        }

        // Criar e adicionar aresta
        Edge<V, E> edge = new Edge<>(vOrig, vDest, weight);
        mapVertices.get(vOrig).put(vDest, edge);

        // Se não-dirigido, adicionar aresta reversa
        if (!isDirected) {
            Edge<V, E> reverseEdge = new Edge<>(vDest, vOrig, weight);
            mapVertices.get(vDest).put(vOrig, reverseEdge);
        }

        return true;
    }

    @Override
    public boolean removeVertex(V vert) {
        if (!validVertex(vert)) {
            return false;
        }

        // Remover todas as arestas que chegam a este vértice
        for (V v : mapVertices.keySet()) {
            mapVertices.get(v).remove(vert);
        }

        // Remover o vértice
        mapVertices.remove(vert);
        return true;
    }

    @Override
    public boolean removeEdge(V vOrig, V vDest) {
        if (!validVertex(vOrig) || !validVertex(vDest)) {
            return false;
        }

        mapVertices.get(vOrig).remove(vDest);

        if (!isDirected) {
            mapVertices.get(vDest).remove(vOrig);
        }

        return true;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Graph<V, E> clone() {
        MapGraph<V, E> cloned = new MapGraph<>(isDirected);

        // Copiar vértices
        for (V vertex : vertices()) {
            cloned.addVertex(vertex);
        }

        // Copiar arestas
        for (Edge<V, E> edge : edges()) {
            cloned.addEdge(edge.getVOrig(), edge.getVDest(), edge.getWeight());
        }

        return cloned;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof MapGraph)) return false;

        MapGraph<?, ?> other = (MapGraph<?, ?>) obj;

        if (isDirected != other.isDirected) return false;
        if (numVertices() != other.numVertices()) return false;
        if (numEdges() != other.numEdges()) return false;

        return mapVertices.equals(other.mapVertices);
    }

    @Override
    public int hashCode() {
        return Objects.hash(isDirected, mapVertices);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Graph (%s)%n", isDirected ? "directed" : "undirected"));
        sb.append(String.format("  Vertices: %d%n", numVertices()));
        sb.append(String.format("  Edges: %d%n", numEdges()));
        sb.append("\nAdjacency Map:\n");

        for (V vertex : mapVertices.keySet()) {
            sb.append(String.format("  %s -> %s%n", vertex, adjVertices(vertex)));
        }

        return sb.toString();
    }
}

