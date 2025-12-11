package org.example.usei12.domain;

public class Edge implements Comparable<Edge> {

    private final Station from;
    private final Station to;
    private final double length; // em km

    public Edge(Station from, Station to, double length) {
        this.from = from;
        this.to = to;
        this.length = length;
    }

    public Station getFrom() {
        return from;
    }

    public Station getTo() {
        return to;
    }

    public double getLength() {
        return length;
    }

    /**
     * Permite ordenar arestas por comprimento.
     * Retorna negativo, zero ou positivo conforme esta aresta
     * seja menor, igual ou maior do que a outra.
     */
    @Override
    public int compareTo(Edge other) {
        return Double.compare(this.length, other.length);
    }
}