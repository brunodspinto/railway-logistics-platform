package org.example.graph;

import java.util.Objects;

/**
 * Representa uma aresta do grafo
 *
 * @param <V> Tipo dos vértices
 * @param <E> Tipo do peso/informação da aresta
 */
public class Edge<V, E> {

    private final V vOrig;      // Vértice origem
    private final V vDest;      // Vértice destino
    private final E weight;     // Peso/informação da aresta

    /**
     * Construtor
     */
    public Edge(V vOrig, V vDest, E weight) {
        if (vOrig == null || vDest == null) {
            throw new IllegalArgumentException("Vertices cannot be null");
        }
        this.vOrig = vOrig;
        this.vDest = vDest;
        this.weight = weight;
    }

    public V getVOrig() {
        return vOrig;
    }

    public V getVDest() {
        return vDest;
    }

    public E getWeight() {
        return weight;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Edge)) return false;

        Edge<?, ?> edge = (Edge<?, ?>) o;
        return vOrig.equals(edge.vOrig) &&
                vDest.equals(edge.vDest);
    }

    @Override
    public int hashCode() {
        return Objects.hash(vOrig, vDest);
    }

    @Override
    public String toString() {
        return String.format("%s -> %s (weight: %s)", vOrig, vDest, weight);
    }
}

