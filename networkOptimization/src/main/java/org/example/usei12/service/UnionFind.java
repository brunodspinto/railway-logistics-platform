package org.example.usei12.service;

import java.util.HashMap;
import java.util.Map;

public class UnionFind<T> {

    private final Map<T, T> parent = new HashMap<>();
    private final Map<T, Integer> rank = new HashMap<>();

    public void makeSet(T item) {
        parent.put(item, item);
        rank.put(item, 0);
    }

    /**
     * Encontra o representante do conjunto (com path compression).
     */
    public T find(T item) {
        T p = parent.get(item);
        if (p == null) return null;
        if (!p.equals(item)) {
            parent.put(item, find(p));
        }
        return parent.get(item);
    }

    /**
     * Une dois conjuntos usando union by rank.
     * Só devolve true se os conjuntos forem distintos.
     */
    public boolean union(T a, T b) {
        T rootA = find(a);
        T rootB = find(b);
        if (rootA == null || rootB == null) return false;
        if (rootA.equals(rootB)) return false;

        int rankA = rank.get(rootA);
        int rankB = rank.get(rootB);

        if (rankA < rankB) {
            parent.put(rootA, rootB);
        } else if (rankA > rankB) {
            parent.put(rootB, rootA);
        } else {
            parent.put(rootB, rootA);
            rank.put(rootA, rankA + 1);
        }
        return true;
    }
}